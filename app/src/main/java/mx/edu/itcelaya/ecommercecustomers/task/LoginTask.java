package mx.edu.itcelaya.ecommercecustomers.task;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import mx.edu.itcelaya.ecommercecustomers.utils.Utils;

/**
 * Created by niluxer on 5/23/16.
 */

public class LoginTask extends AsyncTask<String, Void, String> {
    String jsonResult;
    private String username="", password="";
    private Context contexto;

    public LoginTask(Context c) {
        // TODO Auto-generated constructor stub
        contexto = c;
    }

    public void setUsername(String _username){
        username = _username;
    }

    public void setPassword(String _password){
        password = _password;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            jsonResult = inputStreamToString(OpenHttpConnectionParams(params[0], username, password)).toString();
        } catch (IOException e1) {
            //e1.printStackTrace();
            System.out.printf(e1.getMessage());
        }
        return jsonResult;
    }

    private StringBuilder inputStreamToString(InputStream is) {
        String rLine = "";
        StringBuilder answer = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        try {
            while ((rLine = rd.readLine()) != null) {
                answer.append(rLine);
            }
        }

        catch (IOException e) {
            // e.printStackTrace();
            Toast.makeText(contexto,
                    "Error..." + e.toString(), Toast.LENGTH_LONG).show();
        }
        return answer;
    }


    public static InputStream OpenHttpConnectionParams(String urlString, String username, String password)
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
            httpConn.setRequestMethod("POST");

            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("username", username)
                    .appendQueryParameter("password", password);
            String query = builder.build().getEncodedQuery();

            OutputStream os = httpConn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

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

}