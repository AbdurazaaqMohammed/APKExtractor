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
package com.reandroid.utils;

import java.util.Arrays;
import java.util.Comparator;

public class CompareUtil {

    public static<T extends Comparable<T>> void sort(T[] items) {
        if(items == null || items.length < 2){
            return;
        }
        Arrays.sort(items, getComparableComparator());
    }
    public static<T extends Comparable<? super T>> int compare(T[] items1, T[] items2) {
        if(items1 == items2){
            return 0;
        }
        boolean empty1 = isEmpty(items1);
        boolean empty2 = isEmpty(items2);
        if(empty1 && empty2){
            return 0;
        }
        if(empty1){
            return -1;
        }
        if(empty2){
            return 1;
        }
        int length1 = items1.length;
        int length2 = items2.length;
        int length = length1;
        if(length > length2){
            length = length2;
        }
        for(int i = 0; i < length; i++){
            int compare = compare(items1[i], items2[i]);
            if(compare != 0){
                return compare;
            }
        }
        return Integer.compare(length1, length2);
    }
    private static boolean isEmpty(Object[] objects){
        return objects == null || objects.length == 0;
    }
    public static int compare(int i1, int i2){
        if(i1 == i2){
            return 0;
        }
        if(i1 > i2){
            return 1;
        }
        return -1;
    }
    public static<T extends Comparable<? super T>> int compare(T item1, T item2) {
        if(item1 == item2){
            return 0;
        }
        if(item1 == null){
            return -1;
        }
        if(item2 == null){
            return 1;
        }
        int i = item1.compareTo(item2);
        if(i == 0){
            return 0;
        }
        if(i > 0){
            return 1;
        }
        return -1;
    }
    @SuppressWarnings("unchecked")
    public static <T> Comparator<T> getComparatorUnchecked(){
        return (Comparator<T>) COMPARATOR;
    }

    @SuppressWarnings("unchecked")
    public static <E, T extends Comparable<E>> Comparator<T> getComparableComparator(){
        return (Comparator<T>) COMPARATOR;
    }
    @SuppressWarnings("unchecked")
    public static <T1> Comparator<T1> getToStringComparator() {
        return (Comparator<T1>) TO_STRING_COMPARATOR;
    }

    public static final Comparator<String> STRING_COMPARATOR = CompareUtil::compare;
    private static final Comparator<?> TO_STRING_COMPARATOR = StringsUtil::compareToString;

    @SuppressWarnings("unchecked")
    private static final Comparator<Comparable<?>> COMPARATOR = new Comparator<Comparable<?>>() {
        @Override
        public int compare(Comparable comparable1, Comparable comparable2) {
            return CompareUtil.compare(comparable1, comparable2);
        }
    };
}
