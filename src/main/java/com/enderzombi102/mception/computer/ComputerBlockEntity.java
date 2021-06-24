package com.enderzombi102.mception.computer;

import com.enderzombi102.mception.MCeption;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ComputerBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {

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

	@Override
	public Text getDisplayName() {
		return new LiteralText("Computer Screen");
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new ComputerScreenHandler(syncId, inv);
	}

}
