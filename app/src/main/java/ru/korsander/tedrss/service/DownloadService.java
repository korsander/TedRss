package ru.korsander.tedrss.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import ru.korsander.tedrss.db.TedRssDBManager;
import ru.korsander.tedrss.model.Article;
import ru.korsander.tedrss.parser.TedXmlParser;
import ru.korsander.tedrss.utils.Const;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DownloadService extends IntentService {
    private static final String LOG_TAG = "DownloadService";
    private static final String ACTION_LOAD = "ru.korsander.tedrss.service.action.LOAD";

//    // TODO: Rename parameters
//    private static final String EXTRA_PARAM1 = "ru.korsander.tedrss.service.extra.PARAM1";
//    private static final String EXTRA_PARAM2 = "ru.korsander.tedrss.service.extra.PARAM2";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startLoad(Context context) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_LOAD);
        context.startService(intent);
    }

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOAD.equals(action)) {
                handleActionLoad();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionLoad() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        try {
            if (networkInfo != null && networkInfo.isConnected()) {
                URL url = new URL(Const.RSS_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(150000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(LOG_TAG, "The response is: " + response);
                ArrayList<Article> articles = TedXmlParser.parse(conn.getInputStream());
                TedRssDBManager.insertUniqueArticles(articles);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
