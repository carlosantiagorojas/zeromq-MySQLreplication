package com.ISD;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class ActorRenovar{

    private static String topico;

    public ActorRenovar(){
    }

    public ActorRenovar(String topico) {
        ActorRenovar.topico = topico;
    }

    public static String getTopico() {
        return topico;
    }

    public static void setTopico(String topico) {
        ActorRenovar.topico = topico;
    }

    public static void main(String[] args) throws Exception {
        
         // Suscribir el actor por default al topico Renovacion
         setTopico("Renovacion");
        
         try (ZContext context = new ZContext()) {

            // Conexion con un puerto para atender las publicaciones asociadas al topico
            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
            subscriber.connect("tcp://10.43.100.136:5556");
            subscriber.subscribe(getTopico().getBytes(ZMQ.CHARSET)); // Suscribirse al topico para recibir mensajes del publicador
            
            boolean actualizacion;
            // Leer las publicacion que va colocando el pulblicador
            while (!Thread.currentThread().isInterrupted()) 
            {   
                // Se recibe el mensaje publicado por el gestor de carg
                String respuesta = subscriber.recvStr();
                
                //Guardar la informacion de la solicitud
                String [] elem = respuesta.split(" ");
                String topico = elem[0];
                int codigo = Integer.parseInt(elem[1]);
                String fechaActual = elem[2];
                String fechaEntrega = elem[3];
                
                // Convertir el string recibido a una fecha
                SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
                Date nuevaFecha = formatoFecha.parse(fechaEntrega);

                System.out.println("\nActor suscrito al topico RENOVACION tomando requerimiento con informacion: ");
                System.out.println("Tipo de operacion: " + topico);
                System.out.println("Codigo del prestamo: " + codigo);
                System.out.println("Fecha actual del prestamo: " + fechaActual);
                System.out.println("Nueva fecha de entrega del prestamo: " + fechaEntrega);

                System.out.println("\nRealizando actualizacion en la base de datos...");
                System.out.println("////////////////////////////////////////////////////////////\n");
                
                // Hacer la actualizacion en la base de datos
                actualizacion = FuncionesPrestamos.OperacionRenovacion(codigo, nuevaFecha);
                
                // Informar si se actualizo la base de datos
                
                if(actualizacion == true){
                    System.out.println("-----------------------------------------------------------");
                    System.out.println("El prestamo con codigo " + codigo +" se renovo exitosamente!");
                    System.out.println("-----------------------------------------------------------\n");
                }
                else{
                    System.out.println("-----------------------------------------------------------");
                    System.out.println("No se pudo renovar el prestamo con codigo " + codigo);
                    System.out.println("-----------------------------------------------------------\n");
                }
                
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } 
    }
}
