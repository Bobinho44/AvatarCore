package com.sanan.avatarcore.util.bending.ability.bendinglist;

import org.bukkit.Material;

public enum BendingFallingBlock {
	SAND(Material.SAND),
    RED_SAND(Material.RED_SAND),
    GRAVEL(Material.GRAVEL),
    BLACK_CONCRETE_POWDER(Material.BLACK_CONCRETE_POWDER),
    BLUE_CONCRETE_POWDER(Material.BLUE_CONCRETE_POWDER),
    BROWN_CONCRETE_POWDER(Material.BROWN_CONCRETE_POWDER),
    CYAN_CONCRETE_POWDER(Material.CYAN_CONCRETE_POWDER),
    GRAY_CONCRETE_POWDER(Material.GRAY_CONCRETE_POWDER),
    GREEN_CONCRETE_POWDER(Material.GREEN_CONCRETE_POWDER),
    LIGHT_BLUE_CONCRETE_POWDER(Material.LIGHT_BLUE_CONCRETE_POWDER),
    LIGHT_GRAY_CONCRETE_POWDER(Material.LIGHT_GRAY_CONCRETE_POWDER),
    LIME_CONCRETE_POWDER(Material.LIME_CONCRETE_POWDER),
    MAGENTA_CONCRETE_POWDER(Material.MAGENTA_CONCRETE_POWDER),
    ORANGE_CONCRETE_POWDER(Material.ORANGE_CONCRETE_POWDER),
	PINK_CONCRETE_POWDER(Material.PINK_CONCRETE_POWDER),
	RED_CONCRETE_POWDER(Material.RED_CONCRETE_POWDER),
	YELLOW_CONCRETE_POWDER(Material.YELLOW_CONCRETE_POWDER),
	WHITE_CONCRETE_POWDER(Material.WHITE_CONCRETE_POWDER);
    
	private Material material;  
     
	private BendingFallingBlock(Material material) {  
		this.material = material;  
	}  
     
    public Material getMaterial() {  
    	return material;  
    }  
    
    public static boolean isEarthBendingFallingBlock(Material material) {
    	for (BendingFallingBlock block : BendingFallingBlock.values()) {
    		if (block.getMaterial().equals(material)) {
    			return true;
    		}
    	}
    	return false;
    }
}
