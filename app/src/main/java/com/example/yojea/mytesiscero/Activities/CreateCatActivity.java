package com.example.yojea.mytesiscero.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.yojea.mytesiscero.Connections.Categorias;
import com.example.yojea.mytesiscero.Connections.SQLiteHelper;
import com.example.yojea.mytesiscero.R;

import java.io.ByteArrayOutputStream;

public class CreateCatActivity extends AppCompatActivity {

    //variables locales
    private EditText edtPalabra;
    private Button btnGuardar, btnVolver, btnImagen;
    public ImageView imageView;
    private String estImg; // estado de la imagen para ver si se carga o no
    final int REQUEST_CODE_GALLERY=999;
    public static SQLiteHelper sqLiteHelper;
    ListCategoriaActivity categoria;
    private  String txtActi1cod1;
    private Bitmap txtActi12cod2;

    @Override
    public void onBackPressed() {
        finish();
        actualizargrid();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_cat);

        //se carga la base de datos principal desde el activity inicial
        MainActivity recurso = new MainActivity();
        sqLiteHelper=recurso.sqLiteHelperBd;

        estImg="negativo";
        init();

        //OBTENEMOS LA IMAGEN Y TEXTO DE LA GALERIA ABIERTA
        Intent intent=getIntent();
        Bundle extras =intent.getExtras();
        if (extras != null) {//ver si contiene datos
            imageView.setImageBitmap(GaleriaActivity.bitmap);
            edtPalabra.setText(extras.getString("palabra"));
            estImg="positivo";
        }

        //BOTON VOLVER
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                actualizargrid();
            }
        });

        //BOTON IMAGEN CARGAR
        btnImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GaleriaActivity.requerido="categoria";

                Intent explicit_intent = new Intent(CreateCatActivity.this, GaleriaActivity.class);
                explicit_intent.putExtra("txt1",edtPalabra.getText().toString());

                startActivity(explicit_intent);
                finish();
            }
        });

        //BOTON GUARDAR
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String palabra= edtPalabra.getText().toString();
                    if(!TextUtils.isEmpty(palabra) && !estImg.equals("negativo")){
                        sqLiteHelper.insertarDatoCategoria(
                                edtPalabra.getText().toString().trim(),
                                ImageViewToByte(imageView)
                        );
                        Toast.makeText(CreateCatActivity.this,R.string.guardado_exito,Toast.LENGTH_SHORT).show();
                        edtPalabra.setText("");
                        imageView.setImageResource(R.mipmap.ic_launcher_galeria);
                    }
                    else{
                        Toast.makeText(CreateCatActivity.this,R.string.no_guardado_vacio,Toast.LENGTH_SHORT).show();
                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(CreateCatActivity.this,R.string.no_guardado_error + " " + e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public static byte[] ImageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray=stream.toByteArray();
        return byteArray;
    }


    public void actualizargrid(){
        //obtenemos todos los datos de SQLITE
        String sql;
        sql="SELECT * FROM CATEGORIA";
        Cursor cursor= MainActivity.sqLiteHelperBd.obtenerdatos(sql);
        ListCategoriaActivity.list.clear();
        while (cursor.moveToNext()){
            int id=cursor.getInt(0);
            String palabra= cursor.getString(1);
            byte[] imagen =cursor.getBlob(2);

            ListCategoriaActivity.list.add(new Categorias(id,palabra,imagen));
        }
        ListCategoriaActivity.adapter.notifyDataSetChanged();
    }


    private void init(){
        edtPalabra=(EditText) findViewById(R.id.edtPalabraCat);
        btnGuardar=(Button) findViewById(R.id.btnGuardarCat);
        btnVolver=(Button) findViewById(R.id.btnVolverCat);
        btnImagen=(Button) findViewById(R.id.btnImagenCat);
        imageView=(ImageView) findViewById(R.id.imageViewCreateCat);
    }
}
