package adamros.mods.transducers.proxy;

import ic2.api.item.Items;
import buildcraft.BuildCraftCore;
import buildcraft.BuildCraftEnergy;
import buildcraft.BuildCraftSilicon;
import buildcraft.BuildCraftTransport;
import buildcraft.api.transport.PipeManager;
import buildcraft.core.utils.Localization;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.ItemPipe;
import adamros.mods.transducers.Config;
import adamros.mods.transducers.Constants;
import adamros.mods.transducers.Transducers;
import adamros.mods.transducers.block.BlockPneumaticTransducer;
import adamros.mods.transducers.pipe.HighThroughputPowerPipe;
import adamros.mods.transducers.tileentity.TileElectricEngine;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;

public class CommonProxy
{
    public int electricEngineRenderId;
    public Item highThroughputPowerPipe;

    public static LanguageRegistry langRegistry = LanguageRegistry.instance();

    public void registerRenderers()
    {
    }

    public void registerRecipes()
    {
        // LV Electric Engine
        GameRegistry.addRecipe(new ItemStack(Transducers.blockElectricEngine, 1, 0), new Object[] { "X", "Y", "Z", 'X', Items.getItem("reBattery"), 'Y', new ItemStack(BuildCraftEnergy.engineBlock, 1, 1), 'Z', Items.getItem("electronicCircuit") });
        // MV Electric Engine
        GameRegistry.addRecipe(new ItemStack(Transducers.blockElectricEngine, 1, 1), new Object[] { " A ", " Z ", "XYX", 'X', Items.getItem("reBattery"), 'Y', new ItemStack(Transducers.instance.blockElectricEngine, 1, 0), 'Z', Items.getItem("transformerUpgrade"), 'A', Items.getItem("electronicCircuit") });
        // HV Electric Engine
        GameRegistry.addRecipe(new ItemStack(Transducers.blockElectricEngine, 1, 2), new Object[] { "PAP", "XAX", "ZYZ", 'X', Items.getItem("transformerUpgrade"), 'Y', new ItemStack(Transducers.instance.blockElectricEngine, 1, 1), 'Z', Items.getItem("energyCrystal"), 'A', Items.getItem("advancedCircuit"), 'P', Items.getItem("advancedAlloy") });
        // EV Electric Engine
        GameRegistry.addRecipe(new ItemStack(Transducers.blockElectricEngine, 1, 3), new Object[] { "PXP", "XAX", "ZYZ", 'X', Items.getItem("transformerUpgrade"), 'Y', new ItemStack(Transducers.instance.blockElectricEngine, 1, 2), 'Z', Items.getItem("lapotronCrystal"), 'A', Items.getItem("advancedCircuit"), 'P', Items.getItem("iridiumPlate") });
        // LV Pneumatic Transducer
        GameRegistry.addRecipe(new ItemStack(Transducers.blockPTransducer, 1, 0), new Object[] { "ZYZ", "ZAZ", "ZXZ", 'X', Items.getItem("generator"), 'Y', Items.getItem("electronicCircuit"), 'Z', Items.getItem("tinCableItem"), 'A', Items.getItem("machine") });
        // MV Pneumatic Transducer
        GameRegistry.addRecipe(new ItemStack(Transducers.blockPTransducer, 1, 1), new Object[] { "ZAZ", "ZAZ", "YXY", 'X', new ItemStack(Transducers.instance.blockPTransducer, 1, 0), 'Y', Items.getItem("electronicCircuit"), 'Z', Items.getItem("insulatedCopperCableItem"), 'A', new ItemStack(BuildCraftSilicon.instance.redstoneChipset, 1, 2) });
        // HV Pneumatic Transducer
        GameRegistry.addRecipe(new ItemStack(Transducers.blockPTransducer, 1, 2), new Object[] { "ZBZ", "ZAZ", "YXY", 'X', new ItemStack(Transducers.instance.blockPTransducer, 1, 1), 'Y', Items.getItem("advancedCircuit"), 'Z', Items.getItem("insulatedGoldCableItem"), 'A', new ItemStack(BuildCraftSilicon.instance.redstoneChipset, 1, 3), 'B', Items.getItem("advancedAlloy") });
        // EV Pneumatic Transducer
        GameRegistry.addRecipe(new ItemStack(Transducers.blockPTransducer, 1, 3), new Object[] { "ZBZ", "ZAZ", "YXY", 'X', new ItemStack(Transducers.instance.blockPTransducer, 1, 2), 'Y', Items.getItem("advancedCircuit"), 'Z', Items.getItem("insulatedIronCableItem"), 'A', new ItemStack(BuildCraftSilicon.instance.redstoneChipset, 1, 3), 'B', Items.getItem("iridiumPlate") });
        // High Throughput Power Pipe
        GameRegistry.addShapelessRecipe(new ItemStack(this.highThroughputPowerPipe, 1), new ItemStack(BuildCraftTransport.instance.pipePowerWood, 1), Items.getItem("advancedCircuit"), new ItemStack(BuildCraftSilicon.instance.redstoneChipset, 1, 2));
    }

    public void registerSpecials()
    {
        int pipeId = Transducers.instance.configuration.highThroughputPowerPipeId;
        highThroughputPowerPipe = BlockGenericPipe.registerPipe(pipeId, HighThroughputPowerPipe.class).setCreativeTab(Transducers.instance.tabTransducers);
    }

    public void registerTranslations()
    {
        langRegistry.addStringLocalization("tile.pneumaticTransducer.lv.name", Constants.lvPneumaticTransducerName);
        langRegistry.addStringLocalization("tile.pneumaticTransducer.mv.name", Constants.mvPneumaticTransducerName);
        langRegistry.addStringLocalization("tile.pneumaticTransducer.hv.name", Constants.hvPneumaticTransducerName);
        langRegistry.addStringLocalization("tile.pneumaticTransducer.ev.name", Constants.evPneumaticTransducerName);
        langRegistry.addStringLocalization("tile.electricEngine.lv.name", Constants.lvElectricEngineName);
        langRegistry.addStringLocalization("tile.electricEngine.mv.name", Constants.mvElectricEngineName);
        langRegistry.addStringLocalization("tile.electricEngine.hv.name", Constants.hvElectricEngineName);
        langRegistry.addStringLocalization("tile.electricEngine.ev.name", Constants.evElectricEngineName);
        langRegistry.addStringLocalization("itemGroup.Transducers", Constants.modName);
        langRegistry.addStringLocalization("item.buildcraftPipe.highthroughputpowerpipe", Constants.highThroughputPowerPipe);
        langRegistry.addName(new ItemStack(highThroughputPowerPipe, 1, 0), Constants.highThroughputPowerPipe);
        Localization.addLocalization("/assets/transducers/lang/", "en_US");
    }

    public static void sendPacketToPlayer(Packet250CustomPayload packet, EntityPlayerMP player)
    {
        PacketDispatcher.sendPacketToPlayer(packet, (Player)player);
    }

    public static void sendPacketToServer(Packet250CustomPayload packet)
    {
        PacketDispatcher.sendPacketToServer(packet);
    }
}
