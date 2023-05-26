package com.ISD;

import java.util.HashMap;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class PS {
    
    private HashMap<String, Integer> requerimiento = new HashMap<>();
    private int sede;

    public PS(){

    }

    public PS(String accion, Integer codigo, Integer sede){
        requerimiento.put(accion, codigo);
        this.sede = sede;
    }

    public HashMap<String, Integer> getRequerimiento() {
        return requerimiento;
    }

    public void setRequerimiento(HashMap<String, Integer> requerimiento) {
        this.requerimiento = requerimiento;
    }

    public int getSede() {
        return sede;
    }

    public void setSede(int sede) {
        this.sede = sede;
    }
    
    public void inicio(String rutaArchivo)
    {
        try {

            File file = new File(rutaArchivo);
            Scanner scanner = new Scanner(file);
            //Se lee la linea del archivo 
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                String[] elementos = linea.split(",");
                
                //Se guarda la informacion del requerimiento
                String accion = elementos[0];
                Integer codigo = Integer.parseInt(elementos[1]);
                Integer sede = Integer.parseInt(elementos[2]);
                
                // Se instancia un proceso para mandar la solicitud al gestor
                PS procesoSolicitante = new PS(accion, codigo, sede);

                procesoSolicitante.operacion(accion);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado " + e.getMessage());
        }
    }


    public void operacion(String accion){
        try (ZContext context = new ZContext()) {

            // Conexion con un puerto para enviar la solcitud al gestor de carga
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);

            // Dependiendo de la sede a la que va dirigido el requerimiento se conecta a esa seede
            if(getSede() == 1)
                socket.connect("tcp://10.43.100.136:5555"); // sede 1
            else
                socket.connect("tcp://10.43.100.141:5555"); // sede 2

            // Se configura cuanto tiempo va a esperar por la respuesta antes de hacer el reintento
            socket.setReceiveTimeOut(5000);

            // Se obtiene el codigo de acuerdo a la operacion
            int codigo = requerimiento.get(accion);

            String request = "";
            
            if(accion.equals("solicitar")){
                System.out.println("\n////////////////////////////////////////////////////////////");
                request = "solicitar " + codigo + " " + getSede(); // Se genera la solicitud con formato: accion, codigo del libro
                System.out.println("\nEnviando solicitud para el prestamo del libro con el codigo " + codigo + " a la sede "+ getSede());
                socket.send(request.getBytes(ZMQ.CHARSET), 0); // Se envia la solcitud

            }else if (accion.equals("renovar")){
                System.out.println("\n////////////////////////////////////////////////////////////");
                request = "renovar " + codigo + " " + getSede(); // Se genera la solicitud con formato: accion, codigo
                System.out.println("\nEnviando solicitud de renovacion con codigo " + codigo + " a la sede "+ getSede());
                socket.send(request.getBytes(ZMQ.CHARSET), 0); // Se envia la solcitud

            }else if (accion.equals("devolver")){
                System.out.println("\n////////////////////////////////////////////////////////////");
                request = "devolver " + codigo + " " + getSede(); // Se genera la solicitud con formato: accion, codigo
                System.out.println("\nEnviando solicitud de devolucion con codigo " + codigo + " a la sede "+ getSede());
                socket.send(request.getBytes(ZMQ.CHARSET), 0); // Se envia la solcitud
            }

            // Recibar por alguna respuesta
            byte[] reply = socket.recv(0);
            if (reply == null) {

                // Si no se recibe la respuesta
                System.out.println("No se recibió respuesta del gestor");
                socket.close(); // cerrar el socket antes de reconectarlo
                socket = context.createSocket(SocketType.REQ);

                if(getSede() == 1)
                    socket.connect("tcp://10.43.100.136:5555"); // reconectar el socket de la sede 1
                else
                    socket.connect("tcp://10.43.100.141:5555"); // reconectar el socket de la sede 2

                // Esperar un tiempo mientras se levanta el proceso homologo
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                // Reenviar la solicitud
                if(socket.send(request.getBytes(ZMQ.CHARSET), 0))
                    System.out.println("Reenviando solicitud...");
                else
                    System.out.println("No se pudo reenviar la solicitud");   

            } else {
                // Se obtiene la respuesta del gesto
                System.out.println("\nMensaje recibido del gestor: " + new String(reply, ZMQ.CHARSET));
            }
        }   
    }

}