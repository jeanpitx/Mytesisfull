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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yojea.mytesiscero.Connections.Categorias;
import com.example.yojea.mytesiscero.Connections.Palabras;
import com.example.yojea.mytesiscero.Connections.SQLiteHelper;
import com.example.yojea.mytesiscero.R;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class CreateWordActivity extends AppCompatActivity {

    //variables locales
    private EditText edtPalabra, edtFrase;
    private Button btnGuardar, btnVolver, btnImagen;
    private ImageView imageView;
    private String estImg; // estado de la imagen para ver si se carga o no
    private int foreign;
    final int REQUEST_CODE_GALLERY=999;
    public static SQLiteHelper sqLiteHelper;

    @Override
    public void onBackPressed() {
        finish();
        actualizargrid();
        actualizargrid_cat();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_word);

        foreign=ListCategoriaActivity.foreign;

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
            edtFrase.setText(extras.getString("frase"));
            estImg="positivo";
        }


        //BOTON VOLVER
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                actualizargrid();
                actualizargrid_cat();
            }
        });

        //BOTON IMAGEN CARGAR
        btnImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GaleriaActivity.requerido="palabra";

                Intent explicit_intent = new Intent(CreateWordActivity.this, GaleriaActivity.class);
                explicit_intent.putExtra("txt1",edtPalabra.getText().toString());
                explicit_intent.putExtra("txt2",edtFrase.getText().toString());

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
                    String frase= edtFrase.getText().toString();
                    if(!TextUtils.isEmpty(palabra) && !TextUtils.isEmpty(frase) && !estImg.equals("negativo")){
                        sqLiteHelper.insertarDatoPalabra(
                                edtPalabra.getText().toString().trim(),
                                edtFrase.getText().toString().trim(),
                                ""+foreign,
                                ImageViewToByte(imageView)
                        );
                        Toast.makeText(CreateWordActivity.this,R.string.guardado_exito,Toast.LENGTH_SHORT).show();
                        edtPalabra.setText("");
                        edtFrase.setText("");
                        imageView.setImageResource(R.mipmap.ic_launcher_galeria);
                    }
                    else{
                        Toast.makeText(CreateWordActivity.this,R.string.no_guardado_vacio,Toast.LENGTH_SHORT).show();
                    }

                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(CreateWordActivity.this,R.string.no_guardado_error + " " + e.getMessage(),Toast.LENGTH_SHORT).show();
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
        sql="SELECT * FROM PALABRA WHERE FK="+foreign;
        Cursor cursor= MainActivity.sqLiteHelperBd.obtenerdatos(sql);
        ListPalabrasActivity.list.clear();
        while (cursor.moveToNext()){
            int id=cursor.getInt(0);
            String palabra= cursor.getString(1);
            String frase=cursor.getString(2);
            int fk=cursor.getInt(3);
            byte[] imagen =cursor.getBlob(4);

            ListPalabrasActivity.list.add(new Palabras(id,palabra,frase,fk,imagen));
        }
        ListPalabrasActivity.adapter.notifyDataSetChanged();

        if(cursor.getCount()==1){
            ListPalabrasActivity.gridView.setVisibility(View.VISIBLE);
            ListPalabrasActivity.txt.setVisibility(View.INVISIBLE);
        }
    }

    public void actualizargrid_cat(){
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
        edtPalabra=(EditText) findViewById(R.id.edtPalabra);
        edtFrase=(EditText) findViewById(R.id.edtFrase);
        btnGuardar=(Button) findViewById(R.id.btnGuardar);
        btnVolver=(Button) findViewById(R.id.btnVolver);
        btnImagen=(Button) findViewById(R.id.btnImagen);
        imageView=(ImageView) findViewById(R.id.imageViewCreate);
    }
}
