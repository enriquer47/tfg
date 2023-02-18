package com.example.tfg.Model;

import android.os.Build;

import java.time.LocalDate;

public class Evento {
    private String nombre;
    private LocalDate fecha;
    private int estres;
    public Evento() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Evento(String nombre, int estres) {
        this.nombre = nombre;
        this.estres = estres;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.fecha = LocalDate.now();
        }
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public LocalDate getFecha() {
        return fecha;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    public int getEstres() {
        return estres;
    }
    public void setEstres(int estres) {
        this.estres = estres;
    }

}
