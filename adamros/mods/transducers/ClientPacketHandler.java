package adamros.mods.transducers;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import adamros.mods.transducers.tileentity.TileBase;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientPacketHandler implements IPacketHandler
{
    public ClientPacketHandler()
    {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
        int packetType = -1;
        int xCoord = 0;
        int yCoord = 0;
        int zCoord = 0;

        try
        {
            packetType = data.readInt();
            xCoord = data.readInt();
            yCoord = data.readInt();
            zCoord = data.readInt();
        }
        catch (IOException e)
        {
            FMLLog.getLogger().warning("Failed to read description packet from server: " + e.toString());
            return;
        }

        if (packetType == 0)
        {
            World world = FMLClientHandler.instance().getClient().theWorld;
            TileEntity tile = world.getBlockTileEntity(xCoord, yCoord, zCoord);

            try
            {
                ((TileBase)tile).getDescriptionData(packetType, data);
                return;
            }
            catch (NullPointerException e)
            {
                FMLLog.getLogger().warning("Received description packet for x:" + xCoord + ", y:" + yCoord + ", z:" + zCoord + ", but could not pass it to tile entity");
            }

            return;
        }
    }
}
