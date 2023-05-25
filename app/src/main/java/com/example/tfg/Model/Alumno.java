package com.example.tfg.Model;

public class Alumno {
    private String nombre;
    private int estres; // 0-100 empieza en 50 que es nivel medio de estres

    public Alumno() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Alumno(String nombre) {
        this.nombre = nombre;
        this.estres = 0;
    }
    public int getEstres() {
        return estres;
    }
    public void setEstres(int estres) {
        this.estres = estres;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void addEstres(int estres){
        this.estres=Math.max(0,Math.min(100,this.estres+estres));
        /*
        if (this.estres+estres<=100 && this.estres+estres>=0) {
            this.estres += estres;
        }*/
    }
}
