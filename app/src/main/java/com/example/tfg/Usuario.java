package com.example.tfg;

public class Usuario {
    public String email;
    public boolean esProfesor;
    private String centro;

    public Usuario() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)

    }

    public Usuario(String email, boolean esProfesor) {
        this.email = email;
        this.esProfesor=esProfesor;
        //CENTRO DE PRUEBA
        this.centro="1";
    }
    public String getCentro(){
        return centro;
    }

}
