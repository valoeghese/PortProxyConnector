package valoeghese.ppconnector;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PortProxyConnector implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		System.out.println("ooga wooga wochooga woochooga");

		// create folder for icons
		(cacheFile = FabricLoader.getInstance().getGameDir().resolve(".ppconnector").toFile()).mkdir();

		// load data
		hostsFile = FabricLoader.getInstance().getGameDir().resolve("ppconnector.hosts.json").toFile();

		if (hostsFile.exists()) {
			try (Reader reader = new BufferedReader(new FileReader(hostsFile))) {
				JsonArray object = JsonParser.parseReader(reader).getAsJsonArray();

				for (JsonElement element : object) {
					hosts.add(Host.of(element.getAsJsonObject()));
				}
			}
			catch (IOException e) {
				throw new UncheckedIOException("Error loading portproxy hosts", e);
			}
		}
	}

	private static List<Host> hosts = new ArrayList<>();
	private static File hostsFile;
	public static File cacheFile;

	public static void addHost(Host host) {
		hosts.add(host);
	}

	public static void forEachHost(Consumer<Host> hostConsumer) {
		for (Host host : hosts) {
			hostConsumer.accept(host);
		}
	}

	public static boolean saveHosts() {
		// Update this. Yes, if saving hosts fails, it won't be marked for saving, but it doesn't matter too much
		Host.shouldUpdateHosts = false;

		if (!hostsFile.isFile()) {
			try {
				hostsFile.createNewFile();
			}
			catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		// Serialise
		JsonArray object = new JsonArray();

		for (Host host : hosts) {
			object.add(host.serialise());
		}

		// Write
		try (Writer writer = new BufferedWriter(new FileWriter(hostsFile))) {
			writer.write(object.toString());
			return true;
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	// https://www.baeldung.com/java-get-ip-address
	@Nullable
	public static String getPublicIp() {
		try {
			URL url = new URL("http://checkip.amazonaws.com/");

			try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
				return br.readLine();
			}
			catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		catch (MalformedURLException e) {
			throw new RuntimeException("Unless URL formats have changed since January 2023 this error should never happen.", e);
		}
	}
}