package mx.edu.itcelaya.ecommercecustomers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import mx.edu.itcelaya.ecommercecustomers.model.Address;
import mx.edu.itcelaya.ecommercecustomers.model.Customer;
import mx.edu.itcelaya.ecommercecustomers.task.AsyncResponse;
import mx.edu.itcelaya.ecommercecustomers.task.WooCommerceTask;

/**
 * Created by Radogan on 2016-12-07.
 */

public class EditPruebaActivity extends Activity implements View.OnClickListener {
    String jsonResult;
    int idCustomer;
    EditText txtNombres, txtApellidos, txtEmail;
    Button btnEnviar, btnCancelar;
    Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);


        txtNombres   = (EditText) findViewById(R.id.txtNombres);
        txtApellidos = (EditText) findViewById(R.id.txtApellidos);
        txtEmail     = (EditText) findViewById(R.id.txtEmail);
        btnEnviar    = (Button) findViewById(R.id.btnEnviar);
        btnCancelar  = (Button) findViewById(R.id.btnCancelar);
        btnEnviar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);


        Intent i = getIntent();
        idCustomer = i.getIntExtra("idCustomer", 0);
        loadCustomer();

    }

    private void loadCustomer() {
        WooCommerceTask tarea = new WooCommerceTask(this, WooCommerceTask.GET_TASK, "Cargando Cliente", new AsyncResponse() {
            @Override
            public void setResponse(String output) {
                jsonResult = output;
                loadCustomerInForm();
            }
        });

        tarea.execute(new String[] { MainActivity.url + "/" + idCustomer });

        /*try {
            jsonResult = tarea.execute(new String[] { MainActivity.url + "/" + idCustomer }).get();
        } catch (InterruptedException e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (ExecutionException e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
        }*/
        //Toast.makeText(EditCustomerActivity.this, jsonResult, Toast.LENGTH_SHORT).show();
    }

    private void loadCustomerInForm() {

        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);

            JSONObject jsonChildNode = jsonResponse.getJSONObject("customer");

            JSONObject jsonChildNodeBillingAddress = jsonChildNode.getJSONObject("billing_address");
            Address billingAddress = new Address(jsonChildNodeBillingAddress.getString("first_name"), jsonChildNodeBillingAddress.getString("last_name"));
            JSONObject jsonChildNodeShippingAddress = jsonChildNode.getJSONObject("shipping_address");
            Address shippingAddress = new Address(jsonChildNodeShippingAddress.getString("first_name"), jsonChildNodeShippingAddress.getString("last_name"));

            customer =
                    new Customer(
                            jsonChildNode.getInt("id"),
                            jsonChildNode.getString("email"),
                            jsonChildNode.getString("first_name"),
                            jsonChildNode.getString("last_name"),
                            jsonChildNode.getString("username"),
                            billingAddress,
                            shippingAddress
                    );
            //System.out.println("Nombres: " + jsonChildNode.getString("first_name"));
            //System.out.println("Apellidos: " + jsonChildNode.getString("last_name"));

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_LONG).show();

        }

        txtNombres.setText(customer.getFirst_name());
        txtApellidos.setText(customer.getLast_name());
        txtEmail.setText(customer.getEmail());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEnviar:
                saveCustomer();
                break;
            case R.id.btnCancelar:
                finish();
                break;
        }
    }

    private void saveCustomer() {
        WooCommerceTask tarea = new WooCommerceTask(this, WooCommerceTask.PUT_TASK, "Guardando Cliente...", new AsyncResponse() {
            @Override
            public void setResponse(String output) {
                jsonResult = output;
                Toast.makeText(EditPruebaActivity.this, "Datos guardados correctamente.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        customer.setFirst_name(txtNombres.getText().toString());
        customer.setLast_name(txtApellidos.getText().toString());
        customer.setEmail(txtEmail.getText().toString());
        tarea.setObject(customer);

        tarea.execute(new String[] { MainActivity.url + "/" + idCustomer });

        //String json_customer = Json.toJSon(customer);
        //Toast.makeText(EditCustomerActivity.this, json_customer, Toast.LENGTH_LONG).show();
        //System.out.println(json_customer);


    }
}
