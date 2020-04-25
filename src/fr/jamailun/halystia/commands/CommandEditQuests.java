package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;
import static org.bukkit.ChatColor.YELLOW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.enemies.mobs.MobManager;
import fr.jamailun.halystia.npcs.NpcManager;
import fr.jamailun.halystia.npcs.RpgNpc;
import fr.jamailun.halystia.quests.Messages;
import fr.jamailun.halystia.quests.Quest;
import fr.jamailun.halystia.quests.QuestManager;
import fr.jamailun.halystia.quests.steps.QuestStep;
import fr.jamailun.halystia.quests.steps.QuestStepType;

public class CommandEditQuests extends HalystiaCommand {

	private static final Set<String> firsts = new HashSet<>(Arrays.asList("create", "remove", "level", "loots", "xp", "rename", "steps", "list", "intro", "reset", "reload", "tags"));
	private static final Set<String> loots = new HashSet<>(Arrays.asList("clear", "list", "add", "remove"));
	private static final Set<String> steps = new HashSet<>(Arrays.asList("list", "create", "remove", "message", "loot"));
	private static final Set<String> messages = new HashSet<>(Arrays.asList("see", "add", "remove", "clear", "set", "insert"));
	private static final Set<String> lootStep = new HashSet<>(Arrays.asList("see", "set", "clear"));
	
	private final NpcManager npcs;
	private final QuestManager quests;
	private final MobManager mobs;
	public CommandEditQuests(HalystiaRPG main, NpcManager npcs, QuestManager quests, MobManager mobs) {
		super(main, "edit-quests");
		this.npcs = npcs;
		this.quests = quests;
		this.mobs = mobs;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if( ! (sender instanceof Player)) {
			sender.sendMessage(RED + "Tu dois être un joueur !");
			return true;
		}
		Player p = (Player) sender;
		if(! HalystiaRPG.isInRpgWorld(p)) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Possible uniquement dans le monde RP !");
			return true;
		}
		if(args.length == 0) {
			sendHelp(p, label);
			return true;
		}
		if ( args[0].equals("list")) {
			sendList(p);
			return true;
		}
		if ( args[0].equals("reload")) {
			quests.reload();
			p.sendMessage(GREEN+"Reload des quests effectué.");
			return true;
		}
		if ( args.length < 2) {
			sendHelp(p, label);
			return true;
		}
		if ( ! firsts.contains(args[0]) ) {
			sendHelp(p, label);
			return true;
		}
		// CREATE NEW QUEST
		if(args[0].equals("create")) {
			if ( args.length < 3) {
				p.sendMessage(HalystiaRPG.PREFIX + RED + "/"+label+" create <id> "+DARK_RED+"<npc de départ>"+RED+" !");
				return true;
			}
			if(! args[1].matches("[a-zA-Z0-9]+")) {
				p.sendMessage(ChatColor.RED + "Le nom ne doit contenir que des caractères alphanumériques.");
				return true;
			} else if(args[1].equals("null")) {
				p.sendMessage(ChatColor.RED + "Ce nom n'est pas autorisé :(");
				return true;
			}
			RpgNpc npc = npcs.getNpcWithConfigId(args[2]);
			if(npc == null) {
				p.sendMessage(ChatColor.RED + "Le NPC (" + args[2] + ") n'existe pas !");
				return true;
			}
			if(npc.hasQuest()) {
				p.sendMessage(ChatColor.RED + "Le NPC (" + args[2] + ") lance déjà une quête !");
				return true;
			}
			Quest quest = quests.createQuest(args[1]);
			if ( quest != null ) {
				npc.changeQuest(quest);
				p.sendMessage(ChatColor.GREEN + "La quête ["+args[1]+"] a été créé avec succès !");
			} else {
				p.sendMessage(ChatColor.RED + "La quête ["+args[1]+"] existe déjà !");
			}
			return true;
		}

		// AUTRE
		Quest quest = quests.getQuestById(args[1]);
		if(quest == null) {
			p.sendMessage(ChatColor.RED + "La quête ["+args[1]+"] n'existe pas.");
			return true;
		}
		
		if(args[0].equals("reset")) {
			HalystiaRPG.getInstance().getDataBase().updateStepInQuest(p, quest, -1);
			p.sendMessage(ChatColor.GREEN + "ok !");
			return true;
		}
		
		if(args[0].equals("remove")) {
			quests.removeQuest(quest);
			p.sendMessage(ChatColor.GREEN + "La quête ["+args[1]+"] a été supprimé avec succès !");
			return true;
		}
		
		if(args[0].equals("level")) {
			if(args.length == 2) {
				p.sendMessage(GREEN +"La quête " +quest.getDisplayName() +GREEN+ " est de niveau " + quest.getLevel()+".");
				return true;
			}
			int level = -1;
			try {
				level = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				p.sendMessage(RED + "La valeur ("+args[2]+") n'est pas un nombre entier valide !");
				return true;
			}
			if(level < 0) {
				p.sendMessage(RED + "Le niveau doit être un nombre positif !");
				return true;
			}
			quest.changeLevel(level);
			p.sendMessage(GREEN + "Nouveau niveau requis pour la quête " + quest.getDisplayName() + GREEN + " : " + level + ".");
			return true;
		}
		
		if(args[0].equals("xp")) {
			if(args.length == 2) {
				p.sendMessage(GREEN +"La quête " +quest.getDisplayName() +GREEN+ " donne " + quest.getXp()+"xp.");
				return true;
			}
			int xp = -1;
			try {
				xp = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				p.sendMessage(RED + "La valeur ("+args[2]+") n'est pas un nombre entier valide !");
				return true;
			}
			if(xp < 0) {
				p.sendMessage(RED + "L'expérience doit être un nombre positif !");
				return true;
			}
			quest.changeXp(xp);
			p.sendMessage(GREEN + "Nouvelle xp de récompense pour " + quest.getDisplayName() + GREEN + " : " + xp +".");
			return true;
		}
		
		if(args[0].equals("rename")) {
			if(args.length < 3) {
				p.sendMessage(RED + "Précisez le nouveau nom !");
				return true;
			}
			StringBuilder builder = new StringBuilder();
			for(int i = 2; i < args.length; i++) {
				builder.append(args[i]);
				if(i < args.length - 1)
					builder.append(" ");
			}
			
			if(builder.toString().length() > 30) {
				p.sendMessage(RED + "Le nom est trop long ! ("+builder.toString()+")");
				return true;
			}
			p.playSound(p.getLocation(), Sound.ITEM_AXE_STRIP, 5f, .8f);
			quest.rename(builder.toString());
			p.sendMessage(GREEN + "Le nom de la quête a bien été changé.");
			return true;
		}
		
		if(args[0].equals("intro")) {
			if(args.length < 3) {
				sendHelpIntro(p, label, quest.getDisplayName());
				return true;
			}
			Messages intro = quest.getIntro();
			if(args[2].equals("clear")) {
				intro.clearDialog();
				p.sendMessage(GREEN + "Introduction supprimée avec succès.");
				quest.setIntro(intro);
				return true;
			}
			if(args[2].equals("see")) {
				p.sendMessage(BLUE + "< Introduction >");
				intro.getDialog().forEach(msg -> p.sendMessage(msg));
				p.sendMessage(BLUE + "< ------ >");
				return true;
			}
			
			if(args.length < 4) {
				sendHelpIntro(p, label, quest.getDisplayName());
				return true;
			}
			
			if(args[2].equals("add")) {
				StringBuilder builder = new StringBuilder();
				for(int i = 3; i < args.length; i++) {
					builder.append(args[i]);
					if(i < args.length - 1)
						builder.append(" ");
				}
				intro.addDialogLine(builder.toString());
				p.sendMessage(GREEN + "Ligne d'intro a été ajoutée avec succès.");
				quest.setIntro(intro);
				return true;
			}
			
			if(args[2].equals("remove")) {
				int line = -1;
				try {
					line = Integer.parseInt(args[3]);
				} catch (NumberFormatException e) {
					p.sendMessage(RED + "La valeur ["+args[3]+"] n'est pas une valeur de numéro de ligne valide.");
					return true;
				}
				if(intro.removeDialogLine(line)) {
					p.sendMessage(GREEN + "Ligne d'intro supprimée avec succès.");
					quest.setIntro(intro);
				} else
					p.sendMessage(RED + "La ligne d'intro n'a pas pu être supprimée.");
				return true;
			}
			
			if(args[2].equals("set") || args[2].equals("insert")) {
				if(args.length < 5) {
					p.sendMessage(RED + "/"+label+" " + args[0] + args[1] + " set <line-number> <texte>");
					return true;
				}
				int line = -1;
				try {
					line = Integer.parseInt(args[3]);
				} catch (NumberFormatException e) {
					p.sendMessage(RED + "La valeur ["+args[3]+"] n'est pas une valeur de numéro de ligne valide.");
					return true;
				}
				StringBuilder builder = new StringBuilder();
				for(int i = 4; i < args.length; i++) {
					builder.append(args[i]);
					if(i < args.length - 1)
						builder.append(" ");
				}
				if(args[2].equals("set") ? intro.setDialogLine(builder.toString(), line) : intro.insertDialogLine(builder.toString(), line)) {
					p.sendMessage(GREEN + "Ligne d'intro modifée avec succès.");
					quest.setIntro(intro);
				} else
					p.sendMessage(RED + "Ligne d'intro n'a pas pu être modifiée.");
				return true;
			}
		}
		
		if(args[0].equals("loots")) {
			if(args.length < 3) {
				sendHelpLoots(p, label, quest.getDisplayName());
				return true;
			}
			if(args[2].equals("clear")) {
				quest.clearLoots();
				p.sendMessage(GREEN + "Loots supprimés avec succès.");
				return true;
			}
			if(args[2].equals("list")) {
				p.sendMessage(BLUE + "< Loots >");
				int i = 0;
				for(ItemStack item : quest.getLoots()) {
					p.sendMessage(YELLOW + "["+i+"] " + (
						item.hasItemMeta() ? item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().toString().toLowerCase() : item.getType().toString().toLowerCase()
						) + YELLOW + " x"+item.getAmount()+".");
					i++;
				}
				p.sendMessage(BLUE + "< ------ >");
				return true;
			}
			
			if(args[2].equals("add")) {
				if(p.getInventory().getItemInMainHand() == null) {
					p.sendMessage(RED + "Il faut avoir un objet en main pour le mettre en récompense !");
					return true;
				}
				if(p.getInventory().getItemInMainHand().getType() == Material.AIR) {
					p.sendMessage(RED + "Il faut avoir un objet en main pour le mettre en récompense !");
					return true;
				}
				ItemStack loot = new ItemStack( p.getInventory().getItemInMainHand() );
				quest.addLoot(loot);
				p.sendMessage(GREEN + "Loot ajouté avec succès x"+loot.getAmount()+".");
				return true;
			}
			
			
			if(args[2].equals("remove")) {
				if(args.length < 4) {
					p.sendMessage(RED + "Il faut préciser l'id de loot à retirer. Voir /"+label+ " loots " + args[1] + " list.");
					return true;
				}
				int slot = -1;
				try {
					slot = Integer.parseInt(args[3]);
				} catch (NumberFormatException e) {
					p.sendMessage(RED + "La valeur ["+args[3]+"] n'est pas une valeur de numéro valide.");
					return true;
				}
				if(quest.removeLoot(slot))
					p.sendMessage(GREEN + "Loot supprimé avec succès.");
				else
					p.sendMessage(RED + "Le loot n'a pas pu être supprimé.");
				return true;
			}
			
			sendHelpLoots(p, label, quest.getDisplayName());
			return true;
		}
		
		if(args[0].equals("tags")) {
			if(args.length < 3) {
				sendHelpTags(p, label, quest.getDisplayName());
				return true;
			}
			if(args[2].equals("clear")) {
				quest.clearTagsGifts();
				p.sendMessage(GREEN + "Tags de récompense supprimés avec succès.");
				return true;
			}
			if(args[2].equals("list")) {
				p.sendMessage(BLUE + "< Tags donnés ["+quest.getID()+"] >");
				for(String tag : quest.getTagsGifts())
					p.sendMessage(BLUE+"["+GREEN+tag+BLUE+"]");
				p.sendMessage(BLUE + "< ---------------- >");
				return true;
			}
			if(args.length < 4) {
				p.sendMessage(RED + "Il faut préciser le tag à " + args[2]+".");
				return true;
			}
			final String tag = args[3].toLowerCase();
			if(args[2].equals("add")) {
				if(quest.addTagGift(tag))
					p.sendMessage(GREEN + "Tag de récompense ("+tag+") ajouté avec succès.");
				else
					p.sendMessage(RED + "Le tag ("+tag+") était déjà là.");
				return true;
			}
			if(args[2].equals("remove")) {
				if(quest.removeTagGift(tag))
					p.sendMessage(GREEN + "Tag de récompense ("+tag+") ajouté avec succès.");
				else
					p.sendMessage(RED + "Le tag ("+tag+") était déjà là.");
				return true;
			}
			sendHelpTags(p, label, quest.getDisplayName());
			return true;
		}
		
		if(args[0].equals("steps")) {
			if(args.length < 3) {
				sendHelpSteps(p, label, quest.getDisplayName());
				return true;
			}
			if(args[2].equals("list")) {
				sendSteps(p, quest);
				return true;
			}
			
			if(args[2].equals("loot")) {
				if(args.length < 5) {
					sendHelpSteps(p, label, quest.getDisplayName());
					return true;
				}
				QuestStep step = null;
				int id = -1;
				try {
					id = Integer.parseInt(args[3]);
					step = quest.getStep(id);
					if(step == null) {
						p.sendMessage(RED+"ID de step invalide !");
						return true;
					}
				} catch (NumberFormatException e) {
					p.sendMessage(RED + "La valeur ["+args[3]+"] n'est pas une valeur de nombre valide.");
					return true;
				}
				if(args[4].equals("see")) {
					ItemStack loot = quest.getLootStep(id);
					if(loot == null) {
						p.sendMessage(YELLOW + "Cette étape n'a aucun loot.");
						return true;
					}
					p.sendMessage(YELLOW + "Item ajouté avec succès dans votre inventaire : " + (loot.hasItemMeta() ? loot.getItemMeta().hasDisplayName() ? loot.getItemMeta().getDisplayName() : loot.getType() : loot.getType()));
					p.getInventory().addItem(loot);
					return true;
				}
				if(args[4].equals("set")) {
					if(p.getInventory().getItemInMainHand() == null) {
						p.sendMessage(RED + "Il faut avoir un objet en main pour le mettre en récompense !");
						return true;
					}
					if(p.getInventory().getItemInMainHand().getType() == Material.AIR) {
						p.sendMessage(RED + "Il faut avoir un objet en main pour le mettre en récompense !");
						return true;
					}
					ItemStack loot = new ItemStack( p.getInventory().getItemInMainHand() );
					if ( quest.setLootStep(id, loot) )
						p.sendMessage(GREEN + "Loot de cette étape set avec succès !");
					else
						p.sendMessage(RED + "Echec de la commande.");
					return true;
				}
				if(args[4].equals("clear")) {
					if(quest.clearLootStep(id))
						p.sendMessage(GREEN + "Loot de cette étape supprimé avec succès !");
					else
						p.sendMessage(RED + "Echec de la commande.");
					return true;
				}
				return true;
			}
			
			if(args[2].equals("message")) { // 0=STEPS 1=(QUEST) 2=messages 3=<step> 4=add 5=...
				if(args.length < 5) {
					sendHelpSteps(p, label, quest.getDisplayName());
					return true;
				}
				QuestStep step = null;
				int id = -1;
				try {
					id = Integer.parseInt(args[3]);
					step = quest.getStep(id);
					if(step == null) {
						p.sendMessage(RED+"ID de step invalide !");
						return true;
					}
				} catch (NumberFormatException e) {
					p.sendMessage(RED + "La valeur ["+args[3]+"] n'est pas une valeur de nombre valide.");
					return true;
				}
				
				Messages msg = step.getMessages();
				if(args[4].equals("clear")) {
					msg.clearDialog();
					p.sendMessage(GREEN + "Message supprimée avec succès.");
					quest.setDialog(id, msg);
					return true;
				}
				if(args[4].equals("see")) {
					p.sendMessage(BLUE + "< Introduction >");
					msg.getDialog().forEach(msgs -> p.sendMessage(msgs));
					p.sendMessage(BLUE + "< ------ >");
					return true;
				}
				
				if(args.length < 6) {
					sendHelpSteps(p, label, quest.getDisplayName());
					return true;
				}
				
				if(args[4].equals("add")) {
					StringBuilder builder = new StringBuilder();
					for(int i = 5; i < args.length; i++) {
						builder.append(args[i]);
						if(i < args.length - 1)
							builder.append(" ");
					}
					msg.addDialogLine(builder.toString());
					p.sendMessage(GREEN + "La ligne a été ajoutée avec succès.");
					quest.setDialog(id, msg);
					return true;
				}
				
				if(args[4].equals("remove")) {
					int line = -1;
					try {
						line = Integer.parseInt(args[5]);
					} catch (NumberFormatException e) {
						p.sendMessage(RED + "La valeur ["+args[5]+"] n'est pas une valeur de numéro de ligne valide.");
						return true;
					}
					if(msg.removeDialogLine(line)){
						p.sendMessage(GREEN + "Ligne supprimée avec succès.");
						quest.setDialog(id, msg);
					} else
						p.sendMessage(RED + "La ligne n'a pas pu être supprimée.");
					quest.setDialog(id, msg);
					return true;
				}
				
				if(args[4].equals("set") || args[4].equals("insert")) {
					if(args.length < 7) {
						p.sendMessage(RED + "/"+label+" " + args[0] +" "+ args[1] + " " + args[2] + " " + args[3] + " " + args[4] + " <line-number> <texte>");
						return true;
					}
					int line = -1;
					try {
						line = Integer.parseInt(args[5]);
					} catch (NumberFormatException e) {
						p.sendMessage(RED + "La valeur ["+args[5]+"] n'est pas une valeur de numéro de ligne valide.");
						return true;
					}
					StringBuilder builder = new StringBuilder();
					for(int i = 6; i < args.length; i++) {
						builder.append(args[i]);
						if(i < args.length - 1)
							builder.append(" ");
					}
					if(args[4].equals("set") ? msg.setDialogLine(builder.toString(), line) : msg.insertDialogLine(builder.toString(), line)) {
						p.sendMessage(GREEN + "Ligne modifée avec succès.");
						quest.setDialog(id, msg);
					} else
						p.sendMessage(RED + "La ligne n'a pas pu être modifiée.");
					return true;
				}
				sendHelpSteps(p, label, quest.getDisplayName());
			}
			
			if(args[2].equals("create")) { // 1=STEPS 2=QUEST 3=create 4=TYPE 5=DATA1 6=DATA2
				if(args.length < 4) {
					sendHelpSteps(p, label, quest.getDisplayName());
					return true;
				}
				QuestStepType type = null;
				try {
					type = QuestStepType.valueOf(args[3].toUpperCase());
				} catch (IllegalArgumentException e) {
					p.sendMessage(RED+"Le type de step ["+args[3]+") n'existe pas.");
					return true;
				}
				
				switch(type) {
				case BRING:
					RpgNpc npc = npcs.getNpcWithConfigId(args[4]);
					if(npc == null) {
						p.sendMessage(RED + "Le NPC ("+args[4]+") n'existe pas !");
						return true;
					}
					if(p.getInventory().getItemInMainHand() == null) {
						p.sendMessage(RED + "Il faut avoir un objet en main pour le mettre objet à apporter !");
						return true;
					}
					if(p.getInventory().getItemInMainHand().getType() == Material.AIR) {
						p.sendMessage(RED + "Il faut avoir un objet en main pour le mettre objet à apporter !");
						return true;
					}
					ItemStack item = new ItemStack( p.getInventory().getItemInMainHand() );
					quest.addStepBring(npc, item);
					break;
				case KILL:
					if ( ! mobs.getAllMobNames().contains(args[4]) ) {
						p.sendMessage(RED+"Le monstre ("+args[4]+") n'existe pas !");
						return true;
					}
					if(args.length < 6) {
						p.sendMessage(RED+"Précisez le nombre de monstres à tuer !");
						return true;
					}
					int amount = 1;
					try {
						amount = Integer.parseInt(args[5]);
					} catch (NumberFormatException e) {
						p.sendMessage(RED + "La valeur ("+args[5]+") n'est pas un nombre entier valide !");
						return true;
					}
					if(amount < 1) {
						p.sendMessage(RED + "Le nombre de monstre à tuer doit être > 1 !");
						return true;
					}
					quest.addStepKill(args[4], amount);
					break;
				case SPEAK:
					RpgNpc npcc = npcs.getNpcWithConfigId(args[4]);
					if(npcc == null) {
						p.sendMessage(RED + "Le NPC ("+args[4]+") n'existe pas !");
						return true;
					}
					quest.addStepSpeak(npcc);
					break;
				}
				p.sendMessage(GREEN+"Etape créée avec succès.");
				return true;
			}
			
			if(args[2].equals("remove")) {
				if(args.length < 4) {
					p.sendMessage(RED + "Il faut préciser l'id de l'étape à retirer. Voir /"+label+ " steps " + args[1] + " list.");
					return true;
				}
				int slot = -1;
				try {
					slot = Integer.parseInt(args[3]);
				} catch (NumberFormatException e) {
					p.sendMessage(RED + "La valeur ["+args[3]+"] n'est pas une valeur d'id valide.");
					return true;
				}
				if(quest.destroyStep(slot))
					p.sendMessage(GREEN + "Etape supprimée avec succès.");
				else
					p.sendMessage(RED + "L'étape n'a pas pu être supprimée.");
				return true;
			}
			
			
			
			sendHelpSteps(p, label, quest.getDisplayName());
			return true;
		}
		
		p.sendMessage(RED + "Commande ["+args[0] +"] autorisée mais non paramétrée...");
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length <= 1)
			return firsts.stream().filter(str -> str.startsWith(args[0])).collect(Collectors.toList());
		if(args.length <= 2)
			return quests.getAllConfigIdsStream().filter(str -> str.startsWith(args[1])).collect(Collectors.toList());
		if(args.length <= 3)
			if(args[0].equals("loots") || args[0].equals("tags"))
				return loots.stream().filter(str -> str.startsWith(args[2])).collect(Collectors.toList());
			else if(args[0].equals("steps"))
				return steps.stream().filter(str -> str.startsWith(args[2])).collect(Collectors.toList());
			else if(args[0].equals("create"))
				return npcs.getAllConfigIds().stream().filter(str -> str.startsWith(args[2])).collect(Collectors.toList());
			else if(args[0].equals("intro"))
				return messages.stream().filter(str -> str.startsWith(args[2])).collect(Collectors.toList());
		if(args.length <= 4) {
			if(args[0].equals("steps")) {
				if(args[2].equals("create")) {
					return Arrays.asList(QuestStepType.values()).stream().map(st -> st.toString().toLowerCase()).filter(str -> str.startsWith(args[3])).collect(Collectors.toList());
				} else if(args[2].equals("message") || args[2].equals("remove") || args[2].equals("loot")) {
					Quest quest = quests.getQuestById(args[1]);
					if(quest == null)
						return new ArrayList<>();
					List<String> list = new ArrayList<>();
					for(int i = 0; i < quest.getHowManySteps(); i++)
						list.add(""+i);
					return list;
				}
				return new ArrayList<>();
			} else if(args[0].equals("tags")) {
				if(args[2].equals("remove")) {
					Quest quest = quests.getQuestById(args[1]);
					if(quest == null)
						return new ArrayList<>();
					return quest.getTagsGifts().stream().filter(str -> str.startsWith(args[3])).collect(Collectors.toList());
				}
			}
		}
		if(args.length <= 5) {
			if(args[0].equals("steps")) {
				if(args[2].equals("create")) {
					if(args[3].equalsIgnoreCase(QuestStepType.SPEAK.toString()) || args[3].equalsIgnoreCase(QuestStepType.BRING.toString()))
						return npcs.getAllConfigIds().stream().filter(str -> str.startsWith(args[4])).collect(Collectors.toList());
					else if(args[3].equalsIgnoreCase(QuestStepType.KILL.toString()))
						return mobs.getAllMobNames().stream().filter(str -> str.startsWith(args[4])).collect(Collectors.toList());
				} else if(args[2].equals("message")) {
					return messages.stream().filter(str -> str.startsWith(args[4])).collect(Collectors.toList());
				} else if(args[2].equals("loot")) {
					return lootStep.stream().filter(str -> str.startsWith(args[4])).collect(Collectors.toList());
				}
			}
			return new ArrayList<>();
		}
		if(args.length <= 6)
			if(args[0].equals("steps") && args[2].equals("create"))
				if(args[3].equalsIgnoreCase(QuestStepType.KILL.toString()))
					return Arrays.asList("1", "32", "64").stream().filter(str -> str.startsWith(args[5])).collect(Collectors.toList());
		
		return new ArrayList<>();
	}// 0=STEPS 1=(QUEST) 2=messages 3=<step> 4=add 5=...
	
	private void sendHelp(Player p, String label) {
		p.sendMessage(AQUA + "/" + label + " create <id> <npc> " + WHITE + ": Créer une nouvelle quête débutant sur un npc.");
		p.sendMessage(AQUA + "/" + label + " remove <id> " + WHITE + ": Supprime une quête.");
		p.sendMessage(AQUA + "/" + label + " list " + WHITE + ": Liste les quêtes existantes et leur statut.");
		p.sendMessage(AQUA + "/" + label + " level <id> [new level] " + WHITE + ": Met à jour le niveau requis pour la quête.");
		p.sendMessage(AQUA + "/" + label + " xp <id> [new xp] " + WHITE + ": Met à jour l'exp donnée à la fin de la quête.");
		p.sendMessage(AQUA + "/" + label + " loots <id> <cmd> " + WHITE + ": Change les loots donnés à la fin de la quête.");
		p.sendMessage(AQUA + "/" + label + " rename <id> <name> " + WHITE + ": Renommme une quête. Couleurs.");
		p.sendMessage(AQUA + "/" + label + " steps <id> <arg> [[args]] " + WHITE + ": Commandes pour les étapes de la quête.");
	}
	
	private void sendHelpLoots(Player p, String label, String quete) {
		p.sendMessage(AQUA + "/" + label + " loots "+quete+AQUA+" clear " + WHITE + ": Supprime les loots.");
		p.sendMessage(AQUA + "/" + label + " loots "+quete+AQUA+" list " + WHITE + ": Voir tous les loots.");
		p.sendMessage(AQUA + "/" + label + " loots "+quete+AQUA+" add " + WHITE + ": Rajouter un nouveau loot (item dans la main).");
		p.sendMessage(AQUA + "/" + label + " loots "+quete+AQUA+" remove <n> " + WHITE + ": Supprime un id de loot.");
	}
	
	private void sendHelpSteps(Player p, String label, String quete) {
		p.sendMessage(AQUA + "/" + label + " steps "+quete+AQUA+" list " + WHITE + ": Voir toutes les étapes et leur statut.");
		p.sendMessage(AQUA + "/" + label + " steps "+quete+AQUA+" add <type> <arg1>" + WHITE + ": Ajouter une étape d'un types et de certains paramètres.");
		p.sendMessage(AQUA + "/" + label + " steps "+quete+AQUA+" remove <n> " + WHITE + ": Supprime un id d'étape.");
		p.sendMessage(AQUA + "/" + label + " steps "+quete+AQUA+" messages <args> [[args]] " + WHITE + ": Modifie les messages d'une étape.");
		p.sendMessage(AQUA + "/" + label + " steps "+quete+AQUA+" loot <args> " + WHITE + ": Modifie le loot d'une étape.");
	}
	
	private void sendHelpIntro(Player p, String label, String quete) {
		p.sendMessage(AQUA + "/" + label + " intro "+quete+AQUA+" see " + WHITE + ": Observer les messages de l'intro.");
		p.sendMessage(AQUA + "/" + label + " intro "+quete+AQUA+" clear " + WHITE + ": Supprimer tous les messages de l'intro.");
		p.sendMessage(AQUA + "/" + label + " intro "+quete+AQUA+" add <message>" + WHITE + ": Ajouter un message à l'intro.");
		p.sendMessage(AQUA + "/" + label + " intro "+quete+AQUA+" remove <n>" + WHITE + ": Supprimer un message de l'intro.");
		p.sendMessage(AQUA + "/" + label + " intro "+quete+AQUA+" insert <n> <message> " + WHITE + ": Insert un message de l'intro.");
		p.sendMessage(AQUA + "/" + label + " intro "+quete+AQUA+" set <n> <message> " + WHITE + ": Modifier un message de l'intro.");
	}
	
	private void sendHelpTags(Player p, String label, String displayName) {
		p.sendMessage(AQUA + "/" + label + " tags "+displayName+AQUA+" clear " + WHITE + ": Supprime les tags de récompense.");
		p.sendMessage(AQUA + "/" + label + " tags "+displayName+AQUA+" list " + WHITE + ": Voir tous les tags de récompense.");
		p.sendMessage(AQUA + "/" + label + " tags "+displayName+AQUA+" add <tag>" + WHITE + ": Rajouter un nouveau tag de récompense.");
		p.sendMessage(AQUA + "/" + label + " tags "+displayName+AQUA+" remove <tag> " + WHITE + ": Supprime un tag de récompense.");
	}


	private void sendList(Player p) {
		p.sendMessage(AQUA + "Liste des " + quests.getAllQuests().size() + " quêtes :");
		for(Quest quest : quests.getAllQuests()) {
			StringBuilder builder = new StringBuilder();
			builder
				.append(quest.isCorrect() ? GREEN + "[lvl:"+quest.getLevel()+"]" : RED + "[" + quest.getWhyNotCorrect() + "]")
				.append(quest.getID())
				.append(GRAY).append(" (").append(quest.getDisplayName()).append(GRAY).append(") - ")
				.append(quest.getHowManySteps() == 0 ? DARK_RED : GREEN).append(quest.getHowManySteps())
				.append(" étapes.");
			p.sendMessage(builder.toString());
		}
	}
	
	private void sendSteps(Player p, Quest quest) {
		p.sendMessage(quest.getDisplayName() + AQUA + " : Liste des " + quest.getHowManySteps() + " étapes.");
		QuestStep[] steps = quest.getSteps();
		for(int i = 0; i < steps.length; i++) {
			QuestStep step = steps[i];
			StringBuilder builder = new StringBuilder(YELLOW+"["+i+"] ");
			builder
				.append(step.getType())
				.append(GRAY+"["+step.getObjectiveDescription()+GRAY+"]")
				.append(step.getMessages().getLenght() == 0 ? RED + "[! pas de message !]" : GREEN + "[" + step.getMessages().getLenght() + " msgs]")
				.append(step.getLoot() == null ? "" : GREEN+"[Loot]");
			p.sendMessage(builder.toString());
		}
	}
}