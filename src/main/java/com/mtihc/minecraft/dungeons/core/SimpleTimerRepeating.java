package com.mtihc.minecraft.dungeons.core;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;


public abstract class SimpleTimerRepeating extends SimpleTimer {

	@Override
	protected int scheduleTask(BukkitScheduler scheduler, JavaPlugin plugin,
			long delay) {
		return scheduler.scheduleAsyncRepeatingTask(plugin, this, delay, delay);
	}

	@Override
	public void run() {
		// don't cancel
		if(isRunning()) {
			onRun();
		}
	}


}
