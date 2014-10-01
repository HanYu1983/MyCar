$(function() {
	window.app.voteInfoOpen = open;
	
	console.log('vote info xxxx')
	
	function open( options ){
		var articleId = options.articleId;
		var type = options.type;
		var isFBSharing = false;
		var isVoting = false;
		var allController = pages.allController( $('body' ));
		var voteInfo = pages.voteInfoController( $("#inline2") );
		voteInfo.removeListener();
		voteInfo.addCloseClick( options.onCloseClick );
		
		if( articleId == null )
			return;
		
		if( window.app.local ){
			if( type == 'sample' ){
				voteInfo.hideVoteButton()
			}
		}else{
			serverapi.getArticle({
				articleId: articleId
			},{
				success: function(data){
					if(data.success){
						var fbid =  data.info.fbid
						var fbname = data.info.fbname
						var voteCount = data.info.voteCount
						var comment = data.info.comment
						
						if( type == 'sample' ){
							voteInfo.hideVoteButton()
						}
						
						voteInfo.setPhoto(serverapi.getImageUrl({articleId:articleId, outputType:"site"}))
						voteInfo.setName(fbname);
						voteInfo.setVoteCount(voteCount);
						voteInfo.setContent(comment)
						voteInfo.addVoteClick(function() {
							if( isVoting ){
								alert( window.app.info.waitForVote );
								return;
							}
							isVoting = true;
							allController.openLoading();
							
							vic.facebook.login( function( fbres ){
								var token = fbres.accessToken;
								var fbid = fbres.userID;
								serverapi.vote({
									accessToken: token,
									fbid: fbid,
									articleId:articleId
								},{
									success:function(data){
										isVoting = false;
										allController.closeLoading();
										
										if(data.success){
											alert(window.app.info.voteOk)
											shareToFb( options.onVoteClick );
										}else{
											alert(window.app.info.alreadyVote)
										}
									}
								})
							}, function(){
								isVoting = false;
								allController.closeLoading();
							});
						})
						voteInfo.addFBClick(function() {
							if( isFBSharing ){
								alert(window.app.info.pleaseWaitForFacebook);
								return;
							}
							isFBSharing = true;
							allController.openLoading();
							
							shareToFb( options.onFacebookShareComplete );
							
						})
						
						function shareToFb( callback ){
							vic.facebook.postMessageToMyboard({
								name : fbname,
								link : window.app.indexhtml,
								picture : serverapi.getImageUrlWithHost(window.app.host ,{articleId:articleId, outputType:"fb"}),
								caption : window.app.info.shareFB.caption,
								description : comment,
								callback : function( res ){
									if( callback != undefined ) 	callback();
									if( res != null ){
										alert( window.app.info.shareFBSuccess );
									}
									allController.closeLoading();
									isFBSharing = false;
								}
							}, function(){
								allController.closeLoading();
								isFBSharing = false;
							})
						}
					}
				}
			})
		}
		
		
	}
	
});