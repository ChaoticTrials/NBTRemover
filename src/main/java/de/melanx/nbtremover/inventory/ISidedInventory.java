package de.melanx.nbtremover.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;

public interface ISidedInventory {

    int[] getSlotsForFace(Direction side);

    boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction);

    boolean canExtractItem(int index, ItemStack stack, Direction direction);

}
