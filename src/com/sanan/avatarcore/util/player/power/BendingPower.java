package com.sanan.avatarcore.util.player.power;

public class BendingPower {
    private final int power;

    public BendingPower() {
        this.power = 10;
    }

    private BendingPower(int power) {
        this.power = power;
    }

    public int getPowerValue() {
        return this.power;
    }

    public BendingPower increaseBy(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("An amount value must be greater than 0.");
        }

        return new BendingPower(this.power + amount);
    }

    public BendingPower decreaseBy(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("An amount value must be greater than 0.");
        }

        return new BendingPower(this.power - amount);
    }

    public BendingPower set(int newPower) {
        if (this.power == newPower) {
            return this;
        }

        return new BendingPower(newPower);
    }
}
