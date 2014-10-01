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
 
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.UnsupportedEncodingException;
 import javax.servlet.ServletInputStream;
 
 /**
  * ファイルアップロード時のマルチパート処理のパラメータパート部品です。
  *
  * パラメータ情報を取り扱います。
  *
  * @og.group その他機能
  *
  * @version  4.0
  * @author   Kazuhiko Hasegawa
  * @since    JDK5.0,
  */
 public class ParamPart extends Part {
         private byte[] value;
         private final String encoding;
 
         /**
          * パラメータパート部品 オブジェクトを構築する、コンストラクター
          *
          * @param       name            パラメータの名前
          * @param       in                      ServletInputStreamオブジェクト
          * @param       boundary        境界文字
          * @param       encoding        エンコード
          * @throws IOException
          */
         ParamPart( final String name, final ServletInputStream in,
                                 final String boundary, final String encoding) throws IOException {
                 super(name);
                 this.encoding = encoding;
 
                 // Copy the part's contents into a byte array
 
                 PartInputStream pis = null;
                 ByteArrayOutputStream baos = null;
                 try {
                         pis = new PartInputStream(in, boundary);
                         baos = new ByteArrayOutputStream(512);
                         byte[] buf = new byte[128];
                         int read;
                         while ((read = pis.read(buf)) != -1) {
                                 baos.write(buf, 0, read);
                         }
                         value = baos.toByteArray();
                 }
                 finally {
                         Closer.ioClose( pis );          // 4.0.0 (2006/01/31) close 処理時の IOException を無視
                         Closer.ioClose( baos );         // 4.0.0 (2006/01/31) close 処理時の IOException を無視
                 }
         }
 
         /**
          * 値をバイト配列で返します。
          *
          * @return  値のバイト配列
          */
         public byte[] getValue() {
                 if( value != null ) {
                         return value.clone();
                 }
                 else {
                         return new byte[0];             // 3.6.0.0 (2004/09/22)
                 }
         }
 
         /**
          * 値を文字列で返します。
          *
          * @return      このクラスの初期エンコードに対応した文字列
          * @throws UnsupportedEncodingException
          */
         public String getStringValue() throws UnsupportedEncodingException {
                 return getStringValue(encoding);
         }
 
         /**
          * エンコードを与えて、値を文字列に変換して返します。
          *
          * @param       encoding        エンコード
          *
          * @return      エンコードに対応した文字列
          * @throws UnsupportedEncodingException
          */
         public String getStringValue( final String encoding ) throws UnsupportedEncodingException {
                 return new String(value, encoding);
         }
 
         /**
          * パラメーターかどうか
          *
          * @return      (常に true)
          */
         @Override
         public boolean isParam() {
                 return true;
         }
 }