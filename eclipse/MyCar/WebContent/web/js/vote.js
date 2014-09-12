$(function(){
	if( window.app.local ) open();
	else vic.facebook.init( window.app.fbappid, window.app.fbchannel, open );
	
	function open(){ 
		var currentPage = 1;
		var maxPage = 10;
		var ary_topModel;
		var ary_otherModel;
		var isFBSharing = false;
		var isVoting = false;
		var allController = pages.allController( $('body' ));
		
        controller = pages.voteController( $('body')[0]);
        controller.setInputSearchFocus();
		controller.setSelectPageOnChange( function( val ){
			currentPage = val;
			searchNewArticle();
			console.log( 'currentPage', currentPage );
		});
		controller.addBtnsListener( {
			onRightClick:function(){
				currentPage++;
				if( currentPage > maxPage ){
					currentPage = maxPage;
				}
				console.log( 'currentPage', currentPage );
				controller.setCurrentPage( currentPage - 1 );
				searchNewArticle();
			},
			onLeftClick:function(){
				currentPage--;
				if( currentPage < 1 ){
					currentPage = 1;
				}
				console.log( 'currentPage', currentPage );
				controller.setCurrentPage( currentPage - 1 );
				searchNewArticle();
			},
			onSearchClick:function(){
				
			}
		});
		controller.addPeopleListener( {
			onVoteClick:function( _ret ){
				var model = _ret< 5? ary_topModel[ _ret ] : ary_otherModel[ _ret - 5 ]
				if(model == null)
					return;
				if( isVoting ){
					alert( window.app.info.waitForVote );
					return;
				}
				allController.openLoading();
				isVoting = true;
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
								shareToFb( model,function(){
									window.location.href = 'upload03.html?articleId=' + model.id;
								})
								
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
				var model = _ret< 5? ary_topModel[ _ret ] : ary_otherModel[ _ret - 5 ]
				if(model == null)
					return;
				if( isFBSharing ){
					alert( window.app.info.pleaseWaitForFacebook );
					return;
				}
				allController.openLoading();
				isFBSharing = true;
				
				shareToFb( model);
			},
			onVotePeopleClick:function( _ret ){
				var model = _ret< 5? ary_topModel[ _ret ] : ary_otherModel[ _ret - 5 ]
				if(model == null)
					return;
				
				allController.openPeopleDetail(model.id, 'edit', function(){
					
				},function(){
					window.location.href = 'upload03.html?articleId=' + model.id;
				},function(){
					
				});
			}
		} );
		
		loadData();
		
		function shareToFb( model, callback ){
			var shareUrl = serverapi.getImageUrlWithHost(window.app.host ,{articleId:model.id, outputType:"fb"})
			console.log( model);
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
				ary_topModel = [{fbname:'vic', vote:4, id:1 }, { fbname:'han', vote:7, id:2}, {fbname:'han2', vote:7, id:2}, {fbname:'han3', vote:7, id:2}];
				controller.setModelToPeople( ary_topModel, 0 );
				maxPage = Math.ceil( 60 / 5 );
				controller.appendPageToSelect( maxPage );
			}else{
				serverapi.getArticle({ top:5 },{ success: function(data){
					if( data.success ){
						ary_topModel = data.info;
						controller.setModelToPeople( ary_topModel, 0 );
					}
				}});
				
				serverapi.getArticle({ start:0, count:0, queryCount:'1', outputType:'100' },{ success: function(data){
					if( data.success ){
						var count = data.info;
						maxPage = Math.ceil( count / 5 );
						controller.appendPageToSelect( maxPage );
					}
				}});
			}
			searchNewArticle();
		}
		
		function searchNewArticle(){
			if( window.app.local ){
				ary_otherModel = [{ fbname:'han3', vote:7, id:2}, {fbname:'han2', vote:7, id:2}, {fbname:'han3', vote:7, id:2}];
				controller.setModelToPeople( ary_otherModel, 5 );
			}else{
				serverapi.getArticle({ start:( currentPage - 1 ) * 5, count:5 },{ success: function(data){
					if( data.success ){
						ary_otherModel = data.info;
						controller.setModelToPeople( ary_otherModel, 5 );
					}
				}});
			}
		}
	}
});

//顯示出popup
//MM_showHideLayers('apDiv5','','show')