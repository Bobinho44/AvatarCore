package com.sanan.avatarcore.util.player.chi;

public class BendingChi {

	private int maximum;
	private int current;
	
	public BendingChi(int maximum) {
		this.maximum = maximum;
		this.current = maximum;
	}
	
	public int getCurrent() {
		return current;
	}
	
	public int getMaximum() {
		return maximum;
	}
	
	public void regenerate(int amount) {
		if (this.current + amount >= this.maximum) this.current = this.maximum;
		else this.current += amount;
	}
	
	public void use(int amount) {
		if (this.current - amount >= 0) this.current -= amount;
		else this.current = 0;
	}
	
	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}
	
}
