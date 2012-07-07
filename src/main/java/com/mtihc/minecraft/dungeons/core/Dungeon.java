package com.mtihc.minecraft.dungeons.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;

public class Dungeon implements ConfigurationSerializable {

	private String id;
	private World world;
	private Vector min;
	private Vector max;
	private World despawnWorld;
	private Vector despawnMin;
	private Vector despawnMax;
	private Vector exitMin;
	private Vector exitMax;
	private Vector spawnMin;
	private Vector spawnMax;
	private Map<String, Location> buttons = new HashMap<String, Location>();
	private Map<String, Location> signs = new HashMap<String, Location>();
	
	public Dungeon(String id, World world, Vector min, Vector max) {
		this(id, world, min, max, min, max, new Vector(), new Vector(), world, new Vector(), new Vector());
	}

	public Dungeon(String id, World world, Vector min, Vector max, Vector spawnMin, Vector spawnMax, Vector exitMin, Vector exitMax, World despawnWorld, Vector despawnMin, Vector despawnMax) {
		this.id = id;
		this.world = world;
		this.min = min;
		this.max = max;
		this.spawnMin = spawnMin;
		this.spawnMax = spawnMax;
		this.exitMin = exitMin;
		this.exitMax = exitMax;
		this.despawnWorld = despawnWorld;
		this.despawnMin = despawnMin;
		this.despawnMax = despawnMax;
	}
	
	public String getId() {
		return id;
	}
	
	public World getWorld() {
		return world;
	}
	
	
	
	
	
	public Vector getMinimumPoint() {
		return min;
	}
	
	public Vector getMaximumPoint() {
		return max;
	}
	
	public boolean containsDungeonLocation(Location location) {
		return containsLocation(this.world.getName(), min, max, location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}
	
	public void setDungeonRegion(Vector min, Vector max) {
		this.min = min;
		this.max = max;
	}
	
	
	
	
	
	
	
	

	public World getDespawnWorld() {
		return despawnWorld;
	}
	
	public Vector getDespawnMinimumPoint() {
		return despawnMin;
	}
	public Vector getDespawnMaximumPoint() {
		return despawnMax;
	}
	
	public boolean containsDespawnLocation(Location location) {
		return containsLocation(this.despawnWorld.getName(), despawnMin, despawnMax, location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}
	
	public void setDespawnRegion(World world, Vector minimumPoint, Vector maximumPoint) {
		this.despawnWorld = world;
		this.despawnMin = minimumPoint;
		this.despawnMax = maximumPoint;
	}
	
	
	
	
	
	
	public Vector getExitMinimumPoint() {
		return exitMin;
	}
	
	public Vector getExitMaximumPoint() {
		return exitMax;
	}
	
	public boolean containsExitLocation(Location location) {
		return containsLocation(this.world.getName(), exitMin, exitMax, location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}
	
	public void setExitRegion(Vector minimumPoint, Vector maximumPoint) {
		this.exitMin = minimumPoint;
		this.exitMax = maximumPoint;
	}
	
	
	
	
	
	
	
	public Vector getSpawnMinimumPoint() {
		return spawnMin;
	}
	
	public Vector getSpawnMaximumPoint() {
		return spawnMax;
	}
	
	public boolean containsSpawnLocation(String world, int x, int y, int z) {
		return containsLocation(this.world.getName(), spawnMin, spawnMax, world, x, y, z);
	}
	
	public void setSpawnRegion(Vector minimumPoint, Vector maximumPoint) {
		this.spawnMin = minimumPoint;
		this.spawnMax = maximumPoint;
	}
	
	
	
	
	
	

	
	public Location getNextRandomSpawn(Random random) {
		return Dungeon.getNextRandomLocation(world, spawnMin, spawnMax, random);
	}
	
	public Location getNextRandomDespawn(Random random) {
		return Dungeon.getNextRandomLocation(despawnWorld, despawnMin, despawnMax, random);
	}
	
	
	
	


	public int getTotalButtons() {
		return buttons.size();
	}
	
	public void addButton(Location location) {
		buttons.put(locationToString(location), location);
	}
	
	public boolean removeButton(Location location) {
		return buttons.remove(locationToString(location)) != null;
	}
	
	public Collection<Location> getButtons() {
		return buttons.values();
	}
	
	
	public int getTotalSigns() {
		return signs.size();
	}
	
	public void addSign(Location location) {
		signs.put(locationToString(location), location);
	}
	
	public boolean removeSign(Location location) {
		return signs.remove(locationToString(location)) != null;
	}
	
	public Collection<Location> getSigns() {
		return signs.values();
	}

	private String locationToString(Location location) {
		return location.getWorld().getName() + "_" + location.getBlockX() + "_" + location.getBlockY() + "_" + location.getBlockZ();
	}
	
	
	


	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		
		LinkedHashMap<String, Object> regionSection = new LinkedHashMap<String, Object>();
		regionSection.put("world", world.getName());
		regionSection.put("min", min);
		regionSection.put("max", max);
		
		LinkedHashMap<String, Object> spawnSection = new LinkedHashMap<String, Object>();
		spawnSection.put("min", spawnMin);
		spawnSection.put("max", spawnMax);
		
		LinkedHashMap<String, Object> exitSection = new LinkedHashMap<String, Object>();
		exitSection.put("min", exitMin);
		exitSection.put("max", exitMax);
		
		LinkedHashMap<String, Object> despawnSection = new LinkedHashMap<String, Object>();
		despawnSection.put("world", despawnWorld.getName());
		despawnSection.put("min", despawnMin);
		despawnSection.put("max", despawnMax);
		
		if(buttons.isEmpty()) {
			LinkedHashMap<String, Object> buttonSection = new LinkedHashMap<String, Object>();
			int index = 0;
			for (Location btn : buttons.values()) {
				HashMap<String, Object> btnMap = new HashMap<String, Object>();
				btnMap.put("world", btn.getWorld().getName());
				btnMap.put("location", btn.toVector());
				buttonSection.put("btn" + index, btnMap);
				index++;
			}
			result.put("buttons", buttonSection);
		}
		
		
		if(!signs.isEmpty()) {
			LinkedHashMap<String, Object> signSection = new LinkedHashMap<String, Object>();
			int index = 0;
			for (Location sign : signs.values()) {
				HashMap<String, Object> signMap = new HashMap<String, Object>();
				signMap.put("world", sign.getWorld().getName());
				signMap.put("location", sign.toVector());
				signSection.put("btn" + index, signMap);
				index++;
			}
			
			result.put("signs", signSection);
		}
		
		
		result.put("id", id);
		result.put("region", regionSection);
		result.put("spawn", spawnSection);
		result.put("exit", exitSection);
		result.put("despawn", despawnSection);
		
		return result;
	}
	
	public static Dungeon deserialize(Map<String, Object> values) {
		
		String id = values.get("id").toString();
		
		Map<?, ?> regionSection = (Map<?, ?>) values.get("region");
		Map<?, ?> spawnSection = (Map<?, ?>) values.get("spawn");
		Map<?, ?> exitSection = (Map<?, ?>) values.get("exit");
		Map<?, ?> despawnSection = (Map<?, ?>) values.get("despawn");
		Map<?, ?> buttonSection = (Map<?, ?>) values.get("buttons");
		Map<?, ?> signSection = (Map<?, ?>) values.get("signs");
		
		World world = Bukkit.getWorld((String) regionSection.get("world"));
		Vector min = (Vector) regionSection.get("min");
		Vector max = (Vector) regionSection.get("max");
		
		Vector spawnMin = (Vector) spawnSection.get("min");
		Vector spawnMax = (Vector) spawnSection.get("max");

		Vector exitMin = (Vector) exitSection.get("min");
		Vector exitMax = (Vector) exitSection.get("max");
		
		World despawnWorld = Bukkit.getWorld((String) despawnSection.get("world"));
		Vector despawnMin = (Vector) despawnSection.get("min");
		Vector despawnMax = (Vector) despawnSection.get("max");
		
		
		Dungeon d = new Dungeon(id, world, min, max, spawnMin, spawnMax, exitMin, exitMax, despawnWorld, despawnMin, despawnMax);
		
		if(buttonSection != null) {
			Collection<?> buttonValues = buttonSection.values();
			for (Object value : buttonValues) {
				Map<?, ?> btnMap = (Map<?, ?>) value;
				World w = Bukkit.getWorld((String) btnMap.get("world"));
				Vector v = (Vector) btnMap.get("location");
				d.addButton(v.toLocation(w));
			}
		}
		

		if(signSection != null) {
			Collection<?> signValues = signSection.values();
			for (Object value : signValues) {
				Map<?, ?> signMap = (Map<?, ?>) value;
				World w = Bukkit.getWorld((String) signMap.get("world"));
				Vector v = (Vector) signMap.get("location");
				d.addSign(v.toLocation(w));
			}
		}
		
		
		
		return d;
	}

	
	public static boolean containsLocation(String regionWorld, Vector regionMin, Vector regionMax, String world, int x, int y, int z) {
		return regionWorld.equals(world) 
				&& regionMin.getBlockX() <= x && x <= regionMax.getBlockX() 
				&& regionMin.getBlockY() <= y && y <= regionMax.getBlockY()
				&& regionMin.getBlockZ() <= z && z <= regionMax.getBlockZ();
	}
	
	

	public static Location getNextRandomLocation(World world, Vector min, Vector max, Random random) {
		int xDiff = max.getBlockX() - min.getBlockX();
		int zDiff = max.getBlockZ() - max.getBlockZ();
		
		int xR = Math.max(1, random.nextInt(xDiff + 1)) - 1;
		int zR = Math.max(1, random.nextInt(zDiff + 1)) - 1;
		
		int x = xR + min.getBlockX();
		int y = min.getBlockY();
		int z = zR + min.getBlockZ();
		
		Vector vec = new Vector(x, y, z);
		Block block = vec.toLocation(world).getBlock();
		Block above = block.getRelative(0, 1, 0);//up
		while(!block.isEmpty() && !above.isEmpty()) {
			block = above;
			above = block.getRelative(0, 1, 0);
		}
		
		return block.getLocation();
	}
	
}
