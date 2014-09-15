window.app = window.app || {};
(function(){
	
	if( window.app.local )	open();
	else	vic.facebook.init( window.app.fbappid, window.app.fbchannel, open );
	
	
	function open(){
		var upload02Controller = pages.upload02Controller($('body')[0])
		
		var userdatastr = vic.utils.getCookie('userdata');
		var userdata;
		
		var articleId = vic.utils.getUrlValue( 'articleId' );
		
		var allController = pages.allController( $('body' ) );
		
		var canClickNext = true;
		
		if( userdatastr.length != 0 ){
			userdatastr = decodeURIComponent( userdatastr );
			userdata = eval('(' + userdatastr + ')');
			
			upload02Controller.setName( userdata.name )
			upload02Controller.setPhoneNumber( userdata.phone )
			upload02Controller.setEmail( userdata.email )
			upload02Controller.setGender( userdata.gender )
		}else{
			upload02Controller.setName( window.app.info.defaultName )
		}
		
		upload02Controller.addNameFocusin(function(){
			if( upload02Controller.getName().trim() == window.app.info.defaultName ){
				upload02Controller.setName('')
			}
		})
		
		upload02Controller.addNameFocusout(function(){
			if( upload02Controller.getName().trim() == '' ){
				upload02Controller.setName(window.app.info.defaultName)
			}
		})
		
		upload02Controller.addResetButtonClick(upload02Controller.resetField)
		
		upload02Controller.addSendButtonClick(function(){
			var name = upload02Controller.getName();
			if( name.trim() == 0 ){
				alert( window.app.info.noName );
				return;
			}
			
			if( name.trim() == window.app.info.defaultName ){
				alert( window.app.info.noName );
				return;
			}
						
			var genderBoy = upload02Controller.getGender()
			if( genderBoy == 'UNKNOWN' ){
				alert( window.app.info.noGender );
				return;
			}
			
			if( !upload02Controller.verifyPhoneNumber() ){
				alert( window.app.info.noPhonenumber );
				return;
			}
			
			if( !upload02Controller.verifyEmail() ){
				alert( window.app.info.noEmail );
				return;
			}
			
			var canWork = upload02Controller.verifyNotice()
			if( canWork ){
				if( window.app.local )	{
					MM_showHideLayers('apDiv5','','show');
				}
				else{
					if( !canClickNext ){
						alert( window.app.info.pleaseWaitForFacebook );
						return;
					}
					allController.openLoading();
					canClickNext = false;
					
					vic.facebook.getMyData(function( res ){
						var token = res.accessToken
						var fbid = res.id;
						serverapi.submitUserData( {
							accessToken: token,
							"fbid": fbid,
							name:encodeURIComponent(name), 
							gender:genderBoy, 
							phone:upload02Controller.getPhoneNumber(), 
							email:upload02Controller.getEmail(), 
							articleId:articleId,
							submitType:'vote'
							
						}, {success:function( data ){
							allController.closeLoading();
							canClickNext = true;
							if( data.success ){
								MM_showHideLayers('apDiv5','','show');
							}else{
								alert( window.app.info.dataUploadError );
							}
						}});
					}, function(){
						allController.closeLoading();
						canClickNext = true;
						alert( window.app.info.dataUploadError );
					})
					
				}
			}else{
				alert( window.app.info.pleaseCheckNeeded );
			}
		})
	}
	
})();