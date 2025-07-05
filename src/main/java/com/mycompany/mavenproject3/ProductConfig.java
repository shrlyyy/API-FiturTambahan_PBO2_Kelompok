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

import com.mycompany.mavenproject3.ProductRepository;


public class ProductConfig {

    public static RuntimeWiring buildWiring(ProductRepository productRepo) {
        return RuntimeWiring.newRuntimeWiring()
            .type("Query", typeWiring -> typeWiring
                .dataFetcher("allProducts", env -> productRepo.findAll())
                .dataFetcher("productById", env -> {
                    int id = Integer.parseInt(env.getArgument("id").toString());
                    return productRepo.findById(id);
                })
            )

            .type("Mutation", typeWiring -> typeWiring
                .dataFetcher("addProduct", env -> {
                    Product p = new Product(
                        0, // auto-increment
                        env.getArgument("code"),
                        env.getArgument("name"),
                        env.getArgument("category"),
                        ((Number) env.getArgument("price")).doubleValue(),
                        (int) env.getArgument("stock")
                    );
                    AuditInfo audit = new AuditInfo();
                    audit.setCreatedBy("currentUser"); // ambil dari session/login kalau ada
                    p.setAuditInfo(audit);

                    return productRepo.addProduct(p);
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
                    audit.setEditedBy("currentUser");
                    p.setAuditInfo(audit);

                    return productRepo.updateProduct(p);
                })

                .dataFetcher("deleteProduct", env -> {
                    int id = Integer.parseInt(env.getArgument("id").toString());
                    return productRepo.deleteProduct(id);
                })
            )
            .build();
    }
}

