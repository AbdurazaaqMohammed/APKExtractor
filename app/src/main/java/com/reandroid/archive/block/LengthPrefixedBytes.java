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
package com.reandroid.archive.block;

import com.reandroid.arsc.item.ByteArray;

public class LengthPrefixedBytes extends LengthPrefixedBlock{
    private final ByteArray byteArray;
    public LengthPrefixedBytes(boolean is_long) {
        super(1, is_long);
        this.byteArray = new ByteArray();
        addChild(this.byteArray);
    }

    public ByteArray getByteArray() {
        return byteArray;
    }
    @Override
    protected void onSizeLoaded(int dataSize){
        this.byteArray.setSize(dataSize);
    }
}
