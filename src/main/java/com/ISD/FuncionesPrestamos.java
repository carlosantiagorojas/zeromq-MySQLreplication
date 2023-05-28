package com.ISD;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class FuncionesPrestamos{

    public final static EntityManagerFactory EnManFac = Persistence.createEntityManagerFactory("PrestamoPersistenceUnit");

    public final static EntityManager EnMan = EnManFac.createEntityManager();


    public static boolean OperacionRenovacion(int id, Date fecha) {

        Prestamos prestamo = EnMan.find(Prestamos.class, id);

        if (prestamo == null) {
            System.out.println();
            System.out.println("Informe....");
            System.out.println("El prestamo con codigo " + id + " que quiere renovar no se encuentra prestado");
            System.out.println();
            //EnMan.close();
            //EnManFac.close();

            return false;
        }

        prestamo.setFechaRenovacion(fecha);

        EnMan.getTransaction().begin();
        EnMan.merge(prestamo);
        EnMan.getTransaction().commit();
        //EnMan.close();
        //EnManFac.close();
        
        return true;
    }

    public static boolean EliminarPrestamo(int id) {

        Prestamos prestamo = EnMan.find(Prestamos.class, id);

        if (prestamo == null) {

            System.out.println();
            System.out.println("Informe....");
            System.out.println("El prestamo con codigo " + id + " que quiere devolver no se encuentra prestado");
            System.out.println();
            //EnMan.close();
            //EnManFac.close();

            return false;
        }

        EnMan.getTransaction().begin();
        EnMan.remove(prestamo);
        EnMan.getTransaction().commit();
        //EnMan.close();
        //EnManFac.close();

        return true;
    }


    public static boolean CrearPrestamo(int libro_ISBN) throws SQLIntegrityConstraintViolationException {
        
        Libro libro = EnMan.find(Libro.class, libro_ISBN);
        if(libro != null)
        {
            Prestamos prestamo; 
            int numeroAleatorio;
        
            // Crear un id aleatorio que no este repetido
            do
            {
                Random rand = new Random();
                numeroAleatorio = rand.nextInt(300) + 1;
                prestamo = EnMan.find(Prestamos.class, numeroAleatorio);

            }while(prestamo != null);

            // Generar la nueva fecha
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DATE, 7);
            Date fechaNueva = c.getTime();

            prestamo = new Prestamos(numeroAleatorio, fechaNueva, libro_ISBN, 1);
            
            EnMan.getTransaction().begin();
            EnMan.persist(prestamo);
            EnMan.getTransaction().commit();

            //EnMan.close();
            //EnManFac.close();
            return true;
        }
        else
            return false;
    
    }

}