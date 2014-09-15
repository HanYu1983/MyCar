$(function(){
	if( window.app.local )	open();
	else vic.facebook.init( window.app.fbappid, window.app.fbchannel, open );
	
	function open(){
		
		var currentPage = vic.utils.getUrlValue( 'currentPage', 1);
		var searchWord = vic.utils.getUrlValue( 'search', null );
		var ary_model;
		var maxPage = 10;//for test
		var isFBSharing = false;
		var isVoting = false;
		var controller = pages.voteController( $('body' ));
		var allController = pages.allController( $('body' ));
		
		controller.setInputSearchFocus();
		controller.setInputSearch( searchWord );
		controller.setCurrentPage( currentPage - 1 );
		controller.setSelectPageOnChange( function( val ){
			currentPage = val;
			searchNewArticle();
		});
		controller.addBtnsListener( {
			onRightClick:function(){
				currentPage++;
				if( currentPage > maxPage ){
					currentPage = maxPage;
					return;
				}
				controller.setCurrentPage( currentPage - 1 );
				searchNewArticle();
			},
			onLeftClick:function(){
				currentPage--;
				if( currentPage < 1 ){
					currentPage = 1;
					return;
				}
				controller.setCurrentPage( currentPage - 1 );
				searchNewArticle();
			},
			onSearchClick:function(){
				
			}
		});
		
		controller.addPeopleListener( {
			onVoteClick:function( _ret ){
				var model = ary_model[ _ret ];
				if(model == null)
					return;
				if( isVoting ){
					alert( window.app.info.waitForVote );
					return;
				}
				isVoting = true;
				allController.openLoading();
				
				vic.facebook.login( function( fbres ){
					var fbid = fbres.userID;
					serverapi.vote({
						fbid: fbid,
						articleId:model.id
					},{
						success:function(data){
							isVoting = false;
							allController.closeLoading();
							if(data.success){
								alert(window.app.info.voteOk)
								shareToFb( model, function(){
									window.location.href = 'upload03.html?articleId=' + model.id;
								});
								//window.location.href = 'upload03.html?articleId=' + model.id;
							}else{
								alert(window.app.info.alreadyVote)
							}
						}
					})
				}, function(){
					isVoting = false;
					allController.closeLoading();
				});
				
			},
			onShareClick:function( _ret ){
				var model = ary_model[ _ret ];
				if(model == null)
					return;
				if( isFBSharing ){
					alert( window.app.info.pleaseWaitForFacebook );
					return;
				}
				isFBSharing = true;
				allController.openLoading();
				
				shareToFb( model );
			},
			onVotePeopleClick:function( _ret ){
				var model = ary_model[ _ret ];
				if(model == null)
					return;
				allController.openPeopleDetail(model.id, 'edit', function(){
					
				}, function(){
					window.location.href = 'upload03.html?articleId=' + model.id;
				}, function(){
					
				});			
			}
		} );
		
		loadData();
		
		function shareToFb( model, callback ){
			var shareUrl = serverapi.getImageUrlWithHost(window.app.host ,{articleId:model.id, outputType:"fb"})
			vic.facebook.postMessageToMyboard({
				name : model.fbname,
				link : window.app.indexhtml,
				picture : shareUrl,
				caption : model.fbname,
				description : model.comment,
				callback : function( res ){
					if( res != null ){
						alert( window.app.info.shareFBSuccess );
					}
					isFBSharing = false;
					allController.closeLoading();
					if( callback != undefined)	callback();
				}
			}, function(){
				isFBSharing = false;
				allController.closeLoading();
			})
		}
		
		function loadData(){
			if( window.app.local ){
				ary_model = [	{fbname:'vic1', vote:4, id:1 }, { fbname:'han2', vote:7, id:2}, {fbname:'vic3', vote:4, id:1}, {fbname:'vic', vote:4, id:1},
								{fbname:'vic', vote:4, id:1 }, { fbname:'han', vote:7, id:2}, {fbname:'vic', vote:4, id:1}, {fbname:'vic', vote:4, id:1}];
				count = 39;
				maxPage = Math.ceil( count / 10 );
				controller.setModelToPeople( ary_model, 99 );
				controller.setCount( count );
				controller.appendPageToSelect( maxPage );
				controller.setCurrentPage( currentPage - 1 );
			}else{
				var searchData = {start:( currentPage - 1 ) * 10, count:10 };
				if( searchWord != null){
					searchData.fbname = encodeURIComponent(searchWord);
				}
				//search for model
				serverapi.getArticle(searchData,{ success: function(data){
					if( data.success ){
						ary_model = data.info;
						controller.setModelToPeople( ary_model, 99 );
					}
				}});
				//search for maxcount
				searchData.queryCount = '1';
				serverapi.getArticle(searchData,{ success: function(data){
					if( data.success ){
						var count = data.info;
						maxPage = Math.ceil( count / 10 );
						controller.appendPageToSelect( maxPage );
						controller.setCount( count );
					}
				}});
			}
		}
		
		function searchNewArticle(){
			controller.searchAndRefresh( currentPage );
		}
	}
});

//顯示出popup
//MM_showHideLayers('apDiv5','','show')