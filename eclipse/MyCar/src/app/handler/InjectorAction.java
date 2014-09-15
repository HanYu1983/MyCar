package app.handler;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import app.behavior.IAdminRepository;
import app.behavior.IInjectionProvider;
import app.behavior.ISubmitArticleRepository;
import app.behavior.IUserDataRepository;
import app.behavior.IVoteRepository;
import app.model.AdminRepository;
import app.model.MSSubmitArticleRepository;
import app.model.MSUserDataRepository;
import app.model.MSVoteRepository;
import app.model.SubmitArticleRepositoryDB;
import app.model.UserDataRepository;
import app.model.VoteRepository;
import app.tool.DefaultResult;
import app.tool.FBTool;
import app.tool.FrontController;
import app.tool.IConnectionProvider;
import app.tool.IConnectionProvider.ThreadLocalConnectionProvider;
import app.tool.ITransaction;
import app.tool.ITransaction.DefaultAction;

import com.alibaba.fastjson.JSONObject;

public class InjectorAction extends DefaultAction implements IInjectionProvider{

	private ISubmitArticleRepository submitArticleRepository;
	private IVoteRepository voteRepository;
	private IUserDataRepository userDataRepository;
	private IAdminRepository adminRepository;
	private ThreadLocalConnectionProvider localConnectionProvider;
	
	@Override
	public void setController(FrontController controller){
		super.setController(controller);
		String fbGraphApi = this.getController().getConfig().getCustom().getString("fb-graphapi");
		FBTool.graph_api = fbGraphApi;
		
		localConnectionProvider = new ThreadLocalConnectionProvider(null);
		adminRepository = new AdminRepository( this );
		
		String db = this.getController().getConfig().getCustom().getString("db");
		if( db.equalsIgnoreCase("mssql") ){
			voteRepository = new MSVoteRepository(this);
			userDataRepository = new MSUserDataRepository(this);
			submitArticleRepository = new MSSubmitArticleRepository( this );
		}else{
			voteRepository = new VoteRepository(this);
			userDataRepository = new UserDataRepository(this);
			submitArticleRepository = new SubmitArticleRepositoryDB( this );
		}
	}
	
	// 覆寫它確保子類clone的安全性
	@Override
	public Object clone() throws CloneNotSupportedException {
		InjectorAction clone = (InjectorAction)super.clone();
		// deep copy
		clone.setController(this.getController());
		return clone;
	}
	
	// 所有action都是單例。盡可能的節省記憶體
	@Override
	public ITransaction prototype(){
		return this;
	}
	
	@Override
	public void onDestoryTransaction() {
		localConnectionProvider = null;
		userDataRepository = null;
		voteRepository = null;
		submitArticleRepository = null;
		super.onDestoryTransaction();
	}
	
	@Override
	public final DefaultResult transaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		super.transaction(request, response);
		
		Connection conn = prepareConnection();
		localConnectionProvider.setConnection(conn);
		
		if (conn != null) {
			conn.setAutoCommit(false);
		}
		
		try {
			return this.doTransaction(request, response);
		} catch (Exception e) {
			if( conn != null ){
				conn.rollback();
			}
			throw e;
		} finally {
			localConnectionProvider.setConnection(null);
			if( conn != null ){
				conn.setAutoCommit(true);
				conn.close();
				conn = null;
			}
		}
	}

	protected boolean shouldConnectDB() {
		return false;
	}

	protected DefaultResult doTransaction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return DefaultResult.Success;
	}
	
	private Connection prepareConnection()throws Exception{
		if (shouldConnectDB()) {
			JSONObject config = this.getController().getConfig().getCustom();
			String connectionType = config.getString("connection-type");
			if( connectionType.equalsIgnoreCase("jndi") ){
				InitialContext envContext = new InitialContext();
				Context initContext = (Context) envContext.lookup("java:/comp/env");
				DataSource datasource = (DataSource) initContext.lookup(config.getString("connection-jndi"));
				return datasource.getConnection();
			}else{
				JSONObject manualConfig = config.getJSONObject("connection-manual");
				String driverClassName = manualConfig.getString("driverClassName");
				String url = manualConfig.getString("url");
				String username = manualConfig.getString("username");
				String password = manualConfig.getString("password");
				
				Class.forName(driverClassName);
				Connection con = DriverManager.getConnection(url, username, password);
				return con;
			}
		}
		return null;
	}

	public ISubmitArticleRepository getSubmitArticleRepository() {
		return submitArticleRepository;
	}

	public void setUploadedImageRepository(
			ISubmitArticleRepository uploadedImageRepository) {
		this.submitArticleRepository = uploadedImageRepository;
	}

	public IVoteRepository getVoteRepository() {
		return voteRepository;
	}

	public IUserDataRepository getUserDataRepository() {
		return userDataRepository;
	}

	public IConnectionProvider getConnectionProvider() {
		return localConnectionProvider;
	}

	
	public IAdminRepository getAdminRepository() {
		return adminRepository;
	}

	
	public boolean isDebug() {
		return this.getController().isDebug();
	}
}
