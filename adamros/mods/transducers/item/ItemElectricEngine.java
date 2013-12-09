package adamros.mods.transducers.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import adamros.mods.transducers.Transducers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class ItemElectricEngine extends ItemBlock
{
    public ItemElectricEngine(int par1)
    {
        super(par1);
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("itemElectricEngine");
        setCreativeTab(Transducers.tabTransducers);
    }

    @Override
    public int getMetadata(int meta)
    {
        return meta;
    }

    @Override
    public String getUnlocalizedName(ItemStack is)
    {
        String suffix;

        switch (is.getItemDamage())
        {
            case 0:
                suffix = "lv";
                break;

            case 1:
                suffix = "mv";
                break;

            case 2:
                suffix = "hv";
                break;

            case 3:
                suffix = "ev";
                break;

            default:
                suffix = "lv";
                break;
        }

        return getUnlocalizedName() + "." + suffix;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
        String type;

        switch (par1ItemStack.getItemDamage())
        {
            case 0:
                type = "lv";
                break;

            case 1:
                type = "mv";
                break;

            case 2:
                type = "hv";
                break;

            case 3:
                type = "ev";
                break;

            default:
                type = "lv";
                break;
        }

        par3List.add(StatCollector.translateToLocal("tip.electricEngine." + type).trim());
    }
}
