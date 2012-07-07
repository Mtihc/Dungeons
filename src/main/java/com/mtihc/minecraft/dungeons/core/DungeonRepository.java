package com.mtihc.minecraft.dungeons.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.dungeons.tasks.RestoreControl;

public class DungeonRepository {

	private JavaPlugin plugin;
	private File directory;

	public DungeonRepository(JavaPlugin plugin, String directory) {
		this(plugin, new File(directory));
	}
	
	public DungeonRepository(JavaPlugin plugin, File directory) {
		this.plugin = plugin;
		this.directory = directory;
	}
	
	public JavaPlugin getPlugin() {
		return plugin;
	}
	
	public File getDirectory() {
		return directory;
	}
	
	protected String getDungeonDirectoryPath(String id) {
		return directory + "/" + id;
	}
	
	protected String getDungeonConfigPath(String id) {
		return getDungeonDirectoryPath(id) + "/" + "config.yml";
	}
	
	protected File getDungeonConfigFile(String id) {
		return new File(getDungeonConfigPath(id));
	}
	
	protected YamlConfiguration getDungeonConfig(String id) throws FileNotFoundException, IOException, InvalidConfigurationException {
		YamlConfiguration result = new YamlConfiguration();
		result.load(getDungeonConfigFile(id));
		return result;
	}
	
	protected Dungeon loadDungeon(String id) throws IOException, InvalidConfigurationException {
		try {
			return (Dungeon) getDungeonConfig(id).get("dungeon");
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	
	protected void saveDungeonConfig(Dungeon dungeon) throws IOException {
		YamlConfiguration result = new YamlConfiguration();
		result.set("dungeon", dungeon);
		result.save(getDungeonConfigFile(dungeon.getId()));
	}

	public Dungeon getDungeon(String id) {
		try {
			return loadDungeon(id);
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to load dungeon \"" + id + "\": ", e);
			return null;
		}
	}
	
	public void setDungeon(Dungeon dungeon) {
		if(dungeon == null) {
			return;
		}
		try {
			saveDungeonConfig(dungeon);
			String dir = getDungeonDirectoryPath(dungeon.getId());
			new File(dir + "/snapshots").mkdirs();
			new File(dir + "/schematics").mkdirs();
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to save dungeon \"" + dungeon.getId() + "\": ", e);
			return;
		}
	}
	
	public boolean hasDungeon(String id) {
		return getDungeonConfigFile(id).exists();
	}
	
	public void removeDungeon(String id) {
		deleteFolder(new File(getDungeonDirectoryPath(id)));
	}
	
	public Set<String> getDungeonIds() {
		final Set<String> result = new HashSet<String>();
		directory.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if(hasDungeon(name)) {
					result.add(name);
				}
				return false;
			}
		});
		return result;
	}
	
	
	
	
	
	
	
	
	private static boolean deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if(files == null || files.length == 0) {
			return folder.delete();
		}
		else {
			boolean result = true;
			for (File file : files) {
				if(!deleteFolder(file)) {
					result = false;
				}
			}
			
			if(!result) {
				return false;
			}
			else {
				return folder.delete();
			}
		}
		
	}

	
	
	
	public RestoreControl getRestoreControl(String id) {
		try {
			return new RestoreControl(plugin, getDungeonDirectoryPath(id));
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to get restore-control for \"" + id + "\": ", e);
			return null;
		}
	}
}
