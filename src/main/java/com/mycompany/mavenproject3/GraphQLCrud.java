/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;

/**
 *
 * @author ASUS
 */
import static spark.Spark.*;
import com.google.gson.*;
import graphql.*;

public class GraphQLCrud {

   public static void main(String[] args) throws Exception {
       GraphQL graphql = GraphQLConfig.init();
       Gson gson = new Gson();

       port(4576);
       post("/graphql", (req, res) -> {
           res.type("application/json");

           JsonObject request = gson.fromJson(req.body(), JsonObject.class);
           String query = request.get("query").getAsString();

           ExecutionResult result = graphql.execute(query);
           return gson.toJson(result.toSpecification());
       });
   }
}