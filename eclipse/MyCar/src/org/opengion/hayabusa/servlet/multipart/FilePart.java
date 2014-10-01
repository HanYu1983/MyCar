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
 
 import org.opengion.fukurou.util.Closer ;
 
 import java.io.File;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.BufferedOutputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import javax.servlet.ServletInputStream;
 
 /**
  * ファイルアップロード時のマルチパート処理のファイルパート部品です。
  *
  * ファイル情報を取り扱います。
  *
  * @og.group その他機能
  *
  * @version  4.0
  * @author   Kazuhiko Hasegawa
  * @since    JDK5.0,
  */
 public class FilePart extends Part {
 
         private String filename;
         private final String filePath;
         private final String contentType;
         private final PartInputStream partInput;
 
         /**
          * ファイルパート部品 オブジェクトを構築する、コンストラクター
          *
          * @param       name            Part名称
          * @param       in                      ServletInputStreamオブジェクト
          * @param       boundary        境界文字
          * @param       contentType     コンテンツタイプ
          * @param       filename        ファイル名
          * @param       filePath        ファイルパス
          * @throws IOException
          */
         FilePart( final String name, final ServletInputStream in, final String boundary,
                         final String contentType, final String filename, final String filePath)
                                                                                         throws IOException {
                 super(name);
                 this.filename = filename;
                 this.filePath = filePath;
                 this.contentType = contentType;
                 partInput = new PartInputStream(in, boundary);
         }
 
         /**
          * ファイル名を取得します。
          *
          * @return      ファイル名
          */
         public String getFilename() {
                 return filename;
         }
 
         /**
          * ファイル名をセットします。
          *
          * @param  fname ファイル名
          */
         public void setFilename( final String fname ) {
                 filename = fname ;
         }
 
         /**
          * ファイルパスを取得します。
          *
          * @return      ファイルパス
          */
         public String getFilePath() {
                 return filePath;
         }
 
         /**
          * コンテンツタイプを取得します。
          *
          * @return      コンテンツタイプ
          */
         public String getContentType() {
                 return contentType;
         }
 
         /**
          * 入力ストリームを取得します。
          *
          * @return      入力ストリーム
          */
         public InputStream getInputStream() {
                 return partInput;
         }
 
         /**
          * 指定のファイルに書き出します。
          *
          * @param       fileOrDirectory 出力先ファイル名/ディレクトリ名
          *
          * @return      ストリームに書き出したバイト数
          * @throws  IOException
          */
         public long writeTo( final File fileOrDirectory ) throws IOException {
                 long written = 0;
 
                 OutputStream fileOut = null;
                 try {
                         // Only do something if this part contains a file
                         if(filename != null) {
                                 // Check if user supplied directory
                                 File file;
                                 if(fileOrDirectory.isDirectory()) {
                                         // Write it to that dir the user supplied,
                                         // with the filename it arrived with
                                         file = new File(fileOrDirectory, filename);
                                 }
                                 else {
                                         // Write it to the file the user supplied,
                                         // ignoring the filename it arrived with
                                         file = fileOrDirectory;
                                 }
                                 fileOut = new BufferedOutputStream(new FileOutputStream(file));
                                 written = write(fileOut);
                         }
                 }
                 finally {
                         Closer.ioClose( fileOut );              // 4.0.0 (2006/01/31) close 処理時の IOException を無視
                 }
                 return written;
         }
 
         /**
          * 指定のストリームに書き出します。
          *
          * @param       out     OutputStreamオブジェクト
          *
          * @return      ストリームに書き出したバイト数
          * @throws  IOException
          */
         long write( final OutputStream out ) throws IOException {
                 // decode macbinary if this was sent
                 long size=0;
                 int read;
                 byte[] buf = new byte[8 * 1024];
                 while((read = partInput.read(buf)) != -1) {
                         out.write(buf, 0, read);
                         size += read;
                 }
                 return size;
         }
 
         /**
          * ファイルかどうか
          *
          * @return      (常に true)
          */
         @Override
         public boolean isFile() {
                 return true;
         }
 }