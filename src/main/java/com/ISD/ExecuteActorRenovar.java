package com.ISD;

public class ExecuteActorRenovar {
    
    public static void main(String[] args) {
        
        //Iniciar el actor para la operacion de renovar
        ActorRenovar actorRenovar = new ActorRenovar();
        
        try {
            actorRenovar.iniciar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
