package com.sanan.avatarcore.util.bending.ability;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.sanan.avatarcore.abilities.fire.ShieldOfFireAbility;
import com.sanan.avatarcore.abilities.water.ShieldOfWaterAbility;
import com.sanan.avatarcore.util.bending.BendingAbilityManager;
import com.sanan.avatarcore.util.bending.ability.water.WaterBendingAbility;
import com.sanan.avatarcore.util.bendingwall.BendingWall;
import com.sanan.avatarcore.util.bendingwall.WallManager;
import com.sanan.avatarcore.util.item.ItemBuilder;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;

import net.md_5.bungee.api.ChatColor;

public abstract class CoreBendingAbility implements BendingAbility {

	private final BendingAbilityManager bam = BendingAbilityManager.getInstance();
	private final PlayerManager pm = PlayerManager.getInstance();
	private final WallManager wm = WallManager.getInstance();
	
	protected BendingPlayer player;
	
	private boolean started;
	private long startTime;
	private boolean stopped;
	private long cooldown;
	
	public CoreBendingAbility() {}
	
	public CoreBendingAbility(final BendingPlayer player) {
		if (player == null) return;
		
		player.getChi().use(this.getChiCost());
		
		this.player = player;
		this.started = false;
		this.stopped = false;
	}
	
	public long getCooldown() {
		return cooldown;
	}
	
	public long getStartTime() {
		return startTime;
	}
	public boolean isStarted() {
		return started;
	}
	public boolean isStopped() {
		return stopped;
	}
	
	@Override
	public Player getPlayer() {
		return player.getSpigotPlayer();
	}
	
	@Override
	public ItemStack getItem() {
		ItemBuilder builder = new ItemBuilder(this.getMaterial()).setName(this.getElement().getColor() + this.getName()).setLore(ChatColor.GRAY + "  - " + this.getDescription());
		if (this.isDamageAbility())
			builder.addLore(" ", ChatColor.GOLD + "Damage: " + ChatColor.RED + ChatColor.BOLD + this.getDamageString());
		
		if (!(this instanceof PassiveAbility))
		builder.addLore(ChatColor.GOLD + "Chi: " + ChatColor.RED + ChatColor.BOLD + this.getChiCost());
		
		builder.addLore(ChatColor.GOLD + "Level Required: " + ChatColor.RED + ChatColor.BOLD + this.getLevelRequired());
		
		return builder.build();
	}
	
	public void bendingDamage(LivingEntity victim, double damage, int knockback) {
		if (victim instanceof Player && bam.isPlayerUsingAbility(pm.getBendingPlayer((Player) victim), "Shield Of Fire")) {
			((ShieldOfFireAbility) bam.getPlayerAbility(pm.getBendingPlayer((Player) victim), "Shield Of Fire")).damage(this instanceof WaterBendingAbility ? 3 : 1);
		}
		else {
			victim.damage(damage);
			if (knockback > 0) {
				victim.setVelocity(victim.getLocation().getDirection().multiply(-knockback));
			}
			if (victim.isDead()) {
				EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(getPlayer(), victim, DamageCause.CUSTOM,  damage);
				victim.setLastDamageCause(event);
				EntityDeathEvent eventDeath = new EntityDeathEvent(victim, Arrays.asList(new ItemStack(Material.AIR)));
				victim.setKiller(getPlayer());
				Bukkit.getServer().getPluginManager().callEvent(eventDeath);
			}
		}
	}
	@Override
	public void start() {
		if (this.player == null) return;
		this.started = true;
		this.startTime = System.currentTimeMillis();
		bam.registerAbility(this);
		this.cooldown = System.currentTimeMillis();
	}
	
	public void stop() {
		if (this.player == null) return;
		this.stopped = true;
	}
	
	@Override
	public void remove() {
		if (this.player == null) return;
		this.stopped = true;
		bam.unregisterAbility(this);
		if (!player.hasFinishTutorial() && player.getTutorialStep() == 5) {
			player.previoustepTutorial();
		}
	}
	
	@Override
	public void update() {
		if (isToggleAbility()) {
			if (Math.round((System.currentTimeMillis() - cooldown)/50.0) * 50 >= getToogleChiConsumptionTime() && getToogleChiConsumptionTime() != 0) {
				cooldown = System.currentTimeMillis();
				if (player.getChi().getCurrent() <= 0) {
					remove();
				} else
					player.getChi().use(1);
			}
		}
		if (isDamageAbility()) {
			BendingWall otherWall = wm.isFromWall(getLocation().getBlock().getLocation());
	    	if (otherWall != null && !(this instanceof ShieldOfWaterAbility && ((ShieldOfWaterAbility) this).getWall().equals(otherWall))) {
	    		if (!isToggleAbility()) {
	    			remove();
	    		}
	    		otherWall.damageWall(Float.parseFloat(getDamageString().replaceAll("[^0-9]", "")));
	    	}
		}
	}
		
}
