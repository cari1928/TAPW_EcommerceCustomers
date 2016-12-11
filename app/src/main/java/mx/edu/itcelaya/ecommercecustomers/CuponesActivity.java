package mx.edu.itcelaya.ecommercecustomers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mx.edu.itcelaya.ecommercecustomers.model.Address;
import mx.edu.itcelaya.ecommercecustomers.model.Customer;
import mx.edu.itcelaya.ecommercecustomers.task.AsyncResponse;
import mx.edu.itcelaya.ecommercecustomers.task.WooCommerceTask;

public class CuponesActivity extends AppCompatActivity {

    String jsonResult;
    String email;
    public static String url = "https://tapw-woocomerce-customers-cari1928.c9users.io/wc-api/v3/coupons";
    public static String consumer_key    = "ck_a645f61ead6c17186e280ae58d547031078b345b";
    public static String consumer_secret = "cs_8097a58db4fed33c44437a1296963663398b711d";
    List<Cupones> items   = new ArrayList<Cupones>();
    CuponesAdapter cAdapter;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cupones);

        Intent i = getIntent();
        email = i.getStringExtra("email").toLowerCase();
        //Toast.makeText(getApplicationContext(), "Email" + email, Toast.LENGTH_LONG).show();

        list = (ListView) findViewById(R.id.listCupones);
        registerForContextMenu(list);

        loadCupones();
    }

    public void loadCupones() {
        WooCommerceTask tarea = new WooCommerceTask(this, WooCommerceTask.GET_TASK, "Cargando Clientes...", new AsyncResponse() {
            @Override
            public void setResponse(String output) {
                jsonResult = output;
                ListCupones();
            }
        });
        tarea.execute(new String[] { url });
    }

    public void ListCupones(){
        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("coupons");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                Integer id = jsonChildNode.optInt("id");
                String code = jsonChildNode.optString("code");
                String expiry_date = jsonChildNode.optString("expiry_date");

                JSONArray jsonEmails = jsonChildNode.getJSONArray("customer_emails");
                String emails = jsonEmails + "";

                Boolean flag = false;
                for (int j = 0; j < jsonEmails.length(); j++){
                    //Toast.makeText(getApplicationContext(), "Email: " + email, Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), "Emails: " + jsonEmails.optString(j), Toast.LENGTH_LONG).show();
                    if(email.equalsIgnoreCase(jsonEmails.optString(j))) { //si los correos son iguales
                        flag = true;
                        break;
                    }
                }

                if(flag) {
                    items.add(new Cupones(
                            id,
                            code,
                            expiry_date,
                            emails
                    ));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error" + e.toString(), Toast.LENGTH_LONG).show();
        }

        cAdapter = new CuponesAdapter(this, items);
        list.setAdapter(cAdapter);
    }
}
