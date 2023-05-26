package com.ISD;

import org.zeromq.ZMQ;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class GCTestDos {
    
    public void iniciar() throws Exception {

        GC gestor = new GC();
        
         // Inicio el gestor
         try (ZContext context = new ZContext()){

            System.out.println("------------------INICIANDO GESTOR SEDE 2----------------");
           
            gestor.setContext(context);
            
            ZMQ.Socket socket = gestor.getContext().createSocket(SocketType.REP);
            ZMQ.Socket socketReenviar = gestor.getContext().createSocket(SocketType.REQ);

            gestor.setSocket(socket);
            gestor.setSocketSolicitarPH(socketReenviar);

            // Vincular con el puerto local para recibir las request de procesos solicitantes
            gestor.getSocket().bind("tcp://10.43.100.141:5555");
            
            //Conexion con un puerto para reenviar los mensajes a la sede 1
            gestor.getSocketSolicitarPH().connect("tcp://10.43.100.136:5555");

            // Se configura cuanto tiempo va a esperar por la respuesta antes de hacer el reintento
            gestor.getSocketSolicitarPH().setReceiveTimeOut(5000);

            // Se fuerza a fallar el gestor despues de T tiempo de inicio (se configura en milisegundos)
            /*
            final int TiempoT = 2000;
            final ZContext cZContext = gestor.getContext();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {;
                    cZContext.close();
                }
            }, TiempoT);
            */
           
            while (!Thread.currentThread().isInterrupted()) 
            {   
                // Se recibe el mensaje del proceso solicitante
               
                byte[] reply = gestor.getSocket().recv(0);
                String solicitud = new String(reply, ZMQ.CHARSET); // Se genera un string con la solicitud con formato: accion, codigo                

                // Se guarda la informacion de la solicitud
                String[] elemSolicitud = solicitud.split(" ");
                String accion = elemSolicitud[0];
                int codigo = Integer.parseInt(elemSolicitud[1]);
                int sede = Integer.parseInt(elemSolicitud[2]);
                
                // Informe de la solicitud
                System.out.println("\n////////////////////////////////////////////////////////////");
                System.out.println("Mensaje del proceso solicitante: operacion - " + accion + ", codigo - " + codigo + ", sede - " + sede);
    
                // Dependiendo de la solicitud se realizara una accion
                if(accion.equalsIgnoreCase("devolver") || accion.equalsIgnoreCase("renovar") || accion.equalsIgnoreCase("solicitar") ){
                    
                    // enviar mensaje para designarle el trabajo al gestor de la sede 1
                    gestor.getSocketSolicitarPH().send(solicitud.getBytes(ZMQ.CHARSET), 0);

                    // Se obtiene la respuesta del actorSolicitar
                    byte[] replySolicitar = gestor.getSocketSolicitarPH().recv(0);
                    if (replySolicitar == null) {

                        // Si no se recibe la respuesta
                        System.out.println("No se recibió respuesta del gestor de la sede 1");
                        socket.close(); // cerrar el socket antes de reconectarlo
                        socket = context.createSocket(SocketType.REQ);
        
                        socket.connect("tcp://10.43.100.136:5555"); // reconectar el socket
  
                        // Esperar un tiempo mientras se levanta el proceso homologo
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        
                        // Reenviar la solicitud
                        if(socket.send(solicitud.getBytes(ZMQ.CHARSET), 0)){
                            System.out.println("Reenviando solicitud...");
                        }
                        else
                            System.out.println("No se pudo reenviar la solicitud");   
        
                    } else {

                        // Se obtiene la respuesta del gestor
                        System.out.println("\nMensaje recibido del gestor de la sede 1: " + new String(replySolicitar, ZMQ.CHARSET));
                        
                        // Enviar la respuesta al proceso solicitante 
                        System.out.println("Devolviendo respuesta al proceso solicitante...");
                        System.out.println("////////////////////////////////////////////////////////////\n");
                        String respuestaSolicitante = new String(replySolicitar, ZMQ.CHARSET);
                        gestor.getSocket().send(respuestaSolicitante.getBytes(ZMQ.CHARSET), 0); // Se envia la respuesta al proceso solicitante
                        }
                   }
                else{
                    System.out.println("Operacion no soportada, las operaciones validas son: ");
                    System.out.println("devolver,<codigo del prestamo>,<numero de la sede>");
                    System.out.println("renovar,<codigo del prestamo>,<numero de la sede>");
                    System.out.println("solicitar,<codigo del libro>,<numero de la sede>");
                }
                
                Thread.sleep(1000); // Tiempo de espera para hacer el trabajo 
            }

        } catch (InterruptedException e) {
            
            // Se informa la falla del gestor
            System.out.println();
            System.out.println("\nFalla del gestor: ");
            e.printStackTrace();
            System.out.println();

            // Se cierran los sockets y el contexto para liberar recursos
            gestor.getSocket().close();
            gestor.getContext().close();  
            
            //Se levanta el proceso homologo
            procesoHomologoDos(gestor);

        }
        catch (org.zeromq.ZMQException e) {

            // Se informa la falla del gestor
            System.out.println();
            System.err.println("\nFalla del gestor, excepción de ZeroMQ: " + e.getMessage());
            e.printStackTrace();
            System.out.println();
          
            // Se cierran los sockets y el contexto para liberar recursos
            gestor.getSocket().close(); 
            gestor.getContext().close();   

            //Se levanta el proceso homologo
            procesoHomologoDos(gestor);
        }
    }

 
    public static void procesoHomologoDos(GC gestor){

        System.out.println("-------------------------------------------------------");
        System.out.println("--------------LEVANTANDO PROCESO HOMOLOGO--------------");
        System.out.println("-------------------------------------------------------\n");
        
        //Se crea el nuevo proceso homologo
        ProcesoHomologoDos nuevoPros = new ProcesoHomologoDos();
        try {
            nuevoPros.iniciar(gestor);
        } catch (InterruptedException e) {
            System.err.println("\nFalla del proceso homologo, " + e.getMessage());
            e.printStackTrace();
        }
    }
}
