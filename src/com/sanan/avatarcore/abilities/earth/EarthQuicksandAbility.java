package com.sanan.avatarcore.abilities.earth;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sanan.avatarcore.util.bending.ability.BendingAbilitiesDataManager;
import com.sanan.avatarcore.util.bending.ability.bendinglist.BendingBlock;
import com.sanan.avatarcore.util.bending.ability.earth.EarthBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class EarthQuicksandAbility extends EarthBendingAbility {

	private final BendingAbilitiesDataManager badm = BendingAbilitiesDataManager.getInstance();
	
	private long cooldown;
	private Map<Location, Material> blocks = new HashMap<Location, Material>();
	private Location curr;
	
	public EarthQuicksandAbility(BendingPlayer player, Entity victim) {
		super(player);
		this.cooldown = System.currentTimeMillis();
		this.curr = victim.getLocation().clone().add(-3, -1, -3);
		for (int x = 0; x < 7; x++) {
			for (int z = 0; z < 7; z++) {
				Location position = curr.clone().add(x, 0, z);
				while (!BendingBlock.isEarthBendingBlock(position.getBlock().getType())) {
					position.add(0, -1, 0);
				}
				Material initialBlock = badm.getInitialQuicksandInfectedZoneBlock(position.getBlock().getLocation());
				blocks.put(position.getBlock().getLocation(), initialBlock == null ? position.getBlock().getType() : initialBlock);
				position.getBlock().setType(Material.SOUL_SAND);
			}
		}
		badm.addQuicksandInfectedZone(blocks);
		this.start();
	}

	@Override
	public void update() {
		super.update();
		for (Player player : badm.getPlayersInQuicksandInfectedZone()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 3));	
		}
		
		if (System.currentTimeMillis() - cooldown >= 20000) {
			for (Entry<Location, Material> map : new HashMap<Location, Material>(blocks).entrySet()) {
				if (!badm.isInfectedZone(map.getKey(), blocks)) {
    				map.getKey().getBlock().setType(map.getValue());
				}
    		}
			badm.removeQuicksandInfectedZone(blocks);
			remove();
		}
	}
	
	@Override
	public String getName() {
		return "Quicksand";
	}

	@Override
	public String getDescription() {
		return "Create a cursed zone that will slow down your enemies";
	}

	@Override
	public Location getLocation() {
		return curr;
	}

	@Override
	public Material getMaterial() {
		return Material.SOUL_SAND;
	}

	@Override
	public boolean isToggleAbility() {
		return false;
	}

	@Override
	public boolean isDamageAbility() {
		return false;
	}

	@Override
	public String getDamageString() {
		return "";
	}

	@Override
	public int getChiCost() {
		return 30;
	}
	
	@Override
	public int getLevelRequired() {
		return 40;
	}

	@Override
	public int getToogleChiConsumptionTime() {
		return 0;
	}
	
}
