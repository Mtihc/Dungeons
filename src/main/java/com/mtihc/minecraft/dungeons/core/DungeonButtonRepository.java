package com.mtihc.minecraft.dungeons.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class DungeonButtonRepository {

	private JavaPlugin plugin;
	private File directory;

	public DungeonButtonRepository(JavaPlugin plugin, File directory) {
		this.plugin = plugin;
		this.directory = directory;
	}
	
	public DungeonButtonRepository(JavaPlugin plugin, String directory) {
		this(plugin, new File(directory));
	}


	public File getDirectory() {
		return directory;
	}
	
	
	private String locationToString(Location location) {
		return location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
	}
	
	protected String getButtonConfigPath(Location location) {
		return directory + "/" + locationToString(location) + ".yml";
	}
	
	protected File getButtonConfigFile(Location location) {
		return new File(getButtonConfigPath(location));
	}
	
	protected YamlConfiguration getButtonConfig(Location location) throws FileNotFoundException, IOException, InvalidConfigurationException {
		YamlConfiguration result = new YamlConfiguration();
		result.load(getButtonConfigFile(location));
		return result;
	}
	
	protected DungeonButton loadButton(Location location) throws IOException, InvalidConfigurationException {
		try {
			return (DungeonButton) getButtonConfig(location).get("button");
		} catch(FileNotFoundException e) {
			return null;
		}
	}
	
	protected void saveButtonConfig(DungeonButton button) throws IOException {
		YamlConfiguration result = new YamlConfiguration();
		result.set("button", button);
		result.save(getButtonConfigFile(button.getLocation()));
	}
	
	public DungeonButton getButton(Location location) {
		try {
			return loadButton(location);
		} catch(Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to load button at " + locationToString(location), e);
			return null;
		}
	}
	
	public void setButton(DungeonButton button) {
		if(button == null) {
			return;
		}
		try {
			saveButtonConfig(button);
		} catch(IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to save button at " + locationToString(button.getLocation()), e);
			return;
		}
	}
	
	public boolean hasButton(Location location) {
		return getButtonConfigFile(location).exists();
	}
	
	public void removeButton(Location location) {
		getButtonConfigFile(location).delete();
	}
	
	public Collection<Location> getButtonLocations() {
		final Collection<Location> result = new HashSet<Location>();
		directory.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				String[] split = name.split("_");
				if(split.length != 4) {
					return false;
				}
				World world = Bukkit.getWorld(split[0]);
				if(world == null) {
					return false;
				}
				try {
					int x = Integer.parseInt(split[1]);
					int y = Integer.parseInt(split[2]);
					int z = Integer.parseInt(split[3]);
					result.add(new Location(world, x, y, z));
					return false;
				} catch(NumberFormatException e) {
					return false;
				}
				
			}
		});
		return result;
	}
	
	
	
}
