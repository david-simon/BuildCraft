package buildcraft.lib.client.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.lwjgl.util.vector.Vector3f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

/** Provides a simple way of rendering an item model with just a list of quads. This provides some transforms to use
 * that make it simple to render as a block, item (todo) or tool (todo) */
@SuppressWarnings("deprecation")
public class ModelItemSimple implements IBakedModel {
    public static final ItemCameraTransforms TRANSFORM_DEFAULT = ItemCameraTransforms.DEFAULT;
    public static final ItemCameraTransforms TRANSFORM_BLOCK;
    public static final ItemCameraTransforms TRANSFORM_PLUG_AS_ITEM;
    public static final ItemCameraTransforms TRANSFORM_PLUG_AS_ITEM_BIGGER;
    public static final ItemCameraTransforms TRANSFORM_PLUG_AS_BLOCK;
    // TODO: TRANSFORM_ITEM
    // TODO: TRANSFORM_TOOL

    static {
        // Values taken from "minecraft:models/block/block.json"
        ItemTransformVec3f thirdperson_left = def(75, 45, 0, 0, 2.5, 0, 0.375);
        ItemTransformVec3f thirdperson_right = def(75, 225, 0, 0, 2.5, 0, 0.375);
        ItemTransformVec3f firstperson_left = def(0, 45, 0, 0, 0, 0, 0.4);
        ItemTransformVec3f firstperson_right = def(0, 225, 0, 0, 0, 0, 0.4);
        ItemTransformVec3f head = def(0, 0, 0, 0, 0, 0, 1);
        ItemTransformVec3f gui = def(30, 225, 0, 0, 0, 0, 0.625);
        ItemTransformVec3f ground = def(0, 0, 0, 0, 3, 0, 0.25);
        ItemTransformVec3f fixed = def(0, 0, 0, 0, 0, 0, 0.5);
        TRANSFORM_BLOCK = new ItemCameraTransforms(thirdperson_left, thirdperson_right, firstperson_left, firstperson_right, head, gui, ground, fixed);

        ItemTransformVec3f item_head = def(0, 0, 0, 0, 0, 0, 1);
        ItemTransformVec3f item_gui = def(0, 90, 0, 0, 0, 0, 1);
        ItemTransformVec3f item_ground = def(0, 0, 0, 0, 3, 0, 0.5);
        ItemTransformVec3f item_fixed = def(0, 0, 0, 0, 0, 0, 0.85);
        TRANSFORM_PLUG_AS_ITEM = new ItemCameraTransforms(thirdperson_left, thirdperson_right, firstperson_left, firstperson_right, item_head, item_gui, item_ground, item_fixed);
        TRANSFORM_PLUG_AS_ITEM_BIGGER = scale(TRANSFORM_PLUG_AS_ITEM, 1.8);

        thirdperson_left = def(75, 45, 0, 0, 2.5, 0, 0.375);
        thirdperson_right = def(75, 225, 0, 0, 2.5, 0, 0.375);
        firstperson_left = def(0, 45, 0, 0, 0, 0, 0.4);
        firstperson_right = def(0, 225, 0, 0, 0, 0, 0.4);
        gui = def(30, 135, 0, -3, 1.5, 0, 0.625);
        TRANSFORM_PLUG_AS_BLOCK = new ItemCameraTransforms(thirdperson_left, thirdperson_right, firstperson_left, firstperson_right, head, gui, ground, fixed);
    }

    private static ItemCameraTransforms scale(ItemCameraTransforms from, double by) {
        ItemTransformVec3f thirdperson_left = scale(from.thirdperson_left, by);
        ItemTransformVec3f thirdperson_right = scale(from.thirdperson_right, by);
        ItemTransformVec3f firstperson_left = scale(from.firstperson_left, by);
        ItemTransformVec3f firstperson_right = scale(from.firstperson_right, by);
        ItemTransformVec3f head = scale(from.head, by);
        ItemTransformVec3f gui = scale(from.gui, by);
        ItemTransformVec3f ground = scale(from.ground, by);
        ItemTransformVec3f fixed = scale(from.fixed, by);
        return new ItemCameraTransforms(thirdperson_left, thirdperson_right, firstperson_left, firstperson_right, head, gui, ground, fixed);
    }

    private static ItemTransformVec3f scale(ItemTransformVec3f from, double by) {

        float scale = (float) by;
        Vector3f nScale = new Vector3f(from.scale);
        nScale.scale(scale);

        return new ItemTransformVec3f(from.rotation, from.translation, nScale);
    }

    private static ItemTransformVec3f translate(ItemTransformVec3f from, double dx, double dy, double dz) {
        Vector3f nTranslation = new Vector3f(from.translation);
        nTranslation.translate((float) dx, (float) dy, (float) dz);
        return new ItemTransformVec3f(from.rotation, nTranslation, from.scale);
    }

    private static ItemTransformVec3f def(double rx, double ry, double rz, double tx, double ty, double tz, double scale) {
        return def((float) rx, (float) ry, (float) rz, (float) tx, (float) ty, (float) tz, (float) scale);
    }

    private static ItemTransformVec3f def(float rx, float ry, float rz, float tx, float ty, float tz, float scale) {
        return new ItemTransformVec3f(new Vector3f(rx, ry, rz), new Vector3f(tx / 16f, ty / 16f, tz / 16f), new Vector3f(scale, scale, scale));
    }

    private final List<BakedQuad> quads;
    private final TextureAtlasSprite particle;
    private final ItemCameraTransforms transforms;

    public ModelItemSimple(List<BakedQuad> quads, ItemCameraTransforms transforms) {
        this.quads = quads;
        if (quads.isEmpty()) {
            particle = null;
        } else {
            particle = quads.get(0).getSprite();
        }
        this.transforms = transforms;
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        return side == null ? quads : ImmutableList.of();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return particle;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return transforms;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
