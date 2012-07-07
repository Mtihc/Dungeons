package com.mtihc.minecraft.dungeons.core;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;

public class DungeonSign implements ConfigurationSerializable {

	private String dungeonId;
	private Location signLocation;
	private World regionWorld;
	private Vector regionMin;
	private Vector regionMax;
	
	public DungeonSign(String dungeonId, Location signLocation, World regionWorld, Vector regionMin, Vector regionMax) {
		this.dungeonId = dungeonId;
		this.signLocation = signLocation;
		this.regionWorld = regionWorld;
		this.regionMin = regionMin;
		this.regionMax = regionMax;
	}
	
	@Override
	public DungeonSign clone() {
		return new DungeonSign(dungeonId, signLocation, regionWorld, regionMin, regionMax);
	}
	
	public String getDungeonId() {
		return dungeonId;
	}
	
	public Location getSignLocation() {
		return signLocation;
	}
	
	public void setSignLocation(Location location) {
		this.signLocation = location;
	}
	
	public World getRegionWorld() {
		return regionWorld;
	}
	
	public Vector getRegionMinimumPoint() {
		return regionMin;
	}
	
	public Vector getRegionMaximumPoint() {
		return regionMax;
	}
	
	public void setRegion(World world, Vector min, Vector max) {
		this.regionWorld = world;
		this.regionMin = min;
		this.regionMax = max;
	}
	

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		
		Map<String, Object> signLocationSection = new LinkedHashMap<String, Object>();
		signLocationSection.put("world", signLocation.getWorld().getName());
		signLocationSection.put("location", signLocation.toVector());
		
		values.put("signLocation", signLocationSection);
		
		
		Map<String, Object> regionSection = new LinkedHashMap<String, Object>();
		regionSection.put("world", regionWorld.getName());
		regionSection.put("min", regionMin);
		regionSection.put("max", regionMax);
		
		values.put("region", regionSection);
		
		return values;
	}

	public static DungeonSign deserialize(Map<String, Object> values) {
		
		String dungeonId = (String) values.get("dungeonId");
		
		Map<?, ?> signLocationSection = (Map<?, ?>) values.get("signLocation");
		World signWorld = Bukkit.getWorld(((String) signLocationSection.get("world")));
		Vector vec = (Vector) signLocationSection.get("location");
		
		Map<?, ?> regionSection = (Map<?, ?>) values.get("region");
		World regionWorld = Bukkit.getWorld((String) regionSection.get("world"));
		Vector regionMin = (Vector) regionSection.get("min");
		Vector regionMax = (Vector) regionSection.get("max");
		
		return new DungeonSign(dungeonId, vec.toLocation(signWorld), regionWorld, regionMin, regionMax);
	}
}
