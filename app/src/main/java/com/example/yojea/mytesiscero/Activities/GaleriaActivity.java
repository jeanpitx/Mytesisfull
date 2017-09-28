package com.example.yojea.mytesiscero.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yojea.mytesiscero.Adapters.ImageGaleriaAdapter;
import com.example.yojea.mytesiscero.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class GaleriaActivity extends AppCompatActivity {

    //para rellenar el gri
    private GridView gridView;
    private ArrayList <String> list;
    ListAdapter adapter;

    //variables de elementos a la vista
    Integer[] arrayList;
    private TextView edtEstado;
    private Button btnAceptar, btnCancelar, btnImagen;
    private ImageView imageView;

    //variables locales
    private String estImg; // estado de la imagen para ver si se carga o no
    final int REQUEST_CODE_GALLERY=666;
    public static String requerido;//define que activity llama
    private String txtdos,txtuno; //texto que se recibe y se envia
    public static Bitmap bitmap; //imagen a enviar
    int totalveces;

    @Override
    public void onBackPressed() {
        if (requerido.equals("categoria")){
            Intent intent = new Intent(GaleriaActivity.this, CreateCatActivity.class);
            startActivity(intent);
            finish();
        } else if(requerido.equals("palabra")){
            Intent intent = new Intent(GaleriaActivity.this, CreateWordActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);

        //inicializa variables en una funcion
        init();

        Intent intent=getIntent();
        Bundle extras =intent.getExtras();
        if (extras != null ) {//ver si contiene datos
            txtuno = (String) extras.get("txt1");//Obtengo el texto escrito anteriormente
            txtdos = (String) extras.get("txt2");
        }


        Field[] fields= R.raw.class.getFields();
        arrayList=new Integer[fields.length];
        list = new ArrayList<>();
        int resoID;

        totalveces=(fields.length);
        for (int i=0;i < (totalveces-2) ;i++){
            resoID=getResources().getIdentifier("palabra"+(i+1),"raw",getPackageName());
            list.add("img"+(i+1));
            arrayList[i]=resoID;
        }

        Toast.makeText(getApplicationContext(),"Tarea Terminada",Toast.LENGTH_SHORT).show();
        //fields[i].getName()
        adapter= new ImageGaleriaAdapter(this,R.layout.molde_grid,list, arrayList);
        gridView.setAdapter(adapter);


        //BOTON ACEPTAR
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(estImg.equals("positivo")){
                    bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    if(requerido.equals("categoria")){
                        Intent explicit_intent = new Intent(GaleriaActivity.this, CreateCatActivity.class);
                        explicit_intent.putExtra("palabra", txtuno);
                        startActivity(explicit_intent);
                        finish();
                    }else if(requerido.equals("palabra")){
                        Intent explicit_intent = new Intent(GaleriaActivity.this, CreateWordActivity.class);
                        explicit_intent.putExtra("palabra", txtuno);
                        explicit_intent.putExtra("frase", txtdos);
                        startActivity(explicit_intent);
                        finish();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),R.string.no_img,Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Creamos el evento del clic simple

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SetImagen(arrayList[position]);
                edtEstado.setText(R.string.select_galeria);
                estImg="positivo";

            }
        });


        btnImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(
                        GaleriaActivity.this,
                        //para obtener los permisos se declaran estas lineas y ademas
                        //en el archivo androidmanifest.xml tambien se solicita el permiso
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });

        //BOTON CANCELAR
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requerido.equals("categoria")){
                    Intent intent = new Intent(GaleriaActivity.this, CreateCatActivity.class);
                    startActivity(intent);
                    finish();
                } else if(requerido.equals("palabra")){
                    Intent intent = new Intent(GaleriaActivity.this, CreateWordActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }


        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if(requestCode==REQUEST_CODE_GALLERY){
                if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                    Intent intent= new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_CODE_GALLERY);
                }
                else {
                    Toast.makeText(getApplicationContext(), R.string.mensaje_galeria_fracaso,Toast.LENGTH_SHORT).show();
                }
                return;
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(requestCode==REQUEST_CODE_GALLERY && resultCode==RESULT_OK && data != null){
                Uri uri = data.getData();
                try {
                    InputStream inputStream= getContentResolver().openInputStream(uri);
                    Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(bitmap);//aqui se muestra la nueva imagen
                    estImg="positivo";//si se cargo la nueva imagen
                    edtEstado.setText(R.string.select_galeria);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    estImg="negativo";//no se cargo la nueva imagen y hubo error
                }
            }

            super.onActivityResult(requestCode, resultCode, data);
        }
    private void SetImagen(int valor)
    {
        imageView.setImageResource(valor);
    }

    private void init(){
        edtEstado=(TextView) findViewById(R.id.txtEstadoGal);
        gridView=(GridView) findViewById(R.id.gridgaleria);
        btnAceptar= (Button) findViewById(R.id.acceptimg);
        btnCancelar=(Button) findViewById(R.id.cancelimg);
        btnImagen=(Button) findViewById(R.id.galeimg);
        imageView=(ImageView) findViewById(R.id.galeriaimgview);
    }



}
