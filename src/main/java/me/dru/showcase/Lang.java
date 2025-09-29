package me.dru.showcase;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.chat.BaseComponent;

public class Lang { 
	public String defaultLang = "en_us";
	public String settingTitle = "Showcase settings";
	public String scale = "§r§fScale Size";
	public String current = ChatColor.GOLD+"Current: {0}";
	public String toggleFixed = "§rToggle Fixed Display";
	public String toggleGlass = "§rToggle Glass Display";
	public String yawRotate = "§r§fYaw Direction";
	public String pitchRotate = "§r§fPitch Direction";
	public String zRotate = "§r§fRoll Direction";
	public String auto_rotate = "§r§fAuto Rotation Speed";
	public String displayitems = "§r§fDisplay Items";

	public List<String> desc = Arrays.asList(
			current,
			" ",
			ChatColor.GRAY+"Right click to +1ﾟ",
			ChatColor.GRAY+"Left click to -1ﾟ",
			ChatColor.GRAY+"Hold Shift = 10ﾟ");
	public List<String> scaleDesc = Arrays.asList(
			current,
			" ",
			ChatColor.GRAY+"Right click to +1",
			ChatColor.GRAY+"Left click to -1");
	public String enable = ChatColor.GREEN+ "Enable";
	public String disable = ChatColor.RED+ "Disable";
	
	public String showcase = " Showcase"; 
	public List<String> showcaseDesc = Arrays.asList(
			ChatColor.GRAY+ "Right click to swap/preview item", 
			ChatColor.GRAY+ "Shift + Right Click to edit settings");
	public String glass = "Glass"; 
	public String TINTED_GLASS= "Tinted Glass";
	public String WHITE_STAINED_GLASS= "White Glass";
	public String LIGHT_GRAY_STAINED_GLASS= "Light Gray Glass";
	public String GRAY_STAINED_GLASS="Gray Glass";
	public String BLACK_STAINED_GLASS="Black Glass";
	public String BROWN_STAINED_GLASS="Brown Glass";
	public String RED_STAINED_GLASS="Red Glass";
	public String ORANGE_STAINED_GLASS="Orange Glass";
	public String YELLOW_STAINED_GLASS="Yellow Glass";
	public String LIME_STAINED_GLASS= "Lime Glass";
	public String GREEN_STAINED_GLASS="Green Glass";
	public String CYAN_STAINED_GLASS="Cyan Glass";
	public String BLUE_STAINED_GLASS="Blue Glass";
	public String LIGHT_BLUE_STAINED_GLASS = "Light Blue Glass";
	public String PURPLE_STAINED_GLASS="Purple Glass";
	public String MAGENTA_STAINED_GLASS="Magenta Glass";
	public String PINK_STAINED_GLASS="Pink Glass";

	public String placedLimitReach = "The showcase limit for this chunk has been reached!";
	public String itemBlacklist = "You don't have permission to put in this item!";
	public String slotLimitReach = "You can't put more than {0} items in the showcase!";
	public String noPlacePerms = "You don't have permission to place item showcase!";
	
	private File file;
	private FileConfiguration config;
	public Lang(String lang_code) {
		file = new File("plugins/ModernShowcase/Langs/"+lang_code+".yml");
		config = YamlConfiguration.loadConfiguration(file);
		settingTitle = config.getString("showcase_settings", settingTitle);
		scale = config.getString("sacle_size",scale);
		current  = config.getString("current_value",current);
		toggleFixed  = config.getString("toggle_fixed",toggleFixed);
		toggleGlass  = config.getString("toggle_glass",toggleGlass);
		yawRotate  = config.getString("yaw_direction",yawRotate);
		pitchRotate  = config.getString("pitch_direction",pitchRotate);
		zRotate  = config.getString("roll_direction",zRotate);
		auto_rotate  = config.getString("auto-rotation_speed",auto_rotate);
		displayitems = config.getString("display_items",displayitems);
		if(config.contains("adjust_description"))
			desc  = config.getStringList("adjust_description");
		if(config.contains("scale_description"))
			scaleDesc  = config.getStringList("scale_description");
		
		enable  = config.getString("enable",enable);
		disable  = config.getString("disable", disable);

		showcase  = config.getString("showcase",showcase); 
		if(config.contains("showcase_description"))
			showcaseDesc  = config.getStringList("showcase_description");
		
		glass  = config.getString("glass",glass); 
		TINTED_GLASS = config.getString("tinted_glass",TINTED_GLASS);
		WHITE_STAINED_GLASS = config.getString("white_glass",WHITE_STAINED_GLASS);
		LIGHT_GRAY_STAINED_GLASS = config.getString("light_gray_glass",LIGHT_GRAY_STAINED_GLASS);
		GRAY_STAINED_GLASS=config.getString("gray_glass",GRAY_STAINED_GLASS);
		BLACK_STAINED_GLASS=config.getString("black_glass",BLACK_STAINED_GLASS);
		BROWN_STAINED_GLASS=config.getString("brown_glass",BROWN_STAINED_GLASS);
		RED_STAINED_GLASS=config.getString("red_glass",RED_STAINED_GLASS);
		ORANGE_STAINED_GLASS=config.getString("orange_glass",ORANGE_STAINED_GLASS);
		YELLOW_STAINED_GLASS=config.getString("yellow_glass",YELLOW_STAINED_GLASS);
		LIME_STAINED_GLASS = config.getString("lime_glass",LIME_STAINED_GLASS);
		GREEN_STAINED_GLASS=config.getString("green_glass",GREEN_STAINED_GLASS);
		CYAN_STAINED_GLASS=config.getString("cyan_glass",CYAN_STAINED_GLASS);
		BLUE_STAINED_GLASS=config.getString("blue_glass",BLUE_STAINED_GLASS);
		LIGHT_BLUE_STAINED_GLASS = config.getString("light_blue_glass",LIGHT_BLUE_STAINED_GLASS);
		PURPLE_STAINED_GLASS=config.getString("purple_glass",PURPLE_STAINED_GLASS);
		MAGENTA_STAINED_GLASS=config.getString("magenta_glass",MAGENTA_STAINED_GLASS);
		PINK_STAINED_GLASS=config.getString("pink_glass",PINK_STAINED_GLASS);
		
		placedLimitReach = config.getString("placed_limit_reach",placedLimitReach);
		itemBlacklist = config.getString("item_blacklist",itemBlacklist);
		slotLimitReach = config.getString("slot_limit_reach", slotLimitReach);
		noPlacePerms = config.getString("no_place_permission",noPlacePerms);
		save();
	}

	public void save() {
		config.set("showcase_settings", settingTitle);
		config.set("display_items", displayitems);
		config.set("sacle_size",scale);
		config.set("current_value",current);
		config.set("toggle_fixed",toggleFixed);
		config.set("toggle_glass",toggleGlass);
		config.set("yaw_direction",yawRotate);
		config.set("pitch_direction",pitchRotate);
		config.set("roll_direction",zRotate);
		config.set("auto-rotation_speed",auto_rotate);
		config.set("adjust_description",desc);
		config.set("scale_description",scaleDesc );
		
		config.set("enable",enable);
		config.set("disable", disable);
		
		config.set("showcase",showcase); 
		config.set("showcase_description",showcaseDesc);
		
		config.set("glass",glass); 
		config.set("tinted_glass",TINTED_GLASS);
		config.set("white_glass",WHITE_STAINED_GLASS);
		config.set("light_gray_glass",LIGHT_GRAY_STAINED_GLASS);
		config.set("gray_glass",GRAY_STAINED_GLASS);
		config.set("black_glass",BLACK_STAINED_GLASS);
		config.set("brown_glass",BROWN_STAINED_GLASS);
		config.set("red_glass",RED_STAINED_GLASS);
		config.set("orange_glass",ORANGE_STAINED_GLASS);
		config.set("yellow_glass",YELLOW_STAINED_GLASS);
		config.set("lime_glass",LIME_STAINED_GLASS);
		config.set("green_glass",GREEN_STAINED_GLASS);
		config.set("cyan_glass",CYAN_STAINED_GLASS);
		config.set("blue_glass",BLUE_STAINED_GLASS);
		config.set("light_blue_glass",LIGHT_BLUE_STAINED_GLASS);
		config.set("purple_glass",PURPLE_STAINED_GLASS);
		config.set("magenta_glass",MAGENTA_STAINED_GLASS);
		config.set("pink_glass",PINK_STAINED_GLASS);

		config.set("placed_limit_reach", placedLimitReach);
		config.set("item_blacklist", itemBlacklist);
		config.set("slot_limit_reach", slotLimitReach);
		config.set("no_place_permission", noPlacePerms);
		
		try {
			config.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
