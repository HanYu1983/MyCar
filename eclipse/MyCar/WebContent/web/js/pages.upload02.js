var pages = pages || {};
(function(){
	function upload02Controller( dom ){
		var jdom = $(dom)
		
		var input_name = jdom.find('#input_name' );
		var radio_boy = jdom.find('#radio_boy' );
		var radio_girl = jdom.find('#radio_girl' );
		var input_phonenumber = jdom.find('#input_phonenumber' );
		var input_email = jdom.find('#input_email' );
		var input_notice = jdom.find('#input_notice' );
		var input_notice2 = jdom.find('#input_notice2' );
		var btn_reset = jdom.find('#btn_reset' );
		var btn_send = jdom.find('#btn_send' );
		
		btn_reset.css( 'cursor', 'pointer' );
		btn_send.css('cursor', 'pointer' );
		
		return {
			setGender: function( v ){
				if( v == 'M' ){
					radio_boy.attr( 'checked', 'checked' );
				}else{
					radio_girl.attr( 'checked', 'checked' );
				}
			},
			
			setName: function( v) {
				input_name.val( v );
			},
			
			setPhoneNumber: function( v ){
				input_phonenumber.val( v );
			},
			
			setEmail: function(v){
				input_email.val( v );	
			},
			
			addResetButtonClick: function( fn ){
				btn_reset.click( fn );
			},
			
			addSendButtonClick: function( fn ){
				btn_send.click( fn )
			},
			
			addNameFocusin:function( fn ){
				input_name.focus( fn )
			},
			
			addNameFocusout:function( fn ){
				input_name.focusout( fn );
			},
			
			resetField: function(){
				input_name.val('');
				input_phonenumber.val('');
				input_email.val('');
			},
			
			getName: function(){
				return input_name.val();
			},
			
			getGender: function(){
				var genderBoy = radio_boy.attr( 'checked' );
				var genderGirl = radio_girl.attr( 'checked' );
				if( !genderBoy && !genderGirl ){
					return 'UNKNOWN';
				}
				if( genderBoy )	
					return 'M';
				else 		
					return 'F';
			},
			
			getPhoneNumber: function(){
				return input_phonenumber.val();
			},
			
			getEmail: function(){
				return input_email.val();
			},
			
			verifyPhoneNumber: function(){
				var pattern = /^[0-9]*$/;
				var phoneNumber = this.getPhoneNumber()
				if( phoneNumber.length == 0 || !pattern.test( phoneNumber )){
					return false
				}
				return true
			},
			
			verifyEmail: function(){
				var pattern = /^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]+$/;
				var email = this.getEmail()
				if( !pattern.test(email) ){
					return false
				}
				return true
			},
			
			verifyNotice: function(){
				var check1 = input_notice.attr( 'checked' );
				var check2 = input_notice2.attr( 'checked' );
				return check1 && check2
			}
		}
	}
	pages.upload02Controller = upload02Controller
})()