package com.example.tfg;

public class Usuario {
    public String email;
    public String tipoCuenta;
    private String centro;

    public Usuario() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)

    }

    public Usuario(String email, String tipoCuenta) {
        this.email = email;
        this.tipoCuenta=tipoCuenta;
        //CENTRO DE PRUEBA
        this.centro="1";
    }
    public String getCentro(){
        return centro;
    }

}
