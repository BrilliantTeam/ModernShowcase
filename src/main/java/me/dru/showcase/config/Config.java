package me.dru.showcase.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
	public float sizeInterval;
	public int maxSize, minSize;
	File file;
	FileConfiguration con;
	public Config() {
		load();
	}
	
	public void load() {
		file = new File("plugins/ModernShowcase/Config.yml");
		con = YamlConfiguration.loadConfiguration(file);
		sizeInterval = (float) con.getDouble("size-interval",8);
		maxSize = con.getInt("max-size", 10);
		minSize = con.getInt("min-size", 1);
	}
	
	public void save() {
		load();
		con.set("size-interval", sizeInterval);
		con.set("max-size", maxSize);
		con.set("min-size", minSize);
		try {
			con.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
