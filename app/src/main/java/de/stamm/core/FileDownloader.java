package de.stamm.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

//import org.apache.http.util.ByteArrayBuffer;

import android.util.Log;

public class FileDownloader {

    public static String downloadFile(String urlFile, String outputPath) {
        try {

            URL url = new URL(urlFile); //you can write here any link
            File file = new File(outputPath);

            long startTime = System.currentTimeMillis();
            Log.d("ImageManager", "download begining");
            Log.d("ImageManager", "download url:" + url);
            Log.d("ImageManager", "downloaded file name:" + outputPath);
            /* Open a connection to that URL. */
            URLConnection ucon = url.openConnection();

            /*
             * Define InputStreams to read from the URLConnection.
             */            
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);


            /*
             * Read bytes to the Buffer until there is nothing more to read(-1).
             */
            /*
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
            }

            /* Convert the Bytes read to a String. */
            /*
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.close();
            Log.d("ImageManager", "download ready in"
                            + ((System.currentTimeMillis() - startTime) / 1000)
                            + " sec");
            */

        } catch (Exception e) {
        	e.printStackTrace();
            //Log.e("Error: ", e.getMessage());
        }

        return null;
    }
}
