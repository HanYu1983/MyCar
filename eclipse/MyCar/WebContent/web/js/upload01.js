window.app = window.app || {};
(function(){
	if( window.app.local )	open();
	else	vic.facebook.init( window.app.fbappid, window.app.fbchannel, open );
	
	function open(){
		var controller = pages.upload01Controller( $('body' ));
		var allController = pages.allController( $('body' ));
		var defaultStr = controller.getDescribe();
		var canClickNext = true;
		var isUpload = false;
		var isEdit = false;
		var fbid;
		var articleId;
		var image;
		
		var browser = vic.utils.browserJudge();
		if( browser.name == 'ie' ){
			if( browser.ver != '10.0' ){
				alert( window.app.info.belowIE10 );
			}
		}
		
		controller.setInputUploadChange( function( query ){
			var files = query[0].files;
			if (files && files[0]) {
				isUpload = true;
				allController.openLoading();
				vic.utils.readImage( files[0], function( retImageName, retImage ){
					controller.setTextUploadValue( retImageName );
					image = retImage;
					isUpload = false;
					allController.closeLoading();
				}, function(){
					allController.closeLoading();
					alert( window.app.info.imageUploadError );
				});
			}
		});

		controller.setTextAreaDescribeFocusin( function(){
			if( isEdit )		return;
			isEdit = true;
			controller.setDescribe('');
		});
		controller.setTextAreaDescribeFocusout( function(){
			var nowstr = controller.getDescribe();
			if( nowstr.trim() == 0 ){
				controller.setDescribe( defaultStr );
			}
		});
		
		controller.setBtnResetClick( function(){
			isEdit = false;
			controller.setDescribe( defaultStr );
		});
		
		controller.setBtnNextClick( function(){
			/*
			if( image == undefined ){
				alert( window.app.info.pleaseUploadImage );	
				return;
			}
			
			if( isUpload ){
				alert( window.app.info.waitForImageUpload );	
				return;
			}
			*/
			if( controller.getInputUploadValue() == '' ){
				alert( window.app.info.pleaseUploadImage );	
			}
			controller.setTextUploadValue( controller.getInputUploadValue() );
			
			if( !canClickNext )	{
				alert( window.app.info.pleaseWaitForFacebook );
				return;
			}
			
			var comment = controller.getDescribe();
			if( comment.length > 300 )	{
				alert( window.app.info.over300Word );
				return;
			}
			
			canClickNext = false;
			allController.openLoading();
			
			if( window.app.local )	{
				allController.closeLoading();
				articleId = 'abc';
				allController.openPeopleDetail( articleId, 'sample', function(){
					controller.nextPage( articleId );
				}, function(){
					
				}, function(){
					controller.nextPage( articleId );
				});
			}else{
				//for fb test
				
				var fbdata = window.app.info.shareFB;
				fbdata.name = 'sitename';
				fbdata.callback = function( res ){
					if( res != null ){
						alert( window.app.info.shareFBSuccess );
					}
					vic.facebook.getMyData(function( res ){
						var token = res.accessToken
						var fbid = res.id;
						var name = res.last_name + res.first_name;
						var comment = controller.getDescribe();
						if( comment.length > 300 )	{
							alert( window.app.info.over300Word );
							return;
						}
						controller.setAccessToken( token );
						controller.setFbname( encodeURIComponent(name) );
						controller.setComment( encodeURIComponent(comment) );
						document.uploadForm.submit();
					}, function(){
						canClickNext = true;
						allController.closeLoading();
					});
				}
				
				vic.facebook.postMessageToMyboard( fbdata,
				function(){
					canClickNext = true;
					allController.closeLoading();
				});
			}
		});
	}
})();