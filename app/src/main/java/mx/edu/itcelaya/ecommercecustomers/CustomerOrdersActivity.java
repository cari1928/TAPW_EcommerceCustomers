package mx.edu.itcelaya.ecommercecustomers;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mx.edu.itcelaya.ecommercecustomers.model.Customer;
import mx.edu.itcelaya.ecommercecustomers.model.Order;
import mx.edu.itcelaya.ecommercecustomers.model.Product;
import mx.edu.itcelaya.ecommercecustomers.task.AsyncResponse;
import mx.edu.itcelaya.ecommercecustomers.task.WooCommerceTask;
import mx.edu.itcelaya.ecommercecustomers.utils.NukeSSLCerts;

public class CustomerOrdersActivity extends ListActivity {

    ListView listOrders;
    //String url = "https://192.168.56.1/~niluxer/wordpress/wc-api/v3/customers/";
    String url = "https://10.247.67.17/store_itc/wc-api/v3/customers";
    String jsonResult;
    List<Order> items   = new ArrayList<Order>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_customer_orders);
        listOrders = getListView();
        Intent i = getIntent();
        String idCustomer = i.getStringExtra("id_customer");
        //url.replaceAll("id_customer", idCustomer);
        url = url + idCustomer + "/orders";
        //System.out.println(url);
        //Toast.makeText(this, url, Toast.LENGTH_LONG).show();
        NukeSSLCerts.nuke();
        loadOrders();
    }

    private void loadOrders() {
        WooCommerceTask tarea = new WooCommerceTask(this, WooCommerceTask.GET_TASK, "Cargando Ordenes...", new AsyncResponse() {
            @Override
            public void setResponse(String output) {
                jsonResult = output;
                ListOrders();
            }
        });
        tarea.execute(new String[] { url });

    }

    public void ListOrders() {


        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("orders");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);


                JSONArray jsonProductsNode = jsonChildNode.optJSONArray("line_items");

                String sProducts = "";
                List<Product> products   = new ArrayList<Product>();
                for (int x = 0; x < jsonProductsNode.length(); x++) {
                    JSONObject jsonChildNodeProduct = jsonProductsNode.getJSONObject(x);
                    //sProducts += jsonChildNodeProduct.optString("product_id") + ",";
                    products.add(new Product(
                            jsonChildNodeProduct.optInt("product_id"),
                            jsonChildNodeProduct.optString("name"),
                            jsonChildNodeProduct.optDouble("price"),
                            jsonChildNodeProduct.optInt("quantity")
                    ));
                }

                items.add(
                        new Order(
                                jsonChildNode.optInt("id"),
                                jsonChildNode.optInt("order_number"),
                                jsonChildNode.optString("status"),
                                jsonChildNode.optDouble("total"),
                                products
                        ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_LONG).show();

        }

        listOrders.setAdapter(new OrderAdapter(this, items));
    }

}
