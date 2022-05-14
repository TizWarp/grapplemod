package com.yyon.grapplinghook.entities;

import java.util.HashMap;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.controllers.SegmentHandler;
import com.yyon.grapplinghook.network.GrappleAttachMessage;
import com.yyon.grapplinghook.network.GrappleAttachPosMessage;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

public class grappleArrow extends EntityThrowable implements IEntityAdditionalSpawnData
{
	public Entity shootingEntity = null;
	public int shootingEntityID;
	
	private boolean firstattach = false;
	public vec thispos;
	
	public boolean righthand = true;
	
	public boolean attached = false;
	
	public double pull;
	
	public double taut = 1;
	
	public boolean ignoreFrustumCheck = true;
	
	public boolean isdouble = false;
	
//	public double maxlen = 20;
	public double r;
	
	public SegmentHandler segmenthandler = null;
	
	public GrappleCustomization customization = null;
	
/*	public vec debugpos = null;
	public vec debugpos2 = null;
	public vec debugpos3 = null;*/
	
	// magnet attract
	public vec prevpos = null;
	public boolean foundblock = false;
	public boolean wasinair = false;
	public BlockPos magnetblock = null;
	
 	public vec attach_dir = null;
	
	public grappleArrow(World worldIn) {
		super(worldIn);
		
		this.segmenthandler = new SegmentHandler(this.world, this, vec.positionvec(this), vec.positionvec(this));
		this.customization = new GrappleCustomization();
	}
	
	public grappleArrow(World worldIn, EntityLivingBase shooter,
			boolean righthand, GrappleCustomization customization, boolean isdouble) {
		super(worldIn, shooter);
		
		this.shootingEntity = shooter;
		this.shootingEntityID = this.shootingEntity.getEntityId();
		
		this.isdouble = isdouble;
		
		/*
		double x = 0.36;
		if (righthand) {x = -0.36;}
        vec pos = vec.positionvec(this);
        pos.add_ip(new vec(x, -0.175, 0.45).rotate_yaw(Math.toRadians(shooter.rotationYaw)));
        this.setPosition(pos.x, pos.y, pos.z);
        */
		
		vec pos = vec.positionvec(this.shootingEntity).add(new vec(0, this.shootingEntity.getEyeHeight(), 0));

		this.segmenthandler = new SegmentHandler(this.world, this, new vec(pos), new vec(pos));

		this.customization = customization;
		this.r = customization.maxlen;
		
		this.righthand = righthand;
	}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		
		if (this.shootingEntityID == 0 || this.shootingEntity == null) { // removes ghost grappling hooks
			this.remove();
			return;
		}
		
		if (this.firstattach) {
			this.motionX = 0;
			this.motionY = 0;
			this.motionZ = 0;
			this.firstattach = false;
			super.setPosition(this.thispos.x, this.thispos.y, this.thispos.z);
		}
		
		if (!this.world.isRemote) {
			if (this.shootingEntity != null)  {
				if (!this.attached) {
					if (this.segmenthandler.hookpastbend(this.r)) {
						System.out.println("around bend");
						vec farthest = this.segmenthandler.getfarthest();
						this.serverAttach(this.segmenthandler.getbendblock(1), farthest, null);
					}
					
					if (!this.customization.phaserope) {
						this.segmenthandler.update(vec.positionvec(this), vec.positionvec(this.shootingEntity).add(new vec(0, this.shootingEntity.getEyeHeight(), 0)), this.r, true);
						
						if (this.customization.sticky) {
							if (this.segmenthandler.segments.size() > 2) {
								int bendnumber = this.segmenthandler.segments.size() - 2;
								vec closest = this.segmenthandler.segments.get(bendnumber);
								BlockPos blockpos = this.segmenthandler.getbendblock(bendnumber);
								for (int i = 1; i <= bendnumber; i++) { 
									this.segmenthandler.removesegment(1);
								}
								this.serverAttach(blockpos, closest, null);
							}
						}
					} else {
						this.segmenthandler.updatepos(vec.positionvec(this), vec.positionvec(this.shootingEntity).add(new vec(0, this.shootingEntity.getEyeHeight(), 0)), this.r);
					}
					
					vec farthest = this.segmenthandler.getfarthest();
					double distToFarthest = this.segmenthandler.getDistToFarthest();
					
					vec ropevec = vec.positionvec(this).sub(farthest);
					double d = ropevec.length();
					
					if (this.customization.reelin && this.shootingEntity.isSneaking()) {
						double newdist = d + distToFarthest - 0.4;
						if (newdist > 1 && newdist <= this.customization.maxlen) {
							this.r = newdist;
						}
					}


					if (d + distToFarthest > this.r) {
						vec motion = vec.motionvec(this);
						
						if (motion.dot(ropevec) > 0) {
							motion = motion.removealong(ropevec);
						}
						
						this.setVelocityActually(motion.x, motion.y, motion.z);
						
						ropevec.changelen_ip(this.r - distToFarthest);
						vec newpos = ropevec.add(farthest);
						
						this.setPosition(newpos.x, newpos.y, newpos.z);
					}
					
				}
			}
/*		} else {
			vec farthest = this.segmenthandler.getfarthest();
			double distToFarthest = this.segmenthandler.getDistToFarthest();
			
			vec ropevec = vec.positionvec(this).sub(farthest);
			double d = ropevec.length();
			
			this.taut = d + distToFarthest / maxlen;*/
		}
		
		// magnet attraction
		if (this.customization.attract && vec.positionvec(this).sub(vec.positionvec(this.shootingEntity)).length() > this.customization.attractradius) {
	    	if (!this.foundblock) {
	    		if (!this.world.isRemote) {
	    			vec playerpos = vec.positionvec(this.shootingEntity);
	    			vec pos = vec.positionvec(this);
	    			if (magnetblock == null) {
		    			if (prevpos != null) {
			    			HashMap<BlockPos, Boolean> checkedset = new HashMap<BlockPos, Boolean>();
			    			vec vector = pos.sub(prevpos);
			    			vec normvector = vector.normalize();
			    			for (int i = 0; i < vector.length(); i++) {
			    				double dist = prevpos.sub(playerpos).length();
			    				int radius = (int) dist / 4;
			    				BlockPos found = this.check(prevpos, checkedset);
			    				if (found != null) {
//			    					if (wasinair) {
							    		vec distvec = new vec(found.getX(), found.getY(), found.getZ());
							    		distvec.sub_ip(prevpos);
							    		if (distvec.length() < radius) {
					    					this.setPositionAndUpdate(prevpos.x, prevpos.y, prevpos.z);
					    					pos = prevpos;
					    					
					    					magnetblock = found;
					    					
					    					break;
							    		}
//			    					}
			    				} else {
			    					wasinair = true;
			    				}
			    				
			    				prevpos.add_ip(normvector);
			    			}
		    			}
	    			}
	    			
	    			if (magnetblock != null) {
				    	IBlockState blockstate = this.world.getBlockState(magnetblock);
				    	AxisAlignedBB BB = blockstate.getCollisionBoundingBox(this.world, magnetblock);

						vec blockvec = new vec(magnetblock.getX() + (BB.maxX + BB.minX) / 2, magnetblock.getY() + (BB.maxY + BB.minY) / 2, magnetblock.getZ() + (BB.maxZ + BB.minZ) / 2);
						vec newvel = blockvec.sub(pos);
						
						double l = newvel.length();
						
						newvel.changelen(this.getVelocity());
						
						this.motionX = newvel.x;
						this.motionY = newvel.y;
						this.motionZ = newvel.z;
						
						if (l < 0.2) {
							this.serverAttach(magnetblock, blockvec, EnumFacing.UP);
						}
	    			}
	    			
	    			prevpos = pos;
	    		}
	    	}
		}
	}
	
	public void setVelocityActually(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt(x * x + z * z);
            this.rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
            this.rotationPitch = (float)(MathHelper.atan2(y, (double)f) * (180D / Math.PI));
            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
        }
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRender3d(double x, double y, double z) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}

	public final int RenderBoundingBoxSize = 999;
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		if (this.shootingEntity == null) {
			return super.getRenderBoundingBox();
		}
		
		AxisAlignedBB bb = this.segmenthandler.getBoundingBox(vec.positionvec(this), vec.positionvec(this.shootingEntity).add(new vec(0, this.shootingEntity.getEyeHeight(), 0)));
//		bb = bb.union(this.shootingEntity.getEntityBoundingBox());
//		AxisAlignedBB shooting_bb = this.shootingEntity.getRenderBoundingBox();
//		bb = bb.expand(shooting_bb.minX, shooting_bb.minY, shooting_bb.minZ);
//		bb = bb.expand(shooting_bb.maxX, shooting_bb.maxY, shooting_bb.maxZ);
//		System.out.println(bb);
//		bb.grow(1);
		return bb;
//		return new AxisAlignedBB(this.posX - RenderBoundingBoxSize, this.posY - RenderBoundingBoxSize, this.posZ - RenderBoundingBoxSize, 
//				this.posX + RenderBoundingBoxSize, this.posY + RenderBoundingBoxSize, this.posZ + RenderBoundingBoxSize);
	}

//	@Override
//	public AxisAlignedBB getEntityBoundingBox() {
//		return this.segmenthandler.getBoundingBox(vec.positionvec(this), vec.positionvec(this.shootingEntity).add(new vec(0, this.shootingEntity.getEyeHeight(), 0)));
//	}
	
/*	public boolean toofaraway() {
    	if (this.shootingEntity == null) {return false;}
		if (!this.world.isRemote) {
			if (!grapplemod.attached.contains(this.shootingEntityID)) {
				if (grapplemod.grapplingLength != 0) {
					double d = vec.positionvec(this).sub(vec.positionvec(this.shootingEntity)).length();
					if (d > grapplemod.grapplingLength) {
						return true;
					}
				}
			}
		}
		return false;
	}*/

	public void setPosition(double x, double y, double z) {
		if (this.thispos != null) {
			x = this.thispos.x;
			y = this.thispos.y;
			z = this.thispos.z;
		}
		super.setPosition(x, y, z);
	}
	
	
	@Override
    public void writeSpawnData(ByteBuf data)
    {
	    data.writeInt(this.shootingEntity != null ? this.shootingEntity.getEntityId() : 0);
	    data.writeBoolean(this.righthand);
	    data.writeBoolean(this.isdouble);
	    if (this.customization == null) {
	    	System.out.println("error: customization null");
	    }
	    this.customization.writeToBuf(data);
    }
	
	@Override
    public void readSpawnData(ByteBuf data)
    {
    	this.shootingEntityID = data.readInt();
	    this.shootingEntity = this.world.getEntityByID(this.shootingEntityID);
	    this.righthand = data.readBoolean();
	    this.isdouble = data.readBoolean();
	    this.customization = new GrappleCustomization();
	    this.customization.readFromBuf(data);
    }
	
	public void remove() {
		this.setDead();
	}
	
	@Override
	public String toString() {
		return super.toString() + String.valueOf(System.identityHashCode(this)) + "]";
	}

	@Override
	protected void onImpact(RayTraceResult movingobjectposition) {
		if (!this.world.isRemote) {
			if (this.attached) {
				return;
			}
			if (this.shootingEntityID != 0 && this.shootingEntity != null) {
				if (movingobjectposition == null) {
					return;
				}
				
				Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
		        Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

				if (movingobjectposition.typeOfHit == RayTraceResult.Type.ENTITY && !GrappleConfig.getconf().hookaffectsentities) {
			        onImpact(this.world.rayTraceBlocks(vec3d, vec3d1, false, true, false));
			        return;
				}
				
				if (grapplemod.anyignoresblocks && movingobjectposition.typeOfHit == RayTraceResult.Type.BLOCK) {
					BlockPos blockpos = movingobjectposition.getBlockPos();
					if (blockpos != null) {
						Block block = this.world.getBlockState(blockpos).getBlock();
						if (grapplemod.grapplingignoresblocks.contains(block)) {
							System.out.println("ignore block");
					        onImpact(this.world.rayTraceBlocks(vec3d, vec3d1, false, true, false));
					        return;
						}
					}
				}
				
				if (grapplemod.anybreakblocks && movingobjectposition.typeOfHit == RayTraceResult.Type.BLOCK) {
					BlockPos blockpos = movingobjectposition.getBlockPos();
					if (blockpos != null) {
						Block block = this.world.getBlockState(blockpos).getBlock();
						if (grapplemod.grapplingbreaksblocks.contains(block)) {
							this.world.destroyBlock(blockpos, true);
							System.out.println("break block");
					        onImpact(this.world.rayTraceBlocks(vec3d, vec3d1, false, true, false));
					        return;
						}
					}
				}
				
				if (movingobjectposition.typeOfHit == RayTraceResult.Type.ENTITY) {
					// hit entity
					Entity entityhit = movingobjectposition.entityHit;
					if (entityhit == this.shootingEntity) {
						return;
					}
					
					vec playerpos = vec.positionvec(this.shootingEntity);
					vec entitypos = vec.positionvec(entityhit);
					vec yank = playerpos.sub(entitypos).mult(0.4);
					entityhit.addVelocity(yank.x, Math.min(yank.y, 2), yank.z);
					
					this.removeServer();
					return;
				} else if (movingobjectposition.typeOfHit == RayTraceResult.Type.BLOCK) {
					BlockPos blockpos = movingobjectposition.getBlockPos();
					
					vec vec3 = new vec(movingobjectposition.hitVec.x, movingobjectposition.hitVec.y, movingobjectposition.hitVec.z);

					this.serverAttach(blockpos, vec3, movingobjectposition.sideHit);
				} else {
					System.out.println("unknown impact?");
				}
			}
		}
	}
	
	public void serverAttach(BlockPos blockpos, vec pos, EnumFacing sideHit) {
		if (this.attached) {
			return;
		}
		if (this.shootingEntity == null || this.shootingEntityID == 0) {
			return;
		}
		this.attached = true;

		if (blockpos != null) {
			if (!grapplemod.anyblocks) {
				Block block = this.world.getBlockState(blockpos).getBlock();

				if ((!grapplemod.removeblocks && !grapplemod.grapplingblocks.contains(block))
						|| (grapplemod.removeblocks && grapplemod.grapplingblocks.contains(block))) {
					this.removeServer();
					return;
				}
			}
		}
		
		vec vec3 = vec.positionvec(this);
		vec3.add_ip(vec.motionvec(this));
		if (pos != null) {
            vec3 = pos;
            
            this.setPositionAndUpdate(vec3.x, vec3.y, vec3.z);
		}
		
		//west -x
		//north -z
		if (sideHit == EnumFacing.DOWN) {
			this.posY -= 0.3;
		} else if (sideHit == EnumFacing.WEST) {
			this.posX -= 0.05;
		} else if (sideHit == EnumFacing.NORTH) {
			this.posZ -= 0.05;
		} else if (sideHit == EnumFacing.SOUTH) {
			this.posZ += 0.05;
		} else if (sideHit == EnumFacing.EAST) {
			this.posX += 0.05;
		} else if (sideHit == EnumFacing.UP) {
			this.posY += 0.05;
		}
		
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
        
        this.thispos = vec.positionvec(this);
		this.firstattach = true;
		grapplemod.attached.add(this.shootingEntityID);
		
		grapplemod.sendtocorrectclient(new GrappleAttachMessage(this.getEntityId(), this.posX, this.posY, this.posZ, this.getControlId(), this.shootingEntityID, blockpos, this.segmenthandler.segments, this.segmenthandler.segmenttopsides, this.segmenthandler.segmentbottomsides, this.customization), this.shootingEntityID, this.world);
		if (this.shootingEntity instanceof EntityPlayerMP) { // fixes strange bug in LAN
			EntityPlayerMP sender = (EntityPlayerMP) this.shootingEntity;
			int dimension = sender.dimension;
			MinecraftServer minecraftServer = sender.mcServer;
			for (EntityPlayerMP player : minecraftServer.getPlayerList().getPlayers()) {
				GrappleAttachPosMessage msg = new GrappleAttachPosMessage(this.getEntityId(), this.posX, this.posY, this.posZ);   // must generate a fresh message for every player!
				if (dimension == player.dimension) {
					grapplemod.sendtocorrectclient(msg, player.getEntityId(), player.world);
				}
			}
		}
	}
	
	public void clientAttach(double x, double y, double z) {
		this.setAttachPos(x, y, z);
		
		if (this.shootingEntity instanceof EntityPlayer) {
			grapplemod.proxy.resetlaunchertime(this.shootingEntityID);
		}
	}
	
	
	@Override
    protected float getGravityVelocity()
    {
        return (float) this.customization.hookgravity * 0.1F;
    }
	
    public float getVelocity()
    {
        return (float) this.customization.throwspeed;
    }

	public void removeServer() {
		this.setDead();
		this.shootingEntityID = 0;

	}
	
	public int getControlId() {
		return grapplemod.GRAPPLEID;
	}

	public void setAttachPos(double x, double y, double z) {
		this.setPositionAndUpdate(x, y, z);

		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		this.firstattach = true;
		this.attached = true;
        this.thispos = new vec(x, y, z);
	}
	
	// used for magnet attraction
    public BlockPos check(vec p, HashMap<BlockPos, Boolean> checkedset) {
    	int radius = (int) Math.floor(this.customization.attractradius);
    	BlockPos closestpos = null;
    	double closestdist = 0;
    	for (int x = (int)p.x - radius; x <= (int)p.x + radius; x++) {
        	for (int y = (int)p.y - radius; y <= (int)p.y + radius; y++) {
            	for (int z = (int)p.z - radius; z <= (int)p.z + radius; z++) {
			    	BlockPos pos = new BlockPos(x, y, z);
			    	if (pos != null) {
				    	if (hasblock(pos, checkedset)) {
				    		vec distvec = new vec(pos.getX(), pos.getY(), pos.getZ());
				    		distvec.sub_ip(p);
				    		double dist = distvec.length();
				    		if (closestpos == null || dist < closestdist) {
				    			closestpos = pos;
				    			closestdist = dist;
				    		}
				    	}
			    	}
            	}
	    	}
    	}
		return closestpos;
	}

	// used for magnet attraction
	public boolean hasblock(BlockPos pos, HashMap<BlockPos, Boolean> checkedset) {
    	if (!checkedset.containsKey(pos)) {
    		boolean isblock = false;
	    	IBlockState blockstate = this.world.getBlockState(pos);
	    	Block b = blockstate.getBlock();
			if (!grapplemod.anyblocks && ((!grapplemod.removeblocks && !grapplemod.grapplingblocks.contains(b))
						|| (grapplemod.removeblocks && grapplemod.grapplingblocks.contains(b)))) {
			} else {
		    	if (!(b.isAir(blockstate, this.world, pos))) {
			    	AxisAlignedBB BB = blockstate.getCollisionBoundingBox(this.world, pos);
			    	if (BB != null) {
			    		isblock = true;
			    	}
		    	}
			}
			
	    	checkedset.put(pos, (Boolean) isblock);
	    	return isblock;
    	} else {
    		return checkedset.get(pos);
    	}
	}
}
