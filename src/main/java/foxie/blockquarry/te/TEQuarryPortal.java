package foxie.blockquarry.te;

import foxie.blockquarry.BlockPos;
import foxie.blockquarry.Config;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.util.Random;

public class TEQuarryPortal extends TileEntity {

   private ItemStack[][][] blocks;
   // this will be a bit different to normal XYZ.
   // to simplify memory requirements I am using
   // Block[Y][X][Z]
   private int             topMinedOutY;
   private QuarrySize      quarrySize;

   @Override
   public boolean canUpdate() {
      return false;
   }

   private boolean isYGenerated(int yLevel) {
      return blocks[yLevel] != null;

   }

   private void generateY(int yLevel) {
      // TODO
   }

   private ItemStack getBlock(BlockPos pos) {
      if (!isYGenerated(pos.getY()))
         generateY(pos.getY());

      return blocks[pos.getY()][pos.getX()][pos.getZ()];
   }

   public ItemStack getDugBlock(BlockPos dugBlockPos) {
      ItemStack foundBlock = getBlock(dugBlockPos);
      if (foundBlock == null)
         return null;

      setBlockMined(dugBlockPos);
      return getBlock(dugBlockPos).copy();
   }

   private void setBlockMined(BlockPos dugBlockPos) {
      blocks[dugBlockPos.getY()][dugBlockPos.getX()][dugBlockPos.getZ()] = null;
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
   }

   public static class QuarrySize {
      public int xSize;
      public int ySize;
      public int zSize;

      public static QuarrySize generateRandom(Random random) {
         QuarrySize size = new QuarrySize();
         size.ySize = random.nextInt(Config.quarryMaxY - Config.quarryMinY) + Config.quarryMinY;

         if (random.nextFloat() <= Config.quarrySizeBig) {
            size.xSize = size.zSize = 64;
         } else if (random.nextFloat() <= Config.quarrySizeMiddle) {
            size.xSize = size.zSize = 32;
         } else {
            size.xSize = size.zSize = 16;
         }

         return size;
      }

      public void writeToNBT(NBTTagCompound compound) {
         NBTTagCompound quarrySize = new NBTTagCompound();
         quarrySize.setInteger("xSize", xSize);
         quarrySize.setInteger("ySize", ySize);
         quarrySize.setInteger("zSize", zSize);

         compound.setTag("quarrySize", quarrySize);
      }

      public void readFromNBT(NBTTagCompound compound) {
         if (!compound.hasKey("quarrySize"))
            return;

         NBTTagCompound quarrySize = (NBTTagCompound) compound.getTag("quarrySize");
         xSize = quarrySize.getInteger("xSize");
         ySize = quarrySize.getInteger("ySize");
         zSize = quarrySize.getInteger("zSize");
      }
   }
}
