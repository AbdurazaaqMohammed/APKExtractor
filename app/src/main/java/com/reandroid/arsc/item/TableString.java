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

import com.reandroid.utils.collection.CollectionUtil;
import com.reandroid.arsc.value.Entry;
import com.reandroid.xml.StyleDocument;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class TableString extends StringItem {
    public TableString(boolean utf8) {
        super(utf8);
    }

    @Override
    public void serializeText(XmlSerializer serializer, boolean escapeValues) throws IOException {
        StyleDocument styleDocument = getStyleDocument();
        if(styleDocument == null){
            super.serializeText(serializer, escapeValues);
            return;
        }
        styleDocument.serialize(serializer);
    }
    public StyleDocument getStyleDocument(){
        StyleItem styleItem = getStyle();
        if(styleItem == null){
            return null;
        }
        return styleItem.build(get());
    }
    public Iterator<Entry> getEntries(boolean complex) {
        return super.getUsers(Entry.class, item -> {
            if(complex){
                return item.isComplex();
            }
            return item.isScalar();
        });
    }
    public Iterator<Entry> getEntries(Predicate<Entry> tester) {
        return super.getUsers(Entry.class, tester);
    }

    public List<Entry> listReferencedResValueEntries(){
        return CollectionUtil.toList(getEntries(false));
    }

    @Override
    public boolean hasReference(){
        if(!super.hasReference()){
            return false;
        }
        Collection<?> references = getReferencedList();
        if(references.size() > 1){
            return true;
        }
        Object referenceItem = references.iterator().next();
        return !(referenceItem instanceof StyleItem.StyleIndexReference);
    }
}
