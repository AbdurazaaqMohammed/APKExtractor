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
package com.reandroid.xml;

import com.reandroid.common.FileChannelInputStream;
import com.starry.FileUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class XMLFactory {

    public static XmlPullParser newPullParser(String xmlContent) throws XmlPullParserException {
        XmlPullParser parser = newPullParser();
        StringReader reader = new StringReader(xmlContent);
        parser.setInput(reader);
        return parser;
    }
    public static XmlPullParser newPullParser(File file) throws XmlPullParserException {
        XmlPullParser parser = newPullParser();
        try {
            parser.setInput(new FileChannelInputStream(file), StandardCharsets.UTF_8.name());
        } catch (IOException ex) {
            throw new XmlPullParserException(ex.getMessage());
        }
        return parser;
    }
    public static XmlPullParser newPullParser(Reader reader) throws XmlPullParserException {
        XmlPullParser parser = newPullParser();
        parser.setInput(reader);
        return parser;
    }
    public static XmlPullParser newPullParser(InputStream inputStream) throws XmlPullParserException {
        XmlPullParser parser = newPullParser();
        parser.setInput(inputStream, StandardCharsets.UTF_8.name());
        return parser;
    }
    public static XmlPullParser newPullParser(){
        return new CloseableParser();
    }

    public static XmlSerializer newSerializer(Writer writer) throws IOException{
        XmlSerializer serializer = newSerializer();
        serializer.setOutput(writer);
        return serializer;
    }
    public static XmlSerializer newSerializer(File file) throws IOException{
        File dir = file.getParentFile();
        if(dir != null && !dir.exists()){
            dir.mkdirs();
        }
        return newSerializer(FileUtils.getOutputStream(file));
    }
    public static XmlSerializer newSerializer(OutputStream outputStream) throws IOException{
        XmlSerializer serializer = newSerializer();
        serializer.setOutput(outputStream, StandardCharsets.UTF_8.name());
        return serializer;
    }
    public static XmlSerializer newSerializer(){
        return new CloseableSerializer();
    }
}
