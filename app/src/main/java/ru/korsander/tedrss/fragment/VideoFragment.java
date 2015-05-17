package ru.korsander.tedrss.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

import ru.korsander.tedrss.R;
import ru.korsander.tedrss.TedRss;
import ru.korsander.tedrss.activity.MainActivity;
import ru.korsander.tedrss.loader.MediaLoader;
import ru.korsander.tedrss.model.Article;
import ru.korsander.tedrss.utils.Const;
import ru.korsander.tedrss.view.VideoControllerView;

public class VideoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Article>, SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControl, View.OnTouchListener, MediaPlayer.OnBufferingUpdateListener{
    public  static final String FRAGMENT_NAME = "VideoFragment";
    private static final String POSITION = "position";
    private static final String ARG_ARTICLE_ID = "id";
    private static final int LOADER_MEDIA = 2;
    private int articleId;
    private Article article;

    private SurfaceView videoSurface;
    private MediaPlayer player;
    private VideoControllerView controller;
    private View rootView;
    private int currentBufferPercent;
    private TextView tvTitle;
    private TextView tvDesc;
    private TextView tvPublished;
    private ImageButton ibView;

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
        getLoaderManager().initLoader(LOADER_MEDIA, bundle, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_video, container, false);
        videoSurface = (SurfaceView) rootView.findViewById(R.id.videoSurface);
        rootView.findViewById(R.id.videoSurfaceContainer).setOnTouchListener(this);
        rootView.findViewById(R.id.infoLayout).setVisibility(View.INVISIBLE);
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);

        player = new MediaPlayer();
        if(savedInstanceState != null) {
            player.seekTo(savedInstanceState.getInt(POSITION));
        }
        controller = new VideoControllerView(getActivity());
        return rootView;
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
    public void onStart() {
        super.onStart();
        ((MainActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MainActivity)getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(player != null && player.isPlaying()) outState.putInt(POSITION, player.getCurrentPosition());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        FrameLayout container = (FrameLayout) rootView.findViewById(R.id.videoSurfaceContainer);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            container.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            container.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        handleAspectRatio();
        controller.updateFullScreen();
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
            updateData();
        }
    }


    @Override
    public void onLoaderReset(Loader<Article> loader) {
        if(loader.getId() == LOADER_MEDIA) {

        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void start() {
        player.start();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public int getDuration() {
        return player.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return currentBufferPercent;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public boolean isFullScreen() {
        return getActivity().getResources().getConfiguration().orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    @Override
    public void toggleFullScreen() {
        if(getActivity().getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) rootView.findViewById(R.id.videoSurfaceContainer));
        player.setOnBufferingUpdateListener(this);
        player.start();
        handleAspectRatio();
        rootView.findViewById(R.id.progress).setVisibility(View.GONE);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        controller.show();
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        currentBufferPercent = i;
    }

    private void handleAspectRatio() {
        if(getActivity() != null) {
            Point point  = new Point();
            getActivity().getWindowManager().getDefaultDisplay().getSize(point);
            int videoWidth = player.getVideoWidth();
            int videoHeight = player.getVideoHeight();
            float videoProportion = (float) videoWidth / (float) videoHeight;
            int screenWidth = point.x;
            int screenHeight = point.y;
            float screenProportion = (float) screenWidth / (float) screenHeight;
            android.view.ViewGroup.LayoutParams lp = videoSurface.getLayoutParams();

            if (videoProportion > screenProportion) {
                lp.width = screenWidth;
                lp.height = (int) ((float) screenWidth / videoProportion);
            } else {
                lp.width = (int) (videoProportion * (float) screenHeight);
                lp.height = screenHeight;
            }
            videoSurface.setLayoutParams(lp);
        }
    }

    private void updateData() {
        if(article != null) {
            tvTitle = (TextView) rootView.findViewById(R.id.tvVideoTitle);
            tvDesc = (TextView) rootView.findViewById(R.id.tvVideoDescription);
            tvPublished = (TextView) rootView.findViewById(R.id.tvVideoPublished);
            ibView = (ImageButton) rootView.findViewById(R.id.ibViewInBrowser);

            tvTitle.setText(article.getTitle());
            tvDesc.setText(article.getDescription());
            StringBuilder builder = new StringBuilder();
            builder.append(getActivity().getString(R.string.article_published)).append(" ").append(article.getFormattedDate(Const.RFC1123_SHORT_DATE_PATTERN));
            tvPublished.setText(builder.toString());
            ibView.setTag(article.getLink());
            rootView.findViewById(R.id.infoLayout).setVisibility(View.VISIBLE);

            ibView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = (String) view.getTag();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse(url));
                    TedRss.getContext().startActivity(intent);
                }
            });
            try {
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(article.getMedia().get(0).getUrl());
                player.setOnPreparedListener(this);
                player.setDisplay(videoSurface.getHolder());
                player.prepareAsync();
                currentBufferPercent = 0;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
