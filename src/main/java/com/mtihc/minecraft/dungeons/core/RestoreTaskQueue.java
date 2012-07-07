package com.mtihc.minecraft.dungeons.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.mtihc.minecraft.dungeons.tasks.RestoreControl;
import com.mtihc.minecraft.dungeons.tasks.RestoreTask;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;

public class RestoreTaskQueue {

	public interface Observer {
		void onRestoreAddToQueue(String dungeonId);
		void onRestoreStart(String dungeonId);
		void onRestoreFinish(String dungeonId);
		void onRestoreCancel(String dungeonId);
	}
	
	public long delay = 20;
	public int subregionSize = 50;
	private ArrayList<String> queue = new ArrayList<String>();
	private Task currentTask;
	private DungeonControl control;
	
	private Set<Observer> observers = new LinkedHashSet<Observer>();
	
	public RestoreTaskQueue(DungeonControl control) {
		this.control = control;
	}
	
	public boolean addObserver(Observer o) {
		return observers.add(o);
	}
	
	public boolean hasObserver(Observer o) {
		return observers.contains(o);
	}
	
	public boolean removeObserver(Observer o) {
		return observers.remove(o);
	}

	public void add(String dungeonId) {
		
		if(queue.isEmpty() && currentTask == null) {
			queue.add(dungeonId);
			next();
		}
		else {
			queue.add(dungeonId);
			onRestoreAddToQueue(dungeonId);
		}
		
	}
	
	public String[] getQueue() {
		return queue.toArray(new String[queue.size()]);
	}
	
	public void remove(String dungeonId) {
		if(currentTask != null && currentTask.dungeonId.equals(dungeonId)) {
			currentTask.cancel();
		}
		else if(queue.remove(dungeonId)) {
			onRestoreCancel(dungeonId);
		}
	}
	
	
	public boolean has(String dungeonId) {
		return queue.contains(dungeonId);
	}
	
	
	
	private LocalWorld getLocalWorld(String name) {
		List<LocalWorld> worlds = control.getWorldEdit().getServerInterface().getWorlds();
		for (LocalWorld w : worlds) {
			if(w.getName().equals(name)) {
				return w;
			}
		}
		return null;
	}
	
	private Vector getVector(org.bukkit.util.Vector vec) {
		return new Vector(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
	}

	private void next() {
		if(queue.isEmpty()) {
			return;
		}
		
		String dungeonId = queue.remove(0);
		
		Dungeon d = control.loadDungeon(dungeonId);
		if(d == null) {
			return;
		}
		
		Task task = new Task(
				dungeonId, 
				control.getRestoreControl(dungeonId), 
				new CuboidRegion(
						getLocalWorld(d.getWorld().getName()), 
						getVector(d.getMinimumPoint()), 
						getVector(d.getMaximumPoint())));
		task.start();
	}
	
	public class Task extends RestoreTask {

		
		
		private String dungeonId;

		public Task(String dungeonId, RestoreControl control, CuboidRegion region) {
			super(control, region, Math.max(RestoreTaskQueue.this.delay, 2), Math.max(RestoreTaskQueue.this.subregionSize, 10));
			this.dungeonId = dungeonId;
		}
		
		public String getDungeonId() {
			return dungeonId;
		}

		@Override
		protected void onStart() {
			RestoreTaskQueue.this.currentTask = this;
			onRestoreStart(dungeonId);
		}

		@Override
		protected void onCancel() {
			if(RestoreTaskQueue.this.currentTask == this) {
				RestoreTaskQueue.this.currentTask = null;
			}
			onRestoreCancel(dungeonId);
		}

		@Override
		protected void onFinish() {
			if(RestoreTaskQueue.this.currentTask == this) {
				RestoreTaskQueue.this.currentTask = null;
			}
			onRestoreFinish(dungeonId);
		}
		
	}
	
	private void onRestoreCancel(String dungeonId) {
		Iterator<Observer> os = observers.iterator();
		while(os.hasNext()) {
			Observer o = os.next();
			o.onRestoreCancel(dungeonId);
		}
	}
	
	private void onRestoreFinish(String dungeonId) {
		Iterator<Observer> os = observers.iterator();
		while(os.hasNext()) {
			Observer o = os.next();
			o.onRestoreFinish(dungeonId);
		}
	}
	
	private void onRestoreAddToQueue(String dungeonId) {
		Iterator<Observer> os = observers.iterator();
		while(os.hasNext()) {
			Observer o = os.next();
			o.onRestoreAddToQueue(dungeonId);
		}
	}
	
	private void onRestoreStart(String dungeonId) {
		Iterator<Observer> os = observers.iterator();
		while(os.hasNext()) {
			Observer o = os.next();
			o.onRestoreStart(dungeonId);
		}
	}
	
	
	
	
}
