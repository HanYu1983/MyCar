package app.behavior;

import java.util.Date;

public class VotePO {
	private String id;
	private String fbid;
	private String articleId;
	private Date voteDate;
	
	public String getFbid() {
		return fbid;
	}
	public void setFbid(String fbid) {
		this.fbid = fbid;
	}
	public String getArticleId() {
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	public Date getVoteDate() {
		return voteDate;
	}
	public void setVoteDate(Date voteDate) {
		this.voteDate = voteDate;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
