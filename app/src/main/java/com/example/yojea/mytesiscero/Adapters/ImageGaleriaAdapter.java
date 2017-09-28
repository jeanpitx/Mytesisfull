package com.example.yojea.mytesiscero.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yojea.mytesiscero.Connections.Categorias;
import com.example.yojea.mytesiscero.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yojea on 19/9/2017.
 */

public class ImageGaleriaAdapter extends BaseAdapter {

    private Context mContext;// Contexto de la aplicación // SE CAMBIA CONTEXT POR ACTIVITY QUEDA MAS ADECUADO
    private int layoutMolde;
    private ArrayList<String> galerylist;
    private Integer[] imagenes;

    public ImageGaleriaAdapter(Activity context, int layout, ArrayList<String> lista, Integer[] listaimg){
        this.mContext=context;
        this.layoutMolde=layout;
        this.galerylist=lista;
        this.imagenes=listaimg;

    }

    @Override
    public int getCount() {
        return galerylist.size();
    }

    @Override
    public Object getItem(int position) {
        return galerylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private class viewHolder{
        ImageView imageView;
        TextView txttexto;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //LayoutInflater inflater = mContext.getLayoutInflater();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(layoutMolde, null);
        TextView textView = (TextView) convertView.findViewById(R.id.textViewNombre);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewImg);
        textView.setText(galerylist.get(position));
        imageView.setImageResource(imagenes[position]);
        return convertView;
    }
}
