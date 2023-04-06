package com.example.tfg.Model;

import android.os.Build;

import java.time.LocalDate;

public class Evento {
    private String nombre;
    private String fecha;
    private int estres;
    private String id;
    public Evento() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Evento(String nombre, int estres) {
        this.nombre = nombre;
        this.estres = estres;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
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

}
