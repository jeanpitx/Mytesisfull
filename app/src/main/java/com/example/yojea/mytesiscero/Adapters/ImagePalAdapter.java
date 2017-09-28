package com.example.yojea.mytesiscero.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yojea.mytesiscero.Connections.Palabras;
import com.example.yojea.mytesiscero.R;

import java.util.ArrayList;


public class ImagePalAdapter extends BaseAdapter implements Filterable{


    private Context mContext;  // Contexto de la aplicación // SE CAMBIA CONTEXT POR ACTIVITY QUEDA MAS ADECUADO
    private int layoutMolde;
    private ArrayList<Palabras> palabraslist;
    private ArrayList<Palabras> filterList;
    CustomFilter filter;
    //private Integer[] mImg;

    //constructor
    public ImagePalAdapter(Activity context, int layout, ArrayList<Palabras> lista){
        this.mContext=context;
        this.layoutMolde=layout;
        this.palabraslist=lista;
        this.filterList=lista;

    }

    protected String eventoseleccionado(int posicion) {
        String results;
        results=null;
        if (posicion>=0){
            //filtramos
            for(int i=0;i<palabraslist.size();i++){
                if(palabraslist.get(i).getPalabra().toUpperCase().equals("obtener texto que se muestra")){
                    results=palabraslist.get(i).getFrase();
                }
            }
        }else{
            results="no_existe";
        }

        return results;
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
        return palabraslist.indexOf(getItem(position));
    }



    private class viewHolder{
        ImageView imageView;
        TextView txttexto;
    }

    // Creamos la vista ImageView para cada posición del la cuadrícula.
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        View row = view;
        viewHolder holder= new viewHolder();
        if(row==null){
            //LayoutInflater inflater = mContext.getLayoutInflater(); antes con activity en vez de contex
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutMolde,null);
            holder.txttexto= (TextView) row.findViewById(R.id.textViewNombre);
            holder.imageView =(ImageView) row.findViewById(R.id.imageViewImg);
            row.setTag(holder);
        }else {
            holder=(viewHolder) row.getTag();

        }
        Palabras palabras= palabraslist.get(position);
        holder.txttexto.setText(palabras.getPalabra());
        byte[]  palabrasImg= palabras.getImagen();
        Bitmap bitmap= BitmapFactory.decodeByteArray(palabrasImg,0,palabrasImg.length);
        holder.imageView.setImageBitmap(bitmap);
        return row;


    }


    @Override
    public Filter getFilter() {

        if(filter==null)
        {
            filter= new CustomFilter();
        }

        return filter;
    }


    //Inser Class
    class CustomFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results= new FilterResults();

            if (constraint!= null && constraint.length()>0){
                constraint=constraint.toString().toUpperCase();
                ArrayList<Palabras> filters=new ArrayList<Palabras>();

                //filtramos
                for(int i=0;i<filterList.size();i++){
                    if(filterList.get(i).getPalabra().toUpperCase().contains(constraint)){
                        Palabras p= new Palabras(filterList.get(i).getId(),
                                filterList.get(i).getPalabra(),
                                filterList.get(i).getFrase(),
                                filterList.get(i).getFk(),
                                filterList.get(i).getImagen()
                        );
                        filters.add(p);
                    }
                }
                results.count=filters.size();
                results.values=filters;
            }else{
                results.count=filterList.size();
                results.values=filterList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            palabraslist=(ArrayList<Palabras>) results.values;
            notifyDataSetChanged();

        }
    }
}
