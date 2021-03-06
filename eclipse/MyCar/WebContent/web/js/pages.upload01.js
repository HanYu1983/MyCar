var pages = pages || {};
(function(){

	pkg = pages
	
	function upload01Controller( dom ){
		var jdom = $(dom)
		var fbid = jdom.find('#fbid' )
		var but01 = jdom.find( '.but01' )
		var input_upload = jdom.find('#input_upload' )
		var txt_upload = jdom.find('#txt_upload' )
		var ta_describe = jdom.find('#ta_describe' )
		var btn_reset = jdom.find('#btn_reset' )
		var btn_next = jdom.find('#btn_next')
		var accessToken = $('#accessToken' )
		var fbname = $('#fbname' )
		var comment = $('#comment' )
		txt_upload.val( '' );
		
		return {
			setBtnVisible: function( show ){
				if( !show ){
					but01.removeClass( 'but01' );
					but01.find( 'div' ).hide();
					input_upload.show();
					input_upload.css( 'opacity', 1 );
				}
			},
			setFbid: function( value ){
				fbid.val( value )
			},
			setAccessToken:function( ac ){
				accessToken.val( ac );
			},
			setFbname:function( fbname_ ){
				fbname.val( fbname_ );
			},
			setComment:function( comment_ ){
				comment.val( comment_ );
			},
			setInputUploadChange:function( fn ){
				input_upload.change( function(){
					fn( input_upload );
				});
			},
			setTextAreaDescribeFocusin:function( fn ){
				ta_describe.focus( fn )
			},
			setTextAreaDescribeFocusout:function( fn ){
				ta_describe.focusout( fn );
			},
			setBtnResetClick:function( fn ){
				btn_reset.css( 'cursor', 'pointer' )
				btn_reset.click( fn );
			},
			setBtnNextClick:function( fn ){
				btn_next.css( 'cursor', 'pointer' )
				btn_next.click( fn )
			},
			setTextUploadValue:function( val ){
				txt_upload.val( val );
			},
			setDescribe:function( val ){
				ta_describe.val( val );
			},
			getDescribe:function(){
				return ta_describe.val();
			},
			getInputUploadValue:function(){
				return input_upload.val();
			},
			nextPage:function( articleId ){		
				window.location.href = 'upload02.html?articleId=' + articleId;
			}
		}
	}
	
	pkg.upload01Controller = upload01Controller
	
})();