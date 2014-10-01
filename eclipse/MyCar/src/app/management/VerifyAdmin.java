package app.management;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import app.handler.InjectorAction;
import app.model.ShouldBeAdmin;
import app.model.Tool;
import app.tool.DefaultResult;
import app.tool.HttpRequestInfoProvider;
import app.tool.IRequestInfoProvider;
import app.tool.VerifyTool;
import app.tool.VerifyTool.ParamNotNull;

public class VerifyAdmin extends InjectorAction {

	@Override
	protected boolean shouldConnectDB() {
		return true;
	}
	
	@Override
	public boolean isNeedCache(){
		return false;
	}
	
	@Override
	protected DefaultResult doTransaction(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		IRequestInfoProvider infoProvider = new  HttpRequestInfoProvider(request);
		String logout = infoProvider.getParameter("logout");
		HttpSession session = request.getSession(true);
		
		if(logout!= null){
			session.removeAttribute("isAdmin");
			return DefaultResult.Success;
		}
		
		boolean isAdmin = ShouldBeAdmin.isAdmin(session);
		if(isAdmin ){
			// nothing todo
			
		}else{
			if( this.getController().isGet(request) ){
				return writeLoginPage(request, response);
			}
			
			VerifyTool.verify(infoProvider, new ParamNotNull("name"));
			VerifyTool.verify(infoProvider, new ParamNotNull("pwd"));
			
			String name = infoProvider.getParameter("name");
			String pwd = infoProvider.getParameter("pwd");
			
			boolean verified = this.getAdminRepository().verifyName(name, pwd);
			if(!verified){
				return new DefaultResult("pwd not correct");
			}
			session.setAttribute("isAdmin", true);
		}
		
		return writeAdminPage(request, response);
	}
	private static DefaultResult writeLoginPage(HttpServletRequest request, HttpServletResponse response)throws Exception{
		String apiPath = Tool.getServicePath(request);
		
		StringBuffer buf = new StringBuffer();
		buf.append("<html>");
		{
			buf.append("<head>");
			{
				buf.append("<title>Verify Manager</title>");
			}
			buf.append("</head>");
			buf.append("<body>");
			{
				buf.append("<form action='"+apiPath+"' method='POST'>");
				{
					buf.append("<input type='hidden' name='cmd' value='Admin'>");
					buf.append("name:");
					buf.append("<input name='name'><br>");
					buf.append("password:");
					buf.append("<input name='pwd'><br>");
					buf.append("<input type='submit'>");
				}
				buf.append("</form>");
			}
			buf.append("</body>");
		}
		
		response.setContentType("text/html");
		
		PrintWriter out = response.getWriter();
		try{
			out.write(buf.toString());
		}finally{    
			try{ out.close(); }catch(Exception ee){}
		}
		return null;
	}
	
	
	
	private static DefaultResult writeAdminPage(HttpServletRequest request, HttpServletResponse response)throws Exception{
		String apiPath = Tool.getServicePath(request);
		
		StringBuffer buf = new StringBuffer();
		buf.append("<html>");
		{
			buf.append("<head>");
			{
				buf.append("<meta http-equiv='Content-Type' content='text/html'; charset=utf-8/>");
				buf.append("<title>Verify Manager</title>");
			}
			buf.append("</head>");
			buf.append("<body>");
			{
				buf.append("<oi>");
				{
					buf.append("<li><a href='"+apiPath+"?cmd=VerifyManager&page=0&count=10'>審核程式</a></li>");
					buf.append("<li><a href='"+apiPath+"?cmd=LuckyDraw&submitType=submit&count=0'>投稿抽獎程式</a></li>");
					buf.append("<li><a href='"+apiPath+"?cmd=LuckyDraw&submitType=vote&count=0'>投票抽獎程式</a></li>");
				}
				buf.append("</or>");
			}
			buf.append("</body>");
		}
		response.setContentType("html");
		PrintWriter out = response.getWriter();
		try{
			out.write(buf.toString());
		}finally{    
			try{ out.close(); }catch(Exception ee){}
		}
		return null;
	}
}
