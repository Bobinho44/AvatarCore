package com.sanan.avatarcore.util.bending.ability.bendinglist;

import org.bukkit.Material;

public enum BendingOre {
	COAL(Material.COAL_ORE, Material.COAL), 
	IRON(Material.IRON_ORE, Material.IRON_INGOT),
	LAPIS(Material.LAPIS_ORE, Material.LAPIS_LAZULI),
	GOLD(Material.GOLD_ORE, Material.GOLD_INGOT),
	REDSTONE(Material.REDSTONE_ORE, Material.REDSTONE),
	DIAMOND(Material.DIAMOND_ORE, Material.DIAMOND),
	EMERALD(Material.EMERALD_ORE, Material.EMERALD),
	QUARTZ(Material.NETHER_QUARTZ_ORE, Material.QUARTZ),
	NETHER_GOLD(Material.NETHER_GOLD_ORE, Material.GOLD_INGOT),
	DEBRIS(Material.ANCIENT_DEBRIS, Material.NETHERITE_INGOT);
		
    private Material ore;
	private Material smelted;
	     
	private BendingOre(Material ore, Material smelted) {  
        this.ore = ore;  
        this.smelted = smelted;
	}  
	     
	public Material getOre() {  
        return ore;  
	}  
   
	public Material getSmelted() {
		return smelted;
	}
    
    public static boolean isBendingOre(Material material) {
    	for (BendingOre ore: BendingOre.values()) {
    		if (ore.getOre().equals(material)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public static Material getSmelted(Material material) {
    	for (BendingOre ore : BendingOre.values()) {
    		if (ore.getOre().equals(material)) {
    			return ore.getSmelted();
    		}
    	}
    	return null;
    }
}
