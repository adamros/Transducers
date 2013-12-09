package adamros.mods.transducers.trigger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
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
    private String uniqueTag;
    private int legacyId;

    @SideOnly(Side.CLIENT)
    private Icon iconBlue, iconGreen, iconYellow, iconRed;

    public TriggerElectricEngineHeat(int id, float engineHeat, String tag)
    {
        this.heat = engineHeat;
        this.uniqueTag = tag;
        this.legacyId = id;
        ActionManager.registerTrigger(this);
    }

    @Override
    public int getLegacyId()
    {
        return legacyId;
    }

    @Override
    public String getUniqueTag()
    {
        return uniqueTag;
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
        String name;

        if (heat <= 0.2F)
        {
            name = "trigger.transducers.electricengine.heat.cold";
        }
        else if (heat > 0.2F && heat <= 0.6F)
        {
            name = "trigger.transducers.electricengine.heat.warm";
        }
        else if (heat > 0.6F && heat <= 0.9F)
        {
            name = "trigger.transducers.electricengine.heat.hot";
        }
        else if (heat > 0.9)
        {
            name = "trigger.transducers.electricengine.heat.overheat";
        }
        else
        {
            name = "trigger.transducers.electricengine.heat.cold";
        }

        return StatCollector.translateToLocal(name).trim();
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
    public boolean requiresParameter()
    {
        // TODO Auto-generated method stub
        return false;
    }
}
