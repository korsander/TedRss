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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import ru.korsander.tedrss.R;
import ru.korsander.tedrss.TedRss;
import ru.korsander.tedrss.activity.MainActivity;
import ru.korsander.tedrss.loader.MediaLoader;
import ru.korsander.tedrss.model.Article;
import ru.korsander.tedrss.model.Media;
import ru.korsander.tedrss.utils.Const;
import ru.korsander.tedrss.view.VideoControllerView;

public class VideoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Article>, SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,
        VideoControllerView.MediaPlayerControl, View.OnTouchListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener, AdapterView.OnItemSelectedListener {
    public  static final String FRAGMENT_NAME = "VideoFragment";
    private static final String POSITION = "position";
    private static final String ARG_ARTICLE_ID = "id";
    private static final String ARG_SPINNER_POSITION = "spinnerposition";
    private static final int LOADER_MEDIA = 2;
    private int articleId;
    private int spinnerPos;
    private int prevPosition = 0;
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

    public static VideoFragment newInstance(int id, int pos) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ARTICLE_ID, id);
        args.putInt(ARG_SPINNER_POSITION, pos);
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
            spinnerPos = getArguments().getInt(ARG_SPINNER_POSITION);
        }
        Bundle bundle = new Bundle();
        getLoaderManager().initLoader(LOADER_MEDIA, bundle, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_video, container, false);
        rootView.findViewById(R.id.videoSurfaceContainer).setOnTouchListener(this);
        rootView.findViewById(R.id.infoLayout).setVisibility(View.INVISIBLE);

        player = new MediaPlayer();
        if(savedInstanceState != null) {
            prevPosition = savedInstanceState.getInt(POSITION);
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
        player.reset();
        player.release();
        controller.setMediaPlayer(null);
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
        if(player == null)
        player = new MediaPlayer();
        if(controller == null)
        controller = new VideoControllerView(getActivity());
        videoSurface = (SurfaceView) rootView.findViewById(R.id.videoSurface);
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(player.isPlaying()) {
            player.pause();
            player.setOnBufferingUpdateListener(null);
        }

//        controller.setSpinnerClickListener(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (player != null) {
            outState.putInt(POSITION, player.getCurrentPosition());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null && player != null) {
            prevPosition = savedInstanceState.getInt(POSITION);
        }
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
            if(!player.isPlaying())
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
        player.setDisplay(holder);
        updateData();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //player.stop();
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
        ArrayList<String> mediasStr = new ArrayList<String>();
        for(Media media : article.getMedia()) {
            mediasStr.add(media.getBitrate() + "");
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(TedRss.getContext(), android.R.layout.simple_spinner_dropdown_item, mediasStr.toArray(new String[mediasStr.size()]));
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        Log.e("POS", spinnerPos + "");
        controller.setSpinnerAdapter(spinnerArrayAdapter);
        controller.setSpinnerSelectListener(this);
        controller.setPositionSpinner(spinnerPos);
        player.setOnBufferingUpdateListener(this);
        player.start();
        player.seekTo(prevPosition);
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
            Log.e("MP", "updateData pos:" + spinnerPos);
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
                player.setDataSource(article.getMedia().get(spinnerPos).getUrl());
                player.setOnPreparedListener(this);
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

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            player.reset();
        } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
            player.reset();
        }
        player.setOnErrorListener(this);
        player.setOnPreparedListener(this);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(getActivity() != null && i != spinnerPos) {
            Log.e("SPPOS", i + " = " + spinnerPos + "L:" + l);
            player.reset();
            player.setOnBufferingUpdateListener(null);
            getActivity().getFragmentManager().beginTransaction().remove(getActivity().getFragmentManager().findFragmentByTag(VideoFragment.FRAGMENT_NAME)).commit();
            getActivity().getFragmentManager().popBackStack();
            getActivity().getFragmentManager().beginTransaction().replace(R.id.container, VideoFragment.newInstance(articleId, i), VideoFragment.FRAGMENT_NAME).addToBackStack(VideoFragment.FRAGMENT_NAME).commit();
            spinnerPos = i;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.e("SPPOS", "nothing");
    }
}
