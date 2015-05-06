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
package ru.korsander.tedrss.model;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.korsander.tedrss.utils.Const;

public class Article {
    private int id;
    private String title;
    private String description;
    private String link;
    private String thumb;
    private int duration;
    private long date;
    private boolean viewed;
    private ArrayList<Media> media;

    private static final String LOG_TAG = "model.article";

    public Article() {
        id = -1;
        title = new String();
        description = new String();
        link = new String();
        thumb = new String();
        duration = -1;
        date = -1;
        viewed = false;
        media = new ArrayList<Media>(7);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setId(String guid) {
        Pattern pattern = Pattern.compile("\\d+:\\d+");
        Matcher matcher = pattern.matcher(guid);
        matcher.find();
        String match = matcher.group();
        String[] array = match.split(":");
        StringBuilder builder = new StringBuilder();
        for(int i = (array.length-1); i >= 0; i--) {
            builder.append(array[i]);
        }
        this.id = Integer.parseInt(builder.toString());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setDuration(String duration) {
        String[] array = duration.split(":");
        int hours = Integer.parseInt(array[0]);
        int min = Integer.parseInt(array[1]);
        int sec = Integer.parseInt(array[2]);
        int result = (hours * 60 * 60) + (min * 60) + sec;
        this.duration = result;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Const.RFC1123_DATE_PATTERN, Locale.US);
        try {
            this.date = dateFormat.parse(date).getTime();
        } catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage() != null ? e.getMessage() : e + "");
        }
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public ArrayList<Media> getMedia() {
        return media;
    }

    public void setMedia(ArrayList<Media> media) {
        this.media = media;
    }
}
