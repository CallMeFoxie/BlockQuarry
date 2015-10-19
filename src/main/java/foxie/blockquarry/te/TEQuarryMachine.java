package foxie.blockquarry.te;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import foxie.blockquarry.BlockPos;
import foxie.blockquarry.Tools;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TEQuarryMachine extends TileEntity implements IEnergyReceiver { // TODO IC2 EU support?
   public static final int   POWER_CAPACITY  = 10000; // 10k RF
   public static final float POWER_CREEP     = 1.1f; // 1.1-times more power than the layer above!
   public static final int   POWER_PER_BLOCK = 100; // 100 RF per block?

   EnergyStorage energyStorage;
   private BlockPos currentBlockPos; // currently mined block
   private boolean workReady = true;

   private TEQuarryPortal quarryPortal;

   public TEQuarryMachine() {
      energyStorage = new EnergyStorage(POWER_CAPACITY);
      workReady = true;
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      NBTTagCompound compCurrentBlockPos = new NBTTagCompound();
      if (currentBlockPos != null)
         currentBlockPos.writeToNBT(compCurrentBlockPos);
      compound.setTag("currentBlockPos", compCurrentBlockPos);
      compound.setBoolean("workReady", workReady);
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      NBTTagCompound compCurrentBlockPos = compound.getCompoundTag("currentBlockpos");
      currentBlockPos = BlockPos.readFromNBT(compCurrentBlockPos);
      workReady = compound.getBoolean("workReady");
   }

   public BlockPos getCurrentBlockPos() {
      return currentBlockPos;
   }

   @Override
   public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
      return energyStorage.receiveEnergy(maxReceive, simulate);
   }

   @Override
   public int getEnergyStored(ForgeDirection from) {
      return energyStorage.getEnergyStored();
   }

   @Override
   public int getMaxEnergyStored(ForgeDirection from) {
      return energyStorage.getMaxEnergyStored();
   }

   @Override
   public boolean canConnectEnergy(ForgeDirection from) {
      return true;
   }

   private void loadQuarryPortal() {
      TileEntity tileEntity = worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);
      if (tileEntity instanceof TEQuarryPortal) {
         quarryPortal = (TEQuarryPortal) tileEntity;
      }
   }

   private int getPowerRequired() {
      return (int) Math.pow(POWER_CREEP, quarryPortal.getQuarrySize().ySize - currentBlockPos.getY() + 1) * POWER_PER_BLOCK;
   }

   @Override
   public void updateEntity() {
      if (worldObj.isRemote)
         return;

      if (worldObj.provider.getWorldTime() % 5 != 0) // once every 5 ticks tops = 4 blocks/second at max if there's enough power
         return;

      if (quarryPortal == null)
         loadQuarryPortal();
      if (quarryPortal == null)
         return;

      if (!workReady) // we're possibly done
         return;

      if (currentBlockPos == null)
         currentBlockPos = new BlockPos(0, quarryPortal.getQuarrySize().ySize, 0); // start from the top

      // do magic by digging
      //int powerRequired = getPowerRequired();
      int powerRequired = 0;
      if (energyStorage.getEnergyStored() >= powerRequired) {
         energyStorage.extractEnergy(powerRequired, false);
         ItemStack stack = quarryPortal.getDugBlock(currentBlockPos);
         ejectItemStack(stack);
         movePosition();
      }
   }

   private void movePosition() {
      if (currentBlockPos.getX() + 1 < quarryPortal.getQuarrySize().xSize) { // X++
         currentBlockPos.setX(currentBlockPos.getX() + 1);
      } else {
         if (currentBlockPos.getZ() + 1 < quarryPortal.getQuarrySize().zSize) { // Z++, X = 0
            currentBlockPos.setZ(currentBlockPos.getZ() + 1);
            currentBlockPos.setX(0);
         } else {
            if (currentBlockPos.getY() > 0) {
               currentBlockPos.setY(currentBlockPos.getY() - 1); // Y--, X = 0, Z = 0
               currentBlockPos.setZ(0);
               currentBlockPos.setX(0);
            } else {
               // do nothing? we're done here!
               workReady = false;
            }
         }
      }
   }

   private void ejectItemStack(ItemStack stack) {
      for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
         TileEntity tileEntity = worldObj.getTileEntity(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
         if (tileEntity instanceof IInventory) {
            boolean inserted = Tools.tryInsertIntoInventory((IInventory) tileEntity, direction.getOpposite(), stack);
            if (inserted)
               return;
         }
      }

      EntityItem item = new EntityItem(worldObj, xCoord, yCoord + 1, zCoord, stack);
      item.setVelocity(0, 1d, 0);
      worldObj.spawnEntityInWorld(item);
   }
}
