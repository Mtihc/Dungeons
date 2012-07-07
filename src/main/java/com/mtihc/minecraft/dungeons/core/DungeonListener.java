package com.mtihc.minecraft.dungeons.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.dungeons.core.DungeonControl.LocalDungeon;
import com.mtihc.minecraft.dungeons.core.DungeonControl.LocalPlayer;


public class DungeonListener implements Listener {
	
	
	
	
	
	private final JavaPlugin plugin;
	private final DungeonControl control;
	private final Map<String, SimpleTimer> autoStartTimers = new HashMap<String, SimpleTimer>();
	
	
	
	
	/**
	 * Constructor
	 * @param plugin The plugin
	 * @param control The dungeon control
	 */
	protected DungeonListener(JavaPlugin plugin, DungeonControl control) {
		this.plugin = plugin;
		this.control = control;
	}
	
	
	
	
	
	
	
	
	
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		
		if(event.isCancelled() || event.useInteractedBlock().equals(Result.DENY)) {
			// event is cancelled, 
			// or denied
			return;
		}
		
		if(event.getClickedBlock() == null || !event.getClickedBlock().getType().equals(Material.STONE_BUTTON)) {
			// didn't click a button
			return;
		}
		// 
		// clicked stone button
		// 
		
		Dungeon dungeon = control.getDungeonByButton(event.getClickedBlock().getLocation());
		if(dungeon == null) {
			// button was not linked to a dungeon
			return;
		}
		
		// 
		// clicked stone button, that starts a dungeon
		// 
		
		
		
		final String dungeonId = dungeon.getId();
		
		LocalPlayer p = control.getPlayer(event.getPlayer().getName());
		if(p == null || p.getDungeon() == null) {
			// 
			// player is not in a dungeon
			// 
			
			
			
			LocalDungeon d = control.getLocalDungeon(dungeonId);
			
			if(d == null || d.isStopped()) {
				// 
				// dungeon is not started yet, 
				// create the dungeon
				// 
				d = control.setLocalDungeon(dungeon);
			}
			
			
			
			
			
			if(d.isRunning()) {
				// error: dungeon is already running
				event.getPlayer().sendMessage(ChatColor.RED + "Dungeon \"" + dungeonId + "\" is not available right now. Try again later.");
				return;
			}
			else {
				
				if(!d.hasPlayers()) {
					// 
					// first player to press the button, 
					// restore and autostart
					// 
					d.restoreStart();
				}
				
				// add player to dungeon
				d.addPlayer(event.getPlayer());
			}
		}
		else {
			// 
			// player is already in a dungeon
			// 
			LocalDungeon d = p.getDungeon();
			if(d.getId().equals(dungeonId)) {
				// 
				// clicked a button of his current dungeon
				// 
				if(!d.isStarted()) {
					// 
					// dungeon is not yet started
					// 
					
					// remove player
					d.removePlayer(event.getPlayer().getName());
					
					
					if(!d.hasPlayers()) {
						// 
						// no more players left
						// 
						// stop() is called automatically, 
						// when the last player leaves
						// 
						SimpleTimer timer = autoStartTimers.remove(dungeonId);
						if(timer != null) {
							timer.cancel();
						}
					}
				}
			}
			
		}
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(event.isCancelled()) {
			return;
		}
		
		
		Block block = event.getBlock();
		Location loc = block.getLocation();
		
		if(block.getType().equals(Material.STONE_BUTTON) && control.hasButton(loc)) {
			// is a button
			event.setCancelled(true);
		}
		else {
			ArrayList<Block> btns = getAttachedButtons(block);
			for (Block btn : btns) {
				if(control.hasButton(btn.getLocation())) {
					// has a button attached
					event.setCancelled(true);
					break;
				}
			}
		}
	}
	
	private ArrayList<Block> getAttachedButtons(Block block) {
		ArrayList<Block> list = new ArrayList<Block>();
		Block rel;
		
		rel = block.getRelative(BlockFace.EAST);
		if(rel.getType().equals(Material.STONE_BUTTON)) {
			list.add(rel);
		}
		
		rel = block.getRelative(BlockFace.NORTH);
		if(rel.getType().equals(Material.STONE_BUTTON)) {
			list.add(rel);
		}
		
		rel = block.getRelative(BlockFace.WEST);
		if(rel.getType().equals(Material.STONE_BUTTON)) {
			list.add(rel);
		}
		
		rel = block.getRelative(BlockFace.SOUTH);
		if(rel.getType().equals(Material.STONE_BUTTON)) {
			list.add(rel);
		}
		
		return list;
		
	}
	
	
	
	
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		if(event.isCancelled()) {
			return;
		}
		
		Block block = event.getBlock();
		
		ArrayList<Block> btns = getAttachedButtons(block);
		for (Block btn : btns) {
			if(control.hasButton(btn.getLocation())) {
				event.setCancelled(true);
				break;
			}
		}
	}
	
	
	
	
	
	
	@EventHandler
	public void onBlockExplode(EntityExplodeEvent event) {
		Iterator<Block> iterator = event.blockList().iterator();
		while(iterator.hasNext()) {
			
			Block block = iterator.next();
			Location loc = block.getLocation();
			
			if(block.getType().equals(Material.STONE_BUTTON) && control.hasButton(loc)) {
				// is a button
				iterator.remove();
			}
			else {
				ArrayList<Block> btns = getAttachedButtons(block);
				for (Block btn : btns) {
					if(control.hasButton(btn.getLocation())) {
						// has a button attached
						iterator.remove();
						break;
					}
				}
			}
		}
	}
	
	
	
	
	
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if(event.isCancelled()) {
			// cancelled
			return;
		}
		
		LocalPlayer p = control.getPlayer(event.getPlayer().getName());

		if(p == null || p.getDungeon() == null) {
			// player is not in a dungeon
			return;
		}
		
		LocalDungeon d = control.getLocalDungeon(p.getDungeonId());
		if(d == null || !d.isRunning()) {
			// not running
			return;
		}
		
		// 
		// Player is in a dungeon that is running
		// 
		
		Location to = event.getTo();
		
		if(!d.getDungeon().containsExitLocation(to)) {
			
			// 
			// not in the exit region
			// 
			
			p.setExiting(false);
			
			
			if(d.getDungeon().containsDungeonLocation(to)) {
				// is in the dungeon region
				// cancel kill, if scheduled
				p.setOutsideGameRegion(false);
				
				
			}
			else {
				// not the dungeon region
				// schedule kill
				p.setOutsideGameRegion(true);
				return;
			}
			
			
			
			
		}
		else {
			// 
			// in the exit region
			// 
			if(!p.isExiting()) {
				// just entered the region
				p.setExiting(true);
				
				
				// check if all players are in exit region
				boolean allExited = true;
				for (String name : d.getPlayerNames()) {
					LocalPlayer dp = control.getPlayer(name);
					if(dp != null && !dp.isExiting()) {
						allExited = false;
					}
				}
				
				
				if(allExited) {
					// everyone made it to the exit
					d.stop();
				}
				else {
					// wait here for your friends
					event.getPlayer().sendMessage(ChatColor.GREEN + "You're at the end of the dungeon.");
					event.getPlayer().sendMessage(ChatColor.GREEN + "Wait here for your friends.");
				}
			}
		}
	}
	
	
	
	
	
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		
		String name = event.getEntity().getName();
		LocalPlayer p = control.getPlayer(name);
		
		Bukkit.getLogger().info(name + " died. Is in dungeon: " + (p != null && p.getDungeon() != null));
		
		// Remove player from dungeon.
		// don't despawn, rely on death respawn
		if(p != null && p.getDungeon() != null) {
			p.getDungeon().removePlayer(name, null, false);
		}
	}
	
	
	
	
	
	
	
	
	private Map<String, SimpleTimer> playersOffline = new HashMap<String, SimpleTimer>();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		String name = event.getPlayer().getName();
		SimpleTimer timer = playersOffline.remove(name);
		if(timer != null) {
			timer.cancel();
		}
		
		LocalPlayer p = control.getPlayer(name);
		if(p != null) {
			p.player = event.getPlayer();
		}
	}
	
	@EventHandler
	public void onQuit(final PlayerQuitEvent event) {
		onLeave(event);
	}
	
	@EventHandler
	public void onKick(final PlayerKickEvent event) {
		onLeave(event);
	}
	
	protected void onLeave(final PlayerEvent event) {
		final String name = event.getPlayer().getName();
		final LocalPlayer p = control.getPlayer(name);
		if(p == null || p.getDungeon() == null) {
			return;
		}
		final LocalDungeon d = p.getDungeon();
		p.player = event.getPlayer();
		SimpleTimer timer = new SimpleTimer() {

			@Override
			public void onRun() {
				d.removePlayer(name);
			}
			
		};
		playersOffline.put(name, timer);
		timer.schedule(plugin, 1800L);
	}
	
	
}
