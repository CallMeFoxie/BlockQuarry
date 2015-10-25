package foxie.blockquarry.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import foxie.blockquarry.BlockQuarry;
import foxie.blockquarry.te.TEQuarryMachine;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockQuarryMachine extends BlockContainer {
   @SideOnly(Side.CLIENT)
   IIcon iconTop;
   @SideOnly(Side.CLIENT)
   IIcon iconBottom;
   @SideOnly(Side.CLIENT)
   IIcon iconSide;

   protected BlockQuarryMachine() {
      super(Material.iron);
      setBlockName("quarry_machine");
      setCreativeTab(BlockQuarry.creativeTabBlockQuarry);
   }

   @Override
   public TileEntity createNewTileEntity(World world, int meta) {
      return new TEQuarryMachine();
   }

   @Override
   public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
      TEQuarryMachine machine = (TEQuarryMachine) world.getTileEntity(x, y, z);

      if (machine.getCurrentBlockPos() != null) {
         player.addChatMessage(new ChatComponentText("Digging: X: " + machine.getCurrentBlockPos().getX() + ", Y: " +
                 machine.getCurrentBlockPos().getY() + ", Z: " + machine.getCurrentBlockPos().getZ()));
         player.addChatMessage(new ChatComponentText("Power: " + machine.getEnergyStored(ForgeDirection.UNKNOWN)));
      }

      return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
   }

   @Override
   public void registerBlockIcons(IIconRegister iconReg) {
      iconTop = iconReg.registerIcon("blockquarry:quarry_top");
      iconBottom = iconReg.registerIcon("blockquarry:quarry_bottom");
      iconSide = iconReg.registerIcon("blockquarry:quarry_side");
   }

   @Override
   public IIcon getIcon(int side, int meta) {
      if (side == ForgeDirection.UP.ordinal())
         return iconTop;
      else if (side == ForgeDirection.DOWN.ordinal())
         return iconBottom;

      return iconSide;
   }
}
