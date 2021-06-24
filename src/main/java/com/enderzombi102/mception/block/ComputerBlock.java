package com.enderzombi102.mception.block;

import com.enderzombi102.mception.screen.ComputerScreenHandler;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ComputerBlock extends HorizontalFacingBlock implements BlockEntityProvider, NamedScreenHandlerFactory {

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
		if (! world.isClient ) {
			//This will call the createScreenHandlerFactory method from BlockWithEntity, which will return our blockEntity casted to
			//a namedScreenHandlerFactory. If your block class does not extend BlockWithEntity, it needs to implement createScreenHandlerFactory.
			NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

			if (screenHandlerFactory != null) {
				// With this call the server will request the client to open the appropriate ScreenHandler
				player.openHandledScreen(screenHandlerFactory);
			}
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

	// named screen handled factory

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
