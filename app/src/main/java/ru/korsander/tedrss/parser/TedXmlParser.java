/**
 * Created by korsander on 04.05.2015.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.korsander.tedrss.parser;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;

import ru.korsander.tedrss.model.Article;
import ru.korsander.tedrss.model.Media;

public class TedXmlParser {
    private static final String ITEM = "item";
    private static final String TITLE = "title";
    private static final String LINK = "link";
    private static final String DESC = "description";
    private static final String DATE = "pubDate";
    private static final String GUID = "guid";
    private static final String NS_ITUNES = "itunes";
    private static final String IMAGE = "image";
    private static final String ATTR_URL = "url";
    private static final String DURATION = "duration";
    private static final String NS_MEDIA = "media";
    private static final String GROUP = "group";
    private static final String CONTENT = "content";
    private static final String ATTR_DURATION = "duration";
    private static final String ATTR_BITRATE = "bitrate";
    private static final String ATTR_SIZE = "fileSize";
    private static final String CHANNEL = "channel";

    private static final String LOG_TAG = "parser";

    public TedXmlParser() {

    }

    public ArrayList<Article> parse(InputStream stream) {
        ArrayList<Article> result = null;
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(stream, null);
            int eventType = parser.getEventType();
            Article currentItem = null;
            ArrayList<Media> medias = null;
            Media currentMedia = null;
            boolean done = false;
            while(eventType != XmlPullParser.END_DOCUMENT && !done) {
                String name = "";
                String namespace = "";
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        result = new ArrayList<Article>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        namespace = parser.getPrefix();
                        if(name.equalsIgnoreCase(ITEM)) {
                            currentItem = new Article();
                        } else if(currentItem != null) {
                            if(name.equalsIgnoreCase(TITLE)) {
                                currentItem.setTitle(parser.nextText());
                            } else if(name.equalsIgnoreCase(LINK)) {
                                currentItem.setLink(parser.nextText());
                            } else if(name.equalsIgnoreCase(DESC)) {
                                currentItem.setDescription(parser.nextText());
                            } else if(name.equalsIgnoreCase(DATE)) {
                                currentItem.setDate(parser.nextText());
                            } else if(name.equalsIgnoreCase(GUID)) {
                                currentItem.setId(parser.nextText());
                            } else if(namespace.equalsIgnoreCase(NS_ITUNES)) {
                                if(name.equalsIgnoreCase(IMAGE)) {
                                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                                        if (parser.getAttributeName(i).equalsIgnoreCase(ATTR_URL)) {
                                            currentItem.setThumb(parser.getAttributeValue(i));
                                            break;
                                        }
                                    }
                                } else if(name.equalsIgnoreCase(DURATION)) {
                                    currentItem.setDuration(parser.nextText());
                                }
                            }  else if(namespace.equalsIgnoreCase(NS_MEDIA)) {
                                if(name.equalsIgnoreCase(GROUP)) {
                                    medias = new ArrayList<>(7);
                                } else if(medias != null) {
                                    if(name.equalsIgnoreCase(CONTENT)){
                                        currentMedia = new Media();
                                        for(int i = 0; i < parser.getAttributeCount(); i++) {
                                            String attr = parser.getAttributeName(i);
                                            if(attr.equalsIgnoreCase(ATTR_URL)) {
                                                currentMedia.setUrl(parser.getAttributeValue(i));
                                            } else if(attr.equalsIgnoreCase(ATTR_BITRATE)) {
                                                currentMedia.setBitrate(Integer.parseInt(parser.getAttributeValue(i)));
                                            } else if(attr.equalsIgnoreCase(ATTR_DURATION)) {
                                                currentMedia.setDuration(Integer.parseInt(parser.getAttributeValue(i)));
                                            } else if(attr.equalsIgnoreCase(ATTR_SIZE)) {
                                                currentMedia.setSize(Long.parseLong(parser.getAttributeValue(i)));
                                            }
                                        }
                                        medias.add(currentMedia);
                                    }
                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(name.equalsIgnoreCase(ITEM)) {
                            result.add(currentItem);
                        } else if(name.equalsIgnoreCase(CHANNEL)) {
                            done = true;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch(Exception e) {
            Log.e(LOG_TAG, e.getMessage() != null ? e.getMessage() : e + "");
        }
        return result;
    }
}
