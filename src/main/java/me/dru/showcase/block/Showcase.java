package me.dru.showcase.block;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import me.dru.showcase.ModernShowcase;
import me.dru.showcase.ShowcaseUI;
import me.dru.showcase.config.Config;
import me.dru.showcase.utils.ScheduleUtil;

/**
 * Showcase represent a block with showcase data
 * @author Dru_TNT
 *
 */
public class Showcase {
	private final Block block;
	private ItemDisplay item;
	private int size;
	private float xRotation;
	private float yRotation;
	private float autoRotateSpeed;
	public final static HashMap<Block,Showcase> rotatesInstance = new HashMap<>();
	
	private Showcase(Block block) {
		this.block = block;
		PersistentDataContainer con = block.getChunk().getPersistentDataContainer();
		String id = con.get(spacedKey(block.getLocation()), PersistentDataType.STRING);
		if(id!=null) {
			this.item = (ItemDisplay) Bukkit.getEntity(UUID.fromString(id));
			size = Math.max(1, con.get(spacedKey(block,"size"), PersistentDataType.INTEGER));
			xRotation = con.get(spacedKey(block,"rotX"), PersistentDataType.FLOAT);
			yRotation = con.get(spacedKey(block,"rotY"), PersistentDataType.FLOAT);
			autoRotateSpeed = con.get(spacedKey(block,"auto_rotate"), PersistentDataType.FLOAT);
			if(autoRotateSpeed!=0)
				rotatesInstance.put(block,this);
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
	
	public ItemDisplay getItemDisplay() {
		return item;
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

	
	public void spawn(boolean north,ItemStack show) {
		if(isShowcase())
			despawn();
		//block.setType(Material.GLASS);
		item = block.getWorld().spawn(block.getLocation().add(0.5f,0.5f,0.5f), ItemDisplay.class);
		item.setInterpolationDuration(2*20);
		item.setInterpolationDelay(2);
		PersistentDataContainer con = getDataContainer();
		con.set(spacedKey(block.getLocation()),PersistentDataType.STRING, ""+item.getUniqueId());
		setSize(7);
		setRotation(north ? 0 : 90, 0);
		setAutoRotateSpeed(0);
		setItem(show);
	}
	
	public void despawn() {
		if(isShowcase()) {
			block.breakNaturally();
			if(item!=null) {
				rotatesInstance.remove(item);
				if(item.getItemStack()!=null)
					item.getWorld().dropItem(item.getLocation(), item.getItemStack());
				item.remove();	
			}
			PersistentDataContainer con = getDataContainer();
			con.remove(spacedKey(block.getLocation()));		
		}
	}

	public void setItem(ItemStack item) {
		this.item.setItemStack(item);
	}
	
	public void setRotation(float x, float y) {
		this.xRotation =x;
		this.yRotation =y;

		Location loc =  item.getLocation();
		loc.setYaw(x);
		loc.setPitch(y);
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
		return item.getBillboard()==Billboard.FIXED;
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
	
	public ItemStack getItem() {
		return item.getItemStack();
	}
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		Config config = ModernShowcase.Config();
		size = Math.max(Math.min(size, config.maxSize), config.minSize);
		this.size = size;
		Transformation transform = item.getTransformation();
		transform.getScale().set(size/config.sizeInterval, size/config.sizeInterval, size/config.sizeInterval);
		item.setTransformation(transform);
		
		PersistentDataContainer con = getDataContainer();
		con.set(spacedKey(block,"size"), PersistentDataType.INTEGER, size);
	}

	public float getAutoRotateSpeed() {
		return autoRotateSpeed;
	}
	
	public void setAutoRotateSpeed(float speed) {
		if(autoRotateSpeed==0&&speed!=0)
			rotatesInstance.put(block, this); 
		else if(speed==0) {
			Transformation transfom = item.getTransformation();
			transfom.getLeftRotation().set(new AxisAngle4f(0, new Vector3f(0, 1, 0)));
			item.setTransformation(transfom);
			rotatesInstance.remove(block);	
		}
		autoRotateSpeed = speed;
		getDataContainer().set(spacedKey(block,"auto_rotate"), PersistentDataType.FLOAT, autoRotateSpeed);
	}
	
	


	
}
