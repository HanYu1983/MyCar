package app.tool;

import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

public class HttpRequestInfoProvider implements IRequestInfoProvider {
	private HttpServletRequest request;
	public HttpRequestInfoProvider(HttpServletRequest request){
		this.request = request;
	}
	public String getParameter(String key) {
		return request.getParameter(key);
	}

	public String[] getParameterValues(String key) {
		return request.getParameterValues(key);
	}

	public String getMethod() {
		return request.getMethod();
	}

	@SuppressWarnings("unchecked")
	public Enumeration<String> getParameterNames() {
		return request.getParameterNames();
	}
	public InputStream getParameterObject(String key) {
		throw new UnsupportedOperationException();
	}
	public InputStream[] getParameterObjects(String key) {
		throw new UnsupportedOperationException();
	}

}
