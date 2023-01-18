package valoeghese.ppconnector;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;

import java.util.UUID;

public class Host {
	public Host(GameProfile user, String portProxyLink) {
		this.user = user;
		this.portProxyLink = portProxyLink;
	}

	private volatile GameProfile user;
	private String portProxyLink;

	public GameProfile getUser() {
		return this.user;
	}

	public String getDisplayName() {
		return this.user.getName();
	}

	public String getPortProxyLink() {
		return this.portProxyLink;
	}

	public static Host of(JsonObject object) {
		GameProfile tempProfile = new GameProfile(UUID.fromString(object.get("owner").getAsString()), object.get("ownerName").getAsString());
		Host host = new Host(tempProfile, object.get("link").getAsString());

		Thread completer = new Thread(() -> {
			host.user = Minecraft.getInstance().getMinecraftSessionService().fillProfileProperties(tempProfile, Boolean.getBoolean("ppconnector.requireSecure"));
		});
		completer.setDaemon(true);
		completer.start();

		return host;
	}
}
