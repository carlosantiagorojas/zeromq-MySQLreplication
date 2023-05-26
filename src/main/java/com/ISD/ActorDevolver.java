package com.ISD;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class ActorDevolver {

    private static String topico;
    
    public ActorDevolver(){
    }

    public ActorDevolver(String topico) {
        ActorDevolver.topico = topico;
    }

    public static String getTopico() {
        return topico;
    }

    public static void setTopico(String topico) {
        ActorDevolver.topico = topico;
    }

    public void iniciar() throws Exception {
        
        // Suscribir el actor por default al topico Devolucion
        setTopico("Devolucion");

        try (ZContext context = new ZContext()) {
            // Conexion con un puerto para atender las publicaciones asociadas al topico
            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
            subscriber.connect("tcp://10.43.100.136:5556");
            subscriber.subscribe(getTopico().getBytes(ZMQ.CHARSET)); // Suscribirse al topico para recibir mensajes del publicador
            
            boolean actualizacion;
            // Leer las publicacion que va colocando el publicador
            while (!Thread.currentThread().isInterrupted())
            {
                // Se recibe el mensaje publicado por el gestor de carga
                String respuesta = subscriber.recvStr();
                
                // Guardar la informacion de la solicitud
                String [] elem = respuesta.split(" ");
                String topico = elem[0];
                int codigo = Integer.parseInt(elem[1]);

                System.out.println("\nActor suscrito al topico DEVOLUCION tomando requerimiento con informacion: ");
                System.out.println("Tipo de operacion: " + topico);
                System.out.println("Codigo del prestamo: " + codigo);

                System.out.println("\nRealizando actualizacion en la base de datos...");
                System.out.println("////////////////////////////////////////////////////////////\n");

                // Hacer la actualizacion en la base de datos
                actualizacion = FuncionesLibro.OperacionDevolucion(codigo);

                // Informar si se actualizo la base de datos
                if(actualizacion == true){
                    System.out.println("-----------------------------------------------------------");
                    System.out.println("El prestamo con codigo " + codigo + " se devolvio exitosamente!");
                    System.out.println("-----------------------------------------------------------\n");
                }
                else{
                    System.out.println("-----------------------------------------------------------");
                    System.out.println("No se pudo devolver el prestamo con codigo " + codigo);
                    System.out.println("-----------------------------------------------------------\n");
                } 
            }
        } 
    }
}

