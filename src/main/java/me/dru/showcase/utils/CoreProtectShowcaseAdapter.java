package me.dru.showcase.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import me.dru.showcase.ModernShowcase;
import me.dru.showcase.config.Config;
import net.coreprotect.CoreProtectAPI;
import net.coreprotect.config.ConfigHandler;
import net.coreprotect.model.BlockGroup;

public class CoreProtectShowcaseAdapter {
	CoreProtectAPI coreProtect;
	
	Config config;
	
	public CoreProtectShowcaseAdapter(CoreProtectAPI coreProtect) {
		this.coreProtect = coreProtect;
		this.config = ModernShowcase.Config();
		if(!config.enableCoreProtectLog||coreProtect==null)
			return;
	    BlockGroup.CONTAINERS.add(Material.GLASS);
	    BlockGroup.CONTAINERS.add(Material.TINTED_GLASS);
	    BlockGroup.CONTAINERS.add(Material.WHITE_STAINED_GLASS);
	    BlockGroup.CONTAINERS.add(Material.LIGHT_GRAY_STAINED_GLASS);
	    BlockGroup.CONTAINERS.add(Material.GRAY_STAINED_GLASS);
	    BlockGroup.CONTAINERS.add(Material.BLACK_STAINED_GLASS);
	    BlockGroup.CONTAINERS.add(Material.BROWN_STAINED_GLASS);
	    BlockGroup.CONTAINERS.add(Material.RED_STAINED_GLASS);
	    BlockGroup.CONTAINERS.add(Material.ORANGE_STAINED_GLASS);
	    BlockGroup.CONTAINERS.add(Material.YELLOW_STAINED_GLASS);
	    BlockGroup.CONTAINERS.add(Material.LIME_STAINED_GLASS);
	    BlockGroup.CONTAINERS.add(Material.GREEN_STAINED_GLASS);
	    BlockGroup.CONTAINERS.add(Material.CYAN_STAINED_GLASS);
	    BlockGroup.CONTAINERS.add(Material.BLUE_STAINED_GLASS);
	    BlockGroup.CONTAINERS.add(Material.LIGHT_BLUE_STAINED_GLASS);
	    BlockGroup.CONTAINERS.add(Material.PURPLE_STAINED_GLASS);
	    BlockGroup.CONTAINERS.add(Material.MAGENTA_STAINED_GLASS);
	    BlockGroup.CONTAINERS.add(Material.GREEN_STAINED_GLASS);
	    BlockGroup.CONTAINERS.add(Material.PINK_STAINED_GLASS);
	    
	}


	public boolean isInspecting(Player player) {
		if(coreProtect==null)
			return false;
		return Boolean.TRUE.equals(ConfigHandler.inspecting.get(player.getName()));
	}
	public void logShowcase(Player player,Block block,ItemStack from, @Nullable ItemStack to) {
		if(coreProtect==null||!config.enableCoreProtectLog)
			return;
		Material type = block.getType();
		block.setType(Material.CHEST,false);							
		Inventory inv = ((Chest)block.getState()).getInventory();
		if(from!=null)
			inv.addItem(from);										 
		coreProtect.logContainerTransaction(player.getName(), inv.getLocation());
		inv.clear();
		block.setType(type,false);
		if(to!=null)
			inv.addItem(to);
	}
}
