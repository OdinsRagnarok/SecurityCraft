package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.blocks.BlockRetinalScanner;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;


public class TileEntityRetinalScanner extends CustomizableSCTE {
	
	private OptionBoolean activatedByEntities = new OptionBoolean("activatedByEntities", false);

	public void entityViewed(EntityLivingBase entity){
		if(!worldObj.isRemote && !BlockUtils.getBlockPropertyAsBoolean(worldObj, pos, BlockRetinalScanner.POWERED)){
			if(!(entity instanceof EntityPlayer) && !activatedByEntities.asBoolean()) return;
			
			if(entity instanceof EntityPlayer && !getOwner().isOwner((EntityPlayer) entity)) {
                PlayerUtils.sendMessageToPlayer((EntityPlayer) entity, I18n.translateToLocal("tile.retinalScanner.name"), I18n.translateToLocal("messages.retinalScanner.notOwner").replace("#", getOwner().getName()), TextFormatting.RED);
				return;
			}
			
			BlockUtils.setBlockProperty(worldObj, pos, BlockRetinalScanner.POWERED, true);
    		worldObj.scheduleUpdate(new BlockPos(pos), mod_SecurityCraft.retinalScanner, 60);
    		
            if(entity instanceof EntityPlayer){
                PlayerUtils.sendMessageToPlayer((EntityPlayer) entity, I18n.translateToLocal("tile.retinalScanner.name"), I18n.translateToLocal("messages.retinalScanner.hello").replace("#", entity.getName()), TextFormatting.GREEN);
            }             
    	}
	}
	
	public int getViewCooldown() {
    	return 30;
    }
	
	public boolean activatedOnlyByPlayer() {
    	return !activatedByEntities.asBoolean();
    }

	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST};
	}

	public Option<?>[] customOptions() {
		return new Option[]{ activatedByEntities };
	}

}
