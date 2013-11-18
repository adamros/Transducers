package adamros.mods.transducers.tileentity;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import adamros.mods.transducers.Constants;
import adamros.mods.transducers.Transducers;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;

public abstract class TileBase extends TileEntity
{
    public int getGuiId()
    {
        return -1;
    }

    public void sendButtonEvent(int buttonId)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(stream);

        try
        {
            data.writeInt(0);
            data.writeInt(xCoord);
            data.writeInt(yCoord);
            data.writeInt(zCoord);
            data.writeInt(buttonId);
        }
        catch (IOException e)
        {
            FMLLog.getLogger().info("Client failed to create button event packet: " + e.toString());
            return;
        }

        Transducers.proxy.sendPacketToServer(new Packet250CustomPayload(Constants.packetChannel, stream.toByteArray()));
    }

    protected Packet250CustomPayload createDescriptionPacket()
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(stream);

        try
        {
            data.writeInt(0);
            data.writeInt(xCoord);
            data.writeInt(yCoord);
            data.writeInt(zCoord);
            addCustomDescriptionData(data);
        }
        catch (IOException e)
        {
            FMLLog.getLogger().info("Client failed to create description packet: " + e.toString());
        }

        Packet250CustomPayload packet = new Packet250CustomPayload(Constants.packetChannel, stream.toByteArray());
        packet.isChunkDataPacket = true;
        return packet;
    }

    protected abstract void addCustomDescriptionData(DataOutputStream data) throws IOException;

    public abstract void getDescriptionData(int packetId, DataInputStream data);
}
