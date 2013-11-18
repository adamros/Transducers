package adamros.mods.transducers;

import java.io.File;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class Config
{
    private Configuration config;
    public static int engineId;
    public static int pneumaticGeneratorId;
    public static int highThroughputPowerPipeId;
    public static boolean gregtechSupport;

    public void initConfiguration()
    {
        try
        {
            config = new Configuration(new File(Loader.instance().getConfigDir(), "Transducers.cfg"));
        }
        catch (Exception e)
        {
            FMLLog.getLogger().severe("Configuration cannot be loaded!");
            e.printStackTrace();
            throw new RuntimeException();
        }

        config.save();
    }

    public void loadConfiguration()
    {
        config.load();
        Property blockEngineId = config.get("block", "electricEngine", 765);
        Property blockPneumaticGeneratorId = config.get("block", "pneumaticGenerator", 766);
        Property itemHighThroughputPowerPipe = config.get("item", "highThroughputPowerPipe", 22552);
        Property gregtechRecipes = config.get("recipes", "gregtechRecipes", true);
        engineId = blockEngineId.getInt();
        pneumaticGeneratorId = blockPneumaticGeneratorId.getInt();
        highThroughputPowerPipeId = itemHighThroughputPowerPipe.getInt();
        gregtechSupport = gregtechRecipes.getBoolean(true);
        config.save();
    }
}
