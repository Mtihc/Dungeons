package com.mtihc.minecraft.dungeons.tasks;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SchematicRepository {

	private File directory;
	
	private FilenameFilter folderFilter;
	private FilenameFilter schematicFilter;

	public SchematicRepository(String directory) {
		this(new File(directory));
	}
	
	public SchematicRepository(File directory) {
		this.directory = directory;
		this.directory.mkdirs();
		
		this.folderFilter = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return !name.endsWith(".schematic");
			}
		};
		this.schematicFilter = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".schematic");
			}
		};
	}
	
	public File getDirectory() {
		return directory;
	}
	
	public String[] getSubdirectories() {
		return directory.list(folderFilter);
	}
	
	public String[] getSchematicNames(String folder) {
		return getSchematicNames(new File(directory, folder));
	}
	
	public String[] getSchematicNames() {
		return getSchematicNames(directory);
	}
	
	private String[] getSchematicNames(File folder) {
		return folder.list(schematicFilter);
	}
	
	public File getSchematic(String name) {
		String realName = getRealName(name);
		return new File(directory, realName);
	}
	
	public File getSchematic(String folder, String name) {
		String realName = getRealName(name);
		return new File(directory, folder + "/" + realName);
	}
	
	private String getRealName(String name) {
		String realName = name;
		if(!realName.endsWith(".schematic")) {
			realName += ".schematic";
		}
		return realName;
	}
	
	public List<File> getRandomSchematics() {
		List<File> files = new ArrayList<File>();
		String[] folders = getSubdirectories();
		if(folders == null) {
			return files;
		}
		for (String folder : folders) {
			File f = new File(directory, folder);
			String[] names = f.list(schematicFilter);
			Random r = new Random();
			int index = r.nextInt(names.length);
			files.add(new File(f, names[index]));
		}
		return files;
	}
	
	public List<File> getSchematics() {
		List<File> files = new ArrayList<File>();
		File[] names = directory.listFiles(schematicFilter);
		if(names == null) {
			return files;
		}
		for (File file : names) {
			files.add(file);
		}
		return files;
	}
}
