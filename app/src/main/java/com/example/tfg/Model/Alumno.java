package com.example.tfg.Model;

public class Alumno {
    private String nombre;
    private double estres; // 0-100 empieza en 50 que es nivel medio de estres

    public Alumno() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Alumno(String nombre) {
        this.nombre = nombre;
        this.estres = 50;
    }
    public double getEstres() {
        return estres;
    }
    public void setEstres(double estres) {
        this.estres = estres;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void addEstres(double estres){
        if (this.estres+estres<=100 && this.estres+estres>=0)
        this.estres+=estres;
    }
}
