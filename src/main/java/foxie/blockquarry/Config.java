package foxie.blockquarry;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
   public static float quarrySizeMiddle = 0.5f;
   public static float quarrySizeBig    = 0.5f;

   public static int quarryMinY = 80;
   public static int quarryMaxY = 150;

   public static List<ConfigGenOre> genOres;

   PreconfiguredOre              defaultPreconfiguredOre;
   Map<String, PreconfiguredOre> preconfiguredOreMap;

   Configuration configBase;
   Configuration configOres;

   boolean haveOresBeenLoaded = false;

   public Config() {
      genOres = new ArrayList<ConfigGenOre>();
      preconfiguredOreMap = new HashMap<String, PreconfiguredOre>();
      defaultPreconfiguredOre = new PreconfiguredOre();
      // init default values
      setupPresets();

      MinecraftForge.EVENT_BUS.register(this);
      // load vanilla entries because Forge is a derp and registers them before any mod is constructed... gg
      for (String ore : OreDictionary.getOreNames()) {
         tryRegisteringOre(ore, OreDictionary.getOres(ore).get(0));
      }
   }

   private void setupPresets() {
      registerPreset("oreIron", new PreconfiguredOre(1, 255, 8, 1, 0.5f));
      registerPreset("oreGold", new PreconfiguredOre(1, 30, 8, 1, 0.2f));
      registerPreset("oreCopper", new PreconfiguredOre(30, 100, 12, 1, 0.4f));
      registerPreset("oreTin", new PreconfiguredOre(60, 130, 10, 1, 0.25f));
      registerPreset("oreDiamond", new PreconfiguredOre(1, 20, 8, 1, 0.05f));
      registerPreset("oreEmerald", new PreconfiguredOre(1, 120, 1, 1, 0.01f));
      registerPreset("oreLapis", new PreconfiguredOre(1, 30, 4, 1, 0.1f));
      registerPreset("oreRedstone", new PreconfiguredOre(1, 30, 6, 1, 0.2f));
      registerPreset("oreQuartz", new PreconfiguredOre(1, 255, 6, 1, 0));
      registerPreset("oreCoal", new PreconfiguredOre(1, 255, 24, 1, 0.5f));
      registerPreset("oreAluminium", new PreconfiguredOre(40, 80, 6, 1, 0.3f));
      registerPreset("oreLead", new PreconfiguredOre(1, 40, 6, 1, 0.2f));
      registerPreset("oreSilver", new PreconfiguredOre(1, 40, 6, 1, 0.2f));
      registerPreset("oreRuby", new PreconfiguredOre(20, 60, 3, 1, 0.1f));
      registerPreset("oreSapphire", new PreconfiguredOre(20, 60, 3, 1, 0.1f));
      registerPreset("orePeridot", new PreconfiguredOre(20, 60, 3, 1, 0.1f));
      registerPreset("oreSulfur", new PreconfiguredOre(4, 12, 10, 1, 0.1f));
      registerPreset("oreNickel", new PreconfiguredOre(1, 10, 2, 1, 0.07f));
      registerPreset("oreCertus", new PreconfiguredOre(10, 150, 6, 1, 0.1f));
      registerPreset("oreCertusCharged", new PreconfiguredOre(10, 150, 6, 1, 0.05f));
      registerPreset("oreUranium", new PreconfiguredOre(5, 120, 1, 1, 0.1f));
      registerPreset("oreMonazit", new PreconfiguredOre(5, 120, 6, 1, 0.2f));
      registerPreset("oreIridium", new PreconfiguredOre(1, 10, 1, 1, 0.02f));
      registerPreset("orePlatinum", new PreconfiguredOre(1, 10, 1, 1, 0.05f));
      registerPreset("oreSaltpeter", new PreconfiguredOre(80, 100, 4, 1, 0.2f));
      registerPreset("oreOsmium", new PreconfiguredOre(20, 120, 12, 1, 0.3f));
      registerPreset("oreThorium", new PreconfiguredOre(1, 10, 1, 1, 0.1f));
      // (vis crystal) amber

   }

   public void registerPreset(String oreName, PreconfiguredOre preset) {
      if (preconfiguredOreMap.containsKey(oreName)) {
         preconfiguredOreMap.remove(oreName); // replace
         FMLLog.info("Replacing preconfigured ore " + oreName);
      }

      preconfiguredOreMap.put(oreName, preset);
   }

   public void preinit(String path) {
      // TODO base config loader
      configBase = new Configuration(getConfigFile(path, "base"));
      configOres = new Configuration(getConfigFile(path, "ores"));
   }

   private File getConfigFile(String path, String name) {
      if (!(new File(path).exists()))
         (new File(path)).mkdir();

      return new File(path + File.separator + BlockQuarry.MODID + File.separator + name + ".cfg");
   }

   public void postinit() { // actually loads the config here after all the oredict registrations have been done
      haveOresBeenLoaded = true;

      for (ConfigGenOre ore : genOres) {
         // iterate through and load their configs from configOres.get(oreName.clusterSize) etc
         ore.initFromConfig(configOres);
      }

      if (configOres.hasChanged())
         configOres.save();
      if (configBase.hasChanged())
         configBase.save();
   }

   private void tryRegisteringOre(String name, ItemStack stack) {
      if (!name.substring(0, 3).equals("ore"))
         return;

      if (haveOresBeenLoaded) {
         FMLLog.bigWarning("Some mod has just registered " + name + " waaayy too late in postinit! Sorry, this shall be " +
                 "ignored as config has been already loaded! ItemStack name: " + stack.getUnlocalizedName());
         return;
      }

      if (name.equals("oreAluminum"))
         name = "oreAluminium"; // USA pls

      if (!genOres.contains(name)) {
         ConfigGenOre ore = new ConfigGenOre();
         ore.oreName = name;
         ore.itemStack = stack.copy();
         ore.itemStack.stackSize = 1; // reset

         if (preconfiguredOreMap.containsKey(name))
            ore.preconfiguredOre = preconfiguredOreMap.get(name);
         else
            ore.preconfiguredOre = defaultPreconfiguredOre;

         genOres.add(ore);
      }
   }

   @SubscribeEvent
   public void onOreRegistered(OreDictionary.OreRegisterEvent event) {
      tryRegisteringOre(event.Name, event.Ore);
   }

   public static class ConfigGenOre {
      public  String           oreName;
      public  ItemStack        itemStack;
      public  int              minY;
      public  int              maxY;
      public  int              clusterSize; // simply generates clusterSize of blocks at once
      public  int              stackSizeMax;
      public  float            chance; // fat chance he he he
      private PreconfiguredOre preconfiguredOre;

      public void initFromConfig(Configuration configuration) {
         minY = configuration.getInt("minY", "ore." + getCutName(), preconfiguredOre.minY, 1, 255, "Min Y of ore" + oreName);
         maxY = configuration.getInt("maxY", "ore." + getCutName(), preconfiguredOre.maxY, 1, 255, "Max Y of ore" + oreName);
         clusterSize = configuration.getInt("clusterSize", "ore." + getCutName(), preconfiguredOre.clusterSize, 1, 255, "Cluster size of " + oreName);
         stackSizeMax = configuration.getInt("stackSizeMax", "ore." + getCutName(), preconfiguredOre.stackSizeMax, 1, 64, "Max stack size of dropped itemstack per block");
         chance = configuration.getFloat("chance", "ore." + getCutName(), preconfiguredOre.chance, 0, 1f, "Chance of " + oreName + " to spawn");
      }

      public String getCutName() {
         return oreName.substring(3).toLowerCase();
      }


      @Override
      public boolean equals(Object o) {
         if (o instanceof String)
            return oreName.equals((String) o);

         if (!(o instanceof ConfigGenOre))
            return super.equals(o);

         return oreName.equals(((ConfigGenOre) o).oreName);
      }

      @Override
      public int hashCode() {
         return oreName.hashCode();
      }
   }

   public static class PreconfiguredOre {
      public int   minY;
      public int   maxY;
      public int   clusterSize;
      public int   stackSizeMax;
      public float chance;

      public PreconfiguredOre() {
      }

      public PreconfiguredOre(int minY, int maxY, int clusterSize, int stackSizeMax, float chance) {
         this.minY = minY;
         this.maxY = maxY;
         this.clusterSize = clusterSize;
         this.stackSizeMax = stackSizeMax;
         this.chance = chance;
      }
   }
}
