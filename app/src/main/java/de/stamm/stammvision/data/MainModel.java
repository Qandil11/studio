package de.stamm.stammvision.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.stamm.core.DownloadTask;
import de.stamm.core.DownloadTaskPost;
import de.stamm.core.FileDownloader;
import de.stamm.core.Formats;
import de.stamm.stammvision.BackgroundTask;
import de.stamm.stammvision.rootaccess.RootAccess;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class MainModel extends Application implements Application.ActivityLifecycleCallbacks {

	public static boolean ROOTED = false;

//	public final static String MAIN_URL = "https://www.stammvision.com/"; //"https://ssl-account.com/stammvision.com/";
	public final static String MAIN_URL = "https://www.direct-clip.de/";
//	public final static String MAIN_URL = "https://dev.simplyonair.de/directclip_web/server/directclip_Server/";

	public final static String SYSTEM_APP_FILENAME = "directclip.apk";

	public final static String SERVER = MAIN_URL+"webservice";
	public final static String Server_UPDATE = MAIN_URL+"updates/";
	public final static String Server_MEDIA = MAIN_URL+"media/";
	public final static String PORT = "443";

	public final static String PATH_ROOT = Environment.getExternalStorageDirectory() + "/";
	public final static String PATH_APP = PATH_ROOT + "stammvision/";

	public final static String SYSTEM_APP_ROOT = "/system/app/";

	public final static String SETTINGS_FILENAME = "settings.xml";
	public final static String PLAYLISTS_FILENAME = "playlists.xml";

	public static final String LOGLEVEL_INFO = "INFO";
	public static final String LOGLEVEL_WARN = "WARN";
	public static final String LOGLEVEL_ERROR = "ERROR";

	public static boolean IS_PAUSE = false;

	public static boolean START_BACKGROUND = false;
	public static boolean START_BACKGROUND_SETTINGS = false;

	public static Timestamp LAST_LOG = new Timestamp(System.currentTimeMillis());

	int gsmSignalStrength = -1;

	WebserviceConnector webserviceConnector;
	Settings settings;

	LinkedList<String> deleteFiles = new LinkedList<String>();

	private LinkedList<Playlist> playlists = new LinkedList<Playlist>();
	Playlist currentPlaylist = new Playlist();

	LinkedList<Template> default_templates = new LinkedList<Template>();

	public boolean isDebug = false;
	public boolean isKeepAlive = false;
	public boolean id_already_known = false;

	public boolean isReboot = false;
	public boolean isClear = false;
	public String app_file = "";
	public String app_version = "";

	public boolean is_device_registered = false;
	public boolean is_device_id_known = false;

	public static boolean PAUSE = false;
	public static int PAUSE_COUNT = 0;

	public boolean startDone = false;

	public static String[] HTML_FILES = new String[0];

	public MainModel() {
		webserviceConnector = new WebserviceConnector(this, SERVER, PORT);
		settings = new Settings(this);

		ROOTED = RootAccess.isDeviceRooted();

		String path = PATH_APP;
		File dir = new File(path);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}
		loadData();
	}

	private void loadData() {
		settings.loadXML();
	}

	public double getVersion() {
		try {
			return Double.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0 ).versionName);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0.0;
	}

	public void alarmSound(int seconds) {
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if(alert == null) {
			// alert is null, using backup
			alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if(alert == null){  // I can't see this ever being null (as always have a default notification) but just incase
				// alert backup is null, using 2nd backup
				alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}
		Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alert);
		r.play();
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {}
		r.stop();
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			if (netInfo.getType() == ConnectivityManager.TYPE_WIFI
					|| netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
				return true;
			} else {
				if (netInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_UMTS
						|| netInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_HSDPA
						|| netInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_HSPA
						|| netInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_HSUPA
						|| netInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_HSPAP
						|| netInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE
				) {
					if (this.getGsmSignalStrength() > 7) {
						return true;
					} else {
						return false;
					}
				}
				/*
				 * TelephonyManager.NETWORK_TYPE_1xRTT
				 * TelephonyManager.NETWORK_TYPE_EDGE
				 * TelephonyManager.NETWORK_TYPE_EVDO_0
				 * TelephonyManager.NETWORK_TYPE_EVDO_A
				 */
			}
		}
		return false;
	}


	public int getGsmSignalStrength() {
		return gsmSignalStrength;
	}

	public void setGsmSignalStrength(int gsmSignalStrength) {
		this.gsmSignalStrength = gsmSignalStrength;
	}

	public WebserviceConnector getWebserviceConnector() {
		return webserviceConnector;
	}

	public Playlist getCurrentPlaylist() {
		loadCurrentPlaylist();
		return currentPlaylist;
	}

	/**
	 * @return the settings
	 */
	public Settings getSettings() {
		return settings;
	}


	@SuppressLint("DefaultLocale")
	public void generateDevice_id() {
		String deviceId = this.getDeviceId().toUpperCase();
		settings.setDevice_id(deviceId);
	}

	@SuppressLint("DefaultLocale")
	public void generateDevice_id_online() {
		String deviceId = "";

		if (this.isOnline()) {
			try {
				deviceId = "MAN"+Formats.generateRandomString(13, "alphanumeric").toUpperCase();
				settings.setDevice_id(deviceId);
				settings.saveXML(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isDeviceIdRegistered() {
		return is_device_registered;
	}

	public void getPlaylists() {
		if (this.isOnline()) {
			loadDefaultTemplates();

			Object[][] userData = new Object[1][2];
			userData[0][0] = "method";
			userData[0][1] = "playlists";
			JSONArray array = webserviceConnector.getResult(userData);

			if (array == null) return;
			try {
				this.readPlayList(array);
			} catch (Exception e) {
				Log.e("Exception readPlayList", "Exception in readPlayList "+e.getMessage());
			}
		}
	}

	public void log(final String level, final String text) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		if (this.isOnline() && isDeviceIdRegistered() && ((now.getTime() - LAST_LOG.getTime()) > 1000)) {
			if (isDebug) {
				Thread runningThread = new Thread() {
					@Override
					public void run() {
						Object[][] userData = new Object[3][2];
						userData[0][0] = "method";
						userData[0][1] = "log";
						userData[1][0] = "level";
						userData[1][1] = level;
						userData[2][0] = "entry";
						userData[2][1] = text;
						webserviceConnector.insert(userData);
					}
				};
				runningThread.start();
			}
			LAST_LOG = new Timestamp(System.currentTimeMillis());
		}
	}

	private void savePlaylists(boolean docTypeAvailable) {
		Element root = new  Element("playlists");
		Document doc = new Document(root);
		if (docTypeAvailable) {
			DocType doctype =  new DocType("playlists", "playlists.dtd");
			doc.setDocType(doctype);
		}

		for (int i = 0; i < playlists.size(); i++) {
			Element visit = playlists.get(i).getXML();
			root.addContent(visit);
		}

		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		FileOutputStream output;
		try {
			output = new FileOutputStream(PATH_APP+PLAYLISTS_FILENAME);
			outputter.output(doc, output);
			output.close();
		} catch (FileNotFoundException e) {
			Log.e("saveVisits", "FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			Log.e("saveVisits", "IOException: " + e.getMessage());
		}
	}

	public void loadPlaylists() {
		playlists.clear();
		if ((new File(PATH_APP+PLAYLISTS_FILENAME)).exists()) {
			Document doc;
			try {
				FileInputStream input = new FileInputStream(PATH_APP+PLAYLISTS_FILENAME);
				SAXBuilder sxbuild = new SAXBuilder();
				doc = sxbuild.build(input);
				Element root = doc.getRootElement();

				// program settings
				List<Element> list = root.getChildren("playlist");
				for (int i = 0; i < list.size(); i++) {
					Playlist playlist = new Playlist();
					playlist.readXML((Element) list.get(i));
					playlists.add(playlist);
				}
			} catch (JDOMException e) {
				Log.e("loadVisits", "JDOMException: " + e.getMessage());
			} catch (IOException e) {
				Log.e("loadVisits", "Fehler beim Einlesen der Datei: " + e.getMessage());
			}
		}
	}

	private void readPlayList(JSONArray array) {
		String path = PATH_APP;
		File dir = new File(path);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
			generateDevice_id_online();
		}


		LinkedList<Playlist> newPlaylists = new LinkedList<Playlist>();
		for (int i = 0; i < array.size(); i++) {
			Playlist playlist = new Playlist();
			JSONObject jsonData = (JSONObject) array.get(i);
			playlist.readResult(jsonData);
			LinkedList<Template> templates = getTemplates(playlist.getPlaylists_id());

			if (templates == null) return;

			downloadTargetFiles(templates);

			if (default_templates.size() > 0) {
				LinkedList<Template> templates_sum = default_templates;
				for (int j = 0; j < templates.size(); j++) {
					templates_sum.add(templates.get(j));
				}
				playlist.setTemplates(templates_sum);
			} else {
				playlist.setTemplates(templates);
			}
			if (playlist.getTemplates().size() > 0) newPlaylists.add(playlist);
		}

		boolean changed = mergePlaylists(playlists, newPlaylists);
		changed = true;
		if (changed || playlists.size() == 0 || newPlaylists.size() == 0) {
			playlists.clear();
			playlists = newPlaylists;
		}
		savePlaylists(false);
	}


	private boolean mergePlaylists(LinkedList<Playlist> oldPlaylist,
								   LinkedList<Playlist> newPlaylists) {
		boolean changed = false;
		Hashtable<Integer, Boolean> keepFiles = new Hashtable<Integer, Boolean>();
		for (int i = 0; i < newPlaylists.size(); i++) {
			for (int j = 0; j < oldPlaylist.size(); j++) {
				if (newPlaylists.get(i).getPlaylists_id() == oldPlaylist.get(j).getPlaylists_id()) {
					keepFiles.put(oldPlaylist.get(j).getPlaylists_id(), true);
					log(LOGLEVEL_WARN, "New playlist: " + newPlaylists.get(i).getPlaylist_name());
					changed = true;
					downloadTargetFiles(newPlaylists.get(i).getTemplates());
					for (int k = 0; k < oldPlaylist.get(j).getTemplates().size(); k++) {
						boolean found_inner = false;
						for (int l = 0; l < newPlaylists.get(i).getTemplates().size(); l++) {
							if (newPlaylists.get(i).getTemplates().get(l).getTemplate_file()
									.equals(oldPlaylist.get(j).getTemplates().get(k).getTemplate_file())) {
								found_inner = true;
							}
						}
						if (!found_inner) {
							log(LOGLEVEL_WARN, "delete file: " + oldPlaylist.get(j).getTemplates().get(k).getTemplate_file());
							deleteFiles.add(oldPlaylist.get(j).getTemplates().get(k).getTemplate_file());
						}
					}
				}
			}
		}

		for (int j = 0; j < newPlaylists.size(); j++) {
			if (!keepFiles.containsKey(newPlaylists.get(j).getPlaylists_id())) {
				downloadTargetFiles(newPlaylists.get(j).getTemplates());
				changed = true;
			}
		}

		for (int i = 0; i < newPlaylists.size(); i++) {
			for (int l = 0; l < newPlaylists.get(i).getTemplates().size(); l++) {
				if (!newPlaylists.get(i).getTemplates().get(l).getTemplate_file().trim().equals("")) {
					File tmpFile = new File(MainModel.PATH_APP+newPlaylists.get(i).getTemplates().get(l).getTemplate_file());
					if (!tmpFile.exists()) {
						downloadTargetFiles(newPlaylists.get(i).getTemplates());
						if (!tmpFile.exists()) {
							log(LOGLEVEL_WARN, "File not found after download: " + newPlaylists.get(i).getTemplates().get(l).getTemplate_file());
							return false;
						} else {
							changed = true;
						}
					}
				}
			}
		}

		for (int j = 0; j < oldPlaylist.size(); j++) {
			if (!keepFiles.containsKey(oldPlaylist.get(j).getPlaylists_id())) {
				for (int k = 0; k < oldPlaylist.get(j).getTemplates().size(); k++) {
					deleteFiles.add(oldPlaylist.get(j).getTemplates().get(k).getTemplate_file());
					changed = true;
				}
			}
		}
		return changed;
	}

	private void downloadFiles(LinkedList<Template> templates) {
		for (int i = 0; i < templates.size(); i++) {
			if (!templates.get(i).getTemplate_file().equals("") && !templates.get(i).getTemplate_file().equals("NULL")
					&& !templates.get(i).getTemplate_type().equals("text")) {
				try {
					String filename_download[] = templates.get(i).getTemplate_file().split(";");
					for (int j = 0; j < filename_download.length; j++) {
						String loadFilename = MainModel.Server_MEDIA+this.getSettings().getOnline_folder()+filename_download[j];
						String targetFile = MainModel.PATH_APP+filename_download[j];
						File testFile = new File(targetFile);
						if (!testFile.exists()) {
							this.log(LOGLEVEL_INFO, "Start downloading file: " + loadFilename + " - " + targetFile);


							final DownloadTask downloadTask = new DownloadTask(MainModel.this);
							String[] param = {loadFilename, targetFile};
							downloadTask.execute(param);
							//FileDownloader.downloadFile(loadFilename, targetFile);
						} else {
							//this.log(LOGLEVEL_INFO, "Skipping file: " + targetFile);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					this.log(LOGLEVEL_ERROR, "Error downloading file: " + templates.get(i).getTemplate_file() + " - " + e.getMessage());
				}
			}
		}
	}
	private void downloadTargetFiles(LinkedList<Template> templates) {
		for (int i = 0; i < templates.size(); i++) {
			if (!templates.get(i).getTemplate_file().equals("") && !templates.get(i).getTemplate_file().equals("NULL")
					&& !templates.get(i).getTemplate_type().equals("text")) {
				try {
					String filename_download[] = templates.get(i).getTemplate_file().split(";");
					for (int j = 0; j < filename_download.length; j++) {
						String loadFilename = MainModel.Server_MEDIA+this.getSettings().getOnline_folder()+filename_download[j];
						String targetFile = MainModel.PATH_APP+filename_download[j];
						String targetFile1 = MainModel.PATH_APP+"temp"+filename_download[j];

						File testFile = new File(targetFile);
						if (!testFile.exists()) {
							this.log(LOGLEVEL_INFO, "Start downloading file: " + loadFilename + " - " + targetFile);


							final DownloadTaskPost downloadTask = new DownloadTaskPost(MainModel.this);
							String[] param = {loadFilename, targetFile1, targetFile}; //downoad on temp file so we can not use half downloaded file
							downloadTask.execute(param);
//							if(isCcomplted.equals("ok")) {
//								File testFile1 = new File(targetFile1); // create temp file so we can not use half downloaded file
//
//								copyFile(testFile1, testFile); //copying the temp file to original
//								deleteFile(testFile1); //delete temporay file
//							}
							//FileDownloader.downloadFile(loadFilename, targetFile);
						} else {
							//this.log(LOGLEVEL_INFO, "Skipping file: " + targetFile);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					this.log(LOGLEVEL_ERROR, "Error downloading file: " + templates.get(i).getTemplate_file() + " - " + e.getMessage());
				}
			}
		}
	}
	private void deleteFile(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory())
			for (File child : fileOrDirectory.listFiles())
				deleteFile(child);

		fileOrDirectory.delete();
	}
	public static void copyFile(File sourceFile, File destFile)  {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (!destFile.getParentFile().exists())
						destFile.getParentFile().mkdirs();

					if (!destFile.exists()) {
						destFile.createNewFile();
					}

					FileChannel source = null;
					FileChannel destination = null;

					try {
						source = new FileInputStream(sourceFile).getChannel();
						destination = new FileOutputStream(destFile).getChannel();
						destination.transferFrom(source, 0, source.size());
					} finally {
						if (source != null) {
							source.close();
						}
						if (destination != null) {
							destination.close();
						}
					}
				} catch (IOException io) {
					Log.e("clip", "in exception box");
				}
			}
		}).start();
	}


	private void downloadCommonFile(String filename) {
		String loadFilename = MainModel.Server_MEDIA+filename;
		String targetFile = MainModel.PATH_APP+filename;
		File testFile = new File(targetFile);
		if (!testFile.exists()) {
			this.log(LOGLEVEL_INFO, "Start downloading file: " + loadFilename + " - " + targetFile);

			final DownloadTask downloadTask = new DownloadTask(MainModel.this);
			String[] param = {loadFilename, targetFile};
			downloadTask.execute(param);

			//FileDownloader.downloadFile(loadFilename, targetFile);
		} else {
			//this.log(LOGLEVEL_INFO, "Skipping file: " + targetFile);
		}
	}

	private LinkedList<Template> getTemplates(int playlists_id) {
		LinkedList<Template> templates = new LinkedList<Template>();
		if (this.isOnline()) {
			Object[][] userData = new Object[2][2];
			userData[0][0] = "method";
			userData[0][1] = "templates";
			userData[1][0] = "playlists_id";
			userData[1][1] = playlists_id;
			JSONArray array = webserviceConnector.getResult(userData);

			if (array == null) return null;

			for (int i = 0; i < array.size(); i++) {
				Template template = new Template();
				JSONObject jsonData = (JSONObject) array.get(i);
				template.readResult(jsonData);
				templates.add(template);
			}
		}
		return templates;
	}

	public void loadDefaultTemplates() {
		default_templates= new LinkedList<Template>();
		if (this.isOnline()) {
			Object[][] userData = new Object[1][2];
			userData[0][0] = "method";
			userData[0][1] = "default_templates";
			JSONArray array = webserviceConnector.getResult(userData);

			if (array == null) return;

			for (int i = 0; i < array.size(); i++) {
				Template template = new Template();
				JSONObject jsonData = (JSONObject) array.get(i);
				template.readResult(jsonData);
				default_templates.add(template);
			}
		}
	}

	private void loadCurrentPlaylist() {
		currentPlaylist = null;
		String currentDay = "";
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		switch (day) {
			case Calendar.SUNDAY: currentDay = "so"; break;
			case Calendar.MONDAY: currentDay = "mo"; break;
			case Calendar.TUESDAY: currentDay = "tu"; break;
			case Calendar.WEDNESDAY: currentDay = "we"; break;
			case Calendar.THURSDAY: currentDay = "th"; break;
			case Calendar.FRIDAY: currentDay = "fr"; break;
			case Calendar.SATURDAY: currentDay = "sa"; break;
		}

		for (int i = 0; i < playlists.size(); i++) {
			if(Formats.isCurrentDateInIntervall(playlists.get(i).getStart_date(), playlists.get(i).getEnd_date())
					&& Formats.isCurrentTimeInIntervall(playlists.get(i).getDaily_start_time(), playlists.get(i).getDaily_end_time())) {

				if (currentDay.equals("so") && playlists.get(i).getSo() == 1) {
					currentPlaylist = playlists.get(i);
					break;
				} else if (currentDay.equals("mo") && playlists.get(i).getMo() == 1) {
					currentPlaylist = playlists.get(i);
					break;
				} else if (currentDay.equals("tu") && playlists.get(i).getTu() == 1) {
					currentPlaylist = playlists.get(i);
					break;
				} else if (currentDay.equals("we") && playlists.get(i).getWe() == 1) {
					currentPlaylist = playlists.get(i);
					break;
				} else if (currentDay.equals("th") && playlists.get(i).getTh() == 1) {
					currentPlaylist = playlists.get(i);
					break;
				} else if (currentDay.equals("fr") && playlists.get(i).getFr() == 1) {
					currentPlaylist = playlists.get(i);
					break;
				} else if (currentDay.equals("sa") && playlists.get(i).getSa() == 1) {
					currentPlaylist = playlists.get(i);
					break;
				}
			}
		}

		while (deleteFiles.size() > 0) {
			try {
				File deleteFile = new File(PATH_APP+deleteFiles.get(0));
				if (deleteFile.exists()) deleteFile.delete();
			} catch (Exception e) {
				this.log(LOGLEVEL_ERROR, "Error deleting Files: " + deleteFiles.get(0) + " - " + e.getMessage());
			}
			deleteFiles.remove(0);
		}

		if (currentPlaylist == null && playlists.size() > 0) {
			currentPlaylist = playlists.get(0);
		}
	}

	public String getDeviceId() {
		return android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
	}



	/**
	 * @return the id_already_known
	 */
	public boolean isId_already_known() {
		return id_already_known;
	}

	/**
	 * @param id_already_known the id_already_known to set
	 */
	public void setId_already_known(boolean id_already_known) {
		this.id_already_known = id_already_known;
	}

	@Override
	public void onLowMemory() {
		log("ERROR", "Low Memory - reboot");
		RootAccess.rebootSU();
	}


	// TEST System state

	@Override
	public void onCreate() {
		super.onCreate();
		registerActivityLifecycleCallbacks(this);
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

	}

	@Override
	public void onActivityStarted(Activity activity) {
	}

	@Override
	public void onActivityResumed(Activity activity) {
	}

	@Override
	public void onActivityPaused(Activity activity) {
		log("ERROR", "onActivityPaused - reboot");
		//if (isDebug || isKeepAlive) RootAccess.rebootSU();
	}

	@Override
	public void onActivityStopped(Activity activity) {
		log("ERROR", "onActivityStopped - reboot");
		if (isDebug || isKeepAlive) RootAccess.rebootSU();
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
		//log("ERROR", "onActivitySaveInstanceState - reboot");
		//if (isDebug) RootAccess.rebootSU();
	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		log("ERROR", "onActivityDestroyed - reboot");
		if (isDebug || isKeepAlive) RootAccess.rebootSU();
	}

	public void installInfoNewVersion() {
		if (this.isOnline() && isDeviceIdRegistered()) {
			Thread runningThread = new Thread() {
				@Override
				public void run() {
					Object[][] userData = new Object[1][2];
					userData[0][0] = "method";
					userData[0][1] = "installNewVersion";
					webserviceConnector.insert(userData);
				}
			};
			runningThread.start();
		}
	}

	public void serverCommuniction() {
		if (this.isOnline()) {
			Thread runningThread = new Thread() {
				@Override
				public void run() {
					String names = "";
					for (int i = 0; i < playlists.size(); i++) {
						if (i != 0) names += ";";
						names += playlists.get(i).getPlaylist_name();
					}

					Object[][] userData = new Object[25][2];

					userData[0][0] = "method";
					userData[0][1] = "global";

					userData[1][0] = "updatedPlaylists";
					userData[1][1] = names;

					userData[2][0] = "version";
					userData[2][1] = getVersion();

					String name = "";
					if (currentPlaylist != null) name = currentPlaylist.getPlaylist_name();

					userData[3][0] = "current_playlist";
					userData[3][1] = name;

					WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
					Display display = wm.getDefaultDisplay();
					Point size = new Point();
					display.getSize(size);

					userData[4][0] = "screen_width";
					userData[4][1] = size.x;

					userData[5][0] = "screen_height";
					userData[5][1] = size.y;

					userData[6][0] = "os_version";
					userData[6][1] = System.getProperty("os.version")+ "(" + android.os.Build.VERSION.INCREMENTAL + ")";

					userData[7][0] = "os_api_level";
					userData[7][1] = android.os.Build.VERSION.SDK_INT;

					userData[8][0] = "device";
					userData[8][1] = android.os.Build.DEVICE;

					userData[9][0] = "model";
					userData[9][1] = android.os.Build.MODEL;

					userData[10][0] = "product";
					userData[10][1] = android.os.Build.PRODUCT;

					userData[11][0] = "release";
					userData[11][1] = android.os.Build.VERSION.RELEASE;

					userData[12][0] = "brand";
					userData[12][1] = android.os.Build.BRAND;

					userData[13][0] = "display";
					userData[13][1] = android.os.Build.DISPLAY;

					userData[14][0] = "cpu_abi";
					userData[14][1] = android.os.Build.CPU_ABI;

					userData[15][0] = "cpu_abi_2";
					userData[15][1] = android.os.Build.CPU_ABI2;

					userData[16][0] = "hardware";
					userData[16][1] = android.os.Build.HARDWARE;

					userData[17][0] = "id";
					userData[17][1] = android.os.Build.ID;

					userData[18][0] = "manufacturer";
					userData[18][1] = android.os.Build.MANUFACTURER;

					userData[19][0] = "serial";
					userData[19][1] = android.os.Build.SERIAL;

					userData[20][0] = "user";
					userData[20][1] = android.os.Build.USER;

					userData[21][0] = "host";
					userData[21][1] = android.os.Build.HOST;

					userData[22][0] = "os_name";
					userData[22][1] = System.getProperty("os.name");

					userData[23][0] = "rooted";
					if (ROOTED) userData[23][1] = 1;
					else userData[23][1] = 0;

					userData[24][0] = "online";
					userData[24][1] = "1";

					JSONArray array = webserviceConnector.serverRequest(userData);

					if (array == null) return;

					String standard_files_web = "";
					if (array.size() > 0) {
						JSONObject jsonData = (JSONObject) array.get(0);
						isClear = String.valueOf(jsonData.get("is_clear")).equals("1");
						is_device_registered = String.valueOf(jsonData.get("is_device_registered")).equals("1");
						is_device_id_known = String.valueOf(jsonData.get("is_device_id_known")).equals("1");
						isKeepAlive = String.valueOf(jsonData.get("keep_alive")).equals("1");
						app_file = String.valueOf(jsonData.get("app_file"));
						app_version = String.valueOf(jsonData.get("app_version"));
						isReboot = String.valueOf(jsonData.get("reboot")).equals("1");
						isDebug = String.valueOf(jsonData.get("debug")).equals("1");
						getSettings().setCustomers_name(String.valueOf(jsonData.get("customers_name")));
						getSettings().setOnline_folder(String.valueOf(jsonData.get("customers_folder")));

						standard_files_web = String.valueOf(jsonData.get("standard_files_web"));
					}

					if (isClear) {
						isReboot = true;
						try {
							File f = new File(PATH_APP);
							File[] fileArray = f.listFiles();
							for (int i = 0; i < fileArray.length; i++) {
								fileArray[i].delete();
							}
							playlists.clear();
							currentPlaylist = null;
						} catch (Exception e) {
							log(LOGLEVEL_ERROR, "Error deleting Files: " + deleteFiles.get(0) + " - " + e.getMessage());
						}

						log(MainModel.LOGLEVEL_INFO, "reboot from Web after clear");
						RootAccess.rebootSU();
					}

					HTML_FILES = standard_files_web.split(";");
					for (int i = 0; i < HTML_FILES.length; i++) {
						downloadCommonFile(HTML_FILES[i]);
					}

					if (isReboot) {
						log(MainModel.LOGLEVEL_INFO, "reboot from Web");
						RootAccess.rebootSU();
					}


				}
			};
			runningThread.start();
		}
	}

	/**
	 * @return the is_device_id_known
	 */
	public boolean isIs_device_id_known() {
		return is_device_id_known;
	}

	/**
	 * @param is_device_id_known the is_device_id_known to set
	 */
	public void setIs_device_id_known(boolean is_device_id_known) {
		this.is_device_id_known = is_device_id_known;
	}

	/**
	 * @return the app_file
	 */
	public String getApp_file() {
		return app_file;
	}

	/**
	 * @return the app_version
	 */
	public String getApp_version() {
		return app_version;
	}

	/**
	 * @return the isReboot
	 */
	public boolean isReboot() {
		return isReboot;
	}

	/**
	 * @return the isDebug
	 */
	public boolean isDebug() {
		return isDebug;
	}
	/**
	 * @return the startDone
	 */
	public boolean isStartDone() {
		return startDone;
	}

	/**
	 * @param startDone the startDone to set
	 */
	public void setStartDone(boolean startDone) {
		this.startDone = startDone;
	}


}

