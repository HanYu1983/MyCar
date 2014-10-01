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
 
 import java.io.FilterInputStream;
 import java.io.IOException;
 import javax.servlet.ServletInputStream;
 
 /**
  * ファイルアップロード時のマルチパート処理のファイル読取ストリームです。
  *
  * @og.group その他機能
  *
  * @version  4.0
  * @author   Kazuhiko Hasegawa
  * @since    JDK5.0,
  */
 public class PartInputStream extends FilterInputStream {
         private final String boundary;
         private final byte [] buf = new byte[64*1024];  // 64k
         private int count;
         private int pos;
         private boolean eof;
 
         /**
          * 読取ストリーム と区切り文字を指定してクラスを構築する コンストラクター
          *
          * @param       in                      ServletInputStreamオブジェクト
          * @param       boundary        境界文字
          */
         PartInputStream( final ServletInputStream in,
                                         final String boundary) throws IOException {
                 super(in);
                 this.boundary = boundary;
         }
 
         /**
          * データを埋めます。
          *
          * @throws IOException
          */
         private void fill() throws IOException {
                 if(eof) {
                         return;
                 }
                 // as long as we are not just starting up
                 if(count > 0) {
                         // if the caller left the requisite amount spare in the buffer
                         if(count - pos == 2) {
                                 // copy it back to the start of the buffer
                                 System.arraycopy(buf, pos, buf, 0, count - pos);
                                 count -= pos;
                                 pos = 0;
                         } else {
                                 // should never happen, but just in case
                                 throw new IllegalStateException("fill() detected illegal buffer state");
                         }
                 }
 
                 // try and fill the entire buffer, starting at count, line by line
                 // but never read so close to the end that we might split a boundary
                 int read;
                 int maxRead = buf.length - boundary.length();
                 while (count < maxRead) {
                         // read a line
                         read = ((ServletInputStream)in).readLine(buf, count, buf.length - count);
                         // check for eof and boundary
                         if(read == -1) {
                                 throw new IOException("unexpected end of part");
                         } else {
                                 if(read >= boundary.length()) {
                                         eof = true;
                                         for(int i=0; i < boundary.length(); i++) {
                                                 if(boundary.charAt(i) != buf[count + i]) {
                                                         // Not the boundary!
                                                         eof = false;
                                                         break;
                                                 }
                                         }
                                         if(eof) {
                                                 break;
                                         }
                                 }
                         }
                         // success
                         count += read;
                 }
         }
 
         /**
          * データを読み込みます。
          *
          * @return      読み取られたデータ
          * @throws IOException
          */
         @Override
         public int read() throws IOException {
                 if(count - pos <= 2) {
                         fill();
                         if(count - pos <= 2) {
                                 return -1;
                         }
                 }
                 return buf[pos++] & 0xff;
         }
 
         /**
          * データを読み込みます。
          *
          * @param       bt      バイト配列
          *
          * @return      読み取られたデータ
          * @throws IOException
          */
         @Override
         public int read( final byte[] bt ) throws IOException {
                 return read(bt, 0, bt.length);
         }
 
         /**
          * データを読み込みます。
          *
          * @param       bt      バイト配列
          * @param       off     開始バイト数
          * @param       len     読み取りバイト数
          *
          * @return      読み取られたデータ
          * @throws IOException
          */
         @Override
         public int read( final byte[] bt, final int off, final int len ) throws IOException {
                 int total = 0;
                 if(len == 0) {
                         return 0;
                 }
 
                 int avail = count - pos - 2;
                 if(avail <= 0) {
                         fill();
                         avail = count - pos - 2;
                         if(avail <= 0) {
                                 return -1;
                         }
                 }
                 int copy = Math.min(len, avail);
                 System.arraycopy(buf, pos, bt, off, copy);
                 pos += copy;
                 total += copy;
 
                 while (total < len) {
                         fill();
                         avail = count - pos - 2;
                         if(avail <= 0) {
                                 return total;
                         }
                         copy = Math.min(len - total, avail);
                         System.arraycopy(buf, pos, bt, off + total, copy);
                         pos += copy;
                         total += copy;
                 }
                 return total;
         }
 
         /**
          * 利用可能かどうかを返します。
          *
          * @return      利用可能かどうか
          * @throws IOException
          */
         @Override
         public int available() throws IOException {
                 int avail = (count - pos - 2) + in.available();
                 // Never return a negative value
                 return (avail < 0 ? 0 : avail);
         }
 
         /**
          * 接続を閉じます。
          *
          * @throws IOException
          */
         @Override
         public void close() throws IOException {
                 if(!eof) {
                         int size = read(buf, 0, buf.length);
                         while( size != -1) {
                                 size = read(buf, 0, buf.length);
                         }
                 }
         }
 }