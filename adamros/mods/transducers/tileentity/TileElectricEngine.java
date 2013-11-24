package adamros.mods.transducers.tileentity;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class TileElectricEngine extends AbstractElectricEngine /*implements IInventory*/
{
    public short tier = 0;

    private ResourceLocation texture;

    public TileElectricEngine()
    {
        this.powerHandler.configure(minEnergyReceived(), maxEnergyReceived(), 1, 0);
        this.powerHandler.configurePowerPerdition(1, 100);
    }

    public TileElectricEngine(short tier)
    {
        this.tier = tier;
        this.powerHandler.configure(minEnergyReceived(), maxEnergyReceived(), 1, 0);
        this.powerHandler.configurePowerPerdition(1, 100);

        switch (tier)
        {
            case 0:
                texture = new ResourceLocation("transducers:textures/blocks/EEngine_LV.png");
                break;

            case 1:
                texture = new ResourceLocation("transducers:textures/blocks/EEngine_MV.png");
                break;

            case 2:
                texture = new ResourceLocation("transducers:textures/blocks/EEngine_HV.png");
                break;

            case 3:
                texture = new ResourceLocation("transducers:textures/blocks/EEngine_EV.png");
                break;

            default:
                texture = new ResourceLocation("transducers:textures/blocks/EEngine_LV.png");
                break;
        }
    }

    public ResourceLocation getTexture()
    {
        return texture;
    }

    @Override
    public float minEnergyReceived()
    {
        return 0;
    }

    @Override
    public float maxEnergyReceived()
    {
        return 1000;
    }

    @Override
    public float getMaxEnergy()
    {
        float tmp;

        switch (tier)
        {
            case 0:
                tmp = 200;
                break;

            case 1:
                tmp = 800;
                break;

            case 2:
                tmp = 3200;
                break;

            case 3:
                tmp = 6400;
                break;

            default:
                tmp = 200;
                break;
        }

        return tmp;
    }

    @Override
    public float explosionRange()
    {
        float tmp;

        switch (tier)
        {
            case 0:
                tmp = 2;
                break;

            case 1:
                tmp = 4;
                break;

            case 2:
                tmp = 8;
                break;

            case 3:
                tmp = 16;
                break;

            default:
                tmp = 2;
                break;
        }

        return tmp;
    }

    @Override
    public float getCurrentOutput()
    {
        float tmp;

        switch (tier)
        {
            case 0:
                tmp = 12.8F;
                break;

            case 1:
                tmp = 51.2F;
                break;

            case 2:
                tmp = 204.8F;
                break;

            case 3:
                tmp = 819.2F;
                break;

            default:
                tmp = 12.8F;
                break;
        }

        return tmp;
    }

    @Override
    public int getMaxSafeInput()
    {
        int tmp;

        switch (this.tier)
        {
            case 0:
                tmp = 32;
                break;

            case 1:
                tmp = 128;
                break;

            case 2:
                tmp = 512;
                break;

            case 3:
                tmp = 2048; // I have no clue what this should be...
                break;

            default:
                tmp = 32;
                break;
        }

        return tmp;
    }

    @Override
    public double getMaxIC2EnergyStorage()
    {
        double tmp;

        switch (tier)
        {
            case 0:
                tmp = 500;
                break;

            case 1:
                tmp = 2000;
                break;

            case 2:
                tmp = 8000;
                break;

            case 3:
                tmp = 16000;
                break;

            default:
                tmp = 500;
                break;
        }

        return tmp;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        tier = nbt.getShort("tier");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setShort("tier", tier);
    }

    /*@Override
    public int getSizeInventory() {
    	// TODO Auto-generated method stub
    	return 0;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
    	// TODO Auto-generated method stub
    	return null;
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
    	// TODO Auto-generated method stub
    	return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
    	// TODO Auto-generated method stub
    	return null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
    	// TODO Auto-generated method stub

    }

    @Override
    public String getInvName() {
    	// TODO Auto-generated method stub
    	return null;
    }

    @Override
    public boolean isInvNameLocalized() {
    	// TODO Auto-generated method stub
    	return false;
    }

    @Override
    public int getInventoryStackLimit() {
    	// TODO Auto-generated method stub
    	return 0;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
    	// TODO Auto-generated method stub
    	return false;
    }

    @Override
    public void openChest() { }

    @Override
    public void closeChest() { }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
    	// TODO Auto-generated method stub
    	return false;
    }*/

}
