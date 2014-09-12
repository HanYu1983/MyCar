package app.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import app.behavior.IInjectionProvider;
import app.behavior.IVoteRepository;
import app.behavior.VotePO;

public class MSVoteRepository implements IVoteRepository {
	private static String INSERT_VOTE = "insert into Vote (id, fbid, article_id, vote_at) values (?,?,?,?)";
	private static String SELECT_VOTE_BY_ID = "select * from Vote where id = ?";
	private static String SELECT_VOTE_COUNT_BETWEEN_DATE = "select count(*) from Vote where fbid = ? and ? <= vote_at and ? > vote_at";
	private static String SELECT_VOTE_COUNT_BY_ARTICLE_ID = "select count(*) from Vote where article_id = ?";
	
	private IInjectionProvider injectionProvider;
	
	public MSVoteRepository(IInjectionProvider injectionProvider){
		this.injectionProvider = injectionProvider;
	}

	public synchronized boolean Vote(VotePO vote)throws Exception {
		boolean canVote = isCanVoteToday(vote);
		if( injectionProvider.isDebug() ){
			canVote = true;
		}
		if( canVote ){
			Create(Tool.getSidWithCalendar(), vote);
		}
		return canVote;
	}
	
	private List<VotePO> resultSet2PO(ResultSet rs)throws Exception{
		List<VotePO> ret = new ArrayList<VotePO>();
		while(rs.next()){
			VotePO vote = new VotePO();
			vote.setId(rs.getString("id"));
			vote.setFbid(rs.getString("fbid"));
			vote.setArticleId(rs.getString("article_id"));
			if(rs.getTimestamp("vote_at")!= null)
				vote.setVoteDate(new Date(rs.getTimestamp("vote_at").getTime()));
		}
		return ret;
	}

	public void Create(String key, VotePO obj) throws Exception {
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(INSERT_VOTE);
		try{
			stat.setString(1, key);
			stat.setString(2, obj.getFbid());
			stat.setString(3, obj.getArticleId());
			stat.setTimestamp(4, new Timestamp(obj.getVoteDate().getTime()));
			
			System.out.println(stat);
			stat.execute();
			if( stat.getUpdateCount() == 0 ){
				throw new RuntimeException("insert error ["+key+"]");
			}
		}finally{
			try{ stat.close(); }catch(Exception e){}
		}
	}

	public VotePO Read(String key) throws Exception {
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(SELECT_VOTE_BY_ID);
		try{
			stat.setString(1, key);
			
			System.out.println(stat);
			ResultSet rs = stat.executeQuery();
			try{
				List<VotePO> pos = resultSet2PO(rs);
				if(pos.isEmpty())
					return null;
				else
					return pos.get(0);
			}finally{
				try{ rs.close(); }catch(Exception e){}
			}
		}finally{
			try{ stat.close(); }catch(Exception e){}
		}	
	}

	public void Update(String key, VotePO obj) throws Exception {
		throw new UnsupportedOperationException();

	}

	public void Delete(String key) throws Exception {
		throw new UnsupportedOperationException();

	}

	public Set<String> Keys() {
		return null;
	}

	public boolean isCanVoteToday(VotePO vote) throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date nextDay = cal.getTime();
		
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(SELECT_VOTE_COUNT_BETWEEN_DATE);
		try{
			stat.setString(1, vote.getFbid());
			stat.setDate(2, new java.sql.Date(new Date().getTime()));
			stat.setDate(3, new java.sql.Date(nextDay.getTime()));
			
			System.out.println(stat);
			ResultSet rs = stat.executeQuery();
			try{
				rs.next();
				boolean hasVoted = rs.getInt(1) > 0;
				return hasVoted == false;
			}finally{
				try{ rs.close(); }catch(Exception e){}
			}
		}finally{
			try{ stat.close(); }catch(Exception e){}
		}
		
	}

	public int getVoteCount(String articleId) throws Exception {
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(SELECT_VOTE_COUNT_BY_ARTICLE_ID);
		try{
			stat.setString(1, articleId);
			
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
}
