/*
 *  Copyright (C) 2022 github.com/REAndroid
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.reandroid.arsc.item;

import com.reandroid.utils.StringsUtil;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

public abstract class StringBlock extends BlockItem{

    private String mCache;

    public StringBlock() {
        super(0);
        mCache = "";
    }
    public String get(){
        return mCache;
    }
    public void set(String text){
        if(text == null || text.length() == 0){
            text = StringsUtil.EMPTY;
        }
        String old = this.mCache;
        if(text.equals(old) && countBytes() != 0){
            return;
        }
        this.mCache = text;
        byte[] bytes = encodeString(text);
        setBytesInternal(bytes, false);
        onStringChanged(old, text);
    }
    protected void onBytesChanged(){
        mCache = decodeString(getBytesInternal());
    }
    protected void onStringChanged(String old, String text){
    }
    protected abstract String decodeString(byte[] bytes);
    protected abstract byte[] encodeString(String text);

    public int compareTo(StringBlock stringBlock){
        if(stringBlock == null){
            return -1;
        }
        return StringsUtil.compareStrings(get(), stringBlock.get());
    }
    @Override
    public String toString() {
        return get();
    }

}
