/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;

/**
 *
 * @author ASUS
 */
import graphql.*;
import graphql.schema.idl.*;
import java.io.*;
import java.util.Objects;
import graphql.schema.GraphQLSchema;

import com.mycompany.mavenproject3.ProductDAO;


public class ProductConfig {
    public static void register(RuntimeWiring.Builder builder, ProductDAO productDAO) {
        
        builder.type("Query", typeWiring -> typeWiring
            .dataFetcher("allProducts", env -> productDAO.getAllProducts())
            .dataFetcher("productById", env -> {
                int id = Integer.parseInt(env.getArgument("id").toString());
                return productDAO.findById(id);
            })
        )
        
        .type("Mutation", typeWiring -> typeWiring
            .dataFetcher("addProduct", env -> {
                Product p = new Product(
                    0,
                    env.getArgument("code"),
                    env.getArgument("name"),
                    env.getArgument("category"),
                    ((Number) env.getArgument("price")).doubleValue(),
                    (int) env.getArgument("stock")
                );
                AuditInfo audit = new AuditInfo();
                audit.setCreatedBy("admin_web");
                p.setAuditInfo(audit);
                return productDAO.insertProduct(p);
            })

            .dataFetcher("updateProduct", env -> {
                Product p = new Product(
                    Integer.parseInt(env.getArgument("id").toString()),
                    env.getArgument("code"),
                    env.getArgument("name"),
                    env.getArgument("category"),
                    ((Number) env.getArgument("price")).doubleValue(),
                    (int) env.getArgument("stock")
                );
                AuditInfo audit = new AuditInfo();
                audit.setEditedBy("admin_web");
                p.setAuditInfo(audit);
                return productDAO.updateProduct(p);
            })

            .dataFetcher("deleteProduct", env -> {
                int id = Integer.parseInt(env.getArgument("id").toString());
                return productDAO.deleteProduct(id);
            })
        );
    }
}


