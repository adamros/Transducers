package adamros.mods.transducers.block;

import ic2.api.energy.tile.IEnergyConductor;

import java.util.List;
import java.util.Random;

import buildcraft.api.transport.IPipeTile;
import adamros.mods.transducers.Transducers;
import adamros.mods.transducers.tileentity.TileElectricEngine;
import adamros.mods.transducers.tileentity.TilePTransducer;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockPneumaticTransducer extends BlockContainer
{
    protected Icon LVpneumatic;
    protected Icon LVpneumaticIn;
    protected Icon LVpneumaticOut;
    protected Icon MVpneumatic;
    protected Icon MVpneumaticIn;
    protected Icon MVpneumaticOut;
    protected Icon HVpneumatic;
    protected Icon HVpneumaticIn;
    protected Icon HVpneumaticOut;
    protected Icon EVpneumatic;
    protected Icon EVpneumaticIn;
    protected Icon EVpneumaticOut;

    public BlockPneumaticTransducer(int i)
    {
        super(i, Material.iron);
        setHardness(0.7F);
        setUnlocalizedName("pneumaticTransducer");
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
        LVpneumatic = iconRegister.registerIcon("transducers:PTrans_LV");
        LVpneumaticIn = iconRegister.registerIcon("transducers:PTrans_LV_In");
        LVpneumaticOut = iconRegister.registerIcon("transducers:PTrans_LV_Out");
        MVpneumatic = iconRegister.registerIcon("transducers:PTrans_MV");
        MVpneumaticIn = iconRegister.registerIcon("transducers:PTrans_MV_In");
        MVpneumaticOut = iconRegister.registerIcon("transducers:PTrans_MV_Out");
        HVpneumatic = iconRegister.registerIcon("transducers:PTrans_HV");
        HVpneumaticIn = iconRegister.registerIcon("transducers:PTrans_HV_In");
        HVpneumaticOut = iconRegister.registerIcon("transducers:PTrans_HV_Out");
        EVpneumatic = iconRegister.registerIcon("transducers:PTrans_EV");
        EVpneumaticIn = iconRegister.registerIcon("transducers:PTrans_EV_In");
        EVpneumaticOut = iconRegister.registerIcon("transducers:PTrans_EV_Out");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getBlockTexture(IBlockAccess blocks, int x, int y, int z, int side)
    {
        TileEntity tileentity = blocks.getBlockTileEntity(x, y, z);

        if (tileentity instanceof TilePTransducer)
        {
            TilePTransducer tile = (TilePTransducer)tileentity;

            if (tile.tier == 0)
            {
                if (tile.sideFlags[side] == 1)
                {
                    return LVpneumaticIn;
                }
                else if (tile.sideFlags[side] == 2)
                {
                    return LVpneumaticOut;
                }
                else
                {
                    return LVpneumatic;
                }
            }
            else if (tile.tier == 1)
            {
                if (tile.sideFlags[side] == 1)
                {
                    return MVpneumaticIn;
                }
                else if (tile.sideFlags[side] == 2)
                {
                    return MVpneumaticOut;
                }
                else
                {
                    return MVpneumatic;
                }
            }
            else if (tile.tier == 2)
            {
                if (tile.sideFlags[side] == 1)
                {
                    return HVpneumaticIn;
                }
                else if (tile.sideFlags[side] == 2)
                {
                    return HVpneumaticOut;
                }
                else
                {
                    return HVpneumatic;
                }
            }
            else if (tile.tier == 3)
            {
                if (tile.sideFlags[side] == 1)
                {
                    return EVpneumaticIn;
                }
                else if (tile.sideFlags[side] == 2)
                {
                    return EVpneumaticOut;
                }
                else
                {
                    return EVpneumatic;
                }
            }
        }

        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int meta)
    {
        switch (meta)
        {
            case 0:
                return LVpneumatic;

            case 1:
                return MVpneumatic;

            case 2:
                return HVpneumatic;

            case 3:
                return EVpneumatic;

            default:
                return null;
        }
    }

    @Override
    public boolean canProvidePower()
    {
        return false;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int direction)
    {
        return true;
    }

    @Override
    public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta)
    {
        return new TilePTransducer(meta);
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return null;
    }

    @Override
    public boolean hasTileEntity(int meta)
    {
        return (meta >= 0 && meta < 4);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int param1, float param2, float param3, float param4)
    {
        //if (entityPlayer.isSneaking()) return false;
        return false;
    }

    @Override
    public int idDropped(int i, Random random, int j)
    {
        return blockID;
    }

    @Override
    public int damageDropped(int meta)
    {
        return meta;
    }
}
