package adamros.mods.transducers.block;

import java.util.List;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import adamros.mods.transducers.Transducers;
import adamros.mods.transducers.tileentity.TileElectricEngine;
import adamros.mods.transducers.tileentity.TilePTransducer;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockElectricEngine extends BlockContainer
{
    protected Icon lvEngine;
    protected Icon mvEngine;
    protected Icon hvEngine;
    protected Icon evEngine;

    public BlockElectricEngine(int par1)
    {
        super(par1, Material.iron);
        setHardness(0.7F);
        setUnlocalizedName("electricEngine");
        setCreativeTab(Transducers.tabTransducers);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(int blockId, CreativeTabs creativeTabs, List list)
    {
        list.add(new ItemStack(blockId, 1, 0));
        list.add(new ItemStack(blockId, 1, 1));
        list.add(new ItemStack(blockId, 1, 2));
        list.add(new ItemStack(blockId, 1, 3));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister)
    {
        lvEngine = iconRegister.registerIcon("transducers:PTrans_LV_In");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getBlockTexture(IBlockAccess blocks, int x, int y, int z, int side)
    {
        TileEntity tileentity = blocks.getBlockTileEntity(x, y, z);

        if (tileentity instanceof TileElectricEngine)
        {
            TileElectricEngine tile = (TileElectricEngine)tileentity;

            if (tile.orientation.getOpposite().ordinal() == side)
            {
                return lvEngine;
            }
        }

        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int meta)
    {
        return null;
    }

    @Override
    public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side)
    {
        TileEntity tileentity = world.getBlockTileEntity(x, y, z);

        if (tileentity instanceof TileElectricEngine)
        {
            TileElectricEngine tile = (TileElectricEngine)tileentity;

            if (tile.orientation.getOpposite().ordinal() == side.ordinal())
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int direction)
    {
        return true;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return -1;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta)
    {
        return new TileElectricEngine((short) meta);
    }

    @Override
    public boolean hasTileEntity(int meta)
    {
        return (meta >= 0 && meta < 4);
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return null;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int param1, float param2, float param3, float param4)
    {
        TileEntity te = world.getBlockTileEntity(x, y, z);

        if (te instanceof TileElectricEngine)
        {
            TileElectricEngine tile = (TileElectricEngine)te;

            if (entityPlayer.getCurrentEquippedItem() != null)
            {
                if (entityPlayer.getCurrentEquippedItem().getItem() instanceof IToolWrench)
                {
                    tile.changeOrientation(true);
                    return true;
                }
            }

            return false;
        }

        return false;
    }

    @Override
    public int damageDropped(int meta)
    {
        return meta;
    }

    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis)
    {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        return (tile instanceof TileElectricEngine) ? ((TileElectricEngine)tile).changeOrientation(false) : false;
    }
}
