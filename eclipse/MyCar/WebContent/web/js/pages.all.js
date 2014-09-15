var pages = pages || {};
(function(){

	pkg = pages
	
	function allController( dom ){
		var jdom = $(dom)
		var div_loading = jdom.find('#div_loading' );
		var peopleDetail = jdom.find( '#peopleDetail' );
		var fancyboxClose = jdom.find( '#fancybox-close' );
		
		fancyboxClose.click( function(){
			closePeopleDetail();
		})
		
		function closePeopleDetail(){
			peopleDetail.fadeOut( 300 );
		}
		
		return {
			openLoading:function(){
				div_loading.fadeIn( 0 );
			},
			closeLoading:function(){
				div_loading.fadeOut( 200 );
			},
			openPeopleDetail:function( articleId, type, onCloseClick, onVoteClick, onFacebookShareComplete ){
				window.app.voteInfoOpen( {articleId:articleId, type:type, 
				onCloseClick:function(){
					onCloseClick();
					closePeopleDetail();
				},onVoteClick:onVoteClick, onFacebookShareComplete:onFacebookShareComplete} );
				peopleDetail.fadeIn( 300 );
			}
		}
	}
	
	pkg.allController = allController
	
})();