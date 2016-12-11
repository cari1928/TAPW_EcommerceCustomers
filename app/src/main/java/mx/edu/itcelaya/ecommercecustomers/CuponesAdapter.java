package mx.edu.itcelaya.ecommercecustomers;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import mx.edu.itcelaya.ecommercecustomers.model.Customer;
import mx.edu.itcelaya.ecommercecustomers.utils.Utils;

/**
 * Created by Radogan on 2016-12-09.
 */

public class CuponesAdapter extends BaseAdapter {
    private Context context;
    public List<Cupones> cupones;

    public CuponesAdapter(Context context, List<Cupones> cupones) {
        this.context = context;
        this.cupones = cupones;
    }

    @Override
    public int getCount() {
        return this.cupones.size();
    }

    @Override
    public Object getItem(int i) {
        return this.cupones.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_cupones, null);
        }

        TextView tvNombre   = (TextView) rowView.findViewById(R.id.tvNombre);
        TextView tvDate = (TextView) rowView.findViewById(R.id.tvDate);

        final Cupones item = this.cupones.get(i);
        tvNombre.setText(item.getCode());
        tvDate.setText(item.getExpiry_date());
        rowView.setTag(item.getId());

        return rowView;
    }

    private class BackgroundTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... url) {
            //---download an image---
            Bitmap bitmap = Utils.DownloadImage(url[0]);
            return bitmap;
        }
    }
}
