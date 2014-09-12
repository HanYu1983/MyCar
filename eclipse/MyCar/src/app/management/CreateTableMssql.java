package app.management;

import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.handler.InjectorAction;
import app.tool.DefaultResult;

public class CreateTableMssql extends InjectorAction {

	private static final String CREATE_ADMIN = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE id = object_id(N'[dbo].[Admin]')AND OBJECTPROPERTY(id, N'IsUserTable') = 1) CREATE TABLE [Admin] ( [name] char(10) primary key, pwd char(20) );";
	private static final String CREATE_SUBMITARTICLE = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE id = object_id(N'[dbo].[SubmitArticle]')AND OBJECTPROPERTY(id, N'IsUserTable') = 1) create table SubmitArticle ( id char(50) primary key, comment char(300), fbid char(50), verified bit, fbname nchar(50), submit_date datetime, photo ntext)";
	private static final String CREATE_USERDATA = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE id = object_id(N'[dbo].[UserData]')AND OBJECTPROPERTY(id, N'IsUserTable') = 1) create table UserData ( id char(50) primary key, fbid char(50), gender char(1), phone char(15), email char(30), article_id char(50), name nchar(10), submit_type char(10), submit_date datetime, lucky bit)";
	private static final String CREATE_VOTE = "IF NOT EXISTS (SELECT * FROM sysobjects WHERE id = object_id(N'[dbo].[Vote]')AND OBJECTPROPERTY(id, N'IsUserTable') = 1) create table Vote ( id char(50) primary key, fbid char(50), vote_at datetime, article_id char(50))";
	private static final String INSERT_DEFAULT_ADMIN = "insert into Admin (name, pwd) values ('admin', '1111')";
	
	@Override
	protected boolean shouldConnectDB() {
		return true;
	}
	
	@Override
	protected DefaultResult doTransaction(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Statement stat = super.getConnectionProvider().getConnection().createStatement();
		try{
			stat.execute(CREATE_SUBMITARTICLE);
			stat.execute(CREATE_USERDATA);
			stat.execute(CREATE_VOTE);
			stat.execute(CREATE_ADMIN);
			stat.execute(INSERT_DEFAULT_ADMIN);
			
		}finally{
			try{ stat.close(); }catch(Exception e){}
		}
		return super.doTransaction(request, response);
	}
}
