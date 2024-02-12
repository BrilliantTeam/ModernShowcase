package me.dru.showcase.utils;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.coreprotect.CoreProtectAPI;
import net.coreprotect.config.Config;
import net.coreprotect.config.ConfigHandler;
import net.coreprotect.database.Database;
import net.coreprotect.database.lookup.ChestTransactionLookup;
import net.coreprotect.language.Phrase;
import net.coreprotect.model.BlockGroup;
import net.coreprotect.utility.Chat;
import net.coreprotect.utility.Color;

public class CoreProtectShowcaseAdapter {
	CoreProtectAPI coreProtect;
	
	public CoreProtectShowcaseAdapter(CoreProtectAPI coreProtect) {
		this.coreProtect = coreProtect;
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
		return Boolean.TRUE.equals(ConfigHandler.inspecting.get(player.getName()));
	}
	public void logShowcase(Player player,Chest chest,ItemStack from, @Nullable ItemStack to) {
		if(coreProtect==null)
			return;
		Inventory inv = chest.getInventory();
		inv.addItem(from);
		coreProtect.logContainerTransaction(player.getName(), inv.getLocation());
		inv.remove(from);
		inv.addItem(to);
	}
}
