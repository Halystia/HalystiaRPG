package fr.jamailun.halystia.sql.temporary;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.players.PlayerData;
import fr.jamailun.halystia.quests.Quest;
import fr.jamailun.halystia.quests.steps.QuestStep;
import fr.jamailun.halystia.royaumes.Royaume;
import fr.jamailun.halystia.titles.Title;
import fr.jamailun.halystia.utils.FileDataRPG;

public class FileSaver extends FileDataRPG implements DataHandler {
	
	private final static String CLASSE_ID = ".classe.id";
	private final static String CLASSE_XP = ".classe.xp";
	private final static String ROYAUME = ".roi-de";
	private final static String SOULS_NB = ".nb-souls";
	private final static String SOULS_LAST = ".last-soul";
	private final static String QUESTS = ".quests";
	private final static String TAGS = ".tags";
	private final static String TITLE = ".title";
	private final static String SPAWN = ".spawn";
	private final static String KARMA = ".karma";
	
	public FileSaver(String path, String fileName) {
		super(path, fileName);
	}
	
	@Override
	public boolean createPlayerTableClass() {
		return true;
	}

	@Override
	public boolean addPlayerProfile(Player player) {
		String uuid = player.getUniqueId().toString();
		synchronized (file) {
			if( ! config.contains(uuid+CLASSE_ID))
				config.set(uuid+CLASSE_ID, 0);
			if( ! config.contains(uuid+CLASSE_XP))
				config.set(uuid+CLASSE_XP, 0);
			if( ! config.contains(uuid+SOULS_NB))
				config.set(uuid+SOULS_NB, 3);
			if( ! config.contains(uuid+SOULS_LAST))
				config.set(uuid+SOULS_LAST, System.currentTimeMillis());
			save();
			return true;
		}
	}
	
	@Override
	public void saveAll(Collection<PlayerData> players) {
		synchronized (file) {
			players.forEach(playerData -> {
				String uuid = playerData.getPlayerUUID().toString();
				config.set(uuid+CLASSE_XP, playerData.getExpAmount());
				config.set(uuid+KARMA, playerData.getCurrentKarma());
			});
			save();
		}
	}

	@Override
	public PlayerData getPlayerData(Player player) {
		String uuid = player.getUniqueId().toString();
		synchronized (file) {
			int id = config.getInt(uuid+CLASSE_ID);
			Classe classe = Classe.getClasseWithId(id);
			int exp = config.getInt(uuid+CLASSE_XP);
			int karma = config.getInt(uuid+KARMA);
			return new PlayerData(classe, exp, player, karma);
		}
	}

	@Override
	public boolean changePlayerClasse(Player player, Classe classe) {
		String uuid = player.getUniqueId().toString();
		synchronized (file) {
			config.set(uuid+CLASSE_ID, classe.getClasseId());
			config.set(uuid+CLASSE_XP, 0);
			save();
			return true;
		}
	}

	@Override
	public synchronized boolean updateXp(Player player, int exp) {
		String uuid = player.getUniqueId().toString();
		synchronized (file) {
			config.set(uuid+CLASSE_XP, exp);
			save();
		}
		return true;
	}

	@Override
	public Royaume getKingdom(Player p) {
		String uuid = p.getUniqueId().toString();
		if( ! config.contains(uuid + ROYAUME))
			return null;
		String rStr = config.getString(uuid + ROYAUME);
		try {
			return Royaume.valueOf(rStr);
		} catch(IllegalArgumentException e) {
			return null;
		}
	}

	@Override
	public boolean setRoi(Royaume r, Player p) {
		synchronized (file) {
			for(String key : config.getKeys(false)) {
				if(config.contains(key+ROYAUME)) {
					if(config.getString(key+ROYAUME).equals(r.toString())) {
						config.set(key+ROYAUME, null);
					}
				}
			}
			config.set(p.getUniqueId().toString()+ROYAUME, r.toString());
			save();
			return true;
		}
	}

	@Override
	public int getLastSoulRefresh(Player p) {
		synchronized (p) {
			final long current = System.currentTimeMillis();
			long last = config.getLong(p.getUniqueId().toString()+SOULS_LAST);
			long elapsed = current - last;
			float secs = elapsed / 1000f;
			return (int) secs;
		}
	}

	@Override
	public int getHowManySouls(Player p) {
		return config.getInt(p.getUniqueId().toString()+SOULS_NB);
	}

	@Override
	public boolean refreshSoul(Player p) {
		synchronized (file) {
			int current = getHowManySouls(p);
			current++;
			if(current > 3)
				current = 3;
			if(current < 0)
				current = 1; //pour des cas aussi étranges que particuliers
			config.set(p.getUniqueId().toString()+SOULS_NB, current);
			config.set(p.getUniqueId().toString()+SOULS_LAST, System.currentTimeMillis());
			save();
			return true;
		}
	}

	@Override
	public boolean looseSoul(Player p) {
		synchronized (file) {
			int current = getHowManySouls(p);
			current--;
			if(current > 3)
				current = 2;
			if(current < 0)
				current = 0; //pour des cas aussi étranges que particuliers
			config.set(p.getUniqueId().toString()+SOULS_NB, current);
			config.set(p.getUniqueId().toString()+SOULS_LAST, System.currentTimeMillis());
			save();
			return false;
		}
	}

	@Override
	public Set<Quest> getAllQuests(Player p) {
		synchronized (file) {
			if(! config.contains(p.getUniqueId().toString() + QUESTS))
				return new HashSet<>();
			return HalystiaRPG.getInstance().getQuestManager().getAllQuests().stream().filter(quest -> config.contains(p.getUniqueId().toString() + QUESTS + "." + quest.getID())).collect(Collectors.toSet());
		}
	}

	@Override
	public int getStepInQuest(Player p, Quest quest) {
		synchronized (file) {
			if( ! config.contains(p.getUniqueId().toString() + QUESTS + "." + quest.getID()))
				return -1;
			return config.getInt(p.getUniqueId().toString() + QUESTS + "." + quest.getID() + ".step");
		}
	}
	@Override
	public int getDataInQuest(Player p, Quest quest) {
		synchronized (file) {
			if( ! config.contains(p.getUniqueId().toString() + QUESTS + "." + quest.getID()))
				return -1;
			return config.getInt(p.getUniqueId().toString() + QUESTS + "." + quest.getID() + ".data");
		}
	}

	@Override
	public Set<Quest> getOnGoingQuests(Player p) {
		synchronized (file) {
			Set<Quest> quests = new HashSet<>();
			for(Quest quest : getAllQuests(p)) {
				int step = getStepInQuest(p, quest);
				if(step < quest.getHowManySteps() && step != -1)
					quests.add(quest);
			}
			return quests;
		}
	}

	@Override
	public void updateStepInQuest(Player p, Quest quest, int step) {
		synchronized (file) {
			if(step == -1) {
				config.set(p.getUniqueId().toString() + QUESTS + "." + quest.getID(), null);
			} else {
				config.set(p.getUniqueId().toString() + QUESTS + "." + quest.getID() + ".step", step);
				config.set(p.getUniqueId().toString() + QUESTS + "." + quest.getID() + ".data", 0); //on vire les datas aussi :D
			}
			save();
		}
	}

	@Override
	public void updateDataInQuest(Player p, Quest quest, int data) {
		synchronized (file) {
			config.set(p.getUniqueId().toString() + QUESTS + "." + quest.getID() + ".data", data);
			save();
		}
	}

	@Override
	public Set<QuestStep> getOnGoingQuestSteps(Player p) {
		synchronized (file) {
			Set<QuestStep> steps = new HashSet<>();
			for(Quest quest : getAllQuests(p)) {
				int step = getStepInQuest(p, quest);
				if(step < quest.getHowManySteps() && step != -1)
					steps.add(quest.getStep(step));
			}
			return steps;
		}
	}

	@Override
	public boolean playerHasTag(Player p, String tag) {
		synchronized (file) {
			if( ! config.contains(p.getUniqueId().toString() + TAGS))
				return false;
			return config.getStringList(p.getUniqueId().toString() + TAGS).contains(tag);
		}
	}

	@Override
	public void addTagToPlayer(Player p, String tag) {
		synchronized (file) {
			List<String> tags = config.getStringList(p.getUniqueId().toString() + TAGS);
			if(tags.contains(tag))
				return;
			tags.add(tag);
			config.set(p.getUniqueId().toString() + TAGS, tags);
			save();
		}
	}

	@Override
	public void removeTagFromPlayer(Player p, String tag) {
		synchronized (file) {
			List<String> tags = config.getStringList(p.getUniqueId().toString() + TAGS);
			if( ! tags.contains(tag))
				return;
			tags.remove(tag);
			config.set(p.getUniqueId().toString() + TAGS, tags);
			save();
		}
	}

	@Override
	public List<String> getTagsOfPlayer(Player p) {
		synchronized (file) {
			return config.getStringList(p.getUniqueId().toString() + TAGS);
		}
	}

	@Override
	public String getCurrentTitleOfPlayer(Player p) {
		synchronized (file) {
			return config.getString(p.getUniqueId().toString() + TITLE);
		}
	}

	@Override
	public void setCurrentTitleOfPlayer(Player p, Title title) {
		synchronized (file) {
			config.set(p.getUniqueId().toString() + TITLE, title.getTag());
			save();
		}
	}

	@Override
	public Location getSpawnLocation(Player player) {
		synchronized (file) {
			if( ! config.contains(player.getUniqueId().toString() + SPAWN) )
				return Bukkit.getWorld(HalystiaRPG.WORLD).getSpawnLocation();
			return config.getLocation(player.getUniqueId().toString() + SPAWN);
		}
	}

	@Override
	public void updateSpawnLocation(Player player, Location location) {
		synchronized (file) {
			config.set(player.getUniqueId().toString() + SPAWN, location);
			save();
		}
	}
	
}