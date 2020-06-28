package fr.jamailun.spellParser.structures;

import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.spellParser.contexts.ApplicativeContext;
import fr.jamailun.spellParser.contexts.TokenContext;
import fr.jamailun.spellParser.structures.abstraction.BlockStructure;

public class DelayStructure extends BlockStructure {

	public final static String REGEX = "delay (during|of) [0-9]+ (tick|ticks|second|seconds) (then|do|then do) \\{";
	
	private int time = 1;
	private String unit = "ticks";
	
	public DelayStructure(TokenContext context) {
		super(context);
	}

	public void setDurationInteger(int time) {
		if(time <= 0) {
			System.err.println("Error in DELAY : duration should be greater than 0.");
			invalidate();
			return;
		}
		this.time = time;
	}
	
	public void setUnitString(String unit) {
		if(unit == null) {
			System.err.println("Error in DELAY : unit not valid.");
			invalidate();
			return;
		}
		if(unit.equalsIgnoreCase("second") || unit.equalsIgnoreCase("seconds") || unit.equalsIgnoreCase("tick") || unit.equalsIgnoreCase("ticks")) {
			this.unit = unit;
			return;
		}
		invalidate();
		System.err.println("Error : DELAY, unknown time unit : '"+unit+"'");
	}
	
	@Override
	public void apply(ApplicativeContext context) {
		long period = (unit.equals("ticks") || unit.equals("tick")) ? 1L : 20L;
		new BukkitRunnable() {
			@Override
			public void run() {
				children.forEach(str -> {
					if(str.isValid())
						str.apply(context);
				});
			}
		}.runTaskLater(HalystiaRPG.getInstance(), time * period);
	}

}