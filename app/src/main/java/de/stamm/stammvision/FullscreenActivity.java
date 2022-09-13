package de.stamm.stammvision;

import java.io.File;
import java.util.LinkedList;

import de.stamm.clouddisplay.R;
import de.stamm.stammvision.data.MainModel;
import de.stamm.stammvision.data.MyPhoneStateListener;
import de.stamm.stammvision.data.Playlist;
import de.stamm.stammvision.data.Template;
import de.stamm.stammvision.rootaccess.RootAccess;
import de.stamm.stammvision.util.SystemUiHider;
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
@SuppressLint({ "SetJavaScriptEnabled", "ClickableViewAccessibility" })
public class FullscreenActivity extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHiderText;
	private SystemUiHider mSystemUiHiderImage;
	private SystemUiHider mSystemUiHiderVideo;
	private SystemUiHider mSystemUiHiderWeb;

    private static Context context;
    
	protected MainModel mainModel;
	
	TextView contentTextView;
	ImageView contentImageView;
	VideoView contentVideoView;
	WebView contentWebView;

	TelephonyManager tel;
	MyPhoneStateListener myListener;
	MediaController mediaController;
	
	Playlist playlist;
	int runPos = 0;

	int cycles_count = 0;
	int MAX_CYCLES = 25;
	int MAX_TIME_TO_RESTART_IN_MS = 60 * 60 * 1000; // 1 hour
	int DISPLAY_TIME_INFO = 10000;
	
    Bitmap myBitmap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fullscreen);

		permissions();
		
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        context = getApplicationContext();
        mainModel = ((MainModel) getApplicationContext());        

        /* Update the listener, and start it */
        myListener = new MyPhoneStateListener(mainModel);
        tel = ( TelephonyManager )getSystemService(Context.TELEPHONY_SERVICE);
        tel.listen(myListener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        
        startService(new Intent(this, BackgroundTask.class));

		mediaController = new MediaController(FullscreenActivity.this, false);
		contentTextView = (TextView) findViewById(R.id.textview);
		contentImageView = (ImageView) findViewById(R.id.picview);
		contentVideoView = (VideoView) findViewById(R.id.video_view);
		contentWebView = (WebView) findViewById(R.id.web_view);
		
		registerSystemUiHiderText(contentTextView);
		registerSystemUiHiderImage(contentImageView);
		registerSystemUiHiderVideo(contentVideoView);
		registerSystemUiHiderWeb(contentWebView);
		
		mSystemUiHiderText.hide();
		mSystemUiHiderImage.hide();
		mSystemUiHiderVideo.hide();
		mSystemUiHiderWeb.hide();
		
		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		controlsView.setVisibility(View.GONE);

		//String text = "Starting... please wait...";
		//contentTextView.setText(text);
		
		contentTextView.setVisibility(View.VISIBLE); 
		contentVideoView.setVisibility(View.INVISIBLE); 
		contentImageView.setVisibility(View.INVISIBLE); 
		contentWebView.setVisibility(View.INVISIBLE); 
						
		addOnClickListenerText(contentTextView);
		addOnClickListenerImage(contentImageView);
		addOnClickListenerVideo(contentVideoView);
		addOnClickListenerWeb(contentWebView);

		
		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.settings_button).setOnTouchListener(mDelayHideTouchListener);
		
		mSystemUiHiderText.hide();
		mSystemUiHiderImage.hide();
		mSystemUiHiderVideo.hide();
		mSystemUiHiderWeb.hide();

		mainModel.loadPlaylists();
		
		int timer = 500;
		if (mainModel.isStartDone()) timer = 500;
		  
		new MyCountDownTimer(timer, 1000) {

			int runPos = 0;
			LinkedList<Template> templates = new LinkedList<Template>();
			
            @Override
            public void onTick(long millisUntilFinished) {
            	if (mainModel.isDebug() || mainModel.isKeepAlive) {
	                ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	                RunningTaskInfo foregroundTaskInfo = activityManager.getRunningTasks(1).get(0);
	                String foregroundTaskPackageName = foregroundTaskInfo .topActivity.getPackageName();
	            	if (!foregroundTaskPackageName.equals(getApplicationContext().getPackageName())) {
	                	Log.i("Tick", "Bring to foreground");
	            		Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getApplicationContext().getPackageName());
	                    startActivity(LaunchIntent);
	            	}
            	}            	
            }

            @Override
            public void onFinish() {

				// PREPARE
            	if (contentVideoView != null) contentVideoView.stopPlayback();
				if (myBitmap != null && !myBitmap.isRecycled()) {
					myBitmap.recycle();
					myBitmap = null;
				}
            	
            	if (runPos >= templates.size()) {     
            		if (cycles_count >= MAX_CYCLES) {
                    	Log.i("RESTART", "Do restart");
            			FullscreenActivity.doRestart(getApplicationContext());
            		}
            		cycles_count++;
            		
            		playlist = mainModel.getCurrentPlaylist();
    				if (playlist != null) {
        				templates = playlist.getTemplates();
    				} else {
                		templates.clear();
    				}
    				runPos = 0;
            		
            		// SET max cycles
            		int playlist_duration = getPlaylistLength(templates);            		
            		if (playlist_duration == 0) MAX_CYCLES = Integer.valueOf(MAX_TIME_TO_RESTART_IN_MS / DISPLAY_TIME_INFO);
            		else {
            			MAX_CYCLES = Integer.valueOf(MAX_TIME_TO_RESTART_IN_MS / playlist_duration);
            		}
            		
            		
            		if (mainModel.getSettings().getDevice_id().equals("")) {
						mainModel.log("WARN", "DeviceId not found.");
						mainModel.getSettings().loadXML();
						if (mainModel.getSettings().getDevice_id().equals("")) {
							mainModel.log("WARN", "Loading DeviceId not found, generating new one.");
							mainModel.generateDevice_id();
						} else {
							mainModel.setId_already_known(true);
						}
					}
					else {
						mainModel.setId_already_known(true);
					}
            	}
            	            	
				if (!mainModel.isDeviceIdRegistered() && mainModel.isOnline() && templates.size() == 0) {
					
					// device not known
					contentTextView.setBackgroundColor(Color.parseColor("#ffffff"));
                	String text = "Please connect to the internet.";
                	if (!mainModel.getSettings().getDevice_id().equals("")) text = "Device: " + mainModel.getSettings().getDevice_id();
					contentTextView.setText(text);
					contentTextView.setVisibility(View.VISIBLE);
					contentVideoView.setVisibility(View.INVISIBLE);
					contentImageView.setVisibility(View.INVISIBLE);
					contentWebView.setVisibility(View.INVISIBLE);
					
					recreateCounter(DISPLAY_TIME_INFO, 1000);
	                start();
				}
				else if (templates.size() == 0) {
					// No Playlist					
					contentTextView.setBackgroundColor(Color.parseColor("#ffffff"));
                	String text = "Please connect to the internet.";
                	if (!mainModel.getSettings().getDevice_id().equals("")) text = "Waiting for playlist...";
					contentTextView.setText(text);
					contentTextView.setVisibility(View.VISIBLE);
					contentVideoView.setVisibility(View.INVISIBLE);
					contentImageView.setVisibility(View.INVISIBLE);
					contentWebView.setVisibility(View.INVISIBLE);

					recreateCounter(DISPLAY_TIME_INFO, 1000);
	                start();
				}

				else if (mainModel.isDeviceIdRegistered() || !mainModel.isOnline() || templates.size() > 0) {
					
					// SHOW CONTENT
					if (templates.get(runPos).getTemplate_type().equals("text")) {
						contentTextView.setText(templates.get(runPos).getTemplate_text());
						contentTextView.setBackgroundColor(Color.parseColor(templates.get(runPos).getTemplate_background()));

						contentTextView.setVisibility(View.VISIBLE);
						contentVideoView.setVisibility(View.INVISIBLE);
						contentImageView.setVisibility(View.INVISIBLE);
						contentWebView.setVisibility(View.INVISIBLE);
						Toast.makeText(getContext(), "postiion" + runPos, Toast.LENGTH_SHORT).show();
					}
					else if (templates.get(runPos).getTemplate_type().equals("html")) {
						contentWebView.getSettings().setJavaScriptEnabled(true);

						contentWebView.setWebChromeClient(new WebChromeClient());
						contentWebView.getSettings().setDomStorageEnabled(true);

						String html_text = "<!DOCTYPE html><html><head>";
						for (int i = 0; i < MainModel.HTML_FILES.length; i++) {
							File html_file = new File(MainModel.PATH_APP+MainModel.HTML_FILES[i]);
							if (html_file.exists()) {
								String extension = "";
								int j = MainModel.HTML_FILES[i].lastIndexOf('.');
								if (j > 0) {
								    extension = MainModel.HTML_FILES[i].substring(j+1);
								}
								if (extension.equals("js")) html_text += "<script type=\"text/javascript\" src=\""+MainModel.HTML_FILES[i]+"\"></script>";
								else if (extension.equals("css")) html_text += "<link rel=\"stylesheet\" href=\""+MainModel.HTML_FILES[i]+"\" type=\"text/css\"  />";
								else Log.w("HTML", "new extension "+ extension + " - " + MainModel.HTML_FILES[i]);
							}
						}
						html_text+="</head>";
						String html_text_2 = "<body>"+templates.get(runPos).getTemplate_text()+"</body></html>";

						
						contentWebView.loadDataWithBaseURL("file:///"+MainModel.PATH_APP, html_text+html_text_2, "text/html", "utf-8", null);

						contentTextView.setVisibility(View.INVISIBLE);
						contentVideoView.setVisibility(View.INVISIBLE);
						contentImageView.setVisibility(View.INVISIBLE);
						contentWebView.setVisibility(View.VISIBLE);
						Toast.makeText(getContext(), "postiion" + runPos, Toast.LENGTH_SHORT).show();

					}
					else if (templates.get(runPos).getTemplate_type().equals("html-ext")) {
						if ((!mainModel.isOnline() && templates.get(runPos).getTemplate_duration() > 0)
								|| (mainModel.isOnline() && templates.get(runPos).getTemplate_duration() < 0)) 
							templates.get(runPos).setTemplate_duration(-1 * templates.get(runPos).getTemplate_duration());

						contentWebView.getSettings().setJavaScriptEnabled(true);
						
						contentWebView.setWebChromeClient(new WebChromeClient());
						contentWebView.getSettings().setDomStorageEnabled(true);


						String url = templates.get(runPos).getTemplate_text();
				        contentWebView.loadUrl(url);

						contentTextView.setVisibility(View.INVISIBLE);
						contentVideoView.setVisibility(View.INVISIBLE);
						contentImageView.setVisibility(View.INVISIBLE);

						contentWebView.setVisibility(View.VISIBLE);
						Toast.makeText(getContext(), "postiion" + runPos, Toast.LENGTH_SHORT).show();

					}					
					else if (templates.get(runPos).getTemplate_type().equals("image")) {
						File imgFile = new  File(MainModel.PATH_APP+templates.get(runPos).getTemplate_file());
						if(imgFile.exists()){
	    					if (myBitmap != null) {
	    						myBitmap.recycle();
	    						myBitmap = null;
	    					}

							myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
							contentImageView.setImageBitmap(myBitmap);

							contentTextView.setVisibility(View.INVISIBLE);
							contentVideoView.setVisibility(View.INVISIBLE);
							contentImageView.setVisibility(View.VISIBLE);
							contentWebView.setVisibility(View.INVISIBLE);

//							Toast.makeText(getContext(), "postiion" + runPos, Toast.LENGTH_SHORT).show();
						} else {
							Log.d("clip", "waiting image");

//							if(runPos != 0)
//								runPos = -1; //for undowloaded video, image
//							FullscreenActivity.doRestart(getApplicationContext());

//							contentTextView.setText("Waiting for downloading content...");
//							contentTextView.setBackgroundColor(Color.WHITE);
//
//
//							contentTextView.setVisibility(View.VISIBLE);
//							contentVideoView.setVisibility(View.INVISIBLE);
//							contentImageView.setVisibility(View.INVISIBLE);
//							contentWebView.setVisibility(View.INVISIBLE);
//
//							mainModel.log(MainModel.LOGLEVEL_WARN, "File not found: " + templates.get(runPos).getTemplate_file());
						}

					}
					else if (templates.get(runPos).getTemplate_type().equals("video")) {
						File vidFile = new  File(MainModel.PATH_APP+templates.get(runPos).getTemplate_file());
						if(vidFile.exists()){
							contentTextView.setVisibility(View.INVISIBLE);
							contentVideoView.setVisibility(View.VISIBLE);
							contentImageView.setVisibility(View.INVISIBLE);
							contentWebView.setVisibility(View.INVISIBLE);
	
					        mediaController.setMediaPlayer(contentVideoView);
					        contentVideoView.setVideoPath(MainModel.PATH_APP+templates.get(runPos).getTemplate_file());
							//MediaController mc = new MediaController(FullscreenActivity.this);
							//mc.setVisibility(MediaController.INVISIBLE);
					        contentVideoView.setMediaController(null);
					        contentVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
								@Override
								public boolean onError(MediaPlayer mp, int what, int extra) {
									Log.d("clip", "video error" );
									return true;
								}
							});

					        mediaController.setVisibility(View.INVISIBLE);
					        mediaController.hide();
					        contentVideoView.requestFocus();

							contentVideoView.start();
//							Toast.makeText(getContext(), "postiion" + runPos, Toast.LENGTH_SHORT).show();
//

						} else {
							Log.d("clip", "waiting video");
							if(runPos != 0)
								runPos = -1; //for undowloaded video, image
//							FullscreenActivity.doRestart(getApplicationContext());

//							contentTextView.setText("Waiting for downloading content...");
//							contentTextView.setBackgroundColor(Color.WHITE);

//							contentTextView.setVisibility(View.VISIBLE);
//							contentVideoView.setVisibility(View.INVISIBLE);
//							contentImageView.setVisibility(View.INVISIBLE);
//							contentWebView.setVisibility(View.INVISIBLE);
//
//							mainModel.log(MainModel.LOGLEVEL_WARN, "File not found: " + templates.get(runPos).getTemplate_file());
						}
					}
					int duration = -1;
					if(runPos != -1) { //for undowloaded video, image
					 duration = templates.get(runPos).getTemplate_duration();
					}
//					recreateCounter(templates.get(runPos).getTemplate_duration(), 1000);

					recreateCounter(duration, 1000);
					runPos++;
					start();
				}
            }
        }.start();
	}

	private int getPlaylistLength(LinkedList<Template> templates) {
		int duration = 0;
		for (int i = 0; i < templates.size(); i++) {
			duration += templates.get(i).getTemplate_duration();
		}
		return duration;
	}
	
	public static Context getContext() {
        return context;
    }
		
	public static void doRestart(Context c) {
        try {
            //check if the context is given
            if (c != null) {
                //fetch the packagemanager so we can get the default launch activity 
                // (you can replace this intent with any other activity if you want
                PackageManager pm = c.getPackageManager();
                //check if we got the PackageManager
                if (pm != null) {
                    //create the intent with the default start activity for your application
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                            c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //create a pending intent so the application is restarted after System.exit(0) was called. 
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        //kill the application
                        System.exit(0);
                    } else {
                        Log.e("ERROR restart", "Was not able to restart application, mStartActivity null");
                    }
                } else {
                    Log.e("ERROR restart", "Was not able to restart application, PM null");
                }
            } else {
                Log.e("ERROR restart", "Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            Log.e("ERROR restart", "Was not able to restart application");
        }
    }
	
	@Override
	public void onPause() {
	    super.onPause();  // Always call the superclass method first
	    MainModel.PAUSE = true;
	}
	

    @Override
    public void onUserLeaveHint() {
    	super.onUserLeaveHint();
	    MainModel.PAUSE = true;
    }
	
    @Override
    protected void onStop() {
        super.onStop();
	    MainModel.PAUSE = true;
		mainModel.log("ERROR", "onStop - reboot");
		if (mainModel.isKeepAlive && mainModel.isOnline()) RootAccess.rebootSU();
    }
    
	 private void addOnClickListenerText(View contentView) {
		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
                
				if (TOGGLE_ON_CLICK) {
					mSystemUiHiderText.toggle();
				} else {
					mSystemUiHiderText.show();
				}
			}
		});
	}
	
	 private void addOnClickListenerImage(View contentView) {
		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHiderImage.toggle();
				} else {
					mSystemUiHiderImage.show();
				}
			}
		});
	}
	
	 private void addOnClickListenerVideo(View contentView) {
		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHiderVideo.toggle();
				} else {
					mSystemUiHiderVideo.show();
				}
			}
		});
	}
	 
	 private void addOnClickListenerWeb(View contentView) {
		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHiderWeb.toggle();
				} else {
					mSystemUiHiderWeb.show();
				}
			}
		});
	}

	private void registerSystemUiHiderText(View contentView) {
			final View controlsView = findViewById(R.id.fullscreen_content_controls);
			mSystemUiHiderText = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
			mSystemUiHiderText.setup();
			mSystemUiHiderText
					.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
						// Cached values.
						int mControlsHeight;
						int mShortAnimTime;

						@Override
						@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
						public void onVisibilityChange(boolean visible) {
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
								// If the ViewPropertyAnimator API is available
								// (Honeycomb MR2 and later), use it to animate the
								// in-layout UI controls at the bottom of the
								// screen.
								if (mControlsHeight == 0) {
									mControlsHeight = controlsView.getHeight();
								}
								if (mShortAnimTime == 0) {
									mShortAnimTime = getResources().getInteger(
											android.R.integer.config_shortAnimTime);
								}
								controlsView
										.animate()
										.translationY(visible ? 0 : mControlsHeight)
										.setDuration(mShortAnimTime);
							} else {
								// If the ViewPropertyAnimator APIs aren't
								// available, simply show or hide the in-layout UI
								// controls.
								controlsView.setVisibility(visible ? View.VISIBLE
										: View.GONE);
							}

							if (visible && AUTO_HIDE) {
								// Schedule a hide().
								delayedHideText(AUTO_HIDE_DELAY_MILLIS);
							}
						}
					});
	}
	
	private void registerSystemUiHiderImage(View contentView) {
		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		mSystemUiHiderImage = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
		mSystemUiHiderImage.setup();
		mSystemUiHiderImage
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHideImage(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});
	}
	
	private void registerSystemUiHiderVideo(View contentView) {
		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		mSystemUiHiderVideo = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
		mSystemUiHiderVideo.setup();
		mSystemUiHiderVideo
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHideVideo(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});
	}
	
	private void registerSystemUiHiderWeb(WebView contentView) {
		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		mSystemUiHiderWeb = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
		mSystemUiHiderWeb.setup();
		mSystemUiHiderWeb
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});
	}

	/* Called when the application resumes */
	   @Override
	   protected void onResume() {
	      super.onResume();
		  MainModel.PAUSE = false;
		  MainModel.PAUSE_COUNT = 0;
		  
		  hideAllButtons();
	   }
	   
	private void hideAllButtons() {
		mSystemUiHiderText.hide();
		mSystemUiHiderImage.hide();
		mSystemUiHiderVideo.hide();
		mSystemUiHiderWeb.hide();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHideAll(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
            Intent myIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(myIntent);
			if (AUTO_HIDE) {
				delayedHideAll(AUTO_HIDE_DELAY_MILLIS);
			}
			
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	
	Runnable mHideRunnableText = new Runnable() {
		@Override
		public void run() {
			mSystemUiHiderText.hide();
		}
	};
	Runnable mHideRunnableVideo = new Runnable() {
		@Override
		public void run() {
			mSystemUiHiderVideo.hide();
		}
	};
	Runnable mHideRunnableImage = new Runnable() {
		@Override
		public void run() {
			mSystemUiHiderImage.hide();
		}
	};
	
	Runnable mHideRunnableWeb = new Runnable() {
		@Override
		public void run() {
			mSystemUiHiderWeb.hide();
		}
	};

	private void delayedHideAll(int delayMillis) {
		delayedHideText(delayMillis);
		delayedHideVideo(delayMillis);
		delayedHideImage(delayMillis);
		delayedHide(delayMillis);		
	}
	
	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHideText(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnableText);
		mHideHandler.postDelayed(mHideRunnableText, delayMillis);
	}
	
	private void delayedHideVideo(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnableVideo);
		mHideHandler.postDelayed(mHideRunnableVideo, delayMillis);
	}
	
	private void delayedHideImage(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnableImage);
		mHideHandler.postDelayed(mHideRunnableImage, delayMillis);
	}
	

	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnableWeb);
		mHideHandler.postDelayed(mHideRunnableWeb, delayMillis);
	}

	private boolean permissions() {
		if (!hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE) 
				|| !hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
				|| !hasPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED)
				|| !hasPermission(Manifest.permission.WAKE_LOCK)
				) {
			String[] perms = {
					Manifest.permission.READ_EXTERNAL_STORAGE, 
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.WAKE_LOCK,
					Manifest.permission.RECEIVE_BOOT_COMPLETED
					};			
			int permsRequestCode = 1; 
			ActivityCompat.requestPermissions(FullscreenActivity.this, perms, permsRequestCode);
		}
		return (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE) 
				&& hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
				&& hasPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED)
				&& !hasPermission(Manifest.permission.WAKE_LOCK)
				);
	}
	
	private boolean hasPermission(String permission) {
		return(ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED);
	}
}
