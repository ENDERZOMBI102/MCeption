package com.enderzombi102.mception.block;

import com.enderzombi102.mception.MCeption;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class ComputerBlockEntity extends BlockEntity {

	public ComputerBlockEntity(BlockPos pos, BlockState state) {
		super(MCeption.COMPUTER_BLOCK_ENTITY_TYPE, pos, state);
	}

	// Serialize the BlockEntity
	@Override
	public NbtCompound writeNbt(NbtCompound tag) {
		super.writeNbt(tag);

		return tag;
	}

	// Deserialize the BlockEntity
	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);
	}

}
