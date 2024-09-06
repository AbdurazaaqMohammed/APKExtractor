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
package com.reandroid.arsc.array;

import com.reandroid.arsc.item.IntegerItem;
import com.reandroid.arsc.item.SpecString;

public class SpecStringArray extends StringArray<SpecString> {
    public SpecStringArray(OffsetArray offsets, IntegerItem itemCount, IntegerItem itemStart, boolean is_utf8) {
        super(offsets, itemCount, itemStart, is_utf8);
    }
    @Override
    protected boolean isFlexible(){
        return true;
    }
    @Override
    public SpecString newInstance() {
        return new SpecString(isUtf8());
    }
    @Override
    public SpecString[] newArrayInstance(int length) {
        if(length == 0){
            return EMPTY;
        }
        return new SpecString[length];
    }

    private static final SpecString[] EMPTY = new SpecString[0];
}
