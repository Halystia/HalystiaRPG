package fr.jamailun.halystia.jobs.model.enchanteur;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.constants.Equipment;
import fr.jamailun.halystia.jobs.JobCraftGUI;
import fr.jamailun.halystia.jobs.JobType;
import fr.jamailun.halystia.jobs.model.enchanteur.EnchanteurSources.Source;
import fr.jamailun.halystia.utils.ItemBuilder;
import fr.jamailun.halystia.utils.MenuGUI;

public class EnchanteurGUI extends JobCraftGUI {

	public static final int MAX_ENCHANTMENTS = 5;
	public static final String enchantedStart = ChatColor.LIGHT_PURPLE + "Enchanté par ";
	
	public EnchanteurGUI(JobType job) {
		super(job);
	}
	
	public boolean isValidItem(ItemStack item) {
		if( ! item.hasItemMeta())
			return false;
		if( ! item.getItemMeta().hasAttributeModifiers())
			return false;
		ItemMeta meta = item.getItemMeta();
		int enchants = 0;
		if(meta.hasLore()) {
			for(String line : meta.getLore()) {
				if(line.contains("Enchanté par ")) {
					String[] w = line.split(" ");
					try {
						int nb = Integer.parseInt(w[3]);
						enchants += nb;
					} catch (NumberFormatException | IndexOutOfBoundsException e) {
						enchants++;
					}
					if(enchants >= MAX_ENCHANTMENTS)
						return false;
				}
			}
		}
		return Equipment.isEquipment(item);
	}
	
	public boolean isValidOffrande(ItemStack item) {
		return EnchanteurSources.getFromItem(item) != null;
	}
	
	private final DecimalFormat df = new DecimalFormat("#.##");
	public ItemStack enchantItem(Player p, ItemStack item, Attribute attribut) {
		
		int level = job.getPlayerLevel(p);
		double karma = Math.random() - 0.6 + (0.05*level);

		job.addExp((int)Math.abs((karma*40)), p);
		
		Operation op = Operation.ADD_SCALAR;
		if(attribut == Attribute.GENERIC_ARMOR || attribut == Attribute.GENERIC_MAX_HEALTH)
			op = Operation.ADD_NUMBER;
		
		EquipmentSlot slot = EquipmentSlot.OFF_HAND;
		if(Equipment.HELMET.hasObject(item.getType()))
			slot = EquipmentSlot.HEAD;
		else if(Equipment.CHESTPLATE.hasObject(item.getType()))
			slot = EquipmentSlot.CHEST;
		else if(Equipment.LEGGINGS.hasObject(item.getType()))
			slot = EquipmentSlot.LEGS;
		else if(Equipment.BOOTS.hasObject(item.getType()))
			slot = EquipmentSlot.FEET;
		else if(Equipment.OFFENSIVE.hasObject(item.getType()))
			slot = EquipmentSlot.HAND;

		double valeur = (op == Operation.ADD_NUMBER) ? karma + 1 : karma;
		if(op == Operation.ADD_NUMBER) {
			karma *= 10;
			if(karma < 0)
				valeur = Math.floor(karma);
			else
				valeur = Math.ceil(karma);
		} else {
			valeur = karma / 10;
			if(Math.abs(valeur) > 0.1)
				valeur = Math.signum(valeur) * 0.1;
		}
		if(valeur < 0) {
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, .5f);
			p.sendMessage(HalystiaRPG.PREFIX+ChatColor.RED+""+ChatColor.BOLD+"ÉCHEC ! "+ChatColor.YELLOW+"L'amélioration ne s'est pas bien passée... "
			+ChatColor.RED +(op == Operation.ADD_SCALAR ? df.format(valeur)+"%": ((int)valeur)));
		} else if (valeur > 0) {
			p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 10f, .9f);
			p.sendMessage(HalystiaRPG.PREFIX+ChatColor.GREEN+""+ChatColor.BOLD+"SUCCÈS ! "+ChatColor.YELLOW+"L'amélioration s'est bien passée ! "
			+ChatColor.GREEN+"+" +(op == Operation.ADD_SCALAR ? df.format(valeur)+"%": ((int)valeur)));
		} else {
			p.playSound(p.getLocation(), Sound.BLOCK_BAMBOO_STEP, 1f, .5f);
			p.sendMessage(HalystiaRPG.PREFIX+ChatColor.GRAY+""+ChatColor.BOLD+"RATÉ "+ChatColor.YELLOW+"L'amélioration n'a pas eu lieu...");
		}
		//p.sendMessage("ATTR=§a"+attribut+"§f, karma=§a"+karma+"§7(valeur="+valeur+")§f, OP=§a"+op+"§f, slot=§a"+slot);
		ItemMeta meta = item.getItemMeta();
		
		final EquipmentSlot fslot = slot;
		final Operation fop = op;
		if(meta.getAttributeModifiers(attribut) != null) {
			Optional<AttributeModifier> optioMod = meta.getAttributeModifiers(attribut).stream().filter(mod -> mod.getSlot() == fslot && mod.getOperation() == fop).findFirst();
			if(optioMod.isPresent()) {
				AttributeModifier mod = optioMod.get();
				meta.removeAttributeModifier(attribut, mod);
				AttributeModifier newMod = new AttributeModifier(mod.getUniqueId(), mod.getName(), mod.getAmount() + valeur, op, slot);
				meta.addAttributeModifier(attribut, newMod);
			} else {
				meta.addAttributeModifier(attribut, new AttributeModifier(UUID.randomUUID(), attribut.name(), valeur, op, slot));
			}
		} else {
			meta.addAttributeModifier(attribut, new AttributeModifier(UUID.randomUUID(), attribut.name(), valeur, op, slot));
		}
		
		
		boolean addedLore = false;
		if( meta.hasLore() ) {
			List<String> copyLore = new ArrayList<>(meta.getLore());
			for(int i = 0; i < copyLore.size(); i++) {
				String line = copyLore.get(i);
				String[] w = line.split(" ");
				if(line.contains("Enchanté par ")) {
					try {
						String plName = w[2];
						if(plName.equals(p.getName())) {
							List<String> lore = meta.getLore();
							lore.remove(i);
							int time = 1;
							try {
								time = Integer.parseInt(w[3]);
							} catch (NumberFormatException | IndexOutOfBoundsException ee) {}
							time++;
							lore.add(enchantedStart+p.getName() + (time > 1 ? " "+time+" fois.":""));
							meta.setLore(lore);
							addedLore = true;
							break;
						}
					} catch (IndexOutOfBoundsException e) {
						continue;
					}
				}
			}
		}
		if(!addedLore)
			if(!meta.hasLore()) {
				meta.setLore(Arrays.asList(enchantedStart + p.getName()));
			} else {
				List<String> lore = meta.getLore();
				lore.add(enchantedStart + p.getName());
				meta.setLore(lore);
			}
		if(valeur != 0)
			item.setItemMeta(meta);
		
		
		return new ItemStack(item);
	}

	@Override
	public void openGUItoPlayer(Player p) {
		new EnchantTableInstance(p).show(p);
	}
	
	public class EnchantTableInstance extends MenuGUI {
		public final static int SLOT_ITEM = 11, SLOT_CONFIRM = 22, SLOT_OFFRANDE = 29, SLOT_OUT = 24;
		private final ItemStack RIEN = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setName("§f").toItemStack();
		private final ItemStack BORD = new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setName("§f").toItemStack();
		private boolean canEnchant = false;
		private Optional<ItemStack> item = Optional.empty();
		private Optional<ItemStack> offrande = Optional.empty();
		private Optional<ItemStack> out = Optional.empty();
		private final Player p;
		public EnchantTableInstance(Player p) {
			super(ChatColor.DARK_PURPLE +"Table d'enchanteur", 9*5, HalystiaRPG.getInstance());
			this.p = p;
			for(int i = 0; i < getSize(); i++)
				addOption( ((i%9==0) || ((i+1)%9==0) || i==getSize()-1) ? BORD : RIEN, i);
			resetConfirmSlot();
			resetItemSlot();
			resetOffrandeSlot();
			resetOutSlot();
		}
		@Override
		public void onClose(InventoryCloseEvent e) {
			item.ifPresent(item -> p.getInventory().addItem(item));
			offrande.ifPresent(item -> p.getInventory().addItem(item));
			out.ifPresent(item -> p.getInventory().addItem(item));
			removeFromList();
		}
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(InventoryClickEvent e) {
			e.setCancelled(true);
			if(e.getSlot() == SLOT_ITEM) {
				if(e.getCursor() == null || e.getCursor().getType() == Material.AIR) {
					if(item.isPresent()) {
						e.setCursor(item.get()); //apparemment ça a peu de conséquences. à voir si on tp pas l'item direct dans le joueur.
						item = Optional.empty();
						resetItemSlot();
						canEnchant = false;
						resetConfirmSlot();
						p.updateInventory();
					}
				} else {
					if( ! isValidItem(e.getCursor())) {
						p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Impossible d'enchanter cet item.");
						return;
					}
					if(item.isPresent()) {
						ItemStack ram = new ItemStack(item.get());
						item = Optional.of(e.getCursor());
						addOption(item.get(), SLOT_ITEM);
						canEnchant = offrande.isPresent();
						resetConfirmSlot();
						e.setCursor(ram);
					} else {
						item = Optional.of(e.getCursor());
						addOption(item.get(), SLOT_ITEM);
						e.setCursor(null);
						canEnchant = offrande.isPresent();
						resetConfirmSlot();
					}
				}
			} else if(e.getSlot() == SLOT_OFFRANDE) {
				if(e.getCursor() == null || e.getCursor().getType() == Material.AIR) {
					if(offrande.isPresent()) {
						e.setCursor(offrande.get());
						offrande = Optional.empty();
						resetOffrandeSlot();
						canEnchant = false;
						resetConfirmSlot();
						p.updateInventory();
					}
				} else {
					if( ! isValidOffrande(e.getCursor())) {
						p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Impossible d'utiliser cet objet.");
						return;
					}
					if(offrande.isPresent()) {
						ItemStack ram = new ItemStack(offrande.get());
						offrande = Optional.of(e.getCursor());
						addOption(offrande.get(), SLOT_OFFRANDE);
						canEnchant = item.isPresent();
						resetConfirmSlot();
						e.setCursor(ram);
					} else {
						offrande = Optional.of(e.getCursor());
						addOption(offrande.get(), SLOT_OFFRANDE);
						e.setCursor(null);
						canEnchant = item.isPresent();
						resetConfirmSlot();
					}
				}
			} else if(e.getSlot() == SLOT_OUT) {
				if(e.getCursor() == null || e.getCursor().getType() == Material.AIR) {
					if(out.isPresent()) {
						e.setCursor(out.get()); //apparemment ça a peu de conséquences. à voir si on tp pas l'item direct dans le joueur.
						out = Optional.empty();
						resetOutSlot();
						canEnchant = (item.isPresent() && offrande.isPresent());
						resetConfirmSlot();
						p.updateInventory();
					}
				}
			} else if(e.getSlot() == SLOT_CONFIRM) {
				if( ! (canEnchant && item.isPresent() && offrande.isPresent() && !out.isPresent()) ) {
					return;
				}
				Attribute attr = EnchanteurSources.getFromItem(offrande.get());
				if(attr == null) {
					p.sendMessage(HalystiaRPG.PREFIX +ChatColor.RED+"Source de pouvoir non valable.");
					return;
				}
				out = Optional.of(enchantItem(p, item.get(), attr));
				addOption(out.get(), SLOT_OUT);
				item = Optional.empty();
				
				ItemStack newOff = new ItemStack(offrande.get());
				resetOffrandeSlot();
				if(newOff.getAmount() <= 1)
					offrande = Optional.empty();
				else {
					newOff.setAmount(newOff.getAmount() - 1);
					offrande = Optional.of(newOff);
					addOption(offrande.get(), SLOT_OFFRANDE);
				}
				
				canEnchant = false;
				resetItemSlot();
				resetConfirmSlot();
			}
			p.updateInventory();
		}
		private void resetItemSlot() {
			addOption(new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setName(ChatColor.LIGHT_PURPLE+"Objet à enchanter").toItemStack(), SLOT_ITEM);
		}
		private void resetOutSlot() {
			addOption(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(ChatColor.GRAY+"Il n'y a rien à récupérer").toItemStack(), SLOT_OUT);
		}
		private void resetConfirmSlot() {
			if(canEnchant)
				addOption(new ItemBuilder(Material.GREEN_CONCRETE).setName(ChatColor.GREEN+"Enchanter").toItemStack(), SLOT_CONFIRM);
			else
				addOption(new ItemBuilder(Material.RED_CONCRETE).setName(ChatColor.RED+"Impossible d'enchanter").toItemStack(), SLOT_CONFIRM);
		}
		private void resetOffrandeSlot() {
			addOption(new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setName(ChatColor.BLUE+"Source de pouvoir").toItemStack(), SLOT_OFFRANDE);
		}
	}

	public Map<String, ItemStack> getSources() {
		Map<String, ItemStack> items = new HashMap<>();
		for(Source source : Source.values())
			items.put("source_"+source.getName(), source.getItem());
		return items;
	}
}