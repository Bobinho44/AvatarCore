package com.sanan.avatarcore.util.bending.ability.fire;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import com.sanan.avatarcore.util.bending.BendingAbilityManager;
import com.sanan.avatarcore.util.bending.BendingElement;
import com.sanan.avatarcore.util.bending.ability.CoreBendingAbility;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;

public abstract class FireBendingAbility extends CoreBendingAbility {

	private final static PlayerManager pm = PlayerManager.getInstance();
	private final static BendingAbilityManager bam = BendingAbilityManager.getInstance();
	
	public FireBendingAbility(BendingPlayer player) {
		super(player);
	}
	
	@Override
	public BendingElement getElement() {
		return BendingElement.FIRE;
	}
	
	public void sendFireParticle(Location position, int count, double spreadX, double spreadY, double spreadZ, double speed) {
		for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
			BendingPlayer bPlayer = pm.getBendingPlayer(otherPlayer);
			Particle particle = bam.isPlayerUsingAbility(bPlayer, "Blue Fire") ? Particle.SOUL_FIRE_FLAME : Particle.FLAME;
			if (!player.hasFinishTutorial()) {
				player.getSpigotPlayer().spawnParticle(particle, position, count, spreadX, spreadY, spreadZ, speed);
			}
			else {
				otherPlayer.spawnParticle(particle, position, count, spreadX, spreadY, spreadZ, speed);
			}
		}
	}
	
}
