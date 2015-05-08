package ru.korsander.tedrss.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

import ru.korsander.tedrss.db.TedRssDBManager;
import ru.korsander.tedrss.model.Article;

/**
 * Created by korsander on 08.05.15.
 */
public class ArticlesLoader extends AsyncTaskLoader<ArrayList<Article>> {
    public ArticlesLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Article> loadInBackground() {
        return TedRssDBManager.getArticles();
    }
}
