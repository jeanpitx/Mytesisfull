package com.example.yojea.mytesiscero.Connections;

/**
 * Created by yojea on 14/9/2017.
 */

public class Categorias {

    private int id;
    private String palabra;
    private byte [] imagen;

    public Categorias(int id, String palabra, byte[] imagen) {

        this.id = id;
        this.palabra = palabra;
        this.imagen = imagen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPalabra() {
        return palabra;
    }

    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }


}
