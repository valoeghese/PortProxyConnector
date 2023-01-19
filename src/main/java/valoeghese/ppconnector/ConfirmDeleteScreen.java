package valoeghese.ppconnector;

import benzenestudios.sulphate.Anchor;
import benzenestudios.sulphate.SulphateScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ConfirmDeleteScreen extends SulphateScreen {
	protected ConfirmDeleteScreen(Screen parent, Host toDelete) {
		super(Component.translatable("button.ppconnector.removehost"), parent);
		this.setAnchorY(Anchor.TOP, () -> this.height / 2 + 12);
		this.setRows(2);
		this.toDelete = toDelete;
	}

	private final Host toDelete;

	@Override
	protected void addWidgets() {
		this.addButton(CommonComponents.GUI_YES, b -> {
			PortProxyConnector.removeHost(this.toDelete);

			if (PortProxyConnector.saveHosts()) {
				this.onClose();
			} else {
				PortProxyConnector.addHost(this.toDelete);
				this.minecraft.setScreen(new ErrorScreen(Component.translatable("error.ppconnector.savingHosts"), this.parent));
			}
		});

		this.addButton(CommonComponents.GUI_CANCEL, b -> this.onClose());
	}

	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		drawCenteredString(matrices, this.font, Component.translatable("ppconnector.deleteWarning"), this.width/2, this.height/2 - 12, 0xEFEFEF);
	}
}
