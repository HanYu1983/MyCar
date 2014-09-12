package app.model;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.behavior.IInjectionProvider;
import app.behavior.SubmitArticlePO;

/**
 * @deprecated 不存圖片到硬碟了。因為websphere說不定會有限制取存
 * @author hanyu
 *
 */
public class SubmitArticleRepositoryDesk extends SubmitArticleRepository {

	private static final String INSERT_ARTICLE = "insert into SubmitArticle(id, fbid, fbname, filepath, comment, submit_date) values (?, ?, ?, ?, ?, ?)";
	private String deskPath;
	
	public SubmitArticleRepositoryDesk(IInjectionProvider injectionProvider,
			String deskPath) {
		super(injectionProvider);
		this.deskPath = deskPath;
	}
	
	public String getDeskPath() {
		return deskPath;
	}

	public void setDeskPath(String deskPath) {
		this.deskPath = deskPath;
	}
	
	private void makeDeskDir(String fbid){
		File dir = new File(deskPath + fbid);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public String getDeskPath(String dbpath) {
		String output =deskPath + File.separator + dbpath;
		return output;
	}
	
	private String getDBPath(SubmitArticlePO obj){
		String dbpath = obj.getFbid()+File.separator+obj.getFilename();
		return dbpath;
	}
	
	
	public void Create(String key, SubmitArticlePO obj) {
		makeDeskDir(obj.getFbid());
		String dbpath = getDBPath(obj);
		
		try {
			writeToFile(dbpath, obj.getImage());
			writeToDB(key, dbpath, obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
			
		}
	}
	
	private void writeToFile(String dbpath, byte[] image)throws Exception{
		String output = getDeskPath(dbpath);
		Tool.writeFile(output, image);
	}
	
	private void writeToDB(String key, String dbpath, SubmitArticlePO po)throws Exception{
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(INSERT_ARTICLE);
		try{
			stat.setString(1, key);
			stat.setString(2, po.getFbid());
			stat.setString(3, po.getFbname());
			stat.setString(4, dbpath);
			stat.setString(5, po.getComment());
			stat.setTimestamp(6, new Timestamp(po.getSubmitDate().getTime()));
			
			System.out.println(stat);
			stat.execute();
			
			if( stat.getUpdateCount() == 0 ){
				throw new RuntimeException("insert error ["+key+"]");
			}
		}finally{
			try{ stat.close(); }catch(Exception e){}
		}
	}
	
	@Override
	protected List<SubmitArticlePO> resultSet2PO(ResultSet rs)throws Exception{
		List<SubmitArticlePO> ret = new ArrayList<SubmitArticlePO>();
		while(rs.next()){
			String id = rs.getString("id");
			String fbid = rs.getString("fbid");
			String fbname = rs.getString("fbname");
			String filepath = rs.getString("filepath");
			String comment = rs.getString("comment");
			boolean verified = rs.getBoolean("verified");
			java.sql.Timestamp submitDate = rs.getTimestamp("submit_date");
			int voteCount = rs.getInt("vote_count");
			String photo = rs.getString("photo");
			
			SubmitArticlePO po = new SubmitArticlePO();
			po.setId(id);
			po.setFbid(fbid);
			po.setFbname(fbname);
			po.setFilename(filepath);
			po.setComment(comment);
			po.setVerified(verified);
			if( submitDate != null ){
				po.setSubmitDate(new Date(submitDate.getTime()));
			}
			po.setVoteCount(voteCount);
			po.setPhoto(photo);
			ret.add(po);
		}
		return ret;
	}

}
