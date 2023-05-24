package com.ISD;

public class GCTest 
{
    public static void main(String[] args) throws Exception {
        
        //Iniciar el gestor de carga
        GC gestorCarga = new GC();
        Thread hiloGestor = new Thread(gestorCarga);
        hiloGestor.start();
        
        // Declarar una variable final adicional para poder crear la falla
        final Thread finalHiloGestor = hiloGestor; 

        // Configurar el tiempo de la falla del gestor (valor en milisegundos)
        final int TiempoT = 1000;

        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                finalHiloGestor.interrupt(); // Utilizar la variable final adicional
            }
        }, TiempoT);

    }
}