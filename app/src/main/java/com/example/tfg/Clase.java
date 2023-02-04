package com.example.tfg;

import java.util.ArrayList;

public class Clase {
    private ArrayList<String> alumnos;
    private String nombre;
    private String centro;
    private ArrayList<String> profesores;
    private String centro_nombre;
    //DatabaseReference myRef;

    public Clase() {
        alumnos=new ArrayList<>();
        // Default constructor required for calls to DataSnapshot.getValue(User.class)

    }
    public Clase(String nombre, String centro) {
        alumnos=new ArrayList<>();
        profesores=new ArrayList<>();
        this.nombre=nombre;
        this.centro=centro;
        this.centro_nombre="CN" + centro + nombre;

    }

    public void addAlumno(String idAlumno){
        alumnos.add(idAlumno);
    }
    public void removeAlumno(String idAlumno){
        alumnos.remove(idAlumno);
    }
    public ArrayList<String> getAlumnos(){
        return alumnos;
    }
    public void addProfesor(String idProfe){
        profesores.add(idProfe);
    }
    public void removeProfesor(String idProfe){
        profesores.remove(idProfe);
    }
    public ArrayList<String> getProfesores(){
        return profesores;
    }
    public String getNombre(){
        return nombre;
    }
    public String getCentro(){
        return centro;
    }
    public String getCentro_nombre(){
        return centro_nombre;
    }
}
