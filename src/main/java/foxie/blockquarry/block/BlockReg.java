package foxie.blockquarry.block;

import foxie.blockquarry.Registrator;

public class BlockReg {

   public static BlockQuarrier blockQuarrier;

   public static void preinit() {
      blockQuarrier = new BlockQuarrier();
   }

   public static void init() {
      Registrator.registerBlock(blockQuarrier);
   }

   public static void postinit() {
   }
}
