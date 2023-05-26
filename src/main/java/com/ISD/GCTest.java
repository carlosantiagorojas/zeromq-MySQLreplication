package com.ISD;

import org.zeromq.ZMQ;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class GCTest 
{
    public void iniciar() throws Exception {

        GC gestor = new GC();
        
         // Inicio el gestor
         try (ZContext context = new ZContext()){

            System.out.println("------------------INICIANDO GESTOR SEDE 1----------------");
           
            gestor.setContext(context);
            
            ZMQ.Socket socket = gestor.getContext().createSocket(SocketType.REP);
            ZMQ.Socket publisher = gestor.getContext().createSocket(SocketType.PUB);
            ZMQ.Socket socketSolicitar = gestor.getContext().createSocket(SocketType.REQ);

            gestor.setSocket(socket);
            gestor.setPublisher(publisher);
            gestor.setSocketSolicitar(socketSolicitar);

            // Vincular con el puerto local para recibir las request de procesos solicitantes
            gestor.getSocket().bind("tcp://10.43.100.136:5555");
            
            // Vincular con un puerto para publicar los topicos
            gestor.getPublisher().bind("tcp://10.43.100.136:5556");

            // Conexion con un puerto para la operacion de solicitar
            gestor.getSocketSolicitar().connect("tcp://10.43.100.136:5557");
            
            // Se fuerza a fallar el gestor despues de T tiempo de inicio (se configura en milisegundos)

            final int TiempoT = 1000;
            final ZContext cZContext = gestor.getContext();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {;
                    cZContext.close();
                }
            }, TiempoT);
            
            
            // Variables auxiliares para recibir valores
            String respuesta;
            String topico;
            
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
                if(accion.equalsIgnoreCase("devolver")){
                    
                    respuesta = "\nBibilioteca recibiendo libro con codigo " + codigo + " desde la sede "+ sede + "...\n";
                    
                    // Aceptar de forma inmediata la operacion y devolver una respusesta positva
                    System.out.println("Devolviendo respuesta al proceso solicitante...");
                    System.out.println("////////////////////////////////////////////////////////////\n");
                    gestor.getSocket().send(respuesta.getBytes(ZMQ.CHARSET), 0);
                    
                    // Publicar informacion del requerimiento
                    topico = "Devolucion";
                    gestor.getPublisher().send(topico + " " + codigo); //Se envia: topico, codigo 
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
                    System.out.println("////////////////////////////////////////////////////////////\n");
                    gestor.getSocket().send(respuesta.getBytes(ZMQ.CHARSET), 0);
                    
                    // Publicar informacion del requerimiento
                    topico = "Renovacion";
                    gestor.getPublisher().send(topico + " " + codigo + " " + fechaActual + " " + fechaEntrega); // Se envia: topico, codigo, fecha actual, fecha nueva de entrega 
                }
                else if(accion.equalsIgnoreCase("solicitar"))
                {
                    // Asignarle el trabajo al ActorSolicitar
                    gestor.getSocketSolicitar().send(solicitud.getBytes(ZMQ.CHARSET), 0);

                    // Se obtiene la respuesta del actorSolicitar
                    byte[] replySolicitar = gestor.getSocketSolicitar().recv(0);
                    System.out.println("Mensaje recibido del actor: " + new String(replySolicitar, ZMQ.CHARSET));
                    
                    // Enviar la respuesta al proceso solicitante 
                    System.out.println("Devolviendo respuesta al proceso solicitante...");
                    System.out.println("////////////////////////////////////////////////////////////\n");
                    String respuestaSolicitante = new String(replySolicitar, ZMQ.CHARSET);
                    respuestaSolicitante = respuestaSolicitante + " desde la sede "+ sede +"\n";
                    gestor.getSocket().send(respuestaSolicitante.getBytes(ZMQ.CHARSET), 0); // Se envia la respuesta al proceso solicitante
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
            gestor.getPublisher().close();
            gestor.getSocketSolicitar().close(); 
            gestor.getContext().close();  
            
            //Se levanta el proceso homologo
            procesoHomologo(gestor);

        }
        catch (org.zeromq.ZMQException e) {

            // Se informa la falla del gestor
            System.out.println();
            System.err.println("\nFalla del gestor, excepción de ZeroMQ: " + e.getMessage());
            e.printStackTrace();
            System.out.println();
          
            // Se cierran los sockets y el contexto para liberar recursos
            gestor.getSocket().close();
            gestor.getPublisher().close();
            gestor.getSocketSolicitar().close(); 
            gestor.getContext().close();   

            //Se levanta el proceso homologo
            procesoHomologo(gestor);
        }
    }

 
    public static void procesoHomologo(GC gestor){

        System.out.println("-------------------------------------------------------");
        System.out.println("--------------LEVANTANDO PROCESO HOMOLOGO--------------");
        System.out.println("-------------------------------------------------------\n");
        
        //Se crea el nuevo proceso homologo
        ProcesoHomologo nuevoPros = new ProcesoHomologo();
        try {
            nuevoPros.iniciar(gestor);
        } catch (InterruptedException e) {
            System.err.println("\nFalla del proceso homologo, " + e.getMessage());
            e.printStackTrace();
        }
    }
}