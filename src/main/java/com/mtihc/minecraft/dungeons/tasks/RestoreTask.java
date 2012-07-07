package com.mtihc.minecraft.dungeons.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.data.ChunkStore;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import com.sk89q.worldedit.snapshots.Snapshot;
import com.sk89q.worldedit.snapshots.SnapshotRestore;

public abstract class RestoreTask implements Runnable {
	
	private RestoreControl control;
	private JavaPlugin plugin;
	private CuboidRegion region;
	private long delay;
	private int subregionSize;
	private int taskId;
	private ChunkStore chunkStore;
	private Iterator<CuboidRegion> regions;
	private Iterator<File> schematics;

	public RestoreTask(RestoreControl control, CuboidRegion region, long delay, int subregionSize) {
		this.control = control;
		this.plugin = control.getPlugin();
		this.region = region;
		this.delay = delay;
		this.subregionSize = subregionSize;
		this.taskId = -1;
		
	}
	
	public RestoreControl getRegionControl() {
		return control;
	}
	
	public CuboidRegion getRegion() {
		return region;
	}
	
	public boolean isRunning() {
		return taskId != -1;
	}
	
	public void start() {
		if(!isRunning()) {
			onStart();
			
			// toggle isRunning
			taskId = 0;
			
			Bukkit.getLogger().info("Getting snapshot");
			Snapshot snapshot = control.getRandomSnapshot();
			Bukkit.getLogger().info("Got snapshot");
			
			if(snapshot != null) {
				try {
					Bukkit.getLogger().info("Getting chunkStore");
					this.chunkStore = snapshot._getChunkStore();
					Bukkit.getLogger().info("Got chunkStore");
				} catch (Exception e) {
					this.chunkStore = null;
				} 
			}
			else {
				this.chunkStore = null;
				run();
				return;
			}
			
			if(chunkStore != null) {
				List<CuboidRegion> regionList = getSubRegions(region, subregionSize);
				if(regionList != null && !regionList.isEmpty()) {
					regions = regionList.iterator();
				}
			}
			
			List<File> schematics = new ArrayList<File>();
			List<File> definiteSchematics = control.getSchematics().getSchematics();
			List<File> randomSchematics = control.getSchematics().getRandomSchematics();
			
			for (File file : definiteSchematics) {
				schematics.add(file);
			}
			for (File file : randomSchematics) {
				schematics.add(file);
			}
			this.schematics = schematics.iterator();
			
			if(this.chunkStore != null || this.schematics.hasNext()) {
				taskId = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, this, delay, delay);
			}
			else {
				run();
			}
			
		}
	}
	
	public void cancel() {
		if(stop()) {
			onCancel();
		}
	}
	
	private boolean stop() {
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
		if(chunkStore != null) {
			if(regions == null || !regions.hasNext()) {
				chunkStore = null;
				run();
			}
			else {
				CuboidRegion region = regions.next();
				restoreRegionInstantly(chunkStore, region);
			}
		}
		else {
			if(schematics == null || !schematics.hasNext()) {
				stop();
				onFinish();
			}
			else {
				try {
					placeSchematic(schematics.next(), region.getWorld());
				} catch (Exception e) {
					
				}
			}
		}
	}
	
	protected abstract void onStart();
	
	protected abstract void onCancel();
	
	protected abstract void onFinish();
	
	
	public static void placeSchematic(File file, LocalWorld world) {
		MCEditSchematicFormat format;
		try {
			format = (MCEditSchematicFormat) MCEditSchematicFormat.getFormat(file);
			CuboidClipboard cc = format.load(file);
			cc.place(new EditSession(world, -1), cc.getOrigin(), true);
		} catch(Exception e) {
			
		}
	}

	/**
	 * Restore a region
	 * 
	 * @param chunkStore
	 *            The <code>ChunkStore</code>, usually retrieved from a
	 *            <code>Snapshot</code>
	 * @param region
	 *            The region to restore
	 */
	public static void restoreRegionInstantly(ChunkStore chunkStore,
			Region region) {
		SnapshotRestore restore = new SnapshotRestore(chunkStore, region);
		try {
			restore.restore(new EditSession(region.getWorld(), -1));
		} catch (MaxChangedBlocksException e) {
		}
	}

	/**
	 * Divide a region in sub-regions of the specified size.
	 * 
	 * @param region
	 *            The region to divide
	 * @param subRegionSize
	 *            The size of the sub-regions
	 * @return List of sub-regions
	 */
	public static List<CuboidRegion> getSubRegions(CuboidRegion region,
			int subRegionSize) {
		// create result list
		List<CuboidRegion> regions = new ArrayList<CuboidRegion>();

		// get min/max points
		Vector min = region.getMinimumPoint();
		Vector max = region.getMaximumPoint();

		// starting coordinates
		int x = min.getBlockX();
		int y = min.getBlockY();
		int z = min.getBlockZ();

		// loop over coordinates with big steps, to
		// create subregions
		for (int i = x; i < max.getBlockX(); i += subRegionSize) {
			for (int j = y; j < max.getBlockY(); j += subRegionSize) {
				for (int k = z; k < max.getBlockZ(); k += subRegionSize) {
					regions.add(new CuboidRegion(region.getWorld(), new Vector(
							i, j, k), new Vector(i + subRegionSize, j
									+ subRegionSize, k + subRegionSize)));
				}
			}
		}

		return regions;
	}
}