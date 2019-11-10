package de.melanx.nbtremover.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static de.melanx.nbtremover.block.ModBlocks.NBTREMOVER_TILE;

public class NBTRemoverTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
    ItemStack stackIn;
    ItemStack outSlotStack;

    public NBTRemoverTile() {
        super(NBTREMOVER_TILE);
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            return;
        }

        handler.ifPresent(h -> {
            stackIn = h.getStackInSlot(0);
            outSlotStack = h.getStackInSlot(1);
            if (!stackIn.isEmpty()) {
                stackIn.setTag(null);
                ItemStack stackOut;
                stackOut = stackIn.copy();
                if (outSlotStack.isEmpty() || (outSlotStack.getItem() == stackIn.getItem() && outSlotStack.getCount() < outSlotStack.getMaxStackSize())) {
                    stackOut.setCount(1);
                    h.extractItem(0, 1, false);
                    h.insertItem(1, stackOut, false);
                    markDirty();
                }
            }
        });

        BlockState blockState = world.getBlockState(pos);
        if (blockState.get(BlockStateProperties.POWERED) == stackIn.isEmpty()) {
            world.setBlockState(pos, blockState.with(BlockStateProperties.POWERED, !stackIn.isEmpty()), 3);
        }
        if (outSlotStack.getCount() >= 64) {
            world.setBlockState(pos, blockState.with(BlockStateProperties.POWERED, false), 3);
        }
    }

    @Override
    public void read(CompoundNBT tag) {
        CompoundNBT invTag = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(invTag));
        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("inv", compound);
        });
        return super.write(tag);
    }

    private IItemHandler createHandler() {
        return new ItemStackHandler(64) {

            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getDamage() <= 0;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (stack.getDamage() > 0 && slot != 0) {
                    return stack;
                }
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new NBTRemoverContainer(i, world, pos, playerInventory, playerEntity);
    }
}
