package me.dru.showcase;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;


import me.dru.showcase.block.Showcase;
import me.dru.showcase.utils.CoreProtectShowcaseAdapter;

public class EventManager implements Listener {
	ModernShowcase plugin;
	CoreProtectShowcaseAdapter coreProtect;
	public EventManager(ModernShowcase plugin) {
		this.plugin = plugin;
		coreProtect = plugin.getCoreProtect();
		Bukkit.getWorlds().forEach(w->w.getEntities().forEach(ent->{
			if(ent.getType()==EntityType.ITEM_DISPLAY&&Showcase.isShowcase(ent.getLocation()))
				Showcase.rotatesInstance.put(ent.getLocation().getBlock(),Showcase.get(ent.getLocation()));
		}));
	}
	
	@EventHandler
	public void onCraft(PrepareItemCraftEvent e) {
		if(e.getRecipe()!=null&&e.getRecipe() instanceof ShapedRecipe&&((ShapedRecipe)e.getRecipe()).getKey().getNamespace().equalsIgnoreCase("ModernShowcase")) {
			e.getInventory().setResult(plugin.getShowcaseItem(ModernShowcase.getLang((Player)e.getView().getPlayer()), e.getInventory().getResult().getType()));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlace(BlockPlaceEvent e) {
		if(!e.isCancelled()&&e.getItemInHand()!=null&&Showcase.isShowcase(e.getItemInHand())) {
			Location loc = e.getBlock().getLocation().subtract(e.getPlayer().getLocation());
			Showcase.get(e.getBlock().getLocation()).spawn(Math.abs(loc.getX())<Math.abs(loc.getZ()),new ItemStack(Material.ITEM_FRAME));		
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent e) {
		if(!e.isCancelled()&&Showcase.isShowcase(e.getBlock().getLocation())) {
			Showcase showcase = Showcase.get(e.getBlock().getLocation());
			showcase.despawn();
		}
	}
	
		
	PlayerInteractEvent dependCheck;
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent e) {
		if(e!=dependCheck&&e.getHand()==EquipmentSlot.HAND&&e.getAction()==Action.RIGHT_CLICK_BLOCK&&Showcase.isShowcase(e.getClickedBlock().getLocation())) {
			
			Showcase showcase = Showcase.get(e.getClickedBlock().getLocation());
			if(e.getPlayer().isSneaking()&&e.getItem()!=null&&Showcase.isShowcase(e.getItem())) {
				return;
			}
			e.setCancelled(true);
			if((coreProtect!=null&&coreProtect.isInspecting(e.getPlayer())))
				return;
			//coreProtect.look
			if((e.getItem()==null||e.getItem().getAmount()==0)&&!e.getPlayer().isSneaking()) {
				ShowcaseUI.preview(e.getPlayer(), showcase);
				return;
			}
			
			Material origianl = e.getClickedBlock().getType();
			e.getClickedBlock().setType(Material.CHEST);
			PlayerInteractEvent interact = new PlayerInteractEvent(e.getPlayer(), Action.RIGHT_CLICK_BLOCK,e.getItem(), e.getClickedBlock(), e.getBlockFace(), e.getHand());
			dependCheck = interact;
			Bukkit.getPluginManager().callEvent(interact);
				
			if(e.getPlayer().isSneaking()) {
				if(interact.useInteractedBlock()!=Result.DENY) 
					ShowcaseUI.open(e.getPlayer(), showcase);
				else 
					ShowcaseUI.preview(e.getPlayer(), showcase);
					
				e.getClickedBlock().setType(origianl);
				return;
			} 
			if(e.getItem()!=null&&interact.useInteractedBlock()!=Result.DENY) {
				if(coreProtect!=null)
					coreProtect.logShowcase(e.getPlayer(),((Chest)e.getClickedBlock().getState()),showcase.getItem(),e.getItem());
				if(e.getPlayer().getInventory().firstEmpty()>=0)
					e.getPlayer().getInventory().addItem(showcase.getItem());
				else
					e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), showcase.getItem(),i->i.setPickupDelay(0));

				showcase.setItem(e.getItem());	
				e.getPlayer().getInventory().setItem(EquipmentSlot.HAND, new ItemStack(Material.AIR));
				e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(),Sound.ITEM_ARMOR_EQUIP_LEATHER,1f,1f);
				
			} else {
				ShowcaseUI.preview(e.getPlayer(),showcase);
			}
			e.getClickedBlock().setType(origianl);
		}
	}

	

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityExplode(EntityExplodeEvent e) { 
		for(Block b : e.blockList())
			Showcase.get(b.getLocation()).despawn();	
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockExplode(BlockExplodeEvent e) { 
		for(Block b : e.blockList())
			Showcase.get(b.getLocation()).despawn();	
	}
	
	@EventHandler
	public void onPistonExtend(BlockPistonExtendEvent e) {
		for(Block b : e.getBlocks()) {
			Showcase.get(b.getLocation()).despawn();
		}
	}
	
	@EventHandler
	public void onPistonRetract(BlockPistonRetractEvent e) {
		for(Block b : e.getBlocks()) {
			Showcase.get(b.getLocation()).despawn();
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(InventoryClickEvent e) {
		if(ShowcaseUI.isPreviewInventory(e.getInventory()))
			e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDrag(InventoryDragEvent e) {
		if(ShowcaseUI.isPreviewInventory(e.getInventory()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onCloseInv(InventoryCloseEvent e) {
		if(ShowcaseUI.isPreviewInventory(e.getInventory()))
			ShowcaseUI.closePreviewInventory(e.getInventory());
	}
	
	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		if(e.getEntityType()==EntityType.ITEM_DISPLAY&&Showcase.isShowcase(e.getEntity().getLocation()))
			Showcase.get(e.getEntity().getLocation()).despawn();
	}

	@EventHandler
	public void onLoad(EntitiesLoadEvent e) {
		for(Entity ent : e.getEntities()) {
			if(ent.getType()==EntityType.ITEM_DISPLAY&&Showcase.isShowcase(ent.getLocation())) {
				Showcase.get(ent.getLocation());
			}	
		}
		
	}
	@EventHandler
	public void onUnload(EntitiesUnloadEvent e) {
		for(Entity ent : e.getEntities()) {
			if(ent.getType()==EntityType.ITEM_DISPLAY&&Showcase.isShowcase(ent.getLocation())) {
				Showcase.rotatesInstance.remove(ent.getLocation().getBlock());
			}	
		}
		
	}
	
	private static float rot =0;
	public static void rotate() {
		rot+=0.1f;
		Showcase.rotatesInstance.values().forEach(display->{
			if(display==null)
				return;
			Transformation transfom = display.getItemDisplay().getTransformation();
			transfom.getLeftRotation().set(new AxisAngle4f( (float)Math.PI*rot*display.getAutoRotateSpeed(), new Vector3f(0, 1, 0)));
			display.getItemDisplay().setTransformation(transfom);
		});
	}
}
