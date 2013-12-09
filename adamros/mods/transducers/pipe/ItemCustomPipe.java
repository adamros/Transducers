package adamros.mods.transducers.pipe;

import java.util.List;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import buildcraft.transport.ItemPipe;

public class ItemCustomPipe extends ItemPipe
{
    public ItemCustomPipe(int i)
    {
        super(i);
    }

    @Override
    public String getItemDisplayName(ItemStack itemstack)
    {
        return StatCollector.translateToLocal(getUnlocalizedName(itemstack));
    }

    @Override
    public Item setUnlocalizedName(String name)
    {
        return super.setUnlocalizedName("customPipe." + name + ".name");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
        String[] split = par1ItemStack.getUnlocalizedName().split("item.");

        for (int i = 0; i < split.length; i++)
            if (i == 1)
            {
                split = split[1].split(".name");
            }

        par3List.add(StatCollector.translateToLocal("tip." + split[0]).trim());
    }
}
