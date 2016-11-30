package mx.edu.itcelaya.ecommercecustomers.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
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

import mx.edu.itcelaya.ecommercecustomers.MainActivity;
import mx.edu.itcelaya.ecommercecustomers.model.Customer;
import mx.edu.itcelaya.ecommercecustomers.utils.Json;
import mx.edu.itcelaya.ecommercecustomers.utils.Utils;

/**
 * Created by niluxer on 5/16/16.
 */

public class WooCommerceTask extends AsyncTask<String, Void, String> {

    private String jsonResult;
    private Context contexto;
    private ProgressDialog pDlg;
    private Object obj;

    public static final int POST_TASK = 1;
    public static final int GET_TASK = 2;
    public static final int PUT_TASK = 3;
    public static final int DELETE_TASK = 4;
    private String processMessage = "Procesando...";
    private int taskType = GET_TASK;
    private AsyncResponse delegate;


    public WooCommerceTask(Context c, int taskType, String processMessage, AsyncResponse _delegate) {
        this.contexto = c;
        this.taskType = taskType;
        this.processMessage = processMessage;
        delegate = _delegate;
    }

    private void showProgressDialog() {

        pDlg = new ProgressDialog(contexto);
        pDlg.setMessage(processMessage);
        //pDlg.setProgressDrawable(contexto.getWallpaper());
        pDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDlg.setCancelable(false);
        pDlg.show();

    }

    public void setObject(Object obj) {
        this.obj = obj;
    }

    @Override
    protected void onPreExecute() {
        showProgressDialog();
    }

    @Override
    protected void onPostExecute(String response) {

        if (pDlg.isShowing()) pDlg.dismiss();
        delegate.setResponse(response);

    }

    @Override
    protected String doInBackground(String... params) {

        try {
            switch (this.taskType) {
                case GET_TASK:
                    jsonResult = inputStreamToString(Utils.OpenHttpConnection(params[0], true)).toString();
                break;
                case PUT_TASK:
                    jsonResult = inputStreamToString(OpenHttpConnectionCustomer(params[0], "PUT")).toString();
                break;
                case POST_TASK:
                    jsonResult = inputStreamToString(OpenHttpConnectionCustomer(params[0], "POST")).toString();
                    //OpenHttpConnectionCustomer(params[0], "POST");
                    //jsonResult = "OK";
                    break;
                case DELETE_TASK:
                    jsonResult = inputStreamToString(OpenHttpConnectionCustomer(params[0], "DELETE")).toString();
                    break;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return jsonResult;
    }

    public InputStream OpenHttpConnectionCustomer(String urlString, String method )
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
            httpConn.setRequestMethod(method);
            httpConn.setRequestProperty("Content-Type", "application/json");
            httpConn.setRequestProperty("Accept", "application/json");

            String credentials = MainActivity.consumer_key + ":" + MainActivity.consumer_secret;
            String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            httpConn.setRequestProperty("Authorization", "Basic " + base64EncodedCredentials);


            if(method.equals("PUT") || method.equals("POST")) {
                httpConn.setDoInput(true);
                httpConn.setDoOutput(true);

                Customer customer = (Customer) this.obj;
                String json_customer = Json.toJSon(customer);

                OutputStream os = httpConn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(json_customer);
                writer.flush();
                writer.close();
                os.close();
            }


            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK || response == HttpURLConnection.HTTP_CREATED) {
                in = httpConn.getInputStream();
            } else {
                System.out.println("Error code..." + response + httpConn.getResponseMessage());
            }
        }
        catch (Exception ex) {
            throw new IOException("Error connecting" + response + ex.getMessage());
        }
        return in;
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

}
