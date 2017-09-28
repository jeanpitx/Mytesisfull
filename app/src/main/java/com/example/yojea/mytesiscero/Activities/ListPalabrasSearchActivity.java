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
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
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
import java.util.Locale;

public class ListPalabrasSearchActivity extends AppCompatActivity  implements TextToSpeech.OnInitListener, SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener{

    private GridView gridView;
    private ArrayList<Palabras> list;
    public ImagePalAdapter adapter= null;
    private String estImg="negativo";
    private int foreign;

    //variables textospeak
    private TextToSpeech tts;
    private String textoescuchar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_palabras);

        foreign=ListCategoriaActivity.foreign;

        //al crear la apertura de la aplicacion se almacena en una instancia para utilizar texto a voz con la variable tss
        tts= new TextToSpeech(this,this);

        gridView = (GridView) findViewById(R.id.gridview);
        list =new ArrayList<>();
        adapter= new ImagePalAdapter(this, R.layout.molde_grid, list);
        gridView.setAdapter(adapter);

        //obtenemos todos los datos de SQLITE
        String sql;
        sql="SELECT * FROM PALABRA";
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

        //clic AQUI VA EL CODIGO DE ITEN SELECCIONADO PARA LLENAR EL TEXTO A HABLAR
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
                Intent intent = new Intent(ListPalabrasSearchActivity.this, CreateWordActivity.class);
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
                CharSequence[] Items= {"Editar Palabra","¡Eliminar palabra!"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(ListPalabrasSearchActivity.this);
                dialog.setTitle(R.string.titulo_dialog_mantenido);
                dialog.setItems(Items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if(item==0){
                            //actualizar
                            Cursor c = MainActivity.sqLiteHelperBd.obtenerdatos("SELECT Id,palabra,frase,fk,imagen FROM PALABRA");
                            ArrayList<Palabras> arrID=new ArrayList<Palabras>();
                            while (c.moveToNext()){
                                int id=c.getInt(0);
                                String palabra= c.getString(1);
                                String frase=c.getString(2);
                                int fk=c.getInt(3);
                                byte[] imagen =c.getBlob(4);
                                list.add(new Palabras(id,palabra,frase,fk,imagen));
                            }
                            //obtenemos instancia de la lista de elementos
                            Integer id=list.get(position).getId();
                            Palabras palabras= list.get(id);
                            //mostramos dialogo de actualizaciona aquí, con la palabra que se esta actualizando
                            //codigo original video 2, modificado 31/8
                            showDialogUpdate(ListPalabrasSearchActivity.this,palabras.getId(),palabras.getPalabra(),palabras.getFrase(),palabras.getImagen());

                        }else{
                            //eliminar
                            Cursor c = MainActivity.sqLiteHelperBd.obtenerdatos("SELECT Id FROM PALABRA");
                            ArrayList<Integer> arrID=new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            Integer id=list.get(position).getId();
                            showDialogDelete(arrID.get(id));
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
            Toast.makeText(getApplicationContext(),"No existe lo que busca", Toast.LENGTH_SHORT).show();
        }
    }

    //FUNCION DE ELIMINAR
    private void showDialogDelete(final int idElemento) {
        AlertDialog.Builder dialogdelete=new AlertDialog.Builder(ListPalabrasSearchActivity.this);
        dialogdelete.setTitle(R.string.titulo_dialog_eliminar);
        dialogdelete.setMessage(R.string.mensaje_eliminar);
        dialogdelete.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    MainActivity.sqLiteHelperBd.eliminarDatoPalabra(idElemento);
                    Toast.makeText(getApplicationContext(), R.string.eliminado_exito, Toast.LENGTH_SHORT).show();
                    actualizargrid();
                }catch (Exception e){
                    Log.e("Error Borrado: ", e.getMessage());
                    Toast.makeText(ListPalabrasSearchActivity.this,R.string.no_eliminado_error + " " + e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogdelete.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogdelete.show();
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
                        ListPalabrasSearchActivity.this,
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
                        Toast.makeText(ListPalabrasSearchActivity.this,R.string.no_guardado_vacio,Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception error){
                    Log.e("Error Actualización", error.getMessage());
                    Toast.makeText(ListPalabrasSearchActivity.this,R.string.no_guardado_error + " " + error.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void actualizargrid(){
        //obtenemos todos los datos de SQLITE
        String sql;
        sql="SELECT * FROM PALABRA";
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

    public void actualizargridFiltro(String Dato){
        //obtenemos todos los datos de SQLITE
        String sql;
        sql="SELECT * FROM PALABRA WHERE PALABRA LIKE '%"+ Dato + "%'";
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
        getMenuInflater().inflate(R.menu.grid_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        MenuItem menuItem1 = menu.findItem(R.id.btnBuscar);
        MenuItem menuItem3 = menu.findItem(R.id.btnAgregar);
        MenuItem menuItem4 = menu.findItem(R.id.btnAcerca);

        searchItem.expandActionView();
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(ListPalabrasSearchActivity.this);

        menuItem1.setVisible(false);
        menuItem3.setVisible(false);
        menuItem4.setVisible(false);
        searchItem.setVisible(true);

        searchView.onActionViewExpanded();
        searchView.setQuery("",true);
        searchView.setFocusable(true);
        searchView.requestFocusFromTouch();

        return super.onCreateOptionsMenu(menu);
    }

    //detecta el boton seleccionado
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
                return super.onOptionsItemSelected(item);

    }


    @Override
    public boolean onMenuItemActionExpand (MenuItem item) {
        Toast.makeText(getApplicationContext(), R.string.busq1, Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        Toast.makeText(getApplicationContext(), R.string.busq2, Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        Toast.makeText(getApplicationContext(), R.string.busq3 + s, Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        actualizargridFiltro(s);
        //adapter.getFilter().filter(s);
        //Toast.makeText(getApplicationContext(), "estas buscado: "+ s, Toast.LENGTH_SHORT).show();
        return false;
    }


}
