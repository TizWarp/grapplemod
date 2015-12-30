package com.yyon.grapplinghook.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.yyon.grapplinghook.grapplemod;

//* // 1.8 Compatability
import net.minecraftforge.fml.common.FMLCommonHandler;
/*/ // 1.7.10 Compatability
import net.minecraft.client.renderer.texture.IIconRegister;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
//*/

/*
 * This file is part of GrappleMod.

    GrappleMod is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GrappleMod is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */

public class launcherItem extends Item {
	
//	EntityPlayer playerused = null;
//	int reusetimer = 0;
//	int reusetime = 50;

	public launcherItem() {
		super();
		maxStackSize = 1;
		setFull3D();
		setUnlocalizedName("launcheritem");
		
		this.setMaxDamage(500);
		
//		func_111022_d("grappling");
		setCreativeTab(CreativeTabs.tabCombat);
		
		FMLCommonHandler.instance().bus().register(this);
	}
	
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 72000;
	}
	
	
	
/* // 1.7.10 Compatability
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister)
	{
		 itemIcon = iconRegister.registerIcon("grapplemod:launcheritem");
	}
//*/

	public void dorightclick(ItemStack stack, World worldIn, EntityPlayer player) {
		if (worldIn.isRemote) {
			/*
			NBTTagCompound compound = stack.getSubCompound("launcher", true);
			long timer = worldIn.getTotalWorldTime() - compound.getLong("lastused");
			System.out.println(worldIn.getTotalWorldTime());
			if (timer > reusetime) {
//				playerused = player;
//				reusetimer = reusetime;
				compound.setLong("lastused", worldIn.getTotalWorldTime());
				
	        	Vec3 facing = player.getLookVec();
				Vec3 playermotion = new Vec3(player.motionX, player.motionY, player.motionZ);
				Vec3 newvec = playermotion.add(multvec(facing, 3));
				
//				player.setVelocity(newvec.xCoord, newvec.yCoord, newvec.zCoord);
				player.motionX = newvec.xCoord;
				player.motionY = newvec.yCoord;
				player.motionZ = newvec.zCoord;
				
				if (player instanceof EntityPlayerMP) {
					((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(player));
				} else {
					grapplemod.network.sendToServer(new PlayerMovementMessage(player.getEntityId(), player.posX, player.posY, player.posZ, player.motionX, player.motionY, player.motionZ));
				}
			}
			*/
			grapplemod.proxy.launchplayer(player);
		}
	}
	
/* // 1.7.10 Compatability
	@Override
//*/

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn, int timeLeft)
    {
    	
//        int j = this.getMaxItemUseDuration(stack) - timeLeft;
//        net.minecraftforge.event.entity.player.ArrowLooseEvent event = new net.minecraftforge.event.entity.player.ArrowLooseEvent(playerIn, stack, j);
//        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return;
        
    }
    
/* // 1.7.10 Compatability
	@Override
//*/

	public ItemStack onItemRightClick(ItemStack stack, World worldIn, final EntityPlayer playerIn){
//        net.minecraftforge.event.entity.player.ArrowNockEvent event = new net.minecraftforge.event.entity.player.ArrowNockEvent(playerIn, stack);
//        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return event.result;
        
		playerIn.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        
        this.dorightclick(stack, worldIn, playerIn);
        
		return stack;
	}
	
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn)
    {
        return stack;
    }


	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
/* // 1.7.10 Compatability
    @Override
//*/

	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
//* // 1.8 Compatability
		return EnumAction.NONE;
/*/ // 1.7.10 Compatability
		return EnumAction.none;
//*/

	}
	
	/*
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		ItemStack stack = event.player.getHeldItem();
		if (stack != null) {
			Item item = stack.getItem();
			if (item instanceof launcherItem) {
				if (event.player.onGround) {
					NBTTagCompound compound = stack.getSubCompound("launcher", true);
					if (compound.getLong("lastused") != 0) {
						long timer = event.player.worldObj.getTotalWorldTime() - compound.getLong("lastused");
						if (timer > 1000) {
							compound.setLong("lastused", 0);
						}
					}
				}
			}
		}
	}
	*/
/*	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		if (reusetimer > 0) {
			reusetimer--;
		}
		if (playerused != null) {
			if (playerused.onGround && reusetimer <= 0) {
				playerused = null;
			}
		}
	}*/
}
