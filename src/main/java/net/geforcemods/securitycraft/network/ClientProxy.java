package net.geforcemods.securitycraft.network;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.models.DisguisableDynamicBakedModel;
import net.geforcemods.securitycraft.renderers.BouncingBettyRenderer;
import net.geforcemods.securitycraft.renderers.BulletRenderer;
import net.geforcemods.securitycraft.renderers.EmptyRenderer;
import net.geforcemods.securitycraft.renderers.IMSBombRenderer;
import net.geforcemods.securitycraft.renderers.ItemKeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.KeypadChestTileEntityRenderer;
import net.geforcemods.securitycraft.renderers.SecretSignTileEntityRenderer;
import net.geforcemods.securitycraft.renderers.SecurityCameraTileEntityRenderer;
import net.geforcemods.securitycraft.renderers.SentryRenderer;
import net.geforcemods.securitycraft.renderers.TrophySystemTileEntityRenderer;
import net.geforcemods.securitycraft.screen.BlockPocketManagerScreen;
import net.geforcemods.securitycraft.screen.BlockReinforcerScreen;
import net.geforcemods.securitycraft.screen.BriefcaseInventoryScreen;
import net.geforcemods.securitycraft.screen.BriefcasePasswordScreen;
import net.geforcemods.securitycraft.screen.BriefcaseSetupScreen;
import net.geforcemods.securitycraft.screen.CameraMonitorScreen;
import net.geforcemods.securitycraft.screen.CheckPasswordScreen;
import net.geforcemods.securitycraft.screen.CustomizeBlockScreen;
import net.geforcemods.securitycraft.screen.DisguiseModuleScreen;
import net.geforcemods.securitycraft.screen.EditModuleScreen;
import net.geforcemods.securitycraft.screen.EditSecretSignScreen;
import net.geforcemods.securitycraft.screen.IMSScreen;
import net.geforcemods.securitycraft.screen.InventoryScannerScreen;
import net.geforcemods.securitycraft.screen.KeyChangerScreen;
import net.geforcemods.securitycraft.screen.KeycardReaderSetupScreen;
import net.geforcemods.securitycraft.screen.KeypadFurnaceScreen;
import net.geforcemods.securitycraft.screen.MineRemoteAccessToolScreen;
import net.geforcemods.securitycraft.screen.SCManualScreen;
import net.geforcemods.securitycraft.screen.SentryRemoteAccessToolScreen;
import net.geforcemods.securitycraft.screen.SetPasswordScreen;
import net.geforcemods.securitycraft.screen.UsernameLoggerScreen;
import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.geforcemods.securitycraft.util.Reinforced;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid=SecurityCraft.MODID, bus=Bus.MOD, value=Dist.CLIENT)
public class ClientProxy implements IProxy
{
	@SubscribeEvent
	public static void onModelBake(ModelBakeEvent event)
	{
		String[] facings = {"east", "north", "south", "west"};
		String[] bools = {"true", "false"};
		ResourceLocation[] facingPoweredBlocks = {
				SCContent.keycardReader.getRegistryName(),
				SCContent.keypad.getRegistryName(),
				SCContent.retinalScanner.getRegistryName()
		};
		ResourceLocation[] facingBlocks = {
				SCContent.inventoryScanner.getRegistryName(),
				SCContent.usernameLogger.getRegistryName()
		};
		ResourceLocation[] poweredBlocks = {
				SCContent.laserBlock.getRegistryName()
		};

		for(String facing : facings)
		{
			for(String bool : bools)
			{
				for(ResourceLocation facingPoweredBlock : facingPoweredBlocks)
				{
					registerDisgiusedModel(event, facingPoweredBlock, "facing=" + facing + ",powered=" + bool);
				}
			}

			for(ResourceLocation facingBlock : facingBlocks)
			{
				registerDisgiusedModel(event, facingBlock, "facing=" + facing);
			}
		}

		for(String bool : bools)
		{
			for(ResourceLocation poweredBlock : poweredBlocks)
			{
				registerDisgiusedModel(event, poweredBlock, "powered=" + bool);
			}
		}

		registerDisgiusedModel(event, SCContent.cageTrap.getRegistryName(), "deactivated=true");
		registerDisgiusedModel(event, SCContent.cageTrap.getRegistryName(), "deactivated=false");
	}

	private static void registerDisgiusedModel(ModelBakeEvent event, ResourceLocation rl, String stateString)
	{
		ModelResourceLocation mrl = new ModelResourceLocation(rl, stateString);

		event.getModelRegistry().put(mrl, new DisguisableDynamicBakedModel(rl, event.getModelRegistry().get(mrl)));
	}

	@SubscribeEvent
	public static void onTextureStitchPre(TextureStitchEvent.Pre event)
	{
		if(event.getMap().getBasePath().equals(Atlases.CHEST_ATLAS))
		{
			event.addSprite(new ResourceLocation("securitycraft", "entity/chest/active"));
			event.addSprite(new ResourceLocation("securitycraft", "entity/chest/inactive"));
			event.addSprite(new ResourceLocation("securitycraft", "entity/chest/left_active"));
			event.addSprite(new ResourceLocation("securitycraft", "entity/chest/left_inactive"));
			event.addSprite(new ResourceLocation("securitycraft", "entity/chest/right_active"));
			event.addSprite(new ResourceLocation("securitycraft", "entity/chest/right_inactive"));
			event.addSprite(new ResourceLocation("securitycraft", "entity/chest/christmas"));
			event.addSprite(new ResourceLocation("securitycraft", "entity/chest/christmas_left"));
			event.addSprite(new ResourceLocation("securitycraft", "entity/chest/christmas_right"));
		}
	}

	@SubscribeEvent
	public static void onFMLClientSetup(FMLClientSetupEvent event)
	{
		RenderTypeLookup.setRenderLayer(SCContent.blockPocketManager, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(SCContent.blockPocketWall, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.cageTrap, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(SCContent.inventoryScanner, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(SCContent.inventoryScannerField, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.keycardReader, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(SCContent.keypad, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(SCContent.laserBlock, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(SCContent.laserField, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.retinalScanner, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(SCContent.usernameLogger, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedDoor, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedGlass, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedGlassPane, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedIronBars, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(SCContent.horizontalReinforcedIronBars, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedWhiteStainedGlass, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedOrangeStainedGlass, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedMagentaStainedGlass, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedLightBlueStainedGlass, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedYellowStainedGlass, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedLimeStainedGlass, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedPinkStainedGlass, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedGrayStainedGlass, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedLightGrayStainedGlass, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedCyanStainedGlass, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedPurpleStainedGlass, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedBlueStainedGlass, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedBrownStainedGlass, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedGreenStainedGlass, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedRedStainedGlass, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedBlackStainedGlass, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedWhiteStainedGlassPane, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedOrangeStainedGlassPane, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedMagentaStainedGlassPane, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedLightBlueStainedGlassPane, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedYellowStainedGlassPane, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedLimeStainedGlassPane, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedPinkStainedGlassPane, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedGrayStainedGlassPane, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedLightGrayStainedGlassPane, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedCyanStainedGlassPane, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedPurpleStainedGlassPane, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedBlueStainedGlassPane, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedBrownStainedGlassPane, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedGreenStainedGlassPane, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedRedStainedGlassPane, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.reinforcedBlackStainedGlassPane, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.scannerDoor, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(SCContent.trackMine, RenderType.cutout());
		RenderTypeLookup.setRenderLayer(SCContent.trophySystem, RenderType.cutoutMipped());
		RenderTypeLookup.setRenderLayer(SCContent.flowingFakeWater, RenderType.translucent());
		RenderTypeLookup.setRenderLayer(SCContent.fakeWater, RenderType.translucent());
	}

	@Override
	public void clientSetup()
	{
		RenderingRegistry.registerEntityRenderingHandler(SCContent.eTypeBouncingBetty, BouncingBettyRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SCContent.eTypeTaserBullet, EmptyRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SCContent.eTypeImsBomb, IMSBombRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SCContent.eTypeSecurityCamera, EmptyRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SCContent.eTypeSentry, SentryRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SCContent.eTypeBullet, BulletRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.teTypeKeypadChest, KeypadChestTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.teTypeSecurityCamera, SecurityCameraTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.teTypeSecretSign, SecretSignTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(SCContent.teTypeTrophySystem, TrophySystemTileEntityRenderer::new);
		ScreenManager.registerFactory(SCContent.cTypeBlockReinforcer, BlockReinforcerScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeBriefcase, BriefcasePasswordScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeBriefcaseInventory, BriefcaseInventoryScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeBriefcaseSetup, BriefcaseSetupScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeCustomizeBlock, CustomizeBlockScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeDisguiseModule, DisguiseModuleScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeInventoryScanner, InventoryScannerScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeKeypadFurnace, KeypadFurnaceScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeCheckPassword, CheckPasswordScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeSetPassword, SetPasswordScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeUsernameLogger, UsernameLoggerScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeIMS, IMSScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeKeycardSetup, KeycardReaderSetupScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeKeyChanger, KeyChangerScreen::new);
		ScreenManager.registerFactory(SCContent.cTypeBlockPocketManager, BlockPocketManagerScreen::new);
		KeyBindings.init();
	}

	@Override
	public void registerKeypadChestItem(Register<Item> event)
	{
		event.getRegistry().register(new BlockItem(SCContent.keypadChest, new Item.Properties().group(SecurityCraft.groupSCTechnical).setISTER(() -> () -> new ItemKeypadChestRenderer())).setRegistryName(SCContent.keypadChest.getRegistryName()));
	}

	@Override
	public void tint()
	{
		Map<Block,Integer> toTint = new HashMap<>();

		for(Field field : SCContent.class.getFields())
		{
			if(field.isAnnotationPresent(Reinforced.class) && field.getAnnotation(Reinforced.class).hasTint())
			{
				try
				{
					toTint.put((Block)field.get(null), field.getAnnotation(Reinforced.class).tint());
				}
				catch(IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}

		toTint.put(SCContent.blockPocketManager, 0x0E7063);
		toTint.put(SCContent.blockPocketWall, 0x0E7063);
		toTint.put(SCContent.chiseledCrystalQuartz, 0x15b3a2);
		toTint.put(SCContent.crystalQuartz, 0x15b3a2);
		toTint.put(SCContent.crystalQuartzPillar, 0x15b3a2);
		toTint.put(SCContent.crystalQuartzSlab, 0x15b3a2);
		toTint.put(SCContent.stairsCrystalQuartz, 0x15b3a2);
		toTint.forEach((block, tint) -> Minecraft.getInstance().getBlockColors().register((state, world, pos, tintIndex) -> tint, block));
		toTint.forEach((item, tint) -> Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> tint, item));
		Minecraft.getInstance().getBlockColors().register((state, world, pos, tintIndex) -> {
			Block block = state.getBlock();

			if(block instanceof DisguisableBlock)
			{
				Block blockFromItem = Block.getBlockFromItem(((DisguisableBlock)block).getDisguisedStack(world, pos).getItem());

				if(blockFromItem != Blocks.AIR && !(blockFromItem instanceof DisguisableBlock))
					return Minecraft.getInstance().getBlockColors().getColor(blockFromItem.getDefaultState(), world, pos, tintIndex);
			}

			return 0xFFFFFF;
		}, SCContent.cageTrap, SCContent.inventoryScanner, SCContent.keycardReader, SCContent.keypad, SCContent.laserBlock, SCContent.retinalScanner, SCContent.usernameLogger);
	}

	@Override
	public World getClientWorld()
	{
		return Minecraft.getInstance().world;
	}

	@Override
	public PlayerEntity getClientPlayer()
	{
		return Minecraft.getInstance().player;
	}

	@Override
	public void displayMRATGui(ItemStack stack)
	{
		Minecraft.getInstance().displayGuiScreen(new MineRemoteAccessToolScreen(stack));
	}

	@Override
	public void displaySRATGui(ItemStack stack)
	{
		Minecraft.getInstance().displayGuiScreen(new SentryRemoteAccessToolScreen(stack));
	}

	@Override
	public void displayEditModuleGui(ItemStack stack)
	{
		Minecraft.getInstance().displayGuiScreen(new EditModuleScreen(stack));
	}

	@Override
	public void displayCameraMonitorGui(PlayerInventory inv, CameraMonitorItem item, CompoundNBT stackTag)
	{
		Minecraft.getInstance().displayGuiScreen(new CameraMonitorScreen(inv, item, stackTag));
	}

	@Override
	public void displaySCManualGui()
	{
		Minecraft.getInstance().displayGuiScreen(new SCManualScreen());
	}

	@Override
	public void displayEditSecretSignGui(SecretSignTileEntity te)
	{
		Minecraft.getInstance().displayGuiScreen(new EditSecretSignScreen(te));
	}
}
