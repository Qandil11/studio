package de.stamm.core;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;

public class DownloadTaskPost extends AsyncTask<String, Integer, String> {

    private Context context;
    private PowerManager.WakeLock mWakeLock;
    private String param0;
    private String param1;
    private String param2;


    public DownloadTaskPost(Context context) {
        this.context = context;
    }

    protected String doInBackground(String... param) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            param0 = param[0];
            param1 = param[1];
            param2 = param[2];
            URL url = new URL(param[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            Log.d("clip", "path"+ url.getPath());

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            //int fileLength = connection.getContentLength();

            // download the file
        	input = new BufferedInputStream(url.openStream());
            output = new FileOutputStream(param[1]);

            byte data[] = new byte[8192];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null){
            		output.flush();
                    output.close();
                }

                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return "ok";
    }

    @Override
    protected void onPostExecute(String s) {
//        super.onPostExecute(s);
//       DownloadTask downloadTask = new DownloadTask(context);
//        String[] param = {param1, param2}; //downoad on temp file so we can not use half downloaded file
//        downloadTask.execute(param);
        File testFile = new File(param1);
        File testFile1 = new File(param2); // create temp file so we can not use half downloaded file

        copyFile(testFile, testFile1); //copying the temp file to original
         //delete temporay file

    }
    private void deleteFile(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteFile(child);

        fileOrDirectory.delete();
    }
    public  void copyFile(File sourceFile, File destFile)  {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
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
                        deleteFile(sourceFile);
                    }
                } catch (IOException io) {
                    Log.e("clip", "in exception box");
                }
            }
//        }).start();
//    }

}