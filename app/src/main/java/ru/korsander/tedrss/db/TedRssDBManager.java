package ru.korsander.tedrss.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import ru.korsander.tedrss.TedRss;
import ru.korsander.tedrss.model.Article;
import ru.korsander.tedrss.model.Media;

/**
 * Created by korsander on 06.05.2015.
 */
public class TedRssDBManager {
    private static final String LOG_TAG = "DB_MANAGER";
    public static void insertUniqueArticles(ArrayList<Article> articles) {
        SQLiteDatabase db = TedRssDBHelper.getInstance(TedRss.getContext()).getWritableDatabase();
        StringBuilder builder = new StringBuilder();
        Article currentArticle;
        Media currentMedia;
        boolean flag = true;
        db.beginTransactionNonExclusive();
        try {
            builder.append("INSERT OR REPLACE INTO ").append(TedRssDBHelper.TABLE_ARTICLES).append(" VALUES(?,?,?,?,?,?,?,?)");
            SQLiteStatement statementAtricle = db.compileStatement(builder.toString());
            builder.setLength(0);
            builder.append("INSERT INTO ").append(TedRssDBHelper.TABLE_MEDIA).append(" VALUES (?,?,?,?,?)");
            SQLiteStatement statementMedia = db.compileStatement(builder.toString());
            for (int i = 0; i < articles.size(); i++) {
                currentArticle = articles.get(i);
                String viewed = currentArticle.isViewed() ? "true" : "false";
                statementAtricle.bindLong(1, currentArticle.getId());
                statementAtricle.bindString(2, currentArticle.getTitle());
                statementAtricle.bindString(3, currentArticle.getDescription());
                statementAtricle.bindString(4, currentArticle.getLink());
                statementAtricle.bindString(5, currentArticle.getThumb());
                statementAtricle.bindLong(6, currentArticle.getDuration());
                statementAtricle.bindLong(7, currentArticle.getDate());
                statementAtricle.bindString(8, viewed);
                statementAtricle.execute();
                for (int j = 0; j < currentArticle.getMedia().size(); j++) {
                    currentMedia = currentArticle.getMedia().get(j);
                    statementMedia.bindLong(1, currentMedia.getArticleId());
                    statementMedia.bindString(2, currentMedia.getUrl());
                    statementMedia.bindLong(3, currentMedia.getBitrate());
                    statementMedia.bindLong(4, currentMedia.getDuration());
                    statementMedia.bindLong(5, currentMedia.getSize());
                    statementMedia.execute();
                }
            }

        } catch (Exception e) {
            flag = false;
            Log.e(LOG_TAG, e.getMessage() != null ? e.getMessage() : e + "");
        } finally {
            Log.e(LOG_TAG, "close transaction");
            if(flag) db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
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
        Log.e(">", "start getting articles" + cursor.getCount());
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

    public static void exportDatabse() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//"+TedRss.getContext().getPackageName()+"//databases//"+TedRssDBHelper.DB_NAME+"";
                String backupDBPath = "tedrssbackup.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            Log.e("DBManager", e.getMessage() != null ? e.getMessage() : e + "");
        }
    }

}
