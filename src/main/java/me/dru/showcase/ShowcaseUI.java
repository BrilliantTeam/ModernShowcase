package me.dru.showcase;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.sonatype.inject.EagerSingleton;

import dr.dru.gui.GUI;
import dr.dru.gui.GUILib;
import dr.dru.gui.component.icon.Icon;
import dr.dru.gui.component.icon.StatefulIcon;
import dr.dru.gui.component.panel.ButtonPanel;
import dr.dru.gui.component.panel.IntegerLeverPanel;
import dr.dru.gui.component.position.UIRegion;
import me.dru.showcase.block.Showcase;
import me.dru.showcase.config.Config;
import me.dru.showcase.utils.CoreProtectShowcaseAdapter;
import me.dru.showcase.utils.ScheduleUtil;

public class ShowcaseUI {
	
	public static void open(Player p, Showcase showcase) {
		Lang lang = ModernShowcase.getLang(p);
		GUI g = new GUI(lang.settingTitle, 18);
		Config config = ModernShowcase.Config();
		/*
		if(showcase.getItemDisplay()==null) {
			showcase.despawn();
			return;
		}*/
		
		g.addPanel(UIRegion.single(1,0), new IntegerLeverPanel(new StatefulIcon(pp->{
			return GUILib.getItem(Material.PUFFERFISH, lang.scale, showcase.getSize(), lang.scaleDesc);
		}), showcase.getSize(), config.minSize, config.maxSize, value->showcase.setSize(value)));

		g.addPanel(UIRegion.single(2,0), new ButtonPanel(new StatefulIcon(pp->GUILib.getItem(Material.COMPASS,lang.toggleFixed,1,lang.current.replace("{0}", showcase.isFixedBillboard() ? lang.enable : lang.disable))), e->{
			showcase.togglgBillboard();
			e.gui.render(p);
		}));
		
		/*
		g.addPanel(UIRegion.single(3,0), new ButtonPanel(Icon.of(Material.GLOWSTONE_DUST, lang.displayitems,1,lang.current.replace("{0}", ""+showcase.getItemDisplays().count())), 
				e->{
					setItems(p,showcase);
				}));
		*/
		g.addPanel(UIRegion.single(4,0), new ButtonPanel(Icon.of(Material.ITEM_FRAME, lang.displayitems,1,lang.current.replace("{0}", ""+showcase.getItemDisplays().count())), 
			e->{
				setItems(p,showcase);
			}));
		
		g.addPanel(UIRegion.single(5,0), new IntegerLeverPanel(new StatefulIcon(pp->{
			return GUILib.getItem(Material.ARROW, lang.yawRotate, (int)Math.max(1, Math.abs(showcase.getYawRotation())/10), lang.desc);
		}), (int)showcase.getYawRotation(), -360, 360, value->showcase.setYawRotation(value)));
		
		g.addPanel(UIRegion.single(6,0), new IntegerLeverPanel(new StatefulIcon(pp->{
			return GUILib.getItem(Material.ARROW, lang.pitchRotate, (int)Math.max(1, Math.abs(showcase.getPitchRotation())/10), lang.desc);
		}), (int)showcase.getPitchRotation(), -180, 180, value->showcase.setPitchRotation(value)));
		
		g.addPanel(UIRegion.single(7,0), new IntegerLeverPanel(new StatefulIcon(pp->{
			return GUILib.getItem(Material.ARROW, lang.zRotate, (int)Math.max(1, Math.abs(showcase.getRollRotation())/10), lang.desc);
		}), (int)showcase.getRollRotation(), -360, 360, value->showcase.setRollRotation(value)));
		
		
		g.addPanel(UIRegion.single(1,1), new IntegerLeverPanel(new StatefulIcon(pp->{
			return GUILib.getItem(Material.POWERED_RAIL, lang.auto_rotate, (int)Math.max(1, showcase.getAutoRotateSpeed()*10f), lang.desc);
		}), (int)(showcase.getAutoRotateSpeed()*10f), -config.maxRotateSpeed, config.maxRotateSpeed, value->showcase.setAutoRotateSpeed(value/10f)));


		g.addPanel(UIRegion.single(2,1), new ButtonPanel(new StatefulIcon(pp->GUILib.getItem(Material.GLASS,lang.toggleGlass,1,lang.current.replace("{0}", !showcase.hideGlass() ? lang.enable : lang.disable))), e->{
			showcase.togglgGlass();
			e.gui.render(p);
		}));

		/*
		g.addPanel(UIRegion.single(3,1), new ButtonPanel(new StatefulIcon(pp->GUILib.getItem(Material.COMPARATOR,lang.toggleGlass,1,lang.current.replace("{0}", !showcase.hideGlass() ? lang.enable : lang.disable))), e->{
			showcase.togglgGlass();
			e.gui.render(p);
		}));*/
		g.addPanel(UIRegion.single(4,1), new ButtonPanel(new StatefulIcon(pp->GUILib.getItem(Material.IRON_DOOR,lang.canPreview,1,lang.current.replace("{0}", showcase.canPreivew() ? lang.enable : lang.disable))), e->{
			showcase.setCanPreivew(!showcase.canPreivew());
			e.gui.render(p);
		}));
		int maxOffest = (int)(config.maxOffest);
		g.addPanel(UIRegion.single(5,1), new IntegerLeverPanel(new StatefulIcon(pp->{
			return GUILib.getItem(Material.LEVER, lang.xOffest, (int)Math.max(1, Math.abs(showcase.getXOffest()*100)), lang.scaleDesc);
		}), (int)(showcase.getXOffest()*100), (int)-maxOffest, (int)maxOffest, value->showcase.setXOffest(value/100f)));
		
		g.addPanel(UIRegion.single(6,1), new IntegerLeverPanel(new StatefulIcon(pp->{
			return GUILib.getItem(Material.LEVER, lang.yOffest, (int)Math.max(1, Math.abs(showcase.getYOffest()*100)), lang.scaleDesc);
		}), (int)(showcase.getYOffest()*100), (int)-maxOffest, (int)maxOffest, value->showcase.setYOffest(value/100f)));
		
		g.addPanel(UIRegion.single(7,1), new IntegerLeverPanel(new StatefulIcon(pp->{
			return GUILib.getItem(Material.LEVER, lang.zOffest, (int)Math.max(1, Math.abs(showcase.getZOffest()*100)), lang.scaleDesc);
		}), (int)(showcase.getZOffest()*100), (int)-maxOffest, (int)maxOffest, value->showcase.setZOffest(value/100f)));
		
		

		
		
		
		g.open(p);
	}
	
	private static void setItems(Player p, Showcase showcase) {
		Lang lang = ModernShowcase.getLang(p);
		Config config = ModernShowcase.Config();
		GUI g = new GUI(lang.showcase, 9);
		for(int i=0;i<9;i++) {
			final int idx = i;
			g.setIcon(i, new StatefulIcon(pp->{
				
				ItemStack item = showcase.getItem(idx);
				if(item!=null) 
					return item;
				else
					return GUILib.getItem(Material.STONE, lang.showcase, 0);
			})).setClickListener(e->{
				if(e.inventoryClickEvent.getCursor()!=null&&
						config.blackLists.contains(e.inventoryClickEvent.getCursor().getType())) {
					p.sendMessage(lang.itemBlacklist);
					return;
				}

				if(showcase.getBlock().getType()==Material.CHEST)
					return; 
				ItemStack inCase = showcase.getItem(idx);
				long slots = showcase.getItemDisplays().count();
				if((e.inventoryClickEvent.getCursor()==null||e.inventoryClickEvent.getCursor().getAmount()==0)) { //no item in cursor
					if(slots==1) {
						return;
					} 	
				} else if (inCase==null){ //have item in cursor && put new item
					if(slots>=config.itemSlotLimit) {
						p.sendMessage(lang.slotLimitReach.replace("{0}", ""+config.itemSlotLimit));
						return;
					}
				}
				
				if(config.enableCoreProtectLog) {
					ModernShowcase.getInstance().getCoreProtect().logShowcase(p, showcase.getBlock(),
							showcase.getItem(idx), e.inventoryClickEvent.getCursor().clone());
				}
				showcase.setItem(idx, e.inventoryClickEvent.getCursor());
				
				if(inCase!=null&&!inCase.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(ModernShowcase.getInstance(), "Temp"),PersistentDataType.BOOLEAN)) {
					e.inventoryClickEvent.setCursor(inCase);
				} else {
					e.inventoryClickEvent.setCursor(null);
				}
				e.gui.render(p);	
			});
		}
		g.open(p);
	}

	private static HashSet<Inventory> showcases = new HashSet<>();
	public static void preview(Player p, Showcase showcase) {
		if(showcase.getItemHolder()==null) {
			showcase.despawn();
			return;
		}
		Inventory inv = Bukkit.createInventory(null,InventoryType.DISPENSER," ");
		if(ScheduleUtil.isFolia())
			moveToPlayer(inv, p);
		for(int i=0;i<9;i++) {
			ItemStack item = showcase.getItem(i);
			if(item!=null)
				inv.setItem(i, item);
		}
		showcases.add(inv);
		p.openInventory(inv);
	}

	private static void moveToPlayer(Inventory inv, Player p) {
		try {
			Block b = p.getLocation().getBlock();
			Field position = Class.forName("net.minecraft.world.level.block.entity.BlockEntity").getDeclaredField("worldPosition");
			position.setAccessible(true);
			position.set(inv.getClass().getMethod("getInventory").invoke(inv), Class.forName("net.minecraft.core.BlockPos")
					.getConstructor(int.class, int.class, int.class).newInstance(b.getX(), b.getY(), b.getZ()));
		} catch (ReflectiveOperationException | RuntimeException ignored) {
		}
	}
	
	public static boolean isPreviewInventory(Inventory inventory) {
		return showcases.contains(inventory);
	}
	
	public static boolean closePreviewInventory(Inventory inventory) {
		inventory.clear();
		return showcases.remove(inventory);
	}
	
	
	
}
