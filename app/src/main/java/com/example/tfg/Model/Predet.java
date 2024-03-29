package com.example.tfg.Model;

public class Predet {

    private String nombre;
    private String imagen;
    private int estres;
    private String id;
    private String categoria;
    public Predet() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Predet(String nombre, int estres, String imagen) {
        this.nombre = nombre;
        this.estres = estres;
        this.imagen = imagen;
        this.categoria ="SIN CATEGORÍA";
    }
    public Predet(String nombre, String categoria, int estres, String imagen) {
        this.nombre = nombre;
        this.estres = estres;
        this.imagen = imagen;
        this.categoria = categoria;
    }


    public Predet(String nombre, int estres) {
        this.nombre = nombre;
        this.estres = estres;
        this.imagen = null;
        this.categoria ="SIN CATEGORÍA";

    }
    public Predet(String nombre, String categoria, int estres) {
        this.nombre = nombre;
        this.estres = estres;
        this.imagen = null;
        this.categoria = categoria;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getImagen() {
        return imagen;
    }
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    public int getEstres() {
        return estres;
    }
    public void setEstres(int estres) {
        this.estres = estres;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

}

