package com.distribuida;

import com.distribuida.db.Persona;
import com.distribuida.servicios.ServicioPersona;
import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import jakarta .inject.Inject;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.List;

import static spark.Spark.*;

public class Principal {
    static SeContainer container;

    static List<Persona> listarPersonas(Request req, Response res) {
        var servicio = container.select(ServicioPersona.class)
                .get();
        res.type("application/json");

        return servicio.findAll();
    }

    static Persona buscarPersona(Request req, Response res) {
        var servicio = container.select(ServicioPersona.class)
                .get();
        res.type("application/json");

        String _id = req.params(":id");

        var persona =  servicio.findById(Integer.valueOf(_id));

        if(persona==null) {
            // 404
            halt(404, "Persona no encontrada");
        }

        return persona;
    }

    public static Boolean actualizarPersona(Request req, Response res) {

        var servicio=container.select(ServicioPersona.class).get();
        res.type("application/json");
        String _id=req.params(":id");
        String requestBody=req.body();

        Gson gson= new Gson();
        Persona persona=gson.fromJson(requestBody,Persona.class);
        persona.setId(Integer.valueOf(_id));
        servicio.actualizar(persona);

        if(persona==null){
            halt(404, "Persona no encontrada");
        }
        return  true;
    }

    public static Boolean eliminarPersona(Request req, Response res) {
        res.type("application/json");

        // Obtener el ID de la persona de los parámetros de la solicitud
        String idStr = req.params(":id");
        Integer id = Integer.valueOf(idStr); // Convertir el ID a Integer

        var servicio = container.select(ServicioPersona.class).get();

        // Llamar al método del servicio para eliminar la persona
        boolean eliminado = servicio.borrar(id);

        if (eliminado) {
            // Si la persona fue eliminada exitosamente
            return true;
        } else {
            // Si no se pudo eliminar la persona (por ejemplo, si la persona no existe)
            res.status(404);
            return false;
        }
    }

    static boolean crearPersona(Request req, Response res) {
        var servicio = container.select(ServicioPersona.class).get();
        res.type("application/json");
        Gson gson = new Gson();
        var nuevaPersona = gson.fromJson(req.body(), Persona.class);
        servicio.insert(nuevaPersona);


        return true;
    }

    //Solucionar el cross con vue
    static void configureCors() {
        before((request, response) -> {
            // Configuración CORS
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept");
            response.header("Access-Control-Allow-Credentials", "true");
        });

        options("/*", (request, response) -> {
            response.status(200);
            return "OK";
        });
    }

    public static void main(String[] args) {
        container = SeContainerInitializer
                .newInstance()
                .initialize();

        //ServicioPersona servicio = container.select(ServicioPersona.class)
        //        .get();

        port(8080);
        configureCors();

//        //--
//        Persona p = new Persona();
//        p.setId(1);
//        p.setNombre("nombre1");
//        p.setDireccion("direccion1");
//        p.setEdad(1);
//
//        servicio.insert( p );
//
//        p = new Persona();
//        p.setId(2);
//        p.setNombre("nombre2");
//        p.setDireccion("direccion2");
//        p.setEdad(2);
//
//        servicio.insert( p );
//        //--

        //get("/hello", (req, res) -> "Hello World");



        Gson gson = new Gson();
        get("/personas", Principal::listarPersonas, gson::toJson);
        get("/personas/:id", Principal::buscarPersona, gson::toJson);
       // get("/personas/eliminar/:id", Principal::eliminarPersona, gson::toJson);
        post("/personas/insertar",Principal::crearPersona, gson::toJson);
        delete("/personas/eliminar/:id", Principal::eliminarPersona, gson::toJson);
        put("/personas/actualizar/:id", Principal::actualizarPersona, gson::toJson);



    }

}
