package foxie.blockquarry.block;

import foxie.blockquarry.te.TEQuarrier;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockQuarrier extends BlockContainer {
   protected BlockQuarrier() {
      super(Material.iron);
   }

   @Override
   public TileEntity createNewTileEntity(World world, int meta) {
      return new TEQuarrier();
   }
}
