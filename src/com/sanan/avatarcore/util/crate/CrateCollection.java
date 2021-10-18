package com.sanan.avatarcore.util.crate;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.inventory.ItemStack;

public class CrateCollection {
    
	private Map<ItemStack, Double> map = new HashMap<ItemStack, Double>();
    private Random random = new Random();

    public Map<ItemStack, Double> getCollection() {
    	return map;
    }
    
    public void setNewCollection(Map<ItemStack, Double> collection) {
    	map = collection;
    }
    
    public ItemStack next(int round) {
    	int totalWeight = 0;
    	for (Double weight : map.values()) {
    		totalWeight += f(round, weight);
    	}
        int value = random.nextInt(totalWeight);
        int total = 0;
        for (Entry<ItemStack, Double> map : map.entrySet()) {
        	total += f(round, map.getValue());
        	if (value <= total) {
        		return map.getKey();
        	}
        }
        return map.keySet().iterator().next();
    }
    
    private Double f(int round, Double x) {
    	if (round == 1) {
    		return x;
    	}
    	if (round == 2) {
    		return (Double) (Math.pow(1.42185*x, 0.887211) + 1.96312);
    	}
    	else {
    		return (Double) (Math.pow(2.30461*x, 0.748453) + 2.31458);
    	}
    }
    
}
