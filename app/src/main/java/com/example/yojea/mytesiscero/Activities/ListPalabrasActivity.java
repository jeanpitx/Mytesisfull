package com.example.yojea.mytesiscero.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.yojea.mytesiscero.Adapters.ImagePalAdapter;
import com.example.yojea.mytesiscero.Connections.Palabras;
import com.example.yojea.mytesiscero.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListPalabrasActivity extends AppCompatActivity  implements TextToSpeech.OnInitListener{

    private String estImg="negativo";

    public static ArrayList<Palabras> list;
    public static ImagePalAdapter adapter= null;
    public static TextView txt;
    public static GridView gridView;
    public static int foreign;

    //variables textospeak
    private TextToSpeech tts;
    private String textoescuchar;

    public int getforeign(){
        return foreign;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_palabras);

        foreign= ListCategoriaActivity.foreign;

        //al crear la apertura de la aplicacion se almacena en una instancia para utilizar texto a voz con la variable tss
        tts= new TextToSpeech(this,this);
        Toast.makeText(getApplicationContext(),"Escogió la categoria: " + foreign,Toast.LENGTH_SHORT).show();

        gridView = (GridView) findViewById(R.id.gridview);
        list =new ArrayList<>();
        adapter= new ImagePalAdapter(this, R.layout.molde_grid, list);
        gridView.setAdapter(adapter);

        //obtenemos todos los datos de SQLITE
        String sql;
        sql="SELECT * FROM PALABRA WHERE FK="+foreign;
        Cursor cursor= MainActivity.sqLiteHelperBd.obtenerdatos(sql);
        list.clear();
        while (cursor.moveToNext()){
            int id=cursor.getInt(0);
            String palabra= cursor.getString(1);
            String frase=cursor.getString(2);
            int fk=cursor.getInt(3);
            byte[] imagen =cursor.getBlob(4);

            list.add(new Palabras(id,palabra,frase,fk,imagen));
        }
        cursorvacio(cursor);
        adapter.notifyDataSetChanged();
        //despues de esto se otorgo un permiso en android manifet y defino parentActivityName donde esta la bd

        //AQUI VA EL CODIGO DE ITEN SELECCIONADO PARA LLENAR EL TEXTO A HABLAR
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String frase=list.get(position).getFrase();
                texttopeech(frase);
            }
        });

        //IMAGEN VIEW DE HABLAR BOTON
        ImageButton speak=(ImageButton) findViewById(R.id.btnHablar); //obtenemos las acciones del boton
        speak.setOnClickListener(new View.OnClickListener() { //escuchamos las acciones del boton
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(textoescuchar)){
                    speakOutNow(); // llamamos a la funcion creada al final para que reproduczca el sonido
                }else{
                    Toast.makeText(getApplicationContext(),R.string.error_audio,Toast.LENGTH_SHORT).show();
                }

            }
        });

        //EVENTO CLIC CUANDO NO HAY REGISTROS
        TextView textclic=(TextView) findViewById(R.id.txtsindatos);
        textclic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListPalabrasActivity.this, CreateWordActivity.class);
                startActivity(intent);
                GridView grid = (GridView) findViewById(R.id.gridview);
                grid.setVisibility(View.VISIBLE);
                finish();
            }
        });

        //creamos el evento de clic mantenido
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                CharSequence[] Items= {"Editar palabra","!Eliminar palabra!"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(ListPalabrasActivity.this);
                dialog.setTitle(R.string.titulo_dialog_mantenido);
                dialog.setItems(Items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if(item==0){
                            //actualizar
                            Integer id=list.get(position).getId();
                            Palabras palabras= list.get(id);

                            showDialogUpdate(ListPalabrasActivity.this,palabras.getId(),palabras.getPalabra(),palabras.getFrase(),palabras.getImagen());

                        }else{
                            //eliminar
                            Integer id=list.get(position).getId();
                            showDialogDelete(id);

                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    //funcion que habla
    private void texttopeech(final String text) {
        //pone el texto en el textview
        EditText edthabla=(EditText) findViewById(R.id.editHabla);
        edthabla.setText(text);
        textoescuchar=text;
    }

    //verificamos que existan datos o no.
    private void cursorvacio(Cursor cursor){
        if(cursor.getCount()==0){
            TextView txt = (TextView) findViewById(R.id.txtsindatos);
            txt.setVisibility(View.VISIBLE);
            GridView grid = (GridView) findViewById(R.id.gridview);
            grid.setVisibility(View.INVISIBLE);
        }
    }

    //FUNCION DE ELIMINAR
    private void showDialogDelete(final int idElemento) {
        AlertDialog.Builder dialogdelete=new AlertDialog.Builder(ListPalabrasActivity.this);
        dialogdelete.setTitle(R.string.titulo_dialog_eliminar);
        dialogdelete.setMessage(R.string.mensaje_eliminar);
        dialogdelete.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    MainActivity.sqLiteHelperBd.eliminarDatoPalabra(idElemento);
                    Toast.makeText(getApplicationContext(), R.string.eliminado_exito, Toast.LENGTH_SHORT).show();
                    actualizargrid();
                }catch (Exception e){
                    Log.e("Error Borrado: ", e.getMessage());
                    Toast.makeText(ListPalabrasActivity.this,R.string.no_eliminado_error + " " + e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogdelete.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogdelete.show();
    }


    private void showDialogAcerca(Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.acercade);
        dialog.setTitle(R.string.title2);

        Button btn1 = (Button) dialog.findViewById(R.id.acercaboton);

        //seteamos ancho width para el dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        //seteamos largo height para el dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.80);
        dialog.getWindow().setLayout(width, height);
        dialog.show();


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //muestra un dialogo con formulario para actualizar
    ImageView imageViewCreate;
    private void showDialogUpdate(Activity activity, final int position,String palabra, String frase, byte[] imagen) {

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.activity_create_word);
        dialog.setTitle(R.string.title1);

        imageViewCreate = (ImageView) dialog.findViewById(R.id.imageViewCreate);
        final EditText edtpalabra = (EditText) dialog.findViewById(R.id.edtPalabra);
        final EditText edtfrase = (EditText) dialog.findViewById(R.id.edtFrase);
        Button btnupdate = (Button) dialog.findViewById(R.id.btnActualizar);
        Button btnselec = (Button) dialog.findViewById(R.id.btnImagen);

        Button btnvolver = (Button) dialog.findViewById(R.id.btnVolver);
        Button btnguardar = (Button) dialog.findViewById(R.id.btnGuardar);
        btnupdate.setVisibility(View.VISIBLE);
        btnguardar.setVisibility(View.INVISIBLE);
        btnvolver.setVisibility(View.INVISIBLE);
        edtpalabra.setText(palabra);
        edtfrase.setText(frase);
        Bitmap bitmap= BitmapFactory.decodeByteArray(imagen,0,imagen.length);
        imageViewCreate.setImageBitmap(bitmap);

        //seteamos ancho width para el dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        //seteamos largo height para el dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.95);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        //se puede hacer desde la imagen solo se cambia la imagen para el clicllistener
        btnselec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(
                        ListPalabrasActivity.this,
                        //para obtener los permisos se declaran estas lineas y ademas
                        //en el archivo androidmanifest.xml tambien se solicita el permiso
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888//codigo de permiso en el otro es 999
                );
            }
        });

        //boton actualizar
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String palabra= edtpalabra.getText().toString();
                    String frase= edtfrase.getText().toString();
                    if(!TextUtils.isEmpty(palabra) && !TextUtils.isEmpty(frase)) {
                        MainActivity.sqLiteHelperBd.actualizarDato(
                                edtpalabra.getText().toString().trim(),
                                edtfrase.getText().toString().trim(),
                                CreateWordActivity.ImageViewToByte(imageViewCreate),
                                "" + foreign, //foreig key
                                position
                        );
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), R.string.actualizado_exito, Toast.LENGTH_SHORT).show();
                        actualizargrid();
                    }else{
                        Toast.makeText(ListPalabrasActivity.this,R.string.no_guardado_vacio,Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception error){
                    Log.e("Error de actualización", error.getMessage());
                    Toast.makeText(ListPalabrasActivity.this,R.string.no_guardado_error + " " + error.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void actualizargrid(){
        //obtenemos todos los datos de SQLITE
        String sql;
        sql="SELECT * FROM PALABRA WHERE FK="+foreign;
        Cursor cursor= MainActivity.sqLiteHelperBd.obtenerdatos(sql);
        list.clear();
        while (cursor.moveToNext()){
            int id=cursor.getInt(0);
            String palabra= cursor.getString(1);
            String frase=cursor.getString(2);
            int fk=cursor.getInt(3);
            byte[] imagen =cursor.getBlob(4);

            list.add(new Palabras(id,palabra,frase,fk,imagen));
        }
        cursorvacio(cursor);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==888){
            if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                Intent intent= new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 888);
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
        if(requestCode==888 && resultCode==RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                InputStream inputStream= getContentResolver().openInputStream(uri);
                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                imageViewCreate.setImageBitmap(bitmap);//aqui se muestra la nueva imagen
                estImg="positivo";//si se cargo la nueva imagen
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                estImg="negativo";//no se cargo la nueva imagen y hubo error
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //funcion que reproduce el audio
    private void speakOutNow (){
        //reproduce la variable text en modo flush que remplaza el audio anterior por uno nuevo,
        //tambien hay modo add que va agregando, en null van los parametros de lento y demas
        tts.speak(textoescuchar, TextToSpeech.QUEUE_FLUSH, null);
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onInit(int text) {
        //si el texto escrito y la operacion hablada es exitosa y son iguales entonces
        if(text == TextToSpeech.SUCCESS){
            Locale loc = new Locale("es", "MEX"); // codigo agregado estaba en vez de loc abajo estaba getDefault() o "es", "ES" probar
            int language = tts.setLanguage(loc); //obtien en lenguaje el leguaje predefinido que este caso es español es del sistema
            //en el video se definia ingles en vez de getDefault() y se pregunta si el lenguaje definido ingles estaba soportado o existia en el dispositivo
            //si estaba soportado habilita el boton y reproduce el audio
            if(language==TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
                speakOutNow();
            }
        }
    }


    //jala el menu grid menu para implementarlo en la interfaz
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.grid_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    //detecta el boton seleccionado
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.btnAgregar:
                Intent intent = new Intent(ListPalabrasActivity.this, CreateWordActivity.class);
                startActivity(intent);
                return true;
            case R.id.btnAcerca:
                showDialogAcerca(ListPalabrasActivity.this);
                return true;
            case R.id.btnBuscar:
                Intent intent1 = new Intent(ListPalabrasActivity.this, ListPalabrasSearchActivity.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
