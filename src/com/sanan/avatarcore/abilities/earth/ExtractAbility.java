package com.sanan.avatarcore.abilities.earth;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.sanan.avatarcore.util.bending.ability.bendinglist.BendingOre;
import com.sanan.avatarcore.util.bending.ability.earth.EarthBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;

public class ExtractAbility extends EarthBendingAbility{
	
	private Location curr;
	
	public ExtractAbility(BendingPlayer player, Location location) {
		super(player);
		this.curr = location;
		for (BendingOre block : BendingOre.values()) {
			if (location.getBlock().getType().equals(block.getOre())) {
				getPlayer().getWorld().dropItem(getPlayer().getLocation(), new ItemStack(block.getSmelted()));
				location.getBlock().setType(block.equals(BendingOre.NETHER_GOLD) || block.equals(BendingOre.QUARTZ) ? Material.NETHERRACK: Material.STONE);
			}
		}
		this.start();
	}

	@Override
	public void update() {
		super.update();
		remove();
	}
	
	@Override
	public String getName() {
		return "Extract";
	}

	@Override
	public String getDescription() {
		return "Extract resources from the earth.";
	}

	@Override
	public Location getLocation() {
		return curr;
	}

	@Override
	public Material getMaterial() {
		return Material.GOLDEN_PICKAXE;
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
		return 0;
	}
	
	@Override
	public int getLevelRequired() {
		return 20;
	}

	@Override
	public int getToogleChiConsumptionTime() {
		return 0;
	}
	
}
