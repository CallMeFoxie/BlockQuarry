package foxie.blockquarry.te;

import foxie.blockquarry.BlockPos;
import foxie.blockquarry.Config;
import foxie.blockquarry.Tools;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Random;

public class TEQuarryPortal extends TileEntity {

   private ItemStack[][] blocks;
   // this will be a bit different to normal XYZ.
   // to simplify memory requirements I am using
   // Block[Y][X*width + Z]
   private QuarrySize    quarrySize;
   private int           topGeneratedLevel;

   public TEQuarryPortal() {
      quarrySize = new QuarrySize();
   }

   public TEQuarryPortal(World world) {
      // making new one I guess?
      quarrySize = QuarrySize.generateRandom(world.rand);
      blocks = new ItemStack[quarrySize.ySize][];
      topGeneratedLevel = quarrySize.ySize + 1; // above max
   }

   @Override
   public boolean canUpdate() {
      return false;
   }

   private boolean isYGenerated(int yLevel) {
      return blocks[yLevel] != null;
   }

   private void generateY(int yLevel, Random random) {
      blocks[yLevel] = Tools.generateLevel(getQuarrySize().xSize, yLevel, getQuarrySize().zSize, random);
      topGeneratedLevel = Math.min(yLevel, topGeneratedLevel);
      markDirty();
   }

   private int getArrayPos(BlockPos pos) {
      return pos.getX() * getQuarrySize().xSize + pos.getZ();
   }

   private ItemStack getBlock(BlockPos pos) {
      if (!isYGenerated(pos.getY()))
         generateY(pos.getY(), worldObj.rand);

      return blocks[pos.getY()][getArrayPos(pos)];
   }

   /**
    * THIS is what the public facing things should call!
    * <p/>
    * Yes, I require BlockPos because some implementation in the future MIGHT allow you to
    * select different cut-out size or anything really!
    *
    * @param dugBlockPos position of the block that should be mined
    * @return mined itemstack result
    */
   public ItemStack getDugBlock(BlockPos dugBlockPos) {
      if (dugBlockPos.getY() >= quarrySize.ySize || dugBlockPos.getY() < 0)
         return null;
      if (dugBlockPos.getX() >= quarrySize.xSize || dugBlockPos.getX() < 0)
         return null;
      if (dugBlockPos.getZ() >= quarrySize.zSize || dugBlockPos.getZ() < 0)
         return null;

      ItemStack foundBlock = getBlock(dugBlockPos);
      setBlockMined(dugBlockPos);
      return foundBlock;
   }

   /**
    * THIS is what the public facing things should call!
    *
    * @return size of the quarry
    */
   public QuarrySize getQuarrySize() {
      if (quarrySize == null || !quarrySize.generatedSize)
         quarrySize = QuarrySize.generateRandom(worldObj.rand);

      return quarrySize;
   }

   private void setBlockMined(BlockPos dugBlockPos) {
      blocks[dugBlockPos.getY()][getArrayPos(dugBlockPos)] = null;
      markDirty();
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      getQuarrySize().writeToNBT(compound);

      NBTTagCompound compBlocks = new NBTTagCompound();
      compBlocks.setInteger("topGeneratedLevel", topGeneratedLevel);

      for (int level = quarrySize.ySize - 1; level >= topGeneratedLevel; level--) {
         if (blocks[level] == null)
            continue;

         boolean anyBlocks = false;
         for (int i = 0; i < blocks[level].length; i++) {

            if (blocks[level][i] != null) {
               anyBlocks = true;
               break;
            }
         }
         if (!anyBlocks) // skip empty level
            continue;

         NBTTagCompound levelTag = new NBTTagCompound();

         for (int i = 0; i < blocks[level].length; i++) {
            if (blocks[level][i] != null) {
               levelTag.setTag("stack" + i, blocks[level][i].writeToNBT(new NBTTagCompound()));
            }
         }

         compBlocks.setTag("level" + level, levelTag);
      }

      compound.setTag("blocks", compBlocks);
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      quarrySize.readFromNBT(compound);

      if (!quarrySize.generatedSize)
         quarrySize = QuarrySize.generateRandom(worldObj.rand);

      // read the generated blocks
      NBTTagCompound compound1 = compound.getCompoundTag("blocks");
      if (compound1 == null)
         return;

      topGeneratedLevel = compound1.getInteger("topGeneratedLevel");
      blocks = new ItemStack[quarrySize.ySize][];

      for (int level = quarrySize.ySize; level >= topGeneratedLevel; level--) {
         if (compound1.getTag("level" + level) == null)
            continue;

         NBTTagCompound blocksTag = compound1.getCompoundTag("level" + level);

         if (blocksTag == null) {
            blocks[level] = null;
            continue;
         }

         blocks[level] = new ItemStack[quarrySize.xSize * quarrySize.zSize];

         for (int i = 0; i < quarrySize.xSize * quarrySize.zSize; i++) {
            blocks[level][i] = ItemStack.loadItemStackFromNBT(blocksTag.getCompoundTag("stack" + i));
         }
      }
   }

   public static class QuarrySize {
      public int xSize;
      public int ySize;
      public int zSize;

      public boolean generatedSize = false;

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

         size.generatedSize = true;

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
         if (!compound.hasKey("quarrySize")) {
            // should generate a new one possibly? nah screw it
            return;
         }

         NBTTagCompound quarrySize = (NBTTagCompound) compound.getTag("quarrySize");
         xSize = quarrySize.getInteger("xSize");
         ySize = quarrySize.getInteger("ySize");
         zSize = quarrySize.getInteger("zSize");

         generatedSize = true;
      }
   }
}
