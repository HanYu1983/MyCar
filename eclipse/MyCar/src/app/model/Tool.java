package app.model;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.iharder.Base64;



public class Tool {
	
	public static String join(String join, String[] strs ){
		if( strs.length == 0 )
			return "";
		else
		{
			StringBuffer sb = new StringBuffer(strs[0]);
			for(String str : strs){
				sb.append(join).append(str);
			}
			return sb.toString();
		}
	}
	
	public static String getServicePath(HttpServletRequest request){
		String contextPath = request.getContextPath();
		String servletPath = request.getServletPath();
		String apiPath = contextPath+servletPath;
		return apiPath;
	}
	
	public static String jsBase64toOriginBase64(String js){
		try{
			return js.split(",")[1];
		}catch(Exception e){
			return js;
		}
	}
	
	public static void serviceImage(HttpServletResponse response,
			String filepath) throws Exception {
		String ext = Tool.getFileExtension(filepath);
		FileInputStream flinp = new FileInputStream(filepath);
		try{
			serviceImage(response, ext, flinp);
		}finally{
			try{ flinp.close(); }catch(Exception e){}
		}
	}
	
	public static void serviceImage(HttpServletResponse response,
			String type, InputStream image) throws Exception {
		response.setContentType("image/" + type);
		ServletOutputStream out;
		out = response.getOutputStream();
		try{
			BufferedInputStream buffinp = new BufferedInputStream(image);
			try{
				BufferedOutputStream buffoup = new BufferedOutputStream(out);
				try{
					int ch = 0;
					while ((ch = buffinp.read()) != -1) {
						buffoup.write(ch);
					}
				}finally{
					try{ buffoup.close(); }catch(Exception e){}
				}
			}finally{
				try{ buffinp.close(); }catch(Exception e){}
			}
		}finally{
			try{ out.close(); }catch(Exception e){}
		}
	}

	public static String getFileExtension(String name) {
		int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return ""; // empty extension
		}
		return name.substring(lastIndexOf + 1);
	}

	private static final DecimalFormat timeFormat4 = new DecimalFormat(
			"0000;0000");

	public static String getSidWithCalendar() {
		Calendar cal = Calendar.getInstance();
		String val = String.valueOf(cal.get(Calendar.YEAR));
		val += timeFormat4.format(cal.get(Calendar.DAY_OF_YEAR));
		val += UUID.randomUUID().toString().replaceAll("-", "");
		return val;
	}

	public static String convertStreamToString(InputStream is)
			throws IOException {
		//
		// To convert the InputStream to String we use the
		// Reader.read(char[] buffer) method. We iterate until the
		// Reader return -1 which means there's no more data to
		// read. We use the StringWriter class to produce the string.
		//
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	public static void writeFile(String filename, InputStream in)
			throws IOException, FileNotFoundException {
		OutputStream out = new FileOutputStream(filename);
		byte[] buffer = new byte[1024];
		int length = -1;
		while ((length = in.read(buffer)) != -1) {
			out.write(buffer, 0, length);
		}
		in.close();
		out.close();
	}

	public static void writeFile(String filename, byte[] bytes)
			throws IOException, FileNotFoundException {
		OutputStream out = new FileOutputStream(filename);
		out.write(bytes);
		out.close();
	}

	public static long calculateDurationToNextDay() {
		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		Date date = new Date();
		date = cal.getTime();

		Date now = new Date();
		long interval = date.getTime() - now.getTime();

		if (interval < 0) {
			cal.add(Calendar.DAY_OF_MONTH, 1);
			date = cal.getTime();
			interval = date.getTime() - now.getTime();
		}
		return interval;
	}

	public static BufferedImage decodeToImage(String imageString)
			throws Exception {
		byte[] imageByte = Base64.decode(imageString);
		return bytes2imageUseIORead(imageByte);
		/*
		byte[] imageByte = Base64.getDecoder().decode(imageString);
		return bytes2imageUseIORead(imageByte);
		*/
	}
	
	public static BufferedImage bytes2imageUseIORead(byte[] imageByte)throws Exception{
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
		try{
			return ImageIO.read(bis);
		}finally{
			try{ bis.close(); }catch(Exception e){}
		}
	}
	
	public static BufferedImage bytes2imageUseImageReader(byte[] imageByte, String format)throws Exception{
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
		try{
			Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(format);
	        ImageReader reader = readers.next();
	        Object source = bis; 
	        ImageInputStream iis = ImageIO.createImageInputStream(source); 
	        try{
	        		reader.setInput(iis, true);
	        		ImageReadParam param = reader.getDefaultReadParam();
	        		return reader.read(0, param);
	        }finally{
	        		try{ iis.close(); }catch(Exception e){}
	        }
		}finally{
			try{ bis.close(); }catch(Exception e){}
		}
	}

	public static byte[] image2bytes(BufferedImage image) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", bos);
		byte[] imageBytes = bos.toByteArray();
		return imageBytes;
	}

	public static String encodeToString(BufferedImage image, String type)
			throws IOException {
		String imageString = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		ImageIO.write(image, type, bos);
		byte[] imageBytes = bos.toByteArray();

		imageString = Base64.encodeBytes(imageBytes);
		bos.close();

		//imageString = Base64.getEncoder().encodeToString(imageBytes);
		return imageString;
	}
	
	public static void makeTrustAllCertSetting() throws Exception{
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs,
					String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs,
					String authType) {
			}
		} };
		// Install the all-trusting trust manager
		final SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}
}
