package fr.jamailun.halystia.spells.spellEntity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.mcmonkey.sentinel.SentinelTrait;

import fr.jamailun.halystia.HalystiaRPG;
import net.citizensnpcs.api.CitizensAPI;

/**
 * Special 'entity' (does NOT extends the bukkit's Entity). Who can emits particles and produce sounds.
 * <br/>Automaticly registered in the {@link SpellEntityManager SpellEntityManager}.
 * @see EffectSpellEntity
 * @see EffectAndDamageSpellEntity
 */
public abstract class SpellEntity {
	
	/**
	 * Current location of the entity.
	 */
	protected Location loc;
	/**
	 * Current direction of the entity.
	 */
	protected Vector direction;
	
	/**
	 * The caster of this entity.
	 */
	protected final Player launcher;
	
	/**
	 * Life duration.
	 */
	private final int life;
	
	/**
	 * Equals {@link #life}-(elapsed time).
	 */
	private int lifeTime;
	
	private boolean exists, doParticle, doSound;
	
	private final SpellEntityManager mgr;
	
	public SpellEntity(Location loc, Player launcher, int life) {
		this.mgr = HalystiaRPG.getInstance().getSpellManager().getSpellEntityManager();
		mgr.add(this);
		this.launcher = launcher;
		this.life = life;
		this.loc = loc.clone();
		this.direction = new Vector(0,0,0);
		
		
		lifeTime = 0;
		doParticle = false;
		doSound = false;
		exists = true;
	}
	
	/**
	 * Set the direction of this SpellEntity.
	 * @param direction Vecor to go to.
	 */
	public void setDirection(Vector direction) {
		this.direction = direction;
	}
	
	/**
	 * used by the manager. Do not call it unless you want the entity to live less time.
	 */
	void liveTick() {
		lifeTime++;
		loc = loc.add(direction).clone();
		effect();
		doThing();
		if(lifeTime >= life) {
			exists = false;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isValid() {
		return exists;
	}
	
	private List<Particle> pType = new ArrayList<>();
	private List<Integer> pCount = new ArrayList<>();
	private List<Double> pOV = new ArrayList<>(), pOH = new ArrayList<>(), pSpeed = new ArrayList<>();
	
	/**
	 * Add a classic particle effect. Will dure all the time.
	 */
	public void addParticleEffect(Particle type, int count, double offsetHorizontal, double offsetVertical, double speed) {
		doParticle = true;
		pType.add(type);
		pCount.add(count);
		pOV.add(offsetVertical);
		pOH.add(offsetHorizontal);
		pSpeed.add(speed);
	}
	
	/**
	 * Add a classic sound effect. Will dure all the time.
	 */
	public void addSoundEffect(Sound sound, float volume, float pitch) {
		doSound = true;
		sSound.add(sound);
		sVolume.add(volume);
		sPitch.add(pitch);
	}
	
	private List<Sound> sSound = new ArrayList<>();
	private List<Float> sVolume = new ArrayList<>(), sPitch = new ArrayList<>();
	
	/**
	 * Called at every loop of the life.
	 */
	protected abstract void doThing();
	
	private void effect() {
		if( ! exists)
			return;
		for(Player pl : getPlayersAround(loc, 100, true)) {
			if(doParticle)
				for(int i = 0; i < pType.size(); i++)
					pl.spawnParticle(pType.get(i), loc.getX(), loc.getY(), loc.getZ(), pCount.get(i), pOH.get(i), pOV.get(i), pOH.get(i), pSpeed.get(i));
			if(doSound)
				for(int i = 0; i < pType.size(); i++)
					pl.playSound(loc, sSound.get(i), sVolume.get(i), sPitch.get(i));
		}
	}
	
	/**
	 * @return a List of {@link org.bukkit.entity.Player players} who are around a location.
	 * @param loc source location.
	 * @param hurtHimSelf if true, the casting player will be in the list.
	 * @param distance maximal distance of the query.
	 * @see #getPlayersAround(Player, double, boolean)
	 */
	protected List<Player> getPlayersAround(Location loc, double distance, boolean hurtHimSelf) {
		List<Player> list = new ArrayList<>();
		for(Player pl : loc.getWorld().getPlayers()) {
			if(pl.getUniqueId().equals(launcher.getUniqueId()) && (!hurtHimSelf))
				continue;
			if(pl.getLocation().distance(loc) <= distance)
				list.add(pl);
		}
		return list;
	}
	
	/**
	 * @return a List of {@link org.bukkit.entity.LivingEntity entities} who are around a location.
	 * @param loc source location.
	 * @param hurtHimSelf if true, the casting player will be in the list.
	 * @param distance maximal distance of the query.
	 * @see #getPlayersAround(Player, double, boolean)
	 */
	protected List<LivingEntity> getEntitiesAround(Location loc, double distance, boolean hurtHimSelf) {
		List<LivingEntity> list = new ArrayList<>();
		for(Entity en : loc.getWorld().getEntities()) {
			if( ! (en instanceof LivingEntity))
				continue;
			LivingEntity entity = (LivingEntity) en;
			if(entity.getUniqueId().equals(launcher.getUniqueId()) && (!hurtHimSelf))
				continue;
			if(entity.getLocation().add(0,1.2,0).distance(loc) <= distance) {
				if(CitizensAPI.getNPCRegistry().isNPC(en)) {
					if( ! CitizensAPI.getNPCRegistry().getNPC(en).hasTrait(SentinelTrait.class))
						continue;
					else
						CitizensAPI.getNPCRegistry().getNPC(en).getTrait(SentinelTrait.class).targetingHelper.addTarget(launcher.getUniqueId());
				}
				list.add(entity);
			}
		}
		return list;
	}
	
	/**
	 * Get the Spell's caster.
	 * @return the caster.
	 */
	public Player getCaster() {
		return launcher;
	}
	
	/**
	 * Cancel an stop the SpellEntity.
	 */
	protected void cancel() {
		lifeTime +=life;
	}
	
}
