package com.sanan.avatarcore.util.bending.ability.bendinglist;

import org.bukkit.Material;

public enum BendingFood {
	CHICKEN(Material.CHICKEN, Material.COOKED_CHICKEN), 
	BEEF(Material.BEEF, Material.COOKED_BEEF),
	COD(Material.COD, Material.COOKED_COD),
	MUTTON(Material.MUTTON, Material.COOKED_MUTTON),
	PORKCHOP(Material.PORKCHOP, Material.COOKED_PORKCHOP),
	RABBIT(Material.RABBIT, Material.COOKED_RABBIT),
	SALMON(Material.SALMON, Material.COOKED_SALMON),
	POTATO(Material.POTATO, Material.BAKED_POTATO);
		
    private Material raw;
	private Material cooked;
	     
	private BendingFood(Material raw, Material cooked) {  
        this.raw = raw;  
        this.cooked = cooked;
	}  
	     
	public Material getRaw() {  
        return raw;  
	}  
   
	public Material getCooked() {
		return cooked;
	}
    
    public static boolean isBendingFood(Material raw) {
    	for (BendingFood food: BendingFood.values()) {
    		if (food.getRaw().equals(raw)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public static Material getCooked(Material raw) {
    	for (BendingFood food : BendingFood.values()) {
    		if (food.getRaw().equals(raw)) {
    			return food.getCooked();
    		}
    	}
    	return null;
    }
}
