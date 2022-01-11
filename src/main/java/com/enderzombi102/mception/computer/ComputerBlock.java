package com.enderzombi102.mception.computer;

import com.enderzombi102.mception.MCeption;
import com.enderzombi102.mception.client.screen.ComputerScreen;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ComputerBlock extends HorizontalFacingBlock implements BlockEntityProvider {

	public static final BooleanProperty ACTIVE = BooleanProperty.of("active");

	public ComputerBlock() {
		super(
				FabricBlockSettings
						.of(Material.METAL)
						.luminance( block -> block.get(ACTIVE) ? 7 : 0 )
						.breakByTool(FabricToolTags.PICKAXES)
						.requiresTool()
		);
		setDefaultState(
				getStateManager()
						.getDefaultState()
						.with( ACTIVE, false )
						.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
		);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			MinecraftClient.getInstance().setScreen( new ComputerScreen() );
		}
		return ActionResult.SUCCESS;
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		// shutdown pc
		super.afterBreak(world, player, pos, state, blockEntity, stack);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
		stateManager.add(Properties.HORIZONTAL_FACING);
		stateManager.add(ACTIVE);
	}

	// horizontal facing block
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(
				Properties.HORIZONTAL_FACING,
				ctx.getPlayerFacing().getOpposite()
		);
	}

	// block entity provider

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ComputerBlockEntity(pos, state);
	}
}
