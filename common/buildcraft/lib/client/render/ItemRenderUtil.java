package buildcraft.lib.client.render;

import java.util.concurrent.TimeUnit;

import javax.vecmath.Vector3f;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import buildcraft.api.core.EnumPipePart;

import buildcraft.lib.BCLibConfig;
import buildcraft.lib.client.model.MutableQuad;
import buildcraft.lib.misc.ItemStackKey;

public class ItemRenderUtil {

    private static final LoadingCache<ItemStackKey, Integer> glListCache;

    private static final EntityItem dummyEntityItem = new EntityItem(null);
    private static final RenderEntityItem customItemRenderer = new RenderEntityItem(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem()) {
        @Override
        public boolean shouldSpreadItems() {
            return false;
        }

        @Override
        public boolean shouldBob() {
            return false;
        }
    };
    static {
        glListCache = CacheBuilder.newBuilder()//
            .expireAfterAccess(40, TimeUnit.SECONDS)//
            .removalListener(ItemRenderUtil::onStackRemove)//
            .build(CacheLoader.from(ItemRenderUtil::makeItemGlList));
    }

    private static Integer makeItemGlList(ItemStackKey item) {
        int list = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(list, GL11.GL_COMPILE);
        renderItemImpl(0, 0, 0, item.baseStack);
        GL11.glEndList();
        return list;
    }

    private static void onStackRemove(RemovalNotification<ItemStackKey, Integer> notification) {
        Integer val = notification.getValue();
        if (val != null) {
            GLAllocation.deleteDisplayLists(val);
        }
    }

    private static void renderItemImpl(double x, double y, double z, ItemStack stack) {
        GL11.glPushMatrix();
        GL11.glTranslated(0, -0.2, 0);
        GL11.glScaled(0.9, 0.9, 0.9);

        // This is broken - some stacks render too big but some render way too small.
        // Also not all stacks are centered :/

        if (stack.getItem() instanceof ItemBlock) {
            dummyEntityItem.hoverStart = 0;
        } else {
            // Items are rotated by 45 degrees
            dummyEntityItem.hoverStart = (float) (45 * Math.PI / 180);
        }

        dummyEntityItem.setEntityItemStack(stack);
        customItemRenderer.doRender(dummyEntityItem, x, y, z, 0, 0);

        GL11.glPopMatrix();
    }

    // Batch item rendering

    private static boolean inBatch = false;

    /** Used to render a lot of items in sequential order. Assumes that you don't change the glstate inbetween calls.
     * You must call {@link #endItemBatch()} after your have rendered all of the items. */
    public static void renderItemStack(double x, double y, double z, ItemStack stack, int lightc, EnumFacing dir, VertexBuffer vb) {
        if (stack.isEmpty()) {
            return;
        }
        if (dir == null) {
            dir = EnumFacing.EAST;
        }
        dir = BCLibConfig.rotateTravelingItems.changeFacing(dir);

        IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
        model = model.getOverrides().handleItemState(model, stack, null, null);
        boolean requireGl = stack.hasEffect() || model.isBuiltInRenderer();

        if (vb != null && !requireGl) {
            vb.setTranslation(x, y, z);
            float scale = 0.30f;

            MutableQuad q = new MutableQuad(-1, null);
            for (EnumPipePart part : EnumPipePart.VALUES) {
                for (BakedQuad quad : model.getQuads(null, part.face, 0)) {
                    q.fromBakedItem(quad);
                    q.translated(-0.5, -0.5, -0.5);
                    q.scaled(scale);
                    q.rotate(EnumFacing.SOUTH, dir, 0, 0, 0);
                    if (quad.hasTintIndex()) {
                        int colour = Minecraft.getMinecraft().getItemColors().getColorFromItemstack(stack, quad.getTintIndex());
                        if (EntityRenderer.anaglyphEnable) {
                            colour = TextureUtil.anaglyphColor(colour);
                        }
                        q.multColouri(colour, colour >> 8, colour >> 16, 0xFF);
                    }
                    q.lighti(lightc);
                    Vector3f normal = q.getCalculatedNormal();
                    q.normalvf(normal);
                    q.multShade();
                    q.render(vb);
                }
            }

            vb.setTranslation(0, 0, 0);
            return;
        }

        if (!inBatch) {
            inBatch = true;
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);
            GL11.glScaled(0.3, 0.3, 0.3);
            RenderHelper.disableStandardItemLighting();
        }
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightc % (float) 0x1_00_00, lightc / (float) 0x1_00_00);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);
    }

    public static void endItemBatch() {
        if (inBatch) {
            inBatch = false;
            GL11.glPopMatrix();
        }
    }
}
