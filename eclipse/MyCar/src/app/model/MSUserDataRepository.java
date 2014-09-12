package app.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import app.behavior.IInjectionProvider;
import app.behavior.IUserDataRepository;
import app.behavior.UserDataPO;

public class MSUserDataRepository implements IUserDataRepository {

	private static String VERIFY_TABLE = "UserData as [user] left join SubmitArticle as article on [user].article_id = article.id";
	
	private static String INSERT_USER = "insert into UserData (id, name, gender, phone, email, article_id, submit_type, submit_date, fbid) values (?,?,?,?,?,?,?,?,?)";
	private static String SELECT_USER_BY_ARTICLE_ID = "select * from UserData where article_id = ?";
	private static String SELECT_COUNT_BY_ALL = "select count(*) as count from ("+VERIFY_TABLE+") where verified = 1 and submit_type = ?";
	private static String SELECT_ALL = "with ret as (select [user].*, ROW_NUMBER() over (order by [user].submit_date desc) as row_number from ("+VERIFY_TABLE+") where verified = 1 and submit_type = ?) select * from ret where row_number > ? and row_number <= ?";
	
	private IInjectionProvider injectionProvider;
	
	public MSUserDataRepository(IInjectionProvider injectionProvider){
		this.injectionProvider = injectionProvider;
	}

	protected List<UserDataPO> resultSet2PO(ResultSet rs)throws Exception{
		List<UserDataPO> ret = new ArrayList<UserDataPO>();
		while(rs.next()){
			UserDataPO po = new UserDataPO();
			po.setId(rs.getString("id"));
			po.setName(rs.getString("name"));
			if(rs.getString("gender")!=null)
				po.setGender(rs.getString("gender").charAt(0));
			po.setPhone(rs.getString("phone"));
			po.setEmail(rs.getString("email"));
			po.setArticleId(rs.getString("article_id"));
			po.setSubmitType(rs.getString("submit_type"));
			if( rs.getTimestamp("submit_date") != null )
				po.setSubimtDate(new Date(rs.getTimestamp("submit_date").getTime()));
			po.setFbid(rs.getString("fbid"));
			ret.add(po);
		}
		return ret;
	}
	
	public void Create(String key, UserDataPO obj) throws Exception {
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(INSERT_USER);
		try{
			stat.setString(1, key);
			stat.setString(2, obj.getName());
			stat.setString(3, obj.getGender()+"");
			stat.setString(4, obj.getPhone());
			stat.setString(5, obj.getEmail());
			stat.setString(6, obj.getArticleId());
			stat.setString(7, obj.getSubmitType());
			stat.setTimestamp(8, new Timestamp(obj.getSubimtDate().getTime()));
			stat.setString(9, obj.getFbid());
			System.out.println(stat);
			stat.execute();
			
			if(stat.getUpdateCount()==0){
				throw new RuntimeException("insert error ["+key+"]");
			}
		}finally{
			try{ stat.close(); }catch(Exception e){}
		}
	}

	
	public UserDataPO Read(String key) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void Update(String key, UserDataPO obj) throws Exception {
		throw new UnsupportedOperationException();

	}

	
	public void Delete(String key) throws Exception {
		throw new UnsupportedOperationException();

	}

	
	public Set<String> Keys() {
		throw new UnsupportedOperationException();
	}

	
	public int getCountByAll(String type) throws Exception {
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(SELECT_COUNT_BY_ALL);
		try{
			stat.setString(1, type);
			
			System.out.println(stat);
			ResultSet rs = stat.executeQuery();
			try{
				rs.next();
				return rs.getInt(1);
			}finally{
				try{ rs.close(); }catch(Exception e){}
			}
		}finally{
			try{ stat.close(); }catch(Exception e){}
		}
	}

	
	public List<UserDataPO> getAll(String type, int start, int count)
			throws Exception {
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(SELECT_ALL);
		try{
			stat.setString(1, type);
			stat.setInt(2, start);
			stat.setInt(3, start + count);
			
			System.out.println(stat);
			ResultSet rs = stat.executeQuery();
			try{
				return resultSet2PO(rs);
			}finally{
				try{ rs.close(); }catch(Exception e){}
			}
		}finally{
			try{ stat.close(); }catch(Exception e){}
		}
	}

	
	public UserDataPO getUserDataByArticleId(String articleId) throws Exception {
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(SELECT_USER_BY_ARTICLE_ID);
		try{
			stat.setString(1, articleId);
			
			System.out.println(stat);
			ResultSet rs = stat.executeQuery();
			try{
				List<UserDataPO> pos = resultSet2PO(rs);
				return pos.isEmpty() ? null : pos.get(0);
			}finally{
				try{ rs.close(); }catch(Exception e){}
			}
		}finally{
			try{ stat.close(); }catch(Exception e){}
		}
	}

	
	public List<UserDataPO> markLucky(String submitType, int count) throws Exception {
		List<String> fbids = new ArrayList<String>();
		List<String> ids = new ArrayList<String>();
		Statement stat = injectionProvider.getConnectionProvider().getConnection().createStatement();
		
		try{		
			String sql;
			
			if(count <= 0){
				sql = "select * from UserData where lucky = 1 and submit_type = '"+submitType+"'";
				System.out.println(sql);
				ResultSet rs = stat.executeQuery(sql);
				try{
					return this.resultSet2PO(rs);
				}finally{
					try{ rs.close(); }catch(Exception e){}
				}
			}
			
			while(true){
				sql = "select top 1 * from UserData where article_id in (select id from SubmitArticle where verified = 1) and fbid not in ('"+Tool.join("','", fbids.toArray(new String[fbids.size()]))+"') and submit_type = '"+submitType+"' order by newid()";

				System.out.println(sql);
				ResultSet rs = stat.executeQuery(sql);
				try{
					List<UserDataPO> users = this.resultSet2PO(rs);
					if( users.isEmpty() )
						break;
					UserDataPO user = users.get(0);
					if( user.getFbid() != null )
						fbids.add(user.getFbid());
					ids.add(user.getId());
					if( ids.size() >= count )
						break;
				}finally{
					try{ rs.close(); }catch(Exception e){}
				}
			}
			sql = "update UserData set lucky = NULL where submit_type = '"+ submitType +"' and lucky = 1";
			
			System.out.println(sql);
			stat.addBatch(sql);
			
			sql = "update UserData set lucky = 1 where id in ('"+ Tool.join("','", ids.toArray(new String[ids.size()])) +"')";
		
			System.out.println(sql);
			stat.addBatch(sql);
			
			stat.executeBatch();
			
			sql = "select * from UserData where lucky = 1 and submit_type = '"+submitType+"'";
			System.out.println(sql);
			ResultSet rs = stat.executeQuery(sql);
			try{
				return this.resultSet2PO(rs);
			}finally{
				try{ rs.close(); }catch(Exception e){}
			}
		}finally{
			try{ stat.close(); }catch(Exception e){}
		}
	}

}
