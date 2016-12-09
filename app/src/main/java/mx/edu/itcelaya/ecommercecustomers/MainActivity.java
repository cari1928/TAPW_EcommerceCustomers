package mx.edu.itcelaya.ecommercecustomers;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mx.edu.itcelaya.ecommercecustomers.model.Address;
import mx.edu.itcelaya.ecommercecustomers.model.Customer;
import mx.edu.itcelaya.ecommercecustomers.task.AsyncResponse;
import mx.edu.itcelaya.ecommercecustomers.task.LoginTask;
import mx.edu.itcelaya.ecommercecustomers.task.WooCommerceTask;
import mx.edu.itcelaya.ecommercecustomers.utils.NukeSSLCerts;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ListView list;
    List<Customer> items   = new ArrayList<Customer>();
    public static String consumer_key    = "ck_8610d1b7c089c88b439f3d8102d56ad1ef23b12f";
    public static String consumer_secret = "cs_659b8deee047824dff82defb6354d47823b01fdb";
    public static String url = "https://tapw-proyecto-c3-cari1928.c9users.io/wc-api/v3/customers";
    String auth_url = "https://tapw-proyecto-c3-cari1928.c9users.io/auth_users.php";
    String jsonResult, loginResult;
    Dialog dLogin;
    CustomerAdapter cAdapter;

    Button btnAceptar, btnCancelar;
    EditText txtUsername, txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NukeSSLCerts.nuke();

        Toast.makeText(MainActivity.this, "Bienvenido", Toast.LENGTH_LONG).show();

        mostrarLogin();
        list = (ListView) findViewById(R.id.listCustomers);
        list.setOnItemClickListener(listenerOrdenes);
        registerForContextMenu(list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Boolean bandera = true;

        switch(id) {
            case R.id.mnuNew:
                newCustomer();
                break;

            case R.id.mnuGrafica1:
                loadSales();
                break;

            default:
                bandera = super.onOptionsItemSelected(item);
        }
        return bandera;
    }

    public void loadSales() {
        String url_sales = "https://tapw-proyecto-c3-cari1928.c9users.io/wc-api/v3/reports/sales?filter[period]=week";

        WooCommerceTask tarea = new WooCommerceTask(this, WooCommerceTask.GET_TASK, "Cargando Reporte...", new AsyncResponse() {
            @Override
            public void setResponse(String output) {
                jsonResult = output;

                Toast.makeText(MainActivity.this, jsonResult, Toast.LENGTH_LONG).show();

                Intent intent_grafica = new Intent(MainActivity.this, Grafica1Activity.class);
                intent_grafica.putExtra("json", jsonResult);
                startActivity(intent_grafica);
            }
        });

        tarea.execute(new String[] { url_sales });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.listCustomers) {
            menu.setHeaderTitle("Opciones");
            MenuInflater inflater=getMenuInflater();
            inflater.inflate(R.menu.customer_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //return super.onContextItemSelected(item);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Adapter adapter = list.getAdapter();
        //Object obj  = adapter.getItem(info.position);
        Customer customer  = (Customer) adapter.getItem(info.position);


        switch (item.getItemId()) {
            case R.id.mnuEdit:
                //Toast.makeText(MainActivity.this, "Edit" + customer.getLast_name(), Toast.LENGTH_SHORT).show();
                editCustomer(customer.getId());

                break;
            case R.id.mnuDelete:
                //Toast.makeText(MainActivity.this, "Delete" + customer.getLast_name(), Toast.LENGTH_SHORT).show();
                deleteCustomer(customer.getId());

                break;
        }
        return true;
    }

    private void mostrarLogin() {
        dLogin = new Dialog(this);
        dLogin.setTitle("Login");
        dLogin.setContentView(R.layout.login);

        txtUsername = (EditText) dLogin.findViewById(R.id.txtUsername);
        txtPassword = (EditText) dLogin.findViewById(R.id.txtPassword);
        btnAceptar = (Button) dLogin.findViewById(R.id.btnAceptar);
        btnCancelar = (Button) dLogin.findViewById(R.id.btnCancelar);
        btnAceptar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
        dLogin.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAceptar:
                validaAcceso();
                break;
            case R.id.btnCancelar:
                break;
        }
    }

    public void loadCustomers() {
        WooCommerceTask tarea = new WooCommerceTask(this, WooCommerceTask.GET_TASK, "Cargando Clientes...", new AsyncResponse() {
            @Override
            public void setResponse(String output) {
                jsonResult = output;
                ListCustomers();
            }
        });
        tarea.execute(new String[] { url });

    }

    public void ListCustomers() {

        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("customers");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                JSONObject jsonChildNodeBillingAddress = jsonChildNode.getJSONObject("billing_address");
                Address billingAddress = new Address(jsonChildNodeBillingAddress.getString("first_name"), jsonChildNodeBillingAddress.getString("last_name"));
                JSONObject jsonChildNodeShippingAddress = jsonChildNode.getJSONObject("shipping_address");
                Address shippingAddress = new Address(jsonChildNodeShippingAddress.getString("first_name"), jsonChildNodeShippingAddress.getString("last_name"));

                items.add(
                        new Customer(
                                jsonChildNode.optInt("id"),
                                jsonChildNode.optString("email"),
                                jsonChildNode.optString("first_name"),
                                jsonChildNode.optString("last_name"),
                                jsonChildNode.optString("username"),
                                billingAddress,
                                shippingAddress
                        ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_LONG).show();

        }

        cAdapter = new CustomerAdapter(this, items);

        list.setAdapter(cAdapter);
    }

    AdapterView.OnItemClickListener listenerOrdenes = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //Toast.makeText(MainActivity.this, view.getTag() + "", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, CustomerOrdersActivity.class);
            intent.putExtra("id_customer", view.getTag().toString());
            startActivity(intent);
        }
    };



    private void validaAcceso () {
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        TextView tvNombre = (TextView) findViewById(R.id.user);

        LoginTask tarea = new LoginTask(this);
        tarea.setUsername(username);
        tarea.setPassword(password);
        try {
            loginResult = tarea.execute(new String[] { auth_url }).get();
        } catch (InterruptedException e) {
            //e.printStackTrace();
            System.out.println("Error..." + e.getMessage());
        } catch (ExecutionException e) {
            //e.printStackTrace();
            System.out.println("Error..." + e.getMessage());
        }

        Toast.makeText(MainActivity.this, loginResult, Toast.LENGTH_SHORT).show();

        //creación de web service propio
        try {
            JSONObject jso = new JSONObject(loginResult);
            JSONArray jsonMainNode = jso.optJSONArray("auth");

            for (int i = 0; i < jsonMainNode.length(); i++) {

                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                Boolean valido = jsonChildNode.optBoolean("valido");
                String rol = jsonChildNode.optString("rol");
                String nombre_completo = jsonChildNode.optString("nombre_completo");

                if (valido == true && rol.equals("administrator")) {
                    dLogin.dismiss();
                    loadCustomers();
                    tvNombre.setText(nombre_completo);
                } else {
                    Toast.makeText(this, "" + "Usuario y/o contrase;a no validos", Toast.LENGTH_LONG).show();
                }

            }
        } catch (JSONException e) {
            //e.printStackTrace();
            System.out.println("Errors:" + e.getMessage());
        }


    }

    private void deleteCustomer(final int idCustomer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar");
        builder.setMessage("¿Deseas eliminar el registro seleccionado?");
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                WooCommerceTask tarea = new WooCommerceTask(MainActivity.this, WooCommerceTask.DELETE_TASK, "Eliminando Cliente", new AsyncResponse() {
                    @Override
                    public void setResponse(String output) {
                        jsonResult = output;
                        Toast.makeText(MainActivity.this, "Cliente eliminado correctamente.", Toast.LENGTH_SHORT).show();
                        onRestart();
                    }
                });
                tarea.execute(new String[] { MainActivity.url + "/" + idCustomer });

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        Dialog d = builder.create();
        d.show();


    }

    private void editCustomer(int idCustomer) {
        Intent i = new Intent(this, EditCustomerActivity.class);
        i.putExtra("idCustomer", idCustomer);
        startActivity(i);

    }

    private void newCustomer() {
        Intent i = new Intent(this, NewCustomerActivity.class);
        startActivity(i);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        list.setAdapter(null);
        cAdapter.customers.clear();
        //cAdapter.notifyDataSetChanged();
        loadCustomers();
    }
}
