package foxie.blockquarry;

import cpw.mods.fml.common.IWorldGenerator;
import foxie.blockquarry.block.BlockReg;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

public class BQWorldGen implements IWorldGenerator {
   @Override
   public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
      if (world.provider.dimensionId != 0)
         return;

      if (random.nextFloat() > Config.portalChance)
         return;

      int x = random.nextInt(16);
      int z = random.nextInt(16);
      int y = findY(x, z, world) + 1;

      generate(x + chunkX << 4, y, z + chunkZ << 4, world);
   }

   private int findY(int x, int z, World world) {
      int y = 1;
      while (world.getBlock(x, y++, z) == Blocks.bedrock) ;

      return y;
   }

   private void generate(int x, int y, int z, World world) {
      // start at lowest level, 5x5
      for (int xx = -2; xx <= 2; xx++) {
         for (int zz = -2; zz <= 2; zz++) {
            world.setBlock(x + xx, y, z + zz, Blocks.bedrock);
            world.setBlock(x + xx, y, z + zz, Blocks.bedrock);
         }
      }

      // higher level 3x3 -- 4 layers, 2 lowest ones are bedrock (good luck connecting power and items pipe into 1 block..)
      for (int yy = 1; yy <= 4; yy++) {
         for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
               Block block = Blocks.bedrock;
               if (yy > 2)
                  block = Blocks.obsidian;

               world.setBlock(x + xx, y + yy, z + zz, block);
               world.setBlock(x + xx, y + yy, z + zz, block);
            }
         }
      }

      // place portal
      world.setBlock(x, y + 2, z, BlockReg.blockQuarryPortal);

      // place obsidian above
      world.setBlock(x, y + 3, z, Blocks.obsidian);
   }
}
