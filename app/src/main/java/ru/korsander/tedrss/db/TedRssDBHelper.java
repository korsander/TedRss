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
package ru.korsander.tedrss.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TedRssDBHelper extends SQLiteOpenHelper {
    private Context context;

    public static final String DB_NAME = "tedrss";
    public static final int VERSION = 1;

    //Articles table
    public static final String TABLE_ARTICLES   = "articles";

    public static final String ARTICLE_ID       = "id";
    public static final String ARTICLE_TITLE    = "title";
    public static final String ARTICLE_DESC     = "description";
    public static final String ARTICLE_LINK     = "link";
    public static final String ARTICLE_THUMB    = "thumb";
    public static final String ARTICLE_DURATION = "duration";
    public static final String ARTICLE_DATE     = "date";
    public static final String ARTICLE_VIEWED   = "viewed";

    //Media table
    public static final String TABLE_MEDIA      = "media";

    public static final String MEDIA_ARTICLE_ID = "aid";
    public static final String MEDIA_URL        = "url";
    public static final String MEDIA_BITRATE    = "bitrate";
    public static final String MEDIA_DURATION   = "duration";
    public static final String MEDIA_SIZE       = "size";

    private static final String CREATE_ARTICLES = "CREATE TABLE " + TABLE_ARTICLES + " (" + ARTICLE_ID + " INTEGER UNIQUE, " +
            ARTICLE_TITLE + " TEXT, " + ARTICLE_DESC + " TEXT, " + ARTICLE_LINK + " TEXT, " + ARTICLE_THUMB + " TEXT, " +
            ARTICLE_DURATION + " INTEGER, " + ARTICLE_DATE + " INTEGER, " + ARTICLE_VIEWED + " TEXT)";

    private static final String CREATE_MEDIA = "CREATE TABLE " + TABLE_MEDIA + " (" + MEDIA_ARTICLE_ID + " INTEGER, " +
            MEDIA_URL + " TEXT, " + MEDIA_BITRATE + " INTEGER, " + MEDIA_DURATION + " INTEGER, " + MEDIA_SIZE + " INTEGER)";

    private static final String DELETE_ARTICLES = "DROP TABLE IF EXIST " + TABLE_ARTICLES;
    private static final String DELETE_MEDIA = "DROP TABLE IF EXIST " + TABLE_MEDIA;

    private static TedRssDBHelper sInstance;

    public static synchronized TedRssDBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TedRssDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private TedRssDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ARTICLES);
        db.execSQL(CREATE_MEDIA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(DELETE_ARTICLES);
        db.execSQL(DELETE_MEDIA);
        db.execSQL(CREATE_ARTICLES);
        db.execSQL(CREATE_MEDIA);
    }
}
