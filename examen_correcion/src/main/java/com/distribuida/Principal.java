package com.distribuida;
import com.distribuida.db.Book;
import com.distribuida.rest.BookRest;
import com.distribuida.servicios.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

import static spark.Spark.*;
public class Principal {

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
    static SeContainer container;

    public static void main(String[] args) {
        container = SeContainerInitializer
                .newInstance()
                .initialize();

        port(8080);

        configureCors();

        BookService servicio = container.select(BookService.class)
                .get();

        BookRest bookRest = new BookRest(servicio);

        ObjectMapper mapper = new ObjectMapper();

        get("/books", bookRest::findAll, mapper::writeValueAsString);
        get("/books/:id", bookRest::findById, mapper::writeValueAsString);
        post("/books", bookRest::insert, mapper::writeValueAsString);
        put("/books/:id", bookRest::update, mapper::writeValueAsString);
        delete("/books/:id", bookRest::remove, mapper::writeValueAsString);

    }
}
