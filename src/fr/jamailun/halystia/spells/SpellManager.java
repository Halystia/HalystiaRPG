package fr.jamailun.halystia.spells;

import static org.bukkit.ChatColor.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.*;
import fr.jamailun.halystia.players.*;
import fr.jamailun.halystia.spells.newSpells.alchimiste.*;
import fr.jamailun.halystia.spells.newSpells.archer.*;
import fr.jamailun.halystia.spells.newSpells.invocateur.*;
import fr.jamailun.halystia.spells.newSpells.epeiste.*;
import fr.jamailun.halystia.spells.spellEntity.*;
import fr.jamailun.halystia.utils.ItemBuilder;

/**
 * Handle all spells in the game.<br />
 * You can add a {@link fr.jamailun.halystia.spells.Spell Spell} with the method {@link #addSpell(Class)}.
 */
public class SpellManager {
	
	private Map<UUID, Long> cooldowns;
	private List<Spell> spells;
	
	private final SpellEntityManager mgr;
	private final InvocationsManager invocs;
	
	private final HalystiaRPG main;
	
	/**
	 * Do not use that.
	 */
	public SpellManager(HalystiaRPG main) {
		this.main = main;
		mgr = new SpellEntityManager();
		invocs = new InvocationsManager(main);
		cooldowns = new HashMap<>();
		
		spells = new ArrayList<>();
		// Spells pout alchimiste
		addSpell(SoinsPrimaires.class);
		addSpell(Toxine.class);
		addSpell(Flameche.class);
		addSpell(BenedictionNaturelle.class);
		addSpell(SoinsPerfectionnes.class);
		addSpell(Sommeil.class);
		addSpell(Etincelle.class);
		addSpell(SoinsUltimes.class);
		addSpell(JetVolcanique.class);

		// Spells pout invocateur
		addSpell(InvocationBasique.class);
		addSpell(ResistanceElementaire.class);
		addSpell(DiversionDeGrele.class);
		addSpell(Revenants.class);
		addSpell(FrappeTerrestre.class);
		addSpell(Corruption.class);
		addSpell(InvocationMajeure.class);
		addSpell(InvocationArmee.class);
		addSpell(CatapulteCeleste.class);

		addSpell(ProtectionElementaire.class);

		// Spells pout archer
		addSpell(PluieAceree.class);
		addSpell(Echappatoire.class);
		addSpell(Propulsion.class);
		addSpell(AcierEffrene.class);
		addSpell(Disparition.class);
		addSpell(VitesseExtreme.class);
		addSpell(ManaCeleste.class);
		addSpell(PluieDacier.class);
		addSpell(Partageforce.class);

		// Spells pout épéiste
		addSpell(ExuvationSimple.class);
		addSpell(RepliqueFeu.class);
		addSpell(FracassTete.class);
		addSpell(Vague.class);
		addSpell(Damocles.class);
		addSpell(Partageforce.class);
		addSpell(ExuvationComplexe.class);
		addSpell(AcierBrut.class);
		addSpell(AcierPrecis.class);
		
	}
	/**
	 * Add spell to the system.
	 * @param spell {@link fr.jamailun.halystia.spells.Spell Spell} to add.
	 * @return true if spell could be created. false if error happened.
	 */
	public boolean addSpell(Class<? extends Spell> spell) {
		if(spell == null) {
			Bukkit.getLogger().log(Level.SEVERE, "Spell argument cannot be null !");
			return false;
		}
		try {
			Spell instance = spell.newInstance();
			instance.init();
			spells.add(instance);
			return true;
		} catch (InstantiationException | IllegalAccessException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Spell's class ("+spell+") could not be loaded.");
			return false;
		}
	}
	
	/**
	 * @return {@link fr.jamailun.halystia.spells.spellEntity.SpellEntity SpellEntity} registered by system.
	 */
	public SpellEntityManager getSpellEntityManager() {
		return mgr;
	}
	
	/**
	 * @return {@link fr.jamailun.halystia.spells.spellEntity.InvocationsManager InvocationsManager} registered by system.
	 */
	public InvocationsManager getInvocationsManager() {
		return invocs;
	}
	
	public List<String> getAllSpellsName() {
		List<String> list = new ArrayList<>();
		for(Spell s : spells)
			list.add(s.getStringIdentification());
		return list;
	}
	
	/**
	 *  Get a spell with is identifier.
	 * @param id : identifier of the spell.
	 * @return null if the identifier is not used.
	 */
	public Spell getSpellOfIdentification(String id) {
		for(Spell spell : getAllSpells())
			if(spell.getStringIdentification().equals(id))
				return spell;
		return null;
	}
	
	/**
	 * Get all registered spells.
	 * @return a <b>copy</b> of all registered {@link fr.jamailun.halystia.spells.Spell Spell}.
	 */
	private List<Spell> getAllSpells() {
		return new ArrayList<>(spells);
	}
	
	/**
	 * Get the spell in an item.
	 * @param item ItemStack to read threw.
	 * @return null if no {@link fr.jamailun.halystia.spells.Spell Spell} detected.
	 */
	public Spell getSpellOfItem(ItemStack item) {
		try {
			if(item == null)
				return null;
			if(item.getType() != Material.PAPER)
				return null;
			
			ItemMeta meta = item.getItemMeta();
			List<String> lore = meta.getLore();
			String last = lore.get(lore.size() - 1).split(" ")[1];
			
			for(Spell spell : getAllSpells())
				if(spell.getStringIdentification().contains(last))
					return spell;
		} catch (NullPointerException | IndexOutOfBoundsException e) {}
		return null;
	}
	
	/**
	 * try to cast the spellof a Player with his item in main hand.
	 * @return true if {@link fr.jamailun.halystia.spells.Spell Spell} has been casted.
	 */
	public boolean tryCastSpell(Player p) {
		if(actives.contains(p)) {
			return false;
		}
		actives.add(p);
		scheduleRemove(p);
		
		Spell spell = getSpellOfItem(p.getInventory().getItemInMainHand());
		if(spell == null) {
			spell = getSpellOfItem(p.getInventory().getItemInOffHand());
			if(spell == null)
				return false;
		}
		
		synchronized (spell) {
			if(p.getGameMode() == GameMode.CREATIVE) {
				spell.cast(p);
				return true;
			}
			
			PlayerData pc = main.getClasseManager().getPlayerData(p);
			if(pc == null)
				return false;
			if(spell.getClasseRequired() != Classe.NONE) {
				if(pc.getClasse() != spell.getClasseRequired()) {
					p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu n'as pas la classe nécessaire à la maitrise de ce sort.");
					return false;
				}
			}
			if(pc.getLevel() < spell.getLevelRequired()) {
				p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu n'as pas le niveau nécessaire à la maitrise de ce sort.");
				return false;
			}
			
			int cd = getCooldown(p);
			if(cd > 0) {
				p.sendMessage(HalystiaRPG.PREFIX + RED + "Tu dois encore attendre " + DARK_RED + getNiceCooldown(cd) + RED + " avant de pouvoir relancer un sort.");
				return false;
			}
			
			if(spell.getManaCost() > 0) {
				if(!pc.hasMana(spell.getManaCost())) {
					p.sendMessage(HalystiaRPG.PREFIX + RED + "Il te faut " + DARK_AQUA + spell.getManaCost() + RED + " points de mana pour lancer ce sort !");
					return false;
				}
			}
			if(!spell.cast(p))
				return true;
			
			if(spell.getCooldown() > 0)
				applyCooldown(p, spell.getCooldown());
			pc.consumeMana(spell.getManaCost());
			
			p.sendMessage(HalystiaRPG.PREFIX + GREEN + "Sort " + spell.getColor() + "[" + spell.getName() + spell.getColor() + "]" + GREEN + " lancé. " + AQUA + "-" + spell.getManaCost() + "mana" + GREEN + ".");
			
			return true;
		}
	}
	
	/**
	 * Show cooldonws with cool maners.
	 * @param secs seconds of cooldown.
	 * @return a nice String.
	 */
	public String getNiceCooldown(int secs) {
		int h = 0;
		while(secs >= 3600) {
			secs -= 3600;
			h++;
		}
		int m = 0;
		while(secs >= 60) {
			secs -= 60;
			m++;
		}
		String heures = (h==0 ? "" : h+"h");
		String minutes = (m==0 ? "" : m+"m");
		String secondes = (secs==0 ? "" : secs+" secondes");
		return heures+minutes+secondes;
	}
	
	/**
	 * Substract cooldown of a player.
	 * @param p concerned {@link org.bukkit.entity.Player Player}.
	 * @param cooldown : amount of seconds to substract.
	 */
	public void substractCooldown(Player p, int cooldown) {
		int cd = getCooldown(p);
		cd -= cooldown;
		if(cd < 0)
			cd = 0;
		applyCooldown(p, cd);
	}
	
	/**
	 * Add cooldown to a player.
	 * @param p concerned {@link org.bukkit.entity.Player Player}.
	 * @param cooldown : amount of seconds to add.
	 */
	public void applyCooldown(Player p, int cooldown) {
		final UUID uuid = p.getUniqueId();
		if(cooldowns.containsKey(uuid)) {
			cooldowns.replace(uuid, System.currentTimeMillis() + (cooldown * 1000));
			return;
		}
		cooldowns.put(uuid, System.currentTimeMillis() + (cooldown * 1000));
	}
	
	/**
	 * Get cooldown of a player (in seconds).
	 * @param p concerned {@link org.bukkit.entity.Player Player}.
	 */
	public int getCooldown(Player p) {
		final UUID uuid = p.getUniqueId();
		if( ! cooldowns.containsKey(uuid))
			return 0;
		long end = cooldowns.get(uuid);
		return (int) ( (end - System.currentTimeMillis()) / 1000 );
	}
	
	/**
	 * Generate spell item.
	 * @param {@link fr.jamailun.halystia.spells.Spell Spell} to transform. 
	 * @return the spell paper.
	 */
	public ItemStack generateItem(Spell spell) {
		ItemBuilder builder = new ItemBuilder(Material.PAPER);
		builder.setName(spell.getColor()+"" + BOLD + spell.getName());
		builder.setLore(spell.getLore()).addLoreLine(GRAY + " ");
		
		if(spell.getClasseRequired() == Classe.NONE) {
			if(spell.getLevelRequired() > 1)
				builder.addLoreLine(GRAY+"Requiert classe de niveau "+DARK_PURPLE+ spell.getLevelRequired()+GRAY+".");
			else
				builder.addLoreLine(GREEN +"Aucune classe requise.");
		} else {
			builder.addLoreLine(GRAY+"Sort d'"+DARK_PURPLE+spell.getClasseRequired().getName().toLowerCase()+ GRAY +" de niveau " +DARK_PURPLE+ spell.getLevelRequired()+GRAY+".");
		}
		
		if(spell.getManaCost() > 0)
			builder.addLoreLine(GRAY +"Coûte " + AQUA + spell.getManaCost() + " points de mana" + GRAY + ".");
		else
			builder.addLoreLine(GREEN +"Aucun point de" +AQUA+"mana" +GREEN+" n'est nécessaire.");
			
		builder.addLoreLine(BLACK+""+ITALIC+" "+spell.getStringIdentification());
		
		return builder.toItemStack();
	}
	
	private List<Player> actives = new ArrayList<>();
	private void scheduleRemove(Player p) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					actives.remove(p);
				} catch(Exception e) {
					scheduleRemove(p);
				}
			}
		}.runTaskLater(main, 10L);
	}
	
	/**
	 * Reset the numbers of invocations for all players.
	 * Please do not use it.
	 */
	public void resetInvocations() {
		for(Spell spell : spells) {
			if( ! (spell instanceof InvocationSpell))
				continue;
			((InvocationSpell)spell).reset();
		}
	}
	
}
