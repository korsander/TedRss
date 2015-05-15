package ru.korsander.tedrss.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;

import java.util.ArrayList;

import ru.korsander.tedrss.TedRss;
import ru.korsander.tedrss.db.TedRssDBManager;
import ru.korsander.tedrss.model.Article;
import ru.korsander.tedrss.utils.Const;

/**
 * Created by korsander on 08.05.15.
 */
public class ArticlesLoader extends AsyncTaskLoader<ArrayList<Article>> {
    private ArrayList<Article> currentData;
    private ForceLoadContentObserver observer;
    public ArticlesLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Article> loadInBackground() {
        return TedRssDBManager.getArticles();
    }

    @Override
    public void deliverResult(ArrayList<Article> data) {
        if(isReset()) {
            releaseResources(data);
            return;
        }
        ArrayList<Article> oldData = currentData;
        currentData = data;
        if(isStarted()) {
            super.deliverResult(data);
        }

        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    @Override
    protected void onStartLoading() {
        if (currentData != null) {
            deliverResult(currentData);
        }

        if(observer == null) {
            observer = new ForceLoadContentObserver();
            TedRss.getContext().getContentResolver().registerContentObserver(Uri.parse(Const.ARRTICLES_TABLE_PATH), false, observer);
        }
        if (takeContentChanged() || currentData == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();

        if (currentData != null) {
            releaseResources(currentData);
        }
        if(observer != null) {
            TedRss.getContext().getContentResolver().unregisterContentObserver(observer);
            observer = null;
        }
    }

    @Override
    public void onCanceled(ArrayList<Article> data) {
        super.onCanceled(data);
        releaseResources(data);
    }

    private void releaseResources(ArrayList<Article> data) {
        data = null;
    }
}
