package foxie.blockquarry.block;

public class BlockReg {

   public static BlockQuarryMachine blockQuarryMachine;
   public static BlockQuarryPortal  blockQuarryPortal;

   public static void preinit() {
      blockQuarryMachine = new BlockQuarryMachine();
      blockQuarryPortal = new BlockQuarryPortal();

   }

   public static void init() {
      //Registrator.registerBlock(blockQuarryMachine);
      //Registrator.registerBlock(blockQuarryPortal);

      //Registrator.registerTileEntity(TEQuarryMachine.class, "quarrymachine_te");
      //Registrator.registerTileEntity(TEQuarryPortal.class, "quarryportal_te");
   }

   public static void postinit() {
   }
}
