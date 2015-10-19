package foxie.blockquarry.block;

import foxie.blockquarry.BlockQuarry;
import foxie.blockquarry.te.TEQuarryPortal;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockQuarryPortal extends BlockContainer {
   protected BlockQuarryPortal() {
      super(Material.rock);
      setBlockUnbreakable();
      setBlockName("quarry_portal");
      setBlockTextureName("quarry_portal");
      setCreativeTab(BlockQuarry.creativeTabBlockQuarry);
   }

   @Override
   public TileEntity createNewTileEntity(World world, int meta) {
      return new TEQuarryPortal(world);
   }
}
