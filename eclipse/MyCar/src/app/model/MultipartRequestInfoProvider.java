package app.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.opengion.hayabusa.servlet.multipart.FilePart;
import org.opengion.hayabusa.servlet.multipart.MultipartParser;
import org.opengion.hayabusa.servlet.multipart.ParamPart;
import org.opengion.hayabusa.servlet.multipart.Part;

import app.tool.IRequestInfoProvider;

public class MultipartRequestInfoProvider implements IRequestInfoProvider {
	private HttpServletRequest request;
	private List<Object[]> parts = new ArrayList<Object[]>();
	
	public MultipartRequestInfoProvider(HttpServletRequest request, int size) throws IOException{
		this.request = request;
		MultipartParser parser = new MultipartParser(request, size);
		while(true){
			Part part = parser.readNextPart();
			if(part==null)
				break;
			if( part instanceof ParamPart){
				ParamPart pp = (ParamPart)part;
				parts.add(new Object[]{pp.getName(), pp.getStringValue()});
				
			}else if( part instanceof FilePart){
				FilePart fp = (FilePart)part;
				InputStream is = fp.getInputStream();
				try {
					byte[] b = Tool.readAllBytes(is);
					parts.add(new Object[]{fp.getName(), b});
				} catch (Exception e1) {
					System.out.println(e1);
				} finally{
					try{ is.close(); }catch(Exception e){}
				}
				
			}
		}
	}
	public InputStream getParameterObject(String key) {
		InputStream[] ret = getParameterObjects(key);
		if( ret == null )
			return null;
		else
			return ret[0];
	}
	public InputStream[] getParameterObjects(String key){
		List<InputStream> list = new ArrayList<InputStream>();
		try{
			for(Object[] part : parts){
				String name = (String)part[0];
				
				if( name.equalsIgnoreCase(key) ){
					byte[] b = (byte[])part[1];
					list.add(new ByteArrayInputStream(b)); 
				}
			}
			if(list.isEmpty())
				return null;
			else
				return list.toArray(new InputStream[list.size()]);
		}catch(Exception e){
			System.out.println(e);
		}
		return null;
	}
	
	public String getParameter(String key) {
		String[] ret = getParameterValues(key);
		if( ret == null )
			return null;
		else
			return ret[0];
	}

	public String[] getParameterValues(String key) {
		List<String> list = new ArrayList<String>();
		try{
			for(Object[] part : parts){
				String name = (String)part[0];
				
				if( name.equalsIgnoreCase(key) ){
					list.add(part[1].toString());
				}
				
			}
			if(list.isEmpty())
				return null;
			else
				return list.toArray(new String[list.size()]);
		}catch(Exception e){
			System.out.println(e);
		}
		return null;
	}

	public String getMethod() {
		return request.getMethod();
	}

	public Enumeration<String> getParameterNames() {
		List<String> list = new ArrayList<String>();
		try{
			for(Object[] part : parts){
				String name = (String)part[0];
				list.add(name);
			}
			return Collections.enumeration(list);
		}catch(Exception e){
			System.out.println(e);
		}
		return  Collections.enumeration(new ArrayList<String>());
	}

}
