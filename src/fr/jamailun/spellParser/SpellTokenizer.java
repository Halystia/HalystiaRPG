package fr.jamailun.spellParser;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.jamailun.spellParser.contexts.ApplicativeContext;
import fr.jamailun.spellParser.contexts.TokenContext;
import fr.jamailun.spellParser.structures.ApplyEffectStructure;
import fr.jamailun.spellParser.structures.DamageStructure;
import fr.jamailun.spellParser.structures.DefineStructure;
import fr.jamailun.spellParser.structures.DelayStructure;
import fr.jamailun.spellParser.structures.ForLoopStructure;
import fr.jamailun.spellParser.structures.GlobalStructure;
import fr.jamailun.spellParser.structures.HealStructure;
import fr.jamailun.spellParser.structures.MathStructure;
import fr.jamailun.spellParser.structures.ParticlesStructure;
import fr.jamailun.spellParser.structures.SendMessageStructure;
import fr.jamailun.spellParser.structures.SpawnStructure;
import fr.jamailun.spellParser.structures.ThrowStructure;

public class SpellTokenizer {

	private TokenContext context;

	private GlobalStructure global;

	public static final String GROUP_ENTITIES = "#entities";

	public SpellTokenizer() {
		context = new TokenContext().createChild();
		global = new GlobalStructure(context);
	}

	private int lineNumber = 0;

	/**
	 * Read a line of a spell file
	 * @param line The whole line to parse
	 * @param lineNumber number of the line. Used for logging errors.
	 * @return true only if the parser consider the parsing as over.
	 */
	public boolean readLine(String line, int lineNumber) {
		this.lineNumber = lineNumber;
		while(line.startsWith(" ") || line.startsWith("\t")) {
			line = line.replaceFirst(" |\\t", "");
		}
		if(line.isEmpty() || line.startsWith("#"))
			return false;

		if(line.startsWith("}")) {
			exitContext();
			return global.closeBlock();
		}

		String[] words = line.split(" ");

		if(words[0].equalsIgnoreCase("define")) {
			define(line);
			return false;
		}

		if(words[0].equalsIgnoreCase("for")) {
			forLoop(line);
			return false;
		}

		if(words[0].equalsIgnoreCase("damage")) {
			damage(line);
			return false;
		}

		if(words[0].equalsIgnoreCase("apply")) {
			applyEffect(line);
			return false;
		}

		if(words[0].equalsIgnoreCase("heal")) {
			heal(line);
			return false;
		}

		if(words[0].equalsIgnoreCase("send")) {
			send(line);
			return false;
		}

		if(words[0].equalsIgnoreCase("spawn")) {
			spawn(line);
			return false;
		}
		
		if(words[0].equalsIgnoreCase("delay")) {
			delayBlock(line);
			return false;
		}
		
		if(words[0].equalsIgnoreCase("throw")) {
			throwBlock(line);
			return false;
		}
		
		if(words[0].equalsIgnoreCase("emit")) {
			emitBlock(line);
			return false;
		}
		
		if(words[0].equalsIgnoreCase("math")) {
			maths(line);
			return false;
		}

		if(global.isInData()) {
			if( ! line.matches("[A-Za-z_\\-]+(| *):(| *)[\\pN\\pL.&\\s%?!:]+") ) {
				System.err.println("Error : bad data format ("+line+"), on line " + lineNumber+".");
				return false;
			}
			String[] parts = line.split(":", 2);
			while(parts[1].startsWith(" "))
				parts[1] = parts[1].substring(1);
			global.addData(parts[0].replaceAll(" ", ""), parts[1]);
			return false;
		}

		Bukkit.getConsoleSender().sendMessage("§cError : unknown symbol : '"+words[0]+"' on line n°"+lineNumber+".");
		return false;
	}

	private void maths(String line) {
		if( ! line.matches(MathStructure.REGEX)) {
			System.err.println("Error : bad format on line " + lineNumber+". Used here : '"+line+"'.");
			System.err.println("Use '"+MathStructure.REGEX+"'.");
			return;
		}
		MathStructure structure = new MathStructure(context);
		structure.read(line, lineNumber);
		global.add(structure);
	}

	private void emitBlock(String line) {
		if ( ! line.matches(ParticlesStructure.REGEX) ) {
			System.err.println("Error : bad format on line " + lineNumber+". Used here : '"+line+"'.");
			System.err.println("Use '"+ParticlesStructure.REGEX+"'.");
			return;
		}
		ParticlesStructure structure = new ParticlesStructure(context);
		structure.read(line);
		global.openBlock(structure);
	}

	private void throwBlock(String line) {
		if ( ! line.matches(ThrowStructure.REGEX) ) {
			System.err.println("Error : bad format on line " + lineNumber+". Used here : '"+line+"'.");
			System.err.println("Use '"+ThrowStructure.REGEX+"'.");
			return;
		}
		String[] words = line.split(" ");
		ThrowStructure structure = new ThrowStructure(context, words[1]);
		structure.defineTarget(words[3]);
		global.openBlock(structure);
	}

	private void spawn(String line) {
		if ( ! line.matches(SpawnStructure.REGEX) ) {
			System.err.println("Error : bad format on line " + lineNumber+". Used here : '"+line+"'.");
			System.err.println("Use '"+SpawnStructure.REGEX+"'.");
			return;
		}
		SpawnStructure structure = new SpawnStructure(context);
		structure.read(line);
		global.openBlock(structure);
	}
	
	private void delayBlock(String line) {
		if ( ! line.matches(DelayStructure.REGEX) ) {
			System.err.println("Error : bad format on line " + lineNumber+". Used here : '"+line+"'.");
			System.err.println("Use '"+DelayStructure.REGEX+"'.");
			return;
		}
		String[] words = line.split(" ");
		DelayStructure structure = new DelayStructure(context);
		structure.setDurationInteger(Integer.parseInt(words[2]));
		structure.setUnitString(words[3]);
		global.openBlock(structure);
	}

	private void define(String line) {
		if( ! line.matches(DefineStructure.REGEX) ) {
			System.err.println("Bad format : " + line);
			System.err.println("Use '"+DefineStructure.REGEX+"'" + " on line " + lineNumber+".");
			return;
		}
		
		DefineStructure structure = new DefineStructure(context);
		String[] words = line.split(" ");
		//define %defined as [closest] <thing> from %target within (dist)
		structure.setDefinitionString(words[1]);
		structure.setModeString(words[3]);
		structure.setSelectorString(words[4]);
		structure.defineTarget(words[6]);
		try {
			structure.setRangeDouble(Double.parseDouble(words[8]));
		} catch(NumberFormatException e) {
			System.err.println("Error line " + lineNumber + " : bad range double format.");
			return;
		}
		
		global.add(structure);
	}

	private void enterSubContext() {
		context = context.createChild();
	}

	private void exitContext() {
		if(global.isInData())
			return;
		if ( ! context.hasParent() ) {
			System.err.println("Error : context has no parent (line n°" + lineNumber + ").");
			return;
		}
		context = context.getParent();
	}

	private void forLoop(String line) {
		if( ! line.matches(ForLoopStructure.REGEX)) {
			System.err.println("Bad format : " + line);
			System.err.println("Use 'for <target> as <var> around <entity> in <r> do {'" + " on line " + lineNumber+".");
			return;
		}
		enterSubContext();

		String[] words = line.split(" ");
		ForLoopStructure loop = new ForLoopStructure(context, words[1], words[3]);
		loop.setAroundValue(words[5]);
		loop.setRangeDouble(Double.parseDouble(words[7]));

		global.openBlock(loop);
	}

	private void damage(String line) {
		if( ! line.matches(DamageStructure.REGEX)) {
			System.err.println("Bad format : " + line);
			System.err.println("Use '"+DamageStructure.REGEX+"'" + " on line " + lineNumber+".");
			return;
		}
		String[] words = line.split(" ");
		DamageStructure struct = new DamageStructure(context);
		struct.defineTarget(words[1]);
		struct.setDamageInt(Integer.parseInt(words[3]));
		if ( struct.isValid() )
			global.add(struct);
	}

	private void heal(String line) {
		if( ! line.matches(HealStructure.REGEX)) {
			System.err.println("Bad format : " + line);
			System.err.println("Use '"+HealStructure.REGEX+"'" + " on line " + lineNumber+".");
			return;
		}
		String[] words = line.split(" ");
		HealStructure struct = new HealStructure(context);
		struct.defineTarget(words[1]);
		struct.setHealInt(Integer.parseInt(words[3]));
		if ( struct.isValid() )
			global.add(struct);
	}

	private void applyEffect(String line) {
		if( ! line.matches(ApplyEffectStructure.REGEX)) {
			System.err.println("Bad format : " + line);
			System.err.println("Use '"+ApplyEffectStructure.REGEX+"'" + " on line " + lineNumber+".");
			return;
		}
		String[] words = line.split(" ");
		ApplyEffectStructure struct = new ApplyEffectStructure(context);
		struct.defineTarget(words[7]);
		struct.setEffectString(words[1]);
		struct.setDurationInt(Integer.parseInt(words[4]), words[5]);
		struct.setForceInt(Integer.parseInt(words[2]));
		if ( struct.isValid() )
			global.add(struct);
	}

	private void send(String line) {
		if( ! line.matches(SendMessageStructure.REGEX)) {
			System.err.println("Bad format : " + line);
			System.err.println("Use '"+SendMessageStructure.REGEX+"'" + " on line " + lineNumber+".");
			return;
		}
		String[] parts = line.split("\"", 3);
		String[] words = line.split(" ");
		SendMessageStructure struct = new SendMessageStructure(context);
		struct.defineTarget(words[words.length - 1]);
		struct.setMessage(parts[1]);
		if ( struct.isValid() )
			global.add(struct);
	}

	public boolean isFinished() {
		return global.isFinished();
	}

	public boolean isInLastPart() {
		return ! context.hasParent();
	}

	public void run(Player caster) {
		global.apply(new ApplicativeContext(caster));
	}
}