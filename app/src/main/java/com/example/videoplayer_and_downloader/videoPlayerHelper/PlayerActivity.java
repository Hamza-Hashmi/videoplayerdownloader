package com.example.videoplayer_and_downloader.videoPlayerHelper;

import static com.example.videoplayer_and_downloader.Utils.MyViewsExtentionKt.hideStatusBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.audiofx.LoudnessEnhancer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.LocaleList;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.accessibility.CaptioningManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videoplayer_and_downloader.R;
import com.example.videoplayer_and_downloader.Utils.Base;
import com.example.videoplayer_and_downloader.Utils.CommonKt;
import com.example.videoplayer_and_downloader.adsimplementation.ads_utils.InterstitialAdUpdated;
import com.example.videoplayer_and_downloader.models.MediaModel;
import com.example.videoplayer_and_downloader.videoPlayerHelper.BrightnessControl;
import com.example.videoplayer_and_downloader.videoPlayerHelper.CustomStyledPlayerView;
import com.example.videoplayer_and_downloader.videoPlayerHelper.Prefs;
import com.example.videoplayer_and_downloader.videoPlayerHelper.Utils;
import com.example.videoplayer_and_downloader.videoPlayerHelper.dtpv.DoubleTapPlayerView;
import com.example.videoplayer_and_downloader.videoPlayerHelper.dtpv.youtube.YouTubeOverlay;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioListener;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.CaptionStyleCompat;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.NonNullApi;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class PlayerActivity extends Base {

    private static final long FAST_FORWARD_TIME = 5000;
    private static final long FAST_REWIND_TIME = 5000;
    private PlaybackStateListener playbackStateListener;
    private BroadcastReceiver mReceiver;
    private AudioManager mAudioManager;
    private MediaSessionCompat mediaSession;

    private DefaultTrackSelector trackSelector;
    public static LoudnessEnhancer loudnessEnhancer;

    private CustomStyledPlayerView playerView;
    OrientationEventListener orientationEventListener;
    public static SimpleExoPlayer player;
    TextView titleView;


    public Prefs mPrefs;
    @SuppressLint("StaticFieldLeak")
    public static BrightnessControl mBrightnessControl;
    public static boolean haveMedia;
    private boolean setTracks;
    public static boolean controllerVisible;
    public static boolean controllerVisibleFully;
    public static Snackbar snackbar;
    private ExoPlaybackException errorToShow;
    public static int boostLevel = 0;

    private static final int REQUEST_CHOOSER_VIDEO = 1;
    private static final int REQUEST_CHOOSER_SUBTITLE = 2;
    private static final int REQUEST_CHOOSER_SCOPE_DIR = 10;
    public static final int CONTROLLER_TIMEOUT = 3500;
    private static final String ACTION_MEDIA_CONTROL = "media_control";
    private static final String EXTRA_CONTROL_TYPE = "control_type";
    private static final int REQUEST_PLAY = 1;
    private static final int REQUEST_PAUSE = 2;
    private static final int CONTROL_TYPE_PLAY = 1;
    private static final int CONTROL_TYPE_PAUSE = 2;

    private CoordinatorLayout coordinatorLayout;
    private ConstraintLayout topBar;
    private ImageButton buttonOpen;
    public ImageView exoPlayPause;
    //    public ImageView audio_play_pause_btn,audio_pause_btn;
    private ProgressBar loadingProgressBar;

    private boolean restoreOrientationLock;
    private boolean restorePlayState;
    private float subtitlesScale;
    private boolean isScrubbing;
    private boolean scrubbingNoticeable;
    private long scrubbingStart;
    public boolean frameRendered;
    private boolean alive;
    ConstraintLayout centreControls;
    public static Uri currentMediaUri = null;
    public RecyclerView rvBottomBar;
    public LinearLayout setting_potrate_layout;
    public ImageButton player_speed_btn,player_shuffle_btn,player_rapeat_btn;
    //Margins
    ImageView m1, m2, m3;
    //Hidden Views
    public ImageButton exoSpeed, exoPrev, exoNext, buttonRotation;
    public ImageView exoBack;
    //    public ImageView audio_Next_iv,audio_Prev_Iv;
    final Rational rationalLimitWide = new Rational(239, 100);
    final Rational rationalLimitTall = new Rational(100, 239);

    public FrameLayout adHolder;
    public ImageView playAsAudio, shareVideo,shareVideo_potrait_btn;
    boolean play = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        hideStatusBar(PlayerActivity.this);
        initMainViews();
        playerView.setControllerHideOnTouch(false);
        playerView.setControllerAutoShow(true);

        exoBack.setOnClickListener(v -> onBackPressed());
        ((DoubleTapPlayerView) playerView).setDoubleTapEnabled(false);


        shareVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareMyMedia();
            }
        });

        shareVideo_potrait_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareMyMedia();
            }
        });



        player_shuffle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switchShuffleMode();
            }
        });

        player_rapeat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switchRepeatMode();
            }
        });

        player_speed_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                exoSpeed.performClick();
            }
        });

        playAsAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newMode = playerView.switchAudioMode();
                if (newMode) {
                    buttonRotation.setVisibility(View.GONE);
                } else {
                    buttonRotation.setVisibility(View.VISIBLE);
                }
                if (mPrefs.orientation == Utils.Orientation.LANDSCAPE || mPrefs.orientation == Utils.Orientation.AUTO_ROTATE)
                    switchOrientation(true);

                // Reset Scale
                playerView.setScale(1.f);
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                resetHideCallbacks();

            }
        });
        if (getCurrentItem().isVideo()) {
            DefaultTimeBar timeBar = playerView.findViewById(R.id.exo_progress);

            timeBar.addListener(new TimeBar.OnScrubListener() {
                @Override
                public void onScrubStart(TimeBar timeBar, long position) {
                    restorePlayState = player.isPlaying();
                    if (restorePlayState) {
                        player.pause();
                    }
                    scrubbingNoticeable = false;
                    isScrubbing = true;
                    frameRendered = true;
                    playerView.setControllerShowTimeoutMs(-1);
                    scrubbingStart = player.getCurrentPosition();
                    player.setSeekParameters(SeekParameters.CLOSEST_SYNC);
                    reportScrubbing(position);
                }

                @Override
                public void onScrubMove(TimeBar timeBar, long position) {
                    reportScrubbing(position);
                }

                @Override
                public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                    playerView.setCustomErrorMessage(null);
                    isScrubbing = false;
                    if (restorePlayState) {
                        restorePlayState = false;
                        playerView.setControllerShowTimeoutMs(PlayerActivity.CONTROLLER_TIMEOUT);
                        player.setPlayWhenReady(true);
                    }
                }
            });
        }
      /*  else {
            DefaultTimeBar timeBar_audio = playerView.findViewById(R.id.exo_progress2);

            timeBar_audio.addListener(new TimeBar.OnScrubListener() {
                @Override
                public void onScrubStart(TimeBar timeBar, long position) {
                    Log.e("TAG_player", "onScrubStart: " );
                    restorePlayState = player.isPlaying();
                    if (restorePlayState) {
                        player.pause();
                    }
                    scrubbingNoticeable = false;
                    isScrubbing = true;
                    frameRendered = true;
                    playerView.setControllerShowTimeoutMs(-1);
                    scrubbingStart = player.getCurrentPosition();
                    player.setSeekParameters(SeekParameters.CLOSEST_SYNC);
                    reportScrubbing(position);
                }

                @Override
                public void onScrubMove(TimeBar timeBar, long position) {
                    reportScrubbing(position);
                }

                @Override
                public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                    playerView.setCustomErrorMessage(null);
                    isScrubbing = false;
                    if (restorePlayState) {
                        restorePlayState = false;
                        playerView.setControllerShowTimeoutMs(PlayerActivity.CONTROLLER_TIMEOUT);
                        player.setPlayWhenReady(true);
                    }
                }
            });
        }*/






        buttonRotation.setOnClickListener(view -> {
            switchOrientation(false);
        });
        int titleViewPadding = getResources().getDimensionPixelOffset(R.dimen.exo_styled_bottom_bar_time_padding);
        final StyledPlayerControlView controlView = playerView.findViewById(R.id.exo_controller);
        controlView.setOnApplyWindowInsetsListener((view, windowInsets) -> {
            if (windowInsets != null) {
                view.setPadding(0, windowInsets.getSystemWindowInsetTop(),
                        0, windowInsets.getSystemWindowInsetBottom());

                int insetLeft = windowInsets.getSystemWindowInsetLeft();
                int insetRight = windowInsets.getSystemWindowInsetRight();

                int paddingLeft = 0;
                int marginLeft = insetLeft;

                int paddingRight = 0;
                int marginRight = insetRight;

                if (Build.VERSION.SDK_INT >= 28 && windowInsets.getDisplayCutout() != null) {
                    if (windowInsets.getDisplayCutout().getSafeInsetLeft() == insetLeft) {
                        paddingLeft = insetLeft;
                        marginLeft = 0;
                    }
                    if (windowInsets.getDisplayCutout().getSafeInsetRight() == insetRight) {
                        paddingRight = insetRight;
                        marginRight = 0;
                    }
                }
                windowInsets.consumeSystemWindowInsets();
            }
            return windowInsets;
        });
        playbackStateListener = new PlaybackStateListener();

        mBrightnessControl = new BrightnessControl(this);

        if (mPrefs.brightness >= 0) {
            mBrightnessControl.currentBrightnessLevel = mPrefs.brightness;
            mBrightnessControl.setScreenBrightness(mBrightnessControl.levelToBrightness(mBrightnessControl.currentBrightnessLevel));
        }


        playerView.setControllerVisibilityListener(new StyledPlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                controllerVisible = visibility == View.VISIBLE;
                controllerVisibleFully = playerView.isControllerFullyVisible();
                if (visibility == View.VISIBLE) {
                    Utils.showSystemUi(playerView);
                    findViewById(R.id.exo_play_pause).requestFocus();
                } else {
                    Utils.hideSystemUi(playerView);
                }


            }
        });
        exoPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousMedia();
            }
        });
//        audio_Prev_Iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                playPreviousMedia();
//            }
//        });





        exoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextMedia();
            }
        });

//        audio_Next_iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                playNextMedia();
//            }
//        });

        /*int adCounter = InterstitialAdUpdated.Companion.getMediaPlayCounter();
        if (adCounter < 2) {
            Log.e("Interstitial____", "not show " + adCounter);
            adCounter++;
            InterstitialAdUpdated.Companion.setMediaPlayCounter(adCounter);
            play = true;
        } else {
            play = false;
            Log.e("Interstitial____", "try show counter " + adCounter);
            AdsFileKt.showAdmobInterstitial(PlayerActivity.this, () -> {
                if (player != null) {
                    Log.e("Interstitial____", "close , playing now");
                    player.play();
                } else {
                    Log.e("Interstitial____", "close , player null");
                }
                return null;
            }, () -> {
                Log.e("Interstitial____", "fail , setting play");
                play = true;
                return null;
            });
        }*/


    }


    private void playPreviousMedia() {
        if (player != null) {
            MediaItem exoItem = getPreviousExoItem();
            if (exoItem == null)
                CommonKt.showToast(PlayerActivity.this, getString(R.string.already_first_item));
            else
                player.setMediaItem(exoItem);
        }
    }

    private void playNextMedia() {
        if (player != null) {
            MediaItem exoItem = getNextExoItem();
            if (exoItem == null)
                CommonKt.showToast(PlayerActivity.this, getString(R.string.already_last_item));
            else
                player.setMediaItem(exoItem);
        }
    }

    protected abstract MediaItem getPreviousExoItem();

    protected abstract MediaItem getNextExoItem();

    protected abstract void shareMyMedia();
    protected abstract void showAllSongsList();
    protected abstract void addIntoFavourite();

    public void updateGestureEnabled() {
        if (mediaIsAudio())
            Utils.setGestureEnabled(false);
        else
            Utils.setGestureEnabled(sharedPreferences.getGestureOperationStatus());

        ((DoubleTapPlayerView) playerView).updateScaleDetector();
        ((DoubleTapPlayerView) playerView).updateGestureDetector();
    }

    protected abstract boolean mediaIsAudio();


    public void initOrientation() {
        mPrefs.orientation = getMyOrientation();
        Utils.setOrientation(this, mPrefs.orientation);
    }

    private Utils.Orientation getMyOrientation() {
        Utils.Orientation orientation = Utils.Orientation.AUTO_ROTATE;
        if (sharedPreferences.getDefaultScreenOrientation().equals(getResources().getString(R.string.screen_orientation_landscape)))
            orientation = Utils.Orientation.LANDSCAPE;
        else if (sharedPreferences.getDefaultScreenOrientation().equals(getResources().getString(R.string.screen_orientation_portrait)))
            orientation = Utils.Orientation.PORTRAIT;

        return orientation;
    }


    public void changeAspectRatio() {
        playerView.setScale(1.f);
        if (playerView.getResizeMode() == AspectRatioFrameLayout.RESIZE_MODE_FIT) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            Utils.showText(playerView, getString(R.string.video_resize_crop));
        } else {
            // Default mode
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            Utils.showText(playerView, getString(R.string.video_resize_fit));
        }
        resetHideCallbacks();
    }


    private void initMainViews() {
        mPrefs = new Prefs(this);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        adHolder=findViewById(R.id.holder_ad);
        //titleView = findViewById(R.id.exo_title);
        topBar = findViewById(R.id.player_top_bar);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        playerView = findViewById(R.id.video_view);
        exoPlayPause = findViewById(R.id.exo_play_pause);
        loadingProgressBar = findViewById(R.id.loading);
       rvBottomBar = findViewById(R.id.rv_player_bottom_bar);
        setting_potrate_layout = findViewById(R.id.potrate_layout);
        player_speed_btn = findViewById(R.id.button_fast_forward);
        player_rapeat_btn = findViewById(R.id.button_repeat);
       // player_favourite_btn = findViewById(R.id.player_favourite_tv);
        player_shuffle_btn = findViewById(R.id.button_shuffle);


       // player_viewall_btn = findViewById(R.id.player_view_all_tv);
        playAsAudio = findViewById(R.id.play_as_audio);
        shareVideo = findViewById(R.id.shareVideo);
        shareVideo_potrait_btn = findViewById(R.id.shareVideo2_potrait);
        centreControls = findViewById(R.id.exo_center_controls1);
        exoSpeed = findViewById(R.id.exo_playback_speed);
        exoPrev = findViewById(R.id.my_prev);
        exoNext = findViewById(R.id.my_next);
        exoBack = findViewById(R.id.exo_back);
     //   buttonAspectRatio = findViewById(R.id.button_aspect_ratio);
        buttonRotation = playerView.findViewById(R.id.exo_rotation);
        exoSpeed.setVisibility(View.GONE);
        m1 = findViewById(R.id.m1);
        m2 = findViewById(R.id.m2);
        m3 = findViewById(R.id.m3);
    }


    public void updateUiOrientation() {
        if (mPrefs.orientation == Utils.Orientation.LANDSCAPE)
            updateLandscapeUI();
        else if (mPrefs.orientation == Utils.Orientation.PORTRAIT)
            updatePortraitUI();

    }

    public void updateLandscapeUI() {
        centreControls.setVisibility(View.GONE);
        setting_potrate_layout.setVisibility(View.GONE);
        rvBottomBar.setVisibility(View.VISIBLE);
        shareVideo.setVisibility(View.VISIBLE);

        boolean isPlaying = false;
        if (player != null)
            isPlaying = player.isPlaying();

        updateLandscapeOrientation(true, isPlaying);
        m1.setVisibility(View.VISIBLE);
        m2.setVisibility(View.VISIBLE);
        m3.setVisibility(View.VISIBLE);
    }

    public void updatePortraitUI() {
        if (getCurrentItem().isVideo()) {

            setting_potrate_layout.setVisibility(View.VISIBLE);
            centreControls.setVisibility(View.VISIBLE);

        }else {
            centreControls.setVisibility(View.VISIBLE);
            setting_potrate_layout.setVisibility(View.GONE);
//            audio_pause_btn.setVisibility(View.VISIBLE);
//            audio_play_pause_btn.setVisibility(View.GONE);


        }
        rvBottomBar.setVisibility(View.GONE);
        shareVideo.setVisibility(View.GONE);
        boolean isPlaying = false;
        if (player != null)
            isPlaying = player.isPlaying();

        updateLandscapeOrientation(false, isPlaying);
//        if (isPlaying)
//        {
//            Log.e("TAG", "updatePortraitUI: " );
//            audio_pause_btn.setVisibility(View.VISIBLE);
//            audio_play_pause_btn.setVisibility(View.GONE);
//        }else {
//            audio_pause_btn.setVisibility(View.GONE);
//            audio_play_pause_btn.setVisibility(View.VISIBLE);
//        }
        m1.setVisibility(View.GONE);
        m2.setVisibility(View.GONE);
        m3.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("changevolume12", "11 " + keyCode + "  22 " + event.toString());
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                playerView.removeCallbacks(playerView.textClearRunnable);
                Utils.adjustVolume(mAudioManager, playerView, keyCode == KeyEvent.KEYCODE_VOLUME_UP, event.getRepeatCount() == 0);
                return true;
            case KeyEvent.KEYCODE_BUTTON_SELECT:
            case KeyEvent.KEYCODE_BUTTON_START:
            case KeyEvent.KEYCODE_BUTTON_A:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
            case KeyEvent.KEYCODE_SPACE:
                if (!controllerVisibleFully) {
                    if (player.isPlaying()) {
                        player.pause();
                    } else {
                        player.play();
                    }
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_BUTTON_L2:
                if (!controllerVisibleFully) {
                    playerView.removeCallbacks(playerView.textClearRunnable);
                    long seekTo = player.getCurrentPosition() - 10_000;
                    if (seekTo < 0)
                        seekTo = 0;
                    player.setSeekParameters(SeekParameters.PREVIOUS_SYNC);
                    player.seekTo(seekTo);
                    playerView.setCustomErrorMessage(Utils.formatMilis(seekTo));
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_BUTTON_R2:
                if (!controllerVisibleFully) {
                    playerView.removeCallbacks(playerView.textClearRunnable);
                    long seekTo = player.getCurrentPosition() + 10_000;
                    long seekMax = player.getDuration();
                    if (seekMax != C.TIME_UNSET && seekTo > seekMax)
                        seekTo = seekMax;
                    PlayerActivity.player.setSeekParameters(SeekParameters.NEXT_SYNC);
                    player.seekTo(seekTo);
                    playerView.setCustomErrorMessage(Utils.formatMilis(seekTo));
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                break;
            default:
                if (!controllerVisibleFully) {
                    playerView.showController();
                    return true;
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                playerView.postDelayed(playerView.textClearRunnable, CustomStyledPlayerView.MESSAGE_TIMEOUT_KEY);
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_BUTTON_L2:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_BUTTON_R2:
                playerView.postDelayed(playerView.textClearRunnable, CustomStyledPlayerView.MESSAGE_TIMEOUT_KEY);
                break;
        }
        return super.onKeyUp(keyCode, event);
    }


    public void switchShuffleMode() {
        if (getShuffleMode())
            disableShuffleMode();
        else
            enableShuffleMode();
    }

    private boolean getShuffleMode() {
        return player.getShuffleModeEnabled();
    }

    private void enableShuffleMode() {
        player.setShuffleModeEnabled(true);
        showMessage(R.string.exo_controls_shuffle_on_description);
    }

    private void disableShuffleMode() {
        player.setShuffleModeEnabled(false);
        showMessage(R.string.exo_controls_shuffle_off_description);
    }

    @SuppressLint("WrongConstant")
    public void switchRepeatMode() {
        int currentMode = player.getRepeatMode();
        if (currentMode >= 2)
            currentMode = 0;
        else
            currentMode++;
        player.setRepeatMode(currentMode);
        switch (player.getRepeatMode()) {
            case Player.REPEAT_MODE_ONE:
                showMessage(R.string.exo_controls_repeat_one_description);
                break;
            case Player.REPEAT_MODE_ALL:
                showMessage(R.string.exo_media_action_repeat_all_description);
                break;
            case Player.REPEAT_MODE_OFF:
                showMessage(R.string.exo_controls_repeat_off_description);
                break;
        }
    }

    private void showMessage(int messageResId) {
        showSnack(getResources().getString(messageResId), null);
    }

    public void initializePlayer() {
        haveMedia = mPrefs.mediaUri != null && (Utils.fileExists(this, mPrefs.mediaUri) || Utils.isSupportedUri(mPrefs.mediaUri));

        if (player == null) {
            trackSelector = new DefaultTrackSelector(this);
            if (Build.VERSION.SDK_INT >= 24) {
                final LocaleList localeList = Resources.getSystem().getConfiguration().getLocales();
                final List<String> locales = new ArrayList<>();
                for (int i = 0; i < localeList.size(); i++) {
                    locales.add(localeList.get(i).getISO3Language());
                }
                trackSelector.setParameters(trackSelector.buildUponParameters()
                        .setPreferredAudioLanguages(locales.toArray(new String[0]))
                );
            } else {
                final Locale locale = Resources.getSystem().getConfiguration().locale;
                trackSelector.setParameters(trackSelector.buildUponParameters()
                        .setPreferredAudioLanguage(locale.getISO3Language())
                );
            }

/*
              RenderersFactory renderersFactory = new DefaultRenderersFactory(this)
                    .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON);
            final DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory()
                    .setTsExtractorTimestampSearchBytes(1500 * TsExtractor.TS_PACKET_SIZE);
         player = new SimpleExoPlayer.Builder(this, renderersFactory)
                    .setTrackSelector(trackSelector)
                    .setMediaSourceFactory(new DefaultMediaSourceFactory(this, extractorsFactory))
                    .build();


 */
            final DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory()
                    .setTsExtractorTimestampSearchBytes(1500 * TsExtractor.TS_PACKET_SIZE);
            player = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector)
                    .setMediaSourceFactory(new DefaultMediaSourceFactory(this, extractorsFactory)).
                            build();

            final AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.CONTENT_TYPE_MOVIE)
                    .build();
            player.setAudioAttributes(audioAttributes, true);

            final YouTubeOverlay youTubeOverlay = findViewById(R.id.youtube_overlay);

            youTubeOverlay.performListener(new YouTubeOverlay.PerformListener() {
                @Override
                public void onAnimationStart() {

                    youTubeOverlay.setAlpha(1.0f);
                    youTubeOverlay.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd() {

                    youTubeOverlay.animate()
                            .alpha(0.0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    youTubeOverlay.setVisibility(View.GONE);
                                    youTubeOverlay.setAlpha(1.0f);
                                }
                            });
                }
            });

            youTubeOverlay.seekSeconds(sharedPreferences.getDoubleTapDuration());
            youTubeOverlay.player(player);
        }

        playerView.setPlayer(player);

        initializeMediaSession();
        playerView.setControllerShowTimeoutMs(-1);

        if (haveMedia) {
            Log.d("myflow34", "have Media");
            playerView.setResizeMode(mPrefs.resizeMode);

            if (mPrefs.resizeMode == AspectRatioFrameLayout.RESIZE_MODE_ZOOM) {
                playerView.setScale(mPrefs.scale);
            } else {
                playerView.setScale(1.f);
            }

            /*
            List<MediaItem> mediaItemList = new ArrayList();

            for (Uri uri : getUriList()) {
                mediaItemList.add(new MediaItem.Builder()
                        .setUri(uri)
                        .setMimeType(mPrefs.mediaType).build());
            }

            if (mPrefs.subtitleUri != null && Utils.fileExists(this, mPrefs.subtitleUri)) {
                final String subtitleMime = SubtitleUtils.getSubtitleMime(mPrefs.subtitleUri);
                final String subtitleLanguage = SubtitleUtils.getSubtitleLanguage(mPrefs.subtitleUri);
                final String subtitleName = Utils.getFileName(this, mPrefs.subtitleUri);

                MediaItem.Subtitle subtitle = new MediaItem.Subtitle(mPrefs.subtitleUri, subtitleMime,
                        subtitleLanguage, C.SELECTION_FLAG_DEFAULT, C.ROLE_FLAG_SUBTITLE, subtitleName);
                mediaItemBuilder.setSubtitles(Collections.singletonList(subtitle));



            }
 */

            player.setMediaItem(CommonKt.getExoItem(getCurrentItem()), sharedPreferences.getLastPosition(getCurrentItem().getRealPath()));

            if (loudnessEnhancer != null) {
                loudnessEnhancer.release();
            }
            try {
                int audioSessionId = C.generateAudioSessionIdV21(this);
                loudnessEnhancer = new LoudnessEnhancer(audioSessionId);
                player.setAudioSessionId(audioSessionId);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            player.addAudioListener(new AudioListener() {
                @Override
                public void onAudioSessionIdChanged(int audioSessionId) {
                    if (loudnessEnhancer != null) {
                        loudnessEnhancer.release();
                    }
                    try {
                        loudnessEnhancer = new LoudnessEnhancer(audioSessionId);
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }
            });

            setTracks = true;

            updateLoading(true);

            //   play = mPrefs.getPosition() == 0L;

            player.setPlayWhenReady(play);

            MediaModel item = getCurrentItem();
            currentMediaUri = CommonKt.getUriPath(item.getUri());

            //   player.seekTo(getCurrentSelectedPosition(), C.TIME_UNSET);

            topBar.setVisibility(View.VISIBLE);

            ((DoubleTapPlayerView) playerView).setDoubleTapEnabled(true);

            player.setHandleAudioBecomingNoisy(true);
            mediaSession.setActive(true);
        } else {
            Log.d("myflow34", "no Media");
            playerView.showController();
        }

        player.addListener(playbackStateListener);
        player.prepare();


        if (restorePlayState) {
            restorePlayState = false;
            playerView.showController();
            player.play();
        }


    }

    private void initializeMediaSession() {
        mediaSession = new MediaSessionCompat(this, getString(R.string.app_name));
        MediaSessionConnector mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setPlayer(player);

        mediaSessionConnector.setMediaMetadataProvider(player -> {
            final String title = Utils.getFileName(PlayerActivity.this, mPrefs.mediaUri);
            if (title == null)
                return null;
            else
                return new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                        .build();
        });

    }


    protected abstract MediaModel getCurrentItem();


    public abstract List<MediaModel> getMainList();

    public void releasePlayer() {
        if (player != null) {
            sharedPreferences.setLastPosition(getCurrentItem().getRealPath(), player.getCurrentPosition());
            mediaSession.setActive(false);
            mediaSession.release();

            mPrefs.updatePosition(player.getCurrentPosition());
            mPrefs.updateBrightness(mBrightnessControl.currentBrightnessLevel);
            mPrefs.updateOrientation();
            mPrefs.updateMeta(getSelectedTrackAudio(false), getSelectedTrackAudio(true), getSelectedTrackSubtitle(), playerView.getResizeMode(), playerView.getVideoSurfaceView().getScaleX());

            if (player.isPlaying()) {
                restorePlayState = true;
            }
            player.removeListener(playbackStateListener);
            player.clearMediaItems();
            player.release();
            player = null;
        }
        topBar.setVisibility(View.GONE);

    }

    public static void releasePlayerFromService(Context context) {
        if (player != null) {
            Prefs mPrefs2 = new Prefs(context);
            mPrefs2.updatePosition(player.getCurrentPosition());
            mPrefs2.updateBrightness(mBrightnessControl.currentBrightnessLevel);
            mPrefs2.updateOrientation();
            player.clearMediaItems();
            player.release();
            player = null;
        }
    }


    private class PlaybackStateListener implements Player.EventListener {
        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            playerView.setKeepScreenOn(isPlaying);
            if (!isScrubbing) {
                if (isPlaying) {
                    playerView.setControllerShowTimeoutMs(CONTROLLER_TIMEOUT);
                } else {
                    playerView.setControllerShowTimeoutMs(-1);
                }
            }


            updateLandscapeOrientation(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE, isPlaying);
            updateNotification();

        }

        @Override
        public void onPlaybackStateChanged(int state) {
            updatePlayerItem();


            if (state == Player.STATE_READY) {
                frameRendered = true;
                final Format format = player.getVideoFormat();

            }

            if (setTracks && state == Player.STATE_READY) {
                setTracks = false;
                updateLoading(false);
                if (mPrefs.audioTrack != -1 && mPrefs.audioTrackFfmpeg != -1) {
                    setSelectedTrackAudio(mPrefs.audioTrack, false);
                    setSelectedTrackAudio(mPrefs.audioTrackFfmpeg, true);
                }
                if (mPrefs.subtitleTrack != -1 && (mPrefs.subtitleTrack < getTrackCountSubtitle() || mPrefs.subtitleTrack == Integer.MIN_VALUE))
                    setSelectedTrackSubtitle(mPrefs.subtitleTrack);
            }


        }

        @Override
        public void onPlayerError(@NonNullApi ExoPlaybackException error) {
            Log.d("myerrror44", String.valueOf(error.type));
            updateLoading(false);
            if (controllerVisible && controllerVisibleFully) {
                showError(error);
            } else {
                errorToShow = error;
            }
        }

        @Override
        public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
            if (reason == Player.DISCONTINUITY_REASON_AUTO_TRANSITION && player.getRepeatMode() == Player.REPEAT_MODE_ALL)
                playNextMedia();
            else {
                updatePlayerItem();
                updateNotification();
            }
        }
    }

    private void updatePlayerItem() {

        MediaModel item = getCurrentItem();
        currentMediaUri = CommonKt.getUriPath(item.getUri());
//        titleView.setText(Utils.getFileName(PlayerActivity.this, currentMediaUri));
    }

    protected abstract void updateNotification();

    private void enableRotation() {
        try {
            if (Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) == 0) {
                Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
                restoreOrientationLock = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadSubtitleFile(Uri pickerInitialUri) {
        Toast.makeText(PlayerActivity.this, R.string.open_subtitles, Toast.LENGTH_SHORT).show();
        enableRotation();

        final Intent intent = createBaseFileIntent(Intent.ACTION_OPEN_DOCUMENT, pickerInitialUri);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        final String[] supportedMimeTypes = {
                MimeTypes.APPLICATION_SUBRIP,
                MimeTypes.TEXT_SSA,
                MimeTypes.TEXT_VTT,
                MimeTypes.APPLICATION_TTML,
                "text/*",
                "application/octet-stream"
        };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, supportedMimeTypes);

        safelyStartActivityForResult(intent, REQUEST_CHOOSER_SUBTITLE);
    }

    private void requestDirectoryAccess() {
        enableRotation();

        Uri initialUri = null;

        if (Build.VERSION.SDK_INT >= 26) {
            final String authority = "com.android.externalstorage.documents";
            final String documentId = "primary:" + Environment.DIRECTORY_MOVIES;
            initialUri = DocumentsContract.buildDocumentUri(authority, documentId);
        }

        final Intent intent = createBaseFileIntent(Intent.ACTION_OPEN_DOCUMENT_TREE, initialUri);

        safelyStartActivityForResult(intent, REQUEST_CHOOSER_SCOPE_DIR);
    }

    private Intent createBaseFileIntent(final String action, final Uri initialUri) {
        final Intent intent = new Intent(action);
        intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
        if (Build.VERSION.SDK_INT >= 26 && initialUri != null) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, initialUri);
        }
        return intent;
    }

    void safelyStartActivityForResult(final Intent intent, final int code) {
        if (intent.resolveActivity(getPackageManager()) == null)
            showSnack(getText(R.string.error_files_missing).toString(), intent.toString());
        else
            startActivityForResult(intent, code);
    }

    public void setSelectedTrackSubtitle(final int trackIndex) {
        final MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo != null) {
            final DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
            final DefaultTrackSelector.ParametersBuilder parametersBuilder = parameters.buildUpon();
            for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
                if (mappedTrackInfo.getRendererType(rendererIndex) == C.TRACK_TYPE_TEXT) {
                    if (trackIndex == Integer.MIN_VALUE) {
                        parametersBuilder.setRendererDisabled(rendererIndex, true);
                    } else {
                        parametersBuilder.setRendererDisabled(rendererIndex, false);
                        if (trackIndex == -1) {
                            parametersBuilder.clearSelectionOverrides(rendererIndex);
                        } else {
                            final int[] tracks = {0};
                            final DefaultTrackSelector.SelectionOverride selectionOverride = new DefaultTrackSelector.SelectionOverride(trackIndex, tracks);
                            parametersBuilder.setSelectionOverride(rendererIndex, mappedTrackInfo.getTrackGroups(rendererIndex), selectionOverride);
                        }
                    }
                }
            }
            trackSelector.setParameters(parametersBuilder);
        }
    }

    public int getSelectedTrackSubtitle() {
        if (trackSelector != null) {
            final MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
            if (mappedTrackInfo != null) {
                for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
                    if (mappedTrackInfo.getRendererType(rendererIndex) == C.TRACK_TYPE_TEXT) {
                        final TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(rendererIndex);
                        final DefaultTrackSelector.SelectionOverride selectionOverride = trackSelector.getParameters().getSelectionOverride(rendererIndex, trackGroups);
                        DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
                        if (parameters.getRendererDisabled(rendererIndex)) {
                            return Integer.MIN_VALUE;
                        }
                        if (selectionOverride == null) {
                            return -1;
                        }
                        return selectionOverride.groupIndex;
                    }
                }
            }
        }
        return -1;
    }

    public int getSelectedTrackAudio(final boolean ffmpeg) {
        if (trackSelector != null) {
            final MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
            if (mappedTrackInfo != null) {
                for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
                    if (mappedTrackInfo.getRendererType(rendererIndex) == C.TRACK_TYPE_AUDIO) {
                        final String rendererName = mappedTrackInfo.getRendererName(rendererIndex);
                        if ((rendererName.toLowerCase().contains("ffmpeg") && !ffmpeg) ||
                                (!rendererName.toLowerCase().contains("ffmpeg") && ffmpeg))
                            continue;
                        final TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(rendererIndex);
                        final DefaultTrackSelector.SelectionOverride selectionOverride = trackSelector.getParameters().getSelectionOverride(rendererIndex, trackGroups);
                        DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
                        if (parameters.getRendererDisabled(rendererIndex)) {
                            return Integer.MIN_VALUE;
                        }
                        if (selectionOverride == null) {
                            return -1;
                        }
                        return selectionOverride.groupIndex;
                    }
                }
            }
        }
        return -1;
    }

    public void setSelectedTrackAudio(final int trackIndex, final boolean ffmpeg) {
        final MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo != null) {
            final DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
            final DefaultTrackSelector.ParametersBuilder parametersBuilder = parameters.buildUpon();
            for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
                if (mappedTrackInfo.getRendererType(rendererIndex) == C.TRACK_TYPE_AUDIO) {
                    final String rendererName = mappedTrackInfo.getRendererName(rendererIndex);
                    if ((rendererName.toLowerCase().contains("ffmpeg") && !ffmpeg) ||
                            (!rendererName.toLowerCase().contains("ffmpeg") && ffmpeg))
                        continue;
                    if (trackIndex == Integer.MIN_VALUE) {
                        parametersBuilder.setRendererDisabled(rendererIndex, true);
                    } else {
                        parametersBuilder.setRendererDisabled(rendererIndex, false);
                        if (trackIndex == -1) {
                            parametersBuilder.clearSelectionOverrides(rendererIndex);
                        } else {
                            final int[] tracks = {0};
                            final DefaultTrackSelector.SelectionOverride selectionOverride = new DefaultTrackSelector.SelectionOverride(trackIndex, tracks);
                            parametersBuilder.setSelectionOverride(rendererIndex, mappedTrackInfo.getTrackGroups(rendererIndex), selectionOverride);
                        }
                    }
                }
            }
            trackSelector.setParameters(parametersBuilder);
        }
    }

    public int getTrackCountSubtitle() {
        if (trackSelector != null) {
            final MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
            if (mappedTrackInfo != null) {
                for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
                    if (mappedTrackInfo.getRendererType(rendererIndex) == C.TRACK_TYPE_TEXT) {
                        final TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(rendererIndex);
                        return trackGroups.length;
                    }
                }
            }
        }
        return 0;
    }

    void setSubtitleTextSize() {
        setSubtitleTextSize(getResources().getConfiguration().orientation);
    }

    void setSubtitleTextSize(final int orientation) {
        // Tweak text size as fraction size doesn't work well in portrait
        final SubtitleView subtitleView = playerView.getSubtitleView();
        if (subtitleView != null) {
            final float size;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                size = SubtitleView.DEFAULT_TEXT_SIZE_FRACTION * subtitlesScale;
            } else {
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                float ratio = ((float) metrics.heightPixels / (float) metrics.widthPixels);
                if (ratio < 1)
                    ratio = 1 / ratio;
                size = SubtitleView.DEFAULT_TEXT_SIZE_FRACTION * subtitlesScale / ratio;
            }

            subtitleView.setFractionalTextSize(size);
        }
    }

    void setSubtitleTextSizePiP() {
        final SubtitleView subtitleView = playerView.getSubtitleView();
        if (subtitleView != null)
            subtitleView.setFractionalTextSize(SubtitleView.DEFAULT_TEXT_SIZE_FRACTION * 2);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //    setSubtitleTextSize(newConfig.orientation);
        Log.d("newOrientation", "config " + String.valueOf(newConfig.orientation));
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            updateLandscapeUI();
        else
            updatePortraitUI();
    }

    public abstract void updateLandscapeOrientation(boolean landscape, boolean isPlaying);

    void showError(ExoPlaybackException error) {
        final String errorGeneral = error.getLocalizedMessage();
        String errorDetailed;

        switch (error.type) {
            case ExoPlaybackException.TYPE_SOURCE:
                errorDetailed = error.getSourceException().getLocalizedMessage();
                break;
            case ExoPlaybackException.TYPE_RENDERER:
                errorDetailed = error.getRendererException().getLocalizedMessage();
                break;
            case ExoPlaybackException.TYPE_UNEXPECTED:
                errorDetailed = error.getUnexpectedException().getLocalizedMessage();
                break;
            case ExoPlaybackException.TYPE_REMOTE:
            default:
                errorDetailed = errorGeneral;
                break;
        }

        showSnack(errorGeneral, errorDetailed);
    }

    void showSnack(final String textPrimary, final String textSecondary) {
        snackbar = Snackbar.make(coordinatorLayout, textPrimary, Snackbar.LENGTH_LONG);
        if (textSecondary != null) {
            snackbar.setAction(R.string.error_details, v -> {
                final AlertDialog.Builder builder = new AlertDialog.Builder(PlayerActivity.this);
                builder.setMessage(textSecondary);
                builder.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss());
                final AlertDialog dialog = builder.create();
                dialog.show();
            });
        }
     //   snackbar.setAnchorView(R.id.rv_player_bottom_bar);
        snackbar.show();
    }

    void reportScrubbing(long position) {
        final long diff = position - scrubbingStart;
        if (Math.abs(diff) > 1000) {
            scrubbingNoticeable = true;
        }
        if (scrubbingNoticeable) {
            playerView.clearIcon();
            playerView.setCustomErrorMessage(Utils.formatMilisSign(diff));
        }
        if (frameRendered) {
            frameRendered = false;
            player.seekTo(position);
        }
    }

    void updateSubtitleStyle() {
        final CaptioningManager captioningManager = (CaptioningManager) getSystemService(Context.CAPTIONING_SERVICE);
        final SubtitleView subtitleView = playerView.getSubtitleView();
        if (!captioningManager.isEnabled()) {
            subtitlesScale = 1.05f;
            final CaptionStyleCompat captionStyle = new CaptionStyleCompat(Color.WHITE, Color.TRANSPARENT, Color.TRANSPARENT, CaptionStyleCompat.EDGE_TYPE_OUTLINE, Color.BLACK, Typeface.DEFAULT_BOLD);
            if (subtitleView != null) {
                subtitleView.setStyle(captionStyle);
                subtitleView.setApplyEmbeddedStyles(true);
            }
        } else {
            subtitlesScale = captioningManager.getFontScale();
            if (subtitleView != null) {
                subtitleView.setUserDefaultStyle();
                // Do not apply embedded style as currently the only supported color style is PrimaryColour
                // https://github.com/google/ExoPlayer/issues/8435#issuecomment-762449001
                // This may result in poorly visible text (depending on user's selected edgeColor)
                // The same can happen with style provided using setStyle but enabling CaptioningManager should be a way to change the behavior
                subtitleView.setApplyEmbeddedStyles(false);
            }
        }

        if (subtitleView != null)
            subtitleView.setBottomPaddingFraction(SubtitleView.DEFAULT_BOTTOM_PADDING_FRACTION * 2f / 3f);

        setSubtitleTextSize();
    }

       public void switchOrientation(boolean isAudio) {
        if (isAudio) {
            mPrefs.orientation = Utils.Orientation.PORTRAIT;
        } else {
            mPrefs.orientation = Utils.getNextOrientation(mPrefs.orientation);
        }
        Utils.setOrientation(PlayerActivity.this, mPrefs.orientation);
        if (!isAudio) {
            Utils.showText(playerView, getString(mPrefs.orientation.description), 2500);
        }
        resetHideCallbacks();
        // updateUiOrientation();
    }

    void resetHideCallbacks() {
        if (haveMedia && player.isPlaying()) {
            playerView.setControllerShowTimeoutMs(PlayerActivity.CONTROLLER_TIMEOUT);
        }
    }

    private void updateLoading(final boolean enableLoading) {
        if (enableLoading) {
            exoPlayPause.setVisibility(View.GONE);
//            audio_play_pause_btn.setVisibility(View.GONE);
            loadingProgressBar.setVisibility(View.VISIBLE);
        } else {
            loadingProgressBar.setVisibility(View.GONE);
            exoPlayPause.setVisibility(View.VISIBLE);
//            audio_play_pause_btn.setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onUserLeaveHint() {
        if (sharedPreferences.getAutoPopupPlayStatus() && player != null && player.isPlaying() && CommonKt.isMiniPlayerSupported(this))
            enterMiniplayer();
        else
            super.onUserLeaveHint();
    }


    public void enterMiniplayer() {
        MediaModel media = getCurrentItem();
        if (media.isUniqueMedia() || player == null)
            CommonKt.showToast(this, getString(R.string.no_media_found));
        else {
            CommonKt.setCurrentMediaItem(getCurrentItem());
            startMyFloatingService();
        }
    }

    protected abstract void startMyFloatingService();


    public void continuePlayer(MediaModel currentItem) {
        if (currentItem.isVideo())
        if (player != null) {
            playerView.setPlayer(player);
            if (mediaSession == null)
                initializeMediaSession();
            playerView.setControllerShowTimeoutMs(-1);
            ((DoubleTapPlayerView) playerView).setDoubleTapEnabled(true);
            playerView.showController();
            player.addListener(playbackStateListener);
            if (!alreadyPlaying(currentItem))
                player.setMediaItem(CommonKt.getExoItem(currentItem));
            updatePlayerItem();
        } else
            initializePlayer();
    }

    private boolean alreadyPlaying(MediaModel singleMedia) {
        boolean flag = false;
        if (currentMediaUri != null) {
            String itemUri = String.valueOf(CommonKt.getUriPath(singleMedia.getUri()));
            String currentUri = String.valueOf(currentMediaUri);
            flag = currentUri.equals(itemUri);
        }
        return flag;
    }

    public static void playPrevious() {
        if (player != null)
            player.seekTo(player.getCurrentPosition() - FAST_REWIND_TIME);
    }

    public static void playNext() {
        if (player != null)
            player.seekTo(player.getCurrentPosition() + FAST_FORWARD_TIME);
    }


    public static boolean playPause() {
        if (player != null) {
            if (player.isPlaying())
                player.pause();
            else
                player.play();

            return player.isPlaying();
        } else
            return false;

    }

    public static String getCurrentMediaTitle(Context context) {
        if (player != null)
            return Utils.getFileName(context, currentMediaUri);
        else
            return context.getResources().getString(R.string.no_song);
    }

    public static boolean isMediaPlaying() {
        if (player != null)
            return player.isPlaying();
        else
            return false;
    }

    public boolean playerIsNull() {
        return player == null;
    }

}