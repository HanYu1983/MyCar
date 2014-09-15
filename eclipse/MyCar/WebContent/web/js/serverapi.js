serverapi = {};

(function(){
	
	pack = serverapi
	
	pack.host = '../';
	pack.path = 'Func';
	pack.remote = false;
	
	function getAPIURL(){
		return serverapi.host + serverapi.path;
	}
	
	function remoteAjax(data, callback){
		$.ajax({
			url: getAPIURL(),
			data: data,
			type:'GET',
			contentType: false,
			processData: false,
			dataType: 'jsonp',
			success: function(msg){
				if(callback.success)
					callback.success(msg);
			},
			error:function(xhr, ajaxOptions, thrownError){
				if( callback.error )
					callback.error()
			}
		});
	}
	
	function localAjax(data, callback){
		$.ajax({
			url: getAPIURL(),
			data: data,
			type: 'POST',
			dataType: 'json',
			success: function(msg){
				if(callback.success) 
					callback.success(msg);
			},
			error:function(xhr, ajaxOptions, thrownError){
				alert( window.app.info.appCrash );
				if(callback.error)
					callback.error()
			}
		});
	}
	
	function apiAjax(data, callback ){
		fn = serverapi.remote ? remoteAjax : localAjax;
		fn( data, callback )
	}
	
	// {fbid:"", fbname:"", comment:"", image:"base64 string"}
	function submitArticle(data, callback){
		data.cmd = 'Submit64'
		apiAjax(data, callback)
	}
	
	// {name:"", gender:"F", phone:"", email:"", articleId:"", submitType:"submit"|"vote"}
	function submitUserData(data, callback){
		data.cmd = 'SubmitUserData'
		apiAjax(data, callback)
	}
	
	// {articleId:"", verified:"true"}
	function verifyArticle(data, callback){
		data.cmd = 'VerifyArticle'
		apiAjax(data, callback)
	}
	
	// {fbid:"", articleId:""}
	function vote(data, callback){
		data.cmd = 'Vote'
		apiAjax(data, callback)
	}
	
	// {articleId:["",""]}
	function getVoteCount(data, callback){
		data.cmd = 'GetVoteCount'
		apiAjax(data, callback)
	}

	// {articleId:"", outputType:"fb"|"origin"}
	function getImageUrl(data){
		return getAPIURL()+"?cmd=GetImage&articleId="+data.articleId+"&outputType="+data.outputType
	}
	
	function getImageUrlWithHost(host, data){
		return host + pack.path +"?cmd=GetImage&articleId="+data.articleId+"&outputType="+data.outputType
	}
	
	// 取得熱門 {top:1 [, queryCount:""]}
	// 指定投稿 {articleId:""}
	// 指定fb的投稿 {fbid:1, start:0, count:0, [, queryCount:""]}
	// 有fbname的投稿 {fbname:1, start:0, count:0, [, queryCount:""]}
	// 所部投稿 {start:0, count:0, [, queryCount:""]}
	// {articleId:["",""]}
	function getArticle(data, callback){
		data.cmd = 'GetArticle'
		apiAjax(data, callback)
	}
	
	pack.submitArticle = submitArticle
	pack.submitUserData = submitUserData
	pack.verifyArticle = verifyArticle
	pack.vote = vote
	pack.getVoteCount = getVoteCount
	pack.getImageUrl = getImageUrl
	pack.getImageUrlWithHost = getImageUrlWithHost
	pack.getArticle = getArticle
	
})();