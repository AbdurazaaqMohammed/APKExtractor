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
package com.reandroid.utils.collection;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

public class UniqueIterator<T> extends FilterIterator<T> {

    private Set<T> excludeSet;

    public UniqueIterator(Iterator<T> iterator, Predicate<? super T> filter){
        super(iterator, filter);
    }
    public UniqueIterator(Iterator<T> iterator){
        super(iterator);
    }

    public T exclude(T item) {
        if(item == null){
            return null;
        }
        Set<T> excludeSet = this.excludeSet;
        if(excludeSet == null){
            excludeSet = new HashSet<>();
            this.excludeSet = excludeSet;
        }
        excludeSet.add(item);
        return item;
    }

    @Override
    public T next() {
        return exclude(super.next());
    }

    @Override
    public boolean test(T item) {
        if(item == null){
            return false;
        }
        Set<T> excludeSet = this.excludeSet;
        return excludeSet == null || !excludeSet.contains(item);
    }
}
