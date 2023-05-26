package com.ISD;

public class ExecuteGC {
    public static void main(String[] args) {
        
        //Iniciar gestor de carga de la sede 1
        GCTest gestorSedeUno = new GCTest();
        
        try {
            gestorSedeUno.iniciar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
