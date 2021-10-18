package com.sanan.avatarcore.util.nation.tribe;

import java.util.Objects;

import org.bukkit.Chunk;
import org.bukkit.World;

public class Claim {

	private int x;
	private int y;
	private World world;
	
	public Claim(int x, int y, World world) {
		this.x = x;
		this.y = y;
		this.world = world;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public World getWorld() {
		return world;
	}
	
	public boolean equals(Chunk chunk) {
		return chunk.getX() == x && chunk.getZ() == y && chunk.getWorld().equals(world);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Claim)) {
			return false;
		}
		Claim other = (Claim) obj;
		return other.getX() == x && other.getY() == y && other.getWorld() == world;
	}
	
	 @Override
	 public int hashCode() {
		 return Objects.hash(x, y, world);
	 }
	
}
