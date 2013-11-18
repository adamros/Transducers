package adamros.mods.transducers;

import java.lang.reflect.Field;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import buildcraft.api.core.BuildCraftAPI;
import buildcraft.api.core.IIconProvider;
import adamros.mods.transducers.block.BlockElectricEngine;
import adamros.mods.transducers.block.BlockPneumaticTransducer;
import adamros.mods.transducers.gui.GuiHandler;
import adamros.mods.transducers.item.ItemElectricEngine;
import adamros.mods.transducers.item.ItemPneumaticTransducer;
import adamros.mods.transducers.pipe.HighThroughputPowerPipe;
import adamros.mods.transducers.proxy.CommonProxy;
import adamros.mods.transducers.tileentity.TileElectricEngine;
import adamros.mods.transducers.tileentity.TilePTransducer;
import adamros.mods.transducers.trigger.TriggerElectricEngineHeat;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = Constants.modId, name = Constants.modName, version = Constants.modVersion, dependencies = "required-after:IC2@[2.0.237,);required-after:BuildCraft|Core;required-after:BuildCraft|Energy;required-after:BuildCraft|Transport")
@NetworkMod(clientSideRequired = true, serverSideRequired = false,
            clientPacketHandlerSpec = @SidedPacketHandler(channels = {Constants.packetChannel}, packetHandler = ClientPacketHandler.class),
            serverPacketHandlerSpec = @SidedPacketHandler(channels = {Constants.packetChannel}, packetHandler = ServerPacketHandler.class))
public class Transducers
{
    @Instance(value = Constants.modName)
    public static Transducers instance;

    public Config configuration = new Config();

    public static IconProvider iconProvider = new IconProvider();

    @SidedProxy(clientSide = Constants.clientProxy, serverSide = Constants.commonProxy)
    public static CommonProxy proxy;

    public static CreativeTabTransducers tabTransducers;
    public static BlockPneumaticTransducer blockPTransducer;
    public static BlockElectricEngine blockElectricEngine;

    public static boolean gregtechSupport;

    /*public static TriggerElectricEngineHeat lowHeatTrigger;
    public static TriggerElectricEngineHeat mediumHeatTrigger;
    public static TriggerElectricEngineHeat highHeatTrigger;
    public static TriggerElectricEngineHeat veryHighHeatTrigger;*/

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        instance = this;
        NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
        configuration.initConfiguration();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        configuration.loadConfiguration();
        tabTransducers = new CreativeTabTransducers();
        blockPTransducer = new BlockPneumaticTransducer(configuration.pneumaticGeneratorId);
        blockElectricEngine = new BlockElectricEngine(configuration.engineId);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        this.proxy.registerSpecials();
        this.proxy.registerRenderers();
        GameRegistry.registerBlock(blockPTransducer, ItemPneumaticTransducer.class, "pneumaticTransducer");
        GameRegistry.registerBlock(blockElectricEngine, ItemElectricEngine.class, "electricEngine");
        GameRegistry.registerTileEntity(TilePTransducer.class, "pneumaticTransducer");
        GameRegistry.registerTileEntity(TileElectricEngine.class, "electricEngine");

        if (configuration.gregtechSupport)
        {
            FMLLog.getLogger().severe("[Transducers] Gregtech support is still incomplete. Will continue loading with standard recipes.");
        }

        /*boolean tmp = false;

        try {
        	Class cls = Class.forName("gregtechmod.api.GregTech_API");

        	if (cls.getDeclaredField("VERSION").get(null) != null)
        	{
        		if (configuration.gregtechSupport)
        		{
        			FMLLog.getLogger().info("[Transducers] GregTech mod found - using GregTech items in recipes.");
        			gregtechSupport = true;
        		}
        		else {
        			FMLLog.getLogger().info("[Transducers] GregTech mod found, but recipes with GregTech items were disabled in configuration.");
        			gregtechSupport = false;
        		}
        	}
        } catch (ClassNotFoundException e) {
        	tmp = false;
        } catch (IllegalArgumentException e) {
        	tmp = false;
        } catch (IllegalAccessException e) {
        	tmp = false;
        } catch (NoSuchFieldException e) {
        	tmp = false;
        } catch (SecurityException e) {
        	tmp = false;
        }

        if (tmp)
        {
        	FMLLog.getLogger().info("[Transducers] GregTech mod not found - using default recipes.");
        	gregtechSupport = false;
        }*/
        this.proxy.registerTranslations();
        this.proxy.registerRecipes();
        /*lowHeatTrigger = new TriggerElectricEngineHeat(0.2F);
        mediumHeatTrigger = new TriggerElectricEngineHeat(0.6F);
        highHeatTrigger = new TriggerElectricEngineHeat(0.9F);
        veryHighHeatTrigger = new TriggerElectricEngineHeat(0.91F);*/
    }

    public static class IconProvider implements IIconProvider
    {
        private Icon highThrougputPowerPipeIcon;

        @Override
        public Icon getIcon(int iconIndex)
        {
            return highThrougputPowerPipeIcon;
        }

        @Override
        public void registerIcons(IconRegister iconRegister)
        {
            highThrougputPowerPipeIcon = iconRegister.registerIcon("transducers:pipePowerWood_highThroughput");
        }
    }
}