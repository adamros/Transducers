package adamros.mods.transducers.item;

import adamros.mods.transducers.Transducers;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

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
}
