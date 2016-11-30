package mx.edu.itcelaya.ecommercecustomers.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import mx.edu.itcelaya.ecommercecustomers.MainActivity;

/**
 * Created by niluxer on 5/19/16.
 */
public class Utils {

    public static Bitmap DownloadImage(String URL)     {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL, false);
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IOException e1) {
            //Toast.makeText(context, e1.getMessage(), Toast.LENGTH_LONG).show();
            e1.printStackTrace();
        }
        return bitmap;
    }

    public static InputStream OpenHttpConnection(String urlString, Boolean checkAuth)
            throws IOException
    {
        InputStream in = null;
        int response = -1;
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("GET");

            if (checkAuth) {
                String credentials = MainActivity.consumer_key + ":" + MainActivity.consumer_secret;
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                httpConn.setRequestProperty("Authorization", "Basic " + base64EncodedCredentials);
            }

            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        }
        catch (Exception ex) {
            throw new IOException("Error connecting" + response + ex.getMessage());
        }
        return in;
    }

    public static String md5(String s)
    {
        String hashtext = "";
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(s.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            hashtext = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while(hashtext.length() < 32 ){
                hashtext = "0"+hashtext;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashtext;
    }
}
