package adamros.mods.transducers.item;

import adamros.mods.transducers.Transducers;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemPneumaticTransducer extends ItemBlock
{
    public ItemPneumaticTransducer(int par1)
    {
        super(par1);
        setHasSubtypes(true);
        setMaxDamage(0);
        setUnlocalizedName("itemPneumaticTransducer");
        setCreativeTab(Transducers.tabTransducers);
    }

    @Override
    public int getMetadata(int meta)
    {
        return meta;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        String suffix;

        switch (itemStack.getItemDamage())
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
                suffix = "";
                break;
        }

        return getUnlocalizedName() + "." + suffix;
    }
}
