package com.ISD;

import java.sql.SQLIntegrityConstraintViolationException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class FuncionesLibro{

    public final static EntityManagerFactory EnManFac = Persistence.createEntityManagerFactory("LibroPersistenceUnit");

    public final static EntityManager EnMan = EnManFac.createEntityManager();

    public static boolean ConsultarLibro(int ISBN) {

        Libro Libro = EnMan.find(Libro.class, ISBN);

        if(Libro != null){
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean OperacionDevolucion(int id) {

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

        Libro libro = EnMan.find(Libro.class, prestamo.getLibro_ISBN());

        if(FuncionesPrestamos.EliminarPrestamo(id) != false){

            EnMan.getTransaction().begin();

            libro.setCantidad(libro.getCantidad() + 1);

            EnMan.merge(libro);
            EnMan.getTransaction().commit();
            //EnMan.close();
            //EnManFac.close();;

            return true;
        }

        return false;
    }

    public static boolean OperacionSolicitar(int ISBN) {

        try {
            if(FuncionesPrestamos.CrearPrestamo(ISBN) == true){
            
                Libro libro = EnMan.find(Libro.class, ISBN);
                if(libro.getCantidad() < 1)
                {      
                    System.out.println();
                    System.out.println("Informe....");
                    System.out.println("No quedan mas ejemplares del libro con codigo "+ ISBN +" que solicito");
                    System.out.println();
                    //EnMan.close();
                    //EnManFac.close();
                    return false;
                }
                else
                {
                    libro.setCantidad(libro.getCantidad() - 1);

                    EnMan.getTransaction().begin();
                    EnMan.persist(libro);
                    EnMan.getTransaction().commit();
                    //EnMan.close();
                    //EnManFac.close();

                    return true;
                }

            }
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        }

        return false;
    }
}