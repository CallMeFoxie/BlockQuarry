package foxie.blockquarry;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class Tools {
   public static ItemStack[] generateLevel(int x, int y, int z, Random random) {
      ItemStack[] leveldata = new ItemStack[x * z];
      int remaining = x * z;

      double maxChance = BQConfig.INSTANCE.getOreMaxChance(y);
      BQConfig.ConfigGenChanceLevel[] chances = BQConfig.INSTANCE.getOreMapChance(y);

      while (remaining > 0) {
         double nextRand = random.nextDouble() * maxChance; // scale up
         BQConfig.ConfigGenOre ore = BQConfig.ConfigGenOre.findClosestOre(chances, nextRand);
         int clusterSize = random.nextInt(Math.min(ore.clusterSize, remaining) + 1); // exclusive so +1
         // now fill the stacks up
         for (; clusterSize > 0; clusterSize--) {
            leveldata[remaining - 1] = ore.itemStack.copy();
            leveldata[remaining - 1].stackSize = random.nextInt(ore.stackSizeMax + 1) + 1; // min 1 in the itemstack pls
            remaining--;
         }
      }

      return leveldata;
   }

   public static boolean tryInsertIntoInventory(ISidedInventory inventory, ForgeDirection direction, ItemStack stack) {
      for (int slot : inventory.getAccessibleSlotsFromSide(direction.ordinal())) {
         if (inventory.canInsertItem(slot, stack, direction.ordinal())) {
            inventory.setInventorySlotContents(slot, mergeStacks(stack, inventory.getStackInSlot(slot)));
            return true;
         }
      }

      return false;
   }

   public static boolean tryInsertIntoInventory(IInventory inventory, ForgeDirection direction, ItemStack stack) {
      if (inventory instanceof ISidedInventory)
         return tryInsertIntoInventory((ISidedInventory) inventory, direction, stack);

      for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
         if (canMergeStacks(inventory.getStackInSlot(slot), stack)) {
            inventory.setInventorySlotContents(slot, mergeStacks(inventory.getStackInSlot(slot), stack));
            return true;
         }
      }

      return false;
   }

   public static boolean canMergeStacks(ItemStack a, ItemStack b) {
      if (a == null || b == null)
         return true;

      if (a.isItemEqual(b) && a.getMaxStackSize() >= a.stackSize + b.stackSize)
         return true;

      return false;
   }

   public static ItemStack mergeStacks(ItemStack a, ItemStack b) {
      if (a == null && b != null)
         return b.copy();
      else if (b == null && a != null)
         return a.copy();
      else if (a == null && b == null)
         return null;

      a.stackSize += b.stackSize;
      return a;
   }
}
