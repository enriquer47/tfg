package com.example.tfg.Model;

public class Alumno extends Usuario{
    private int estres; // 0-100 empieza en 50 que es nivel medio de estres
    public Alumno() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Alumno(String email, String tipoCuenta, String uid, String nombre, String apellidos) {
        super(email, uid, nombre, apellidos);
        this.estres = 50;
        this.setTipoCuenta("Alumno");
    }
    public int getEstres() {
        return estres;
    }
    public void setEstres(int estres) {
        this.estres = estres;
    }
}
