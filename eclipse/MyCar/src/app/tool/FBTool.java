package app.tool;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class FBTool {
	public static String graph_api = "https://graph.facebook.com/v2.1/";
	public static String me(String accessToken) throws IOException{
		URL url = new URL(graph_api+"me?access_token="+accessToken);
		return toString(url.openStream(), "iso-8859-1");
	}
	
	public static String toString(InputStream is, String encode) throws IOException{
		byte[] b = new byte[is.available()];
		is.read(b);
		return new String(b, encode);
	}
}
