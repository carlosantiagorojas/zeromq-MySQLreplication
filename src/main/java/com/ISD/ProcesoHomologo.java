package com.ISD;

import org.zeromq.ZMQ;
import org.zeromq.SocketType;
import org.zeromq.ZContext;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ProcesoHomologo{

    public void iniciar(GC gestor) throws InterruptedException
    {
        
        ZContext context = new ZContext();

        System.out.println("------------------INICIANDO GESTOR----------------");
        // Vincular con el puerto de los procesos solicitantes
        ZMQ.Socket socket = context.createSocket(SocketType.REP);
        // puerto de los procesos solicitantes
        // socket.bind("tcp://10.43.100.136:5555");
        //puerto local 
        socket.bind("tcp://10.43.100.136:5555");

        // Vincular con un puerto para publicar los topicos
        ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
        publisher.bind("tcp://10.43.100.136:5556");
        
        // Conexion con un puerto para la operacion de solicitar
        ZMQ.Socket socketSolicitar = context.createSocket(SocketType.REQ);
        socketSolicitar.connect("tcp://10.43.100.136:5557");

        // Inicio el gestor
        try {
            
            System.out.println("entre aqui");
            // Variables auxiliares para recibir valores
            String respuesta;
            String topico;
            
            while (!Thread.currentThread().isInterrupted()) 
            {   
                System.out.println("entre while ..... ");
                // Se recibe el mensaje del proceso solicitante
                byte[] reply = socket.recv(0);
                String solicitud = new String(reply, ZMQ.CHARSET); // Se genera un string con la solicitud con formato: accion, codigo                
                
                System.out.println("llego la solicitud: " + solicitud);

                // Se guarda la informacion de la solicitud
                String[] elemSolicitud = solicitud.split(" ");
                String accion = elemSolicitud[0];
                int codigo = Integer.parseInt(elemSolicitud[1]);
                int sede = Integer.parseInt(elemSolicitud[2]);

                System.out.println("\n////////////////////////////////////////////////////////////");
                System.out.println("Mensaje del proceso solicitante: operacion - " + accion + ", codigo - " + codigo + ", sede - " + sede);

                // Dependiendo de la solicitud se realizara una accion
                if(accion.equalsIgnoreCase("devolver")){
                    
                    respuesta = "\nBibilioteca recibiendo libro con codigo " + codigo + " desde la sede "+ sede + "...\n";
                    
                    // Aceptar de forma inmediata la operacion y devolver una respusesta positva
                    System.out.println("Devolviendo respuesta al proceso solicitante...");
                    socket.send(respuesta.getBytes(ZMQ.CHARSET), 0);
                    
                    // Publicar informacion del requerimiento
                    topico = "Devolucion";
                    publisher.send(topico + " " + codigo); //Se envia: topico, codigo 
                }
                else if(accion.equalsIgnoreCase("renovar")){

                    // Configurar el formato de la fecha
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    // Obtener la fecha actual
                    Calendar c = Calendar.getInstance();
                    Calendar a = Calendar.getInstance();

                    // Aplicar la fecha
                    c.setTime(new Date());
                    a.setTime(new Date());
                    c.add(Calendar.DATE, 7); // Añadir la nueva fecha de entrega (1 semana apartir de la fecha actual)

                    // Guardar en una cadena de caracteres la fecha
                    String fechaEntrega = sdf.format(c.getTime());
                    String fechaActual = sdf.format(a.getTime());

                    respuesta = "\nBiblioteca renovando el prestamo con codigo " + codigo + " desde la sede "+ sede + "...\nLa fecha actual es: " + fechaActual + "\nLa nueva fecha de entrega es: " + fechaEntrega +"\n";
                    
                    // Aceptar de forma inmediata la operacion y devolver una respusesta positva
                    System.out.println("Devolviendo respuesta al proceso solicitante...");
                    socket.send(respuesta.getBytes(ZMQ.CHARSET), 0);
                    
                    // Publicar informacion del requerimiento
                    topico = "Renovacion";
                    publisher.send(topico + " " + codigo + " " + fechaActual + " " + fechaEntrega); // Se envia: topico, codigo, fecha actual, fecha nueva de entrega 
                }
                else if(accion.equalsIgnoreCase("solicitar"))
                {
                    // Asignarle el trabajo al ActorSolicitar
                    socketSolicitar.send(solicitud.getBytes(ZMQ.CHARSET), 0);

                    // Se obtiene la respuesta del actorSolicitar
                    byte[] replySolicitar = socketSolicitar.recv(0);
                    System.out.println("Mensaje recibido del actor: " + new String(replySolicitar, ZMQ.CHARSET) + " desde la sede "+ sede);
                    
                    // Enviar la respuesta al proceso solicitante 
                    System.out.println("Devolviendo respuesta al proceso solicitante...");
                    System.out.println("////////////////////////////////////////////////////////////\n");
                    String respuestaSolicitante = new String(replySolicitar, ZMQ.CHARSET);
                    respuestaSolicitante = respuestaSolicitante + " desde la sede "+ sede +"\n";
                    socket.send(respuestaSolicitante.getBytes(ZMQ.CHARSET), 0); // Se envia la respuesta al proceso solicitante
                }
                else{
                    System.out.println("Operacion no soportada, las operaciones validas son: ");
                    System.out.println("devolver,<codigo del prestamo>,<numero de la sede>");
                    System.out.println("renovar,<codigo del prestamo>,<numero de la sede>");
                    System.out.println("solicitar,<codigo del libro>,<numero de la sede>");
                }
                
                 // Configurar el tiempo de espera
                Thread.sleep(1000); // Tiempo de espera para hacer el trabajo 
            }

        } catch (InterruptedException e) {
            System.out.println("\nFalla del gestor: ");
            e.printStackTrace();

            socket.close();
            publisher.close();
            socketSolicitar.close();
            context.close();
            
          
        }
        catch (org.zeromq.ZMQException e) {
            System.err.println("\nFalla del gestor, excepción de ZeroMQ: " + e.getMessage());

            socket.close();
            publisher.close();
            socketSolicitar.close();
            context.close();
        }
    }

    
}
