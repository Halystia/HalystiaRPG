package fr.jamailun.halystia.utils;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.jobs.model.enchanteur.EnchanteurSources.SourceType;
import fr.jamailun.halystia.players.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RpgEquipment {

	public static final String DAMAGES_INT_BEGIN = ChatColor.BLUE + "Dégâts : ";
	public static final String DAMAGES_BUFF_BEGIN = ChatColor.BLUE + "Dégâts bonus : ";
	public static final String HEALTH_BEGIN = ChatColor.BLUE + "Vie bonus : ";
	public static final String ARMOR_BEGIN = ChatColor.BLUE + "Armure : ";
	public static final String SPEED_BEGIN = ChatColor.BLUE + "Vitesse bonus : ";
	public static final String MANA_BEGIN = ChatColor.BLUE + "Mana bonus : ";
	public static final String LIFESTEAL_BEGIN = ChatColor.BLUE + "Vol de vie : ";
	public static final String LEVEL_BEGIN = ChatColor.GRAY + "Équipement de niveau"+ChatColor.GOLD+" ";
	
	protected List<String> addionalLore;
	protected int level = 0;
	protected int health, armor, mana, damagesInt;
	protected double speed, damageBuff;
	protected double lifeSteal;
	
	protected ItemBuilder item;
	
	public RpgEquipment(Material material) {
		this(new ItemBuilder(material));
	}
	
	public RpgEquipment(ItemStack item) {
		this(new ItemBuilder(item));
	}
	
	public RpgEquipment(ItemBuilder item) {
		this.item = item;
		readItem();
	}
	
	public ItemBuilder toItemBuilder() {
		return item.clone();
	}
	
	private final DecimalFormat deF = new DecimalFormat("#.##");
	
	public ItemStack toItemStack() {
		item.resetLore();
		if(level > 0)
			item.addLoreLine(LEVEL_BEGIN + level);
		if(health != 0)
			item.addLoreLine(HEALTH_BEGIN + getSymbol(health) + Math.abs(health));
		if(armor != 0)
			item.addLoreLine(ARMOR_BEGIN + getSymbol(armor) + Math.abs(armor));
		if(mana != 0)
			item.addLoreLine(MANA_BEGIN + getSymbol(mana) + Math.abs(mana));
		if(speed != 0)
			item.addLoreLine(SPEED_BEGIN + getSymbol(speed) + deF.format(Math.abs(speed)) + " %");
		if(damagesInt != 0)
			item.addLoreLine(DAMAGES_INT_BEGIN + getSymbol(damagesInt) + Math.abs(damagesInt));
		if(damageBuff != 0)
			item.addLoreLine(DAMAGES_BUFF_BEGIN + getSymbol(damageBuff) + deF.format(Math.abs(damageBuff)) + " %");
		if(lifeSteal > 0)
			item.addLoreLine(LIFESTEAL_BEGIN + getSymbol(lifeSteal) + deF.format(Math.abs(lifeSteal)) + " %");
		if( ! addionalLore.isEmpty()) {
			item.addLoreLine("");
			addionalLore.forEach(l -> item.addLoreLine(l));
		}
		item.addItemFlag(ItemFlag.HIDE_ATTRIBUTES);
		return item.toItemStack();
	}
	
	private String getSymbol(double value) {
		return value > 0 ? ChatColor.GREEN+"+ " : ChatColor.RED+"- ";
	}
	private String getSymbol(int value) {
		return getSymbol((double)value);
	}
	private int readSymbol(String part) {
		return ChatColor.stripColor(part).contains("+") ? 1 : -1;
	}
	
	private void readItem() {
		addionalLore = new ArrayList<>();
		for(String line : item.getLore()) {
			try {
				String[] array = line.split(" ");
				if(line.contains(LEVEL_BEGIN)) {
					String nb = array[3];
					level = Integer.parseInt(nb);
					continue;
				}
				if(line.contains(HEALTH_BEGIN)) {
					String nb = array[4];
					health = Integer.parseInt(nb) * readSymbol(array[3]);
					continue;
				}
				if(line.contains(ARMOR_BEGIN)) {
					String nb = array[3];
					armor = Integer.parseInt(nb) * readSymbol(array[2]);
					continue;
				}
				if(line.contains(MANA_BEGIN)) {
					String nb = array[4];
					armor = Integer.parseInt(nb) * readSymbol(array[3]);
					continue;
				}
				if(line.contains(SPEED_BEGIN)) {
					String nb = array[4];
					speed = Double.parseDouble(nb) * (double)readSymbol(array[3]);
					continue;
				}
				if(line.contains(DAMAGES_INT_BEGIN)) {
					String nb = array[3];
					damagesInt = Integer.parseInt(nb) * readSymbol(array[2]);
					continue;
				}
				if(line.contains(DAMAGES_BUFF_BEGIN)) {
					String nb = array[4];
					damageBuff = Double.parseDouble(nb) * (double)readSymbol(array[3]);
					continue;
				}
				if(line.contains(LIFESTEAL_BEGIN)) {
					String nb = array[5];
					lifeSteal = Double.parseDouble(nb) * (double)readSymbol(array[4]);
					continue;
				}
			} catch(NumberFormatException | IndexOutOfBoundsException e) {
				HalystiaRPG.getInstance().getConsole().sendMessage(ChatColor.RED + "Erreur. " + e.getClass() + " -> " + e.getMessage());
				HalystiaRPG.getInstance().getConsole().sendMessage(ChatColor.RED + "Ligne : ["+line+"]");
			}
			if( ! line.isEmpty() && ! line.equals(ChatColor.GRAY+""))
				addionalLore.add(line);
		}
	}

	public int getHealth() {
		return health;
	}

	public int getArmor() {
		return armor;
	}

	public int getMana() {
		return mana;
	}

	public double getSpeed() {
		return speed;
	}

	public int getLevel() {
		return level;
	}

	public double getDamageBuff() {
		return damageBuff;
	}
	
	public RpgEquipment addEnchant(Enchantment enchant, int level) {
		item.addEnchant(enchant, level);
		return this;
	}
	
	public RpgEquipment setName(String name) {
		item.setName(name);
		return this;
	}

	public RpgEquipment setLevel(int level) {
		if(level < 0)
			level = 0;
		if(level > PlayerData.LEVEL_MAX)
			level = PlayerData.LEVEL_MAX;
		this.level = level;
		return this;
	}
	
	public RpgEquipment setUnbreakable() {
		item = item.setUnbreakable();
		return this;
	}
	
	public RpgEquipment shine() {
		item = item.shine();
		return this;
	}
	
	public RpgEquipment shineAndUnbreak() {
		item = item.shine().setUnbreakable();
		return this;
	}

	public int getDamagesInt() {
		return damagesInt;
	}

	public RpgEquipment setDamagesInt(int damagesInt) {
		this.damagesInt = damagesInt;
		return this;
	}

	public RpgEquipment setDamageBuff(double damageBuff) {
		this.damageBuff = damageBuff;
		return this;
	}

	public RpgEquipment setHealth(int health) {
		this.health = health;
		return this;
	}

	public RpgEquipment setArmor(int armor) {
		this.armor = armor;
		return this;
	}

	public RpgEquipment setMana(int mana) {
		this.mana = mana;
		return this;
	}

	public RpgEquipment setSpeed(double speed) {
		this.speed = speed;
		return this;
	}
	
	public void delta(SourceType type, double value) {
		switch (type) {
		case ARMOR:
			armor += value;
			break;
		case DEGATS_INT:
			damagesInt += value;
			break;
		case DEGATS_P:
			damageBuff += value/100;
			break;
		case HEALTH:
			health += value;
			break;
		case MANA:
			mana += value;
			break;
		case SPEED:
			speed += value/100;
			break;
		default:
			break;
		}
	}

	public double getLifeSteal() {
		return Math.max(0, lifeSteal);
	}
	
	public RpgEquipment setLifeSteal(double lf) {
		this.lifeSteal = lf;
		return this;
	}
	
}