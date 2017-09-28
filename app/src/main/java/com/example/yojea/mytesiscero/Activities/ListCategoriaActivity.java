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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yojea.mytesiscero.Adapters.ImageCatAdapter;
import com.example.yojea.mytesiscero.Connections.Categorias;
import com.example.yojea.mytesiscero.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;


public class ListCategoriaActivity extends AppCompatActivity{

    private GridView gridView;
    public static ArrayList<Categorias> list;
    public static ImageCatAdapter adapter= null;
    private String estImg="negativo";
    public static int foreign;
    private TextView textoBuscado;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirma);
        builder.setMessage(R.string.seguro);
        builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog= builder.create();
        dialog.show();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_categoria);

        textoBuscado = (TextView) findViewById(R.id.txtdescriptivo);

        gridView = (GridView) findViewById(R.id.gridviewcat);
        list =new ArrayList<>();
        adapter= new ImageCatAdapter(this, R.layout.molde_grid_grid, list);
        gridView.setAdapter(adapter);

        //obtenemos todos los datos de SQLITE
        String sql;
        sql="SELECT * FROM CATEGORIA";
        Cursor cursor= MainActivity.sqLiteHelperBd.obtenerdatos(sql);
        list.clear();
        while (cursor.moveToNext()){
            int id=cursor.getInt(0);
            String palabra= cursor.getString(1);
            byte[] imagen =cursor.getBlob(2);

            list.add(new Categorias(id,palabra, imagen));
        }
        cursorvacio(cursor);
        adapter.notifyDataSetChanged();
        //despues de esto se otorgo un permiso en android manifet y defino parentActivityName donde esta la bd


        //EVENTO CLIC CUANDO NO HAY REGISTROS
        TextView textclic=(TextView) findViewById(R.id.txtsindatoscat);
        textclic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListCategoriaActivity.this, CreateCatActivity.class);
                startActivity(intent);
                GridView grid = (GridView) findViewById(R.id.gridviewcat);
                grid.setVisibility(View.VISIBLE);
                finish();
            }
        });

        //Creamos el evento del clic simple
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                foreign=list.get(position).getId();
                Intent intent = new Intent(ListCategoriaActivity.this, ListPalabrasActivity.class);
                startActivity(intent);
            }
        });


        //creamos el evento de clic mantenido
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                CharSequence[] Items= {"Editar Categoria","!Eliminar categoria!"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(ListCategoriaActivity.this);
                dialog.setTitle(R.string.titulo_dialog_mantenido);
                dialog.setItems(Items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if(item==0){
                            //actualizar
                            //obtenemos instancia de la lista de elementos
                            Integer id=list.get(position).getId();
                            Categorias categorias= list.get(id);
                            //mostramos dialogo de actualizaciona aquí, con la palabra que se esta actualizando
                            //codigo original video 2, modificado 31/8
                            showDialogUpdate(ListCategoriaActivity.this,categorias.getId(),categorias.getPalabra(),categorias.getImagen());

                        }else{
                            //eliminar
                            Cursor c = MainActivity.sqLiteHelperBd.obtenerdatos("SELECT Id FROM CATEGORIA");
                            ArrayList<Integer> arrID=new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            Integer id=list.get(position).getId();
                            showDialogDelete(id); // id cat y la fk
                            //MainActivity.sqLiteHelperBd.queryData("DELETE FROM PALABRA");//borrar todo
                            //Eliminar
                            //Toast.makeText(getApplicationContext(),"Eliminado con Exito todo..",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }
    //verificamos que existan datos o no.
    private void cursorvacio(Cursor cursor){
        if(cursor.getCount()==0){
            TextView txt = (TextView) findViewById(R.id.txtsindatos);
            txt.setVisibility(View.VISIBLE);
            GridView grid = (GridView) findViewById(R.id.gridview);
            grid.setVisibility(View.INVISIBLE);
            TextView txtcat = (TextView) findViewById(R.id.txtdescriptivo);
            txtcat.setVisibility(View.INVISIBLE);
        }
    }

    //FUNCION DE ELIMINAR
    private void showDialogDelete(final int idElemento) {
        AlertDialog.Builder dialogdelete=new AlertDialog.Builder(ListCategoriaActivity.this);
        dialogdelete.setTitle(R.string.titulo_dialog_eliminar);
        dialogdelete.setMessage(R.string.mensaje_eliminar);
        dialogdelete.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    MainActivity.sqLiteHelperBd.eliminarDatoCategoria_palabras(idElemento);
                    MainActivity.sqLiteHelperBd.eliminarDatoCategoria(idElemento);
                    Toast.makeText(getApplicationContext(), R.string.eliminado_exito, Toast.LENGTH_SHORT).show();
                    actualizargrid();
                }catch (Exception e){
                    Log.e("Error Borrado: ", e.getMessage());
                    Toast.makeText(ListCategoriaActivity.this,R.string.no_eliminado_error + " " + e.getMessage(),Toast.LENGTH_SHORT).show();
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
    private void showDialogUpdate(Activity activity, final int position, String palabra,  byte[] imagen) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.activity_create_cat);
        dialog.setTitle(R.string.title1);

        imageViewCreate = (ImageView) dialog.findViewById(R.id.imageViewCreateCat);
        final EditText edtpalabra = (EditText) dialog.findViewById(R.id.edtPalabraCat);
        Button btnupdate = (Button) dialog.findViewById(R.id.btnActualizarCat);
        Button btnselec = (Button) dialog.findViewById(R.id.btnImagenCat);

        Button btnvolver = (Button) dialog.findViewById(R.id.btnVolverCat);
        Button btnguardar = (Button) dialog.findViewById(R.id.btnGuardarCat);
        btnupdate.setVisibility(View.VISIBLE);
        btnguardar.setVisibility(View.INVISIBLE);
        btnvolver.setVisibility(View.INVISIBLE);
        edtpalabra.setText(palabra);
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
                        ListCategoriaActivity.this,
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
                    if(!TextUtils.isEmpty(palabra)) {
                        MainActivity.sqLiteHelperBd.actualizarDatoCat(
                                edtpalabra.getText().toString().trim(),
                                CreateCatActivity.ImageViewToByte(imageViewCreate),
                                position
                        );
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), R.string.actualizado_exito, Toast.LENGTH_SHORT).show();
                        actualizargrid();
                    }else{
                        Toast.makeText(ListCategoriaActivity.this,R.string.no_guardado_vacio_cat,Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception error){
                    Log.e("Error de actualización", error.getMessage());
                    Toast.makeText(ListCategoriaActivity.this,R.string.no_guardado_error + " " + error.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void actualizargrid(){
        //obtenemos todos los datos de SQLITE
        String sql;
        sql="SELECT * FROM CATEGORIA";
        Cursor cursor= MainActivity.sqLiteHelperBd.obtenerdatos(sql);
        list.clear();
        while (cursor.moveToNext()){
            int id=cursor.getInt(0);
            String palabra= cursor.getString(1);
            byte[] imagen =cursor.getBlob(2);

            list.add(new Categorias(id,palabra,imagen));
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
                Intent intent = new Intent(ListCategoriaActivity.this, CreateCatActivity.class);
                startActivity(intent);
                return true;
            case R.id.btnAcerca:
                showDialogAcerca(ListCategoriaActivity.this);
                return true;
            case R.id.btnBuscar:
                Intent intent1 = new Intent(ListCategoriaActivity.this, ListPalabrasSearchActivity.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
