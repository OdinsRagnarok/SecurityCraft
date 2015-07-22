package org.freeforums.geforce.securitycraft.main;

import org.freeforums.geforce.securitycraft.main.Utils.BlockUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabSCTechnical extends CreativeTabs{
	
	public CreativeTabSCTechnical(){
		super(getNextID(), "tabSecurityCraft");
	}

	
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem(){
		return BlockUtils.getItemFromBlock(mod_SecurityCraft.usernameLogger);
	}
	
	public String getTranslatedTabLabel(){
		return "SecurityCraft: Technical";
		
	}
}
