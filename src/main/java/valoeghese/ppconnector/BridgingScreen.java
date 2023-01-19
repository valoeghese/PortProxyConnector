package valoeghese.ppconnector;

import benzenestudios.sulphate.Anchor;
import benzenestudios.sulphate.SulphateScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import valoeghese.ppconnector.util.Response;

import java.io.IOException;

/**
 * Screen for while we're bridging between port proxy and vanilla. That is, connecting to port proxy to tell it to connect to us.
 */
public class BridgingScreen extends SulphateScreen {
	protected BridgingScreen(Screen parent, Host host) {
		super(Component.empty(), parent);

		this.setAnchorY(Anchor.TOP, () -> this.height / 4 + 120 + 12);

		// responses:
		// (ip)
		// invalid code //even if not online

		Thread postThread = new Thread(() -> {
			String publicIp = PortProxyConnector.getPublicIp();

			if (publicIp == null) {
				this.error(Component.translatable("error.ppconnector.noGetIp"));
				return;
			}

			try (Response response = Response.post(host.getPortProxyLink())
					.set("ip", publicIp)
					.submit()) {
				String remoteIp = response.getAsString();

				if ("invalid code".equals(remoteIp)) {
					this.error(Component.translatable("error.ppconnector.notOnline"));
					return;
				}
				else {
					// in case they changed screens by cancelling
					if (Minecraft.getInstance().screen == this) {
						RenderSystem.recordRenderCall(() -> ConnectScreen.startConnecting(this.parent, Minecraft.getInstance(), ServerAddress.parseString(remoteIp), new ServerData(host.getDisplayName(), remoteIp, false)));
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
				this.error(Component.literal(e.toString()));
				return;
			}
		});

		postThread.setDaemon(true);
		postThread.start();
	}

	private void error(Component reason) {
		if (RenderSystem.isOnRenderThread()) {
			this.minecraft.setScreen(new ErrorScreen(reason, this.parent));
		}
		else {
			RenderSystem.recordRenderCall(() -> this.minecraft.setScreen(new ErrorScreen(reason, this.parent)));
		}
	}

	@Override
	protected void addWidgets() {
		this.addButton(CommonComponents.GUI_CANCEL, b -> this.onClose());
	}

	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
		drawCenteredString(matrices, this.font, Component.translatable("ppconnector.connecting"), this.width/2, this.height / 2 - 50, 0xEFEFEF);
	}
}
