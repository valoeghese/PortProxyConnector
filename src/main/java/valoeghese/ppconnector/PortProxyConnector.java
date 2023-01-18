package valoeghese.ppconnector;

import net.fabricmc.api.ClientModInitializer;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

public class PortProxyConnector implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		System.out.println("ooga wooga wochooga woochooga");
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