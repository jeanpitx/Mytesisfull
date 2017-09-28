package com.example.yojea.mytesiscero.Connections;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by yojea on 28/8/2017.
 */

public class SQLiteHelper extends SQLiteOpenHelper{


    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }
    public void eliminarDatoPalabra(int id){
        SQLiteDatabase database = getWritableDatabase();
        String sql="DELETE FROM PALABRA WHERE id=?";
        SQLiteStatement statement=database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double) id);

        statement.execute();
        database.close();
    }

    public void eliminarDatoCategoria(int id){
        SQLiteDatabase database = getWritableDatabase();
        String sql="DELETE FROM CATEGORIA WHERE id=?";
        SQLiteStatement statement=database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double) id);

        statement.execute();
        database.close();
    }

    public void eliminarDatoCategoria_palabras(int id){
        SQLiteDatabase database = getWritableDatabase();
        String sql="DELETE FROM palabra WHERE fk=?";
        SQLiteStatement statement=database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double) id);

        statement.execute();
        database.close();
    }

    public void insertarDatoPalabra(String palabra, String frase, String foreigkey, byte[] imagen){
        SQLiteDatabase database = getWritableDatabase();
        String sql="INSERT INTO PALABRA VALUES (NULL, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1,palabra );
        statement.bindString(2,frase);
        statement.bindString(3,foreigkey);
        statement.bindBlob(4,imagen);

        statement.executeInsert();
    }

    public void insertarDatoCategoria(String palabra, byte[] imagen){
        SQLiteDatabase database = getWritableDatabase();
        String sql="INSERT INTO CATEGORIA VALUES (NULL, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1,palabra );
        statement.bindBlob(2,imagen);

        statement.executeInsert();
    }

    public void actualizarDato(String palabra, String frase, byte[] imagen,String foreigkey, int id){
        SQLiteDatabase database= getWritableDatabase();
        String sql="UPDATE PALABRA SET palabra=?, frase=?, imagen=?, fk=? WHERE id=?";
        SQLiteStatement statement= database.compileStatement(sql);

        statement.bindString(1,palabra );
        statement.bindString(2,frase);
        statement.bindBlob(3,imagen);
        statement.bindString(4,foreigkey);
        statement.bindDouble(5,(double) id);

        statement.execute();
        database.close();
    }

    public void actualizarDatoCat(String palabra, byte[] imagen, int id){
        SQLiteDatabase database= getWritableDatabase();
        String sql="UPDATE CATEGORIA SET palabra=?, imagen=? WHERE id=?";
        SQLiteStatement statement= database.compileStatement(sql);

        statement.bindString(1,palabra );
        statement.bindBlob(2,imagen);
        statement.bindDouble(3,(double) id);

        statement.execute();
        database.close();
    }



    public Cursor obtenerdatos(String sql){
        SQLiteDatabase database= getReadableDatabase();
        return database.rawQuery(sql, null);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //se ejecuta esto cuando la base no existe
        //db.execSQL(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //se ejecuta cuando se detectan cambios de versiones de la base de datos
        //por simplicidad se elimina la que existe y se crea una nueva
        //db.execSQL("DROP TABLE IF EXISTS Palabras");
        //db.execSQL(sql);
    }
}
