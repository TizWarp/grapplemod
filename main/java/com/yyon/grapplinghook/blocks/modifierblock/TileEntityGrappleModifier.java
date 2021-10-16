package com.yyon.grapplinghook.blocks.modifierblock;

import java.util.HashMap;

import javax.annotation.Nullable;

import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.network.GrappleModifierMessage;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import com.yyon.grapplinghook.utils.GrappleCustomization.upgradeCategories;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;

public class TileEntityGrappleModifier extends TileEntity {
	public HashMap<GrappleCustomization.upgradeCategories, Boolean> unlockedCategories = new HashMap<GrappleCustomization.upgradeCategories, Boolean>();
	public GrappleCustomization customization;
	
	public TileEntityGrappleModifier() {
		super(CommonSetup.grappleModifierTileEntityType);
		this.customization = new GrappleCustomization();
	}

	public void unlockCategory(upgradeCategories category) {
		unlockedCategories.put(category, true);
		this.sendUpdates();
	}
	
	public void setCustomizationClient(GrappleCustomization customization) {
		this.customization = customization;
		CommonSetup.network.sendToServer(new GrappleModifierMessage(this.worldPosition, this.customization));
		this.sendUpdates();
	}

	public void setCustomizationServer(GrappleCustomization customization) {
		this.customization = customization;
		this.sendUpdates();
	}

	private void sendUpdates() {
		this.setChanged();
	}
	
	public boolean isUnlocked(upgradeCategories category) {
		return this.unlockedCategories.containsKey(category) && this.unlockedCategories.get(category);
	}
	
	@Override
	public CompoundNBT save(CompoundNBT nbtTagCompound) {
		super.save(nbtTagCompound);

		CompoundNBT unlockedNBT = nbtTagCompound.getCompound("unlocked");
		
		for (GrappleCustomization.upgradeCategories category : GrappleCustomization.upgradeCategories.values()) {
			String num = String.valueOf(category.toInt());
			boolean unlocked = this.isUnlocked(category);
			
			unlockedNBT.putBoolean(num, unlocked);
		}
		
		nbtTagCompound.put("unlocked", unlockedNBT);
		nbtTagCompound.put("customization", this.customization.writeNBT());

		return nbtTagCompound;
	}

	@Override
	public void load(BlockState state, CompoundNBT parentNBTTagCompound) {
		super.load(state, parentNBTTagCompound); // The super call is required to load the tiles location
		
		CompoundNBT unlockedNBT = parentNBTTagCompound.getCompound("unlocked");
		
		for (GrappleCustomization.upgradeCategories category : GrappleCustomization.upgradeCategories.values()) {
			String num = String.valueOf(category.toInt());
			boolean unlocked = unlockedNBT.getBoolean(num);
			
			this.unlockedCategories.put(category, unlocked);
		}
		
		CompoundNBT custom = parentNBTTagCompound.getCompound("customization");
		this.customization.loadNBT(custom);
	}
	
	
	// When the world loads from disk, the server needs to send the TileEntity information to the client
		//  it uses getUpdatePacket(), getUpdateTag(), onDataPacket(), and handleUpdateTag() to do this:
	  //  getUpdatePacket() and onDataPacket() are used for one-at-a-time TileEntity updates
	  //  getUpdateTag() and handleUpdateTag() are used by vanilla to collate together into a single chunk update packet
		//  Not really required for this example since we only use the timer on the client, but included anyway for illustration
		@Override
	  @Nullable
	  public SUpdateTileEntityPacket getUpdatePacket()
	  {
			CompoundNBT nbtTagCompound = new CompoundNBT();
			this.save(nbtTagCompound);
			int tileEntityType = 42;  // arbitrary number; only used for vanilla TileEntities.  You can use it, or not, as you want.
			return new SUpdateTileEntityPacket(this.worldPosition, tileEntityType, nbtTagCompound);
		}

		@Override
		public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
	    BlockState blockState = this.level.getBlockState(this.worldPosition);
	    this.load(blockState, pkt.getTag());   // read from the nbt in the packet
		}

	  /* Creates a tag containing all of the TileEntity information, used by vanilla to transmit from server to client
	 */
	  @Override
	  public CompoundNBT getUpdateTag()
	  {
	    CompoundNBT nbtTagCompound = new CompoundNBT();
	    this.save(nbtTagCompound);
	    return nbtTagCompound;
	  }

	  /* Populates this TileEntity with information from the tag, used by vanilla to transmit from server to client
	 */
	  @Override
	  public void handleUpdateTag(BlockState blockState, CompoundNBT parentNBTTagCompound)
	  {
	    this.load(blockState, parentNBTTagCompound);
	  }
}