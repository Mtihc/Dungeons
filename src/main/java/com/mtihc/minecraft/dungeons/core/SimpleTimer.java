package com.mtihc.minecraft.dungeons.core;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public abstract class SimpleTimer implements Runnable {

	private int taskId = -1;
	private JavaPlugin plugin;
	
	public SimpleTimer() {
	}
	
	public boolean isRunning() {
		return taskId != -1;
	}
	
	public void schedule(JavaPlugin plugin, long delay) {
		cancel();
		this.plugin = plugin;
		this.taskId = scheduleTask(plugin.getServer().getScheduler(), plugin, delay);
	}
	
	protected int scheduleTask(BukkitScheduler scheduler, JavaPlugin plugin, long delay) {
		return scheduler.scheduleAsyncDelayedTask(plugin, this, delay);
	}
	
	public boolean cancel() {
		if(isRunning()) {
			plugin.getServer().getScheduler().cancelTask(taskId);
			taskId = -1;
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public void run() {
		if(cancel()) {
			onRun();
		}
	}
	
	protected abstract void onRun();

}
