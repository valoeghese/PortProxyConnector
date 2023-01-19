package valoeghese.ppconnector;

import benzenestudios.sulphate.Anchor;
import benzenestudios.sulphate.SulphateScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class ErrorScreen extends SulphateScreen {
	protected ErrorScreen(Component text, @Nullable Screen parent) {
		super(Component.empty(), parent);
		this.setAnchorY(Anchor.TOP, () -> this.height / 2 + 12);
		this.text = text;
	}

	private final Component text;

	@Override
	protected void addWidgets() {
		this.addButton(Component.translatable("button.ppconnector.ok"), b -> this.onClose());
	}

	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		drawCenteredString(matrices, this.font, this.text, this.width/2, this.height/2 - 12, 0xEFEFEF);
	}
}
