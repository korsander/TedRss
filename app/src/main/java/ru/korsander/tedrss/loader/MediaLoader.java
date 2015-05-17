package ru.korsander.tedrss.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import ru.korsander.tedrss.db.TedRssDBManager;
import ru.korsander.tedrss.model.Article;

/**
 * Created by korsander on 13.05.2015.
 */
public class MediaLoader extends AsyncTaskLoader<Article> {
    private int id;
    private Article currentData;

    public MediaLoader(Context context, int id) {
        super(context);
        this.id = id;
    }

    @Override
    public Article loadInBackground() {
        return TedRssDBManager.getArticle(id);
    }

    @Override
    public void deliverResult(Article data) {
        if(isReset()) {
            releaseResources(data);
            return;
        }

        Article oldData = currentData;
        currentData = data;
        if(isStarted()) {
            super.deliverResult(currentData);
        }
        if(oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    @Override
    protected void onStartLoading() {
        if (currentData != null) {
            deliverResult(currentData);
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
    }

    @Override
    public void onCanceled(Article data) {
        super.onCanceled(data);
        releaseResources(data);
    }

    private void releaseResources(Article data) {
        data = null;
    }
}
