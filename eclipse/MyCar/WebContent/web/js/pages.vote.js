var pages = pages || {};
(function(){

	pkg = pages;
	
	function voteController( dom ){
		var jdom = $(dom)
		var peopleDetail = jdom.find( '#peopleDetail' );
		
		//for search.html
		var label_searchCount = $('#label_searchCount' );
		
		var ary_btn = [
			jdom.find('#btn_search' ),
			jdom.find('#btn_right' ),
			jdom.find('#btn_left' )
		];
		
		var ary_people = [
			jdom.find('#people01' ),
			jdom.find('#people02' ),
			jdom.find('#people03' ),
			jdom.find('#people04' ),
			jdom.find('#people05' ),
			jdom.find('#people06' ),
			jdom.find('#people07' ),
			jdom.find('#people08' ),
			jdom.find('#people09' ),
			jdom.find('#people10' )
		];
		
		var input_search = jdom.find('#input_search' );
		var select_page = jdom.find('#select_page' );
		
		function getBtnVote( query ){
			return query.children().eq(3);
		}
		
		function getBtnShare( query ){
			return query.children().eq(4);
		}
		
		function getPeopleImageBorder( query ){
			return query.children().eq( 0 );
		}
		
		function getPeopleImage( query ){
			return query.children().eq(0).children().eq( 0 );
		}
		
		function getPeopleName( query ){
			return query.children().eq(1);
		}
		
		function getPeopleVote( query ){
			return query.children().eq(2).children().eq( 0 );
		}
		
		return {
			setInputSearchFocus:function(){
			    input_search.focus( function(){
					input_search.val('');
				})
			},
			setSelectPageOnChange:function( method ){
				select_page.change( function(){
					method( select_page.val() );
				});
			},
            addBtnsListener:function( options ){
                for( var i in ary_btn ){
					var query = ary_btn[i];
					query.css( 'cursor', 'pointer' );
					query.click( function(){
						var btn = $( this );
						switch( btn.attr( 'id' ) ){
							case 'btn_right':
								options.onRightClick( btn );
								break;
							case 'btn_left':
								options.onLeftClick( btn );
								break;
							case 'btn_search':
								var searchWord = input_search.val();
								if( searchWord == window.app.info.searchFBInputName )	alert(window.app.info.pleaseInputSearchWord);
								else window.location.href = 'search.html?search=' + escape( input_search.val() );
								options.onSearchClick( btn );
						}
					});
				}
            },
			addPeopleListener:function( options ){
				for( var i = 0; i < ary_people.length; ++i ){
					var query = ary_people[i];
					
					var btn_share = getBtnShare( query );
					btn_share.css( 'cursor', 'pointer' );
					
					var closure = function(_idx, _method){
						return function(){
							_method( _idx );
						}
					};
					btn_share.click( closure(i, options.onShareClick ) );
					
					var btn_votePeople = getPeopleImageBorder( query );
					btn_votePeople.css( 'cursor', 'pointer' );
					btn_votePeople.click( closure(i, options.onVotePeopleClick ) );
					
					var btn_vote = getBtnVote( query );
					btn_vote.css( 'cursor', 'pointer' );
					btn_vote.click( closure(i, options.onVoteClick ) );
					btn_vote.mouseover( function(){
						var img = $(this).children().eq( 0 );
						img.attr( 'src', 'images/but05a.jpg' );
					});
					
					btn_vote.mouseout( function(){
						var img = $(this).children().eq( 0 );
						img.attr( 'src', 'images/but05.jpg' );
					});
					
					var namequery = getPeopleName( query );
					namequery.html( '' );
					
					var votequery = getPeopleVote( query );
					votequery.html( '' );
					
					var imgquery = getPeopleImage( query );
					imgquery.attr( 'src', '' );
				}
			},
			setModelToPeople:function( ary_model, start ){
				var endId;
				if( start == 0){
					endId = 5;
					for( i in ary_people ){
						var peoplequery = ary_people[parseInt( i ) + start];
						if( i < endId ){
							if( i > ary_model.length - 1 ){
								peoplequery.hide();
							}else{
								var model = ary_model[i];
								peoplequery.fadeIn(300);
								getPeopleName( peoplequery ).html( model.fbname );
								getPeopleVote( peoplequery ).html( model.voteCount );
								getPeopleImage( peoplequery ).attr( 'src', serverapi.getImageUrl({articleId:model.id, outputType:"100"} ) );
								
							}
						}
					}
				}else if ( start == 5 ){
					endId = 10;
					for( i in ary_people ){
						var peoplequery = ary_people[parseInt( i )];
						if( i >= 5 && i < 10 ){
							if( ( i - 5 ) > ary_model.length - 1 ){
								peoplequery.hide();
							}else{
								var model = ary_model[i - 5];
								peoplequery.show();
								getPeopleName( peoplequery ).html( model.fbname );
								getPeopleVote( peoplequery ).html( model.voteCount );
								getPeopleImage( peoplequery ).attr( 'src', serverapi.getImageUrl({articleId:model.id, outputType:"100"} ) );
							}
						}
					}
				}else if( start == 99 ){
					for( i in ary_people ){
						var peoplequery = ary_people[i];
						if( i > ary_model.length - 1 ){
							peoplequery.hide();
						}else{
							var model = ary_model[i];
							peoplequery.show();
							getPeopleName( peoplequery ).html( model.fbname );
							getPeopleVote( peoplequery ).html( model.voteCount );
							getPeopleImage( peoplequery ).attr( 'src', serverapi.getImageUrl({articleId:model.id, outputType:"100"} ) );
							
						}
					}
				}
			},
			appendPageToSelect:function( count ){
				select_page.empty();
				for( var i = 1; i <= count; ++i ){
					select_page.append( '<option value=' + i + '>第' + i + '頁</option>' );
				}
			},
			setCurrentPage:function(currentPage){
				select_page.children().eq( currentPage ).attr('selected', '1');
			},
			setInputSearch:function( searchWord ){
				input_search.val( searchWord == null ? window.app.info.searchFBInputName : searchWord);
			},
			//search.html below
			searchAndRefresh:function( pid ){
				var word = input_search.val();
				if( word == window.app.info.searchFBInputName ){
					window.location.href = 'search.html?currentPage=' + pid;
				}else{
					window.location.href = 'search.html?search=' + escape( word ) + '&currentPage=' + pid;
				}
			},
			setCount:function ( count ){
				label_searchCount.html( '★共有' + count + '筆搜尋結果' );
			}
		}
	}
	
	pkg.voteController = voteController;
	
})();