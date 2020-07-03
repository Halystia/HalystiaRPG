package fr.jamailun.spellParser.structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.jamailun.halystia.spells.spellEntity.EffectAndDamageSpellEntity;
import fr.jamailun.spellParser.contexts.ApplicativeContext;
import fr.jamailun.spellParser.contexts.TokenContext;
import fr.jamailun.spellParser.data.PotionTypesReader;
import fr.jamailun.spellParser.structures.abstraction.DataBlockStructure;

public class CreateStructure extends DataBlockStructure {

	public static final String REGEX = "create from %[A-Za-z0-9_] with \\{";
	private final static String START_PARTICLE = "particles", START_SOUND = "sounds", START_EFFECT = "effects";
	private final static int ARRAY_SIZE = 10;
	
	private List<PotionEffect> effects = new ArrayList<>();
	
	private Particle[] particlesType = new Particle[ARRAY_SIZE];
	private int[] particlesCount = new int[ARRAY_SIZE];
	private double[] particlesSpeed = new double[ARRAY_SIZE], particlesOX = new double[ARRAY_SIZE], particlesOY = new double[ARRAY_SIZE];
	private Sound[] soundsType = new Sound[ARRAY_SIZE];
	private float[] soundsVolume = new float[ARRAY_SIZE], soundsPitch = new float[ARRAY_SIZE];
	private PotionEffectType[] effectTypes = new PotionEffectType[ARRAY_SIZE];
	private int[] effectDurations = new int[ARRAY_SIZE], effectAmplifier = new int[ARRAY_SIZE];
	private boolean[] effectHide = new boolean[ARRAY_SIZE];
	
	public CreateStructure(TokenContext context) {
		super(context);
	}

	@Override
	public void registerData(String key, String value) {
		if(key.startsWith(START_PARTICLE) && key.contains("]_")) {
			int id = getFromArray(value);
			String mode = key.split("_")[1];
			if(mode.equals("type")) {
				try {
					Particle type = Particle.valueOf(value);
					particlesType[id] = type;
				} catch(IllegalArgumentException e) {
					System.err.println("Error in CREATE structure : illegal particle type '"+value+"' (id="+id+").");
				}
				return;
			}
			if(mode.equals("count")) {
				try {
					int data = Integer.parseInt(value);
					particlesCount[id] = data;
				} catch(IllegalArgumentException e) {
					System.err.println("Error in CREATE structure : illegal integer 'count' format (id="+id+").");
				}
				return;
			}
			if(mode.equals("speed")) {
				try {
					double data = Double.parseDouble(value);
					particlesSpeed[id] = data;
				} catch(IllegalArgumentException e) {
					System.err.println("Error in CREATE structure : illegal double 'speed' format (id="+id+").");
				}
				return;
			}
			if(mode.equals("offset-xz")) {
				try {
					double data = Double.parseDouble(value);
					particlesOX[id] = data;
				} catch(IllegalArgumentException e) {
					System.err.println("Error in CREATE structure : illegal double 'offset-xz' format (id="+id+").");
				}
				return;
			}
			if(mode.equals("offset-y")) {
				try {
					double data = Double.parseDouble(value);
					particlesOY[id] = data;
				} catch(IllegalArgumentException e) {
					System.err.println("Error in CREATE structure : illegal double 'offset-y' format (id="+id+").");
				}
				return;
			}
			System.err.println("Error in CREATE structure : unknown particle data name : '"+mode+"' (id="+id+").");
			return;
		}
		if(key.startsWith(START_SOUND) && key.contains("]_")) {
			int id = getFromArray(value);
			String mode = key.split("_")[1];
			if(mode.equals("type")) {
				try {
					Particle type = Particle.valueOf(value);
					particlesType[id] = type;
				} catch(IllegalArgumentException e) {
					System.err.println("Error in CREATE structure : illegal particle type '"+value+"' (id="+id+").");
				}
				return;
			}
			if(mode.equals("pitch")) {
				try {
					double data = Double.parseDouble(value);
					soundsPitch[id] = (float) data;
				} catch(IllegalArgumentException e) {
					System.err.println("Error in CREATE structure : illegal double 'pitch' format (id="+id+").");
				}
				return;
			}
			if(mode.equals("volume")) {
				try {
					double data = Double.parseDouble(value);
					soundsVolume[id] = (float) data;
				} catch(IllegalArgumentException e) {
					System.err.println("Error in CREATE structure : illegal double 'volume' format (id="+id+").");
				}
				return;
			}
			System.err.println("Error in CREATE structure : unknown soud data name : '"+mode+"' (id="+id+").");
			return;
		}
		if(key.startsWith(START_EFFECT) && key.contains("]_")) {
			int id = getFromArray(value);
			String mode = key.split("_")[1];
			if(mode.equals("type")) {
				PotionEffectType type = PotionTypesReader.getFromString(value);
				if( type == null ) {
					System.err.println("Error in CREATE structure : illegal effect type '"+value+"' (id="+id+").");
					return;
				}
				effectTypes[id] = type;
				return;
			}
			if(mode.equals("duration")) {
				try {
					int data = Integer.parseInt(value);
					effectDurations[id] = data;
				} catch(IllegalArgumentException e) {
					System.err.println("Error in CREATE structure : illegal integer 'duration' format (id="+id+").");
				}
				return;
			}
			if(mode.equals("force")) {
				try {
					int data = Integer.parseInt(value);
					effectAmplifier[id] = data - 1; // force = amplifier + 1
				} catch(IllegalArgumentException e) {
					System.err.println("Error in CREATE structure : illegal integer 'force' format (id="+id+").");
				}
				return;
			}
			if(mode.equals("hide")) {
				if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
					effectHide[id] = value.equalsIgnoreCase("true");
					return;
				}
				try {
					int data = Integer.parseInt(value);
					effectHide[id] = data > 0;
				} catch(IllegalArgumentException e) {
					System.err.println("Error in CREATE structure : illegal boolean 'hide' format (id="+id+").");
				}
				return;
			}
			System.err.println("Error in CREATE structure : unknown soud data name : '"+mode+"' (id="+id+").");
			return;
		}
		super.registerData(key, value);
	}
	
	@Override
	public void apply(ApplicativeContext context) {
		String targetID = this.context.getDefinition(target);
		if ( ! context.isDefinedHasEntity(targetID) ) {
			//error msg
			return;
		}
		Entity entity = context.getEntity(targetID);
		if(entity == null)
			return;
		if( ! (entity instanceof LivingEntity) )
			return;
		EffectAndDamageSpellEntity spell = new EffectAndDamageSpellEntity(entity.getLocation(), (LivingEntity) entity,
				(int) getDoubleData("duration"),
				getDoubleData("range"),
				getBooleanData("hurt-caster"),
				getBooleanData("stop-on-hit")
		);
		spell.setDamages(Math.max(0, getDoubleData("damages")));
		if(isDataSet("speed"))
			spell.setDirection(entity.getVelocity().normalize().multiply(getDoubleData("speed")));
		if(isDataSet("fire-duration"))
			spell.setFireTick((int)getDoubleData("fire-duration"));
		if(isDataSet("y-force"))
			spell.setYForce(getDoubleData("y-force"));
		for(int i = 0; i < ARRAY_SIZE; i++) {
			if(effectTypes[i] != null)
				spell.addPotionEffect(new PotionEffect(effectTypes[i],
						Math.min(0, effectDurations[i]), Math.min(0, effectAmplifier[i]), effectHide[i]));
		}
		spell.setPotionEffects(effects);
		for(int i = 0; i < ARRAY_SIZE; i++) {
			if(particlesType[i] != null)
				spell.addParticleEffect(particlesType[i], Math.max(1, particlesCount[i]), Math.max(0.001, particlesOX[i]), Math.max(0.001, particlesOY[i]), Math.max(0.001, particlesSpeed[i]));
		}
		for(int i = 0; i < ARRAY_SIZE; i++) {
			if(soundsType[i] != null)
				spell.addSoundEffect(soundsType[i], Math.max(0.001f, soundsVolume[i]), Math.max(0.001f, soundsPitch[i]) );
		}
	}

	@Override
	public List<String> getAllKeys() {
		return keys;
	}
	
	private static List<String> keys;
	static {
		keys = Arrays.asList("speed", "damages", "y-force", "hurt-caster", "duration", "stop-on-hit", "range", "ignores-armor", "fire-duration");
		for(int i = 0; i < 10; i++) {
			keys.add(START_PARTICLE+"["+i+"]_type");
			keys.add(START_PARTICLE+"["+i+"]_count");
			keys.add(START_PARTICLE+"["+i+"]_speed");
			keys.add(START_PARTICLE+"["+i+"]_offset-xz");
			keys.add(START_PARTICLE+"["+i+"]_offset-y");

			keys.add(START_SOUND+"["+i+"]_type");
			keys.add(START_SOUND+"["+i+"]_volume");
			keys.add(START_SOUND+"["+i+"]_pitch");

			keys.add(START_EFFECT+"["+i+"]_type");
			keys.add(START_EFFECT+"["+i+"]_duration");
			keys.add(START_EFFECT+"["+i+"]_force");
			keys.add(START_EFFECT+"["+i+"]_hide");
		}
	}
	
	private int getFromArray(String value) {
		if( ! value.matches("[A-Za-z\\-_...]+\\[[0-9]\\]_[A-Za-z0-9_...]+"))
			return -1;
		try {
			String right = value.split("\\[")[1];
			String looked = right.split("\\]")[0];
			
			return Integer.parseInt(looked);
			
		} catch(NumberFormatException | IndexOutOfBoundsException e) {
			return -1;
		}
	}
}