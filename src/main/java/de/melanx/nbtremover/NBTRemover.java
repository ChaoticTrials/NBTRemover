package de.melanx.nbtremover;

import de.melanx.nbtremover.block.ModBlocks;
import de.melanx.nbtremover.block.NBTRemoverBlock;
import de.melanx.nbtremover.block.NBTRemoverContainer;
import de.melanx.nbtremover.block.NBTRemoverTile;
import de.melanx.nbtremover.setup.ClientProxy;
import de.melanx.nbtremover.setup.IProxy;
import de.melanx.nbtremover.setup.ModSetup;
import de.melanx.nbtremover.setup.ServerProxy;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(NBTRemover.MODID)
public class NBTRemover {

    public static final String MODID = "nbtremover";

    public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
    public static ModSetup setup = new ModSetup();

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public NBTRemover() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        proxy.init();
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
            event.getRegistry().register(new NBTRemoverBlock());
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            Item.Properties properties = new Item.Properties()
                    .group(setup.itemGroup);
            event.getRegistry().register(new BlockItem(ModBlocks.NBTREMOVER, properties).setRegistryName("nbtremover"));
        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
            event.getRegistry().register(TileEntityType.Builder.create(NBTRemoverTile::new, ModBlocks.NBTREMOVER).build(null).setRegistryName("nbtremover"));
        }

        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                return new NBTRemoverContainer(windowId, NBTRemover.proxy.getClientWorld(), pos, inv, NBTRemover.proxy.getClientPlayer());
            }).setRegistryName("nbtremover"));
        }
    }
}
