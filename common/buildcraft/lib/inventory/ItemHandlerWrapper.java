package buildcraft.lib.inventory;

import net.minecraft.item.ItemStack;

import net.minecraftforge.items.IItemHandler;

import buildcraft.api.core.IStackFilter;

import buildcraft.lib.misc.StackUtil;

import javax.annotation.Nonnull;

public final class ItemHandlerWrapper extends AbstractInvItemTransactor {
    private final IItemHandler wrapped;

    public ItemHandlerWrapper(IItemHandler handler) {
        this.wrapped = handler;
    }

    @Nonnull
    @Override
    protected ItemStack insert(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return wrapped.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    protected ItemStack extract(int slot, IStackFilter filter, int min, int max, boolean simulate) {
        if (min <= 0) min = 1;
        if (max < min) return StackUtil.EMPTY;
        ItemStack current = wrapped.getStackInSlot(slot);
        if (current.isEmpty() || current.getCount() < min) return StackUtil.EMPTY;
        if (filter.matches(asValid(current))) {
            return wrapped.extractItem(slot, max, simulate);
        }
        return StackUtil.EMPTY;
    }

    @Override
    protected int getSlots() {
        return wrapped.getSlots();
    }

    @Override
    protected boolean isEmpty(int slot) {
        return wrapped.getStackInSlot(slot).isEmpty();
    }
}
