package buildcraft.lib.item;

import java.util.function.Function;

import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;

import buildcraft.lib.block.BlockBCBase_Neptune;

/** Basically a copy of {@link ItemMultiTexture}, but extends {@link ItemBC_Neptune} */
public class ItemBlockBCMulti extends ItemBlockBC_Neptune {
    protected final Function<ItemStack, String> nameFunction;

    public ItemBlockBCMulti(BlockBCBase_Neptune block, Function<ItemStack, String> nameFunction) {
        super(block);
        this.nameFunction = nameFunction;
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    public ItemBlockBCMulti(BlockBCBase_Neptune block, final String[] namesByMeta) {
        this(block, stack -> {
            int meta = stack.getMetadata();
            if (meta < 0 || meta >= namesByMeta.length) meta = 0;
            return namesByMeta[meta];
        });
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + this.nameFunction.apply(stack);
    }
}
