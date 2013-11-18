package adamros.mods.transducers.renderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

@SideOnly(Side.CLIENT)
public class ItemEEngineRenderer implements IItemRenderer
{
    private ModelBase model = new ModelBase() { } ;
    private ModelRenderer box;
    private ModelRenderer trunk;
    private ModelRenderer movingBox;
    private ModelRenderer chamber;

    private static final ResourceLocation CHAMBER_TEXTURE = new ResourceLocation("transducers:textures/blocks/chamber.png");
    private static final ResourceLocation TRUNK_TEXTURE = new ResourceLocation("transducers:textures/blocks/trunk.png");

    public ItemEEngineRenderer()
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
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        switch (type)
        {
            case ENTITY:
                renderEngine(-0.5F, 0F, -0.5F, 1.2F, item.getItemDamage());
                return;

            case EQUIPPED:
            case EQUIPPED_FIRST_PERSON:
            case FIRST_PERSON_MAP:
            case INVENTORY:
                renderEngine(0F, 0F, 0F, 1F, item.getItemDamage());
                return;

            default:
                return;
        }
    }

    private void renderEngine(float x, float y, float z, float scale, int subtype)
    {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_CULL_FACE);
        //GL11.glColor3f(1F, 1F, 1F);
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glScalef(scale, scale, scale);
        float factor = (float)(1.0 / 16.0);
        ResourceLocation texture;

        switch (subtype)
        {
            case 0:
                texture = new ResourceLocation("transducers:textures/blocks/EEngine_LV.png");
                break;

            case 1:
                texture = new ResourceLocation("transducers:textures/blocks/EEngine_MV.png");
                break;

            case 2:
                texture = new ResourceLocation("transducers:textures/blocks/EEngine_HV.png");
                break;

            case 3:
                texture = new ResourceLocation("transducers:textures/blocks/EEngine_EV.png");
                break;

            default:
                texture = new ResourceLocation("transducers:textures/blocks/EEngine_LV.png");
                break;
        }

        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        box.render(factor);
        movingBox.render(factor);
        Minecraft.getMinecraft().renderEngine.bindTexture(CHAMBER_TEXTURE);
        chamber.render(factor);
        Minecraft.getMinecraft().renderEngine.bindTexture(TRUNK_TEXTURE);
        GL11.glColor3f(0.1F, 0.1F, 0.1F);
        trunk.render(factor);
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}
