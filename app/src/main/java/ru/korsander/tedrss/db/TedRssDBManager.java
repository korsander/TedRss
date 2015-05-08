package ru.korsander.tedrss.db;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import ru.korsander.tedrss.TedRss;
import ru.korsander.tedrss.model.Article;
import ru.korsander.tedrss.model.Media;

/**
 * Created by korsander on 06.05.2015.
 */
public class TedRssDBManager {
    private static final String LOG_TAG = "DB_MANAGER";
    private static final String COMMA = ", ";
    public static void insertUniqueArticles(ArrayList<Article> articles) {
        SQLiteDatabase db = TedRssDBHelper.getInstance(TedRss.getContext()).getWritableDatabase();
        StringBuilder builder = new StringBuilder();
        Article currentArticle;
        Media currentMedia;
        boolean flag = true;
        db.beginTransaction();
        try {
            for (int i = 0; i < articles.size(); i++) {
                currentArticle = articles.get(i);
                String viewed = currentArticle.isViewed() ? "true" : "false";
                builder.setLength(0);
                builder.append("INSERT OR REPLACE INTO ").append(TedRssDBHelper.TABLE_ARTICLES).append(" VALUES (")
                        .append(currentArticle.getId()).append(COMMA)
                        .append(DatabaseUtils.sqlEscapeString(currentArticle.getTitle())).append(COMMA)
                        .append(DatabaseUtils.sqlEscapeString(currentArticle.getDescription())).append(COMMA)
                        .append(DatabaseUtils.sqlEscapeString(currentArticle.getLink())).append(COMMA)
                        .append(DatabaseUtils.sqlEscapeString(currentArticle.getThumb())).append(COMMA)
                        .append(currentArticle.getDuration()).append(COMMA)
                        .append(currentArticle.getDate()).append(COMMA)
                        .append(DatabaseUtils.sqlEscapeString(viewed)).append(");");
                //Log.e(LOG_TAG, builder.toString());
                db.rawQuery(builder.toString(), null);
                for (int j = 0; j < currentArticle.getMedia().size(); j++) {
                    currentMedia = currentArticle.getMedia().get(j);
                    builder.setLength(0);
                    builder.append("INSERT INTO ").append(TedRssDBHelper.TABLE_MEDIA).append(" VALUES (")
                            .append(currentMedia.getArticleId()).append(COMMA)
                            .append(DatabaseUtils.sqlEscapeString(currentMedia.getUrl())).append(COMMA)
                            .append(currentMedia.getBitrate()).append(COMMA).append(currentMedia.getDuration()).append(COMMA)
                            .append(currentMedia.getSize()).append(");");
                    //Log.e(LOG_TAG, builder.toString());
                    db.rawQuery(builder.toString(), null);
                }
            }
        } catch (Exception e) {
            flag = false;
            Log.e(LOG_TAG, e.getMessage() != null ? e.getMessage() : e + "");
        } finally {
            if(flag) db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

    public static ArrayList<Article> getArticles() {
        SQLiteDatabase db = TedRssDBHelper.getInstance(TedRss.getContext()).getReadableDatabase();
        Log.e(">", "start getting articles");
        ArrayList<Article> articles = new ArrayList<Article>();

        StringBuilder builder = new StringBuilder();
        builder.append("SELECT * FROM ");
        builder.append(TedRssDBHelper.TABLE_ARTICLES);

        Cursor cursor =  db.rawQuery(builder.toString(), null);
        try {
            if (cursor != null && cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    Article article = new Article();
                    article.setId(cursor.getInt(cursor.getColumnIndex(TedRssDBHelper.ARTICLE_ID)));
                    article.setTitle(cursor.getString(cursor.getColumnIndex(TedRssDBHelper.ARTICLE_TITLE)));
                    article.setDescription(cursor.getString(cursor.getColumnIndex(TedRssDBHelper.ARTICLE_DESC)));
                    article.setLink(cursor.getString(cursor.getColumnIndex(TedRssDBHelper.ARTICLE_LINK)));
                    article.setThumb(cursor.getString(cursor.getColumnIndex(TedRssDBHelper.ARTICLE_THUMB)));
                    article.setDuration(cursor.getInt(cursor.getColumnIndex(TedRssDBHelper.ARTICLE_DURATION)));
                    article.setDate(cursor.getLong(cursor.getColumnIndex(TedRssDBHelper.ARTICLE_DATE)));
                    article.setViewed(cursor.getString(cursor.getColumnIndex(TedRssDBHelper.ARTICLE_VIEWED)));
                    articles.add(article);
                }
            }
        } catch (Exception e) {
            Log.e("getArticles", e.getMessage() != null ? e.getMessage() : e+"");
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
        Log.e(">", "stop get article " + articles.size());
        return articles;
    }

}
