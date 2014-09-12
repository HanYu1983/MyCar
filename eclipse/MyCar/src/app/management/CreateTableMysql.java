package app.management;

import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.handler.InjectorAction;
import app.tool.DefaultResult;

public class CreateTableMysql extends InjectorAction {

	private static final String CREATE_SUBMITARTICLE = "create table if not exists SubmitArticle ( id char(50) primary key, comment text character set utf8, fbid char(50), verified bit(1), fbname char(50) character set utf8, submit_date datetime, photo longtext)";
	private static final String CREATE_USERDATA = "create table if not exists UserData ( id char(50) primary key, fbid char(50), gender char(1), phone char(15), email char(30), article_id char(50), name char(10) character set utf8, submit_type char(10), submit_date datetime, lucky bit(1) )";
	private static final String CREATE_VOTE = "create table if not exists Vote ( id char(50) primary key, fbid char(50), vote_at datetime, article_id char(50));";
	private static final String CREATE_ADMIN = "create table if not exists Admin ( name char(10) primary key, pwd char(20) );";
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
