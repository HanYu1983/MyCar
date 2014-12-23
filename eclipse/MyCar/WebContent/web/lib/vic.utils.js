var vic = vic || {};
vic.utils = vic.utils || {};

// for IE no console
if( console == undefined ){
	console = { log: function(v){} };
}

if( String.prototype.trim == undefined ){
	 String.prototype.trim = function(){
		return $.trim(this);
	};
}

vic.utils.includeJS = function( ary_path ){
	for( var i = 0; i < ary_path.length; ++i ){
		document.write( '<script type="text/javascript" src="' + ary_path[i] + '"></script>' );
	}
}

vic.utils.includeCSS = function( ary_path ){
	for( var i = 0; i < ary_path.length; ++i ){
		document.write( '<link href="' + ary_path[i] + '" rel="stylesheet" type="text/css" />' );
	}
}

vic.utils.createElement = function( type, obj ){
	var element = document.createElement( type );
	for( prop in obj ){
		element[ prop ] = obj[ prop ];
	}
	return element;
}

vic.utils.getElement = function( id ){
	return document.getElementById( id );
}

vic.utils.appendChild = function(){
	if( arguments.length > 1 ){
		var a = arguments[0];
		for( i = 1; i < arguments.length; ++i ){
			if( arguments[i] ){
				a.appendChild( arguments[i] );
			}
		}
		return a;
	}else{
		return null;
	}
}

vic.utils.removeChildren = function( node ){
	if( node == null )	return;
	while( node.hasChildNodes() ){
		node.removeChild( node.firstChild );
	}
}

vic.utils.addEventListener = function( obj, eventName, listener ){
	if( obj.attachEvent )			obj.attachEvent( 'on' + eventName, listener );
	else if( obj.addEventListener )	obj.addEventListener( eventName, listener, false );
	else							return false;
	return true;
}

vic.utils.removeEventListener = function( obj, eventName, listener ){
	if( obj.detachEvent )				obj.detachEvent( 'on' + eventName, listener );
	else if( obj.removeEventListener )	obj.removeEventListener( eventName, listener, false );
	else return false;
	return true;
}

vic.utils.changeOpacity = function( opacity, id ){
	var styleobj = document.getElementById( id ).style;
	styleobj.opacity = opacity;
	styleobj.MozOpacity = opacity;
	styleobj.KhtmlOpacity = opacity;
	styleobj.filter = 'alpha(opacity=' + opacity + ')';
}

vic.utils.inherit = function(from, fields) {
	function Inherit() {} Inherit.prototype = from.prototype; var proto = new Inherit();
	for (var name in fields) proto[name] = fields[name];
	if( fields.toString !== Object.prototype.toString ) proto.toString = fields.toString;
	proto.uber = from.prototype;
	return proto;
}

vic.utils.toURL = function( url, data, datatype, method, onComplete ){
	$.ajax({
		url: url,
		data: data,
		type: method,
		dataType: datatype,
		
		//none multipart should't have below
		//processData: false,
		
		//multipart need below
		//contentType: false,
		//processData: false,
		
		//multipart should't have below
		//contentType: 'multipart/form-data',
		
		cache: false,
		success: function(msg){
		//	console.log( msg );
			if( onComplete != undefined )	onComplete( msg );
		},
		error:function(xhr, ajaxOptions, thrownError){ 
			alert(xhr.status); 
			alert(thrownError); 
		}
	});
}

vic.utils.getMultipart = function( options ){
	var data = new FormData();
	for( var k in options ){ data.append( k, options[k] );}
	return data;
}

vic.utils.imageToBase64 = function( image ){
	var canvas = document.createElement( 'canvas' );
	canvas.width = image.width;
	canvas.height = image.height;
	try{
		canvas.getContext( '2d' ).drawImage( image, 0, 0 );
	}catch( e ){
		alert('please upload image');
	}
	return canvas.toDataURL();
}

vic.utils.readImage = function( file, onload, onerror, onprogress ){
	var reader = new FileReader();
	var image = new Image();
	reader.onload = function (e) {
		image.src = e.target.result;
		//onload( file.name, image );
		
		image.onload = function(){
			onload( file.name, image );
		}
	};
	reader.onerror = function( e ){
		reader.abort();
		reader = undefined;
		if( onerror != undefined )	onerror();
	};
	reader.onprogress = function( e ){
		if( onprogress != undefined )	onprogress( e );
	};
	reader.readAsDataURL(file);
}

vic.utils.scaleCanvas = function( canvas, width, height, drawWidth, drawHeight ){
	var newcanvas = document.createElement( 'canvas' );
	var newcanvasCtx = newcanvas.getContext( '2d' );
	newcanvas.width = width
	newcanvas.height = height;
	var scaleWidth = drawWidth / canvas.width;
	var scaleHeight = drawHeight / canvas.height;
	newcanvasCtx.transform( scaleWidth, 0, 0, scaleHeight, 0, 0 );
	newcanvasCtx.drawImage( canvas, 0, 0 );
	return newcanvas;
}

vic.utils.centerAndScaleImage = function( imageWidth, imageHeight, containerWidth, containerHeight ){
	var ratio = imageWidth / imageHeight;
	var toRatio = containerWidth / containerHeight;
	var fac;
	if ( ratio > toRatio ) {
		fac = containerWidth / imageWidth;
	}else {
		fac = containerHeight / imageHeight;
	}
	imageWidth = imageWidth * fac;
	imageHeight = imageHeight * fac;
	var offsetX = ( containerWidth - imageWidth ) / 2;
	var offsetY = ( containerHeight - imageHeight ) / 2;
	return {scale:fac, offset:{x:offsetX, y:offsetY}};
};
/*
var image = new Image();
image.src= '001.jpg';
image.onload = function(){
	var canvas = vic.utils.drawImageToCanvas( image, {width:300, height:300}, 0 );
	document.body.appendChild( canvas );
};
*/
vic.utils.drawImageToCanvas = function(image, target, color ){
	var scaleObj = vic.utils.centerAndScaleImage( image.width, image.height, target.width, target.height );
	var newcanvas = document.createElement( 'canvas' );
	var newcanvasCtx = newcanvas.getContext( '2d' );
	newcanvas.width = target.width;
	newcanvas.height = target.height;
	newcanvasCtx.fillStyle = color;
	newcanvasCtx.fillRect( 0, 0, newcanvas.width, newcanvas.height );
	newcanvasCtx.transform( scaleObj.scale, 0, 0, scaleObj.scale, scaleObj.offset.x, scaleObj.offset.y );
	newcanvasCtx.drawImage( image, 0, 0 );
	newcanvasCtx.setTransform( 1, 0, 0, 1, 0, 0 );
	return newcanvas;
}

vic.utils.getUrlValue = function( varname, defaultValue ){
	var url = window.location.href;
	if( url == undefined )	return defaultValue;
	var qparts = url.split("?");
	if (qparts.length < 2){return defaultValue;}
	var query = qparts[1];
	var vars = query.split("&");
	var value = "";
	for (i=0; i<vars.length; i++){
		var parts = vars[i].split("=");
		if (parts[0] == varname){
			value = parts[1];
			break;
		}
	}
	value = unescape(value);
	value.replace(/\+/g," ");
	
	if( value == '')		return defaultValue;
	return value;
}

vic.utils.getCookie = function(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(name) != -1) return c.substring(name.length,c.length);
    }
    return "";
}

vic.utils.setCookie = function(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}

vic.utils.checkCookie = function( key ) {
    if (vic.utils.getCookie( key ) != "" )	return false;
	return true;
}

vic.utils.browserJudge = function(){
	var Sys = {};
	var ua = navigator.userAgent.toLowerCase();
	var s;
	(s = ua.match(/rv:([\d.]+)\) like gecko/)) ? Sys.ie = s[1] :
	(s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] :
	(s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] :
	(s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] :
	(s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] :
	(s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;
	
	if (Sys.ie) 		return {name:'ie', ver:Sys.ie };
	if (Sys.firefox) 	return {name:'firefox', ver:Sys.firefox };
	if (Sys.chrome) 	return {name:'chrome', ver:Sys.chrome };
	if (Sys.opera) 		return {name:'opera', ver:Sys.opera };
	if (Sys.safari) 	return {name:'safari', ver:Sys.safari };
}


vic.utils.isChinese = function(s){
	for(var i = 0; i < s.length; i++) {
		var notChinese = s.charCodeAt(i) < 0x4E00 || s.charCodeAt(i) > 0x9FA5;
		if( notChinese )
			return false
	}
	return true
}


