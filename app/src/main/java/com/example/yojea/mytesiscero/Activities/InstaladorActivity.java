package com.example.yojea.mytesiscero.Activities;

import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.drawable.BitmapDrawable;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yojea.mytesiscero.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;


public class InstaladorActivity extends AppCompatActivity {

    private boolean salto=false;
    ProgressBar pbarProgreso;
    public static ImageView imageView;
    private TextView textView;
    private int numImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instalador);

        numImg=0;

        pbarProgreso  =(ProgressBar) findViewById(R.id.progressBar);
        imageView = (ImageView) findViewById(R.id.imagenvolatil);
        textView = (TextView) findViewById(R.id.cargando);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // acciones que se ejecutan tras los milisegundos
                llenardatos();
            }
        }, 20);


        //saltar
        Button boton=(Button) findViewById(R.id.buttonSalta);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InstaladorActivity.this, ListCategoriaActivity.class);
                startActivity(intent);
                finish();
                salto=true;
            }
        });



    }
    private void llenardatos(){
        int i=0;

        //llena los dos primeros barras de proceso que son de creado de tabla
        pbarProgreso.setProgress(i);
        MainActivity.sqLiteHelperBd.queryData("CREATE TABLE IF NOT EXISTS PALABRA (Id INTEGER PRIMARY KEY AUTOINCREMENT, palabra VARCHAR, frase VARCHAR, fk INTEGER, imagen BLOB)");
        i=i+4; pbarProgreso.setProgress(i);
        MainActivity.sqLiteHelperBd.queryData("CREATE TABLE IF NOT EXISTS CATEGORIA (Id INTEGER PRIMARY KEY AUTOINCREMENT, palabra VARCHAR, imagen BLOB)");

        //llenamos las categorias, dentro de categoria se llenan las palabras
        guardarCat();

        if(salto==false){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Toast.makeText(getBaseContext(), R.string.completa, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(InstaladorActivity.this, ListCategoriaActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1500);

        }
    }


    private void SetImagen(int valor)
    {
        imageView.setImageResource(valor);
    }


    private void guardarCat(){
        pbarProgreso.setProgress(0);
        pbarProgreso.setMax(22); //22 es el numero de imagenes por defecto
        ArrayList<String> arraycat= new  ArrayList<>();
        arraycat.addAll(Arrays.asList(getResources().getStringArray(R.array.array_cat)));
        try {
            int resoID;
            for(int i=0;i<arraycat.size();i++){
                resoID=getResources().getIdentifier("cat"+(i+1),"drawable",getPackageName());
                textView.setText("Carga de datos.. " +  arraycat.get(i).toString() + " OK");
                SetImagen(resoID);
                MainActivity.sqLiteHelperBd.insertarDatoCategoria(
                        arraycat.get(i).toString(),
                        ImageViewToByte(imageView)
                );
                guardarPal(""+ i);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            textView.setText("Carga de categorias.. " +  "Dato actual " + " Falló");
            Toast.makeText(InstaladorActivity.this,R.string.no_guardado_error + " " + e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarPal(String fk){
        int resoID1,resoID2, foreana;
        foreana=Integer.parseInt(fk);
        foreana++; // se incrementa uno por que el autoincrement empieza con 1

        resoID1=getResources().getIdentifier("array_pal"+(fk),"array",getPackageName());
        resoID2=getResources().getIdentifier("array_fra"+(fk),"array",getPackageName());

        ArrayList<String> arraypal= new  ArrayList<>();
        arraypal.addAll(Arrays.asList(getResources().getStringArray(resoID1)));
        ArrayList<String> arrayfra= new  ArrayList<>();
        arrayfra.addAll(Arrays.asList(getResources().getStringArray(resoID2)));

        fk=""+foreana; //aqui se inserta incrementado
        try {
            int resoID;
            for(int i=0;i<arraypal.size();i++){

                resoID=getResources().getIdentifier("pal"+(numImg+1),"drawable",getPackageName());
                numImg=numImg+1;

                textView.setText("Carga de palabra.. " +  arraypal.get(i).toString()+ " Corrécta");

                SetImagen(resoID);
                MainActivity.sqLiteHelperBd.insertarDatoPalabra(
                        arraypal.get(i).toString(),
                        arrayfra.get(i).toString(),
                        fk,
                        ImageViewToByte(imageView)
                );
                pbarProgreso.setProgress(numImg);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            textView.setText("Carga de palabras.. " +  "Dato actual " + " Falló");
            Toast.makeText(InstaladorActivity.this, "error  guardado " + " " + e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }


////////////////////////////////////////////////////////////////////////
    public static byte[] ImageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray=stream.toByteArray();
        return byteArray;
    }

}
