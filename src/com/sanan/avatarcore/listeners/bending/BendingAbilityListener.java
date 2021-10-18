package com.sanan.avatarcore.listeners.bending;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.abilities.earth.EarthLevitationAbility;
import com.sanan.avatarcore.abilities.earth.EarthWallAbility;
import com.sanan.avatarcore.abilities.fire.ShieldOfFireAbility;
import com.sanan.avatarcore.abilities.water.ShieldOfWaterAbility;
import com.sanan.avatarcore.abilities.water.WaterManipulationAbility;
import com.sanan.avatarcore.util.bending.BendingAbilityManager;
import com.sanan.avatarcore.util.bending.ability.BendingAbilitiesDataManager;
import com.sanan.avatarcore.util.bending.ability.CoreBendingAbility;
import com.sanan.avatarcore.util.bending.ability.bendinglist.BendingFood;
import com.sanan.avatarcore.util.bending.ability.bendinglist.BendingOre;
import com.sanan.avatarcore.util.bending.ability.fire.FireBendingAbility;
import com.sanan.avatarcore.util.nation.tribe.BendingTribe;
import com.sanan.avatarcore.util.nation.tribe.Claim;
import com.sanan.avatarcore.util.nation.tribe.ClaimManager;
import com.sanan.avatarcore.util.nation.tribe.TribeManager;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;

import io.papermc.paper.event.entity.EntityMoveEvent;

public class BendingAbilityListener implements Listener {
	
	private final PlayerManager pm = PlayerManager.getInstance();
	private final BendingAbilityManager bam = BendingAbilityManager.getInstance();
	private final BendingAbilitiesDataManager badm = BendingAbilitiesDataManager.getInstance();
	private final AvatarCore ac = AvatarCore.getInstance();
	private final TribeManager tm = TribeManager.getInstance();
	private final ClaimManager clm = ClaimManager.getInstance();
	
	/*
	 * Fire Jet
	 */
	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		if (bam.isPlayerUsingAbility(bPlayer, "Fire Jet")) {
			event.setCancelled(true);
			player.setAllowFlight(true);
			player.setFlying(true);
			player.setFlySpeed(0.2f);
		}
	}
	
	/*@EventHandler
	public void enchant(PrepareItemEnchantEvent event) {
		Player player = event.getEnchanter();
		int max = 0;
		for (EnchantmentOffer offer : event.getOffers()) {
			max = offer.getCost() > max ? offer.getCost() : max;
		}
		if (player.getLevel() < max) {
			player.setLevel(max);
		}
	}*/
	
	@EventHandler
	public void onFireBallExplode(EntityExplodeEvent event) {
		if (event .getEntity().getCustomName() != null && event.getEntity().getCustomName().equals("FireBallAbility")) {
			Player shooter = (Player) ((Projectile) event.getEntity()).getShooter();
			BendingPlayer bShooter = pm.getBendingPlayer(shooter);
			if (!bShooter.hasFinishTutorial()) {
				event.setCancelled(true);
			}
			for (LivingEntity entity : event.getLocation().getNearbyLivingEntities(2.5)) {
				if (entity instanceof Player) {
					BendingPlayer bPlayer = pm.getBendingPlayer((Player) entity);
					if (bam.isPlayerUsingAbility(bPlayer, "Shield Of Fire")) {
						((ShieldOfFireAbility) bam.getPlayerAbility(bPlayer, "Shield Of Fire")).damage(1);
						continue;
					}
				}
				if (entity.equals(shooter) || (!bShooter.hasFinishTutorial() && (entity.getCustomName() == null || !entity.getCustomName().equals(bShooter.getSpigotPlayer().getName())))) {
					continue;
				}
				((CoreBendingAbility) bam.getPlayerAbility(bShooter, "Fireball")).bendingDamage(entity, 5, 0);
			}
		}
	}
	
	/*
	 * Air Cushion
	 */
	@EventHandler
	public void onPlayerFall(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			BendingPlayer bPlayer = pm.getBendingPlayer(player);
			if (bam.isPlayerUsingAbility(bPlayer, "Air Cushion") && event.getCause() == DamageCause.FALL) {
				event.setCancelled(true);
			}
		}
	}
	
	/*
	 * Air Punch
	 */
	@EventHandler
	public void onPlayerPunch(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) return;
		Player player = (Player) event.getDamager();
		if (player.getInventory().getItemInMainHand().getType() != Material.AIR) return;
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		if (bam.isPlayerUsingAbility(bPlayer, "Air Punch")) 
			event.getEntity().setVelocity(event.getEntity().getLocation().getDirection().multiply(-4));
	}
	
	/*
	 * Earth Levitation
	 */
	@EventHandler
	public void onBendingAbilityUse(PlayerInteractEvent event) {
		if (event.getHand() == null || event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
		Player player = event.getPlayer();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		if (!bPlayer.isInBendingMode()) return;
		
		if (bam.isPlayerUsingAbility(bPlayer, "Earth Levitation")) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				EarthLevitationAbility earthlev = (EarthLevitationAbility) bam.getPlayerAbility(bPlayer, "Earth Levitation");
				earthlev.shoot();	
			}
		}
		else if (bam.isPlayerUsingAbility(bPlayer, "Water Manipulation")) {
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				WaterManipulationAbility waterman = (WaterManipulationAbility) bam.getPlayerAbility(bPlayer, "Water Manipulation");
				waterman.shoot();	
			}
		}
		else if (bam.isPlayerUsingAbility(bPlayer, "Shield Of Water")) {
			ShieldOfWaterAbility watershield = (ShieldOfWaterAbility) bam.getPlayerAbility(bPlayer, "Shield Of Water");
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) 
				watershield.shoot();
			else if (watershield.getActualTask() != 0 && !watershield.isSolid())
				watershield.solidification();
		}
	}
	
	/*
	 * Enhanced Agility
	 */
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
	    BendingPlayer bPlayer = pm.getBendingPlayer(player);
		if (bam.isPlayerUsingAbility(bPlayer, "Enhanced Agility")) {
			Location loc = player.getLocation();
			if (player.getGameMode() != GameMode.CREATIVE && loc.getY() < 252 && loc.add(0, -1, 0).getBlock().getType() != Material.AIR && !player.isFlying()) {
				player.setAllowFlight(true);
			}
		}
	}
	 
	@EventHandler
	public void onPlayerFly(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
	    BendingPlayer bPlayer = pm.getBendingPlayer(player);
		if (bam.isPlayerUsingAbility(bPlayer, "Enhanced Agility")) {
			if (player.getGameMode() == GameMode.CREATIVE) return; 
		    	event.setCancelled(true);
		    	player.setAllowFlight(false);
		        player.setFlying(false);
		        player.setVelocity(player.getLocation().getDirection().multiply(1.5).setY(1));       
			}
	    }
	    
	/*
	 * Earth wall
	 */
	@EventHandler
	public void onBendingEarthWallUse(PlayerInteractEvent event) {
		if (event.getHand() == null || event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
		Player player = event.getPlayer();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		if (bam.isPlayerUsingAbility(bPlayer, "Earth Wall") && bPlayer.isInBendingMode()) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				event.setCancelled(true);
				EarthWallAbility earthwall = (EarthWallAbility) bam.getPlayerAbility(bPlayer, "Earth Wall");
				if (!earthwall.getName().equalsIgnoreCase(ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName()))) return;
				if (earthwall.getWallNumber() < 2 && earthwall.isStarted()) {
					earthwall.addNewWall(event.getClickedBlock().getLocation());	
				}
				else if (earthwall.getWallNumber() >= 2) {
					player.sendMessage("You already have two active walls!");
				}
			}
		}
		else if (bam.isPlayerUsingAbility(bPlayer, "Fish") && !bPlayer.isInBendingMode() && event.getItem() == null) {
			if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
				List<Block> los = player.getLineOfSight(null, 5);
				for (Block block : los) {
					if (block.getType() == Material.WATER) {
						if (badm.containsFishingPlayer(player)) {
							player.sendMessage("§bYou have caught a fish!  (Use this ability again in 10 seconds)");
							return;
						}
						badm.addFishingPlayer(player);
					    new BukkitRunnable() {
					        public void run() {
					        	badm.removeFishingPlayer(player);
					        }
					    }.runTaskLaterAsynchronously(ac, 200);
						List<Material> fishs = new ArrayList<Material>(Arrays.asList(Material.COD, Material.SALMON, Material.PUFFERFISH, Material.TROPICAL_FISH));
						player.getWorld().dropItemNaturally(block.getLocation().clone().add(0, 1, 0), new ItemStack(fishs.get(new Random().nextInt(4))));
						return;
					}
				}
			}
		} else if (bam.isPlayerUsingAbility(bPlayer, "Flame Redirection") && player.getFireTicks() != 0) {
			if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
				badm.reverseFireAbility(player.getLocation());
			}
		}
	}
	
	/*@EventHandler
    public void onChunkLoaded(ChunkLoadEvent event) {
		if (Bukkit.getOnlinePlayers().size() > 0) return;
		Chunk chunk = event.getChunk();
		for(int x = 0; x < 16; x++) {
			for(int y = 0; y < 256; y++) {
				for(int z = 0; z < 16; z++) {
					Block block = chunk.getBlock(x, y, z);
					if (block.getType() == Material.FIRE || block.getType() == Material.CAMPFIRE)
						badm.addBlueFireBlocks(block);
				}
			}
		}
	}
	
	@EventHandler
    public void onFireInteract(PlayerInteractEvent event) {
		event.getPlayer().sendMessage("a");
		if (event.getClickedBlock() == null) return;
		event.getPlayer().sendMessage("b");
		Block block = event.getClickedBlock();
		if (block.getType() != Material.FIRE && block.getType() != Material.CAMPFIRE) return;
		event.getPlayer().sendMessage("c");
		badm.showBlueClue();
	}
	
	@EventHandler
    public void onFirePlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		if (block.getType() != Material.FIRE && block.getType() != Material.CAMPFIRE) return;
		badm.addBlueFireBlocks(block);
		badm.showBlueClue();
	}
	
	@EventHandler
    public void onFireBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (block.getType() != Material.FIRE && block.getType() != Material.CAMPFIRE) return;
		badm.removeBlueFireBlocks(block);
	}
	
	@EventHandler
    public void onFireFade(BlockFadeEvent event) {
		Block block = event.getBlock();
		if (block.getType() != Material.FIRE && block.getType() != Material.CAMPFIRE) return;
		badm.removeBlueFireBlocks(block);
	}*/
	
	@EventHandler
    public void onSinkingPlayerMove(EntityMoveEvent event) {
        if (badm.containsSinkingPlayer(event.getEntity())) {
        	Location to = event.getTo().getBlock().getLocation();
        	Location from = event.getFrom().getBlock().getLocation();
            if (to.getX() != from.getX() || to.getY() != from.getY() || to.getZ() != from.getZ()) {
                event.setCancelled(true);
            }
        }
	}
	
	@EventHandler
	public void onPlayerCharge(PlayerToggleSneakEvent event) {
		if (!event.isSneaking()) return;
		Player player = event.getPlayer();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		if (!bam.isPlayerUsingAbility(bPlayer, "Charged Attacks") || !bPlayer.isInBendingMode()) return;
		new BukkitRunnable() {
			float damage = 0;
			Long cooldown = System.currentTimeMillis();
		    public void run() {
		    	if (!player.isSneaking() || !player.getLocation().clone().add(0, -1, 0).getBlock().getType().isSolid()) {
		    		cancel();
		    	}
		    	else if (damage >= 2) 
		    		((FireBendingAbility) bam.getPlayerAbility(bPlayer, "Charged Attacks")).sendFireParticle(player.getLocation(), 20, 0.2, 0.2, 0.2, 0.05);
		    	else {
			    	if (System.currentTimeMillis() - cooldown >= 1000) {
			    		cooldown = System.currentTimeMillis();
			    		damage += 0.5;
			    		player.sendMessage("§cAbilities charge: " + damage + "/2.");
			    	}
			    	player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(),
							20, 0.2, 0.2, 0.2, 0.05);
		    	}
		    }
		}.runTaskTimerAsynchronously(ac, 0L, 1L);
	}
	
	@EventHandler
	public void onPlayerCook(PlayerToggleSneakEvent event) {
		if (!event.isSneaking()) return;
		Player player = event.getPlayer();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		ItemStack item = player.getInventory().getItemInMainHand();
		if (!bam.isPlayerUsingAbility(bPlayer, "Cook") || bPlayer.isInBendingMode() || item == null) return;
		new BukkitRunnable() {
			Long cooldown = System.currentTimeMillis();
		    public void run() {
		    	String type = BendingOre.isBendingOre(item.getType()) ? "ore" : BendingFood.isBendingFood(item.getType()) ? "food" : "nothing";
		    	if (!player.isSneaking() || type.equals("nothing") || !player.getInventory().getItemInMainHand().equals(item)) {
		    		cancel();
		    	}
		    	else if (type.equals("ore") && System.currentTimeMillis() - cooldown >= 3500) {
		    		ItemStack smeltedOre = new ItemStack(BendingOre.getSmelted(item.getType()), item.getAmount());
		    		player.getInventory().setItemInMainHand(smeltedOre);
		    		cancel();
		    	}
		    	else if (type.equals("food") && System.currentTimeMillis() - cooldown >= 2000) {
		    		ItemStack cookedFood = new ItemStack(BendingFood.getCooked(item.getType()), item.getAmount());
		    		player.getInventory().setItemInMainHand(cookedFood);
		    		cancel();
		    	}
		    	player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(),
						20, 0.2, 0.2, 0.2, 0.05);
		    }
		}.runTaskTimerAsynchronously(ac, 0L, 1L);
	}
	
	@EventHandler
	public void onPickUp(EntityPickupItemEvent event) {
		if (event.getEntity() instanceof Player) {
			if (!badm.canPickUpItems(event.getItem(), (Player) event.getEntity())) {
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onPlaceFire(PlayerInteractEvent event) {
		if (event.getHand() == null || event.getHand().equals(EquipmentSlot.OFF_HAND) || event.getItem() != null || event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.getClickedBlock().isSolid()) return;
		Player player = event.getPlayer();;
        BendingPlayer bPlayer = pm.getBendingPlayer(player);
        if (!bam.isPlayerUsingAbility(bPlayer, "Ignite")) return;
        Location location = event.getClickedBlock().getLocation();
		BendingTribe tribe = tm.getTribe(bPlayer);
		Claim claim = new Claim(location.getChunk().getX(), location.getChunk().getZ(), player.getWorld());
        BendingTribe claimOwner = clm.getClaims().get(claim);
        if (claimOwner == null || claimOwner.equals(tribe)) {
        	Block block = location.getBlock().getRelative(event.getBlockFace());
        	if (block.getLocation().clone().add(0, -1, 0).getBlock().isSolid() && block.getType() == Material.AIR) {
        		block.setType(Material.FIRE);
        	}
        } 
        else {
        	player.sendMessage("§CYou cannot use the Ignite ability in a claim area!");
        }
	}
}
