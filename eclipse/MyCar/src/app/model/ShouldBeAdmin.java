package app.model;

import javax.servlet.http.HttpSession;

import app.tool.IRequestInfoProvider;
import app.tool.VerifyException;
import app.tool.VerifyTool.IVerifyFunc;

public class ShouldBeAdmin implements IVerifyFunc {
	private HttpSession session;
	public ShouldBeAdmin(HttpSession session){
		this.session = session;
	}
	
	public void verify(IRequestInfoProvider request) throws VerifyException {
		if( isAdmin(session) == false ){
			throw new VerifyException("you are not adminstrator");
		}
	}
	
	public static boolean isAdmin(HttpSession session){
		Boolean isAdmin = (Boolean)session.getAttribute("isAdmin");
		return isAdmin != null && isAdmin.booleanValue();
	}

}
