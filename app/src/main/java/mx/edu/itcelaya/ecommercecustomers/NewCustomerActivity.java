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

import java.util.concurrent.ExecutionException;

import mx.edu.itcelaya.ecommercecustomers.model.Address;
import mx.edu.itcelaya.ecommercecustomers.model.Customer;
import mx.edu.itcelaya.ecommercecustomers.task.AsyncResponse;
import mx.edu.itcelaya.ecommercecustomers.task.WooCommerceTask;
import mx.edu.itcelaya.ecommercecustomers.utils.Json;
import mx.edu.itcelaya.ecommercecustomers.utils.NukeSSLCerts;

public class NewCustomerActivity extends Activity implements View.OnClickListener {

    String jsonResult;
    int idCustomer;
    EditText txtNombres, txtApellidos, txtEmail, txtUsuario;
    Button btnEnviar, btnCancelar;
    Customer customer = new Customer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_customer);

        txtNombres   = (EditText) findViewById(R.id .txtNombres);
        txtApellidos = (EditText) findViewById(R.id.txtApellidos);
        txtEmail     = (EditText) findViewById(R.id.txtEmail);
        txtUsuario   = (EditText) findViewById(R.id.txtUsuario);
        btnEnviar    = (Button) findViewById(R.id.btnEnviar);
        btnCancelar  = (Button) findViewById(R.id.btnCancelar);
        btnEnviar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);

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
        WooCommerceTask tarea = new WooCommerceTask(this, WooCommerceTask.POST_TASK, "Guardando Cliente...", new AsyncResponse() {
            @Override
            public void setResponse(String output) {
                jsonResult = output;
                Toast.makeText(NewCustomerActivity.this, "Datos guardados correctamente.", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        customer.setFirst_name(txtNombres.getText().toString());
        customer.setLast_name(txtApellidos.getText().toString());
        customer.setEmail(txtEmail.getText().toString());
        customer.setUsername(txtUsuario.getText().toString());
        customer.setBilling_address(new Address(txtNombres.getText().toString(), txtApellidos.getText().toString()));
        customer.setShipping_address(new Address(txtNombres.getText().toString(), txtApellidos.getText().toString()));

        tarea.setObject(customer);

        tarea.execute(new String[] { MainActivity.url });

    }

}
