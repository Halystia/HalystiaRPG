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

public abstract class SpellEntity {
	
	protected Location loc;
	protected Vector direction;
	
	private Player launcher;
	private int lifeTime, life;
	
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
	
	public void setDirection(Vector direction) {
		this.direction = direction;
	}
	
	public void liveTick() {
		lifeTime++;
		loc = loc.add(direction).clone();
		doThing();
		if(lifeTime >= life) {
			exists = false;
		}
	}
	
	public boolean isValid() {
		return exists;
	}
	
	private Particle pType;
	private int pCount;
	private double pOV, pOH, pSpeed;
	
	public void addParticleEffect(Particle type, int count, double offsetHorizontal, double offsetVertical, double speed) {
		doParticle = true;
		pType = type;
		pCount = count;
		pOV = offsetVertical;
		pOH = offsetHorizontal;
		pSpeed = speed;
	}
	
	public void addSoundEffect(Sound sound, float volume, float pitch) {
		doSound = true;
		this.sound = sound;
		this.volume = volume;
		this.pitch = pitch;
	}
	
	private Sound sound;
	private float volume, pitch;
	
	protected abstract void doThing();
	
	public void effect() {
		if( ! exists)
			return;
		for(Player pl : getPlayersAround(loc, 100, true)) {
			if(doParticle)
				pl.spawnParticle(
					pType, 
					loc.getX(), loc.getY(), loc.getZ(), 
					pCount,
					pOH, pOV, pOH,
				pSpeed
				);
			
			if(doSound)
				pl.playSound(loc, sound, volume, pitch);
		}
	}
	
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
	
	protected List<LivingEntity> getEntityAround(Location loc, double distance, boolean hurtHimSelf) {
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
	
	public Player getLauncher() {
		return launcher;
	}
	
	protected void cancel() {
		lifeTime +=life;
	}
	
}
