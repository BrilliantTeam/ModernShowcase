package me.dru.showcase;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.dru.showcase.block.Showcase;

public class Commands implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		sender.sendMessage(ChatColor.AQUA+ "ModernShowcase, Made by Dru_TNT");
		sender.sendMessage(ChatColor.AQUA+ "Join our DC for more support: https://discord.gg/9c287zPpUZ");
		return true;
	}

}
