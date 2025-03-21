package me.dru.showcase.block;

import java.awt.Event;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import dr.dru.gui.listener.EventManager;
import me.dru.showcase.ModernShowcase;
import me.dru.showcase.config.Config;
import me.dru.showcase.utils.ScheduleUtil;

/**
 * Showcase represent a block with showcase data
 * @author Dru_TNT
 *
 */
public class Showcase {
	private final Block block;
	private UUID[] item = new UUID[9];
	private UUID onDisplayItem;
	private int size;
	private float xRotation;
	private float yRotation;
	private float autoRotateSpeed;
	private ItemDisplay holder;
	public final static HashMap<Block,Showcase> rotatesInstance = new HashMap<>();
	
	private Showcase(Block block) {
		this.block = block;
		PersistentDataContainer con = block.getChunk().getPersistentDataContainer();
		

		 

		NamespacedKey holderKey = spacedKey(block,"holder");
		if(!loadItemDisplay()) //legacy import
		{ 
			String id = con.get(spacedKey(block.getLocation()), PersistentDataType.STRING);
			
			
			if(id!=null&&id.length()>1) {
				ItemDisplay old = ((ItemDisplay)Bukkit.getEntity(UUID.fromString(id)));
				if(old!=null) {
					spawnHolder(false, old.getItemStack(),false);
					old.remove();
					setSize(Math.max(1, con.get(spacedKey(block,"size"), PersistentDataType.INTEGER)));
					setRotation(con.get(spacedKey(block,"rotX"), PersistentDataType.FLOAT), con.get(spacedKey(block,"rotY"), PersistentDataType.FLOAT));	
				} else 
					despawn();
				
				//con.remove(spacedKey(block.getLocation()));
			}
		}
		
		if(isShowcase()) { //check valid
			
			if(con.has(holderKey,PersistentDataType.STRING)) {
				holder = (ItemDisplay)Bukkit.getEntity(UUID.fromString(con.get(holderKey, PersistentDataType.STRING)));
				if(holder==null) {
					despawn();
					return;
				}
			}
			size = Math.max(1, con.get(spacedKey(block,"size"), PersistentDataType.INTEGER));
			xRotation = con.get(spacedKey(block,"rotX"), PersistentDataType.FLOAT);
			yRotation = con.get(spacedKey(block,"rotY"), PersistentDataType.FLOAT);
			setItemDisplay(me.dru.showcase.EventManager.rotTimes);
			if(con.has(spacedKey(block,"auto_rotate"),PersistentDataType.FLOAT))
				autoRotateSpeed = con.get(spacedKey(block,"auto_rotate"), PersistentDataType.FLOAT);
			tryRegisterMotion();
		}
			
	}
	
	public static Showcase get(Location loc) {
		if(rotatesInstance.containsKey(loc.getBlock()))
			return rotatesInstance.get(loc.getBlock());
		return new Showcase(loc.getBlock());
	}

	public boolean isShowcase() {
		return isShowcase(block.getLocation());
	}
	
	public static boolean isShowcase(Block b) {
		return isShowcase(b.getLocation());
	}

	public static boolean isShowcase(Location b) {
		PersistentDataContainer con = b.getChunk().getPersistentDataContainer();
		return con.has(spacedKey(b), PersistentDataType.STRING);
	}
	
	private static NamespacedKey key = new NamespacedKey(ModernShowcase.getInstance(), "ModernShowcase");
	public static boolean isShowcase(ItemStack item) {
		return item.hasItemMeta()&&item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN);
	}
	
	public Block getBlock() {
		return block;
	}


	public ItemDisplay getItemHolder() {
		return holder;
	}
	
	public ItemDisplay setItemDisplay(int idx) {

		 List<UUID> items = Arrays.stream(item).filter(id->id!=null).toList();
		 
		 onDisplayItem = items.get(idx%items.size());

		 ItemDisplay display = getItemHolder();
		 //ERROR DEBUG will be null on showcase init
		 if(getItemDisplay()!=null)
			 display.setItemStack(getItemDisplay().getItemStack());
		 return display;
	}
	
	/*
	public ItemDisplay setItemDisplay(int idx,boolean checkOld) {
		
		ItemDisplay display;
		ItemDisplay oldDisplay = null;
		if(checkOld)
			oldDisplay = getItemDisplay();
		
		 List<UUID> items = Arrays.stream(item).filter(id->id!=null).toList();
		 onDisplayItem = items.get(idx%items.size());
		 
		 display = getItemDisplay();
		 if(oldDisplay==display)
			 return oldDisplay;
		setSize(getSize());
		setRotation(getYawRotation(),getPitchRotation());
		 if(display!=null)
			 display.setVisibleByDefault(true);
		if(checkOld&&oldDisplay!=null) {
			oldDisplay.setVisibleByDefault(false);	
		}
		return display;
	}
	*/

	public ItemDisplay getItemDisplay() {

		if(onDisplayItem==null)
			return setItemDisplay(me.dru.showcase.EventManager.rotTimes);
		Entity display =Bukkit.getEntity(onDisplayItem);
		if(display==null)
			onDisplayItem = Arrays.stream(item).filter(id->id!=null).findFirst().get();
		return (ItemDisplay) display;
	}
	
	public ItemDisplay getItemDisplay(int slot) {
		return (ItemDisplay) Bukkit.getEntity(item[slot]);
	}
	public Stream<ItemDisplay> getItemDisplays() {
		return Arrays.stream(item).filter(id->id!=null).map(id->(ItemDisplay)Bukkit.getEntity(id)).filter(i->i!=null);
	}
	
	private boolean loadItemDisplay() {
		PersistentDataContainer con = getDataContainer();
		NamespacedKey key = spacedKey(block,"item_display");
		if(!con.has(key,PersistentDataType.STRING))
			return false;
		item = Arrays.stream(con.get(key, PersistentDataType.STRING)
				.split("/")).map(id->id.isEmpty()||Bukkit.getEntity(UUID.fromString(id))==null? null : UUID.fromString(id)).collect(Collectors.toList()).toArray(new UUID[9]);
		return true;
	}

	private void saveItemDisplay() {
		StringBuilder sb = new StringBuilder();
		
		for(int i=0;i<9;i++) {

			if(!sb.isEmpty())
				sb.append("/");
			UUID id = item[i];
			if(id!=null&&Bukkit.getEntity(item[i])!=null)
				sb.append(id==null? "":id);
		
		}
		
		getDataContainer().set(spacedKey(block,"item_display"), PersistentDataType.STRING, sb.toString());
	}

	private static NamespacedKey spacedKey(Location b) {
		return new NamespacedKey(ModernShowcase.getInstance(), b.getBlockX()+"_"+b.getBlockY()+"_"+b.getBlockZ());
	}
	
	private static NamespacedKey spacedKey(Block b, String tag) {
		return spacedKey(b.getLocation(),tag);
	}
	private static NamespacedKey spacedKey(Location b, String tag) {
		return new NamespacedKey(ModernShowcase.getInstance(), b.getBlockX()+"_"+b.getBlockY()+"_"+b.getBlockZ()+"_"+tag);
	}

	public static int getPlacedAmountOnChunk(Chunk chunk) {
		PersistentDataContainer con = chunk.getPersistentDataContainer();
		NamespacedKey key = new NamespacedKey(ModernShowcase.getInstance(), "placed_amount");
		return con.has(key) ? con.get(key, PersistentDataType.INTEGER) : 0;
	}
	
	public static void addPlacedAmountOnChunk(Chunk chunk, int amount) {
		PersistentDataContainer con = chunk.getPersistentDataContainer();
		NamespacedKey key = new NamespacedKey(ModernShowcase.getInstance(), "placed_amount");
		int total = (con.has(key,PersistentDataType.INTEGER) ? con.get(key, PersistentDataType.INTEGER) : 0) +amount;
		con.set(key, PersistentDataType.INTEGER, Math.max(total, 0));
	}
	public void spawnHolder(boolean north,ItemStack show) {
		spawnHolder(north, show,true);
	}
	public void spawnHolder(boolean north,ItemStack show, boolean init) {
		PersistentDataContainer con = getDataContainer();
		NamespacedKey key = spacedKey(block,"holder");
		if(con.has(key,PersistentDataType.STRING)) {
			Entity ent = Bukkit.getEntity(UUID.fromString(con.get(key, PersistentDataType.STRING)));
			if(ent!=null)
				return;
		}
		ItemDisplay display = spawn(north, -1, show);
		display.setVisibleByDefault(true);
				
		holder = display;
		con.set(key, PersistentDataType.STRING, display.getUniqueId().toString());
		con.set(spacedKey(block.getLocation()), PersistentDataType.STRING, "1");

		setItem(4,show);
		setItemDisplay(4);
		if(init) {
			setSize(7);
			setRotation(north ? 0 : 90, yRotation);
		}
	}
	
	public ItemDisplay spawn(boolean north, int idx,ItemStack show) {
		//if(isShowcase())
		//	despawn();
		//block.setType(Material.GLASS);
		
		ItemDisplay id = block.getWorld().spawn(block.getLocation().add(0.5f,0.5f,0.5f), ItemDisplay.class);
		id.setInterpolationDuration(2*20);
		id.setInterpolationDelay(2);
		id.setVisibleByDefault(false);
		//if(holder==null)
		//	holder = id;
		//if(item.size()==0) {
		if(idx>=0) {
			item[idx] = id.getUniqueId();
			if(show!=null)
				setItem(idx,show);
			setItemDisplay(idx);			
		}

		saveItemDisplay();
			//PersistentDataContainer con = getDataContainer();
			//con.set(spacedKey(block.getLocation()),PersistentDataType.STRING, ""+id.getUniqueId());
		

		//}

		return id;
	}
	
	private ItemDisplay spawnItemDisplay(int idx) {
		return spawn(true,idx, null);
		
	}
	
	public void despawn() {
		if(isShowcase()) {
			block.breakNaturally();
			if(holder!=null)
				holder.remove();
			rotatesInstance.remove(block);
			Arrays.stream(item).filter(id->id!=null).map(id->(ItemDisplay)Bukkit.getEntity(id)).forEach(id->{
				id.getWorld().dropItem(id.getLocation(), id.getItemStack());
				id.remove();
			});
				
			
			PersistentDataContainer con = getDataContainer();
			con.remove(spacedKey(block.getLocation()));
			con.remove(spacedKey(block.getLocation(),"item_display"));
			
		}
	}

	
	public void setRotation(float x, float y) {
		this.xRotation =x;
		this.yRotation =y;

		ItemDisplay item = getItemHolder();
		Location loc = item.getLocation();
		loc.setYaw(x);
		loc.setPitch(y);
		/*
		getItemDisplays().forEach(display->{

			ScheduleUtil.teleportAsync(display,loc);
		});*/
		ScheduleUtil.teleportAsync(item,loc);
		PersistentDataContainer con = getDataContainer();
		con.set(spacedKey(block,"rotX"), PersistentDataType.FLOAT, x);
		con.set(spacedKey(block,"rotY"), PersistentDataType.FLOAT, y);
	}
	
	public void setYawRotation(float x) {
		setRotation(x, yRotation);
	}

	public void setPitchRotation(float y) {
		setRotation(xRotation, y);
	}

	
	public float getYawRotation() {
		return xRotation;
	}

	public float getPitchRotation() {
		return yRotation;
	}

	
	public void togglgBillboard() {
		ItemDisplay item = getItemHolder();
		switch(item.getBillboard()) {
		case FIXED:
			item.setBillboard(Billboard.CENTER);
			break;
		default: case CENTER:
			item.setBillboard(Billboard.FIXED);
			break;
		}	
	
		
	}
	
	public boolean isFixedBillboard() {
		return getItemHolder().getBillboard()==Billboard.FIXED;
	}

	public void togglgGlass() {
		PersistentDataContainer con = getDataContainer();
		if(block.getType()==Material.BARRIER) {
			block.setType(Material.matchMaterial(con.get(spacedKey(block,"glass"), PersistentDataType.STRING)));
		}
		else {
			con.set(spacedKey(block, "glass"),PersistentDataType.STRING, block.getType().name());
			block.setType(Material.BARRIER);		
		}
	}
	
	public boolean hideGlass() {
		return block.getType()==Material.BARRIER;
	}
	public PersistentDataContainer getDataContainer() {
		return block.getChunk().getPersistentDataContainer();
	}
	
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		Config config = ModernShowcase.Config();

		ItemDisplay item = getItemHolder();
		size = Math.max(Math.min(size, config.maxSize), config.minSize);
		this.size = size;
		Transformation transform = item.getTransformation();
		transform.getScale().set(size/config.sizeInterval, size/config.sizeInterval, size/config.sizeInterval);
		
		/*
		getItemDisplays().forEach(i->{
			i.setTransformation(transform);	
		});*/
		item.setTransformation(transform);
		
		
		PersistentDataContainer con = getDataContainer();
		con.set(spacedKey(block,"size"), PersistentDataType.INTEGER, size);
	}

	public float getAutoRotateSpeed() {
		return autoRotateSpeed;
	}
	
	public void setAutoRotateSpeed(float speed) {

		ItemDisplay item = getItemHolder();
		if(autoRotateSpeed==0&&speed!=0)
			rotatesInstance.put(block, this); 
		else if(speed==0) {
			Transformation transfom = item.getTransformation();
			transfom.getLeftRotation().set(new AxisAngle4f(0, new Vector3f(0, 1, 0)));
			getItemHolder().setTransformation(transfom);
			rotatesInstance.remove(block);	
		}
		autoRotateSpeed = speed;
		getDataContainer().set(spacedKey(block,"auto_rotate"), PersistentDataType.FLOAT, autoRotateSpeed);

	}
	
	
	
	public @Nullable Collection<ItemStack> getItems() {
		return Arrays.stream(item).map(id->id!=null ? ((ItemDisplay)Bukkit.getEntity(id)).getItemStack() :null).toList();
	}
	public @Nullable ItemStack getItem(int slot) {
		if(this.item[slot]==null)
			return null;
		return getItemDisplay(slot).getItemStack(); 
	}
	public void setItem(int slot, ItemStack item) {
		if(item==null||item.getAmount()==0) {
			removeItem(slot);
			return;
		}
		if(this.item[slot]==null) {
			this.item[slot] = spawnItemDisplay(slot).getUniqueId();
		}
		getItemDisplay(slot).setItemStack(item);

		tryRegisterMotion();

	}
	
	public void removeItem(int slot) {
		if(item[slot]!=null) {
			ItemDisplay item = getItemDisplay(slot);
			item.remove();
		}
		item[slot] = null;
		saveItemDisplay();
		tryRegisterMotion();
	}
	
	private void tryRegisterMotion() {

		if(getItemDisplays().count()>0||autoRotateSpeed!=0)
			rotatesInstance.put(block, this);
		else 
			rotatesInstance.remove(block);
	}
	
	


	
}
