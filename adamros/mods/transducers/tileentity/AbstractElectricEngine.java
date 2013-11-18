package adamros.mods.transducers.tileentity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.IEnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergySink;
import adamros.mods.transducers.Constants;
import adamros.mods.transducers.Transducers;
import adamros.mods.transducers.Utils;
import buildcraft.api.core.SafeTimeTracker;
import buildcraft.api.gates.IOverrideDefaultTriggers;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.IPipeTile.PipeType;

public abstract class AbstractElectricEngine extends TileBase implements IPowerEmitter, IPowerReceptor, IOverrideDefaultTriggers, IPipeConnection, IEnergySink, IEnergyAcceptor
{
    protected PowerHandler powerHandler = new PowerHandler(this, Type.ENGINE);
    public ForgeDirection orientation = ForgeDirection.UP;
    private SafeTimeTracker timeTracker = new SafeTimeTracker();
    private SafeTimeTracker packetRateLimit = new SafeTimeTracker();

    private boolean isAddedToENet = false;
    public float progress;
    public float progressPart;
    public float energy;
    public float heat = 20;
    private boolean isValid = false;
    public boolean validDirection = false;
    private float currentOutput;

    public double convertedIC2Energy;
    public boolean isWorking = false;

    private TileEntity tilesOnSides[] = new TileEntity[6];

    private void detectTilesOnSides()
    {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
        {
            if (!this.worldObj.blockExists(this.xCoord + dir.offsetX, this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ))
            {
                this.tilesOnSides[dir.ordinal()] = null;
                break;
            }

            int blockID = this.worldObj.getBlockId(this.xCoord + dir.offsetX, this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ);
            Block block = Block.blocksList[blockID];

            if ((block != null) && (block.hasTileEntity(this.worldObj.getBlockMetadata(this.xCoord + dir.offsetX, this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ))))
            {
                this.tilesOnSides[dir.ordinal()] = (TileEntity)this.worldObj.getBlockTileEntity(this.xCoord + dir.offsetX, this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ);
            }
            else
            {
                this.tilesOnSides[dir.ordinal()] = null;
            }
        }
    }

    @Override
    public void validate()
    {
        super.validate();
        timeTracker.markTime(worldObj);
        packetRateLimit.markTime(worldObj);
    }

    @Override
    public void invalidate()
    {
        if (this.isAddedToENet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
            this.isAddedToENet = false;
        }

        super.invalidate();
    }

    public abstract float minEnergyReceived();

    public abstract float maxEnergyReceived();

    public abstract float getMaxEnergy();

    public abstract float explosionRange();

    public abstract float getCurrentOutput();

    public float maxEnergyExtracted()
    {
        return this.getCurrentOutput();
    }

    public float getEngineSpeed()
    {
        return 0.36F * (this.heat / 180);
    }

    public void updateHeat()
    {
        this.heat = ((200 - 20) * (this.energy / this.getMaxEnergy()) + 20);
    }

    public float getHeat()
    {
        return (this.heat - 20) / (200 - 20);
    }

    private void sendPower()
    {
        TileEntity tile = this.tilesOnSides[this.orientation.ordinal()];

        if (tile instanceof IPowerReceptor)
        {
            if (((IPowerReceptor)tile).getPowerReceiver(this.orientation) != null)
            {
                PowerReceiver receptor = ((IPowerReceptor)tile).getPowerReceiver(this.orientation);
                receptor.receiveEnergy(PowerHandler.Type.ENGINE, this.getCurrentOutput(), this.orientation.getOpposite());
                currentOutput = extractEnergy(0, this.getCurrentOutput(), true);
                //TODO: Dynamic energy output
            }
        }
    }

    public float extractEnergy(float min, float max, boolean doExtract)
    {
        if (energy < min)
        {
            return 0;
        }

        float actualMax;

        if (max > maxEnergyExtracted())
        {
            actualMax = maxEnergyExtracted();
        }
        else
        {
            actualMax = max;
        }

        if (actualMax < min)
        {
            return 0;
        }

        float extracted;

        if (energy >= actualMax)
        {
            extracted = actualMax;

            if (doExtract)
            {
                this.energy -= actualMax;
            }
        }
        else
        {
            extracted = energy;

            if (doExtract)
            {
                this.energy = 0;
            }
        }

        return extracted;
    }

    public double transduce(double amount)
    {
        if (amount > this.getMaxSafeInput())
        {
            this.convertedIC2Energy += (this.getMaxSafeInput() * Constants.numerator) / Constants.denominator;
            return amount - getMaxSafeInput();
        }
        else
        {
            this.convertedIC2Energy += (amount * Constants.numerator) / Constants.denominator;
            return 0D;
        }
    }

    @Override
    public void updateEntity()
    {
        if (this.worldObj != null)
        {
            if (this.timeTracker.markTimeIfDelay(worldObj, 20))
            {
                this.isValid = false;
                this.timeTracker.markTime(worldObj);

                if (!this.isValid)
                {
                    detectTilesOnSides();
                    this.isValid = true;
                }
            }

            if (!isOrientationValid())
            {
                changeOrientation(true);
            }

            if (!this.isAddedToENet && !worldObj.isRemote)
            {
                MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
                this.isAddedToENet = true;
            }

            if (!isRedstonePowered())
            {
                if (this.energy >= 1)
                {
                    this.energy -= 1;
                }
                else if (this.energy < 1)
                {
                    this.energy = 0;
                }
            }

            updateHeat();

            if (worldObj.isRemote)
            {
                if (this.progressPart != 0)
                {
                    this.progress += getEngineSpeed();

                    if (this.progress >= 1)
                    {
                        this.progressPart = 0;
                        this.progress = 0;
                    }
                }
                else if (this.isWorking)
                {
                    this.progressPart = 1;
                }

                return;
            }

            if (this.progressPart != 0)
            {
                this.progress += this.getEngineSpeed();

                if (this.progress > 0.5 && this.progressPart == 1)
                {
                    this.progressPart = 2;

                    if (isRedstonePowered() && this.energy > this.getCurrentOutput())
                    {
                        sendPower();
                    }
                    else
                    {
                        this.progressPart = 0;
                    }
                }
                else if (this.progress >= 1)
                {
                    this.progress = 0;
                    this.progressPart = 0;
                }
            }
            else if (isRedstonePowered() && this.energy > this.getCurrentOutput())
            {
                this.progressPart = 1;
                setWorking(true);
            }
            else
            {
                setWorking(false);
            }

            getPower();

            if (packetRateLimit.markTimeIfDelay(worldObj, 10))
            {
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            }
        }
    }

    private void getPower()
    {
        if ((this.convertedIC2Energy - this.getCurrentOutput() >= 0) && (isRedstonePowered()) && (this.energy + this.getCurrentOutput() <= this.getMaxEnergy()))
        {
            addEnergy(this.getCurrentOutput() * 0.25F);
            this.convertedIC2Energy -= this.getCurrentOutput() * 0.25F;
        }
    }

    public void setWorking(boolean working)
    {
        if (this.isWorking == working)
        {
            return;
        }

        this.isWorking = working;
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    public void addEnergy(float energyToAdd)
    {
        this.energy += energyToAdd;

        if (getHeat() > 0.97f)
        {
            //this.worldObj.createExplosion((Entity)null, this.xCoord, this.yCoord, this.zCoord, explosionRange(), true);
            //this.worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
            //this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord));
            FMLLog.getLogger().warning("Overheat!");
        }

        if (this.energy > getMaxEnergy())
        {
            this.energy = getMaxEnergy();
        }
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction)
    {
        if (direction.getOpposite() == this.orientation)
        {
            return true;
        }
        else
        {
            try
            {
                Class cls = Class.forName("ic2.core.block.wiring.TileEntityCable").getClass();

                if (emitter.getClass().isInstance(cls))
                {
                    Field f = cls.cast(emitter).getClass().getDeclaredField("connectivity");
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
    }

    @Override
    public double demandedEnergyUnits()
    {
        if ((this.getMaxIC2EnergyStorage() - this.convertedIC2Energy) > this.getMaxSafeInput())
        {
            return this.getMaxSafeInput();
        }

        return (this.getMaxIC2EnergyStorage() - this.convertedIC2Energy);
    }

    @Override
    public double injectEnergyUnits(ForgeDirection directionFrom, double amount)
    {
        if (amount > getMaxSafeInput())
        {
            this.worldObj.createExplosion((Entity)null, xCoord, yCoord, zCoord, explosionRange(), true);
            this.worldObj.setBlockToAir(xCoord, yCoord, zCoord);
            this.worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord));
            return 0;
        }
        else if (directionFrom == this.orientation)
        {
            return amount;
        }
        else
        {
            return transduce(amount);
        }
    }

    @Override
    public abstract int getMaxSafeInput();

    public abstract double getMaxIC2EnergyStorage();

    @Override
    public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with)
    {
        if (type == PipeType.POWER)
        {
            if (this.orientation == with)
            {
                return ConnectOverride.CONNECT;
            }
            else
            {
                return ConnectOverride.DISCONNECT;
            }
        }
        else
        {
            return ConnectOverride.DISCONNECT;
        }
    }

    @Override
    public LinkedList<ITrigger> getTriggers()
    {
        /*LinkedList<ITrigger> triggers = new LinkedList<ITrigger>();

        triggers.add(Transducers.instance.lowHeatTrigger);
        triggers.add(Transducers.instance.mediumHeatTrigger);
        triggers.add(Transducers.instance.highHeatTrigger);
        triggers.add(Transducers.instance.veryHighHeatTrigger);

        return triggers;*/
        return null;
    }

    @Override
    public PowerReceiver getPowerReceiver(ForgeDirection side)
    {
        return this.powerHandler.getPowerReceiver();
    }

    @Override
    public void doWork(PowerHandler workProvider)
    {
        /* Do nothing :P */
    }

    @Override
    public World getWorld()
    {
        return this.worldObj;
    }

    @Override
    public boolean canEmitPowerFrom(ForgeDirection side)
    {
        return (this.orientation == side);
    }

    @Override
    public Packet250CustomPayload getDescriptionPacket()
    {
        return createDescriptionPacket();
    }

    @Override
    protected void addCustomDescriptionData(DataOutputStream data) throws IOException
    {
        data.writeInt(this.orientation.ordinal());
        //data.writeFloat(this.heat);
        data.writeFloat(this.energy);
        //data.writeFloat(this.progress);
        data.writeBoolean(this.isWorking);
        //data.writeFloat(this.progressPart);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getDescriptionData(int packetId, DataInputStream data)
    {
        try
        {
            orientation = ForgeDirection.getOrientation(data.readInt());
            //heat = data.readFloat();
            energy = data.readFloat();
            //progress = data.readFloat();
            isWorking = data.readBoolean();
            //progressPart = data.readFloat();
        }
        catch (IOException e)
        {
            FMLLog.getLogger().warning("Client failed to read description data: " + e.toString());
            return;
        }

        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        orientation = ForgeDirection.getOrientation(nbt.getInteger("orientation"));
        energy = nbt.getFloat("mjenergy");
        progress = nbt.getFloat("progress");
        heat = nbt.getFloat("heat");
        convertedIC2Energy = nbt.getDouble("convertedIc2Energy");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("orientation", orientation.ordinal());
        nbt.setFloat("mjenergy", energy);
        nbt.setFloat("progress", progress);
        nbt.setFloat("heat", heat);
        nbt.setDouble("convertedIc2Energy", convertedIC2Energy);
    }

    public boolean isRedstonePowered()
    {
        return this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);
    }

    public boolean changeOrientation(boolean toPipe)
    {
        for (ForgeDirection i : ForgeDirection.VALID_DIRECTIONS)
        {
            TileEntity tile = this.tilesOnSides[i.ordinal()];

            if ((!toPipe && tile instanceof IPipeTile) || ((tile instanceof IPowerReceptor) && ((IPowerReceptor)tile).getPowerReceiver(this.orientation) != null))
            {
                this.orientation = i;

                if (!worldObj.isRemote)
                {
                    MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
                    isAddedToENet = false;
                }

                this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                this.worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, this.worldObj.getBlockId(xCoord, yCoord, zCoord));
                return true;
            }
        }

        return false;
    }

    private boolean isOrientationValid()
    {
        TileEntity tile = this.tilesOnSides[this.orientation.ordinal()];
        return (tile instanceof IPowerReceptor) && (((IPowerReceptor)tile).getPowerReceiver(this.orientation) != null);
    }
}
