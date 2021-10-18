package com.sanan.avatarcore.util.player.tutorial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.util.bending.ability.hotbar.BendingHotbar;
import com.sanan.avatarcore.util.crate.CrateItemUtil;
import com.sanan.avatarcore.util.npc.NPC;
import com.sanan.avatarcore.util.npc.NPCManager;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;

import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;

public class BendingTutorial {

	private final NPCManager npcm = NPCManager.getInstance();
	private final PlayerManager pm = PlayerManager.getInstance();
	private final AvatarCore ac = AvatarCore.getInstance();
	
	private boolean skipped = false;
	private double step;
	private NPC trainer;
	private Player player;
	private List<ItemStack> inventory = new ArrayList<ItemStack>();
	private List<BendingHotbar> hotbars = new ArrayList<BendingHotbar>();
	
	public BendingTutorial(Player player, double step) {
		this.player = player;
		this.step = Math.round(step) - 0.5;
		savePlayerInfo();
		for (Player one : Bukkit.getServer().getOnlinePlayers()) {
			for (Player other : Bukkit.getServer().getOnlinePlayers()) {
				if (other != one && (!pm.getBendingPlayer(one).hasFinishTutorial() && !pm.getBendingPlayer(other).hasFinishTutorial())) {
					one.hidePlayer(ac, other);
				}
			}
        }
		if (step < 9) {
			BendingPlayer bPlayer = pm.getBendingPlayer(player);
			if (bPlayer.isInBendingMode()) {
				bPlayer.toggleBendingMode();
			}
			new BukkitRunnable() {
			    public void run() {
			      if (player.getWorld().getName().equalsIgnoreCase("tutorial")) {
			    	  for (Entity entity : new ArrayList<Entity>(Bukkit.getWorld("tutorial").getEntities())) {
			  			if (entity.getType() == EntityType.DROPPED_ITEM || entity.getType() == EntityType.ARMOR_STAND || entity.getType() == EntityType.FIREBALL) {
			  				((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(entity.getEntityId()));
			  			}
			  		}
						trainer = npcm.createNPC("Trainer");
						
						trainer.spawn(player, new Location(player.getWorld(), -50.5, 5, -272.0));
						Location location = new Location(player.getWorld(), -54.5, 4, -272.0);
						trainer.walk(player, location);
						new BukkitRunnable() {
							public void run() {
								if (trainer.getLocation().toVector().equals(location.toVector())) {
									BendingTutorial.this.step -= 0.5;
						    		nextStep();
						    		cancel();
								}
							}
						}.runTaskTimer(ac, 0, 1);
						cancel();
			      	}
			    }
			}.runTaskTimer(ac, 0, 1);
		}
	}
	
	public void skip() {
		skipped = true;
		step8();
	}
	
	public void nextStep() {
		step++;
		try {
			getClass().getMethod("step" + (int) step).invoke(this);
		} catch (Exception e) {}
	}
	
	public void previousStep() {
		step--;
		nextStep();
	}
	
	public double getStep() {
		return step;
	}
	
	public void step1() {
		step -= 0.5;
		new BukkitRunnable() {
			public void run() {
				if (skipped) return;
				player.sendMessage("§7[§6Bending Trainer§7] --> §eIf you wish to skip this tutorial you may use the command §c/skip §eat any time.");
				new BukkitRunnable() {
					public void run() {
						if (skipped) return;
						player.sendMessage("§7[§6Bending Trainer§7] --> §eOkay, let’s begin your training!");
						new BukkitRunnable() {
							public void run() {
								if (skipped) return;
								player.sendMessage("§7[§6Bending Trainer§7] --> §cFirst Lesson: §eCrouch §7(shift) §e3 times fast to enter into bending mode.");
								step += 0.5;
							}
						}.runTaskLaterAsynchronously(ac, 40);
					}
				}.runTaskLaterAsynchronously(ac, 40);
			}
		}.runTaskLaterAsynchronously(ac, 40);
	}
	
	public void step2() {
		player.sendMessage("§7[§6Bending Trainer§7] --> §eVery nice you have entered bending mode! Now, click on the slime ball titled §bManage Bending Hotbars. §eOnce you do that click on the eye of ender to create an empty hotbar!");
	}
	
	public void step3() {
		player.closeInventory();
		String bClass = PlayerManager.getInstance().getBendingPlayer(player).getBendingLevel().getBendingClass();
		player.sendMessage("§7[§6Bending Trainer§7] --> §eWell done! The next step is to equip your first ability! Right-click on the newly created bending hotbar labeled §6hotbar0 §e and click the anvil. Once you click the anvil click on a red barrier block labeled §dEmpty. §eAfter that click on the " + bClass + " emblem and equip your first ability!");
	}
	
	public void step4() {
		player.closeInventory();
		player.sendMessage("§7[§6Bending Trainer§7] --> §ePerfect! You are a pro at this! The last thing you need to do in order to bend is left-click on your newly created hotbar, this will equip it.");
	}
	
	public void step5() {
		if (!trainerIsDead()) {
			player.sendMessage("§7[§6Bending Trainer§7] --> §e Dang, you missed… Well go ahead, try again!");
		}
		else {
			step -= 0.5;
			player.sendMessage("§7[§6Bending Trainer§7] --> §eOkay! Now, it is time to use your new ability!");
			Entity fakeTrainer  = player.getWorld().spawnEntity(trainer.getLocation().clone().add(0, -3, 0), EntityType.VILLAGER);
			((LivingEntity) fakeTrainer).setAI(false);
			((LivingEntity) fakeTrainer).setCollidable(false);
			((LivingEntity) fakeTrainer).setHealth(0.5);
			((LivingEntity) fakeTrainer).setInvisible(true);
			fakeTrainer.teleport(fakeTrainer.getLocation().clone().add(0, 3, 0));
			fakeTrainer.setCustomName(player.getName());
			BendingPlayer bPlayer = pm.getBendingPlayer(player);
			switch (bPlayer.getBendingLevel().getBendingClass()) {
			case "earth":
				new BukkitRunnable() {
					public void run() {
						if (skipped) return;
						player.sendMessage("§7[§6Bending Trainer§7] --> §aEarth §eBenders can only use their abilities on blocks that come from the earth. While holding §aEarth Levitation §eright click on the stone at your feet. Then, look around to move the stone.");
						step += 0.5;
					}
				}.runTaskLaterAsynchronously(ac, 40);
				break;
			case "water":
				new BukkitRunnable() {
					public void run() {
						if (skipped) return;
						player.sendMessage("§7[§6Bending Trainer§7] --> §bWater §eBenders can, for the most part, only use abilities with water.  While holding §bWater Manipulation §eright click on the water to your right. Then, look around to move the water. Be carful not to bump your water into anything! This will cause it to disappear!");
						step += 0.5;
					}
				}.runTaskLaterAsynchronously(ac, 40);
				break;
			case "air":
				new BukkitRunnable() {
					public void run() {
						if (skipped) return;
						player.sendMessage("§7[§6Bending Trainer§7] --> §fAir §eBenders can manipulate the air around them to execute their abilities!");
						new BukkitRunnable() {
							public void run() {
								if (skipped) return;
								player.sendMessage("§7[§6Bending Trainer§7] --> §eAnyways, aim at me while holding §fAirblast §eand right-click to use the ability! Try and hit me I can take it!");
								step += 0.5;
							}
						}.runTaskLaterAsynchronously(ac, 40);
					}
				}.runTaskLaterAsynchronously(ac, 40);
				break;
			default:
				new BukkitRunnable() {
					public void run() {
						if (skipped) return;
						player.sendMessage("§7[§6Bending Trainer§7] --> §cFire §eBenders can use their energy to produce flames that fuel their abilities!");
						new BukkitRunnable() {
							public void run() {
								if (skipped) return;
								player.sendMessage("§7[§6Bending Trainer§7] --> §eAnyways, aim at me while holding §eFireball §eand right-click to use the ability! Try and hit me I can take it!");
								step += 0.5;
							}
						}.runTaskLaterAsynchronously(ac, 40);
					}
				}.runTaskLaterAsynchronously(ac, 40);
				break;
			}
		}
	}
	
	public void step6() {
		step -= 0.5;
		clearEntities();
		ItemStack shard = CrateItemUtil.getCrateShard("hero");
		shard.setAmount(1);
		pm.give(player, Arrays.asList(shard));
		trainer.kill(player);
		new BukkitRunnable() {
		    public void run() {
		    	trainer.setLocation(new Location(player.getWorld(), -50.5, 5, -272.0));
		    	trainer.spawn(player);
				Location location = new Location(player.getWorld(), -54.5, 4, -272.0);
				trainer.walk(player, location);
				new BukkitRunnable() {
				    public void run() {
				    	if (trainer.getLocation().toVector().equals(location.toVector())) {
				    		new BukkitRunnable() {
								public void run() {
									if (skipped) return;
									player.sendMessage("§7[§6Bending Trainer§7] --> §e Wow! You really pack a punch!");
									new BukkitRunnable() {
										public void run() {
											if (skipped) return;
											player.sendMessage("§7[§6Bending Trainer§7] --> §e Notice that you got some bending XP for killing me and leveled up!  You can gain XP by killing mobs, players, and mining blocks!");
											new BukkitRunnable() {
												public void run() {
													if (skipped) return;
													player.sendMessage("§7[§6Bending Trainer§7] --> §e You can unlock new abilities by leveling up! Your next ability will be unlocked at level 10! Also, once you reach level 100 you can go back to that portal room and choose an additional bending class! And once you master all the elements you will become the Avatar!");
													new BukkitRunnable() {
														public void run() {
															if (skipped) return;
															player.sendMessage("§7[§6Bending Trainer§7] --> §e Woah there! It looks like you dropped a §3shard §eon the ground!  Crouch 3 times fast to exit bending mode and pick up your shard! Every time you level up you get some shards which you can right-click on to open up §5mystery crates!");
															step += 0.5;
														}
													}.runTaskLaterAsynchronously(ac, 40);
												}
											}.runTaskLaterAsynchronously(ac, 40);
										}
									}.runTaskLaterAsynchronously(ac, 40);
								}
							}.runTaskLaterAsynchronously(ac, 40);
							cancel();
				    	}
				    }
				}.runTaskTimer(ac, 0, 1);
		    }
		}.runTaskLaterAsynchronously(AvatarCore.getInstance(), 60);
	}
	
	public void step7() {
		player.sendMessage("§7[§6Bending Trainer§7] --> §eDon’t be shy, hold the shard in your hand and right-click to open up the crate GUI!  Then, left-click on the shard in your inventory to equip it. Once the shard is equipped a slime ball will pop up which you can click to spin and win a prize!");
	}
	
	public void step8() {
		step -= 0.5;
		player.sendMessage("§7[§6Bending Trainer§7] --> §eWell, my job here is done! You can walk through the portal behind me to get to the spawn! Good luck on your journey " + player.getName() + "!");
		Location location = new Location(player.getWorld(), -50.5, 5, -272.0);
		trainer.walk(player, location);
		new BukkitRunnable() {
		    public void run() {
		    	if (trainer.getLocation().toVector().equals(location.toVector())) {
		    		step += 0.5;
		    		trainer.despawn(player);
		    		cancel();
		    	}
		    }
		}.runTaskTimer(ac, 0, 1);
	}
	
	public void step9() {
		step = 10;
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		bPlayer.finishTutorial();
		if (bPlayer.isInBendingMode()) {
			bPlayer.toggleBendingMode();
		}
		restorePlayerInfo();
		for (Player other : Bukkit.getServer().getOnlinePlayers()) {
			if (other != player) {
				player.showPlayer(ac, other);
			}
        }
	}
	
	private boolean trainerIsDead() {
		for (Entity entity : trainer.getLocation().getNearbyLivingEntities(2)) {
			if (entity.getType() == EntityType.VILLAGER && entity.getCustomName().equalsIgnoreCase(player.getName())) {
				return false;
			}
		}
		return true;
	}
	
	/*private void sendMessages(List<String> messages) {
		List<String> tosend = new ArrayList<String>(messages);
		int size = tosend.size();
		for (int i = 0; i < size; i++) {
			new BukkitRunnable() {
				public void run() {
					player.sendMessage(tosend.get(0));
					tosend.remove(0);
					if (tosend.size() == 0) {
						step += 0.5;
					}
				}
			}.runTaskLaterAsynchronously(ac, 40);
		}
	}*/
	
	public boolean isSkipped() {
		return skipped;
	}
	
	public void setStep(double step) {
		this.step = step;
	}
	
	public static void clearEntity(Player player, int id) {
		for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
			if (!player.equals(otherPlayer)) {
				((CraftPlayer) otherPlayer).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(id));
			}
		}
	}
	
	private void clearEntities() {
		for (Entity entity : new ArrayList<Entity>(Bukkit.getWorld("tutorial").getEntities())) {
			if ((entity.getType() == EntityType.VILLAGER || entity.getType() == EntityType.DROPPED_ITEM) && entity.getCustomName() != null && entity.getCustomName().equalsIgnoreCase(player.getName())) {
				entity.remove();
			}
		}
	}
	
	private void savePlayerInfo() {
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		for (int i = 0; i < 36; i++) {
			ItemStack item = player.getInventory().getItem(i);
			inventory.add(item == null ? new ItemStack(Material.AIR) : item);
		}
		hotbars = bPlayer.getHotbars();
		player.getInventory().clear();
		bPlayer.clearBendingHotbars();
	}
	
	public void restorePlayerInfo() {
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		player.getInventory().clear();
		bPlayer.clearBendingHotbars();
		for (int i = 0; i < 36; i++) {
			player.getInventory().setItem(i, inventory.get(i));
		}
		for (BendingHotbar hotbar : hotbars) {
			bPlayer.addBendingHotbar(hotbar);
		}
	}
	
}
