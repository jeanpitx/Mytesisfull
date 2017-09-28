package com.example.yojea.mytesiscero.Connections;



public class Palabras {
    //datos de la base de datos
    private int id;
    private String palabra;
    private String frase;
    private int fk;
    private byte [] imagen;

    //ItemClickListener itemClickListener;


    public Palabras(int id, String palabra, String frase, int fk, byte[] imagen) {
        this.id = id;
        this.palabra = palabra;
        this.frase = frase;
        this.fk = fk;
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

    public String getFrase() {
        return frase;
    }

    public void setFrase(String frase) {
        this.frase = frase;
    }

    public int getFk() {
        return fk;
    }

    public void setFk(int fk) {
        this.fk = fk;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }


}
