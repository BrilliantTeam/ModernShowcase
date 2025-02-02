package me.dru.showcase.config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
	public float sizeInterval;
	public int maxSize, minSize,maxRotateSpeed;
	public int placedPerChunkLimit;
	public int rotatePeroid;
	public int itemSlotLimit;
	public List<Material> blackLists;
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
		maxRotateSpeed = con.getInt("max-rotate-speed", 25);
		rotatePeroid = con.getInt("rotate-ticks-peroid", 2);
		placedPerChunkLimit = con.getInt("placed-limit-per-chunk",-1);
		itemSlotLimit = Math.min(con.getInt("showcase-slots-limit",9), 9);
		if(con.contains("blackitems"))
			blackLists = con.getStringList("blackitems").stream()
				.map(item->Material.matchMaterial(item.toString().toUpperCase())).toList();
		else 
			blackLists = Arrays.asList(Material.STRUCTURE_VOID,Material.JIGSAW);
		
			
		save(false);
	}

	public void save() {
		save(true);
	}
	public void save(boolean reload) {
		if(reload)
			load();
		con.set("size-interval", sizeInterval);
		con.set("max-size", maxSize);
		con.set("min-size", minSize);
		con.set("placed-limit-per-chunk", placedPerChunkLimit);
		con.set("max-rotate-speed", maxRotateSpeed);
		con.set("rotate-ticks-peroid", rotatePeroid);
		con.set("showcase-slots-limit", itemSlotLimit);
		con.set("blackitems", blackLists.stream().map(s->s.name()).collect(Collectors.toList()));
		try {
			con.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
