package app.management;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.behavior.UserDataPO;
import app.handler.InjectorAction;
import app.model.ShouldBeAdmin;
import app.model.Tool;
import app.tool.DefaultResult;
import app.tool.HttpRequestInfoProvider;
import app.tool.IRequestInfoProvider;
import app.tool.VerifyTool;
import app.tool.VerifyTool.ParamShouldBeNumber;
import app.tool.VerifyTool.ParamShouldBeValue;

public class LuckyDraw extends InjectorAction {

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
		VerifyTool.verify(infoProvider, new ShouldBeAdmin(request.getSession(true)));
		VerifyTool.verify(infoProvider, new ParamShouldBeValue("submitType", new String[]{"submit", "vote"}));
		VerifyTool.verify(infoProvider, new ParamShouldBeNumber("count"));
		
		String submitType = request.getParameter("submitType");
		int count = Math.max(0,Integer.parseInt(request.getParameter("count")));
		
		String apiPath = Tool.getServicePath(request);
		
		int total = this.getUserDataRepository().getCountByAll(submitType);		
		List<UserDataPO> pos = this.getUserDataRepository().markLucky(submitType, count);
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("<html>");
		{
			buf.append("<head>");
			{
				buf.append("<meta http-equiv='Content-Type' content='text/html'; charset=utf-8/>");
				buf.append("<title>Lucky Draw</title>");
			}
			buf.append("</head>");
			buf.append("<body>");
			{
				buf.append("<a href='"+apiPath+"?cmd=Admin'>管理後台</a><br>");

				buf.append("<h2>");
				buf.append("共"+total+"筆抽獎資料</br>");
				buf.append("<a href='"+apiPath+"?cmd=LuckyDraw&submitType="+submitType+"&count=25'>抽獎</a>");
				buf.append("</h2>");
				
				buf.append("<table border=1 align=center>");
				{
					
					buf.append("<tr>");
					{
						buf.append("<td rowspan=2 align=center>photo</td>");
						buf.append("<td align=center>id</td>");
						buf.append("<td align=center>date</td>");
						buf.append("<td align=center>name</td>");
					}
					buf.append("</tr>");
					buf.append("<tr>");
					{
						buf.append("<td align=center>gender</td>");
						buf.append("<td align=center>phone number</td>");
						buf.append("<td align=center>email</td>");
					}
					buf.append("</tr>");
					for (UserDataPO po : pos) {
						writeRow(buf, po, apiPath);
					}
				}
				buf.append("</table>");
			}
			buf.append("</body>");
		}
		buf.append("</html>");
		
		response.setContentType("html");

		PrintWriter out = response.getWriter();
		try{
			out.write(buf.toString());
		}finally{
			out.flush();
			try{ out.close(); }catch(Exception ee){}
		}
		return null;
	}
	
	private void writeRow(StringBuffer buf, UserDataPO po, String apiPath){
		buf.append("<tr>");
		{
			buf.append("<td rowspan=2 align=center><a target=_blank href='"+apiPath+"?cmd=GetImage&articleId="+po.getArticleId()+"'><img width='100' height='100' src='"+apiPath+"?cmd=GetImage&outputType=100&articleId="+po.getArticleId()+"'></img></a></td>");
			buf.append("<td align=center>"+po.getFbid()+"</td>");
			buf.append("<td align=center>"+po.getSubimtDate()+"</td>");
			buf.append("<td align=center>"+po.getName()+"</td>");
		}
		buf.append("</tr>");
		buf.append("<tr>");
		{
			buf.append("<td align=center>"+po.getGender()+"</td>");
			buf.append("<td align=center>"+po.getPhone()+"</td>");
			buf.append("<td align=center>"+po.getEmail()+"</td>");
		}
		buf.append("</tr>");
	}
}
