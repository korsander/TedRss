package ru.korsander.tedrss.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import ru.korsander.tedrss.R;
import ru.korsander.tedrss.TedRss;
import ru.korsander.tedrss.model.Article;
import ru.korsander.tedrss.model.Media;
import ru.korsander.tedrss.utils.Const;

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
            SQLiteStatement statementArticle = db.compileStatement(builder.toString());
            builder.setLength(0);
            builder.append("INSERT OR REPLACE INTO ").append(TedRssDBHelper.TABLE_MEDIA).append(" VALUES (?,?,?,?,?,?)");
            SQLiteStatement statementMedia = db.compileStatement(builder.toString());
            for (int i = 0; i < articles.size(); i++) {
                currentArticle = articles.get(i);
                String viewed = currentArticle.isViewed() ? "true" : "false";
                statementArticle.bindLong(1, currentArticle.getId());
                statementArticle.bindString(2, currentArticle.getTitle());
                statementArticle.bindString(3, currentArticle.getDescription());
                statementArticle.bindString(4, currentArticle.getLink());
                statementArticle.bindString(5, currentArticle.getThumb());
                statementArticle.bindLong(6, currentArticle.getDuration());
                statementArticle.bindLong(7, currentArticle.getDate());
                statementArticle.bindString(8, viewed);
                statementArticle.execute();
                for (int j = 0; j < currentArticle.getMedia().size(); j++) {
                    currentMedia = currentArticle.getMedia().get(j);
                    statementMedia.bindLong(1, currentMedia.getId());
                    statementMedia.bindLong(2, currentMedia.getArticleId());
                    statementMedia.bindString(3, currentMedia.getUrl());
                    statementMedia.bindLong(4, currentMedia.getBitrate());
                    statementMedia.bindLong(5, currentMedia.getDuration());
                    statementMedia.bindLong(6, currentMedia.getSize());
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
            TedRss.getContext().getContentResolver().notifyChange(Uri.parse(Const.ARRTICLES_TABLE_PATH), null);
            TedRss.getContext().getSharedPreferences(TedRss.getContext().getString(R.string.default_sp_filename), Context.MODE_PRIVATE)
                    .edit().putLong(Const.LAST_UPDATED, System.currentTimeMillis()).apply();
        }
    }

    public static ArrayList<Article> getArticles() {
        SQLiteDatabase db = TedRssDBHelper.getInstance(TedRss.getContext()).getReadableDatabase();
        Log.e(">", "start getting articles");
        ArrayList<Article> articles = new ArrayList<Article>();

        StringBuilder builder = new StringBuilder();
        builder.append("SELECT * FROM ");
        builder.append(TedRssDBHelper.TABLE_ARTICLES);
        builder.append(" ORDER BY ").append(TedRssDBHelper.ARTICLE_DATE).append(" DESC");

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

    public static Article getArticle(int id) {
        SQLiteDatabase db = TedRssDBHelper.getInstance(TedRss.getContext()).getReadableDatabase();
        Article result = new Article();
        ArrayList<Media> array = new ArrayList<Media>();

        StringBuilder builder = new StringBuilder();
        builder.append("SELECT * FROM ").append(TedRssDBHelper.TABLE_ARTICLES)
                .append(" WHERE ").append(TedRssDBHelper.ARTICLE_ID).append(" = ")
                .append(id).append(" LIMIT 1");
        Cursor cursor =  db.rawQuery(builder.toString(), null);
        try {
            if (cursor != null && cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    result.setId(cursor.getInt(cursor.getColumnIndex(TedRssDBHelper.ARTICLE_ID)));
                    result.setTitle(cursor.getString(cursor.getColumnIndex(TedRssDBHelper.ARTICLE_TITLE)));
                    result.setDescription(cursor.getString(cursor.getColumnIndex(TedRssDBHelper.ARTICLE_DESC)));
                    result.setLink(cursor.getString(cursor.getColumnIndex(TedRssDBHelper.ARTICLE_LINK)));
                    result.setThumb(cursor.getString(cursor.getColumnIndex(TedRssDBHelper.ARTICLE_THUMB)));
                    result.setDuration(cursor.getInt(cursor.getColumnIndex(TedRssDBHelper.ARTICLE_DURATION)));
                    result.setDate(cursor.getLong(cursor.getColumnIndex(TedRssDBHelper.ARTICLE_DATE)));
                    result.setViewed(cursor.getString(cursor.getColumnIndex(TedRssDBHelper.ARTICLE_VIEWED)));
                }
            }
            builder.setLength(0);
            builder.append("SELECT * FROM ").append(TedRssDBHelper.TABLE_MEDIA)
                    .append(" WHERE ").append(TedRssDBHelper.MEDIA_ARTICLE_ID).append(" = ")
                    .append(id).append(" ORDER BY ").append(TedRssDBHelper.MEDIA_BITRATE).append(" ASC");
            cursor = db.rawQuery(builder.toString(), null);
            if (cursor != null && cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    Media currentMedia = new Media();
                    currentMedia.setId(cursor.getInt(cursor.getColumnIndex(TedRssDBHelper.MEDIA_ID)));
                    currentMedia.setArticleId(cursor.getInt(cursor.getColumnIndex(TedRssDBHelper.MEDIA_ARTICLE_ID)));
                    currentMedia.setUrl(cursor.getString(cursor.getColumnIndex(TedRssDBHelper.MEDIA_URL)));
                    currentMedia.setBitrate(cursor.getInt(cursor.getColumnIndex(TedRssDBHelper.MEDIA_BITRATE)));
                    currentMedia.setDuration(cursor.getInt(cursor.getColumnIndex(TedRssDBHelper.MEDIA_DURATION)));
                    currentMedia.setSize(cursor.getLong(cursor.getColumnIndex(TedRssDBHelper.MEDIA_SIZE)));
                    array.add(currentMedia);
                }
            }
            result.setMedia(array);
        } catch(Exception e) {
            Log.e("getArticles", e.getMessage() != null ? e.getMessage() : e+"");
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
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
