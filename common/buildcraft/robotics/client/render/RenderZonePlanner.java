package buildcraft.robotics.client.render;

import buildcraft.api.properties.BuildCraftProperties;
import buildcraft.lib.client.model.MutableVertex;
import buildcraft.lib.client.sprite.DynamicTextureBC;
import buildcraft.lib.misc.data.WorldPos;
import buildcraft.robotics.BCRoboticsBlocks;
import buildcraft.robotics.tile.TileZonePlanner;
import buildcraft.robotics.zone.ZonePlannerMapChunk;
import buildcraft.robotics.zone.ZonePlannerMapChunkKey;
import buildcraft.robotics.zone.ZonePlannerMapDataClient;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.concurrent.TimeUnit;

public class RenderZonePlanner extends TileEntitySpecialRenderer<TileZonePlanner> {
    private static final Cache<WorldPos, DynamicTextureBC> TEXTURES = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .removalListener(RenderZonePlanner::onRemove)
            .build();
    private static final int TEXTURE_WIDTH = 10;
    private static final int TEXTURE_HEIGHT = 8;

    private static void onRemove(RemovalNotification<WorldPos, DynamicTextureBC> notification) {
        DynamicTextureBC texture = notification.getValue();
        if (texture != null) {
            texture.deleteGlTexture();
        }
    }

    @Override
    public final void renderTileEntityAt(TileZonePlanner tile, double x, double y, double z, float partialTicks, int destroyStage) {
        Minecraft.getMinecraft().mcProfiler.startSection("bc");
        Minecraft.getMinecraft().mcProfiler.startSection("zone");

        double offset = 0.001;
        double minX = 3 / 16D - offset;
        double maxX = 13 / 16D + offset;
        double minY = 5 / 16D - offset;
        double maxY = 13 / 16D + offset;
        double minZ = -offset;
        double maxZ = 1 + offset;

        IBlockState state = tile.getWorld().getBlockState(tile.getPos());
        if (state.getBlock() != BCRoboticsBlocks.zonePlanner) {
            return;
        }
        EnumFacing side = state.getValue(BuildCraftProperties.BLOCK_FACING).getOpposite();

        DynamicTextureBC texture = getTexture(tile, side);
        if (texture == null) {
            return;
        }
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        texture.updateTexture();
        texture.bindGlTexture();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.disableBlend();
        GlStateManager.disableCull();
        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        buffer.setTranslation(x, y, z);

        Vec3d min;
        Vec3d max;

        float minU = 0;
        float maxU = texture.getMaxU();
        float minV = 0;
        float maxV = texture.getMaxV();

        switch (side) {
            case NORTH:
                min = new Vec3d(minX, minY, maxZ);
                max = new Vec3d(maxX, maxY, maxZ);
                break;
            case EAST:
                min = new Vec3d(minZ, minY, minX);
                max = new Vec3d(minZ, maxY, maxX);
                break;
            case SOUTH:
                min = new Vec3d(minX, minY, minZ);
                max = new Vec3d(maxX, maxY, minZ);
                break;
            case WEST:
            default:
                min = new Vec3d(maxZ, minY, minX);
                max = new Vec3d(maxZ, maxY, maxX);
                break;
        }

        MutableVertex vertex = new MutableVertex();

        vertex.colouri(-1);
        vertex.lighti(0xF, 0xF);

        vertex.positiond(min.xCoord, min.yCoord, min.zCoord).texf(minU, minV).render(buffer);
        vertex.positiond(max.xCoord, min.yCoord, max.zCoord).texf(maxU, minV).render(buffer);
        vertex.positiond(max.xCoord, max.yCoord, max.zCoord).texf(maxU, maxV).render(buffer);
        vertex.positiond(min.xCoord, max.yCoord, min.zCoord).texf(minU, maxV).render(buffer);

        buffer.setTranslation(0, 0, 0);
        tessellator.draw();
        RenderHelper.enableStandardItemLighting();

        Minecraft.getMinecraft().mcProfiler.endSection();
        Minecraft.getMinecraft().mcProfiler.endSection();
    }

    private static DynamicTextureBC getTexture(TileZonePlanner tile, EnumFacing side) {
        if (TEXTURES.getIfPresent(new WorldPos(tile)) == null) {
            DynamicTextureBC texture = createTexture(tile, side);
            if (texture != null) {
                TEXTURES.put(new WorldPos(tile), texture);
            }
        }
        return TEXTURES.getIfPresent(new WorldPos(tile));
    }

    private static DynamicTextureBC createTexture(TileZonePlanner tile, EnumFacing side) {
        DynamicTextureBC texture = new DynamicTextureBC(TEXTURE_WIDTH, TEXTURE_HEIGHT);
        for (int textureX = 0; textureX < TEXTURE_WIDTH; textureX++) {
            for (int textureY = 0; textureY < TEXTURE_HEIGHT; textureY++) {
                int posX;
                int posZ;
                int scale = 4;
                int offset1 = (textureX - TEXTURE_WIDTH / 2) * scale;
                int offset2 = (textureY - TEXTURE_HEIGHT / 2) * scale;
                switch (side) {
                    case NORTH:
                        posX = tile.getPos().getX() + offset1;
                        posZ = tile.getPos().getZ() - offset2;
                        break;
                    case EAST:
                        posX = tile.getPos().getX() + offset2;
                        posZ = tile.getPos().getZ() + offset1;
                        break;
                    case SOUTH:
                        posX = tile.getPos().getX() + offset1;
                        posZ = tile.getPos().getZ() + offset2;
                        break;
                    case WEST:
                    default:
                        posX = tile.getPos().getX() - offset2;
                        posZ = tile.getPos().getZ() + offset1;
                        break;
                }
                ChunkPos chunkPos = new ChunkPos(posX >> 4, posZ >> 4);
                texture.setColor(textureX, textureY, -1);
                ZonePlannerMapChunkKey key = new ZonePlannerMapChunkKey(
                        chunkPos,
                        tile.getWorld().provider.getDimension(),
                        tile.getLevel()
                );
                ZonePlannerMapChunk zonePlannerMapChunk = ZonePlannerMapDataClient.INSTANCE.getChunk(tile.getWorld(), key);
                if (zonePlannerMapChunk != null) {
                    texture.setColor(
                            textureX,
                            textureY,
                            zonePlannerMapChunk.getColour(posX, posZ) | 0xFF_00_00_00
                    );
                } else {
                    return null;
                }
            }
        }
        return texture;
    }
}
