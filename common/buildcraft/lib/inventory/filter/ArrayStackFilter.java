/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package buildcraft.lib.inventory.filter;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import buildcraft.api.core.IStackFilter;
import buildcraft.api.recipes.StackDefinition;

import buildcraft.lib.misc.StackUtil;

import javax.annotation.Nonnull;

/** Returns true if the stack matches any one one of the filter stacks. */
public class ArrayStackFilter implements IStackFilter {

    protected NonNullList<ItemStack> stacks;

    public ArrayStackFilter(ItemStack... stacks) {
        this.stacks = StackUtil.listOf(stacks);
    }

    @Override
    public boolean matches(@Nonnull ItemStack stack) {
        if (stacks.size() == 0 || !hasFilter()) {
            return true;
        }
        for (ItemStack s : stacks) {
            if (StackUtil.isMatchingItem(s, stack)) {
                return true;
            }
        }
        return false;
    }

    public boolean matches(IStackFilter filter2) {
        for (ItemStack s : stacks) {
            if (filter2.matches(s)) {
                return true;
            }
        }

        return false;
    }

    public NonNullList<ItemStack> getStacks() {
        return stacks;
    }

    public boolean hasFilter() {
        for (ItemStack filter : stacks) {
            if (filter != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public NonNullList<ItemStack> getExamples() {
        return stacks;
    }

    public static StackDefinition definition(int count, ItemStack... stacks) {
        return new StackDefinition(new ArrayStackFilter(stacks), count);
    }

    public static StackDefinition definition(ItemStack... stacks) {
        return definition(1, stacks);
    }

    public static StackDefinition definition(int count, Block... blocks) {
        return definition(count, Arrays.stream(blocks).map(ItemStack::new).toArray(ItemStack[]::new));
    }

    public static StackDefinition definition(Block... blocks) {
        return definition(1, blocks);
    }

    public static StackDefinition definition(int count, Item... items) {
        return definition(count, Arrays.stream(items).map(ItemStack::new).toArray(ItemStack[]::new));
    }

    public static StackDefinition definition(Item... items) {
        return definition(1, items);
    }
}
