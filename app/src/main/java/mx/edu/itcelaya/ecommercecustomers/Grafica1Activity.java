package mx.edu.itcelaya.ecommercecustomers;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import mx.edu.itcelaya.ecommercecustomers.task.AsyncResponse;
import mx.edu.itcelaya.ecommercecustomers.task.WooCommerceTask;

/**
 * Created by Radogan on 2016-12-05.
 */

public class Grafica1Activity extends AppCompatActivity {
    public static String url = "https://tapw-proyecto-c3-cari1928.c9users.io/wc-api/v3/reports/sales?filter[date_min]=2016-11-23&filter[date_max]=2016-12-08";
    BarChart b1;
    BarDataSet dataset;
    BarData data;
    String jsonResult;
    ArrayList<BarEntry> datos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafica1);

        b1 = (BarChart) findViewById(R.id.bar1);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonResult = extras.getString("json");
            //Toast.makeText(this, jsonResult, Toast.LENGTH_SHORT).show();
            procesaJson();
        }
    }

    public void loadSales() {
        WooCommerceTask tarea = new WooCommerceTask(this, WooCommerceTask.GET_TASK, "Cargando Reporte...", new AsyncResponse() {
            @Override
            public void setResponse(String output) {
                jsonResult = output;
                procesaJson();
                //Toast.makeText(Grafica1Activity.this, jsonResult, Toast.LENGTH_LONG).show();
            }
        });

        tarea.execute(new String[] { url });

    }

    public void procesaJson() {

        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            //JSONArray jsonMainNode = jsonResponse.optJSONArray("sales");
            JSONObject jsonChildNode = jsonResponse.getJSONObject("sales");

            //System.out.println("total sales: " + jsonChildNode.getString("total_sales"));
            //System.out.println("totals: " + jsonChildNode.getJSONObject("totals"));

            JSONObject jsonTotalsNode = jsonChildNode.getJSONObject("totals");

            Iterator<String> itr = jsonTotalsNode.keys();

            int cont = 0;
            while(itr.hasNext()) {

                Object element = itr.next();
                String fecha = element.toString();
                JSONObject jsonTotalsNodeObj = jsonTotalsNode.getJSONObject(fecha);
                String sales = jsonTotalsNodeObj.getString("sales");
                System.out.println(fecha + "->" + sales + " ");
                //if (Float.valueOf(sales) > 0.0) {
                datos.add(new BarEntry(cont, Float.valueOf(sales)));
                cont++;
                //}
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_LONG).show();

        }

        generaGrafica();

    }

    private void generaGrafica() {

        BarEntry v1 = datos.get(0);
        System.out.println("Valor 1" + v1.toString() + v1.getY());

        BarDataSet dataset = new BarDataSet(datos, "Total Ventas por dia");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData data = new BarData(dataset);
        //b1 = (BarChart) findViewById(R.id.bar1);
        b1.setData(data);
        System.out.println("Ends...");

    }
}
