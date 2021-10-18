package com.sanan.avatarcore.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.sanan.avatarcore.util.nation.tribe.BendingTribe;
import com.sanan.avatarcore.util.nation.tribe.Claim;
import com.sanan.avatarcore.util.nation.tribe.ClaimManager;
import com.sanan.avatarcore.util.nation.tribe.TribeManager;
import com.sanan.avatarcore.util.nation.tribe.TribeMember;
import com.sanan.avatarcore.util.player.BendingPlayer;
import com.sanan.avatarcore.util.player.PlayerManager;

public class TribeListener implements Listener {
	
    private final ClaimManager clm = ClaimManager.getInstance();
    private final PlayerManager pm = PlayerManager.getInstance();
    private final TribeManager tm = TribeManager.getInstance();
    
    @EventHandler
    public void onMoveOnClaim(PlayerMoveEvent event) {
	    Player player = event.getPlayer();
	    BendingPlayer bPlayer = pm.getBendingPlayer(player);
	    	
	    if (!tm.playerHasTribe(bPlayer)) {
	    	return;
	    }
	    BendingTribe tribe = tm.getTribe(bPlayer);
	    TribeMember tPlayer = tribe.getMember(player.getUniqueId());
	    if (tPlayer.isInAutoClaim()) {
	    	Claim claim = new Claim(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ(), player.getWorld());
	        String tryClaim = tribe.tryClaim(claim);
	        
	        if (tryClaim.matches("^From .*$")) {
	        	tribe.sendTribeMessage(player.getName() + " has claimed 1 chunk of land from " + tryClaim.split(" ")[1] + "'s tribe.");
	        }
	        else if (tryClaim.equalsIgnoreCase("claim")) {
	        	tribe.sendTribeMessage(player.getName() + " has claimed 1 chunk of land");
	        }
	        else {
	        	return;
	        }
	        player.sendMessage("To stop auto claim type /tr autoclaim a 2nd time.");
	    }
    }
    
    @EventHandler
    public void onPlayerAttackTribeMember(EntityDamageByEntityEvent event) {
    	if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
    		Player player = (Player) event.getDamager();
    		BendingPlayer bPlayer = pm.getBendingPlayer(player);
    		if (!tm.playerHasTribe(bPlayer)) return;
    		BendingTribe tribe = tm.getTribe(bPlayer);
    		if (tribe.isAlly((Player) event.getEntity())) {
    			event.setCancelled(true);
    			player.sendMessage("You cannot attack an allied tribesman!");
    			return;
    		}
    		BendingPlayer bVictim = pm.getBendingPlayer((Player) event.getEntity());
    		BendingTribe victimTribe = tm.getTribe(bVictim);
    		if (tribe.equals(victimTribe)) {
    			event.setCancelled(true);
    			player.sendMessage("You cannot attack a member of your tribe!");
    		}
    		
    	}
	}
    
    @EventHandler
    public void onInteractOnClaim(PlayerInteractEvent event) {
    	if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType().isInteractable()) {
    		Player player = event.getPlayer();
    		Location location = event.getClickedBlock().getLocation();
            BendingPlayer bPlayer = pm.getBendingPlayer(player);
    		BendingTribe tribe = tm.getTribe(bPlayer);
    		Claim claim = new Claim(location.getChunk().getX(), location.getChunk().getZ(), player.getWorld());
            BendingTribe claimOwner = clm.getClaims().get(claim);
            if (claimOwner != null && !claimOwner.equals(tribe)) {
            	event.setCancelled(true);
            	player.sendMessage("You cannot interact in another tribe's claim!");
            }
    		
    	}
    }
    
	@EventHandler
	public void onPlaceOnClaim(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Location location = event.getBlock().getLocation();
        BendingPlayer bPlayer = pm.getBendingPlayer(player);
		BendingTribe tribe = tm.getTribe(bPlayer);
		Claim claim = new Claim(location.getChunk().getX(), location.getChunk().getZ(), player.getWorld());
        BendingTribe claimOwner = clm.getClaims().get(claim);
        if (claimOwner != null && !claimOwner.equals(tribe)) {
        	event.setCancelled(true);
        	player.sendMessage("You cannot place a block in another tribe's claim!");
        }
	}
	
	@EventHandler
	public void onBreakOnClaim(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Location location = event.getBlock().getLocation();
        BendingPlayer bPlayer = pm.getBendingPlayer(player);
		BendingTribe tribe = tm.getTribe(bPlayer);
		Claim claim = new Claim(location.getChunk().getX(), location.getChunk().getZ(), player.getWorld());
        BendingTribe claimOwner = clm.getClaims().get(claim);
        if (claimOwner != null && !claimOwner.equals(tribe)) {
        	event.setCancelled(true);
        	player.sendMessage("You cannot break a block in another tribe's claim!");
        }
	}
	
	@EventHandler
	public void onSetHomeOnClaim(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().toLowerCase().contains("sethome")) {
			Player player = event.getPlayer();
	        BendingPlayer bPlayer = pm.getBendingPlayer(player);
			BendingTribe tribe = tm.getTribe(bPlayer);
			Claim claim = new Claim(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ(), player.getWorld());
	        BendingTribe claimOwner = clm.getClaims().get(claim);
	        if (claimOwner != null && !claimOwner.equals(tribe)) {
	        	event.setCancelled(true);
	        	player.sendMessage("You can't put a home in another tribe's claim!!");
	        }
		}
	}
}
