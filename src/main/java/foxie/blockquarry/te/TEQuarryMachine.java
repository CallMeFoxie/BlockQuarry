package foxie.blockquarry.te;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import foxie.blockquarry.BlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TEQuarryMachine extends TileEntity implements IEnergyReceiver { // TODO IC2 EU support?
   public static final int   POWER_CAPACITY  = 10000; // 10k RF
   public static final float POWER_CREEP     = 1.1f; // 1.1-times more power than the layer above!
   public static final int   POWER_PER_BLOCK = 100; // 100 RF per block?

   EnergyStorage energyStorage;
   private BlockPos currentBlockPos; // currently mined block

   private TEQuarryPortal quarryPortal;

   public TEQuarryMachine() {
      energyStorage = new EnergyStorage(POWER_CAPACITY);
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      NBTTagCompound compCurrentBlockPos = new NBTTagCompound();
      currentBlockPos.writeToNBT(compCurrentBlockPos);
      compound.setTag("currentBlockPos", compCurrentBlockPos);
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      NBTTagCompound compCurrentBlockPos = compound.getCompoundTag("currentBlockpos");
      currentBlockPos = BlockPos.readFromNBT(compCurrentBlockPos);
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

   }

   private int getPowerRequired() {
      return (int) Math.pow(POWER_CREEP, quarryPortal.getQuarrySize().ySize - currentBlockPos.getY() + 1) * POWER_PER_BLOCK;
   }

   @Override
   public void updateEntity() {

   }
}
