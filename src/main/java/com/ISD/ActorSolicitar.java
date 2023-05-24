package com.ISD;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class ActorSolicitar{

    public ActorSolicitar() {
    }

    public static void main(String[] args) throws Exception {

        try (ZContext context = new ZContext()) {
            
            // Vincular con el puerto de los procesos solicitantes
            ZMQ.Socket socketSolicitar = context.createSocket(SocketType.REP);
            socketSolicitar.bind("tcp://localhost:5557");

            String respuestaGestor;
            boolean disponible;

            while (!Thread.currentThread().isInterrupted()) 
            {   
                // Se recibe el mensaje del gestor de carga
                byte[] reply = socketSolicitar.recv(0);
                String solicitud = new String(reply, ZMQ.CHARSET); // Se genera un string con la solicitud con formato: accion, codigo

                // Se guarda la informacion de la solicitud
                String[] elemSoliciutd = solicitud.split(" ");
                String topico = elemSoliciutd[0];
                int codigo = Integer.parseInt(elemSoliciutd[1]);

                
                System.out.println("\nInformacion del mensaje del gestor de carga recibido por el actor SOLICITAR:");
                System.out.println("Tipo de operacion: " + topico);
                System.out.println("Codigo del libro: " + codigo);

                System.out.println("\nRealizando actualizacion en la base de datos...");

                // Hacer la actualizacion en la base de datos
                disponible = FuncionesLibro.OperacionSolicitar(codigo);

                // Informar si se actualizo la base de datos

                if(disponible == true)
                {
                    respuestaGestor = "El libro SI esta disponible con codigo " + codigo;
                    socketSolicitar.send(respuestaGestor.getBytes(ZMQ.CHARSET), 0);
                    System.out.println("-----------------------------------------------------------");
                    System.out.println("El libro con codigo " + codigo + " se solicito exitosamente!");
                    System.out.println("-----------------------------------------------------------\n");
                }
                else{
                    respuestaGestor = "El libro NO esta disponible con codigo " + codigo;
                    socketSolicitar.send(respuestaGestor.getBytes(ZMQ.CHARSET), 0);
                    System.out.println("-----------------------------------------------------------");
                    System.out.println("No se pudo solicitar el libro con codigo " + codigo);
                    System.out.println("-----------------------------------------------------------\n");
                }
            }
        }
    }
}
