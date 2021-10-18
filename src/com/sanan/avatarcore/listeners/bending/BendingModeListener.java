package com.sanan.avatarcore.listeners.bending;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import org.jetbrains.annotations.NotNull;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.commands.WarpNotFoundException;

import com.sanan.avatarcore.AvatarCore;
import com.sanan.avatarcore.abilities.air.AirPropulsionAbility;
import com.sanan.avatarcore.abilities.air.AirblastAbility;
import com.sanan.avatarcore.abilities.air.AirswipeAbility;
import com.sanan.avatarcore.abilities.air.SuctionAbility;
import com.sanan.avatarcore.abilities.earth.EarthGauntletAbility;
import com.sanan.avatarcore.abilities.earth.EarthLevitationAbility;
import com.sanan.avatarcore.abilities.earth.EarthLevitationPlusAbility;
import com.sanan.avatarcore.abilities.earth.EarthQuicksandAbility;
import com.sanan.avatarcore.abilities.earth.EarthSinkingAbility;
import com.sanan.avatarcore.abilities.earth.EarthWallAbility;
import com.sanan.avatarcore.abilities.earth.ExtractAbility;
import com.sanan.avatarcore.abilities.fire.DragonBreathAbility;
import com.sanan.avatarcore.abilities.fire.FireJetAbility;
import com.sanan.avatarcore.abilities.fire.FireballAbility;
import com.sanan.avatarcore.abilities.fire.ShieldOfFireAbility;
import com.sanan.avatarcore.abilities.water.MistSteppingAbility;
import com.sanan.avatarcore.abilities.water.ShieldOfWaterAbility;
import com.sanan.avatarcore.abilities.water.WaterManipulationAbility;
import com.sanan.avatarcore.util.bending.BendingAbilityManager;
import com.sanan.avatarcore.util.bending.BendingElement;
import com.sanan.avatarcore.util.bending.ability.BendingAbilitiesDataManager;
import com.sanan.avatarcore.util.bending.ability.BendingAbility;
import com.sanan.avatarcore.util.bending.ability.EnduringAbility;
import com.sanan.avatarcore.util.bending.ability.PassiveAbility;
import com.sanan.avatarcore.util.bending.ability.bendinglist.BendingBlock;
import com.sanan.avatarcore.util.bending.ability.hotbar.BendingHotbar;
import com.sanan.avatarcore.util.bending.ability.hotbar.BendingInventoryUtil;
import com.sanan.avatarcore.util.bending.ability.hotbar.BendingItemUtil;
import com.sanan.avatarcore.util.bending.ability.hotbar.holders.EditBendingHotbarHolder;
import com.sanan.avatarcore.util.bending.ability.hotbar.holders.HotbarAbilitySelectHolder;
import com.sanan.avatarcore.util.bending.ability.hotbar.holders.HotbarChooseElementHolder;
import com.sanan.avatarcore.util.bending.ability.hotbar.holders.ManageBendingHotbarsHolder;
import com.sanan.avatarcore.util.bending.ability.hotbar.holders.PassiveAbilitySelectHolder;
import com.sanan.avatarcore.util.bending.ability.hotbar.holders.SelectedBendingHotbarHolder;
import com.sanan.avatarcore.util.bending.ability.water.WaterBendingAbility;
import com.sanan.avatarcore.util.bendingwall.BendingWall;
import com.sanan.avatarcore.util.bendingwall.WallManager;
import com.sanan.avatarcore.util.bendingwall.WaterBendingWall;
import com.sanan.avatarcore.util.data.Setting;
import com.sanan.avatarcore.util.nation.NationManager;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;
import com.sanan.avatarcore.util.player.nation.PlayerNationData;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;

import net.ess3.api.InvalidWorldException;

import net.md_5.bungee.api.ChatColor;

public class BendingModeListener implements Listener {
	
	//In milliseconds
	private final int BENDING_MODE_ACTIVATE_THRESHOLD = 800;  
	
	private final Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
	private final BendingAbilitiesDataManager badm = BendingAbilitiesDataManager.getInstance();
	private final AvatarCore ac = AvatarCore.getInstance();
	private static PlayerManager pm = PlayerManager.getInstance();
	private static BendingAbilityManager bam = BendingAbilityManager.getInstance();
	private static NationManager nm = NationManager.getInstance();
	
	public ArrayList<Material> getBlocks(Block clickedBlock) {
		ArrayList<Material> blocks = new ArrayList<Material>();
		for (int i = -1; i < 2; i++) {
			for (int j = -2; j < 1; j++) {
				for (int k = -1; k < 2; k++) {
					Material item = Material.AIR;
					 for (BendingBlock block : BendingBlock.values()) {
						 if (block.getMaterial().equals(clickedBlock.getLocation().clone().add(i, j, k).getBlock().getType())) {
							 item = block.getMaterial();
						 }
					 }
					blocks.add(item);
				}
			}
		}
		return blocks;
	}
	
	@EventHandler
	public void onBendingInteractAbilityUse(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		@NotNull Entity victim = event.getRightClicked();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		ItemStack item = player.getInventory().getItemInMainHand();
	
		
		if (bPlayer.isInBendingMode() && item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
			event.setCancelled(true);
			String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
			for (BendingAbility ability : BendingAbilityManager.getInstance().getAllLoadedAbilities(false)) {
				if (ability.getName().equalsIgnoreCase(name)) {
					if (ability.isToggleAbility() && bam.isPlayerUsingAbility(bPlayer, name)) {
						bam.getPlayerAbility(bPlayer, name).remove();
						return;
					}
					
					if (bam.isPlayerUsingAbility(bPlayer, name)) return;
					
					if (!bPlayer.canAffordBendingAbility(ability)) {
						player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_BURN, 1, 1);
						return;
					}
	
					if (ability instanceof EarthSinkingAbility) {
						if (!BendingBlock.isEarthBendingBlock(victim.getLocation().clone().add(0, -1, 0).getBlock().getType()) || badm.containsSinkingPlayer(victim)) return;
						new EarthSinkingAbility(bPlayer, victim);
					} else if (ability instanceof EarthQuicksandAbility) 
						new EarthQuicksandAbility(bPlayer,victim);
				}
			}
		}
	}
	@EventHandler
	public void onBendingAbilityUse(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		ItemStack item = event.getItem();
		
		if (bPlayer.isInBendingMode() && item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && player.getOpenInventory().toString().contains("CraftInventoryView")) {
			event.setCancelled(true);
			String name = item.getItemMeta().getDisplayName();
			
			//Access Manage Hotbars
			if (name.equalsIgnoreCase(BendingItemUtil.getManageHotbarsAccessor().getItemMeta().getDisplayName())) {
				if (!bPlayer.hasFinishTutorial() && bPlayer.getTutorialStep() > 3.0) return;
				player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
				player.openInventory(BendingInventoryUtil.getManageBendingHotbarInventory(bPlayer));
				return;
			}
			
			if (name.equalsIgnoreCase(BendingItemUtil.getManageHotbarsPassiveAbility().getItemMeta().getDisplayName())) {
				player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
				player.openInventory(BendingInventoryUtil.getPassiveAbilitySelectInventory(bPlayer));
				return;
			}
			
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if (selectHotbar(player, name)) return;
			}
			
			else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (openModifierHotbarMenu(player, name)) return;
				RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));
				ApplicableRegionSet region = regions.getApplicableRegions(BlockVector3.at(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
				if (region.queryState(null, Flags.PVP) == State.DENY || region.queryState(null, Flags.INVINCIBILITY) == State.ALLOW) {
					player.sendMessage("§cBending is not permitted in this area.");
					return;
				}
				name = ChatColor.stripColor(name);
				for (BendingAbility ability : BendingAbilityManager.getInstance().getAllLoadedAbilities(false)) {
					if (ability.getName().equalsIgnoreCase(name)) {
						if (ability.isToggleAbility() && bam.isPlayerUsingAbility(bPlayer, name)) {
							bam.getPlayerAbility(bPlayer, name).remove();
							return;
						}
						
						if (bam.isPlayerUsingAbility(bPlayer, name)) return;
						
						if (!bPlayer.hasFinishTutorial() && bPlayer.getTutorialStep() != 5) return;
						
						if (!bPlayer.canAffordBendingAbility(ability)) {
							player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_BURN, 1, 1);
							return;
						}
						if (ability instanceof FireballAbility)
							new FireballAbility(bPlayer);
						else if (ability instanceof FireJetAbility)
							new FireJetAbility(bPlayer, 0.075f);
						else if (ability instanceof DragonBreathAbility)
							new DragonBreathAbility(bPlayer, 10);
						else if (ability instanceof MistSteppingAbility)
							new MistSteppingAbility(bPlayer);
						else if (ability instanceof ShieldOfFireAbility)
							new ShieldOfFireAbility(bPlayer);
						else if (ability instanceof EarthLevitationAbility || ability instanceof EarthLevitationPlusAbility) {
							if (!bam.isPlayerUsingAbility(bPlayer, "Earth Levitation") && event.getClickedBlock() != null) {
								ArrayList<Material> blocks = getBlocks(event.getClickedBlock());
								if (Collections.frequency(blocks, Material.AIR) == 27) return;
								new EarthLevitationAbility(bPlayer, blocks, event.getClickedBlock().getLocation());
							} 
						} else if (ability instanceof ExtractAbility) {
							if (event.getClickedBlock() == null) return;
							new ExtractAbility(bPlayer, event.getClickedBlock().getLocation());
						} else if (ability instanceof EarthGauntletAbility) 
							new EarthGauntletAbility(bPlayer);
						else if (ability instanceof EarthWallAbility)  {
							if (event.getClickedBlock() == null || !BendingBlock.isEarthBendingBlock(event.getClickedBlock().getType())) return;
							Location startingLocation = event.getClickedBlock().getLocation();
							double initialY = startingLocation.getY();
					        while (!EarthWallAbility.isBottom(BendingWall.createStableRow(startingLocation, player.getLocation(), 3))) {
					        	startingLocation.add(0, -1, 0);
					        }
					        if (initialY - startingLocation.getY() > 3) return;
							new EarthWallAbility(bPlayer, event.getClickedBlock().getLocation());
						} else if (ability instanceof AirblastAbility)
							new AirblastAbility(bPlayer, 0.3);
						else if (ability instanceof AirPropulsionAbility)
							new AirPropulsionAbility(bPlayer);
						else if (ability instanceof AirswipeAbility)
							new AirswipeAbility(bPlayer, 0.3);
						else if (ability instanceof SuctionAbility)
							new SuctionAbility(bPlayer);
						else if (ability instanceof ShieldOfWaterAbility) {
							List<Block> los = player.getLineOfSight(null, 5);
							for (Block block : los) {
								if (block.getType() == Material.WATER) {
									for (BendingWall wall : WallManager.getInstance().getWalls()) {
										if (wall instanceof WaterBendingWall && ((WaterBendingWall) wall).getOwner().equals(player)) {
											return;
										}
									}
									new ShieldOfWaterAbility(bPlayer, block.getLocation().clone().add(0, 1, 0));
									return;
								}
							}
						}
						else if (ability instanceof WaterBendingAbility) {
							List<Block> los = player.getLineOfSight(null, 5);
							for (Block block : los) {
								if (block.getType() == Material.WATER) {
									new WaterManipulationAbility(bPlayer);
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBendingHotbarRename(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		
		if (bPlayer.isInBendingMode() && bPlayer.isRenamingHotbar()) {
			event.setCancelled(true);
			String newName = event.getMessage();
			
			if (newName.length() >= (int)Setting.MAXIMUM_HOTBAR_NAME_LENGTH.get()) {
				player.sendMessage(ChatColor.RED + "The specified name is too long! Hotbar names must be within 12 characters.");
				return;
			}
			
			else if (newName.equalsIgnoreCase("cancel")) {
				bPlayer.getRenamingHotbar().setRenaming(false);
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 1);
				new BukkitRunnable() {
					@Override
					public void run() {
						player.openInventory(BendingInventoryUtil.getManageBendingHotbarInventory(bPlayer));
					}
				}.runTask(ac);
				return;
			}
			else {
				bPlayer.getRenamingHotbar().setName(newName);
				bPlayer.getRenamingHotbar().setRenaming(false);
				player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
				bPlayer.applyBendingInventory();
				bPlayer.applyBendingHotbarAbilities();
				new BukkitRunnable() {
					@Override
					public void run() {
						player.openInventory(BendingInventoryUtil.getManageBendingHotbarInventory(bPlayer));
					}
				}.runTask(ac);
				return;
			}
		}
	}
	
	@EventHandler
	public void onBendingModeToggle(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
	
		if (event.isSneaking() && player.getGameMode() != GameMode.CREATIVE) {

			if (bPlayer.isRenamingHotbar()) {
				BendingHotbar hotbar = bPlayer.getRenamingHotbar();
				player.sendMessage(ChatColor.GOLD + "Enter a new name for " + ChatColor.AQUA + hotbar.getName() + ChatColor.GOLD + " OR type '" + ChatColor.RED + "cancel" + ChatColor.GOLD + "' to cancel this action.");
				return;
			}
			
			int shifts = bPlayer.getShiftCounter();
			long lastShift = bPlayer.getLastSneakStamp();
			long diff = System.currentTimeMillis() - lastShift;
			if (!bPlayer.hasFinishTutorial() && (bPlayer.getTutorialStep() != 1.0 && bPlayer.getTutorialStep() != 6.0)) return;
			if (diff >= 0 && diff <= BENDING_MODE_ACTIVATE_THRESHOLD) {
				if (shifts == 2) {
					//Activate Bending Mode
					if (bPlayer.getTutorialStep() == 1.0) {
						bPlayer.nextStepTutorial();
					}
					player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 1, 1);
					bPlayer.setShiftCounter(0);
					bPlayer.toggleBendingMode();
					return;
				}
				bPlayer.setShiftCounter(2);
				bPlayer.updateLastSneakStamp();
				player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
				return;
			}
			else {
				bPlayer.setShiftCounter(1);
				bPlayer.updateLastSneakStamp();
			}
		}
	}
	
	@EventHandler
	public void onBendingModeInventoryInteract(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		ItemStack item = event.getCurrentItem();
		
		if (bPlayer.isInBendingMode()) {
			event.setCancelled(true);
			if (item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
				String name = item.getItemMeta().getDisplayName();
			
				//Access Manage Hotbars
				if (name.equalsIgnoreCase(BendingItemUtil.getManageHotbarsAccessor().getItemMeta().getDisplayName())) {
					if (!bPlayer.hasFinishTutorial() && bPlayer.getTutorialStep() > 3.0) return;
					player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
					player.openInventory(BendingInventoryUtil.getManageBendingHotbarInventory(bPlayer));
					return;
				}
				
				else if (name.equalsIgnoreCase(BendingItemUtil.getManageHotbarsPassiveAbility().getItemMeta().getDisplayName())) {
					player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
					player.openInventory(BendingInventoryUtil.getPassiveAbilitySelectInventory(bPlayer));
					return;
				}
				
				//Passive  ability
				else if (name.equalsIgnoreCase(BendingItemUtil.getManageHotbarsPassiveAbility().getItemMeta().getDisplayName())) {
					Inventory inv = BendingInventoryUtil.getPassiveAbilitySelectInventory(bPlayer);
					player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
					player.openInventory(inv);
					return;
				}
				
				else if (event.getClickedInventory().getHolder() instanceof PassiveAbilitySelectHolder) {
					for (BendingAbility loadedAbilities : BendingAbilityManager.getInstance().getAllLoadedAbilities(true)) {
						if (loadedAbilities.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(name)) {
							if (item.getType().equals(Material.BLACK_STAINED_GLASS_PANE)) {
								player.sendMessage("You do not have the necessary level to activate this passive ability!");
								player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
								return;
							}
							//Activate ABILITY
							player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
							if (item.getEnchantments().size() == 0) {
								if (bPlayer.getPassiveAbilityActivated().size() >= 7 - Collections.frequency(bPlayer.getBendingLevel().getBendingLevel().values(), 0)) {
									player.sendMessage("You have reached the selected passive abilities limit!");
									player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
									return;
								}
								item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
								item.addItemFlags(ItemFlag.HIDE_ENCHANTS);
								bPlayer.activatePassiveAbility(loadedAbilities);
								PassiveAbility.createPassiveAbilityInstance(bPlayer, loadedAbilities);
							} else {
								item.removeEnchantment(Enchantment.KNOCKBACK);
								item.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
								bPlayer.desactivatePassiveAbility(loadedAbilities);
								BendingAbility ability = bam.getPlayerAbility(bPlayer, loadedAbilities.getName());
								if (ability != null) {
									ability.remove();
								}
							}
							return;
						}
					}
				}
				
				else if (name.equalsIgnoreCase(BendingItemUtil.getManageHotbarsCreate().getItemMeta().getDisplayName())) {
					
					if (!bPlayer.hasFinishTutorial() && bPlayer.getTutorialStep() != 2.0) return;

					int count = bPlayer.getHotbars().size();
					
					if (count >= 7) {
						//Maximum amount
						player.closeInventory();
						player.sendMessage(ChatColor.RED + "You have reached the maximum amount of " + ChatColor.GOLD + "7 " + ChatColor.RED + "hotbars.");
						return;
					}
					int slot = bPlayer.isEquipped() ? 9 + count : 0 + count;
					
					BendingHotbar newHotbar = new BendingHotbar("hotbar" + count);
					bPlayer.addBendingHotbar(newHotbar);
					player.getInventory().setItem(slot, BendingItemUtil.getHotbarDisplayItem(newHotbar));
					player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
					if (!bPlayer.hasFinishTutorial()) {
						bPlayer.nextStepTutorial();
						return;
					}
					player.closeInventory();
					player.openInventory(BendingInventoryUtil.getManageBendingHotbarInventory(bPlayer));
					return;
				}
				
				/*
				 * SELECTED HOTBAR ACTIONS
				 */
				else if (event.getClickedInventory().getHolder() instanceof SelectedBendingHotbarHolder) {
					SelectedBendingHotbarHolder holder = (SelectedBendingHotbarHolder) event.getClickedInventory().getHolder();
					//REMOVE HOTBAR ACTION
					if (name.equalsIgnoreCase(BendingItemUtil.getSelectedHotbarRemove().getItemMeta().getDisplayName())) {
						if (!bPlayer.hasFinishTutorial()) return;
						if (bPlayer.getEquippedHotbar() != null && bPlayer.getEquippedHotbar().equals(holder.getSelected()))
							bPlayer.setEquippedHotbar(null);
						bPlayer.removeBendingHotbar(holder.getSelected());
						bPlayer.applyBendingInventory();
						bPlayer.applyBendingHotbarAbilities();
						player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
						player.openInventory(BendingInventoryUtil.getManageBendingHotbarInventory(bPlayer));
						return;
					}
					//RENAME HOTBAR ACTION
					else if (name.equalsIgnoreCase(BendingItemUtil.getSelectedHotbarRename().getItemMeta().getDisplayName())) {
						player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
						holder.getSelected().setRenaming(true);
						player.closeInventory();
						player.sendMessage(ChatColor.GOLD + "Enter a new name for " + ChatColor.AQUA + holder.getSelected().getName() + ChatColor.GOLD + " OR type '" + ChatColor.RED + "cancel" + ChatColor.GOLD + "' to cancel this action.");
						return;
					}
					//EDIT HOTBAR ACTION
					else if (name.equalsIgnoreCase(BendingItemUtil.getSelectedHotbarEdit().getItemMeta().getDisplayName())) {
						player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
						player.openInventory(BendingInventoryUtil.getEditBendingHotbarInventory(holder.getSelected()));
						return;
					}
				}
				
				/*
				 * EDITING HOTBAR / ABILITIES SELECT
				 */
				else if (event.getClickedInventory().getHolder() instanceof EditBendingHotbarHolder) {
					EditBendingHotbarHolder holder = (EditBendingHotbarHolder) event.getClickedInventory().getHolder();
					if (event.getSlot() >= 9 && event.getSlot() <= 17) {
						int abilitySlot = event.getSlot() - 9;
						if (event.getClick() == ClickType.LEFT) {
							player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
							player.openInventory(BendingInventoryUtil.getHotbarChooseElementInventory(holder.getHotbar(), abilitySlot, bPlayer));
						}
						else if (event.getClick() == ClickType.RIGHT) {
							player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
							holder.getHotbar().getAbilities()[abilitySlot] = null;
							player.openInventory(BendingInventoryUtil.getEditBendingHotbarInventory(holder.getHotbar()));
							bPlayer.applyBendingInventory();
							bPlayer.applyBendingHotbarAbilities();
						}
						return;
					}
					
				}
				
				
				//SELECT HOTBAR
				else if (event.getClickedInventory().getHolder() instanceof ManageBendingHotbarsHolder) {
					openModifierHotbarMenu(player, name);
				}
				
				/*
				 * ELEMENT SELECT
				 */
				else if (event.getClickedInventory().getHolder() instanceof HotbarChooseElementHolder) {
					HotbarChooseElementHolder holder = (HotbarChooseElementHolder)event.getClickedInventory().getHolder();
					Inventory inv = null;
					if (name.equalsIgnoreCase(BendingItemUtil.getFireElementItem().getItemMeta().getDisplayName())) 
						inv = BendingInventoryUtil.getHotbarAbilitySelectInventory(holder.getHotbar(), holder.getSlot(), bPlayer, BendingElement.FIRE);
					else if (name.equalsIgnoreCase(BendingItemUtil.getEarthElementItem().getItemMeta().getDisplayName())) 
						inv = BendingInventoryUtil.getHotbarAbilitySelectInventory(holder.getHotbar(), holder.getSlot(), bPlayer, BendingElement.EARTH);
					else if (name.equalsIgnoreCase(BendingItemUtil.getWaterElementItem().getItemMeta().getDisplayName())) 
						inv = BendingInventoryUtil.getHotbarAbilitySelectInventory(holder.getHotbar(), holder.getSlot(), bPlayer, BendingElement.WATER);
					else if (name.equalsIgnoreCase(BendingItemUtil.getAirElementItem().getItemMeta().getDisplayName())) 
						inv = BendingInventoryUtil.getHotbarAbilitySelectInventory(holder.getHotbar(), holder.getSlot(), bPlayer, BendingElement.AIR);

					player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
					player.openInventory(inv);
					return;
				}
				
				/*
				 * ABILITY ASSIGN
				 */
				else if (event.getClickedInventory().getHolder() instanceof HotbarAbilitySelectHolder) {
					HotbarAbilitySelectHolder holder = (HotbarAbilitySelectHolder) event.getClickedInventory().getHolder();
					for (BendingAbility loadedAbilities : BendingAbilityManager.getInstance().getAllLoadedAbilities(false)) {
						if (loadedAbilities.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(name)) {
							if (!bPlayer.hasFinishTutorial() && bPlayer.getTutorialStep() != 3.0) return;
							
							if (item.getType().equals(Material.BLACK_STAINED_GLASS_PANE)) {
								player.sendMessage("You do not have the necessary level to use this ability!");
								player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
								return;
							}
							//ASSIGN ABILITY
							holder.getHotbar().getAbilities()[holder.getSlot()] = loadedAbilities;
							player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
							player.openInventory(BendingInventoryUtil.getEditBendingHotbarInventory(holder.getHotbar()));
							bPlayer.applyBendingInventory();
							bPlayer.applyBendingHotbarAbilities();
							if (!bPlayer.hasFinishTutorial()) {
								bPlayer.nextStepTutorial();
							}
							return;
						}
					}
				}
				
				//EQUIP HOTBAR
				else if (event.getClickedInventory() instanceof PlayerInventory) {
					if (event.getClick() == ClickType.LEFT) {
						selectHotbar(player, name);
					}
					else if (event.getClick() == ClickType.RIGHT) {
						openModifierHotbarMenu(player, name);
					}
				}
 			}
		}
	}

	@EventHandler
	public void onBendingOffhand(PlayerSwapHandItemsEvent event) {
		Player player = event.getPlayer();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		
		if (bPlayer.isInBendingMode()) 
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onBendingModeItemDrop(PlayerDropItemEvent event) {
		Player player =  event.getPlayer();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		
		if (bPlayer.isInBendingMode()) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You cannot do this while in " + ChatColor.AQUA + "Bending Mode");
		}
	}
	
	@EventHandler
	public void onBendingModePickupItem(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		
		if (bPlayer != null && bPlayer.isInBendingMode()) {
			event.setCancelled(true);
			if (!badm.containsPickupItemWarningPlayer(player) && badm.canPickUpItems(event.getItem(), player)) {
				player.sendMessage("You can not pick up items in bending mode! Crouch 3 times to exit bending mode.");
				badm.addPickupItemWarningPlayer(player);
				new BukkitRunnable() {
			        public void run() {
			        	badm.removePickupItemWarningPlayer(player);;
			        }
			    }.runTaskLaterAsynchronously(ac, 200);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBendingModeQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		
		for (BendingAbility ability : bam.getAllCurrentPlayerAbilities(bPlayer)) {
			if (ability instanceof EnduringAbility) {
				ability.remove();
			}
		}
		bam.cleanupPlayerAbilities(bPlayer);
		if (bPlayer.isInBendingMode()) {
			bPlayer.toggleBendingMode();
		}
		
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBendingDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			BendingPlayer bPlayer = pm.getBendingPlayer(player);
			bPlayer.getChi().regenerate(100);
			// kill player xp
			if (event.getEntity().getKiller() instanceof Player) {
				if (!badm.containsDeathPlayersPair(event.getEntity().getKiller(), player)) {
					int deathAbilityBonusXp = event.getEntity().getLastDamageCause().getCause().equals(DamageCause.CUSTOM) ? 50 : 0;
					pm.getBendingPlayer(event.getEntity().getKiller()).addXp(100 + deathAbilityBonusXp);
					badm.addDeathPlayersPair(event.getEntity().getKiller(), player);
				    new BukkitRunnable() {
				        public void run() {
				        	badm.removeDeathPlayersPair(event.getEntity().getKiller(), player);
				        }
				    }.runTaskLaterAsynchronously(ac, 6000);
				}
			}
			for (BendingAbility ability : bam.getAllCurrentPlayerAbilities(bPlayer)) {
				if (ability instanceof EnduringAbility) {
					ability.remove();
				}
				if (ability.isToggleAbility()) ability.remove();
			}
			
			if (bPlayer.isInBendingMode()) {
				event.setDroppedExp(0);
				event.getDrops().clear();
				for (ItemStack i : bPlayer.getLastSavedInventory()) {
					event.getDrops().add(i);
				}
				bPlayer.toggleBendingMode();
			}
			
		// kill mob xp
		} else if (event.getEntity().getKiller() instanceof Player) {
			int xp = 0;
			Player player = event.getEntity().getKiller();
			BendingPlayer bPlayer = pm.getBendingPlayer(player);
			if (!bPlayer.hasFinishTutorial() && (bPlayer.getTutorialStep() != 5.0 || !event.getEntity().getCustomName().equals(player.getName()))) return;
			if (!bPlayer.hasFinishTutorial()) {
				event.setCancelled(true);
				event.getEntity().remove();
				xp = 100;
				bPlayer.nextStepTutorial();
	}
			else if (event.getEntity() instanceof IronGolem) { xp = 30; }
			else if (event.getEntity().getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
				if (event.getEntity() instanceof Monster) {	xp = 20; } 
				else { xp = 10;	}
			} 
			else {
				if (event.getEntity() instanceof Monster) {	xp = 50; } 
				else { xp = 40;	}
			}
			bPlayer.addXp(xp);
		}
	}

	@EventHandler
	public void onBendingUseCommand(PlayerCommandPreprocessEvent event) {
			BendingPlayer bPlayer = pm.getBendingPlayer(event.getPlayer());
			if (bPlayer.isInBendingMode()) {
				String[] disableCommands = {"kit", "clearinventory", "trade", "acution", "tpa"};
				for (String cmd : disableCommands) {
					if (event.getMessage().toLowerCase().contains(cmd)) {
						event.getPlayer().sendMessage("§cYou cannot use this command in bending mode!");
						event.setCancelled(true);
				}
			}
		}
		
	}

	@EventHandler
	public void onBendingPortalEnter(PlayerPortalEvent event) {
		BendingPlayer bPlayer = pm.getBendingPlayer((Player) event.getPlayer());
		if (bPlayer.isInBendingMode()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBendingCustomPortalEnter(PlayerMoveEvent event) throws WarpNotFoundException, InvalidWorldException {
		Player player = event.getPlayer();
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		
		double x = event.getTo().getBlock().getLocation().getX();
		double y = event.getTo().getBlock().getLocation().getY(); 
		double z = event.getTo().getBlock().getLocation().getZ();
		
		if (event.getTo().getWorld().getName().equalsIgnoreCase((String) Setting.SECONDARY_WORLD_NAME.get())) { 
			String command = "";
			//earth
			if (x >= -76 && x <= -74 && y >= 10 && y <= 12 && z == 148) {
				command = "bending join earth";
			}
			//water
			else if (x == -128 && y >= 10 && y <= 12 && z >= 99 && z <= 101) {
				command = "bending join water";
			}
			//air
			else if (x >= -75 && x <= -73 && y >= 12 && y <= 16 && z == 43) {
				command = "bending join air";
			}
			//fire
			else if (x == -35 && y >= 13 && y <= 16 && z >= 99 && z <= 102) {
				command = "bending join fire";
			}
			if (!command.equals("")) {
				String actualClass = bPlayer.getBendingLevel().getBendingClass();
				player.performCommand(command);
				if (actualClass.equals(bPlayer.getBendingLevel().getBendingClass())) {
					player.setVelocity(player.getLocation().getDirection().multiply(-2));
				}
				else {
					player.teleport(essentials.getWarps().getWarp("spawn"));
				}
			}
		}
		else if (event.getTo().getWorld().getName().equalsIgnoreCase((String) Setting.MAIN_WORLD_NAME.get())) {
			String commandJoin = "";
			String commandTP = "";
			if (x >= 493 && x <= 497 && y >= 87 && y <= 93 && z == -97) {
				commandJoin = "nation join Earth";
				commandTP = "BaSingSe";
			}
			else if (x >= 503 && x <= 507 && y >= 87 && y <= 93 && z == -94) {
				commandJoin = "nation join Air";
				commandTP = "AirTemple";
			}
			else if (x >= 513 && x <= 517 && y >= 87 && y <= 93 && z == -90) {
				commandJoin = "nation join Fire";
				commandTP = "FireNation";
			}
			else if (x == 527 && y >= 87 && y <= 93 && z >= -79 && z <= -75) {
				commandJoin = "nation join Water";
				commandTP = "WaterTribe";
			}
			
			else if (x == 531 && y >= 87 && y <= 93 && z >= -69 && z <= -65) {
				if (bPlayer.getBendingLevel().getLevel() == 100 || bPlayer.getBendingLevel().getBendingClass().equalsIgnoreCase("none")) {
					player.teleport(essentials.getWarps().getWarp("upgrade"));
					return;
				}
				else {
					player.sendMessage("You haven't reached level 100 or you are already an Avatar!");
					player.setVelocity(player.getLocation().getDirection().multiply(-2));
					return;
				}
			}
			if (!commandTP.equals("")) {
				String nationName = commandJoin.split(" ")[2];
				PlayerNationData pnd = bPlayer.getPlayerNationData();
				if (!pnd.hasNationChosen() || (ChatColor.stripColor(pnd.getNationChosen()).equalsIgnoreCase(nationName) && !nm.playerHasNation(bPlayer))) 
					player.performCommand(commandJoin);
					pnd = bPlayer.getPlayerNationData();
				if (ChatColor.stripColor(pnd.getNationChosen()).equalsIgnoreCase(nationName) && nm.playerHasNation(bPlayer)) {
					player.teleport(essentials.getWarps().getWarp(commandTP));
					return;
				}
				player.teleport(essentials.getWarps().getWarp(nationName.toLowerCase()));
			}
		}
	}
	
	private boolean selectHotbar(Player player, String name) {
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		if (!bPlayer.hasFinishTutorial() && bPlayer.getTutorialStep() != 4.0) return false;
		for (BendingHotbar playerHotbars : bPlayer.getHotbars()) {
			if (name.equalsIgnoreCase(BendingItemUtil.getHotbarDisplayItem(playerHotbars).getItemMeta().getDisplayName())) {
				player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
				BendingHotbar newHotbar = playerHotbars; 
				if (playerHotbars.equals(bPlayer.getEquippedHotbar())) { 
					newHotbar = null; 
				}
				bPlayer.setEquippedHotbar(newHotbar);
				bPlayer.applyBendingInventory();
				bPlayer.applyBendingHotbarAbilities();
				if (!bPlayer.hasFinishTutorial()) {
					bPlayer.nextStepTutorial();
					player.closeInventory();
				}
				return true;	
			}
		}
		return false;
	}

	private boolean openModifierHotbarMenu(Player player, String name) {
		BendingPlayer bPlayer = pm.getBendingPlayer(player);
		if (!bPlayer.hasFinishTutorial() && bPlayer.getTutorialStep() > 3.0) return false;
		for (BendingHotbar playerHotbar : bPlayer.getHotbars()) {
			if (name.equalsIgnoreCase(BendingItemUtil.getHotbarDisplayItem(playerHotbar).getItemMeta().getDisplayName())) {
				player.openInventory(BendingInventoryUtil.getSelectedBendingHotbarInventory(playerHotbar));
				player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
				return true;
			}
		}
		return false;
	}
	
}
