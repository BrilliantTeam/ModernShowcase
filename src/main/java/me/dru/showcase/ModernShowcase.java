package me.dru.showcase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import me.dru.showcase.utils.ScheduleUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import dr.dru.gui.GUILib;
import me.dru.showcase.block.Showcase;
import me.dru.showcase.config.Config;
import me.dru.showcase.utils.CoreProtectShowcaseAdapter;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;

public class ModernShowcase extends JavaPlugin {
	private static ModernShowcase instance;
	private static HashMap<String,Lang> langs = new HashMap<>();
	private CoreProtectAPI coreProtect;
	public Config config;
	@Override
	public void onEnable() {
		instance = this;
		coreProtect = searchCoreProtect();
		registerConfig();
		registerLangs();
		registerEvents();
		registerCommands();
		registerCrafting();
		registerBstats();
		GUILib.register(this);
		ScheduleUtil.GLOBAL.runTaskTimer(instance, ()-> EventManager.rotate(), config.rotatePeroid, config.rotatePeroid);
		Bukkit.getLogger().info("ModernShowcase is enabled.");
		
	}

	@Override
	public void onDisable() {
		config.save();
		disableInstances();
	}
	
	private void disableInstances() {
		Showcase.rotatesInstance.forEach((b,show)->{
			show.getItemDisplay().setVisibleByDefault(false);
		});
	}

	private void registerConfig() {
		config = new Config();
	}
	
	public static Config Config() {
		return instance.config;
	}

	public static ModernShowcase getInstance() {
		return instance;
	}
	public CoreProtectShowcaseAdapter getCoreProtect() {
		return coreProtect != null ? new CoreProtectShowcaseAdapter(coreProtect) : null;
	}
	private CoreProtectAPI searchCoreProtect() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that CoreProtect is loaded
        if (plugin == null || !(plugin instanceof CoreProtect)) {
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (CoreProtect.isEnabled() == false) {
            return null;
        }

        // Check that a compatible version of the API is loaded
        if (CoreProtect.APIVersion() < 9) {
            return null;
        }
        Bukkit.getLogger().info("[ModerenShowcase] CoreProtect detected, hooked successfully.");
        return CoreProtect;
}
	private void registerBstats() {
		int pluginId = 20936;
        new Metrics(this, pluginId);
	}

	private void registerCommands() {
		getCommand("ModernShowcase").setExecutor(new Commands());
	}

	private void registerEvents() {
		Bukkit.getPluginManager().registerEvents(new EventManager(this), this);
	}
	
	private List<Recipe> glassRecipes = new ArrayList<>();
	private static HashMap<Material, Function<Lang,String>> glasses = new HashMap<>();
	private void registerCrafting() {
		glasses.put(Material.GLASS, lang->lang.glass);
		glasses.put(Material.TINTED_GLASS, lang->lang.TINTED_GLASS);
		glasses.put(Material.WHITE_STAINED_GLASS, lang->lang.WHITE_STAINED_GLASS);
		glasses.put(Material.LIGHT_GRAY_STAINED_GLASS, lang->lang.LIGHT_GRAY_STAINED_GLASS);
		glasses.put(Material.GRAY_STAINED_GLASS, lang->lang.GRAY_STAINED_GLASS);
		glasses.put(Material.BLACK_STAINED_GLASS, lang->lang.BLACK_STAINED_GLASS);
		glasses.put(Material.BROWN_STAINED_GLASS, lang->lang.BROWN_STAINED_GLASS);
		glasses.put(Material.RED_STAINED_GLASS, lang->lang.RED_STAINED_GLASS);
		glasses.put(Material.ORANGE_STAINED_GLASS, lang->lang.ORANGE_STAINED_GLASS);
		glasses.put(Material.YELLOW_STAINED_GLASS, lang->lang.YELLOW_STAINED_GLASS);
		glasses.put(Material.LIME_STAINED_GLASS, lang->lang.LIME_STAINED_GLASS);
		glasses.put(Material.GREEN_STAINED_GLASS, lang->lang.GREEN_STAINED_GLASS);
		glasses.put(Material.CYAN_STAINED_GLASS, lang->lang.CYAN_STAINED_GLASS);
		glasses.put(Material.BLUE_STAINED_GLASS, lang->lang.BLUE_STAINED_GLASS);
		glasses.put(Material.LIGHT_BLUE_STAINED_GLASS, lang->lang.LIGHT_BLUE_STAINED_GLASS);
		glasses.put(Material.PURPLE_STAINED_GLASS, lang->lang.PURPLE_STAINED_GLASS);
		glasses.put(Material.MAGENTA_STAINED_GLASS, lang->lang.MAGENTA_STAINED_GLASS);
		glasses.put(Material.PINK_STAINED_GLASS, lang->lang.PINK_STAINED_GLASS);

		Lang l = ModernShowcase.langs.get("en_us");
		glasses.forEach((type,name)->{
			ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, type.name().toLowerCase()+"_showcase"), getShowcaseItem(l, type));
			recipe.shape("aaa","aba","aaa");
			recipe.setIngredient('a', type);
			recipe.setIngredient('b', Material.ITEM_FRAME);
			Bukkit.addRecipe(recipe);
			glassRecipes.add(recipe);
		});
		
	}
	
	public List<Recipe> getGlassRecipes() {
		return glassRecipes;
	}

	private String glassName(Lang lang, Material type) {
		return glasses.get(type).apply(lang);
	}
	
	public ItemStack getShowcaseItem(Lang lang,Material type) {
		ItemStack is = GUILib.getItem(type, ChatColor.AQUA+""+ ChatColor.BOLD+ glassName(lang,type)+lang.showcase, 1);
		ItemMeta im = is.getItemMeta();
		im.setLore(lang.showcaseDesc);
		im.getPersistentDataContainer().set(new NamespacedKey(this, "ModernShowcase"), PersistentDataType.BOOLEAN, true);
		
		is.setItemMeta(im);
		return is;
	}

	public static Lang getLang(Player player) {
		return langs.getOrDefault(player.getLocale(),langs.get("en_us"));
	}

	private void registerLangs() {
		File folder= new File("plugins/ModernShowcase/Langs");
		if(folder.exists()&&folder.listFiles().length>0) {
			for(File lang : folder.listFiles()) {
				String file = lang.getName().replace(".yml", "");
				langs.put(file, new Lang(file));
			}
		} else {
			Lang zh_tw = new Lang("zh_tw");
			zh_tw.settingTitle = "展示櫃設定";
			zh_tw.scale = "§f§l大小縮放";
			zh_tw.toggleFixed = "§f§l切換固定顯示";
			zh_tw.toggleGlass = "§f§l切換玻璃顯示";
			zh_tw.yawRotate = "§f§l水平旋轉（X 軸）";
			zh_tw.pitchRotate = "§f§l垂直旋轉（Y 軸）";
			zh_tw.auto_rotate = "§f§l自動旋轉速度";
			zh_tw.displayitems = "§f§l展示物品";
			zh_tw.current = "§6當前設定：{0}";
			zh_tw.desc = Arrays.asList(
				    zh_tw.current,
				    ChatColor.GRAY+"    ",
				    ChatColor.GRAY+"    右鍵 + 1ﾟ    ",
				    ChatColor.GRAY+"    左鍵 - 1ﾟ    ",                   
				    ChatColor.GRAY+"    Shift 右鍵 + 10ﾟ    ",                  
				    ChatColor.GRAY+"    Shift 左鍵 - 10ﾟ    ",
				    ChatColor.GRAY+"    ");
			zh_tw.scaleDesc = Arrays.asList(
				    zh_tw.current,
				    ChatColor.GRAY+"    ",
				    ChatColor.GRAY+"    右鍵 §7+ 1    ",
				    ChatColor.GRAY+"    左鍵 §7- 1    ",                   
				    ChatColor.GRAY+"    ");
			zh_tw.enable = "§a啟用";
			zh_tw.disable = "§c停用";

			zh_tw.showcase = "展示櫃"; 

			zh_tw.showcaseDesc = Arrays.asList(
					"§7使用說明：",
					"§7    ",
					"§7    §8空手 §7+ §6右鍵§7：§f查看展示物品  ",
					"§7    §e蹲下 §7+ §6右鍵§7：§f編輯展示櫃設定    ",
					"§7    ");
			zh_tw.glass = "玻璃"; 
			zh_tw.TINTED_GLASS= "遮光玻璃";
			zh_tw.WHITE_STAINED_GLASS= "白色玻璃";
			zh_tw.LIGHT_GRAY_STAINED_GLASS= "淺灰色玻璃";
			zh_tw.GRAY_STAINED_GLASS="灰色玻璃";
			zh_tw.BLACK_STAINED_GLASS="黑色玻璃";
			zh_tw.BROWN_STAINED_GLASS="棕色玻璃";
			zh_tw.RED_STAINED_GLASS="紅色玻璃";
			zh_tw.ORANGE_STAINED_GLASS="橘色玻璃";
			zh_tw.YELLOW_STAINED_GLASS="黃色玻璃";
			zh_tw.LIME_STAINED_GLASS= "淺綠色玻璃";
			zh_tw.GREEN_STAINED_GLASS="綠色玻璃";
			zh_tw.CYAN_STAINED_GLASS="青色玻璃";
			zh_tw.BLUE_STAINED_GLASS="藍色玻璃";
			zh_tw.LIGHT_BLUE_STAINED_GLASS="淺藍色玻璃";
			zh_tw.PURPLE_STAINED_GLASS="紫色玻璃";
			zh_tw.MAGENTA_STAINED_GLASS="洋紅色玻璃";
			zh_tw.PINK_STAINED_GLASS="粉色玻璃";
			zh_tw.placedLimitReach = "此區塊的展示櫃數量已到達上限！";
			zh_tw.itemBlacklist = "你沒有足夠權限放入此物品！";
			zh_tw.slotLimitReach = "你不能在展示櫃裡放入超過{0}個物品！";
			zh_tw.save();
			
			Lang zh_cn = new Lang("zh_cn");
			zh_cn.settingTitle = "展示柜设定";
			zh_cn.scale = "§f§l大小缩放";
			zh_cn.toggleFixed = "§f§l切换固定显示";
			zh_cn.toggleGlass = "§f§l切换玻璃显示";
			zh_cn.yawRotate = "§f§l水平旋转（X 轴）";
			zh_cn.pitchRotate = "§f§l垂直旋转（Y 轴）";
			zh_cn.auto_rotate = "§f§l自动旋转速度";
			zh_cn.displayitems = "§f§l显示项目";
			zh_cn.current = "§6当前设定：{0}";
			zh_cn.desc = Arrays.asList(
					zh_cn.current,
				    ChatColor.GRAY+"    ",
				    ChatColor.GRAY+"    右键 + 1ﾟ    ",
				    ChatColor.GRAY+"    左键 - 1ﾟ    ",                   
				    ChatColor.GRAY+"    Shift 右键 + 10ﾟ    ",                  
				    ChatColor.GRAY+"    Shift 左键 - 10ﾟ    ",
				    ChatColor.GRAY+"    ");
			zh_cn.scaleDesc = Arrays.asList(
					zh_cn.current,
				    ChatColor.GRAY+"    ",
				    ChatColor.GRAY+"    右键 §7+ 1    ",
				    ChatColor.GRAY+"    左键 §7- 1    ",                   
				    ChatColor.GRAY+"    ");
			zh_cn.enable = "§a启用";
			zh_cn.disable = "§c停用";

			zh_cn.showcase = "展示柜"; 
		
			zh_cn.showcaseDesc = Arrays.asList(
					"§7使用说明：",
					"§7    ",
					"§7    §8空手 §7+ §6右键§7：§f查看展示物品  ",
					"§7    §e蹲下 §7+ §6右键§7：§f编辑展示柜设定    ",
					"§7    ");
			zh_cn.glass = "玻璃"; 
			zh_cn.TINTED_GLASS= "遮光玻璃";
			zh_cn.WHITE_STAINED_GLASS= "白色玻璃";
			zh_cn.LIGHT_GRAY_STAINED_GLASS= "浅灰色玻璃";
			zh_cn.GRAY_STAINED_GLASS="灰色玻璃";
			zh_cn.BLACK_STAINED_GLASS="黑色玻璃";
			zh_cn.BROWN_STAINED_GLASS="棕色玻璃";
			zh_cn.RED_STAINED_GLASS="红色玻璃";
			zh_cn.ORANGE_STAINED_GLASS="橘色玻璃";
			zh_cn.YELLOW_STAINED_GLASS="黄色玻璃";
			zh_cn.LIME_STAINED_GLASS= "浅绿色玻璃";
			zh_cn.GREEN_STAINED_GLASS="绿色玻璃";
			zh_cn.CYAN_STAINED_GLASS="青色玻璃";
			zh_cn.BLUE_STAINED_GLASS="蓝色玻璃";
			zh_cn.LIGHT_BLUE_STAINED_GLASS="浅蓝色玻璃";
			zh_cn.PURPLE_STAINED_GLASS="紫色玻璃";
			zh_cn.MAGENTA_STAINED_GLASS="洋红色玻璃";
			zh_cn.PINK_STAINED_GLASS="粉色玻璃";
			zh_cn.placedLimitReach = "此区块的展示柜数量已到达上限";
			zh_cn.itemBlacklist = "你没有足够权限放入此物件！";
			zh_cn.slotLimitReach = "你不能在展示櫃裡放入超過{0}個物品！";
			zh_cn.save();
			langs.put("en_us", new Lang("en_us"));
			langs.put("zh_tw", zh_tw);
			langs.put("zh_cn", zh_cn);
		}

	}
}
