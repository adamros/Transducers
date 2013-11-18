package adamros.mods.transducers;

import adamros.mods.transducers.item.ItemPneumaticTransducer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabTransducers extends CreativeTabs
{
    public CreativeTabTransducers()
    {
        super("Transducers");
    }

    @Override
    public ItemStack getIconItemStack()
    {
        return new ItemStack(Transducers.blockPTransducer, 1, 3);
    }
}
