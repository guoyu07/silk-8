/*
 * Copyright 2012 Taro L. Saito
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//--------------------------------------
// XerialJ
//
// SilkIndentToken.java
// Since: 2011/06/06 11:06:59
//
// $URL$
// $Author$
//--------------------------------------
package xerial.silk.parser.token;

import xerial.silk.io.UTF8String;

/**
 * Token for representing an indent
 * 
 * @author leo
 * 
 */
public class SilkIndentToken extends SilkToken
{
    public final int indentLength;

    public SilkIndentToken(int indentLength) {
        super(SilkTokenType.Indent, 0);
        this.indentLength = indentLength;
    }

    @Override
    public CharSequence getText() {
        return UTF8String.format("<%d>", indentLength);
    }

}