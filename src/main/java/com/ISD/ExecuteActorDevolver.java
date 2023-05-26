package com.ISD;

public class ExecuteActorDevolver {
    public static void main(String[] args) {
        
        //Iniciar el actor para la operacion de devolver
        ActorDevolver actorDevolver = new ActorDevolver();
        
        try {
            actorDevolver.iniciar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
