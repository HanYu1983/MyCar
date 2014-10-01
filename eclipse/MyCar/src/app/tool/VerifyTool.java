package app.tool;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;


public class VerifyTool {
	public interface IVerifyFunc {
		void verify(IRequestInfoProvider request)throws VerifyException;
	}

	public static void verify(IRequestInfoProvider request, IVerifyFunc fn)throws VerifyException{
		fn.verify(request);
	}
	
	public static class MethodShouldBePost implements IVerifyFunc {

		
		public void verify(IRequestInfoProvider request) throws VerifyException {
			boolean not = !request.getMethod().equalsIgnoreCase("POST");
			if(not){
				throw new VerifyException("method should be post!");
			}
		}
		
	}

	public static class ParamNotNull implements IVerifyFunc {
		protected String name;

		public ParamNotNull(String name) {
			this.name = name;
		}

		public void verify(IRequestInfoProvider request)throws VerifyException{
			String[] vs = request.getParameterValues(name);
			boolean novalue = vs == null || vs.length < 1;
			if (novalue) {
				throw new VerifyException("No parameter [" + name + "]");
			}
		}
	}
	
	public static class ParamShouldBeNumber extends ParamNotNull {

		public ParamShouldBeNumber(String name) {
			super(name);
		}

		public void verify(IRequestInfoProvider request)throws VerifyException{
			super.verify(request);
			String[] vs = request.getParameterValues(name);
			try{
				for (String v : vs) {
					Float.parseFloat(v);
				}
			}catch(Exception e){
				throw new VerifyException("parameter [" + name + "] is not number");
			}
		}
	}
	
	public static class ParamShouldBeBoolean extends ParamNotNull {

		public ParamShouldBeBoolean(String name) {
			super(name);
		}

		public void verify(IRequestInfoProvider request)throws VerifyException{
			super.verify(request);
			String[] vs = request.getParameterValues(name);
			try{
				for (String v : vs) {
					Boolean.parseBoolean(v);
				}
			}catch(Exception e){
				throw new VerifyException("parameter [" + name + "] is not boolean");
			}
		}
	}
	
	public static class ParamShouldBeValue extends ParamNotNull {
		private String[] values;

		public ParamShouldBeValue(String name, String[] values) {
			super(name);
			this.values = values;
		}

		public void verify(IRequestInfoProvider request)throws VerifyException{
			super.verify(request);
			String[] vs = request.getParameterValues(name);
			for(String v : vs){
				boolean has = false;
				for(String shouldBe : this.values){
					if( shouldBe.equalsIgnoreCase(v) ){
						has = true;
					}
				}
				if(has == false){
					StringBuffer sb = new StringBuffer();
					for(String shouldBe : this.values){
						sb.append(shouldBe+", ");
					}
					sb.setLength(sb.length()-2);
					throw new VerifyException("parameter ["+name+"] should be ["+sb.toString()+"]");
				}
			}
		}
	}
	
	public static class ParamShouldHasOneOf implements IVerifyFunc {
		private Set<String> names;

		public ParamShouldHasOneOf(String[] names) {
			this.names = new HashSet<String>();
			for(String n : names){
				this.names.add(n);
			}
		}

		public void verify(IRequestInfoProvider request)throws VerifyException {
			
			Enumeration<String> pns = request.getParameterNames();
			boolean hasKey = false;
			while(pns.hasMoreElements()){
				String pn = pns.nextElement();
				if(this.names.contains(pn)){
					hasKey = true;
					break;
				}
			}
			if(hasKey == false){
				throw new VerifyException("should at least has one of parameter"+this.names);
			}
			
		}
	}
}
