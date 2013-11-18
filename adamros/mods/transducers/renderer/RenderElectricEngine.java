package adamros.mods.transducers.renderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import adamros.mods.transducers.EngineColors;
import adamros.mods.transducers.tileentity.TileElectricEngine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;

@SideOnly(Side.CLIENT)
public class RenderElectricEngine extends TileEntitySpecialRenderer
{
    private static final ResourceLocation CHAMBER_TEXTURE = new ResourceLocation("transducers:textures/blocks/chamber.png");
    private static final ResourceLocation TRUNK_TEXTURE = new ResourceLocation("transducers:textures/blocks/trunk.png");
    private ModelBase model = new ModelBase() { } ;
    private ModelRenderer box;
    private ModelRenderer trunk;
    private ModelRenderer movingBox;
    private ModelRenderer chamber;
    private ResourceLocation baseTexture;
    private static final float[] angleMap = new float[6];

    static
    {
        angleMap[ForgeDirection.EAST.ordinal()] = (float) - Math.PI / 2;
        angleMap[ForgeDirection.WEST.ordinal()] = (float) Math.PI / 2;
        angleMap[ForgeDirection.UP.ordinal()] = 0;
        angleMap[ForgeDirection.DOWN.ordinal()] = (float) Math.PI;
        angleMap[ForgeDirection.SOUTH.ordinal()] = (float) Math.PI / 2;
        angleMap[ForgeDirection.NORTH.ordinal()] = (float) - Math.PI / 2;
    }

    public RenderElectricEngine()
    {
        this.box = new ModelRenderer(this.model, 0, 1);
        this.box.addBox(-8.0F, -8.0F, -8.0F, 16, 4, 16);
        this.box.rotationPointX = 8.0F;
        this.box.rotationPointY = 8.0F;
        this.box.rotationPointZ = 8.0F;
        this.trunk = new ModelRenderer(this.model, 1, 1);
        this.trunk.addBox(-4.0F, -4.0F, -4.0F, 8, 12, 8);
        this.trunk.rotationPointX = 8.0F;
        this.trunk.rotationPointY = 8.0F;
        this.trunk.rotationPointZ = 8.0F;
        this.movingBox = new ModelRenderer(this.model, 0, 1);
        this.movingBox.addBox(-8.0F, -4.0F, -8.0F, 16, 4, 16);
        this.movingBox.rotationPointX = 8.0F;
        this.movingBox.rotationPointY = 8.0F;
        this.movingBox.rotationPointZ = 8.0F;
        this.chamber = new ModelRenderer(this.model, 1, 1);
        this.chamber.addBox(-5.0F, -4.0F, -5.0F, 10, 2, 10);
        this.chamber.rotationPointX = 8.0F;
        this.chamber.rotationPointY = 8.0F;
        this.chamber.rotationPointZ = 8.0F;
    }

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f)
    {
        TileElectricEngine engine = ((TileElectricEngine) tileentity);

        if (engine != null)
        {
            render(engine.heat, engine.progress, engine.orientation, engine.getTexture(), x, y, z);
        }
    }

    private void render(float heat, float progress, ForgeDirection orientation, ResourceLocation baseTexture, double x, double y, double z)
    {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glTranslatef((float) x, (float) y, (float) z);
        float step;

        if (progress > 0.5)
        {
            step = 7.99F - (progress - 0.5F) * 2F * 7.99F;
        }
        else
        {
            step = progress * 2F * 7.99F;
        }

        float translatefact = step / 16;
        float[] angle = { 0, 0, 0 };
        float[] translate = { orientation.offsetX, orientation.offsetY, orientation.offsetZ };

        switch (orientation)
        {
            case EAST:
            case WEST:
            case DOWN:
                angle[2] = angleMap[orientation.ordinal()];
                break;

            case SOUTH:
            case NORTH:
            default:
                angle[0] = angleMap[orientation.ordinal()];
                break;
        }

        box.rotateAngleX = angle[0];
        box.rotateAngleY = angle[1];
        box.rotateAngleZ = angle[2];
        trunk.rotateAngleX = angle[0];
        trunk.rotateAngleY = angle[1];
        trunk.rotateAngleZ = angle[2];
        movingBox.rotateAngleX = angle[0];
        movingBox.rotateAngleY = angle[1];
        movingBox.rotateAngleZ = angle[2];
        chamber.rotateAngleX = angle[0];
        chamber.rotateAngleY = angle[1];
        chamber.rotateAngleZ = angle[2];
        float factor = (float)(1.0 / 16.0);
        bindTexture(baseTexture);
        box.render(factor);
        GL11.glTranslatef(translate[0] * translatefact, translate[1] * translatefact, translate[2] * translatefact);
        movingBox.render(factor);
        GL11.glTranslatef(-translate[0] * translatefact, -translate[1] * translatefact, -translate[2] * translatefact);
        bindTexture(CHAMBER_TEXTURE);
        float chamberf = 2F / 16F;

        for (int i = 0; i <= step + 2; i += 2)
        {
            chamber.render(factor);
            GL11.glTranslatef(translate[0] * chamberf, translate[1] * chamberf, translate[2] * chamberf);
        }

        for (int i = 0; i <= step + 2; i += 2)
        {
            GL11.glTranslatef(-translate[0] * chamberf, -translate[1] * chamberf, -translate[2] * chamberf);
        }

        bindTexture(TRUNK_TEXTURE);
        GL11.glColor3f(((float)EngineColors.getR((int)(heat / 2) - 10) * ((1F / 255F))), ((float)EngineColors.getG((int)(heat / 2) - 10) * (1F / 255F)), ((float)EngineColors.getB((int)(heat / 2) - 10) * (1F / 255F)));
        trunk.render(factor);
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
