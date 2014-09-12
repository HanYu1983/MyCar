package app.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import app.behavior.IInjectionProvider;
import app.behavior.ISubmitArticleRepository;
import app.behavior.SubmitArticlePO;

public class MSSubmitArticleRepository implements ISubmitArticleRepository {
	
	private static final String SELECT_VOTE_TABLE = "select article_id, count(*) as count from Vote group by article_id";
	private static final String ARTICLE_VOTE_TABLE = "SubmitArticle as article left join ("+SELECT_VOTE_TABLE+") as vote on article.id = vote.article_id";
	
	private static final String SELECT_TOP = "with ret as (select article.*, vote.count as vote_count, ROW_NUMBER() over (order by vote.count desc) as row_number from "+ARTICLE_VOTE_TABLE+" where verified = 1) select * from ret where row_number > 0 and row_number <= ?";
	
	private static final String SELECT_ARTICLE_ALL = "with ret as (select article.*, vote.count as vote_count, ROW_NUMBER() over (order by submit_date desc) as row_number from "+ARTICLE_VOTE_TABLE+" where verified = 1) select * from ret where row_number >? and row_number <= ?";
	private static final String SELECT_ARTICLE_ALL_COUNT = "select count(*) as count from SubmitArticle where verified = 1";
	
	private static final String SELECT_ARTICLE_ALL_IGNORE_VERIFIED = "with ret as (select *, 0 as vote_count, ROW_NUMBER() over (order by submit_date desc) as row_number from SubmitArticle) select * from ret where row_number > ? and row_number <= ?";
	private static final String SELECT_ARTICLE_ALL_IGNORE_VERIFIED_COUNT = "select count(*) as count from SubmitArticle";
	
	private static final String SELECT_ARTICLE_BY_FBNAME = "with ret as (select article.*, vote.count as vote_count, ROW_NUMBER() over (order by submit_date desc) as row_number from "+ARTICLE_VOTE_TABLE+" where verified = 1 and fbname like ?) select * from ret where row_number >? and row_number <= ?";
	private static final String SELECT_ARTICLE_BY_FBNAME_COUNT = "select count(*) as count from SubmitArticle where verified = 1 and fbname like ?";
	
	private static final String SELECT_ARTICLE_BY_ID = "select article.*, vote.count as vote_count from "+ARTICLE_VOTE_TABLE+" where id = ?";
	
	private static final String INSERT_ARTICLE = "insert into SubmitArticle(id, fbid, fbname, photo, comment, submit_date) values (?, ?, ?, ?, ?, ?)";
	
	private static final String UPDATE_ARTICLE_VERIFY = "update SubmitArticle set verified = ? where id = ?";
	
	protected IInjectionProvider injectionProvider;

	public MSSubmitArticleRepository(IInjectionProvider injectionProvider) {
		this.injectionProvider = injectionProvider;
	}
	
	protected List<SubmitArticlePO> resultSet2PO(ResultSet rs)throws Exception{
		List<SubmitArticlePO> ret = new ArrayList<SubmitArticlePO>();
		while(rs.next()){
			String id = rs.getString("id");
			String fbid = rs.getString("fbid");
			String fbname = rs.getString("fbname");
			String comment = rs.getString("comment");
			boolean verified = rs.getBoolean("verified");
			java.sql.Timestamp submitDate = rs.getTimestamp("submit_date");
			Integer voteCount = rs.getInt("vote_count");
			String photo = rs.getString("photo");
			
			SubmitArticlePO po = new SubmitArticlePO();
			po.setId(id);
			po.setFbid(fbid);
			po.setFbname(fbname);
			po.setComment(comment);
			po.setVerified(verified);
			if( submitDate != null ){
				po.setSubmitDate(new Date(submitDate.getTime()));
			}
			if( voteCount!= null )
				po.setVoteCount(voteCount);
			po.setPhoto(photo);
			ret.add(po);
		}
		return ret;
	}

	public void Create(String key, SubmitArticlePO po) throws Exception {
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(INSERT_ARTICLE);
		try {
			stat.setString(1, key);
			stat.setString(2, po.getFbid());
			stat.setString(3, po.getFbname());
			stat.setString(4, po.getPhoto());
			stat.setString(5, po.getComment());
			stat.setTimestamp(6, new Timestamp(po.getSubmitDate().getTime()));

			System.out.println(stat);
			stat.execute();

			if (stat.getUpdateCount() == 0) {
				throw new RuntimeException("insert error [" + key + "]");
			}
		} finally {
			try {
				stat.close();
			} catch (Exception e) {
			}
		}

	}

	public SubmitArticlePO Read(String key) throws Exception {
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(SELECT_ARTICLE_BY_ID);
		try{
			stat.setString(1, key);
			
			System.out.println(stat);
			ResultSet rs = stat.executeQuery();
			try{
				List<SubmitArticlePO> list = resultSet2PO(rs);
				return list.isEmpty() ? null : list.get(0);
			}finally{
				try{ rs.close(); }catch(Exception e){}
			}
		}finally{
			try{ stat.close(); }catch(Exception e){}
		}
	}

	public void Update(String key, SubmitArticlePO obj) throws Exception {
		throw new UnsupportedOperationException();
	}

	public void Delete(String key) throws Exception {
		throw new UnsupportedOperationException();
	}

	public Set<String> Keys() {
		throw new UnsupportedOperationException();
	}

	public String getDeskPath(String dbpath) {
		throw new UnsupportedOperationException();
	}

	public void verifyArticle(String articleKey, boolean verified)
			throws Exception {
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(UPDATE_ARTICLE_VERIFY);
		try{
			stat.setBoolean(1, verified);
			stat.setString(2, articleKey);
			
			System.out.println(stat);
			stat.execute();
			
			boolean fail = stat.getUpdateCount() == 0;
			if(fail){
				throw new Exception("no article["+articleKey+"]");
			}
		}finally{
			try{ stat.close(); }catch(Exception e){}
		}
	}

	public List<SubmitArticlePO> getTop(int count) throws Exception {
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(SELECT_TOP);
		try{
			stat.setInt(1, count);
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

	public List<SubmitArticlePO> getByFbid(String fbid, int start, int count)
			throws Exception {
		throw new UnsupportedOperationException();
	}

	public List<SubmitArticlePO> getByFbname(String fbname, int start, int count)
			throws Exception {
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(SELECT_ARTICLE_BY_FBNAME);
		try{
			stat.setString(1, '%'+fbname+'%');
			stat.setInt(2, start);
			stat.setInt(3, start + count);
			
			System.out.println(stat);
			ResultSet rs = stat.executeQuery();
			try{
				List<SubmitArticlePO> list = resultSet2PO(rs);
				return list;
			}finally{
				try{ rs.close(); }catch(Exception e){}
			}
			
		}finally{
			try{ stat.close(); }catch(Exception e){}
		}
	}

	public List<SubmitArticlePO> getAll(int start, int count) throws Exception {
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(SELECT_ARTICLE_ALL);
		try{
			stat.setInt(1, start);
			stat.setInt(2, start + count);
			
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

	public List<SubmitArticlePO> getAllIgnoreVerified(int start, int count)
			throws Exception {
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(SELECT_ARTICLE_ALL_IGNORE_VERIFIED);
		try{
			stat.setInt(1, start);
			stat.setInt(2, start+count);
			
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

	public int getCountByFbid(String fbid) throws Exception {
		throw new UnsupportedOperationException();
	}

	public int getCountByFbname(String fbname) throws Exception {
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(SELECT_ARTICLE_BY_FBNAME_COUNT);
		try{
			stat.setString(1, '%'+fbname+'%');
			
			System.out.println(stat);
			ResultSet rs = stat.executeQuery();
			try{
				rs.next();
				return rs.getInt("count");
			}finally{
				try{ rs.close(); }catch(Exception e){}
			}
			
		}finally{
			try{ stat.close(); }catch(Exception e){}
		}	
	}

	public int getCountByAll() throws Exception {
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(SELECT_ARTICLE_ALL_COUNT);
		try{
			System.out.println(stat);
			ResultSet rs = stat.executeQuery();
			try{
				rs.next();
				return rs.getInt("count");
			}finally{
				try{ rs.close(); }catch(Exception e){}
			}
		}finally{
			try{ stat.close(); }catch(Exception e){}
		}
	}

	public int getCountByAllIgnoreVerified() throws Exception {
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(SELECT_ARTICLE_ALL_IGNORE_VERIFIED_COUNT);
		try{
			
			System.out.println(stat);
			ResultSet rs = stat.executeQuery();
			try{
				rs.next();
				return rs.getInt("count");
			}finally{
				try{ rs.close(); }catch(Exception e){}
			}
		}finally{
			try{ stat.close(); }catch(Exception e){}
		}
	}

}
