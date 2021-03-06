package buildcraft.lib.fluid;

import net.minecraft.block.material.MapColor;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import buildcraft.lib.misc.LocaleUtil;

public class BCFluid extends Fluid {
    private int colour = 0xFFFFFFFF, light = 0xFF_FF_FF_FF, dark = 0xFF_FF_FF_FF;
    private boolean isFlamable = false;
    private int lightOpacity = 0;
    private MapColor mapColour = MapColor.BLACK;
    private int heat;
    private boolean heatable;
    private String blockName;

    public BCFluid(String fluidName, ResourceLocation still, ResourceLocation flowing) {
        super(fluidName, still, flowing);
        blockName = fluidName;
    }

    @Override
    public String getLocalizedName(FluidStack stack) {
        if (heat <= 0 && !isHeatable()) return super.getLocalizedName(stack);
        String name = super.getLocalizedName(stack);
        String heatString = LocaleUtil.localize("buildcraft.fluid.heat_" + heat);
        return name + heatString;
    }

    public void setMapColour(MapColor mapColour) {
        this.mapColour = mapColour;
    }

    public final MapColor getMapColour() {
        return this.mapColour;
    }

    public void setFlamable(boolean isFlamable) {
        this.isFlamable = isFlamable;
    }

    public final boolean isFlammable() {
        return isFlamable;
    }

    public void setLightOpacity(int lightOpacity) {
        this.lightOpacity = lightOpacity;
    }

    public final int getLightOpacity() {
        return lightOpacity;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getBlockName() {
        return blockName;
    }

    @Override
    public int getColor() {
        return colour;
    }

    public int getLightColour() {
        return light;
    }

    public int getDarkColour() {
        return dark;
    }

    public BCFluid setColour(int colour) {
        this.colour = colour;
        return this;
    }

    public BCFluid setColour(int light, int dark) {
        this.light = light;
        this.dark = dark;
        this.colour = 0xFF_FF_FF_FF;
        return this;
    }

    public BCFluid setHeat(int heat) {
        this.heat = heat;
        return this;
    }

    public int getHeatValue() {
        return heat;
    }

    public BCFluid setHeatable(boolean value) {
        heatable = value;
        return this;
    }

    public boolean isHeatable() {
        return heatable;
    }
}
