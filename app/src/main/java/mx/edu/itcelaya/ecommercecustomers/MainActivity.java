package mx.edu.itcelaya.ecommercecustomers;

import android.app.Dialog;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    List<Cupones> rItems = new ArrayList<Cupones>();

    public static String consumer_key    = "ck_a645f61ead6c17186e280ae58d547031078b345b";
    public static String consumer_secret = "cs_8097a58db4fed33c44437a1296963663398b711d";
    public static String url = "https://tapw-woocomerce-customers-cari1928.c9users.io/wc-api/v3/customers";
    String auth_url = "https://tapw-woocomerce-customers-cari1928.c9users.io/auth_users.php";

    String jsonResult, loginResult;
    Dialog dLogin;
    CustomerAdapter cAdapter;
    Button btnAceptar, btnCancelar;
    EditText txtUsername, txtPassword;
    Button btnRegresa;
    android.app.AlertDialog dialogFoto;

    //------------Menús Customer y Administrator------------
    Menu menu;
    public static String role = "";
    private boolean isChangedStat = false;

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
        this.menu = menu;;
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Boolean bandera = true;

        if(role.equals("administrator")) {
            switch (id) {
                case 1: //categorías
                    //constructor(ventana de donde viene, ventana a donde va)
                    Intent in = new Intent(MainActivity.this, segunda_ventanaActivity.class);
                    startActivity(in);
                    break;
                case 2:
                    newCustomer();
                    break;
                case 6:
                    loadSales();
                    break;
                default:
                    bandera = super.onOptionsItemSelected(item);
            }
        } else if(role.equals("customer")) {
            switch (id) {
                case 1: //cupones
                    //constructor(ventana de donde viene, ventana a donde va)
                    Intent in = new Intent(MainActivity.this, CuponesActivity.class);
                    in.putExtra("email", items.get(0).getEmail());
                    startActivity(in);
                    break;
                case 2: //gráfica
                    loadSales();
                    break;
                default:
                    bandera = super.onOptionsItemSelected(item);
            }
        } else {
            bandera = super.onOptionsItemSelected(item);
        }

        return bandera;
    }

    public void loadSales() {
        //String url_sales = "https://tapw-woocomerce-customers-cari1928.c9users.io/wc-api/v3/reports/sales?filter[period]=week";
        String url_sales = "https://tapw-woocomerce-customers-cari1928.c9users.io/wc-api/v3/customers/"+items.get(0).getId()+"/orders";

        WooCommerceTask tarea = new WooCommerceTask(this, WooCommerceTask.GET_TASK, "Cargando Reporte...", new AsyncResponse() {
            @Override
            public void setResponse(String output) {
                jsonResult = output;

                //Toast.makeText(MainActivity.this, jsonResult, Toast.LENGTH_LONG).show();

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

        if(view == btnRegresa) {
            dialogFoto.dismiss();
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
            JSONObject jso = new JSONObject(loginResult);
            JSONArray jsonMainNode = jso.optJSONArray("auth");

            for (int i = 0; i < jsonMainNode.length(); i++) {

                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                Boolean valido = jsonChildNode.optBoolean("valido");
                String rol = jsonChildNode.optString("rol");
                String nombre_completo = jsonChildNode.optString("nombre_completo");
                Integer id = jsonChildNode.optInt("id");
                String user_email = jsonChildNode.optString("email");

                if (valido == true && rol.equals("administrator")) {
                    role = rol;
                    isChangedStat = false;
                    Toast.makeText(this, "Hola Administrador :)", Toast.LENGTH_LONG).show();

                } else if(valido == true && rol.equals("customer")) {
                    dLogin.dismiss();

                    items.add(new Customer(
                            id,
                            user_email,
                            nombre_completo,
                            rol
                    ));

                    role = rol;
                    isChangedStat = true;

                    cAdapter = new CustomerAdapter(this, items);
                    list.setAdapter(cAdapter);
                } else {
                    Toast.makeText(this, "" + "Usuario y/o contrase;a no validos", Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            //e.printStackTrace();
            System.out.println("Errors:" + e.getMessage());
        }
    }

    AdapterView.OnItemClickListener listenerOrdenes = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            rItems = new ArrayList<>();
            Customer customer = items.get(i); //posición
            //Toast.makeText(getBaseContext(), "Nombre " + customer.getFull_name(), Toast.LENGTH_LONG).show();
            loadCupones();
        }
    };

    public void loadCupones() {
        url = "https://tapw-woocomerce-customers-cari1928.c9users.io/wc-api/v3/coupons";
        LoadCuponesTask tarea = new LoadCuponesTask(this, consumer_key, consumer_secret);
        try {
            jsonResult = tarea.execute(new String[] { url }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //Toast.makeText(getBaseContext(), jsonResult, Toast.LENGTH_LONG).show();

        ListCupones();
    }
    
    public void ListCupones() {
        try {
            String email = items.get(0).getEmail();
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
                    rItems.add(new Cupones(
                            id,
                            code,
                            expiry_date,
                            emails
                    ));
                }
            }

            //para mostrar los reviews
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this); //recibe el contexto de la app
            LinearLayout layout1 = new LinearLayout(this); //para colocar en él los elementos
            layout1.setOrientation(LinearLayout.VERTICAL);

            //nuevo listview en conjunto con un arrayadapter
            ListView vReviews = new ListView(this);
            vReviews.setAdapter(new CuponesAdapter(this, rItems));

            //boton
            btnRegresa = new Button(this);
            btnRegresa.setText("Cerrar");
            btnRegresa.setOnClickListener(this);

            //se pasan los elementos al layout
            layout1.addView(vReviews);
            layout1.addView(btnRegresa);

            builder.setView(layout1); //se le pasa el layout a builder
            dialogFoto = builder.create(); //se termina de crear el dialogo
            dialogFoto.show(); //se muestra el dialogo

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error" + e.toString(), Toast.LENGTH_LONG).show();

        }
    }

    private void validaAcceso () {
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();

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

        //Toast.makeText(MainActivity.this, loginResult, Toast.LENGTH_SHORT).show();

        ListCustomers();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if(isChangedStat) { //customer
            menu.add(0, 1, 0, "Cupones");
            menu.add(0, 2, 0, "Gráfica Órdenes");
            //menu.add(0, 3, 0, "Pedido");
        } else { //administrator
            menu.add(0, 1, 0, "Categorías");
            menu.add(0, 2, 0, "Nuevo Cliente");
            menu.add(0, 3, 0, "Nuevo Cupón");
            menu.add(0, 4, 0, "Nuevo Pedido");
            menu.add(0, 5, 0, "Productos");
            menu.add(0, 6, 0, "Reporte de Ventas");
        }
        return super.onPrepareOptionsMenu(menu);
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
        //loadCustomers();
        ListCustomers();
    }


}
