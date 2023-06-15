package com.ISD;

public class ExecuteActorSolicitar {
    public static void main(String[] args) {
        
        //Iniciar el actor para la operacion de solicitar
        ActorSolicitar actorSolicitar = new ActorSolicitar();
        
        try {
            actorSolicitar.iniciar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
