package com.enderzombi102.mception.client.screen;

import dev.lambdaurora.spruceui.screen.SpruceScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

@Environment(EnvType.CLIENT)
public class ComputerScreenImpl extends SpruceScreen {

	protected ComputerScreenImpl() {
		super( new LiteralText("computer") );
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
	}
}
