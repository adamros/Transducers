package adamros.mods.transducers.proxy;

import buildcraft.transport.TransportProxyClient;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import adamros.mods.transducers.Transducers;
import adamros.mods.transducers.renderer.ItemEEngineRenderer;
import adamros.mods.transducers.renderer.RenderElectricEngine;
import adamros.mods.transducers.tileentity.TileElectricEngine;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    public void registerRenderers()
    {
        electricEngineRenderId = RenderingRegistry.getNextAvailableRenderId();
        ClientRegistry.bindTileEntitySpecialRenderer(TileElectricEngine.class, new RenderElectricEngine());
        MinecraftForgeClient.registerItemRenderer(Transducers.blockElectricEngine.blockID, new ItemEEngineRenderer());
        MinecraftForgeClient.registerItemRenderer(Transducers.instance.proxy.highThroughputPowerPipe.itemID, TransportProxyClient.pipeItemRenderer);
    }
}
