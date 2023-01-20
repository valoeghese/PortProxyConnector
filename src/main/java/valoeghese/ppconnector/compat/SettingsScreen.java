package valoeghese.ppconnector.compat;

import benzenestudios.sulphate.SulphateScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import valoeghese.ppconnector.ErrorScreen;
import valoeghese.ppconnector.PortProxyConnector;

public class SettingsScreen extends SulphateScreen {
	protected SettingsScreen(@Nullable Screen parent) {
		super(Component.translatable("menu.ppconnector.settings"), parent);
		this.replaceRealms = this.originalReplaceRealms = PortProxyConnector.settings.getProperty("replaceRealms").equals("true");
	}

	private final boolean originalReplaceRealms;
	private boolean replaceRealms;

	@Override
	protected void addWidgets() {
		this.addButton(getReplaceRealmsText(), bn -> {
			replaceRealms = !replaceRealms;
			bn.setMessage(this.getReplaceRealmsText());
		});

		this.addDone();
	}

	@Override
	public void onClose() {
		if (this.replaceRealms == this.originalReplaceRealms) {
			super.onClose();
		}
		else {
			PortProxyConnector.settings.setProperty("replaceRealms", String.valueOf(this.replaceRealms));

			if (PortProxyConnector.saveSettings()) {
				super.onClose();
			}
			else {
				this.minecraft.setScreen(new ErrorScreen(Component.translatable("error.ppconnector.savingSettings"), this.parent));
			}
		}
	}

	private Component getReplaceRealmsText() {
		return Component.translatable("button.ppconnector.replaceRealms").append(this.replaceRealms ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
	}
}
