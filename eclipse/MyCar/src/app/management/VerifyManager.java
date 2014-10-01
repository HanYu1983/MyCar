package app.management;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.behavior.SubmitArticlePO;
import app.handler.InjectorAction;
import app.model.ShouldBeAdmin;
import app.model.Tool;
import app.tool.DefaultResult;
import app.tool.HttpRequestInfoProvider;
import app.tool.IRequestInfoProvider;
import app.tool.VerifyTool;
import app.tool.VerifyTool.ParamShouldBeBoolean;
import app.tool.VerifyTool.ParamShouldBeNumber;

public class VerifyManager extends InjectorAction {

	@Override
	protected boolean shouldConnectDB() {
		return true;
	}
	
	@Override
	public boolean isNeedCache(){
		return false;
	}
	
	private static int nextPage(int curr, int offset){
		int ret = curr + offset;
		if( ret <0 )
			ret = 0;
		return ret;
	}

	@Override
	protected DefaultResult doTransaction(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		IRequestInfoProvider infoProvider = new  HttpRequestInfoProvider(request);
		VerifyTool.verify(infoProvider, new ShouldBeAdmin(request.getSession(true)));
		VerifyTool.verify(infoProvider, new ParamShouldBeNumber("page"));
		VerifyTool.verify(infoProvider, new ParamShouldBeNumber("count"));

		int page = Integer.parseInt(infoProvider.getParameter("page"));
		int count = Integer.parseInt(infoProvider.getParameter("count"));
		String articleId = infoProvider.getParameter("articleId");
		boolean shouldVerify = articleId != null;
		if( shouldVerify ){
			VerifyTool.verify(infoProvider, new ParamShouldBeBoolean("verified"));
			boolean verified = Boolean.parseBoolean(infoProvider.getParameter("verified"));
			super.getSubmitArticleRepository().verifyArticle(articleId, verified);
		}
		
		String apiPath = Tool.getServicePath(request);
		
		int curr = page* count;
		int total = this.getSubmitArticleRepository().getCountByAllIgnoreVerified();
		
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
				buf.append("<a href='"+apiPath+"?cmd=Admin'>管理後台</a><br>");
				
				buf.append("<a href='"+apiPath+"?cmd=VerifyManager&page="+nextPage(page, -1)+"&count="+count+"'> << </a>");
				buf.append("<a href='"+apiPath+"?cmd=VerifyManager&page="+nextPage(page, 1)+"&count="+count+"'> >> </a>");
				buf.append(curr + "/"+total+"<br>");
				buf.append("<table border=1 align=center>");
				{
					
					buf.append("<tr>");
					{
						buf.append("<td align=center>verified</td>");
						buf.append("<td align=center>photo</td>");
						buf.append("<td align=center>id</td>");
						buf.append("<td align=center>date</td>");
						buf.append("<td align=center>fbid</td>");
						buf.append("<td align=center>fbname</td>");
					}
					buf.append("</tr>");
					
					List<SubmitArticlePO> pos = super.getSubmitArticleRepository().getAllIgnoreVerified( curr, count );
					for (SubmitArticlePO po : pos) {
						writeRow(buf, po, apiPath, page, count);
					}
				}
				buf.append("</table>");
				buf.append("<a href='"+apiPath+"?cmd=VerifyManager&page="+nextPage(page, -1)+"&count="+count+"'> << </a>");
				buf.append("<a href='"+apiPath+"?cmd=VerifyManager&page="+nextPage(page, 1)+"&count="+count+"'> >> </a>");
				buf.append(curr + "/"+total+"<br>");
			}
			buf.append("</body>");
		}
		buf.append("</html>");

		response.setContentType("text/html");
		
		PrintWriter out = response.getWriter();
		try{
			out.write(buf.toString());
		}finally{    
			try{ out.close(); }catch(Exception ee){}
		}
		return null;
	}
	
	private void writeRow(StringBuffer buf, SubmitArticlePO po, String apiPath, int page, int count){
		buf.append("<tr>");
		{
			buf.append("<td rowspan=2 align=center><a href='"+apiPath+"?cmd=VerifyManager&page="+page+"&count="+count+"&articleId="+po.getId()+"&verified="+(po.isVerified()?"false":"true")+"'>"+(po.isVerified()?"O":"X")+"</a></td>");
			buf.append("<td rowspan=2 align=center><a target=_blank href='"+apiPath+"?cmd=GetImage&articleId="+po.getId()+"'><img width='100' height='100' src='"+apiPath+"?cmd=GetImage&outputType=100&articleId="+po.getId()+"'></img></a></td>");
			buf.append("<td align=center>"+po.getId()+"</td>");
			buf.append("<td align=center>"+po.getSubmitDate()+"</td>");
			buf.append("<td align=center>"+po.getFbid()+"</td>");
			buf.append("<td align=center>"+po.getFbname()+"</td>");
		}
		buf.append("</tr>");
		buf.append("<tr>");
		{
			buf.append("<td colspan=5>"+po.getComment()+"</td>");
		}
		buf.append("</tr>");
	}
}
