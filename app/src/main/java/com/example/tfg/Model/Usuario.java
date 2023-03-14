package com.example.tfg.Model;

public class Usuario {
    private String email;
    private String tipoCuenta;
    private String uid;
    private String nombre;
    private String apellidos;

    public Usuario() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Usuario(String email, String tipoCuenta) {
        this.email = email;
        this.tipoCuenta = tipoCuenta;
    }

    public Usuario(String email, String tipoCuenta, String uid, String nombre, String apellidos) {
        this.email = email;
        this.tipoCuenta = tipoCuenta;
        this.uid = uid;
        this.nombre = nombre;
        this.apellidos = apellidos;
    }
    public Usuario(String email, String uid, String nombre, String apellidos) {
        this.email = email;
        this.uid = uid;
        this.nombre = nombre;
        this.apellidos = apellidos;
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
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getUid() {
        return uid;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getNombre() {
        return nombre;
    }
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
    public String getApellidos() {
        return apellidos;
    }
}
