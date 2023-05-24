package com.ISD;

import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ProcesoHomologo{

    private GC gestor;
    
    public GC getGestor() {
        return gestor;
    }

    public void setGestor(GC gestor) {
        this.gestor = gestor;
    }

    public ProcesoHomologo(GC gestor) {
        this.gestor = gestor;
    }

    public void iniciarProcesoHomologo() {

         // Inicio el gestor
         try (ZContext context = new ZContext()){


            String respuesta;
            String topico;
            
            while (!Thread.currentThread().isInterrupted()) 
            {   
                System.out.println("entre while ..... ");
                // Se recibe el mensaje del proceso solicitante
                byte[] reply = this.gestor.getSocket().recv(0);
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
                    this.gestor.getSocket().send(respuesta.getBytes(ZMQ.CHARSET), 0);
                    
                    // Publicar informacion del requerimiento
                    topico = "Devolucion";
                    this.gestor.getPublisher().send(topico + " " + codigo); //Se envia: topico, codigo 
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
                    this.gestor.getSocket().send(respuesta.getBytes(ZMQ.CHARSET), 0);
                    
                    // Publicar informacion del requerimiento
                    topico = "Renovacion";
                    this.gestor.getPublisher().send(topico + " " + codigo + " " + fechaActual + " " + fechaEntrega); // Se envia: topico, codigo, fecha actual, fecha nueva de entrega 
                }
                else if(accion.equalsIgnoreCase("solicitar"))
                {
                    // Asignarle el trabajo al ActorSolicitar
                    this.gestor.getSocketSolicitar().send(solicitud.getBytes(ZMQ.CHARSET), 0);

                    // Se obtiene la respuesta del actorSolicitar
                    byte[] replySolicitar = this.gestor.getSocketSolicitar().recv(0);
                    System.out.println("Mensaje recibido del actor: " + new String(replySolicitar, ZMQ.CHARSET) + " desde la sede "+ sede);
                    
                    // Enviar la respuesta al proceso solicitante 
                    System.out.println("Devolviendo respuesta al proceso solicitante...");
                    System.out.println("////////////////////////////////////////////////////////////\n");
                    String respuestaSolicitante = new String(replySolicitar, ZMQ.CHARSET);
                    respuestaSolicitante = respuestaSolicitante + " desde la sede "+ sede +"\n";
                    this.gestor.getSocket().send(respuestaSolicitante.getBytes(ZMQ.CHARSET), 0); // Se envia la respuesta al proceso solicitante
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
            
            System.out.println();
            System.out.println("\nFalla del gestor: ");
            e.printStackTrace();
            System.out.println();

            this.gestor.getContext().close();
            this.gestor.getSocket().close();
            this.gestor.getPublisher().close();
            this.gestor.getSocketSolicitar().close();    
           
        }
        catch (org.zeromq.ZMQException e) {

            System.out.println();
            System.err.println("\nFalla del gestor, excepción de ZeroMQ: " + e.getMessage());
            e.printStackTrace();
            System.out.println();

            this.gestor.getContext().close();
            this.gestor.getSocket().close();
            this.gestor.getPublisher().close();
            this.gestor.getSocketSolicitar().close();   
        }
    }
}
