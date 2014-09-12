var vic = vic || {};
vic.events = vic.events || {};
(function(){
	function Event( type, data ){
		this._type = type;
		this._data = data;
	}
	
	Event.prototype = {
		getType:function(){
			return this._type;
		},
		getData:function(){
			return this._data;
		}
	}
	
	function EventDispatcher(){}
	
	EventDispatcher.prototype = {
		addEventListener:function( n, fn ){
			if( this._observers == undefined ){
				this._observers = {};
			}
			if( this._observers[n] == undefined ){
				this._observers[n] = [];
			}
			if( this._observers[n].indexOf( fn ) == -1 ){
				this._observers[n].push( fn );
			}
		},
		removeEventListener:function( n, fn ){
			if( this._observers == undefined )	return;
			if( this._observers[n] == undefined )	return;
			var ary = this._observers[n];
			var id = ary.indexOf( fn );
			if( id != -1 ){
				ary.splice( id, 1 );
			}
			if( ary.length == 0 ){
				delete this._observers[n];
			}
		},
		removeAllEventListener:function(){
			if( this._observers == undefined )	return;
			this._observers = undefined;
		},
		dispatchEvent:function( e ){
			if( this._observers == undefined )	return;
			var en = e.getType();
			if( this._observers[ en ] != undefined ){
				e.currentTarget = this;
				var k, ary = this._observers[ en ]
				for ( k in ary ){
					ary[k]( e );
				}
			}
		}
	}
	
	vic.events.Event = Event;
	vic.events.EventDispatcher = EventDispatcher;
})()