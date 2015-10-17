package foxie.blockquarry.block;

import foxie.blockquarry.te.TEQuarryMachine;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockQuarryMachine extends BlockContainer {
   protected BlockQuarryMachine() {
      super(Material.iron);
   }

   @Override
   public TileEntity createNewTileEntity(World world, int meta) {
      return new TEQuarryMachine();
   }
}
