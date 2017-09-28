package com.example.yojea.mytesiscero.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.yojea.mytesiscero.Connections.SQLiteHelper;
import com.example.yojea.mytesiscero.R;


public class MainActivity extends AppCompatActivity {

    public static SQLiteHelper sqLiteHelperBd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        sqLiteHelperBd=new SQLiteHelper(this, "PalabrasDB.sqllite",null,1);

        //obtenemos todos los datos de SQLITE
        String sql;
        sql="SELECT name FROM sqlite_master WHERE TYPE='table' AND name='CATEGORIA';";
        Cursor cursor= sqLiteHelperBd.obtenerdatos(sql);
        if(cursor.getCount()==0){ //si no existen se abre la ventana de carga de todos los datos
            Toast.makeText(getApplicationContext(), R.string.no_existen,Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, InstaladorActivity.class);
            startActivity(intent);
            finish();
        }else{ //si si existen se abre la ventana categoria
            Toast.makeText(getApplicationContext(),R.string.si_existen,Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, ListCategoriaActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
