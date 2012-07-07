package com.mtihc.minecraft.dungeons.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.dungeons.plugin.DungeonPlugin;
import com.mtihc.minecraft.dungeons.tasks.RestoreControl;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class DungeonControl implements RestoreTaskQueue.Observer {
	
	
	public interface Observer {

		void onRestoreQueue(String dungeonId, LocalDungeon localDungeon);
		void onRestoreStart(String dungeonId, LocalDungeon localDungeon);
		void onRestoreFinish(String dungeonId, LocalDungeon localDungeon);
		void onRestoreCancel(String dungeonId, LocalDungeon localDungeon);
		void onDungeonEnd(LocalDungeon localDungeon);
		void onDungeonStart(LocalDungeon localDungeon);
		void onPlayerDespawn(LocalPlayer localPlayer);
		void onPlayerRemove(LocalPlayer localPlayer);
		void onPlayerSpawn(LocalPlayer localPlayer);
		void onPlayerAdd(LocalPlayer localPlayer);
		
	}
	
	
	
	
	
	
	
	
	
	
	private JavaPlugin plugin;
	private WorldEditPlugin worldEdit;
	private DungeonRepository dungeonRepo;
	private DungeonButtonRepository buttonRepo;
	//private DungeonSignRepository signRepo;
	private RestoreTaskQueue restore;
	
	private Map<String, LocalDungeon> dungeons = new HashMap<String, LocalDungeon>();
	private Map<String, LocalPlayer> players = new HashMap<String, LocalPlayer>();
	private Set<Observer> observers = new LinkedHashSet<Observer>();

	
	
	
	
	
	
	public DungeonControl(JavaPlugin plugin, WorldEditPlugin worldEdit, DungeonRepository dungeons, DungeonButtonRepository buttons, DungeonSignRepository signs) {
		this.plugin = plugin;
		this.worldEdit = worldEdit;
		
		// 
		// DungeonRepository
		// 
		this.dungeonRepo = dungeons;
		
		// 
		// DungeonButtonRepository
		// 
		this.buttonRepo = buttons;
		
		// 
		// RestoreTaskQueue
		// 
		this.restore = new RestoreTaskQueue(this);
		this.restore.addObserver(this);

		// 
		// DungeonListener
		// 
		plugin.getServer().getPluginManager().registerEvents(new DungeonListener(plugin, this), plugin);
	}
	
	public DungeonControl(JavaPlugin plugin, WorldEditPlugin worldEdit) {
		this(
				plugin, 
				worldEdit, 
				new DungeonRepository(
						plugin, 
						plugin.getDataFolder() + "/dungeons"), 
				new DungeonButtonRepository(
						plugin, 
						plugin.getDataFolder() + "/buttons"), 
				new DungeonSignRepository(
						plugin, 
						plugin.getDataFolder() + "/signs"));
	}
	
	
	
	
	
	
	
	
	
	
	
	public JavaPlugin getPlugin() {
		return plugin;
	}
	
	public WorldEditPlugin getWorldEdit() {
		return worldEdit;
	}
	

	
	
	
	
	

	public boolean addObserver(Observer o) {
		return observers.add(o);
	}
	
	public boolean removeObserver(Observer o) {
		return observers.remove(o);
	}
	
	public boolean hasObserver(Observer o) {
		return observers.contains(o);
	}
	
	
	
	
	
	
	
	
	
	
	@Override
	public void onRestoreAddToQueue(String dungeonId) {
		Iterator<Observer> iterator = observers.iterator();
		while(iterator.hasNext()) {
			iterator.next().onRestoreQueue(dungeonId, dungeons.get(dungeonId));
		}
	}

	@Override
	public void onRestoreStart(String dungeonId) {
		Iterator<Observer> iterator = observers.iterator();
		while(iterator.hasNext()) {
			iterator.next().onRestoreStart(dungeonId, dungeons.get(dungeonId));
		}
	}

	@Override
	public void onRestoreFinish(String dungeonId) {
		Iterator<Observer> iterator = observers.iterator();
		while(iterator.hasNext()) {
			iterator.next().onRestoreFinish(dungeonId, dungeons.get(dungeonId));
		}
	}

	@Override
	public void onRestoreCancel(String dungeonId) {
		Iterator<Observer> iterator = observers.iterator();
		while(iterator.hasNext()) {
			iterator.next().onRestoreCancel(dungeonId, dungeons.get(dungeonId));
		}
	}

	
	
	
	
	
	
	
	
	
	public Dungeon loadDungeon(String id) {
		return dungeonRepo.getDungeon(id);
	}
	
	public void saveDungeon(Dungeon dungeon) {
		dungeonRepo.setDungeon(dungeon);
	}

	public void removeDungeon(String id) {
		
		Dungeon d = dungeonRepo.getDungeon(id);
		dungeonRepo.removeDungeon(id);
		if(d != null) {
			for (Location loc : d.getButtons()) {
				buttonRepo.removeButton(loc);
			}
		}
		
	}
	
	public boolean hasDungeon(String id) {
		return dungeonRepo.hasDungeon(id);
	}
	
	public Set<String> getDungeonIds() {
		return dungeonRepo.getDungeonIds();
	}
	
	
	
	
	
	public DungeonButton getButton(Location location) {
		return buttonRepo.getButton(location);
	}
	
	public DungeonButton setButton(Location location, String dungeonId) {
		// check dungeon existance
		Dungeon d = dungeonRepo.getDungeon(dungeonId);
		if(d == null) {
			return null;
		}
		
		// save button file
		DungeonButton btn = new DungeonButton(location, dungeonId);
		buttonRepo.setButton(btn);
		
		// add button to dungeon
		d.addButton(location);
		// save dungeon
		dungeonRepo.setDungeon(d);
		
		return btn;
	}
	
	public void removeButton(Location location) {
		// check button existance
		DungeonButton btn = buttonRepo.getButton(location);
		if(btn == null) {
			return;
		}
		
		
		Dungeon d = dungeonRepo.getDungeon(btn.getDungeonId());
		if(d != null) {
			// remove from dungeon in dungeon repository
			d.removeButton(location);
			dungeonRepo.setDungeon(d);
		}
		
		// remove from button repository
		buttonRepo.removeButton(location);
	}
	
	public boolean hasButton(Location location) {
		return buttonRepo.hasButton(location);
	}
	
	public Collection<Location> getButtonLocations() {
		return buttonRepo.getButtonLocations();
	}
	
	
	
	
	
	
	
	
	/*

	public DungeonSign getSign(Location location) {
		return signRepo.getSign(location);
	}
	
	public boolean setSign(DungeonSign sign) {
		// check dungeon existance
		Dungeon d = dungeonRepo.getDungeon(sign.getDungeonId());
		if(d == null) {
			return false;
		}
		
		// save sign file
		signRepo.setSign(sign);
		
		// add sign to dungeon
		d.addSign(sign.getSignLocation());
		// save dungeon
		dungeonRepo.setDungeon(d);
		
		return true;
	}
	
	public void removeSign(Location location) {
		// check sign existance
		DungeonSign sign = signRepo.getSign(location);
		if(sign == null) {
			return;
		}
		
		
		Dungeon d = dungeonRepo.getDungeon(sign.getDungeonId());
		if(d != null) {
			// remove from dungeon in dungeon repository
			d.removeSign(location);
			dungeonRepo.setDungeon(d);
		}
		
		// remove from button repository
		signRepo.removeSign(sign.getSignLocation());
	}
	
	public boolean hasSign(Location location) {
		return signRepo.hasSign(location);
	}
	
	public Collection<Location> getSignLocations() {
		return signRepo.getSignLocations();
	}
	*/
	
	
	
	
	
	
	
	
	
	
	
	
	public Collection<Location> getDungeonButtonLocations(String id) {
		Dungeon d = loadDungeon(id);
		return d.getButtons();
	}
	
	public LocalDungeon getLocalDungeonByButton(Location location) {
		DungeonButton btn = buttonRepo.getButton(location);
		if(btn != null) {
			String id = btn.getDungeonId();
			return dungeons.get(id);
		}
		else {
			return null;
		}
	}

	public Dungeon getDungeonByButton(Location location) {
		DungeonButton btn = buttonRepo.getButton(location);
		if(btn != null) {
			String id = btn.getDungeonId();
			return dungeonRepo.getDungeon(id);
		}
		else {
			return null;
		}
	}
	
	
	
	
	
	
	

	
	public boolean hasLocalDungeon(String id) {
		return dungeons.containsKey(id);
	}
	
	public LocalDungeon getLocalDungeon(String id) {
		return dungeons.get(id);
	}
	
	public LocalDungeon setLocalDungeon(Dungeon dungeon) {
		LocalDungeon d = dungeons.get(dungeon.getId());
		if(d != null && d.isRunning()) {
			d.stop();
		}
		else {
			d = new LocalDungeon(dungeon);
			dungeons.put(dungeon.getId(), d);
			// removed when stopped
		}
		return d;
	}
	
	public String[] getLocalDungeonIds() {
		return dungeons.keySet().toArray(new String[dungeons.size()]);
	}
	
	
	public boolean hasPlayer(String name) {
		return players.containsKey(name);
	}
	
	public LocalPlayer getPlayer(String name) {
		return players.get(name);
	}
	
	public String[] getPlayerNames() {
		return players.keySet().toArray(new String[players.size()]);
	}
	


	protected RestoreControl getRestoreControl(String dungeonId) {
		return dungeonRepo.getRestoreControl(dungeonId);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//on button press
	public class LocalDungeon {
		
		protected Dungeon dungeon;
		protected Map<String, LocalPlayer> players = new HashMap<String, LocalPlayer>();
		
		private boolean started = false;
		private boolean stopped = false;
		private SimpleTimer startTimer;
		
		
		LocalDungeon(Dungeon dungeon) {
			this.dungeon = dungeon;
			
		}
		
		public String getId() {
			return dungeon.getId();
		}
		
		public Dungeon getDungeon() {
			return dungeon;
		}
		
		public boolean hasPlayers() {
			return !players.isEmpty();
		}
		
		public int getTotalPlayers() {
			return players.size();
		}
		
		public LocalPlayer getPlayerByName(String name) {
			return players.get(name);
		}
		
		public boolean addPlayer(Player player) {
			LocalPlayer p = players.get(player.getName());
			if(p != null && p.getDungeon() != null && p.getDungeon().isRunning()) {
				return false;
			}
			
			String name = player.getName();
			p = new LocalPlayer(player);
			p.dungeon = this;
			players.put(name, p);
			DungeonControl.this.players.put(name, p);
			onPlayerAdd(p);
			
			if(isRunning()) {
				// spawn player (late)
				onPlayerSpawn(p);
				player.teleport(dungeon.getNextRandomSpawn(new Random()));
			}
			return true;
		}

		public void removePlayer(String name) {
			removePlayer(name, null, true);
		}
		
		protected void removePlayer(String name, Random random, boolean despawn) {
			LocalPlayer p = players.remove(name);
			if(p == null) {
				return;
			}
			if(p.hurtTimer != null) {
				p.hurtTimer.cancel();
				p.hurtTimer = null;
			}
			
			DungeonControl.this.players.remove(name);
			onPlayerRemove(p);
			
			if(started && despawn) {
				if(random == null) {
					random = new Random();
				}
				onPlayerDespawn(p);
				p.getPlayer().teleport(dungeon.getNextRandomDespawn(random));
			}
			p.dungeon = null;
			
			if(!stopped && players.isEmpty()) {
				stop();
			}
		}
		
		public LocalPlayer[] getPlayers() {
			Collection<LocalPlayer> values = players.values();
			return values.toArray(new LocalPlayer[values.size()]);
		}
		
		public String[] getPlayerNames() {
			Set<String> keys = players.keySet();
			return keys.toArray(new String[keys.size()]);
		}
		
		public boolean isRestoring() {
			return restore.has(getId()) || (startTimer != null && startTimer.isRunning());
		}
		
		public void restoreStart() {
			if(restore.has(getId())) {
				return;
			}
			restore.add(getId());
			
			startTimer = new SimpleTimer() {
				
				@Override
				protected void onRun() {
					startTimer = null;
					if(!players.isEmpty()) {
						// if not still restoring
						// (something similar is checked when restoring ends)
						if(!restore.has(getId())) {
							start();
						}
					}
					else {
						stop();
					}
					
				}
			};
			// TODO configurable start delay instead of hardcoded
			startTimer.schedule(DungeonPlugin.getPlugin(), 600);
		}
		
		public void restoreCancel() {
			restore.remove(getId());
			if(startTimer != null) {
				startTimer.cancel();
				startTimer = null;
			}
		}
		
		public void start() {
			
			if(players.isEmpty()) {
				return;
			}
			

			restoreCancel();
			
			started = true;
			stopped = false;
			
			// spawn players
			Random random = new Random();
			for (LocalPlayer p : players.values()) {
				onPlayerSpawn(p);
				p.getPlayer().teleport(dungeon.getNextRandomSpawn(random));
			}
			
			// dungeon is probably already in the map at this point, adding it just in case
			DungeonControl.this.dungeons.put(getId(), this);
			onDungeonStart(this);
		}
		
		public void stop() {

			restoreCancel();

			stopped = true;
			
			if(!players.isEmpty()) {
				// despawn players
				Random random = new Random();
				String[] names = players.keySet().toArray(new String[players.size()]);
				for (String name : names) {
					removePlayer(name, random, true);
				}
			}
			
			
			// remove dungeon from map
			onDungeonEnd(DungeonControl.this.dungeons.remove(getId()));
		}
		
		public boolean isStarted() {
			return started;
		}
		
		public boolean isStopped() {
			return stopped;
		}
		
		public boolean isRunning() {
			return started && !stopped;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public class LocalPlayer {
		
		protected Player player;
		private LocalDungeon dungeon = null;
		private boolean exiting = false;
		private boolean outside = false;
		private SimpleTimer hurtTimer;
		
		LocalPlayer(Player player) {
			this.player = player;
		}
		
		public String getName() {
			return player.getName();
		}
		
		public String getDisplayName() {
			return player.getDisplayName();
		}
		
		public Player getPlayer() {
			Player p = Bukkit.getPlayer(getName());
			if(p != null) {
				this.player = p;
			}
			return this.player;
		}
		
		public String getDungeonId() {
			if(dungeon == null) {
				return null;
			}
			else {
				return dungeon.getId();
			}
		}
		
		public LocalDungeon getDungeon() {
			return dungeon;
		}

		public boolean isExiting() {
			return exiting;
		}
		
		protected void setExiting(boolean exiting) {
			this.exiting = exiting;
		}
		
		public boolean isOutsideGameRegion() {
			return outside;
		}

		protected void setOutsideGameRegion(boolean value) {
			if(outside != value) {
				outside = value;
				if(value) {
					// on exit game region
					if(hurtTimer == null || !hurtTimer.isRunning()) {
						getPlayer().damage(1);
						hurtTimer = new SimpleTimerRepeating() {
							
							@Override
							protected void onRun() {
								if(outside) {
									Player p = getPlayer();
									
									p.damage(1);
									p.sendMessage(ChatColor.RED + "Go back! You are leaving the dungeon's area.");
								}
								else {
									cancel();
									hurtTimer = null;
								}
							}
						};
						hurtTimer.schedule(plugin, 20L);
					}
				}
				else {
					// on enter game region
					if(hurtTimer != null) {
						hurtTimer.cancel();
						hurtTimer = null;
					}
				}
			}
		}
		
	}
	
	
	
	private void onDungeonEnd(LocalDungeon localDungeon) {
		Iterator<Observer> iterator = observers.iterator();
		while(iterator.hasNext()) {
			iterator.next().onDungeonEnd(localDungeon);
		}
	}

	private void onDungeonStart(LocalDungeon localDungeon) {
		Iterator<Observer> iterator = observers.iterator();
		while(iterator.hasNext()) {
			iterator.next().onDungeonStart(localDungeon);
		}
	}

	private void onPlayerDespawn(LocalPlayer localPlayer) {
		Iterator<Observer> iterator = observers.iterator();
		while(iterator.hasNext()) {
			iterator.next().onPlayerDespawn(localPlayer);
		}
	}

	private void onPlayerRemove(LocalPlayer localPlayer) {
		Iterator<Observer> iterator = observers.iterator();
		while(iterator.hasNext()) {
			iterator.next().onPlayerRemove(localPlayer);
		}
	}

	private void onPlayerSpawn(LocalPlayer localPlayer) {
		Iterator<Observer> iterator = observers.iterator();
		while(iterator.hasNext()) {
			iterator.next().onPlayerSpawn(localPlayer);
		}
	}

	private void onPlayerAdd(LocalPlayer localPlayer) {
		Iterator<Observer> iterator = observers.iterator();
		while(iterator.hasNext()) {
			iterator.next().onPlayerAdd(localPlayer);
		}
	}
	
}
