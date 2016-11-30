package mx.edu.itcelaya.ecommercecustomers;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mx.edu.itcelaya.ecommercecustomers.model.Customer;
import mx.edu.itcelaya.ecommercecustomers.model.Order;
import mx.edu.itcelaya.ecommercecustomers.model.Product;
import mx.edu.itcelaya.ecommercecustomers.utils.Utils;

/**
 * Created by niluxer on 5/20/16.
 */

public class OrderAdapter extends BaseAdapter {
    private Context context;
    private List<Order> orders;

    public OrderAdapter(Context context, List<Order> orders) {
        super();
        this.context = context;
        this.orders = orders;
    }

    @Override
    public int getCount() {
        return this.orders.size();
    }

    @Override
    public Object getItem(int i) {
        return this.orders.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View rowView = view;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_orders, null);
        }

        TextView tvOrderNumber   = (TextView) rowView.findViewById(R.id.tvOrderNumber);
        TextView tvStatus        = (TextView) rowView.findViewById(R.id.tvStatus);
        //TextView tvProductName        = (TextView) rowView.findViewById(R.id.tvProductName);
        //TextView tvProductPrice        = (TextView) rowView.findViewById(R.id.tvProductPrice);
        ListView lvProducts = (ListView) rowView.findViewById(R.id.lvProducts);

        final Order item = this.orders.get(position);
        tvOrderNumber.setText(item.getOrder_number() + "" );
        tvStatus.setText(item.getStatus());
        List<Product> pList = item.getLine_items();

        final ArrayList<String> list = new ArrayList<String>();
        for (Iterator<Product> iter = pList.iterator(); iter.hasNext(); ) {
            Product element = iter.next();
            list.add(element.getName());
        }
        final ArrayAdapter adapter = new ArrayAdapter(context,
                android.R.layout.simple_list_item_1, list);
        lvProducts.setAdapter(adapter);
        //tvProductName.setText();
        rowView.setTag(item.getId());
        return rowView;
    }
}
