package com.mtihc.minecraft.dungeons.core;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;

public class DungeonButton implements ConfigurationSerializable {

	private Location location;
	private String dungeonId;

	public DungeonButton(Location location, String dungeonId) {
		this.location = location;
		this.dungeonId = dungeonId;
	}
	
	public World getWorld() {
		return location.getWorld();
	}
	
	public Location getLocation() {
		return location;
	}
	
	public String getDungeonId() {
		return dungeonId;
	}
	
	public void setDungeonId(String id) {
		this.dungeonId = id;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("world", location.getWorld().getName());
		values.put("location", location.toVector());
		values.put("dungeonId", dungeonId);
		return values;
	}
	
	public static DungeonButton deserialize(Map<String, Object> values) {
		World world = Bukkit.getWorld((String) values.get("world"));
		if(world == null) {
			return null;
		}
		Vector vec = (Vector) values.get("location");
		if(vec == null) {
			return null;
		}
		Location loc = vec.toLocation(world);
		
		String dungeonId = (String) values.get("dungeonId");
		if(dungeonId == null) {
			return null;
		}
		
		return new DungeonButton(loc, dungeonId);
	}

}
