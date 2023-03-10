package valoeghese.ppconnector;

import benzenestudios.sulphate.Anchor;
import benzenestudios.sulphate.SulphateScreen;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import valoeghese.ppconnector.util.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class AddHostScreen extends SulphateScreen {
	protected AddHostScreen(@Nullable Screen parent) {
		super(Component.translatable("menu.ppconnector.addhost"), parent);
		this.setAnchorY(Anchor.TOP, () -> this.height / 2 - 10 + 24);
		this.setRows(2);
	}

	private EditBox username;
	private EditBox link;
	private Button button;

	@Override
	protected void addWidgets() {
		this.addRenderableWidget(this.username = new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 10 - 24, 200, 20, Component.translatable("textbox.ppconnector.enterUsername")));
		this.addRenderableWidget(this.link = new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 10, 200, 20, Component.translatable("textbox.ppconnector.enterLink")));

		this.username.setMaxLength(16);
		this.link.setMaxLength(64);

		Button b = this.addButton(98, 20, Component.translatable("button.ppconnector.addhost"), bn -> {
			this.button.active = false;
			this.username.active = false;
			this.link.active = false;

			Thread t = new Thread(() -> {
				String username = this.username.getValue();
				String url = this.link.getValue();

				// check not empty
				if (username.isEmpty()) {
					this.error(Component.translatable("error.ppconnector.emptyUsername"));
					return;
				}

				if (url.isEmpty()) {
					this.error(Component.translatable("error.ppconnector.emptyLink"));
					return;
				}

				// check valid url
				if (!PORTPROXY_URL.matcher(url).find()) {
					this.error(Component.translatable("error.ppconnector.invalidLink", url));
					return;
				}

				@Nullable String uuid = USERNAME_TO_UUID.computeIfAbsent(username, u -> {
					try (Response response = Response.get("https://api.mojang.com/users/profiles/minecraft/" + username)) {
						if (response.getStatusCode() / 100 == 2) {
							return UNDASHED_UUID_GAPS.matcher(response.getAsJson().getAsJsonObject().get("id").getAsString()).replaceAll(UUID_DASHIFIER_REPLACEMENT);
						}
					}
					catch (IOException e) {
						e.printStackTrace();
					}

					return null;
				});

				// Handle error getting uuid
				if (uuid == null) {
					USERNAME_TO_UUID.remove(username);
					this.error(Component.translatable("error.ppconnector.uuidLookupFailure", username));
					return;
				}

				Host host = new Host(new GameProfile(UUID.fromString(uuid), username), url, true);
				PortProxyConnector.addHost(host);

				if (PortProxyConnector.saveHosts()) {
					Minecraft.getInstance().tell(this::onClose);
				} else {
					PortProxyConnector.removeHost(host);
					Minecraft.getInstance().tell(() -> this.minecraft.setScreen(new ErrorScreen(Component.translatable("error.ppconnector.savingHosts"), this.parent)));
				}
			});

			t.setName("Add Host " + this.username.getValue());
			t.setDaemon(true);
			t.start();
		});

		if (this.button != null) b.active = this.button.active;
		this.button = b;

		this.addButton(98, 20, CommonComponents.GUI_CANCEL, bn -> onClose());
	}

	private void error(Component component) {
		if (RenderSystem.isOnRenderThread()) {
			this.minecraft.setScreen(new ErrorScreen(component, this));
		}
		else {
			RenderSystem.recordRenderCall(() -> this.minecraft.setScreen(new ErrorScreen(component, this)));
		}

		this.button.active = true;
		this.link.active = true;
		this.username.active = true;
	}

	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);

		// Fix mojang's jank
		if (this.link.isFocused() && this.username.isFocused()) {
			this.link.setFocus(false);
		}

		// Placeholders

		if (this.link.getValue().isEmpty()) {
			drawString(matrices, this.font, this.link.getMessage(), this.link.x + 4, this.link.y + this.link.getHeight()/4 + 1, 0x9F9F9F);
		}

		if (this.username.getValue().isEmpty()) {
			drawString(matrices, this.font, this.username.getMessage(), this.username.x + 4, this.username.y + this.username.getHeight()/4 + 1, 0x9F9F9F);
		}
	}

	private static final Map<String, String> USERNAME_TO_UUID = new HashMap<>();

	private static final Pattern PORTPROXY_URL = Pattern.compile("https:\\/\\/eyezah\\.com\\/portproxy\\/join\\?.+");
	private static final Pattern UNDASHED_UUID_GAPS = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");
	private static final String UUID_DASHIFIER_REPLACEMENT = "$1-$2-$3-$4-$5";
}
