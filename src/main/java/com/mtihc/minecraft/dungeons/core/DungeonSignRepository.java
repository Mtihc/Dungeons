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

public class DungeonSignRepository {

	private JavaPlugin plugin;
	private File directory;

	public DungeonSignRepository(JavaPlugin plugin, File directory) {
		this.plugin = plugin;
		this.directory = directory;
	}
	
	public DungeonSignRepository(JavaPlugin plugin, String directory) {
		this(plugin, new File(directory));
	}
	
	public File getDirectory() {
		return directory;
	}
	
	protected String locationToString(Location location) {
		return location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
	}

	protected String getSignConfigFilePath(Location location) {
		return directory + "/" + locationToString(location);
	}
	
	protected File getSignConfigFile(Location location) {
		return new File(getSignConfigFilePath(location));
	}
	
	protected YamlConfiguration getSignConfig(Location location) throws FileNotFoundException, IOException, InvalidConfigurationException {
		File file = getSignConfigFile(location);
		YamlConfiguration config = new YamlConfiguration();
		config.load(file);
		return config;
	}
	
	public DungeonSign getSign(Location location) {
		try {
			YamlConfiguration config = getSignConfig(location);
			return (DungeonSign) config.get("Sign");
		} catch (FileNotFoundException e) {
			return null;
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to load dungeon Sign at " + locationToString(location), e);
			return null;
		}
	}
	
	public void setSign(DungeonSign Sign) {
		Location location = Sign.getSignLocation();
		YamlConfiguration config = new YamlConfiguration();
		config.set("Sign", Sign);
		try {
			config.save(getSignConfigFile(location));
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to save dungeon Sign at " + locationToString(location));
			return;
		}
	}
	
	public boolean removeSign(Location location) {
		return getSignConfigFile(location).delete();
	}
	
	public boolean hasSign(Location location) {
		return getSignConfigFile(location).exists();
	}
	

	public Collection<Location> getSignLocations() {
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
