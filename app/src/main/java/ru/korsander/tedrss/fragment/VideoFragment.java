package ru.korsander.tedrss.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import ru.korsander.tedrss.R;
import ru.korsander.tedrss.model.Article;

public class VideoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Article>{
    private static final String ARG_ARTICLE_ID = "id";
    private static final int LOADER_MEDIA = 2;
    private int articleId;
    private Article article;

    private VideoView videoView;

    private OnFragmentInteractionListener mListener;

    public static VideoFragment newInstance(int id) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ARTICLE_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            articleId = getArguments().getInt(ARG_ARTICLE_ID);
        }

        Bundle bundle = new Bundle();
        getLoaderManager().initLoader(LOADER_ARTICLES, bundle, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        videoView = (VideoView) view.findViewById(R.id.videoView);
        getLoaderManager().getLoader(LOADER_ARTICLES).forceLoad();
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Article> onCreateLoader(int i, Bundle bundle) {
        Loader<Article> loader = null;
        Log.e(">", "load start");
        if(i == LOADER_MEDIA) {
            loader = new MediaLoader(TedRss.getContext(), articleId);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Article> loader, Article article) {
        if(loader.getId() == LOADER_MEDIA) {
            Log.e(">", "load finish");
            this.article = article;
            videoView.setVideoURI(Uri.parse(article.getMedia().get(0).getUrl()));
            videoView.setMediaController(new MediaController(getActivity()));
            videoView.requestFocus(0);
            videoView.start()
        }
    }

    @Override
    public void onLoaderReset(Loader<Article> loader) {
        if(loader.getId() == LOADER_MEDIA) {

        }
    }
}
