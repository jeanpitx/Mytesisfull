package com.example.yojea.mytesiscero.Adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yojea.mytesiscero.Activities.MainActivity;
import com.example.yojea.mytesiscero.Connections.Categorias;
import com.example.yojea.mytesiscero.Connections.Palabras;
import com.example.yojea.mytesiscero.R;

import java.util.ArrayList;


public class ImageCatAdapter extends BaseAdapter {


    private Context mContext;  // Contexto de la aplicación // SE CAMBIA CONTEXT POR ACTIVITY QUEDA MAS ADECUADO
    private int layoutMolde;
    private ArrayList<Categorias> palabraslist;

    //constructor
    public ImageCatAdapter(Activity context, int layout, ArrayList<Categorias> lista){
        this.mContext=context;
        this.layoutMolde=layout;
        this.palabraslist=lista;

    }




    @Override
    public int getCount() {
        return palabraslist.size();
    }

    @Override
    public Object getItem(int position) {
        return palabraslist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private class viewHolder{
        ImageView imageView1;
        ImageView imageView2;
        ImageView imageView3;
        ImageView imageView4;
        TextView txttexto;
    }

    // Creamos la vista ImageView para cada posición del la cuadrícula.
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        View row = view;
        viewHolder holder= new viewHolder();
        if(row==null){
            //LayoutInflater inflater = mContext.getLayoutInflater(); //antes con activity en vez de contex
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutMolde,null);
            holder.txttexto= (TextView) row.findViewById(R.id.textViewNombre);
            holder.imageView1 =(ImageView) row.findViewById(R.id.imgcatmolde1);
            holder.imageView2 =(ImageView) row.findViewById(R.id.imgcatmolde2);
            holder.imageView3 =(ImageView) row.findViewById(R.id.imgcatmolde3);
            holder.imageView4 =(ImageView) row.findViewById(R.id.imgcatmolde4);
            row.setTag(holder);
        }else {
            holder=(viewHolder) row.getTag();

        }


        Categorias palabras= palabraslist.get(position);
        holder.txttexto.setText(palabras.getPalabra());

        ArrayList<Palabras> palabraslistimg;
        palabraslistimg =new ArrayList<>();

        //obtenemos todos los datos de SQLITE para llenar las 4 primeras imagenes
        String sql;
        sql="SELECT * FROM PALABRA WHERE FK=" + palabraslist.get(position).getId();
        Cursor cursor= MainActivity.sqLiteHelperBd.obtenerdatos(sql);
        palabraslistimg.clear();

        while (cursor.moveToNext()){
            int id=cursor.getInt(0);
            String palabra= cursor.getString(1);
            String frase= cursor.getString(2);
            int fk= cursor.getInt(3);
            byte[] imagen =cursor.getBlob(4);
            palabraslistimg.add(new Palabras(id,palabra,frase,fk, imagen));
        }
        //si esta vacia, ponemos las imagenes por defecto
        if(palabraslistimg.size()==0) {
            holder.imageView1.setImageResource(R.mipmap.ic_launcher_galeria);
            holder.imageView2.setImageResource(R.mipmap.ic_launcher_galeria);
            holder.imageView3.setImageResource(R.mipmap.ic_launcher_galeria);
            holder.imageView4.setImageResource(R.mipmap.ic_launcher_galeria);
        }else {
            for (int i = 0; i < palabraslistimg.size(); i++) {
                Palabras imgpalabra = palabraslistimg.get(i);
                byte[] palabrasImg = imgpalabra.getImagen();
                Bitmap bitmap = BitmapFactory.decodeByteArray(palabrasImg, 0, palabrasImg.length);
                if (i == 0) holder.imageView1.setImageBitmap(bitmap);
                if (i == 1) holder.imageView2.setImageBitmap(bitmap);
                if (i == 2) holder.imageView3.setImageBitmap(bitmap);
                if (i == 3) holder.imageView4.setImageBitmap(bitmap);
            }
        }

        return row;



    }


}
