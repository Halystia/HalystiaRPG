package fr.jamailun.halystia.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

	/**
	A nice util to create menus.
	<i>If you're planning on using this in your resource, give me some credit, and <b>leave this notice</b>!</i>
	Liked it? Tell me! Didn't like it? Tell me! Got an idea to improve? Tell me! Contact me via spigot @ NonameSL.
	@author NonameSL
	@version 1.4.1
	*/
public abstract class MenuGUI {
	/**
	 * Should be called on InventoryClickEvent, is required for the onClick method to work.
	 * @param e The event called.
	 * @param main The plugin owning the menus.
	 * @param cancelShift Wether or not to cancel shift clicks from bottom inventory.
	 * @return The menu recognized, null if not a menugui that was clicked.
	 */
		
	public static MenuGUI checkForMenuClick(JavaPlugin main, InventoryClickEvent e, boolean cancelShift){
		try {
			if(e == null)
				return null;
			if(e.getClickedInventory() == null)
				return null;
		} catch (NullPointerException ee) {}
		
		for(MenuGUI gui : guis) {
				if(gui.main.getDescription().getName().equals(main.getDescription().getName())){
					if(
							gui.inv.getType() == e.getClickedInventory().getType() &&
							gui.name.equals(e.getView().getTitle()) &&
							gui.inv.getViewers().equals(e.getClickedInventory().getViewers())
						){
						gui.onClick(e);
						return gui;
					}
					if(cancelShift&&e.getClick().name().contains("SHIFT")/*Backwardss compability*/&&
							gui.inv.getType()==e.getView().getTopInventory().getType() &&
									gui.name.equals(e.getView().getTitle()) &&
							gui.inv.getViewers().equals(e.getView().getTopInventory().getViewers())
						){
						e.setCancelled(true);
						return null;
					}
				}
		}
		return null;
	}
	
	/**
	* Should be called on InventoryCloseEvent, is required for the onClose method to work.
	* @param e The event called.
	* @param main The plugin owning the menus.
	* @return The menu recognized, null if not a menugui that was closed.
	*/
	public static MenuGUI checkForMenuClose(JavaPlugin main, InventoryCloseEvent e){
		if(e == null)
			return null;
		if(e.getInventory() == null)
			return null;
		for(MenuGUI gui : guis){
			if(
					gui.main.getDescription().getName().equals(main.getDescription().getName()) &&
					gui.inv.getType()==e.getInventory().getType() &&
					gui.name.equals(e.getView().getTitle()) &&
					gui.inv.getViewers().equals(e.getInventory().getViewers())
				){
				gui.onClose(e);
				return gui;
			}
		}
		return null;
	}
	
	
	
	public static final ArrayList<MenuGUI> guis = new ArrayList<>();
	private String name;
	private int size;
	public JavaPlugin main;
	private Inventory inv;
	public Player player;

	
	
	/**
	* Create a new MenuGUI.
	* @param name The title of the menu.
	* @param size The size of the menu (Valid options are 9, 18, 27, 36, 45, 54)
	* @param main The plugin to use it with.
	*/
	public MenuGUI(String name, int size, JavaPlugin main) {
		this.name = name;
		this.size = size;
		this.main = main;
		this.inv = Bukkit.createInventory(null, size, name);
		guis.add(this);
	}
	
	/**
	* @added per jamailun
	* 
	*/
	public void removeFromList() {
		guis.remove(this);
	}
	
	
	public abstract void onClose(InventoryCloseEvent e);
	
	public abstract void onClick(InventoryClickEvent e);

	public Inventory getInventory(){
		return inv;
	}
	
	public void setTitle(String title){
		this.name=title;
		recreateInventory();
	}
	
	public void recreateInventory(){
		this.inv=Bukkit.createInventory(null, size, name);
	}
	
	public MenuGUI addOption(ItemStack is){
		addOption(is, -1);
		return this;
	}

	public MenuGUI addOption(ItemStack is, int position){
		if(Math.floor(position/9)>5)
			return this;
		if(position<0)
			inv.addItem(is);
		else
			inv.setItem(position, is);
		return this;
	}

	public void show(Player player) {
		this.player = player;
		player.openInventory(inv);
	}

	public void show(Player... p){
		for(Player player : p)
			show(player);
	}

	public int getSize(){
		return size;
	}
	
	public List<Player> ecivtViewers(){
		return evictViewers(null);
	}
	

	public List<Player> evictViewers(String msg){
		List<Player> viewers = new ArrayList<>();
		for(HumanEntity entity : inv.getViewers()){
			entity.closeInventory();
			if(msg != null && entity instanceof Player){
				((Player)entity).sendMessage(msg);
				viewers.add(((Player)entity));
			}
		}
		return viewers;
	}

	public JavaPlugin getPlugin() {
		return main;
	}
}