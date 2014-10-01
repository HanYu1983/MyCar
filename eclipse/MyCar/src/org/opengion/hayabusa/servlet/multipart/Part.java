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
 
 /**
  * ファイルアップロード時のマルチパート処理のパート部品です。
  *
  * パート部品の共通親クラスになります。
  *
  * @og.group その他機能
  *
  * @version  4.0
  * @author   Kazuhiko Hasegawa
  * @since    JDK5.0,
  */
 public class Part {
         private final String name;
 
         /**
          * 名前を指定して、構築するコンストラクター
          *
          * @param       name    名前
          */
         Part( final String name ) {
                 this.name = name;
         }
 
         /**
          * 名前を返します。
          *
          * @return      名前
          */
         public String getName() {
                 return name;
         }
 
         /**
          * ファイルかどうか
          *
          * @return      (常に false)
          */
         public boolean isFile() {
                 return false;
         }
 
         /**
          * パラメーターかどうか
          *
          * @return      (常に false)
          */
         public boolean isParam() {
                 return false;
         }
 }