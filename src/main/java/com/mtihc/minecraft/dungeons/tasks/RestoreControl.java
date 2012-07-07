package com.mtihc.minecraft.dungeons.tasks;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.snapshots.InvalidSnapshotException;
import com.sk89q.worldedit.snapshots.Snapshot;
import com.sk89q.worldedit.snapshots.SnapshotRepository;

public class RestoreControl {

	private JavaPlugin plugin;
	private File directory;
	private WorldEditPlugin worldEdit;
	private SnapshotRepository snapshots;
	private SchematicRepository schematics;

	public RestoreControl(JavaPlugin plugin, String directory) throws Exception {
		this(plugin, new File(directory));
	}
	
	public RestoreControl(JavaPlugin plugin, File directory) throws Exception {
		this.plugin = plugin;
		this.directory = directory;
		this.directory.mkdirs();
		
		Plugin we = this.plugin.getServer().getPluginManager().getPlugin("WorldEdit");
		if(we == null || !(we instanceof WorldEditPlugin)) {
			throw new Exception("WorldEdit plugin not installed.");
		}
		
		this.worldEdit = (WorldEditPlugin) we;
	}
	
	public JavaPlugin getPlugin() {
		return plugin;
	}
	
	public WorldEditPlugin getWorldEdit() {
		return worldEdit;
	}
	
	public File getDirectory() {
		return directory;
	}

	public SnapshotRepository getSnapshots() {
		if(snapshots == null) {
			snapshots = new SnapshotRepository(directory + "/snapshots");
		}
		return snapshots;
	}
	
	public SnapshotRepository getDefaultSnapshots() {
		return worldEdit.getLocalConfiguration().snapshotRepo;
	}
	
	public SchematicRepository getSchematics() {
		if(schematics == null) {
			schematics = new SchematicRepository(directory + "/schematics");
		}
		return schematics;
	}
	
	public Snapshot getRandomSnapshot() {
		String[] names = getSnapshots().getDirectory().list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return !name.contains(".");
			}
		});
		
		if(names == null || names.length == 0) {
			return null;
		}
		
		Random r = new Random();
		String name = names[r.nextInt(names.length)];
		
		
		try {
			Snapshot result = getSnapshots().getSnapshot(name);
			if(result == null) {
				return null;
			}
			else {
				return result;
			}
		} catch (InvalidSnapshotException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to load snapshot.", e);
			return null;
		}
		
	}
	
	public CuboidRegion createCuboidRegion(String worldName, Vector pos1, Vector pos2) {
		List<LocalWorld> worlds = worldEdit.getServerInterface().getWorlds();
		for (LocalWorld localWorld : worlds) {
			if(localWorld.getName().equalsIgnoreCase(worldName)) {
				return new CuboidRegion(localWorld, new com.sk89q.worldedit.Vector(pos1.getBlockX(), pos1.getBlockY(), pos1.getBlockZ()), new com.sk89q.worldedit.Vector(pos2.getBlockX(), pos2.getBlockY(), pos2.getBlockZ()));
			}
		}
		return null;
	}
	
	
}
