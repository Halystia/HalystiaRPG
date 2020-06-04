package fr.jamailun.halystia.players;

import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.DARK_GRAY;
import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.RED;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.utils.PlayerUtils;

/**
 * RAM value of all datas concerning a {@link org.bukkit.entity.Player Player}.
 * @author jamailun
 */
public class PlayerData {
	
	/**
	 * Maximum level a player can obtain.
	 */
	public final static int LEVEL_MAX = 100;
	/**
	 * How much mana will be given to a player per second.
	 */
	public final static int MANA_REFILL_PER_SECOND = 10;
	
	private Classe classe; // very static lul
	private int exp, level, karma; // on stocke le niveau pour pas le recalculer toutes les 10 secondes.
	private Player player; // Assossiated player. (tampon)
	private boolean playerValid; // If the player is valid. (tampon)
	private double maxMana; // Calculated at every levelup.
	private double mana, manaToRefill; // Mobile values
	
	/**
	 * Create a PlayerData object when a {@link org.bukkit.entity.Player Player} connects to the game.
	 * @param classe : {@link fr.jamailun.halystia.players.Classe Classe} of the player.
	 * @param exp : experiences points of the player
	 * @param p : {@link org.bukkit.entity.Player Player} who connected.
	 */
	public PlayerData(Classe classe, int exp, Player player, int karma) {
		this.classe = classe;
		this.exp = exp;
		this.player = player;
		this.karma = karma;
		
		level = -1;
		playerValid = true;
		calculateLevel();
	}
	
	/**
	 * Recalculte level of Player. Carefull : costs RAM.
	 * <br /> Calculate manaMax and healthMax if the level change.
	 * @return true if the level is different !
	 */
	public boolean calculateLevel() {
		double level = (1.5 / 11)*Math.pow(exp+1, .51);
		if(level < 1)
			level = 1;
		if(level > LEVEL_MAX)
			level = LEVEL_MAX;
		final boolean different = ((int)level != this.level);
		this.level = (int) level;
		//Si le joueur est connecté et que le niveau a changé : on calcule !
		if(different && playerValid) {
			updateMaxHealth();
			updateMaxMana();
		}
		return different;
	}

	public int getExpRequired(int level) {
		double xp = 49.7358406536 * Math.pow(level, 1.960784431373);
		if(xp < 0)
			xp = 0;
		return (int) xp;
	}
	
	private void updateMaxHealth() {
		if(level == 0) {
			player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
			return;
		}
		int bonus = (int) (((double)level) / 10.0);
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20 + (bonus * 2));
	}
	
	private void updateMaxMana() {
		maxMana = level * 3;
		mana = maxMana;
	}
	
	/**
	 * @return current {@link fr.jamailun.halystia.players.Classe Classe} of the player.
	 */
	public Classe getClasse() {
		return classe;
	}
	
	/**
	 * Get affected Player.
	 * @return {@link org.bukkit.entity.Player Player} if is valid.
	 * @throws IllegalAccessError if not valid.
	 */
	public Player getPlayer() {
		if(!playerValid)
			throw new IllegalAccessError("Player is not valid !");
		return player;
	}
	
	/**
	 * Get current level of Player. 
	 * @return level. 0 if player has no class.
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * Get amount of experiences points.
	 * @return raw amount of experience.
	 */
	public int getExpAmount() {
		return exp;
	}
	
	/**
	 * get UUID of associed Player.
	 * @return UUID of player, even if Player is disconnected.
	 */
	public UUID getPlayerUUID() {
		return player.getUniqueId();
	}
	
	/**
	 * Add an amount of mana to be refilled along time.
	 * @param mana to regenerate at the maximum.
	 * @return true if it's possible, false if mana is full.
	 */
	public boolean addManaRegen(int mana) {
		if(this.mana >= maxMana)
			return false;
		manaToRefill += mana;
		return true;
	}
	
	/**
	 * Add experience to the player. If levelup, send a message and recalculate things.
	 * <br />Calls {@link #calculateLevel()}
	 * @param xp : amount of experience points to add.
	 */
	public void addXp(int xp) {
		if(classe == Classe.NONE || !playerValid || xp <= 0)
			return;
		exp += xp;
		new PlayerUtils(player).sendActionBar(GREEN+"+ " + GOLD + xp + GREEN + " xp");
		if( calculateLevel() ) {
			player.sendMessage(HalystiaRPG.PREFIX + LIGHT_PURPLE + "Félicitation ! " + GREEN + "Tu passes niveau " + DARK_GREEN + "" + BOLD + level + GREEN + " !");
			player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2f, 1.1f);
		}
	}
	
	/**
	 * Force experience of player. Used only by administrators.
	 * <br />Calls {@link #calculateLevel()}
	 * @param xp : amount of experience points to add.
	 */
	public void forceXp(int xp) {
		if(classe == Classe.NONE)
			return;
		if(xp < 0)
			xp = 0;
		exp = xp;
		calculateLevel();
		player.sendMessage(HalystiaRPG.PREFIX + RED + "Attention ! " + GRAY + "Un opérateur a forcé ton exp à : " + exp + ".");
		player.sendMessage(HalystiaRPG.PREFIX + GRAY + "Tu passes niveau " + LIGHT_PURPLE + level + GRAY + ".");
	}
	
	/**
	 * Change the {@link fr.jamailun.halystia.players.Classe Classe} of a player.
	 * <br /><b>Carrefull !</b> Cannot be cancel.
	 * <br />This will reset experience of the player.
	 * @param classe : new {@link fr.jamailun.halystia.players.Classe Classe} of the player.
	 */
	public void changeClasse(Classe classe) {
		this.classe = classe;
		exp = 0;
		calculateLevel();
	}
	
	/**
	 * Test equality between two objects.
	 * @see {@linkplain java.lang.Object#equals(Object)}
	 */
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof PlayerData))
			return false;
		return ((PlayerData)o).getPlayerUUID().equals(getPlayerUUID());
	}
	
	/**
	 * Consume mana.
	 * @param mana : amount of mana to remove.
	 */
	public void consumeMana(int mana) {
		if(mana < 0)
			throw new IllegalArgumentException("Mana consumed cannot be < 0 !");
		this.mana -= mana;
	}
	
	/**
	 * Verify if a player can cast a spell.
	 * @param mana : amount of mana points to have.
	 * @return true if the player has enought mana.
	 */
	public boolean hasMana(int mana) {
		if(mana < 0)
			throw new IllegalArgumentException("Mana cannot be < 0 !");
		return this.mana >= mana;
	}
	
	public void refillMana(double mana) {
		if(mana < 0)
			mana = 0;
		if(manaToRefill < MANA_REFILL_PER_SECOND) {
			mana += manaToRefill;
			manaToRefill = 0;
		} else {
			mana += MANA_REFILL_PER_SECOND;
			manaToRefill -= MANA_REFILL_PER_SECOND;
		}
		this.mana += mana;
		if(this.mana > maxMana) {
			this.mana = maxMana;
			manaToRefill = 0;
		}
	}
	
	/**
	 * Character used in percentagnes bars.
	 */
	public final static String BAR_CHAR = new String(Character.toChars(9632));
	
	public String getExpBar(int size) {
		int level = getLevel();
		if(level == LEVEL_MAX)
			return GOLD + "Niveau max !";
		double percent = getPercentXp();
		
		StringBuilder builder = new StringBuilder(DARK_GRAY+"[");
		for(int i = 1; i <= size; i++) {
			double currentPercent = ((double)i) / ((double)size);
			if(currentPercent <= percent)
				builder.append(GREEN+BAR_CHAR);
			else
				builder.append(GRAY+BAR_CHAR);
		}
		builder.append(DARK_GRAY+"]");
		return builder.toString();
	}
	
	public double getPercentXp() {
		int level = getLevel();
		double lvlN0 = level == 1 ? 0 : getExpRequired(level);
		double lvlN1 = getExpRequired(level + 1);
		double filled = getExpAmount() - lvlN0;
		double upper = lvlN1 - lvlN0;
		double percent = filled / upper;
		return percent;
	}
	
	public int getManaProgress() {
		double prog = mana / maxMana;
		if(prog < 0)
			prog = 0;
		if(prog > 1)
			prog = 1;
		return (int) (prog*100);
	}
	
	public BarColor getManaBarColor() {
		double prog = mana / maxMana;
		if(prog < 0.1)
			return BarColor.RED;
		if(prog <= 0.5)
			return BarColor.YELLOW;
		return BarColor.BLUE;
	}
	
	public String getManaString() {
		return ChatColor.DARK_AQUA+""+ChatColor.BOLD+"Mana : " + ChatColor.AQUA + (int)mana + "/" + (int)maxMana
				+ ((manaToRefill > 0) ? ChatColor.LIGHT_PURPLE + "   (+"+(int)manaToRefill+")" : "");
	}
	
	public String getAmeString() {
		int souls = HalystiaRPG.getInstance().getDataBase().getHowManySouls(player);
		ChatColor color = ChatColor.DARK_RED;
		if(souls == 1)
			color = ChatColor.RED;
		else if(souls == 2)
			color = ChatColor.YELLOW;
		else if(souls == 3)
			color = ChatColor.GREEN;
		return color +""+ ChatColor.BOLD + "Âmes : " +color+ souls + ChatColor.DARK_AQUA + ",   ";
	}
	
	public int getCurrentKarma() {
		return karma;
	}
	
	public String getNiceKarma() {
		return getKarmaColor()+ karma;
	}
	
	public String getKarmaColor() {
		if(karma <= 300)
			return ChatColor.RED +"";
		if(karma >= 300)
			return ChatColor.GREEN +"";
		return ChatColor.WHITE +"";
	}
	
	public void deltaKarma(int delta) {
		karma += karma;
	}
	
	void reconnect(Player player) {
		this.player = player;
		playerValid = true;
	}
	
	void disconnect() {
		playerValid = false;
	}
	
	public boolean isPlayerValid() {
		return playerValid;
	}

	public void fullMana() {
		mana = maxMana;
	}
}