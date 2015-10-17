package foxie.blockquarry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Method;

public class Registrator {
   public static void registerBlock(Block block) {
      GameRegistry.registerBlock(block, BlockQuarry.MODID + "_" + block.getUnlocalizedName().substring(5));
      checkForEvents(block);
   }

   public static void registerItem(Item item) {
      GameRegistry.registerItem(item, BlockQuarry.MODID + "_" + item.getUnlocalizedName().substring(5));
      checkForEvents(item);
   }

   public static void registerMultiBlock(Block block, Class<? extends ItemBlock> myClass) {
      GameRegistry.registerBlock(block, myClass, BlockQuarry.MODID + "_" + block.getUnlocalizedName().substring(5));
   }

   public static void registerTileEntity(Class<? extends TileEntity> te, String unlocalizedname) {
      GameRegistry.registerTileEntity(te, BlockQuarry.MODID + "_" + unlocalizedname);
      checkForEvents(te);
   }

   public static void registerOreDict(Item item, String oreName) {
      OreDictionary.registerOre(oreName, new ItemStack(item));
   }


   public static void checkForEvents(Object o) {
      Class clazz = o.getClass();
      boolean needsSubscribing = false;
      for (Class myClass = clazz; myClass != null && myClass.getCanonicalName().startsWith("foxie.blockquarry."); myClass = myClass.getSuperclass()) {
         // added check for class name to speed it up
         Method[] methods = myClass.getDeclaredMethods();
         for (Method method : methods) {
            if (method.isAnnotationPresent(SubscribeEvent.class)) {
               needsSubscribing = true;
               break;
            }
         }
      }

      if (needsSubscribing) {
         MinecraftForge.EVENT_BUS.register(o);
         FMLCommonHandler.instance().bus().register(o);
      }
   }
}

