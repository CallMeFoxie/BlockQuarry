package foxie.blockquarry;

import net.minecraft.item.ItemStack;

import java.util.Random;

public class Tools {
   public static ItemStack[] generateLevel(int x, int y, int z, Random random) {
      ItemStack[] leveldata = new ItemStack[x * z];
      int remaining = x * z;

      float maxChance = Config.INSTANCE.getOreMaxChance(y);
      Config.ConfigGenChanceLevel[] chances = Config.INSTANCE.getOreMapChance(y);

      while (remaining > 0) {
         float nextRand = random.nextFloat() * maxChance; // scale up
         Config.ConfigGenOre ore = Config.ConfigGenOre.findClosestOre(chances, nextRand);
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
}
