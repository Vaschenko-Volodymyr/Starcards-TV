package tv.starcards.starcardstv.application.ui.vlc;

import tv.starcards.starcardstv.R;
import tv.starcards.starcardstv.application.API;
import tv.starcards.starcardstv.application.http.HttpGetWithPacketToken;
import tv.starcards.starcardstv.application.util.Parser;
import tv.starcards.starcardstv.application.util.SystemUiHider;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaList;

import java.lang.ref.WeakReference;
import java.security.Timestamp;
import java.util.Date;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenVlcPlayer extends Activity implements SurfaceHolder.Callback, IVideoPlayer {

    private static final String TAG = "FullscreenVlcPlayer";

    private String              packetId;
    private String              channelId;
    private String              urlToStream;
    private String              url;

    // Display Surface
    private LinearLayout        vlcContainer;
    private SurfaceView         mSurface;
    private SurfaceHolder       holder;

    // Overlay / Controls
    private FrameLayout         vlcOverlay;
    private ImageView           vlcButtonPlayPause;
    private Handler             handlerOverlay;
    private Runnable            runnableOverlay;
    private Handler             handlerSeekbar;
    private Runnable            runnableSeekbar;
    private SeekBar             vlcSeekbar;
    private TextView            vlcDuration;
    private TextView            overlayTitle;

    // media player
    private LibVLC              libvlc;
    private int                 mVideoWidth;
    private int                 mVideoHeight;
    private final static int    VideoSizeChanged = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve our url
        Bundle b = getIntent().getExtras();
        urlToStream = b.getString("url", null);
        packetId = b.getString("packetId");
        packetId = packetId.substring(2, packetId.length());
        channelId = b.getString("id");
        url = API.PACKETS_SUMMARY + "/" + packetId + API.TV_CHANNELS + "/" + channelId + "/" + "epg?from=";

        // HIDE THE ACTION BAR
        // getActionBar().hide();

        // SETUP THE UI
        setContentView(R.layout.activity_fullscreen_vlc_player);

        // VLC
        vlcContainer = (LinearLayout) findViewById(R.id.vlc_container);
        mSurface = (SurfaceView) findViewById(R.id.vlc_surface);


        // OVERLAY / CONTROLS
        vlcOverlay = (FrameLayout) findViewById(R.id.vlc_overlay);
        vlcButtonPlayPause = (ImageView) findViewById(R.id.vlc_button_play_pause);
        vlcSeekbar = (SeekBar) findViewById(R.id.vlc_seekbar);
        vlcDuration = (TextView) findViewById(R.id.vlc_duration);

        overlayTitle = (TextView) findViewById(R.id.vlc_overlay_title);
        overlayTitle.setText(urlToStream);


        requestUrl();
        // AUTOSTART
        //playMovie();

    }

    private void requestUrl() {
        Date date = new Date();
        Long start = System.currentTimeMillis()/1000;
        Long end = start + 3600;
        url = url + start + "&to=" + end;
        Log.d(TAG, url);
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest req = new HttpGetWithPacketToken(Request.Method.GET, url , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            handleUrl(response);
                        } catch (JSONException e) {
                            Log.w("JSONException", "wrong parsing in fillPackets");
                        }
                        Log.w("TV LINK", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Packet request error", error.toString());
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        rq.add(req);
    }

    private void handleUrl(JSONObject response) throws JSONException {
        Log.d(TAG, "EPG response - " + response.toString());
//        Parser parser = new Parser();
//        String result = "{" + parser.parse(response.toString(), "results") + "}";
//        String localUrl = parser.parse(result, "url");
//        Log.w("url", localUrl);
//        urlToStream = localUrl;
//        playMovie();
    }


    private void showOverlay() {
        vlcOverlay.setVisibility(View.VISIBLE);
    }

    private void hideOverlay() {
        vlcOverlay.setVisibility(View.GONE);
    }

    private void setupControls() {
        //getActionBar().hide();
        // PLAY PAUSE
        vlcButtonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (libvlc.isPlaying()) {
                    libvlc.pause();
                    vlcButtonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play_over_video));
                } else {
                    libvlc.play();
                    vlcButtonPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause_over_video));
                }
            }
        });

        // SEEKBAR
        handlerSeekbar = new Handler();
        runnableSeekbar = new Runnable() {
            @Override
            public void run() {
                if (libvlc != null) {
                    long curTime = libvlc.getTime();
                    long totalTime = (long) (curTime / libvlc.getPosition());
                    int minutes = (int) (curTime / (60 * 1000));
                    int seconds = (int) ((curTime / 1000) % 60);
                    int endMinutes = (int) (totalTime / (60 * 1000));
                    int endSeconds = (int) ((totalTime / 1000) % 60);
                    String duration = String.format("%02d:%02d / %02d:%02d", minutes, seconds, endMinutes, endSeconds);
                    vlcSeekbar.setProgress((int) (libvlc.getPosition() * 100));
                    vlcDuration.setText(duration);
                }
                handlerSeekbar.postDelayed(runnableSeekbar, 1000);
            }
        };

        runnableSeekbar.run();
        vlcSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.v("NEW POS", "pos is : " + i);
                //if (i != 0)
                //    libvlc.setPosition(((float) i / 100.0f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // OVERLAY
        handlerOverlay = new Handler();
        runnableOverlay = new Runnable() {
            @Override
            public void run() {
                vlcOverlay.setVisibility(View.GONE);
                toggleFullscreen(true);
            }
        };
        final long timeToDisappear = 3000;
        handlerOverlay.postDelayed(runnableOverlay, timeToDisappear);
        vlcContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vlcOverlay.setVisibility(View.VISIBLE);

                handlerOverlay.removeCallbacks(runnableOverlay);
                handlerOverlay.postDelayed(runnableOverlay, timeToDisappear);
            }
        });
    }

    public void playMovie() {
        if (libvlc != null && libvlc.isPlaying())
            return ;
        vlcContainer.setVisibility(View.VISIBLE);
        holder = mSurface.getHolder();
        holder.addCallback(this);
        createPlayer(urlToStream);
    }


    private void toggleFullscreen(boolean fullscreen)
    {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        if (fullscreen)
        {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            vlcContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        else
        {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        getWindow().setAttributes(attrs);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setSize(mVideoWidth, mVideoHeight);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    /**
     * **********
     * Surface
     * ***********
     */

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int format,
                               int width, int height) {
        if (libvlc != null)
            libvlc.attachSurface(surfaceholder.getSurface(), this);
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
    }

    private void setSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        if (mVideoWidth * mVideoHeight <= 1)
            return;

        // get screen size
        int w = getWindow().getDecorView().getWidth();
        int h = getWindow().getDecorView().getHeight();

        // getWindow().getDecorView() doesn't always take orientation into
        // account, we have to correct the values
        boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (w > h && isPortrait || w < h && !isPortrait) {
            int i = w;
            w = h;
            h = i;
        }

        float videoAR = (float) mVideoWidth / (float) mVideoHeight;
        float screenAR = (float) w / (float) h;

        if (screenAR < videoAR)
            h = (int) (w / videoAR);
        else
            w = (int) (h * videoAR);

        // force surface buffer size
        if (holder != null)
            holder.setFixedSize(mVideoWidth, mVideoHeight);

        // set display size
        ViewGroup.LayoutParams lp = mSurface.getLayoutParams();
        lp.width = w;
        lp.height = h;
        mSurface.setLayoutParams(lp);
        mSurface.invalidate();
    }

    @Override
    public void setSurfaceSize(int width, int height, int visible_width,
                               int visible_height, int sar_num, int sar_den) {
        Message msg = Message.obtain(mHandler, VideoSizeChanged, width, height);
        msg.sendToTarget();
    }

    /**
     * **********
     * Player
     * ***********
     */

    private void createPlayer(String media) {
        releasePlayer();
        setupControls();
        try {
            if (media.length() > 0) {
                Toast toast = Toast.makeText(this, media, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
                        0);
                toast.show();
            }

            // Create a new media player
            libvlc = LibVLC.getInstance();
            libvlc.setHardwareAcceleration(LibVLC.HW_ACCELERATION_FULL);
            libvlc.eventVideoPlayerActivityCreated(true);
            libvlc.setSubtitlesEncoding("");
            libvlc.setAout(LibVLC.AOUT_OPENSLES);
            libvlc.setTimeStretching(true);
            libvlc.setChroma("RV32");
            libvlc.setVerboseMode(true);
            LibVLC.restart(this);
            EventHandler.getInstance().addHandler(mHandler);
            holder.setFormat(PixelFormat.RGBX_8888);
            holder.setKeepScreenOn(true);
            MediaList list = libvlc.getMediaList();
            list.clear();
            list.add(new Media(libvlc, LibVLC.PathToURI(media)), false);
            libvlc.playIndex(0);
        } catch (Exception e) {
            Toast.makeText(this, "Could not create Vlc Player", Toast.LENGTH_LONG).show();
        }
    }

    private void releasePlayer() {
        if (handlerSeekbar != null && runnableSeekbar != null)
            handlerSeekbar.removeCallbacks(runnableSeekbar);
        EventHandler.getInstance().removeHandler(mHandler);
        if (libvlc == null)
            return;
        libvlc.stop();
        libvlc.detachSurface();
        holder = null;
        libvlc.closeAout();

        mVideoWidth = 0;
        mVideoHeight = 0;
    }

    /**
     * **********
     * Events
     * ***********
     */

    private Handler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private WeakReference<FullscreenVlcPlayer> mOwner;

        public MyHandler(FullscreenVlcPlayer owner) {
            mOwner = new WeakReference<FullscreenVlcPlayer>(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            FullscreenVlcPlayer player = mOwner.get();

            // Player events
            if (msg.what == VideoSizeChanged) {
                player.setSize(msg.arg1, msg.arg2);
                return;
            }

            // Libvlc events
            Bundle b = msg.getData();
            switch (b.getInt("event")) {
                case EventHandler.MediaPlayerEndReached:
                    player.releasePlayer();
                    break;
                case EventHandler.MediaPlayerPlaying:
                case EventHandler.MediaPlayerPaused:
                case EventHandler.MediaPlayerStopped:
                default:
                    break;
            }
        }
    }

}
