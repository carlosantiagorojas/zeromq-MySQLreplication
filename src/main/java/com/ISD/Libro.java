package com.ISD;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "libro")
public class Libro implements Serializable {

    @Id
    @Column(name = "ISBN")
    private int ISBN;

    @Column(name = "Nombre")
    private String Nombre;

    @Column(name = "Cantidad")
    private int Cantidad;

    public Libro() {
    }

    public Libro(int iSBN, String nombre, int cantidad) {
        ISBN = iSBN;
        Nombre = nombre;
        Cantidad = cantidad;
    }

    public int getISBN() {
        return ISBN;
    }

    public void setISBN(int iSBN) {
        ISBN = iSBN;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public int getCantidad() {
        return Cantidad;
    }

    public void setCantidad(int cantidad) {
        Cantidad = cantidad;
    }
   
}