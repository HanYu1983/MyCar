package app.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;

import app.behavior.AdminPO;
import app.behavior.IAdminRepository;
import app.behavior.IInjectionProvider;

public class AdminRepository implements IAdminRepository {

	private static final String SELECT_COUNT_BY_NAME_PWD = "select count(*) as count from Admin where name = ? and pwd = ?";
	
	protected IInjectionProvider injectionProvider;
	
	public AdminRepository(IInjectionProvider injectionProvider){
		this.injectionProvider = injectionProvider;
	}
	
	
	public void Create(String key, AdminPO obj) throws Exception {
		throw new UnsupportedOperationException();

	}

	
	public AdminPO Read(String key) throws Exception {
		throw new UnsupportedOperationException();
		
	}

	
	public void Update(String key, AdminPO obj) throws Exception {
		throw new UnsupportedOperationException();

	}

	
	public void Delete(String key) throws Exception {
		throw new UnsupportedOperationException();

	}

	
	public Set<String> Keys() {
		throw new UnsupportedOperationException();
		
	}

	
	public boolean verifyName(String name, String pwd)throws Exception {
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(SELECT_COUNT_BY_NAME_PWD);
		try{
			stat.setString(1, name);
			stat.setString(2, pwd);
			
			System.out.println(stat);
			ResultSet rs = stat.executeQuery();
			try{
				rs.next();
				return rs.getInt("count") == 1;
			}finally{
				try{ rs.close(); }catch(Exception e){}
			}
		}finally{
			try{ stat.close(); }catch(Exception e){}
		}
	}

}
