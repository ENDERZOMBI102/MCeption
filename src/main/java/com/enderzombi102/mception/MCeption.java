package com.enderzombi102.mception;

import com.enderzombi102.mception.computer.ComputerBlock;
import com.enderzombi102.mception.computer.ComputerBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MCeption implements ModInitializer {

	public static final ComputerBlock COMPUTER_BLOCK;
	public static final Logger LOGGER = LogManager.getLogger("MCeption");
	public static final BlockEntityType<ComputerBlockEntity> COMPUTER_BLOCK_ENTITY_TYPE;

	static {
		COMPUTER_BLOCK = Registry.register(
				Registry.BLOCK,
				ID("computer_block"),
				new ComputerBlock()
		);
		Registry.register(
				Registry.ITEM,
				ID("computer_block"),
				new BlockItem(
						COMPUTER_BLOCK,
						new FabricItemSettings()
								.group(ItemGroup.DECORATIONS)
								.rarity(Rarity.UNCOMMON)
				)
		);
		COMPUTER_BLOCK_ENTITY_TYPE = Registry.register(
				Registry.BLOCK_ENTITY_TYPE,
				ID("computer_block"),
				FabricBlockEntityTypeBuilder.create(
						ComputerBlockEntity::new,
						COMPUTER_BLOCK
				).build(null)
		);
	}

	@Override
	public void onInitialize() { }

	public static Identifier ID(String path) {
		return new Identifier("mception", path);
	}
}
