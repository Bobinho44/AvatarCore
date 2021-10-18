package com.sanan.avatarcore.util.npc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sanan.avatarcore.util.data.ConfigManager;

public class NPCManager {

	private static NPCManager instance;
	private static ConfigManager cm = ConfigManager.getInstance();
	
	private List<NPC> NPC_INSTANCE = new ArrayList<>();
	public static List<Location> NPC_LOCATION = new ArrayList<>();
	
	public static NPCManager getInstance() {
		if (instance == null) {
			instance = new NPCManager();
		}
		return instance;
	}
	
	public List<NPC> getAllNPC() {
		return NPC_INSTANCE;
	}
	
	public NPC getNPC(int id) {
		for (NPC npc : getAllNPC()) {
			if (npc.getId() == id) {
				return npc;
              }
          }
		return null;
	}
	
	public NPC getNPC(Location location) {
		for (NPC npc : getAllNPC()) {
			if (npc.getLocation().equals(location)) {
				return npc;
              }
          }
		return null;
	}
	
	public boolean isNPC(int id) {
		for (NPC npc : getAllNPC()) {
			if (npc.getId() == id) {
				return true;
              }
          }
		return false;
	}
	
	public void spawnAllNPC(Player player) {
		for (NPC npc : getAllNPC()) {
			npc.spawn(player);
		}
	}

	public NPC createNPC(String npcData) {
		final ConfigurationSection npcDataSection = cm.getNPCData().getConfigurationSection(npcData);
		npcDataSection.getStringList("elements");
		UUID uuid = UUID.fromString(npcDataSection.getString("uuid"));
		Location location = ConfigManager.getInstance().stringToLocation((npcDataSection.getString("location"))); 
		String name = npcDataSection.getString("name");
		List<String> texture = new ArrayList<String>(Arrays.asList(npcDataSection.getString("textureData"), npcDataSection.getString("textureSignature")));
		NPC npc = new NPC(uuid, location, name, texture);
		NPC_INSTANCE.add(npc);
		NPC_LOCATION.add(location);
		return npc;
	}
	
	public void registerAllNPC() {
		FileConfiguration npcData = cm.getNPCData();
		for (Object key : new ArrayList<>(npcData.getKeys(false))) {
			createNPC((String) key);
		}
	}
}
