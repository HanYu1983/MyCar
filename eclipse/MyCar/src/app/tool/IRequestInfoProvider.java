package app.tool;

import java.io.InputStream;
import java.util.Enumeration;

public interface IRequestInfoProvider {
	InputStream getParameterObject(String key);
	InputStream[] getParameterObjects(String key);
	String getParameter(String key);
	String[] getParameterValues(String key);
	String getMethod();
	Enumeration<String> getParameterNames();
}
