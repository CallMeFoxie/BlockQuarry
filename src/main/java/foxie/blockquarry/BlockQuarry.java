package foxie.blockquarry;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
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
   }

   @Mod.EventHandler
   public void onServerStarted(FMLServerStartedEvent event) {
   }

}
