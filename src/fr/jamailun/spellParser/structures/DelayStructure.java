package fr.jamailun.spellParser.structures;

import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.spellParser.contexts.ApplicativeContext;
import fr.jamailun.spellParser.contexts.TokenContext;
import fr.jamailun.spellParser.data.TimeUnit;
import fr.jamailun.spellParser.structures.abstraction.BlockStructure;

public class DelayStructure extends BlockStructure {

	public final static String REGEX = "delay (during|of) [0-9]+ (tick|ticks|second|seconds) (then|do|then do) \\{";
	
	private int time = 1;
	private TimeUnit unit = TimeUnit.SECOND;
	
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
		TimeUnit tUnit = TimeUnit.fromString(unit);
		if(tUnit == null) {
			invalidate();
			System.err.println("Error : DELAY, unknown time unit : '"+unit+"'");
			return;
		}
		this.unit = tUnit;
	}
	
	@Override
	public void apply(ApplicativeContext context) {
		new BukkitRunnable() {
			@Override
			public void run() {
				children.forEach(str -> {
					if(str.isValid())
						str.apply(context);
				});
			}
		}.runTaskLater(HalystiaRPG.getInstance(), time * unit.getTicksDuration() * 1L);
	}

}