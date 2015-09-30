package foxie.blockquarry;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config {
   public static float quarrySizeMiddle = 0.5f;
   public static float quarrySizeBig    = 0.5f;

   public static int quarryMinY = 80;
   public static int quarryMaxY = 150;

   public static List<ConfigGenOre> genOres = new ArrayList<ConfigGenOre>();

   Configuration configBase;
   Configuration configOres;

   boolean haveOresBeenLoaded = false;

   public Config() {
      MinecraftForge.EVENT_BUS.register(this);
      // let's hope nobody registered BEFORE this
   }

   public void preinit(String path) {
      // TODO config loader
      configBase = new Configuration(getConfigFile(path, "base"));
      configOres = new Configuration(getConfigFile(path, "ores"));
   }

   private File getConfigFile(String path, String name) {
      if (!(new File(path).exists()))
         (new File(path)).mkdir();

      return new File(path + File.pathSeparator + name + ".cfg");
   }

   public void postinit() { // actually loads the config here after all the oredict registrations have been done
      haveOresBeenLoaded = true;

      for (ConfigGenOre ore : genOres) {
         // iterate through and load their configs from configOres.get(oreName.clusterSize) etc
      }
   }

   private void tryRegisteringOre(String name, ItemStack stack) {
      if (!name.substring(0, 3).equals("ore"))
         return;

      if (haveOresBeenLoaded) {
         FMLLog.bigWarning("Some mod has just registered " + name + " waaayy too late in postinit! Sorry, this shall be ignored as config has been already loaded!");
      }

      if (!genOres.contains(name)) {
         ConfigGenOre ore = new ConfigGenOre();
         ore.oreName = name;
         ore.itemStack = stack;

         genOres.add(ore);
      }
   }

   @SubscribeEvent
   public void onOreRegistered(OreDictionary.OreRegisterEvent event) {
      tryRegisteringOre(event.Name, event.Ore);
   }

   public static class ConfigGenOre {
      public String    oreName;
      public ItemStack itemStack;
      public int       minY;
      public int       maxY;
      public int       clusterSize; // simply generates clusterSize of blocks at once
      public float     chance; // fat chance he he he


      @Override
      public boolean equals(Object o) {
         if (o instanceof String)
            return oreName.equals((String) o);

         if (!(o instanceof ConfigGenOre))
            return super.equals(o);

         return oreName.equals(((ConfigGenOre) o).oreName);
      }
   }
}
