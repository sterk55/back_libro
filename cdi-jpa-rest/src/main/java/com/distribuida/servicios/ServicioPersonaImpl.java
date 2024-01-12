package com.distribuida.servicios;

import com.distribuida.db.Persona;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class ServicioPersonaImpl implements ServicioPersona {
    @Inject
    EntityManager em;

    @Override
    public List<Persona> findAll() {
        return em.createQuery("select o from Persona o")
                .getResultList();
    }

    public Persona findById(Integer id) {

        return em.find(Persona.class, id);
    }

    @Override
    public boolean borrar(Integer id) {
        var tx = em.getTransaction();
        try {
            tx.begin();

            Persona persona = findById(id);

            if (persona != null) {
                // Si la persona existe, elimínala
                em.remove(persona);
                // Completar la transacción si la eliminación fue exitosa
                tx.commit();
                return true;
            } else {
                System.out.println("La persona con ID " + id + " no fue encontrada.");
                // Revertir la transacción si la persona no fue encontrada
                tx.rollback();
                return false;
            }
        } catch (Exception ex) {
            // Revertir la transacción en caso de error
            tx.rollback();
            // Retornar false indicando que la operación no fue exitosa
            return false;
        }
    }


    @Override
    public boolean actualizar(Persona persona) {
        var tx = em.getTransaction();

        try {
            tx.begin();
            em.merge(persona);
            tx.commit();
            // Retorna true indicando que la actualización fue exitosa
            return true;
        } catch(Exception ex) {
            tx.rollback();
            // Retorna false indicando que ocurrió un error durante la actualización
            return false;
        }
    }


    public void insert(Persona p) {
        var tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(p);
            tx.commit();
        }
        catch(Exception ex) {
            tx.rollback();
        }
    }
}
