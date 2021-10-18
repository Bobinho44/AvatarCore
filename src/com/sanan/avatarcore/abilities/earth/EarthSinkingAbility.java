package com.sanan.avatarcore.abilities.earth;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;

import com.sanan.avatarcore.util.bending.ability.BendingAbilitiesDataManager;
import com.sanan.avatarcore.util.bending.ability.EnduringAbility;
import com.sanan.avatarcore.util.bending.ability.earth.EarthBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class EarthSinkingAbility extends EarthBendingAbility implements EnduringAbility {

	private final BendingAbilitiesDataManager badm = BendingAbilitiesDataManager.getInstance();
	
	private Entity victim;
	private long cooldown;
	private BlockData blockAbove;
	
	public EarthSinkingAbility(BendingPlayer player, Entity victim) {
		super(player);
		Location loc = victim.getLocation().clone().add(0, -2, 0);
		if (!loc.getBlock().isSolid()) {
			blockAbove = loc.getBlock().getBlockData();
			loc.getBlock().setType(Material.BARRIER);
		}
		this.victim = victim;
		this.cooldown = System.currentTimeMillis();
		this.victim.teleport(victim.getLocation().clone().add(0, -0.8, 0));
		badm.addSinkingPlayer(victim);
		this.start();
	}

	@Override
	public void update() {
		super.update();
		if (System.currentTimeMillis() - cooldown >= 3000) {
			remove();
		}
	}

	@Override
	public void remove() {
		super.remove();
		victim.teleport(victim.getLocation().clone().add(0, 1, 0));
		badm.removeSinkingPlayer(victim);
		Location loc = victim.getLocation().clone().add(0, -2, 0);
		if (loc.getBlock().getType() == Material.BARRIER) {
			loc.getBlock().setBlockData(blockAbove);
		}
	}
	
	@Override
	public String getName() {
		return "Earth Sinking";
	}

	@Override
	public String getDescription() {
		return "Make an offering to the earth by offering an opponent to be sucked into its entrails";
	}

	@Override
	public Location getLocation() {
		return victim.getLocation();
	}

	@Override
	public Material getMaterial() {
		return Material.HOPPER;
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
		return 25;
	}

	@Override
	public int getLevelRequired() {
		return 70;
	}

	@Override
	public int getToogleChiConsumptionTime() {
		return 0;
	}
	
}
