package foxie.blockquarry;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import foxie.blockquarry.proxy.ProxyCommon;

@Mod(modid = BlockQuarry.MODID, name = BlockQuarry.NAME, version = BlockQuarry.VERSION)
public class BlockQuarry {
   public static final String MODID   = "blockquarry";
   public static final String NAME    = "Block Quarry";
   public static final String AUTHOR  = "CallMeFoxie";
   public static final String VERSION = "@VERSION@";

   @SidedProxy(clientSide = "foxie.blockquarry.proxy.ProxyClient", serverSide = "foxie.blockquarry.proxy.ProxyCommon")
   public static ProxyCommon proxy;

   @Mod.Instance(MODID)
   public static BlockQuarry INSTANCE;

   Config config;

   public BlockQuarry() {
      config = new Config();
   }

   @Mod.EventHandler
   public void preinit(FMLPreInitializationEvent event) {
      config.preinit(event.getModConfigurationDirectory().getAbsolutePath());
      proxy.preinit(event);
   }

   @Mod.EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   @Mod.EventHandler
   public void postinit(FMLPostInitializationEvent event) {
      proxy.postinit(event);
      config.postinit();
   }

   @Mod.EventHandler
   public void onServerStarted(FMLServerStartedEvent event) {
   }

   /**
    * API option, registering a preset ore. Should happen in the constructor or preinit TOPS, before anything is
    * registered in the OreDictionary!
    */
   @Mod.EventHandler
   public void onIMCEvent(FMLInterModComms.IMCEvent event) {
      for (FMLInterModComms.IMCMessage message : event.getMessages()) {
         if ("preconfiguredOre".equals(message.key)) {
            if (!message.isStringMessage()) {
               FMLLog.bigWarning("Mod " + message.getSender() + " tried registering a new ore default without sending the proper data :(");
               continue;
            }
            String[] data = message.getStringValue().split(" ");
            if (data.length != 6) {
               FMLLog.bigWarning("Mod " + message.getSender() + " sent an invalid preconfigured ore option!");
               continue;
            }

            try {
               config.registerPreset(data[0], new Config.PreconfiguredOre(Integer.parseInt(data[1]), Integer.parseInt(data[2]),
                       Integer.parseInt(data[3]), Integer.parseInt(data[4]), Float.parseFloat(data[5])));
            } catch (Exception e) {
               FMLLog.bigWarning("Mod " + message.getSender() + " sent an invalid preconfigured ore option, almost made me crash!");
            }
         }
      }
   }

}
