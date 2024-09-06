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
package com.reandroid.archive.writer;

import com.reandroid.archive.Archive;
import com.reandroid.archive.block.DataDescriptor;
import com.reandroid.archive.block.LocalFileHeader;
import com.reandroid.archive.block.ZipHeader;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZipAligner {
    private final Map<Pattern, Integer> alignmentMap;
    private int defaultAlignment;
    private boolean enableDataDescriptor;

    public ZipAligner(){
        alignmentMap = new HashMap<>();
    }

    public void setFileAlignment(Pattern patternFileName, int alignment){
        if(patternFileName == null){
            return;
        }
        alignmentMap.remove(patternFileName);
        if(alignment > 1){
            alignmentMap.put(patternFileName, alignment);
        }
    }
    public void clearFileAlignment(){
        alignmentMap.clear();
    }
    public void setDefaultAlignment(int defaultAlignment) {
        if(defaultAlignment <= 0){
            defaultAlignment = 1;
        }
        this.defaultAlignment = defaultAlignment;
    }
    public void setEnableDataDescriptor(boolean enableDataDescriptor) {
        this.enableDataDescriptor = enableDataDescriptor;
    }

    public void align(long offset, LocalFileHeader lfh){
        if(ZipHeader.isZip64Length(offset + lfh.getSize())){
            return;
        }
        lfh.setZipAlign(0);
        int padding;
        if(lfh.getMethod() != Archive.STORED){
            padding = 0;
            createDataDescriptor(lfh);
        }else {
            int alignment = getAlignment(lfh.getFileName());
            long dataOffset = offset + lfh.countBytes();
            padding = (int) ((alignment - (dataOffset % alignment)) % alignment);
        }
        lfh.setZipAlign(padding);
    }
    private void createDataDescriptor(LocalFileHeader lfh){
        DataDescriptor dataDescriptor;
        if(false){
            dataDescriptor = DataDescriptor.fromLocalFile(lfh);
        }else {
            dataDescriptor = null;
        }
        lfh.setDataDescriptor(dataDescriptor);
    }
    private int getAlignment(String name){
        for(Map.Entry<Pattern, Integer> entry:alignmentMap.entrySet()){
            Matcher matcher = entry.getKey().matcher(name);
            if(matcher.matches()){
                return entry.getValue();
            }
        }
        return defaultAlignment;
    }

    public static ZipAligner apkAligner(){
        ZipAligner zipAligner = new ZipAligner();
        zipAligner.setDefaultAlignment(ALIGNMENT_4);
        Pattern patternNativeLib = Pattern.compile("^lib/.+\\.so$");
        zipAligner.setFileAlignment(patternNativeLib, ALIGNMENT_PAGE);
        zipAligner.setEnableDataDescriptor(false);
        return zipAligner;
    }

    private static final int ALIGNMENT_4 = 4;
    private static final int ALIGNMENT_PAGE = 4096;
}
