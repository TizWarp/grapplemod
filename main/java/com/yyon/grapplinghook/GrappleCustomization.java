package com.yyon.grapplinghook;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class GrappleCustomization {
	public static final String[] booleanoptions = new String[] {"phaserope", "motor", "motorwhencrouching", "motorwhennotcrouching", "smartmotor", "enderstaff", "repel", "attract", "doublehook", "smartdoublemotor", "motordampener", "reelin", "pullbackwards", "oneropepull"};
	public static final String[] doubleoptions = new String[] {"maxlen", "hookgravity", "throwspeed", "motormaxspeed", "motoracceleration", "playermovementmult", "repelforce", "attractradius", "angle", "sneakingangle", "verticalthrowangle", "sneakingverticalthrowangle"};
	
	// rope
	public double maxlen = 30;
	public boolean phaserope = false;

	// hook thrower
	public double hookgravity = 1F;
	public double throwspeed = 2F;
	public boolean reelin = true;
	public double verticalthrowangle = 0F;
	public double sneakingverticalthrowangle = 0F;

	// motor
	public boolean motor = false;
	public double motormaxspeed = 4;
	public double motoracceleration = 0.2;
	public boolean motorwhencrouching = true;
	public boolean motorwhennotcrouching = true;
	public boolean smartmotor = false;
	public boolean motordampener = false;
	public boolean pullbackwards = true;
	
	// swing speed
	public double playermovementmult = 1;

	// ender staff
	public boolean enderstaff = false;

	// forcefield
	public boolean repel = false;
	public double repelforce = 1;
	
	// hook magnet
	public boolean attract = false;
	public double attractradius = 3;
	
	// double hook
	public boolean doublehook = false;
	public boolean smartdoublemotor = true;
	public double angle = 20;
	public double sneakingangle = 10;
	public boolean oneropepull = false;
	
	public GrappleCustomization() {
		
	}
	
	public NBTTagCompound writeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		for (String option : booleanoptions) {
			compound.setBoolean(option, this.getBoolean(option));
		}
		for (String option : doubleoptions) {
			compound.setDouble(option, this.getDouble(option));
		}
		return compound;
	}
	
	public void loadNBT(NBTTagCompound compound) {
		for (String option : booleanoptions) {
			this.setBoolean(option, compound.getBoolean(option));
		}
		for (String option : doubleoptions) {
			this.setDouble(option, compound.getDouble(option));
		}
	}
	
	public void setBoolean(String option, boolean bool) {
		if (option.equals("phaserope")) {this.phaserope = bool;}
		else if (option.equals("motor")) {this.motor = bool;}
		else if (option.equals("motorwhencrouching")) {this.motorwhencrouching = bool;}
		else if (option.equals("motorwhennotcrouching")) {this.motorwhennotcrouching = bool;}
		else if (option.equals("smartmotor")) {this.smartmotor = bool;}
		else if (option.equals("enderstaff")) {this.enderstaff = bool;}
		else if (option.equals("repel")) {this.repel = bool;}
		else if (option.equals("attract")) {this.attract = bool;}
		else if (option.equals("doublehook")) {this.doublehook = bool;}
		else if (option.equals("smartdoublemotor")) {this.smartdoublemotor = bool;}
		else if (option.equals("motordampener")) {this.motordampener = bool;}
		else if (option.equals("reelin")) {this.reelin = bool;}
		else if (option.equals("pullbackwards")) {this.pullbackwards = bool;}
		else if (option.equals("oneropepull")) {this.oneropepull = bool;}
	}
	
	public boolean getBoolean(String option) {
		if (option.equals("phaserope")) {return this.phaserope;}
		else if (option.equals("motor")) {return this.motor;}
		else if (option.equals("motorwhencrouching")) {return this.motorwhencrouching;}
		else if (option.equals("motorwhennotcrouching")) {return this.motorwhennotcrouching;}
		else if (option.equals("smartmotor")) {return this.smartmotor;}
		else if (option.equals("enderstaff")) {return this.enderstaff;}
		else if (option.equals("repel")) {return this.repel;}
		else if (option.equals("attract")) {return this.attract;}
		else if (option.equals("doublehook")) {return this.doublehook;}
		else if (option.equals("smartdoublemotor")) {return this.smartdoublemotor;}
		else if (option.equals("motordampener")) {return this.motordampener;}
		else if (option.equals("reelin")) {return this.reelin;}
		else if (option.equals("pullbackwards")) {return this.pullbackwards;}
		else if (option.equals("oneropepull")) {return this.oneropepull;}
		System.out.println("Option doesn't exist: " + option);
		return false;
	}
	
	public void setDouble(String option, double d) {
		if (option.equals("maxlen")) {this.maxlen = d;}
		else if (option.equals("hookgravity")) {this.hookgravity = d;}
		else if (option.equals("throwspeed")) {this.throwspeed = d;}
		else if (option.equals("motormaxspeed")) {this.motormaxspeed = d;}
		else if (option.equals("motoracceleration")) {this.motoracceleration = d;}
		else if (option.equals("playermovementmult")) {this.playermovementmult = d;}
		else if (option.equals("repelforce")) {this.repelforce = d;}
		else if (option.equals("attractradius")) {this.attractradius = d;}
		else if (option.equals("angle")) {this.angle = d;}
		else if (option.equals("sneakingangle")) {this.sneakingangle = d;}
		else if (option.equals("verticalthrowangle")) {this.verticalthrowangle = d;}
		else if (option.equals("sneakingverticalthrowangle")) {this.sneakingverticalthrowangle = d;}
	}
	
	public double getDouble(String option) {
		if (option.equals("maxlen")) {return maxlen;}
		else if (option.equals("hookgravity")) {return hookgravity;}
		else if (option.equals("throwspeed")) {return throwspeed;}
		else if (option.equals("motormaxspeed")) {return motormaxspeed;}
		else if (option.equals("motoracceleration")) {return motoracceleration;}
		else if (option.equals("playermovementmult")) {return playermovementmult;}
		else if (option.equals("repelforce")) {return repelforce;}
		else if (option.equals("attractradius")) {return attractradius;}
		else if (option.equals("angle")) {return angle;}
		else if (option.equals("sneakingangle")) {return sneakingangle;}
		else if (option.equals("verticalthrowangle")) {return verticalthrowangle;}
		else if (option.equals("sneakingverticalthrowangle")) {return sneakingverticalthrowangle;}
		System.out.println("Option doesn't exist: " + option);
		return 0;
	}
	
	public void writeToBuf(ByteBuf buf) {
		for (String option : booleanoptions) {
			buf.writeBoolean(this.getBoolean(option));
		}
		for (String option : doubleoptions) {
			buf.writeDouble(this.getDouble(option));
		}
	}
	
	public void readFromBuf(ByteBuf buf) {
		for (String option : booleanoptions) {
			this.setBoolean(option, buf.readBoolean());
		}
		for (String option : doubleoptions) {
			this.setDouble(option, buf.readDouble());
		}
	}

	public String getName(String option) {
		if (option.equals("maxlen")) {return "Rope Length";}
		else if (option.equals("phaserope")) {return "Phase Rope";}
		else if (option.equals("hookgravity")) {return "Gravity on hook";}
		else if (option.equals("throwspeed")) {return "Throw Speed";}
		else if (option.equals("reelin")) {return "Crouch to Reel In";}
		else if (option.equals("verticalthrowangle")) {return "Vertical Throw Angle";}
		else if (option.equals("motor")) {return "Motor Enabled";}
		else if (option.equals("motormaxspeed")) {return "Motor Maximum Speed";}
		else if (option.equals("motoracceleration")) {return "Motor Acceleration";}
		else if (option.equals("motorwhencrouching")) {return "Motor when crouching";}
		else if (option.equals("motorwhennotcrouching")) {return "Motor when not crouching";}
		else if (option.equals("smartmotor")) {return "Smart Motor";}
		else if (option.equals("motordampener")) {return "Sideways Motion Dampener";}
		else if (option.equals("pullbackwards")) {return "Pull Backwards";}
		else if (option.equals("playermovementmult")) {return "Swing speed";}
		else if (option.equals("enderstaff")) {return "Ender Staff";}
		else if (option.equals("repel")) {return "Forcefield Enabled";}
		else if (option.equals("repelforce")) {return "Repel Force";}
		else if (option.equals("attract")) {return "Magnet Enabled";}
		else if (option.equals("attractradius")) {return "Attraction Radius";}
		else if (option.equals("doublehook")) {return "Double Hook";}
		else if (option.equals("smartdoublemotor")) {return "Smart Motor";}
		else if (option.equals("angle")) {return "Angle";}
		else if (option.equals("sneakingangle")) {return "Angle when crouching";}
		else if (option.equals("oneropepull")) {return "Allow pulling with one rope";}
		else if (option.equals("sneakingverticalthrowangle")) {return "Crouching Vertical Angle ";}
		return "unknown option";
	}
	
	public String getDescription(String option) {
		if (option.equals("maxlen")) {return "The length of the rope";}
		else if (option.equals("phaserope")) {return "Allows rope to phase through blocks";}
		else if (option.equals("hookgravity")) {return "Gravity on hook when thrown";}
		else if (option.equals("throwspeed")) {return "Speed of hook when thrown";}
		else if (option.equals("reelin")) {return "Before the hook is attached, crouching will stop the hook from moving farther and slowly reel it in";}
		else if (option.equals("verticalthrowangle")) {return "Throws the grappling hook above the crosshairs by this angle";}
		else if (option.equals("motor")) {return "Pulls player towards hook";}
		else if (option.equals("motormaxspeed")) {return "Maximum speed of motor";}
		else if (option.equals("motoracceleration")) {return "Acceleration of motor";}
		else if (option.equals("motorwhencrouching")) {return "Motor is active when crouching";}
		else if (option.equals("motorwhennotcrouching")) {return "Motor is active when crouching";}
		else if (option.equals("smartmotor")) {return "Adjusts motor speed so that player moves towards crosshairs (up/down)";}
		else if (option.equals("motordampener")) {return "Reduces motion perpendicular to the rope so that the rope pulls straighter";}
		else if (option.equals("pullbackwards")) {return "Motor pulls even if you are facing the other way";}
		else if (option.equals("playermovementmult")) {return "Acceleration of player when using movement keys while swinging";}
		else if (option.equals("enderstaff")) {return "Left click launches player forwards";}
		else if (option.equals("repel")) {return "Player is repelled from nearby blocks when swinging";}
		else if (option.equals("repelforce")) {return "Force nearby blocks exert on the player";}
		else if (option.equals("attract")) {return "Hook is attracted to nearby blocks when thrown";}
		else if (option.equals("attractradius")) {return "Radius of attraction";}
		else if (option.equals("doublehook")) {return "Two hooks are thrown at once";}
		else if (option.equals("smartdoublemotor")) {return "Adjusts motor speed so that player moves towards crosshairs (left/right) when used with motor";}
		else if (option.equals("angle")) {return "Angle that each hook is thrown from center";}
		else if (option.equals("sneakingangle")) {return "Angle that each hook is thrown from center when crouching (don't have 'crouch to reel in' enabled if you want to use this)";}
		else if (option.equals("oneropepull")) {return "When motor is enabled and only one hook is attached, activate the motor (if disabled, wait until both hooks are attached before pulling)";}
		else if (option.equals("sneakingverticalthrowangle")) {return "Throws the grappling hook above the crosshairs by this angle when crouching";}
		return "unknown option";
	}
	
	public boolean isoptionvalid(String option) {
		if (option == "motormaxspeed" || option == "motoracceleration" || option == "motorwhencrouching" || option == "motorwhennotcrouching" || option == "smartmotor" || option == "motordampener" || option == "pullbackwards") {
			return this.motor;
		}
		
		else if (option == "sneakingangle") {
			return this.doublehook && !this.reelin;
		}
		
		else if (option == "repelforce") {
			return this.repel;
		}
		
		else if (option == "attractradius") {
			return this.attract;
		}
		
		else if (option == "angle") {
			return this.doublehook;
		}
		
		else if (option == "smartdoublemotor" || option == "oneropepull") {
			return this.doublehook && this.motor;
		}
		
		return true;
	}
}