package buildcraft.factory.client.render;

import buildcraft.factory.tile.TileMiner;
import buildcraft.lib.client.render.laser.LaserData_BC8;
import buildcraft.lib.client.render.laser.LaserData_BC8.LaserType;
import buildcraft.lib.client.render.laser.LaserRenderer_BC8;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.animation.FastTESR;

import javax.annotation.Nonnull;

public class RenderTube extends FastTESR<TileMiner> {
    private final LaserType laserType;

    public RenderTube(LaserType laserType) {
        this.laserType = laserType;
    }

    @Override
    public void renderTileEntityFast(@Nonnull TileMiner tile, double x, double y, double z, float partialTicks, int destroyStage, @Nonnull VertexBuffer buffer) {
        double tubeY = tile.getPos().getY() - tile.getLength(partialTicks);
        if (tubeY == 0) {
            return;
        }

        BlockPos from = tile.getPos();
        buffer.setTranslation(x - from.getX(), y - from.getY(), z - from.getZ());

        Vec3d start = new Vec3d(from.getX() + 0.5, from.getY(), from.getZ() + 0.5);

        Vec3d end = new Vec3d(from.getX() + 0.5, tubeY, from.getZ() + 0.5);

        LaserData_BC8 data = new LaserData_BC8(laserType, start, end, 1 / 16.0);
        LaserRenderer_BC8.renderLaserDynamic(data, buffer);

        buffer.setTranslation(0, 0, 0);
    }
}
