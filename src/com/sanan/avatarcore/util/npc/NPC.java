package com.sanan.avatarcore.util.npc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_16_R3.PacketPlayOutEntity;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.DataWatcher;
import net.minecraft.server.v1_16_R3.DataWatcherObject;
import net.minecraft.server.v1_16_R3.DataWatcherRegistry;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;
import net.minecraft.server.v1_16_R3.World;
import net.minecraft.server.v1_16_R3.WorldServer;

import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.sanan.avatarcore.AvatarCore;

public class NPC {

	private EntityPlayer npc;
	
	public NPC(UUID uuid, Location location, String name, List<String> texture) {
		MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
	    WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
	    GameProfile skin = new GameProfile(uuid, name);
	    skin.getProperties().put("textures", new Property("textures", texture.get(0), texture.get(1)));		
		this.npc = new EntityPlayer(server, world, skin, new PlayerInteractManager(world));
	    this.npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}
	
	public int getId() {
		return npc.getId();
	}
	
	public String getName() {
		return npc.getName();
	}
	
	public Location getLocation() {
		return npc.getBukkitEntity().getLocation();
	}
	 
	public void setLocation(Location location) {
		npc.world = ((CraftWorld) location.getWorld()).getHandle();
		npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}
	public void spawn(Player player) {
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		
		connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npc));
		connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
		
		new BukkitRunnable() {
	        public void run() {
	        	connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, npc)); 
	        }
	    }.runTaskLaterAsynchronously(AvatarCore.getInstance(), 15);
	}
	
	public void spawn(Player player, Location location) {
		setLocation(location);
		spawn(player);
	}
	
	public void despawn(Player player) {
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutEntityDestroy(getId()));
	}
	
	public void kill(Player player) {
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		DataWatcher watcher = npc.getDataWatcher();
		watcher.set(new DataWatcherObject<>(8, DataWatcherRegistry.c), (float) 0);
		connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), watcher, true));
	}
	
	public List<Byte> look(Player player, Location location) {
		if (!location.getWorld().equals(getLocation().getWorld())) return null;
		PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
		double zDifference = location.getZ() - getLocation().getZ();
		double xDifference = location.getX() - getLocation().getX();
		
		double distance = location.distance(getLocation());
		double yDifference = location.getY() - getLocation().getY();
		
		Vector vectorPitch = new Vector(distance, Math.abs(yDifference), 0);
		double pitch = Math.toDegrees(vectorPitch.angle(new Vector(0, 2, 0)));
		int isUpper = yDifference >= 0 ? 1 : -1;
		
		Vector vectorYaw = new Vector(zDifference, 0, xDifference);
		double yaw = Math.toDegrees(vectorYaw.angle(new Vector(0, 0, 1)));

		yaw = zDifference >= 0 ? yaw - 90 : 270 - yaw;
		connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(npc.getId(), getByte(yaw), getByte(isUpper * (pitch -90)), true));
		connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, getByte(yaw)));
		return Arrays.asList(getByte(yaw), getByte(isUpper * (pitch -90)));
	}
	
	public void walk(Player player, Location location) {
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		Location start = npc.getBukkitEntity().getLocation().clone();
		Vector delta = location.toVector().subtract(start.toVector());
		double distance = Math.sqrt(delta.getX()*delta.getX()+delta.getZ()*delta.getZ())*2;
		delta.divide(new Vector(distance, distance, distance));
		new BukkitRunnable() {
			int i = 1;
		    public void run() {
				Location curr = start.clone().add(delta.clone().multiply(i));
				curr.setY(Math.round(curr.getY()));
				while (getLocation() != null && player.getWorld().equals(getLocation().getWorld()) && !player.getWorld().getBlockAt(curr.clone().add(0, -1, 0)).isSolid()) {
					curr.add(0, -1, 0);
				}
				Location prev = npc.getBukkitEntity().getLocation();
		    	setLocation(curr);
				short x = (short) ((curr.getX() * 32 - prev.getX() * 32) * 128);
				short y = (short) Math.round(((curr.getY() * 32 - prev.getY() * 32) * 128));
				short z = (short) ((curr.getZ() * 32 - prev.getZ() * 32) * 128);
				List<Byte> looks = look(player, location);
				if (looks != null) {
					connection.sendPacket(new PacketPlayOutRelEntityMoveLook(npc.getId(), x, y, z, looks.get(0), looks.get(1), true));
				     i++;
				     if (i > distance) {
				    	 cancel();
				     }
				}
		    }
		}.runTaskTimer(AvatarCore.getInstance(), 0, 2);
	}
	
	private byte getByte(double number) {
		return (byte) (number * 256.0F / 360.0F);
	}
	
}
