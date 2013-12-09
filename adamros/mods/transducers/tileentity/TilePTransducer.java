package adamros.mods.transducers.tileentity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import adamros.mods.transducers.Constants;
import adamros.mods.transducers.Transducers;
import adamros.mods.transducers.Utils;
import buildcraft.api.gates.IOverrideDefaultTriggers;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import buildcraft.api.tools.IToolWrench;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;
import ic2.api.energy.event.EnergyTileEvent;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.item.Items;
import ic2.api.tile.IWrenchable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;

public class TilePTransducer extends TileBase implements IEnergySource, IPowerReceptor, IPipeConnection, IWrenchable
{
    public int tier = 0;
    public boolean isAddedToENet = false;
    public double maxEnergyOutput;
    public float remainingIC2Energy;
    public float remainingMJEnergy;

    double energyToOutput = 0;

    // Flags for sides; 0 = normal side, 1 = input, 2 = output
    public short[] sideFlags = {0, 0, 0, 0, 0, 0};

    private PowerHandler powerHandler = new PowerHandler(this, Type.MACHINE);

    public TilePTransducer()
    {
    }

    public TilePTransducer(int tier)
    {
        this.tier = tier;
    }

    private void initPowerHandler()
    {
        switch (tier)
        {
            case 0:
                this.powerHandler.configure(1.001F, 12.8F, 1, 1000);
                break;

            case 1:
                this.powerHandler.configure(12.801F, 51.2F, 1, 1000);
                break;

            case 2:
                this.powerHandler.configure(51.201F, 204.8F, 1, 1000);
                break;

            case 3:
                this.powerHandler.configure(204.801F, 819.2F, 1, 1000);
                break;

            default:
                this.powerHandler.configure(1.001F, 15F, 1, 1000);
                break;
        }

        this.powerHandler.configurePowerPerdition(1, 100);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.tier = nbt.getInteger("tier");
        this.maxEnergyOutput = nbt.getDouble("maxoutput");
        NBTTagList sideSettings = nbt.getTagList("sideflags");

        for (int i = 0; i < sideSettings.tagCount(); ++i)
        {
            NBTTagCompound entry = (NBTTagCompound)sideSettings.tagAt(i);

            if (i >= 0 && i < this.sideFlags.length)
            {
                this.sideFlags[i] = entry.getShort("flag");
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("tier", this.tier);
        nbt.setDouble("maxoutput", this.maxEnergyOutput);
        NBTTagList sideSettings = new NBTTagList();

        for (int i = 0; i < this.sideFlags.length; ++i)
        {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setShort("flag", this.sideFlags[i]);
            sideSettings.appendTag(entry);
        }

        nbt.setTag("sideflags", sideSettings);
    }

    @Override
    public void validate()
    {
        super.validate();
        MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
        initPowerHandler();

        switch (tier)
        {
            case 0:
                this.maxEnergyOutput = 32D;
                break;

            case 1:
                this.maxEnergyOutput = 128D;
                break;

            case 2:
                this.maxEnergyOutput = 512D;
                break;

            case 3:
                this.maxEnergyOutput = 2048D;
                break;

            default:
                this.maxEnergyOutput = 32D;
                break;
        }
    }

    @Override
    public void invalidate()
    {
        if (this.isAddedToENet)
        {
            EnergyTileUnloadEvent event = new EnergyTileUnloadEvent(this);
            MinecraftForge.EVENT_BUS.post(event);
            this.isAddedToENet = false;
        }

        super.invalidate();
    }

    @Override
    public void updateEntity()
    {
        if (Utils.isClientSide())
        {
            return;
        }

        if (!isAddedToENet && worldObj != null)
        {
            EnergyTileLoadEvent event = new EnergyTileLoadEvent(this);
            MinecraftForge.EVENT_BUS.post(event);
            this.isAddedToENet = true;
        }
    }

    @Override
    public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction)
    {
        if (this.sideFlags[direction.ordinal()] != 2)
        {
            try
            {
                Class cls = Class.forName("ic2.core.block.wiring.TileEntityCable").getClass();

                if (receiver.getClass().isInstance(cls))
                {
                    Field f = cls.cast(receiver).getClass().getDeclaredField("connectivity");
                    Object tmp = f.get(Class.forName("ic2.core.block.wiring.TileEntityCable"));
                    f.set(tmp, (Object)0);
                }
            }
            catch (ClassNotFoundException e)
            {
                FMLLog.getLogger().warning("[Transducers] Cannot find IC2 TileEntityCable class!");
            }
            catch (NoSuchFieldException e)
            {
                FMLLog.getLogger().warning("[Transducers] Cannot find IC2 TileEntityCable class -> connectivity field!");
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }

            return false;
        }

        return true;
    }

    @Override
    public PowerReceiver getPowerReceiver(ForgeDirection side)
    {
        return this.powerHandler.getPowerReceiver();
    }

    @Override
    public void doWork(PowerHandler workProvider)
    {
        float min = this.powerHandler.getActivationEnergy();
        float max = (float)(((this.maxEnergyOutput * Constants.numerator + ((this.remainingIC2Energy < Constants.denominator) ? Constants.denominator : 0) - this.maxEnergyOutput * Constants.numerator % Constants.denominator) / Constants.denominator) + this.remainingMJEnergy);

        if ((this.powerHandler.useEnergy(min, max, false) % Constants.numerator) > 0)
        {
            this.remainingMJEnergy += this.powerHandler.useEnergy(min, max, false) % Constants.numerator;
        }

        float energyToTransduce = this.powerHandler.useEnergy(min, (float) Math.min(this.powerHandler.useEnergy(min, max, false) - (this.powerHandler.useEnergy(min, max, false) % Constants.numerator), 2048.0F - (2048.0F % Constants.numerator)), true);
        this.energyToOutput = (energyToTransduce / Constants.numerator * Constants.denominator);

        if (this.energyToOutput > this.maxEnergyOutput)
        {
            this.remainingIC2Energy += this.energyToOutput - this.maxEnergyOutput;
            this.energyToOutput = this.maxEnergyOutput;
        }
        else if (this.energyToOutput < this.maxEnergyOutput && this.remainingIC2Energy > 0)
        {
            double tmp = this.maxEnergyOutput - this.energyToOutput;

            if (this.remainingIC2Energy >= tmp)
            {
                this.remainingIC2Energy -= tmp;
                this.energyToOutput = this.maxEnergyOutput;
            }
            else if (this.remainingIC2Energy < tmp)
            {
                this.energyToOutput += this.remainingIC2Energy;
                this.remainingIC2Energy = 0;
            }
        }
    }

    @Override
    public World getWorld()
    {
        return this.worldObj;
    }

    @Override
    public double getOfferedEnergy()
    {
        return this.energyToOutput;
    }

    @Override
    public void drawEnergy(double amount)
    {
        this.energyToOutput -= amount;
    }

    @Override
    public Packet250CustomPayload getDescriptionPacket()
    {
        return createDescriptionPacket();
    }

    @Override
    protected void addCustomDescriptionData(DataOutputStream data) throws IOException
    {
        for (int i = 0; i < this.sideFlags.length; i++)
        {
            data.writeShort(this.sideFlags[i]);
        }
    }

    @Override
    public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with)
    {
        if (type == PipeType.POWER)
        {
            return (this.sideFlags[with.ordinal()] == 1) ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT;
        }

        return ConnectOverride.DISCONNECT;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getDescriptionData(int packetId, DataInputStream data)
    {
        try
        {
            for (int i = 0; i < this.sideFlags.length; i++)
            {
                this.sideFlags[i] = data.readShort();
            }
        }
        catch (IOException e)
        {
            FMLLog.getLogger().warning("Client failed to read description data: " + e.toString());
            return;
        }

        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side)
    {
        return !entityPlayer.isSneaking();
    }

    @Override
    public short getFacing()
    {
        return 0;
    }

    private void setSideFlag(short side, int sideFlag)
    {
        if (sideFlag == 1)
        {
            for (int i = 0; i < this.sideFlags.length; i++)
            {
                if (this.sideFlags[i] == 1)
                {
                    this.sideFlags[i] = 0;
                }
            }

            this.sideFlags[side] = 1;
        }
        else if (sideFlag == 2)
        {
            for (int i = 0; i < this.sideFlags.length; i++)
            {
                if (this.sideFlags[i] == 2)
                {
                    this.sideFlags[i] = 0;
                }
            }

            this.sideFlags[side] = 2;
        }
        else
        {
            this.sideFlags[side] = 0;
        }
    }

    @Override
    public void setFacing(short facing)
    {
        if (this.isAddedToENet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            this.isAddedToENet = false;
        }

        switch (this.sideFlags[facing])
        {
            case 0:
                setSideFlag(facing, 1);
                break;

            case 1:
                setSideFlag(facing, 2);
                break;

            case 2:
                setSideFlag(facing, 0);
                break;
        }

        if (!this.isAddedToENet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            this.isAddedToENet = true;
        }

        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord));
    }

    @Override
    public boolean wrenchCanRemove(EntityPlayer entityPlayer)
    {
        return entityPlayer.isSneaking();
    }

    @Override
    public float getWrenchDropRate()
    {
        return 1.0F;
    }

    @Override
    public ItemStack getWrenchDrop(EntityPlayer entityPlayer)
    {
        if (!entityPlayer.capabilities.isCreativeMode)
        {
            return new ItemStack(this.worldObj.getBlockId(xCoord, yCoord, zCoord), 1, this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
        }
        else
        {
            return null;
        }
    }
}
