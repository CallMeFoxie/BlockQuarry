package foxie.blockquarry;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import foxie.blockquarry.te.TEQuarryMachine;
import foxie.lib.Config;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BQConfig {
   public static List<ConfigGenOre> genOres;

   public static Map<Integer, ConfigGenChanceLevel[]> oreGenLevelChances;

   public static Map<Integer, Double> maxOreGenChance;
   public static BQConfig             INSTANCE;

   PreconfiguredOre              defaultPreconfiguredOre;
   Map<String, PreconfiguredOre> preconfiguredOreMap;
   Map<String, String>           preconfiguredAliases;
   Configuration                 configOres;
   boolean haveOresBeenLoaded = false;

   public BQConfig() {
      INSTANCE = this;

      genOres = new ArrayList<ConfigGenOre>();
      preconfiguredOreMap = new HashMap<String, PreconfiguredOre>();
      defaultPreconfiguredOre = new PreconfiguredOre();
      preconfiguredAliases = new HashMap<String, String>();
      oreGenLevelChances = new HashMap<Integer, ConfigGenChanceLevel[]>();
      maxOreGenChance = new HashMap<Integer, Double>();

      // init default values
      setupPresets();

      MinecraftForge.EVENT_BUS.register(this);
      // load vanilla entries because Forge is a derp and registers them before any mod is constructed... gg
      for (String ore : OreDictionary.getOreNames()) {
         if (ore.startsWith("ore"))
            tryRegisteringOre(ore, OreDictionary.getOres(ore).get(0));
      }

      tryRegisteringOre("cobblestone", OreDictionary.getOres("cobblestone").get(0));

      if (!OreDictionary.doesOreNameExist("dirt"))
         OreDictionary.registerOre("dirt", Blocks.dirt);
      if (!OreDictionary.doesOreNameExist("gravel"))
         OreDictionary.registerOre("gravel", Blocks.gravel);
      if (!OreDictionary.doesOreNameExist("gemCoal"))
         OreDictionary.registerOre("gemCoal", Items.coal);

      tryRegisteringOre("dirt", OreDictionary.getOres("dirt").get(0));
      tryRegisteringOre("gravel", OreDictionary.getOres("gravel").get(0));
   }

   private void setupPresets() {
      registerPreset("cobblestone", new PreconfiguredOre(0, 255, 1, 1, 25f, "cobblestone"));
      registerPreset("dirt", new PreconfiguredOre(0, 255, 1, 4, 5f, "dirt"));
      registerPreset("gravel", new PreconfiguredOre(0, 255, 8, 1, 1f, "gravel"));

      registerPreset("oreIron", new PreconfiguredOre(0, 255, 8, 1, 0.5f));
      registerPreset("oreGold", new PreconfiguredOre(0, 30, 8, 1, 0.2f));
      registerPreset("oreCopper", new PreconfiguredOre(30, 100, 12, 1, 0.4f));
      registerPreset("oreTin", new PreconfiguredOre(60, 130, 10, 1, 0.25f));
      registerPreset("oreDiamond", new PreconfiguredOre(0, 20, 8, 1, 0.05f, "gemDiamond"));
      registerPreset("oreEmerald", new PreconfiguredOre(0, 120, 1, 1, 0.01f, "gemEmerald"));
      registerPreset("oreLapis", new PreconfiguredOre(0, 30, 4, 1, 0.1f, "gemLapis"));
      registerPreset("oreRedstone", new PreconfiguredOre(0, 30, 6, 1, 0.2f));
      registerPreset("oreQuartz", new PreconfiguredOre(0, 255, 6, 1, 0, "gemQuartz"));
      registerPreset("oreCoal", new PreconfiguredOre(0, 255, 24, 1, 0.5f, "gemCoal"));
      registerPreset("oreAluminium", new PreconfiguredOre(40, 80, 6, 1, 0.3f));
      registerPreset("oreLead", new PreconfiguredOre(0, 40, 6, 1, 0.2f));
      registerPreset("oreSilver", new PreconfiguredOre(0, 40, 6, 1, 0.2f));
      registerPreset("oreRuby", new PreconfiguredOre(20, 60, 3, 1, 0.1f, "gemRuby"));
      registerPreset("oreSapphire", new PreconfiguredOre(20, 60, 3, 1, 0.1f, "gemSapphire"));
      registerPreset("orePeridot", new PreconfiguredOre(20, 60, 3, 1, 0.1f, "gemPeridot"));
      registerPreset("oreSulfur", new PreconfiguredOre(4, 12, 10, 1, 0.1f));
      registerPreset("oreNickel", new PreconfiguredOre(0, 10, 2, 1, 0.07f));
      registerPreset("oreCertus", new PreconfiguredOre(10, 150, 6, 1, 0.1f));
      registerPreset("oreCertusCharged", new PreconfiguredOre(10, 150, 6, 1, 0.05f));
      registerPreset("oreUranium", new PreconfiguredOre(5, 120, 1, 1, 0.1f));
      registerPreset("oreMonazit", new PreconfiguredOre(5, 120, 6, 1, 0.2f));
      registerPreset("oreIridium", new PreconfiguredOre(0, 10, 1, 1, 0.02f));
      registerPreset("orePlatinum", new PreconfiguredOre(0, 10, 1, 1, 0.05f));
      registerPreset("oreSaltpeter", new PreconfiguredOre(80, 100, 4, 1, 0.2f));
      registerPreset("oreOsmium", new PreconfiguredOre(20, 120, 12, 1, 0.3f));
      registerPreset("oreThorium", new PreconfiguredOre(0, 10, 1, 1, 0.05f));
      registerPreset("apatite", new PreconfiguredOre(80, 160, 30, 1, 0.1f, "gemApatite"));
      // (vis crystal) amber quicksilver

      preconfiguredAliases.put("oreAluminum", "oreAluminium");

   }

   public void registerPreset(String oreName, PreconfiguredOre preset) {
      if (preconfiguredOreMap.containsKey(oreName)) {
         preconfiguredOreMap.remove(oreName); // replace
         FMLLog.info("Replacing preconfigured ore " + oreName);
      }

      preconfiguredOreMap.put(oreName, preset);
   }

   public void preinit(String path) {
      configOres = new Configuration(Config.getConfigFile(path, "ores"));
   }


   public void postinit() { // actually loads the config here after all the oredict registrations have been done
      haveOresBeenLoaded = true;

      for (ConfigGenOre ore : genOres) {
         // iterate through and load their configs from configOres.get(oreName.clusterSize) etc
         ore.initFromConfig(configOres);
      }

      if (TEQuarryMachine.drop_dusts) {
         for (ConfigGenOre ore : genOres) {
            if (!ore.enableDusting)
               continue;

            if (ore.preconfiguredOre.dropDust == null) {
               ArrayList<ItemStack> stacks = OreDictionary.getOres("dust" + ore.oreName.substring(3));
               if (stacks.size() > 0)
                  ore.preconfiguredOre.dropDust = "dust" + ore.oreName.substring(3); // dorp "ore"
            }

            if (ore.preconfiguredOre.dropDust != null && OreDictionary.doesOreNameExist(ore.preconfiguredOre.dropDust)) {
               ore.itemStack = OreDictionary.getOres(ore.preconfiguredOre.dropDust).get(0).copy();
               ore.itemStack.stackSize = 1;
            }
         }
      }

      if (configOres.hasChanged())
         configOres.save();
   }

   private void tryRegisteringOre(String name, ItemStack stack) {
      if (haveOresBeenLoaded) {
         FMLLog.bigWarning("Some mod has just registered " + name + " waaayy too late in postinit! Sorry, this shall be " +
                 "ignored as config has been already loaded! ItemStack name: " + stack.getUnlocalizedName());
         return;
      }

      if (preconfiguredAliases.containsKey(name))
         name = preconfiguredAliases.get(name);

      if (!genOres.contains(name)) {
         ConfigGenOre ore = new ConfigGenOre();
         ore.oreName = name;
         ore.itemStack = stack.copy();
         ore.itemStack.stackSize = 1; // reset

         if (preconfiguredOreMap.containsKey(name))
            ore.setPreconfiguredOre(preconfiguredOreMap.get(name));
         else
            ore.setPreconfiguredOre(defaultPreconfiguredOre);

         genOres.add(ore);
      }
   }

   public ConfigGenChanceLevel[] getOreMapChance(int yLevel) {
      if (!oreGenLevelChances.containsKey(yLevel)) {
         double maxChance = 0f;
         //ConfigGenChanceLevel[] map = new ConfigGenChanceLevel[genOres.size()];
         ArrayList<ConfigGenChanceLevel> map = new ArrayList<ConfigGenChanceLevel>();
         for (ConfigGenOre genOre : genOres) {
            if (genOre.maxY >= yLevel && genOre.minY <= yLevel) {
               // chance map
               map.add(new ConfigGenChanceLevel(genOre, maxChance)); // nextDouble() ceil() into the next value and you've got your ore
               maxChance += genOre.chance;
            }
         }

         maxOreGenChance.put(yLevel, maxChance);
         oreGenLevelChances.put(yLevel, map.toArray(new ConfigGenChanceLevel[map.size()]));
      }

      return oreGenLevelChances.get(yLevel);
   }

   public Double getOreMaxChance(int yLevel) {
      if (!maxOreGenChance.containsKey(yLevel))
         getOreMapChance(yLevel); // will generate it for us ;o

      return maxOreGenChance.get(yLevel);
   }

   @SubscribeEvent
   public void onOreRegistered(OreDictionary.OreRegisterEvent event) {
      if (event.Name.startsWith("ore"))
         tryRegisteringOre(event.Name, event.Ore);
   }

   public static class ConfigGenChanceLevel {
      public ConfigGenOre ore;
      public double       totalOrderChance;

      public ConfigGenChanceLevel(ConfigGenOre ore, double totalOrderChance) {
         this.ore = ore;
         this.totalOrderChance = totalOrderChance;
      }
   }

   public static class ConfigGenOre {
      public String    oreName;
      public ItemStack itemStack;
      public int       minY;
      public int       maxY;
      public int       clusterSize; // simply generates clusterSize of blocks at once
      public int       stackSizeMax;
      public float     chance; // fat chance he he he
      public boolean enableDusting = true;
      private PreconfiguredOre preconfiguredOre;

      public static ConfigGenOre findClosestOre(ConfigGenChanceLevel[] chances, double chance) {
         for (int i = 0; i < chances.length - 1; i++) { // linear search, boring, I know... maybe update later?
            if (chance >= chances[i].totalOrderChance && chances[i + 1].totalOrderChance > chance) {
               return chances[i].ore;
            }
         }

         return chances[chances.length - 1].ore;
      }

      public void setPreconfiguredOre(PreconfiguredOre ore) {
         this.preconfiguredOre = ore;
      }

      public void initFromConfig(Configuration configuration) {
         minY = configuration.getInt("minY", "gen." + oreName, preconfiguredOre.minY, 0, 255, "Min Y of ore" + oreName);
         maxY = configuration.getInt("maxY", "gen." + oreName, preconfiguredOre.maxY, 0, 255, "Max Y of ore" + oreName);
         clusterSize = configuration.getInt("clusterSize", "gen." + oreName, preconfiguredOre.clusterSize, 1, 255, "Cluster size of " + oreName);
         stackSizeMax = configuration.getInt("stackSizeMax", "gen." + oreName, preconfiguredOre.stackSizeMax, 1, 64, "Max stack size of dropped itemstack per block");
         chance = configuration.getFloat("chance", "gen." + oreName, preconfiguredOre.chance, 0, 1000f, "Chance of " + oreName + " to spawn");
         if (preconfiguredOre.dropDust != null)
            enableDusting = configuration.getBoolean("enableDusting", "gen." + oreName, enableDusting, "Enable dropping dusts (may be overriden by global config)");
      }

      @Override
      public boolean equals(Object o) {
         if (o instanceof String)
            return oreName.equals(o);

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
      public String dropDust = null;

      public PreconfiguredOre() {
      }

      public PreconfiguredOre(int minY, int maxY, int clusterSize, int stackSizeMax, float chance, String dustingPreposition) {
         this(minY, maxY, clusterSize, stackSizeMax, chance);
         this.dropDust = dustingPreposition;
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
