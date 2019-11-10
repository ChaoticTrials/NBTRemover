package de.melanx.nbtremover.block;

import de.melanx.nbtremover.NBTRemover;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {

    @ObjectHolder(NBTRemover.MODID + ":nbtremover")
    public static NBTRemoverBlock NBTREMOVER;

    @ObjectHolder(NBTRemover.MODID + ":nbtremover")
    public static TileEntityType<NBTRemoverTile> NBTREMOVER_TILE;

    @ObjectHolder(NBTRemover.MODID + ":nbtremover")
    public static ContainerType<NBTRemoverContainer> NBTREMOVER_CONTAINER;

}
