package adamros.mods.transducers.pipe;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import adamros.mods.transducers.Transducers;
import adamros.mods.transducers.Utils;
import buildcraft.transport.IPipeTransportPowerHook;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportPower;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.pipes.PipePowerWood;
import buildcraft.api.core.IIconProvider;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import buildcraft.api.transport.IPipeTile;

public class HighThroughputPowerPipe extends Pipe<PipeTransportPower> implements IPowerReceptor, IPipeTransportPowerHook
{
    private PowerHandler powerHandler;
    private boolean powerSources[] = new boolean[6];

    public HighThroughputPowerPipe(int itemID)
    {
        super(new PipeTransportPower(), itemID);
        powerHandler = new PowerHandler(this, Type.PIPE);
        this.powerHandler.configure(1F, 4096F, 1F, 2000F);
        this.powerHandler.configurePowerPerdition(1, 100);
        transport.powerCapacities.put(HighThroughputPowerPipe.class, 4096);
        transport.initFromPipe(getClass());
    }

    @Override
    public PowerReceiver getPowerReceiver(ForgeDirection side)
    {
        return powerHandler.getPowerReceiver();
    }

    @Override
    public void doWork(PowerHandler workProvider) { }

    @Override
    public boolean canPipeConnect(TileEntity tile, ForgeDirection side)
    {
        if (tile instanceof TileGenericPipe)
        {
            if (((TileGenericPipe)tile).pipe instanceof PipePowerWood || ((TileGenericPipe)tile).pipe instanceof HighThroughputPowerPipe)
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (Utils.isClientSide() || powerHandler.getEnergyStored() <= 0.0F)
        {
            return;
        }

        int sources = 0;

        for (ForgeDirection o : ForgeDirection.VALID_DIRECTIONS)
        {
            if (!container.isPipeConnected(o))
            {
                powerSources[o.ordinal()] = false;
                continue;
            }

            if (powerHandler.isPowerSource(o))
            {
                powerSources[o.ordinal()] = true;
            }

            if (powerSources[o.ordinal()])
            {
                sources++;
            }
        }

        if (sources <= 0)
        {
            powerHandler.useEnergy(5, 5, true);
            return;
        }

        float energyToRemove;

        if (powerHandler.getEnergyStored() > 40)
        {
            energyToRemove = powerHandler.getEnergyStored() / 40 + 4;
        }
        else if (powerHandler.getEnergyStored() > 10)
        {
            energyToRemove = powerHandler.getEnergyStored() / 10;
        }
        else
        {
            energyToRemove = 1;
        }

        energyToRemove /= (float) sources;

        for (ForgeDirection o : ForgeDirection.VALID_DIRECTIONS)
        {
            if (!powerSources[o.ordinal()])
            {
                continue;
            }

            float energyUsable = powerHandler.useEnergy(0, energyToRemove, false);
            float energySent = transport.receiveEnergy(o, energyUsable);

            if (energySent > 0)
            {
                powerHandler.useEnergy(0, energySent, true);
            }
        }
    }

    @Override
    public World getWorld()
    {
        return this.container.worldObj;
    }

    @Override
    public int getIconIndex(ForgeDirection arg0)
    {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIconProvider getIconProvider()
    {
        return Transducers.instance.iconProvider;
    }

    @Override
    public float receiveEnergy(ForgeDirection arg0, float arg1)
    {
        return -1;
    }

    @Override
    public float requestEnergy(ForgeDirection arg0, float arg1)
    {
        if (!(container.getTile(arg0) instanceof IPipeTile))
        {
            return 0;
        }

        return arg1;
    }
}
