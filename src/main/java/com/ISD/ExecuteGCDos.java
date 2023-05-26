package com.ISD;

public class ExecuteGCDos {

    public static void main(String[] args) {
        
        //Iniciar gestor de carga de la sede 2
        GCTestDos gestorSedeDos = new GCTestDos();
        
        try {
            gestorSedeDos.iniciar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
