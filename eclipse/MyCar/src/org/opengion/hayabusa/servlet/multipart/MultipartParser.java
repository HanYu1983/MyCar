/*
  * Copyright (c) 2009 The openGion Project.
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
  * either express or implied. See the License for the specific language
  * governing permissions and limitations under the License.
  */
 package org.opengion.hayabusa.servlet.multipart;
 
 import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.opengion.fukurou.util.Closer;
 
 /**
  * ファイルアップロード時のマルチパート処理のパーサーです。
  *
  * @og.group その他機能
  *
  * @version  4.0
  * @author   Kazuhiko Hasegawa
  * @since    JDK5.0,
  */
 public class MultipartParser {
         private final ServletInputStream in;
         private final String boundary;
         private FilePart lastFilePart;
         private final byte[] buf = new byte[8 * 1024];
         private static final String DEFAULT_ENCODING = "MS932";
         private String encoding = DEFAULT_ENCODING;
 
         /**
          * マルチパート処理のパーサーオブジェクトを構築する、コンストラクター
          *
          * @og.rev 5.3.7.0 (2011/07/01) 最大容量オーバー時のエラーメッセージ変更
          * @og.rev 5.5.2.6 (2012/05/25) maxSize で、0,またはマイナスで無制限
          *
          * @param       req             HttpServletRequestオブジェクト
          * @param       maxSize 最大容量(0,またはマイナスで無制限)
          * @throws IOException
          */
         public MultipartParser( final HttpServletRequest req, final int maxSize ) throws IOException {
                 String type = null;
                 String type1 = req.getHeader("Content-Type");
                 String type2 = req.getContentType();
 
                 if(type1 == null && type2 != null) {
                         type = type2;
                 }
                 else if(type2 == null && type1 != null) {
                         type = type1;
                 }
 
                 else if(type1 != null && type2 != null) {
                         type = (type1.length() > type2.length() ? type1 : type2);
                 }
 
                 if(type == null ||
                                 !type.toLowerCase(Locale.JAPAN).startsWith("multipart/form-data")) {
                         throw new IOException("Posted content type isn't multipart/form-data");
                 }
 
                 int length = req.getContentLength();
                 // 5.5.2.6 (2012/05/25) maxSize で、0,またはマイナスで無制限
 //              if(length > maxSize) {
                 if( maxSize > 0 && length > maxSize ) {
 //                      throw new IOException("Posted content length of " + length +
 //                                                                      " exceeds limit of " + maxSize);
                         throw new IOException("登録したファイルサイズが上限(" + ( maxSize / 1024 / 1024 ) + "MB)を越えています。"
                                                                         + " 登録ファイル=" + ( length / 1024 / 1024 ) + "MB" ); // 5.3.7.0 (2011/07/01)
                 }
 
                 // 4.0.0 (2005/01/31) The local variable "boundary" shadows an accessible field with the same name and compatible type in class org.opengion.hayabusa.servlet.multipart.MultipartParser
                 String bound = extractBoundary(type);
                 if(bound == null) {
                         throw new IOException("Separation boundary was not specified");
                 }
 
                 this.in = req.getInputStream();
                 this.boundary = bound;
 
                 String line = readLine();
                 if(line == null) {
                         throw new IOException("Corrupt form data: premature ending");
                 }
 
                 if(!line.startsWith(boundary)) {
                         throw new IOException("Corrupt form data: no leading boundary: " +
                                                                                                                 line + " != " + boundary);
                 }
         }
 
         /**
          * エンコードを設定します。
          *
          * @param  encoding エンコード
          */
         public void setEncoding( final String encoding ) {
                  this.encoding = encoding;
          }
 
         /**
          * 次のパートを読み取ります。
          *
          * @og.rev 3.5.6.2 (2004/07/05) 文字列の連結にStringBuilderを使用します。
          *
          * @return      次のパート
          * @throws IOException
          */
         public Part readNextPart() throws IOException {
                 if(lastFilePart != null) {
                         Closer.ioClose( lastFilePart.getInputStream() );                // 4.0.0 (2006/01/31) close 処理時の IOException を無視
                         lastFilePart = null;
                 }
 
                 List<String> headers = new ArrayList<String>();
 
                 String line = readLine();
                 if(line == null) {
                         return null;
                 }
                 else if(line.length() == 0) {
                         return null;
                 }
 
                 while (line != null && line.length() > 0) {
                         String nextLine = null;
                         boolean getNextLine = true;
                         StringBuilder buf = new StringBuilder( 100 );
                         buf.append( line );
                         while (getNextLine) {
                                 nextLine = readLine();
                                 if(nextLine != null
                                                 && (nextLine.startsWith(" ")
                                                 || nextLine.startsWith("\t"))) {
                                         buf.append( nextLine );
                                 }
                                 else {
                                         getNextLine = false;
                                 }
                         }
 
                         headers.add(buf.toString());
                         line = nextLine;
                 }
 
                 if(line == null) {
                         return null;
                 }
 
                 String name = null;
                 String filename = null;
                 String origname = null;
                 String contentType = "text/plain";
 
                 for( String headerline : headers ) {
                         if(headerline.toLowerCase(Locale.JAPAN).startsWith("content-disposition:")) {
                                 String[] dispInfo = extractDispositionInfo(headerline);
 
                                 name = dispInfo[1];
                                 filename = dispInfo[2];
                                 origname = dispInfo[3];
                         }
                         else if(headerline.toLowerCase(Locale.JAPAN).startsWith("content-type:")) {
                                 String type = extractContentType(headerline);
                                 if(type != null) {
                                         contentType = type;
                                 }
                         }
                 }
 
                 if(filename == null) {
                         return new ParamPart(name, in, boundary, encoding);
                 }
                 else {
                         if( "".equals( filename ) ) {
                                 filename = null;
                         }
                         lastFilePart = new FilePart(name,in,boundary,contentType,filename,origname);
                         return lastFilePart;
                 }
         }
 
         /**
          * ローカル変数「境界」アクセス可能なフィールドを返します。
          *
          * @param       line    １行
          *
          * @return      境界文字列
          * @see         org.opengion.hayabusa.servlet.multipart.MultipartParser
          */
         private String extractBoundary( final String line ) {
                 // 4.0.0 (2005/01/31) The local variable "boundary" shadows an accessible field with the same name and compatible type in class org.opengion.hayabusa.servlet.multipart.MultipartParser
                 int index = line.lastIndexOf("boundary=");
                 if(index == -1) {
                         return null;
                 }
                 String bound = line.substring(index + 9);
                 if(bound.charAt(0) == '"') {
                         index = bound.lastIndexOf('"');
                         bound = bound.substring(1, index);
                 }
 
                 bound = "--" + bound;
 
                 return bound;
         }
 
         /**
          * コンテンツの情報を返します。
          *
          * @param       origline        元の行
          *
          * @return      コンテンツの情報配列
          * @throws IOException
          */
         private String[] extractDispositionInfo( final String origline ) throws IOException {
                 String[] retval = new String[4];
 
                 String line = origline.toLowerCase(Locale.JAPAN);
 
                 int start = line.indexOf("content-disposition: ");
                 int end = line.indexOf(';');
                 if(start == -1 || end == -1) {
                         throw new IOException("Content disposition corrupt: " + origline);
                 }
                 String disposition = line.substring(start + 21, end);
                 if(!"form-data".equals(disposition)) {
                         throw new IOException("Invalid content disposition: " + disposition);
                 }
 
                 start = line.indexOf("name=\"", end);   // start at last semicolon
                 end = line.indexOf("\"", start + 7);     // skip name=\"
                 if(start == -1 || end == -1) {
                         throw new IOException("Content disposition corrupt: " + origline);
                 }
                 String name = origline.substring(start + 6, end);
 
                 String filename = null;
                 String origname = null;
                 start = line.indexOf("filename=\"", end + 2);   // start after name
                 end = line.indexOf("\"", start + 10);                                   // skip filename=\"
                 if(start != -1 && end != -1) {                                                          // note the !=
                         filename = origline.substring(start + 10, end);
                         origname = filename;
                         int slash =
                                 Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
                         if(slash > -1) {
                                 filename = filename.substring(slash + 1);       // past last slash
                         }
                 }
 
                 retval[0] = disposition;
                 retval[1] = name;
                 retval[2] = filename;
                 retval[3] = origname;
                 return retval;
         }
 
         /**
          * コンテンツタイプの情報を返します。
          *
          * @param       origline        元の行
          *
          * @return      コンテンツタイプの情報
          * @throws IOException
          */
         private String extractContentType( final String origline ) throws IOException {
                 String contentType = null;
 
                 String line = origline.toLowerCase(Locale.JAPAN);
 
                 if(line.startsWith("content-type")) {
                         int start = line.indexOf(' ');
                         if(start == -1) {
                                 throw new IOException("Content type corrupt: " + origline);
                         }
                         contentType = line.substring(start + 1);
                 }
                 else if(line.length() > 0) {    // no content type, so should be empty
                         throw new IOException("Malformed line after disposition: " + origline);
                 }
 
                 return contentType;
         }
 
         /**
          * 行を読み取ります。
          *
          * @return      読み取られた１行分
          * @throws IOException
          */
         private String readLine() throws IOException {
                 StringBuilder sbuf = new StringBuilder();
                 int result;
 
                 do {
                         result = in.readLine(buf, 0, buf.length);
                         if(result != -1) {
                                 sbuf.append(new String(buf, 0, result, encoding));
                         }
                 } while (result == buf.length);
 
                 if(sbuf.length() == 0) {
                         return null;
                 }
 
                 // 4.0.0 (2005/01/31) The method StringBuilder.setLength() should be avoided in favor of creating a new StringBuilder.
                 String rtn = sbuf.toString();
                 int len = sbuf.length();
                 if(len >= 2 && sbuf.charAt(len - 2) == '\r') {
                         rtn = rtn.substring(0,len - 2);
                 }
                 else if(len >= 1 && sbuf.charAt(len - 1) == '\n') {
                         rtn = rtn.substring(0,len - 1);
                 }
                 return rtn ;
         }
 }