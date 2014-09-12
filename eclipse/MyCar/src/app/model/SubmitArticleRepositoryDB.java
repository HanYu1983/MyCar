package app.model;

import java.sql.PreparedStatement;
import java.sql.Timestamp;

import app.behavior.IInjectionProvider;
import app.behavior.SubmitArticlePO;

public class SubmitArticleRepositoryDB extends SubmitArticleRepository {
	private static final String INSERT_ARTICLE = "insert into SubmitArticle(id, fbid, fbname, photo, comment, submit_date) values (?, ?, ?, ?, ?, ?)";
	
	public SubmitArticleRepositoryDB(IInjectionProvider injectionProvider) {
		super(injectionProvider);
	}

	
	public void Create(String key, SubmitArticlePO po)throws Exception {
		PreparedStatement stat = injectionProvider.getConnectionProvider().getConnection().prepareStatement(INSERT_ARTICLE);
		try{
			stat.setString(1, key);
			stat.setString(2, po.getFbid());
			stat.setString(3, po.getFbname());
			stat.setString(4, po.getPhoto());
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

}
