package app.tool;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import app.model.Tool;


public class FBTool {
	public static String graph_api = "https://graph.facebook.com/v2.1/";
	
	public static String getUser(String fbid, String accessToken) throws IOException {
		URL url = new URL(graph_api + fbid+"/?access_token=" + accessToken);
		return toString(url.openStream(), "iso-8859-1");
	}

	public static String me(String accessToken) throws IOException {
		URL url = new URL(graph_api + "me?access_token=" + accessToken);
		return toString(url.openStream(), "iso-8859-1");
	}

	public static String toString(InputStream is, String encode)
			throws IOException {
		return new String(Tool.readAllBytes(is), encode);
	}
}
