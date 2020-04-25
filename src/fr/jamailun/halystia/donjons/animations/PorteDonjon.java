package fr.jamailun.halystia.donjons.animations;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;

public class PorteDonjon {
	
	private final HalystiaRPG api;
	private Material contour;
	private Block centreBlock;
	private final Location centreLoc;
	private final Material centreType;
	private boolean ouvert;
	private Orientation orientation;
	private Location lA1, lA2, lA3, lB1, lB2, lB3, lC1, lC2, lC3;
	
	public PorteDonjon(Block centre, HalystiaRPG api) {
		this.api = api;
		this.centreBlock = centre;
		lB2 = centre.getLocation();
		centreType = centre.getType();
		centreLoc = centre.getLocation();
		ouvert = false;
		initMaterial();
		initOrientation();
		initLocations();
	}
	
	public boolean isOpened() {
		return ouvert;
	}
	
	public void close() {
		stadeSelector(4, 1, false);
		stadeSelector(3, 15, true);
		stadeSelector(2, 30, true);
		stadeSelector(1, 45, true);
		ouvert = true;
	}
	
	public void open() {
		stadeSelector(1, 1, false);
		stadeSelector(2, 30, true);
		stadeSelector(3, 60, true);
		stadeSelector(4, 90, true);
		ouvert = false;
	}
	
	private void stadeSelector(final int n, int time, final boolean sound) {
		if(time<=0) time=1;
		new BukkitRunnable() {
			@Override
			public void run() {
				if(sound) bruit();
				if(n==1) stade1();
				if(n==2) stade2();
				if(n==3) stade3();
				if(n==4) stade4();
			}
		}.runTaskLater(api, time*1L);
	}
	
	private void bruit() {
		for(Player p : centreBlock.getWorld().getPlayers()) {
			p.playSound(centreLoc, Sound.BLOCK_ANVIL_LAND, (float) 1, 1.3F);
		}
	}
	
	private void stade1() {
		lA1.getBlock().setType(contour);
		lA2.getBlock().setType(contour);
		lA3.getBlock().setType(contour);
		lB1.getBlock().setType(contour);
		lB2.getBlock().setType(centreType);
		lB3.getBlock().setType(contour);
		lC1.getBlock().setType(contour);
		lC2.getBlock().setType(contour);
		lC3.getBlock().setType(contour);
	}
	
	private void stade2() {
		lA1.getBlock().setType(contour);
		lA2.getBlock().setType(centreType);
		lA3.getBlock().setType(contour);
		lB1.getBlock().setType(contour);
		lB2.getBlock().setType(contour);
		lB3.getBlock().setType(contour);
		lC1.getBlock().setType(Material.AIR);
		lC2.getBlock().setType(Material.AIR);
		lC3.getBlock().setType(Material.AIR);
	}
	
	private void stade3() {
		lA1.getBlock().setType(contour);
		lA2.getBlock().setType(contour);
		lA3.getBlock().setType(contour);
		lB1.getBlock().setType(Material.AIR);
		lB2.getBlock().setType(Material.AIR);
		lB3.getBlock().setType(Material.AIR);
		lC1.getBlock().setType(Material.AIR);
		lC2.getBlock().setType(Material.AIR);
		lC3.getBlock().setType(Material.AIR);
	}
	
	private void stade4() {
		lA1.getBlock().setType(Material.AIR);
		lA2.getBlock().setType(Material.AIR);
		lA3.getBlock().setType(Material.AIR);
		lB1.getBlock().setType(Material.AIR);
		lB2.getBlock().setType(Material.AIR);
		lB3.getBlock().setType(Material.AIR);
		lC1.getBlock().setType(Material.AIR);
		lC2.getBlock().setType(Material.AIR);
		lC3.getBlock().setType(Material.AIR);
	}
	
	protected void initLocations() {
		World w = centreLoc.getWorld();
		double yHaut = centreLoc.getY() + 1;
		double yCentre = centreLoc.getY();
		double yBas = centreLoc.getY() - 1;
		if(orientation == Orientation.X) {
			double xHaut = centreLoc.getX() + 1;
			double xCentre = centreLoc.getX();
			double xBas = centreLoc.getX() - 1;
			double z = centreLoc.getZ();
			lA1 = new Location(w, xHaut, yHaut, z);
			lA2 = new Location(w, xCentre, yHaut, z);
			lA3 = new Location(w, xBas, yHaut, z);
			lB1 = new Location(w, xHaut, yCentre, z);
			lB3 = new Location(w, xBas, yCentre, z);
			lC1 = new Location(w, xHaut, yBas, z);
			lC2 = new Location(w, xCentre, yBas, z);
			lC3 = new Location(w, xBas, yBas, z);
			return;
		}else if(orientation == Orientation.Z) {
			double zHaut = centreLoc.getZ() + 1;
			double zCentre = centreLoc.getZ();
			double zBas = centreLoc.getZ() - 1;
			double x = centreLoc.getX();
			lA1 = new Location(w, x, yHaut, zHaut);
			lA2 = new Location(w, x, yHaut, zCentre);
			lA3 = new Location(w, x, yHaut, zBas);
			lB1 = new Location(w, x, yCentre, zHaut);
			lB3 = new Location(w, x, yCentre, zBas);
			lC1 = new Location(w, x, yBas, zHaut);
			lC2 = new Location(w, x, yBas, zCentre);
			lC3 = new Location(w, x, yBas, zBas);
			return;
		}
	}
	
	protected void initOrientation() {
		//si c'est orientation X:
		Location testX = new Location(centreLoc.getWorld(), centreLoc.getX(), centreLoc.getY(), centreLoc.getZ());
		testX.setX(testX.getX() + 1);
		Block blockX = testX.getBlock();
		if(blockX.getType() == this.contour) {
			this.orientation = Orientation.X;
			return;
		}
		//si c'est orientation Z:
		Location testZ = new Location(centreLoc.getWorld(), centreLoc.getX(), centreLoc.getY(), centreLoc.getZ());
		testZ.setZ(testZ.getZ() + 1);
		Block blockZ = testZ.getBlock();
		if(blockZ.getType() == this.contour) {
			this.orientation = Orientation.Z;
			return;
		}
		//erreur
		api.getConsole().sendMessage(ChatColor.DARK_RED + "Erreur. La porte n'est pas orientée.");
		this.orientation = Orientation.ERREUR;
	}
	
	protected void initMaterial() {
		Location bas = new Location(centreLoc.getWorld(), centreLoc.getX(), centreLoc.getY(), centreLoc.getZ());
		double nY = bas.getY();
		nY--;
		bas.setY(nY);
		Block b = bas.getBlock();
		this.contour = b.getType();
	}
	
	public Block getCentre() {
		return centreBlock;
	}
	
	public Material getContourType() {
		return contour;
	}
	
	public Orientation getOrientation() {
		return orientation;
	}
	
	public enum Orientation {
		X,
		Z,
		ERREUR;
	}
}
