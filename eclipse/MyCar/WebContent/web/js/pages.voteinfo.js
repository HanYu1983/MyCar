var pages = pages || {};
(function(){

	pkg = pages
	
	function voteInfoController( dom ){
		var jdom = $(dom)
		var photoDom = jdom.find("table tr td #photo")
		var nameDom = jdom.find("table tr td #name")
		var voteCountDom = jdom.find("table tr td #voteCount")
		var voteButtonDom = jdom.find("table tr td #voteButton img")
		var fbButtonDom = jdom.find("table tr td #fbButton img")
		var contentDom = jdom.find("table #content")
		var voteCount = jdom.find( '#voteCount' );
		var fancyboxClose = $( '#fancybox-close' );
		
		return {
			setPhoto: function(src){
				photoDom.attr("src", src)
			},
			setName: function(na){
				nameDom.html(na)
			},
			setVoteCount: function( v ){
				voteCountDom.find("span").html(v)
			},
			addVoteClick: function( fn ){
				voteButtonDom.css( 'cursor', 'pointer' );
				voteButtonDom.click(fn)
			},
			addFBClick: function( fn ){
				fbButtonDom.css( 'cursor', 'pointer' );
				fbButtonDom.click( fn )
			},
			addCloseClick:function( fn ){
				fancyboxClose.css( 'cursor', 'pointer' );
				fancyboxClose.click( function(){
					fn();
				});
			},
			setContent: function( v ){
				contentDom.html(v)
			},
			hideVoteButton: function( v ){
				voteButtonDom.css('opacity', '0')
				voteButtonDom.unbind('click')
				voteCount.css( 'opacity', '0' );
			},
			removeListener:function(){
				voteButtonDom.unbind('click');
				fbButtonDom.unbind('click');
				fancyboxClose.unbind('click' );
				this.setPhoto('')
			}
		}
	}
	
	pkg.voteInfoController = voteInfoController
	
})();