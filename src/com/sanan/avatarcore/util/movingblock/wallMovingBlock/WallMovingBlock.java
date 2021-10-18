package com.sanan.avatarcore.util.movingblock.wallMovingBlock;

import com.sanan.avatarcore.util.item.ItemBuilder;
import com.sanan.avatarcore.util.movingblock.MovingBlock;

public class WallMovingBlock extends MovingBlock {

	public WallMovingBlock(ItemBuilder block, WallMovingBlockEntity entity) {
		super(block, entity);
	}
	
	public void setLife(float life) {
		((WallMovingBlockEntity) getMovingBlock()).setLife(life);
	}

	public void remove() {
		getMovingBlock().remove();
	}
}
