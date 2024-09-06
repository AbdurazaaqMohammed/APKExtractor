/*
 * Copyright (c) 2002 JSON.org (now "Public Domain")
 * This is NOT property of REAndroid
 * This package is renamed from org.json.* to avoid class conflict when used on anroid platforms
*/
package com.reandroid.json;

public class JSONML {

    private static Object parse(
        XMLTokener x,
        boolean    arrayForm,
        JSONArray  ja,
        boolean keepStrings
    ) throws JSONException {
        String     attribute;
        char       c;
        String     closeTag = null;
        int        i;
        JSONArray  newja = null;
        JSONObject newjo = null;
        Object     token;
        String     tagName = null;

// Test for and skip past these forms:
//      <!-- ... -->
//      <![  ... ]]>
//      <!   ...   >
//      <?   ...  ?>

        while (true) {
            if (!x.more()) {
                throw x.syntaxError("Bad XML");
            }
            token = x.nextContent();
            if (token == XML.LT) {
                token = x.nextToken();
                if (token instanceof Character) {
                    if (token == XML.SLASH) {

// Close tag </

                        token = x.nextToken();
                        if (!(token instanceof String)) {
                            throw new JSONException(
                                    "Expected a closing name instead of '" +
                                    token + "'.");
                        }
                        if (x.nextToken() != XML.GT) {
                            throw x.syntaxError("Misshaped close tag");
                        }
                        return token;
                    } else if (token == XML.BANG) {

// <!

                        c = x.next();
                        if (c == '-') {
                            if (x.next() == '-') {
                                x.skipPast("-->");
                            } else {
                                x.back();
                            }
                        } else if (c == '[') {
                            token = x.nextToken();
                            if (token.equals("CDATA") && x.next() == '[') {
                                if (ja != null) {
                                    ja.put(x.nextCDATA());
                                }
                            } else {
                                throw x.syntaxError("Expected 'CDATA['");
                            }
                        } else {
                            i = 1;
                            do {
                                token = x.nextMeta();
                                if (token == null) {
                                    throw x.syntaxError("Missing '>' after '<!'.");
                                } else if (token == XML.LT) {
                                    i += 1;
                                } else if (token == XML.GT) {
                                    i -= 1;
                                }
                            } while (i > 0);
                        }
                    } else if (token == XML.QUEST) {

// <?

                        x.skipPast("?>");
                    } else {
                        throw x.syntaxError("Misshaped tag");
                    }

// Open tag <

                } else {
                    if (!(token instanceof String)) {
                        throw x.syntaxError("Bad tagName '" + token + "'.");
                    }
                    tagName = (String)token;
                    newja = new JSONArray();
                    newjo = new JSONObject();
                    if (arrayForm) {
                        newja.put(tagName);
                        if (ja != null) {
                            ja.put(newja);
                        }
                    } else {
                        newjo.put("tagName", tagName);
                        if (ja != null) {
                            ja.put(newjo);
                        }
                    }
                    token = null;
                    for (;;) {
                        if (token == null) {
                            token = x.nextToken();
                        }
                        if (token == null) {
                            throw x.syntaxError("Misshaped tag");
                        }
                        if (!(token instanceof String)) {
                            break;
                        }

// attribute = value

                        attribute = (String)token;
                        if (!arrayForm && ("tagName".equals(attribute) || "childNode".equals(attribute))) {
                            throw x.syntaxError("Reserved attribute.");
                        }
                        token = x.nextToken();
                        if (token == XML.EQ) {
                            token = x.nextToken();
                            if (!(token instanceof String)) {
                                throw x.syntaxError("Missing value");
                            }
                            newjo.accumulate(attribute, keepStrings ? token :XML.stringToValue((String)token));
                            token = null;
                        } else {
                            newjo.accumulate(attribute, "");
                        }
                    }
                    if (arrayForm && newjo.length() > 0) {
                        newja.put(newjo);
                    }

// Empty tag <.../>

                    if (token == XML.SLASH) {
                        if (x.nextToken() != XML.GT) {
                            throw x.syntaxError("Misshaped tag");
                        }
                        if (ja == null) {
                            if (arrayForm) {
                                return newja;
                            }
                            return newjo;
                        }

// Content, between <...> and </...>

                    } else {
                        if (token != XML.GT) {
                            throw x.syntaxError("Misshaped tag");
                        }
                        closeTag = (String)parse(x, arrayForm, newja, keepStrings);
                        if (closeTag != null) {
                            if (!closeTag.equals(tagName)) {
                                throw x.syntaxError("Mismatched '" + tagName +
                                        "' and '" + closeTag + "'");
                            }
                            tagName = null;
                            if (!arrayForm && newja.length() > 0) {
                                newjo.put("childNodes", newja);
                            }
                            if (ja == null) {
                                if (arrayForm) {
                                    return newja;
                                }
                                return newjo;
                            }
                        }
                    }
                }
            } else {
                if (ja != null) {
                    ja.put(token instanceof String
                        ? keepStrings ? XML.unescape((String)token) :XML.stringToValue((String)token)
                        : token);
                }
            }
        }
    }
    public static JSONArray toJSONArray(String string) throws JSONException {
        return (JSONArray)parse(new XMLTokener(string), true, null, false);
    }
    public static JSONArray toJSONArray(String string, boolean keepStrings) throws JSONException {
        return (JSONArray)parse(new XMLTokener(string), true, null, keepStrings);
    }
    public static JSONArray toJSONArray(XMLTokener x, boolean keepStrings) throws JSONException {
        return (JSONArray)parse(x, true, null, keepStrings);
    }
    public static JSONArray toJSONArray(XMLTokener x) throws JSONException {
        return (JSONArray)parse(x, true, null, false);
    }

    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject using the JsonML transform. Each XML tag is represented as
     * a JSONObject with a "tagName" property. If the tag has attributes, then
     * the attributes will be in the JSONObject as properties. If the tag
     * contains children, the object will have a "childNodes" property which
     * will be an array of strings and JsonML JSONObjects.

     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     * @param string The XML source text.
     * @return A JSONObject containing the structured data from the XML string.
     * @throws JSONException Thrown on error converting to a JSONObject
     */
    public static JSONObject toJSONObject(String string) throws JSONException {
        return (JSONObject)parse(new XMLTokener(string), false, null, false);
    }
    
    
    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject using the JsonML transform. Each XML tag is represented as
     * a JSONObject with a "tagName" property. If the tag has attributes, then
     * the attributes will be in the JSONObject as properties. If the tag
     * contains children, the object will have a "childNodes" property which
     * will be an array of strings and JsonML JSONObjects.

     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     * @param string The XML source text.
     * @param keepStrings If true, then values will not be coerced into boolean
     *  or numeric values and will instead be left as strings
     * @return A JSONObject containing the structured data from the XML string.
     * @throws JSONException Thrown on error converting to a JSONObject
     */
    public static JSONObject toJSONObject(String string, boolean keepStrings) throws JSONException {
        return (JSONObject)parse(new XMLTokener(string), false, null, keepStrings);
    }

    
    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject using the JsonML transform. Each XML tag is represented as
     * a JSONObject with a "tagName" property. If the tag has attributes, then
     * the attributes will be in the JSONObject as properties. If the tag
     * contains children, the object will have a "childNodes" property which
     * will be an array of strings and JsonML JSONObjects.

     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     * @param x An XMLTokener of the XML source text.
     * @return A JSONObject containing the structured data from the XML string.
     * @throws JSONException Thrown on error converting to a JSONObject
     */
    public static JSONObject toJSONObject(XMLTokener x) throws JSONException {
           return (JSONObject)parse(x, false, null, false);
    }

    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject using the JsonML transform. Each XML tag is represented as
     * a JSONObject with a "tagName" property. If the tag has attributes, then
     * the attributes will be in the JSONObject as properties. If the tag
     * contains children, the object will have a "childNodes" property which
     * will be an array of strings and JsonML JSONObjects.

     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     * @param x An XMLTokener of the XML source text.
     * @param keepStrings If true, then values will not be coerced into boolean
     *  or numeric values and will instead be left as strings
     * @return A JSONObject containing the structured data from the XML string.
     * @throws JSONException Thrown on error converting to a JSONObject
     */
    public static JSONObject toJSONObject(XMLTokener x, boolean keepStrings) throws JSONException {
           return (JSONObject)parse(x, false, null, keepStrings);
    }
    public static String toString(JSONArray ja) throws JSONException {
        int                 i;
        JSONObject jo;
        int                 length;
        Object              object;
        StringBuilder        sb = new StringBuilder();
        String              tagName;

// Emit <tagName

        tagName = ja.getString(0);
        XML.noSpace(tagName);
        tagName = XML.escape(tagName);
        sb.append('<');
        sb.append(tagName);

        object = ja.opt(1);
        if (object instanceof JSONObject) {
            i = 2;
            jo = (JSONObject)object;

// Emit the attributes

            // Don't use the new entrySet API to maintain Android support
            for (final String key : jo.keySet()) {
                final Object value = jo.opt(key);
                XML.noSpace(key);
                if (value != null) {
                    sb.append(' ');
                    sb.append(XML.escape(key));
                    sb.append('=');
                    sb.append('"');
                    sb.append(XML.escape(value.toString()));
                    sb.append('"');
                }
            }
        } else {
            i = 1;
        }

// Emit content in body

        length = ja.length();
        if (i >= length) {
            sb.append('/');
            sb.append('>');
        } else {
            sb.append('>');
            do {
                object = ja.get(i);
                i += 1;
                if (object != null) {
                    if (object instanceof String) {
                        sb.append(XML.escape(object.toString()));
                    } else if (object instanceof JSONObject) {
                        sb.append(toString((JSONObject)object));
                    } else if (object instanceof JSONArray) {
                        sb.append(toString((JSONArray)object));
                    } else {
                        sb.append(object);
                    }
                }
            } while (i < length);
            sb.append('<');
            sb.append('/');
            sb.append(tagName);
            sb.append('>');
        }
        return sb.toString();
    }

    public static String toString(JSONObject jo) throws JSONException {
        StringBuilder sb = new StringBuilder();
        int                 i;
        JSONArray           ja;
        int                 length;
        Object              object;
        String              tagName;
        Object              value;

//Emit <tagName

        tagName = jo.optString("tagName");
        if (tagName == null) {
            return XML.escape(jo.toString());
        }
        XML.noSpace(tagName);
        tagName = XML.escape(tagName);
        sb.append('<');
        sb.append(tagName);

//Emit the attributes

        // Don't use the new entrySet API to maintain Android support
        for (final String key : jo.keySet()) {
            if (!"tagName".equals(key) && !"childNodes".equals(key)) {
                XML.noSpace(key);
                value = jo.opt(key);
                if (value != null) {
                    sb.append(' ');
                    sb.append(XML.escape(key));
                    sb.append('=');
                    sb.append('"');
                    sb.append(XML.escape(value.toString()));
                    sb.append('"');
                }
            }
        }

//Emit content in body

        ja = jo.optJSONArray("childNodes");
        if (ja == null) {
            sb.append('/');
            sb.append('>');
        } else {
            sb.append('>');
            length = ja.length();
            for (i = 0; i < length; i += 1) {
                object = ja.get(i);
                if (object != null) {
                    if (object instanceof String) {
                        sb.append(XML.escape(object.toString()));
                    } else if (object instanceof JSONObject) {
                        sb.append(toString((JSONObject)object));
                    } else if (object instanceof JSONArray) {
                        sb.append(toString((JSONArray)object));
                    } else {
                        sb.append(object);
                    }
                }
            }
            sb.append('<');
            sb.append('/');
            sb.append(tagName);
            sb.append('>');
        }
        return sb.toString();
    }
}
