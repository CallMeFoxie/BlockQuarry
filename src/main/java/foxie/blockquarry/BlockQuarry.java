package foxie.blockquarry;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import foxie.blockquarry.block.BlockReg;
import foxie.blockquarry.item.ItemReg;
import foxie.blockquarry.proxy.ProxyCommon;
import foxie.lib.Config;
import foxie.lib.IFoxieMod;
import foxie.lib.Registrator;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

@Mod(modid = BlockQuarry.MODID, name = BlockQuarry.NAME, version = BlockQuarry.VERSION)
public class BlockQuarry implements IFoxieMod {
   public static final String MODID   = "blockquarry";
   public static final String NAME    = "Block Quarry";
   public static final String VERSION = "@VERSION@";

   @SidedProxy(clientSide = "foxie.blockquarry.proxy.ProxyClient", serverSide = "foxie.blockquarry.proxy.ProxyCommon")
   public static ProxyCommon proxy;

   @Mod.Instance(MODID)
   public static BlockQuarry INSTANCE;

   public static CreativeTabs creativeTabBlockQuarry;

   BQConfig BQConfig;
   Config   config;

   public BlockQuarry() {
      BQConfig = new BQConfig();
      creativeTabBlockQuarry = new CreativeTabs("blockQuarry") {
         @Override
         @SideOnly(Side.CLIENT)
         public Item getTabIconItem() {
            return Items.apple;
         }
      };
   }

   @Mod.EventHandler
   public void preinit(FMLPreInitializationEvent event) {
      BQConfig.preinit(event.getModConfigurationDirectory().getAbsolutePath());
      config = new Config(Config.getConfigFile(event.getModConfigurationDirectory().getAbsolutePath(), "base"));
      proxy.preinit(event);
      BlockReg.preinit();
      ItemReg.preinit();
   }

   @Mod.EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.init(event);
      BlockReg.init();
      ItemReg.init();
      Registrator.registerOreGen(new BQWorldGen(), 1);
   }

   @Mod.EventHandler
   public void postinit(FMLPostInitializationEvent event) {
      proxy.postinit(event);
      BQConfig.postinit();
      BlockReg.postinit();
      ItemReg.postinit();
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
               BQConfig.registerPreset(data[0], new BQConfig.PreconfiguredOre(Integer.parseInt(data[1]), Integer.parseInt(data[2]),
                       Integer.parseInt(data[3]), Integer.parseInt(data[4]), Float.parseFloat(data[5])));
            } catch (Exception e) {
               FMLLog.bigWarning("Mod " + message.getSender() + " sent an invalid preconfigured ore option, almost made me crash!");
            }
         }
      }
   }

   @Override
   public Config getConfig() {
      return this.config;
   }

   @Override
   public String getModId() {
      return MODID;
   }
}
