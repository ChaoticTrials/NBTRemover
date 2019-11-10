package de.melanx.nbtremover.setup;

import de.melanx.nbtremover.NBTRemover;
import de.melanx.nbtremover.block.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModSetup {

    public ItemGroup itemGroup = new ItemGroup(NBTRemover.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.NBTREMOVER);
        }
    };

}
