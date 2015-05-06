package ru.korsander.tedrss.db;

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
        db.beginTransaction();
        for(int i = 0; i < articles.size(); i++) {
            currentArticle = articles.get(i);
            String viewed = currentArticle.isViewed() ? "true" : "false";
            builder.setLength(0);
            builder.append("INSERT OR UPDATE INTO ").append(TedRssDBHelper.TABLE_ARTICLES).append(" VALUES (")
                    .append(currentArticle.getId()).append(COMMA).append(currentArticle.getTitle()).append(COMMA)
                    .append(currentArticle.getDescription()).append(COMMA).append(currentArticle.getLink()).append(COMMA)
                    .append(currentArticle.getThumb()).append(COMMA).append(currentArticle.getDuration()).append(COMMA)
                    .append(currentArticle.getDate()).append(COMMA).append(viewed).append(");");
            Log.e(LOG_TAG, builder.toString());
            db.rawQuery(builder.toString(), null);
            for(int j = 0; j < currentArticle.getMedia().size(); j++) {
                currentMedia = currentArticle.getMedia().get(j);
                builder.setLength(0);
                builder.append("INSERT INTO ").append(TedRssDBHelper.TABLE_MEDIA).append(" VALUES (")
                        .append(currentMedia.getArticleId()).append(COMMA).append(currentMedia.getUrl()).append(COMMA)
                        .append(currentMedia.getBitrate()).append(COMMA).append(currentMedia.getDuration()).append(");");
                Log.e(LOG_TAG, builder.toString());
                db.rawQuery(builder.toString(), null);
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

}
