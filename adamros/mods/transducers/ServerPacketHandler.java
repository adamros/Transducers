package adamros.mods.transducers;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class ServerPacketHandler implements IPacketHandler
{
    public ServerPacketHandler()
    {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onPacketData(INetworkManager manager,
                             Packet250CustomPayload packet, Player player)
    {
        // TODO Auto-generated method stub
    }
}
