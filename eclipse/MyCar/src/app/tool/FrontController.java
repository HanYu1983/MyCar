package app.tool;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.model.Tool;

import com.alibaba.fastjson.JSON;

/**
 * Servlet implementation class FrontController
 */

public class FrontController extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private FrontControllerConfig config;
	private Map<String, ITransaction> actions = new HashMap<String, ITransaction>();

	public FrontControllerConfig getConfig() {
		return config;
	}
	
	public boolean isDebug(){
		String t = this.getServletContext().getInitParameter("Debug");
		if( t == null )
			return true;
		return Boolean.parseBoolean(t);
	}
	
	public boolean isGet(HttpServletRequest request){
		return request.getMethod().equals("GET");
	}
	
	public boolean isPost(HttpServletRequest request){
		return request.getMethod().equals("POST");
	}

	public void setConfig(FrontControllerConfig config) {
		this.config = config;
		prepareTransaction(config);
	}

	private void prepareTransaction(FrontControllerConfig config) {
		actions.clear();
		for (String key : config.getActions().keySet()) {
			String actionClzStr = config.getActions().get(key);
			try {
				Class<?> clz = Class.forName(actionClzStr);
				ITransaction action = (ITransaction) clz.newInstance();
				action.setController(this);
				actions.put(key, action);
			} catch (Exception e) {
				// nothing to do
				log("[FrontController][prepareTransaction]no class:"
						+ actionClzStr);
			}
		}
	}
	
	private void clearTransaction(){
		for(ITransaction action : actions.values()){
			action.onDestoryTransaction();
		}
		actions.clear();
	}

	// init 只會在第一次request呼叫一次
	@Override
	public void init() throws ServletException {
		super.init();

		String configPath = this.getServletConfig().getInitParameter(
				"controller-config-path");
		InputStream is = this.getServletContext().getResourceAsStream(
				configPath);

		try {
			String configStr = Tool.convertStreamToString(is);
			FrontControllerConfig config = JSON.parseObject(configStr,
					FrontControllerConfig.class);
			this.setConfig(config);

		} catch (IOException e) {
			throw new ServletException(e);
		}
	}

	@Override
	public void destroy() {
		clearTransaction();
	}

	// defaultProcess 會在每次request時都呼叫一次，同一物件，由不同的執行緒執行
	protected void defaultProcess(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		try {
			String[] cmdP = request.getParameterValues("cmd");
			boolean hasCmd = cmdP != null && cmdP.length >= 1;
			if (hasCmd) {
				String cmd = cmdP[0];
				boolean hasAction = actions.containsKey(cmd);
				if (hasAction) {
					ITransaction action = actions.get(cmd).prototype();
					DefaultResult result = action.transaction(request, response);
					boolean customContent = result == null;
					if(customContent){
						// nothing todo
					}else{
						result.setDebug(isDebug());
						String jsonStr = JSON.toJSONString(result);
						PrintWriter out = response.getWriter();
						try{
							out.write(jsonStr);
						}finally{
							try{ out.close(); }catch(Exception ee){}
						}
					}
					
				} else {
					throw new Exception("No Action " + cmd);
				}
			} else {
				throw new Exception("No Cmd param");
			}
		} catch (VerifyException e){
			System.out.println(e);
			e.printStackTrace();
			
			DefaultResult result = new DefaultResult(e.getMessage());
			result.setDebug(isDebug());
			String jsonStr = JSON.toJSONString(result);
			PrintWriter out = response.getWriter();
			try{
				out.write(jsonStr);
			}finally{
				try{ out.close(); }catch(Exception ee){}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			DefaultResult result = new DefaultResult(e.getMessage());
			result.setDebug(isDebug());
			String jsonStr = JSON.toJSONString(result);
			PrintWriter out = response.getWriter();
			try{
				out.write(jsonStr);
			}finally{
				try{ out.close(); }catch(Exception ee){}
			}
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		defaultProcess(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		defaultProcess(request, response);
	}

}
