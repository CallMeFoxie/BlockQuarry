package foxie.blockquarry.block;

import foxie.blockquarry.Registrator;

public class BlockReg {

   public static BlockQuarryMachine blockQuarryMachine;

   public static void preinit() {
      blockQuarryMachine = new BlockQuarryMachine();
   }

   public static void init() {
      Registrator.registerBlock(blockQuarryMachine);
   }

   public static void postinit() {
   }
}
