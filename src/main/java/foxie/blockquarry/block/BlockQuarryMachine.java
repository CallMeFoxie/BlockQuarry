package foxie.blockquarry.block;

import foxie.blockquarry.BlockQuarry;
import foxie.blockquarry.te.TEQuarryMachine;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockQuarryMachine extends BlockContainer {
   protected BlockQuarryMachine() {
      super(Material.iron);
      setBlockName("quarry_machine");
      setBlockTextureName("quarry_machine");
      setCreativeTab(BlockQuarry.creativeTabBlockQuarry);
   }

   @Override
   public TileEntity createNewTileEntity(World world, int meta) {
      return new TEQuarryMachine();
   }

   @Override
   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
      TEQuarryMachine machine = (TEQuarryMachine) world.getTileEntity(x, y, z);

      player.addChatMessage(new ChatComponentText("Digging: X: " + machine.getCurrentBlockPos().getX() + ", Y: " +
              machine.getCurrentBlockPos().getY() + ", Z: " + machine.getCurrentBlockPos().getZ()));
      player.addChatMessage(new ChatComponentText("Power: " + machine.getEnergyStored(ForgeDirection.UNKNOWN)));

      return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
   }
}
