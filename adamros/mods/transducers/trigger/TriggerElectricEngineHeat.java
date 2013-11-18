package adamros.mods.transducers.trigger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import adamros.mods.transducers.Transducers;
import adamros.mods.transducers.tileentity.TileElectricEngine;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerParameter;
import buildcraft.api.gates.TriggerParameter;

public class TriggerElectricEngineHeat implements ITrigger
{
    private float heat = 0.0F;

    @SideOnly(Side.CLIENT)
    private Icon iconBlue, iconGreen, iconYellow, iconRed;

    public TriggerElectricEngineHeat(float engineHeat)
    {
        this.heat = engineHeat;
        ActionManager.registerTrigger(this);
    }

    @Override
    public int getLegacyId()
    {
        return 98;
    }

    @Override
    public String getUniqueTag()
    {
        return "trigger.electricengine.heat";
    }

    @Override
    public Icon getIcon()
    {
        if (heat <= 0.2F)
        {
            return iconBlue;
        }
        else if (heat > 0.2F && heat <= 0.6F)
        {
            return iconGreen;
        }
        else if (heat > 0.6F && heat <= 0.9F)
        {
            return iconYellow;
        }
        else if (heat > 0.9)
        {
            return iconRed;
        }
        else
        {
            return iconBlue;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister)
    {
        iconBlue = iconRegister.registerIcon("buildcraft:triggers/trigger_engineheat_blue");
        iconGreen = iconRegister.registerIcon("buildcraft:triggers/trigger_engineheat_green");
        iconYellow = iconRegister.registerIcon("buildcraft:triggers/trigger_engineheat_yellow");
        iconRed = iconRegister.registerIcon("buildcraft:triggers/trigger_engineheat_red");
    }

    @Override
    public boolean hasParameter()
    {
        return false;
    }

    @Override
    public String getDescription()
    {
        if (heat <= 0.2F)
        {
            return "Low temperature";
        }
        else if (heat > 0.2F && heat <= 0.6F)
        {
            return "Medium temperature";
        }
        else if (heat > 0.6F && heat <= 0.9F)
        {
            return "High temperature";
        }
        else if (heat > 0.9)
        {
            return "Very high temperature";
        }
        else
        {
            return "Unknown";
        }
    }

    @Override
    public boolean isTriggerActive(ForgeDirection side, TileEntity tile, ITriggerParameter parameter)
    {
        if (tile instanceof TileElectricEngine)
        {
            TileElectricEngine engine = (TileElectricEngine)tile;

            if (heat <= 0.2F && engine.getHeat() <= heat)
            {
                return true;
            }
            else if (heat > 0.2F && heat <= 0.6F && engine.getHeat() <= heat)
            {
                return true;
            }
            else if (heat > 0.6F && heat <= 0.9F && engine.getHeat() <= heat)
            {
                return true;
            }
            else if (heat > 0.9 && engine.getHeat() > heat)
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public ITriggerParameter createParameter()
    {
        return new TriggerParameter();
    }

	@Override
	public boolean requiresParameter() {
		// TODO Auto-generated method stub
		return false;
	}
}
