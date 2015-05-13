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
    public MediaLoader(Context context, int id) {
        super(context);
        this.id = id;
    }

    @Override
    public Article loadInBackground() {
        return TedRssDBManager.getArticle(id);
    }
}
