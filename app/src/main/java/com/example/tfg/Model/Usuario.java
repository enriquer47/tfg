package com.example.tfg.Model;

public class Usuario {
    private String nombre;
    private String email;
    private String tipoCuenta;


    public Usuario() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        this.tipoCuenta="";
    }
    public Usuario(String nombre, String apellidos, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }
}
