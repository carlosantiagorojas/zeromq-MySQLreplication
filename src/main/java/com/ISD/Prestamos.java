package com.ISD;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "prestamos")
public class Prestamos implements Serializable {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "FechaRenovacion")
    private Date FechaRenovacion;

    @Column(name = "libro_ISBN")
    private int libro_ISBN;

    @Column(name = "sede")
    private int sede;

    public Prestamos() {
    }

    public Prestamos(int id, Date fechaRenovacion, int libro_ISBN, int sede) {
        this.id = id;
        FechaRenovacion = fechaRenovacion;
        this.libro_ISBN = libro_ISBN;
        this.sede = sede;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFechaRenovacion() {
        return FechaRenovacion;
    }

    public void setFechaRenovacion(Date fechaRenovacion) {
        FechaRenovacion = fechaRenovacion;
    }

    public int getLibro_ISBN() {
        return libro_ISBN;
    }

    public void setLibro_ISBN(int libro_ISBN) {
        this.libro_ISBN = libro_ISBN;
    }

    public int getSede() {
        return sede;
    }

    public void setSede(int sede) {
        this.sede = sede;
    }

}