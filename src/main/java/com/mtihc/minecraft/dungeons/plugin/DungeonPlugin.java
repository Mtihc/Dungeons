package com.mtihc.minecraft.dungeons.plugin;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.dungeons.core.Dungeon;
import com.mtihc.minecraft.dungeons.core.DungeonButton;
import com.mtihc.minecraft.dungeons.core.DungeonControl;
import com.mtihc.minecraft.dungeons.core.DungeonControl.LocalDungeon;
import com.mtihc.minecraft.dungeons.core.DungeonControl.LocalPlayer;
import com.mtihc.minecraft.dungeons.plugin.commands.DungeonCommand;
import com.mtihc.minecraft.dungeons.plugin.commands.SimpleCommand;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class DungeonPlugin extends JavaPlugin implements DungeonControl.Observer {

	static {
		// 
		// ConfigurationSerialization
		// 
		ConfigurationSerialization.registerClass(Dungeon.class);
		ConfigurationSerialization.registerClass(DungeonButton.class);
	}
	
	
	
	
	// 
	// static vars
	// 
	private static DungeonPlugin plugin;

	/**
	 * @return the enabled plugin
	 */
	public static DungeonPlugin getPlugin() {
		return plugin;
	}
	
	
	
	
	
	
	
	
	
	// 
	// private vars
	// 
	private SimpleCommand cmd;
	private DungeonControl control;
	
	
	
	
	
	
	
	
	// 
	// onEnable / onDisable
	// 
	
	@Override
	public void onDisable() {

		plugin = null;
	}

	@Override
	public void onEnable() {

		plugin = this;
		
		// 
		// find WorldEdit
		// 
		WorldEditPlugin we = findWorldEdit();
		if(we == null) {
			return;
		}
		
		// 
		// DungeonControl
		// 
		control = new DungeonControl(this, we);
		// observe 
		control.addObserver(this);
		
		// 
		// DungeonCommand
		// 
		PluginCommand command = getCommand("dungeon");
		cmd = new DungeonCommand(null, command.getLabel(), command.getAliases(), command.getPermission());
		
		
	}
	
	
	
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		String lbl = label.toLowerCase();
		if (lbl.equals(cmd.getLabel()) || cmd.getAliases().contains(lbl)) {
			cmd.execute(sender, label, args);
			return true;
		} else {
			return false;
		}
	}
	
	
	
	
	
	public DungeonControl getControl() {
		return control;
	}
	
	
	
	
	
	@Override
	public void onRestoreQueue(String dungeonId, LocalDungeon localDungeon) {
		getLogger().info("[RESTORE] Dungeon \"" + dungeonId + "\" added to region restore queue.");
	}

	@Override
	public void onRestoreStart(String dungeonId, LocalDungeon localDungeon) {
		getLogger().info("[RESTORE] Restoring region of dungeon \"" + dungeonId + "\".");
	}

	@Override
	public void onRestoreFinish(String dungeonId, LocalDungeon localDungeon) {
		getLogger().info("[RESTORE] Finished restoring region of dungeon \"" + dungeonId + "\".");
	}

	@Override
	public void onRestoreCancel(String dungeonId, LocalDungeon localDungeon) {
		getLogger().info("[RESTORE] Stopped restoring region of dungeon \"" + dungeonId + "\".");
	}

	@Override
	public void onDungeonEnd(LocalDungeon localDungeon) {
		getLogger().info("[DUNGEON] Dungeon \"" + localDungeon.getId() + "\" ended.");
	}

	@Override
	public void onDungeonStart(LocalDungeon localDungeon) {
		getLogger().info("[DUNGEON] Dungeon \"" + localDungeon.getId() + "\" started.");
	}

	@Override
	public void onPlayerDespawn(LocalPlayer localPlayer) {
		localPlayer.getPlayer().sendMessage(ChatColor.GREEN + "[DUNGEON] Spawned outside of dungeon \"" + localPlayer.getDungeonId() + "\".");
	}

	@Override
	public void onPlayerRemove(LocalPlayer localPlayer) {
		localPlayer.getPlayer().sendMessage(ChatColor.GREEN + "[DUNGEON] You left dungeon \"" + localPlayer.getDungeonId() + "\".");
	}

	@Override
	public void onPlayerSpawn(LocalPlayer localPlayer) {
		localPlayer.getPlayer().sendMessage(ChatColor.GREEN + "[DUNGEON] Spawned in dungeon \"" + localPlayer.getDungeonId() + "\".");
	}

	@Override
	public void onPlayerAdd(LocalPlayer localPlayer) {
		localPlayer.getPlayer().sendMessage(ChatColor.GREEN + "[DUNGEON] You will join dungeon \"" + localPlayer.getDungeonId() + "\" in a few minutes.");
	}
	
	
	
	
	
	
	
	
	
	
	private WorldEditPlugin findWorldEdit() {
		
		Plugin we = getServer().getPluginManager().getPlugin("WorldEdit");
		if(we != null && we instanceof WorldEditPlugin) {
			
			// WorldEdit found!
			getLogger().log(Level.INFO, we.getDescription().getFullName() + " found!");
			
			return (WorldEditPlugin) we;
		}
		else {
			
			// WorldEdit was not found
			getLogger().log(Level.SEVERE, ChatColor.RED + "WorldEdit plugin was not found.");
			// disable
			getServer().getPluginManager().disablePlugin(this);
			
			return null;
		}
	}
	
	
	
	

}
