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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mycompany.mavenproject3.ProductConfig;
import com.mycompany.mavenproject3.ProductRepository;

import graphql.schema.GraphQLSchema;

public class GraphQLConfig {
    public static GraphQL init() throws IOException, SQLException {
        InputStream schemaStream = GraphQLConfig.class.getClassLoader().getResourceAsStream("ProductSchema.graphqls");
        if (schemaStream == null) {
            throw new RuntimeException("ProductSchema.graphqls not found in classpath.");
        }
        String schema = new String(schemaStream.readAllBytes());

        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeRegistry = schemaParser.parse(schema);

        // Buat koneksi database
        String url = "jdbc:mysql://localhost:3306/fiturtambahan-pbo2";
        String username = "root";
        String password = "";
        Connection conn = DriverManager.getConnection(url, username, password);

        ProductRepository productRepo = new ProductRepository(conn);
        RuntimeWiring wiring = ProductConfig.buildWiring(productRepo);

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, wiring);

        return GraphQL.newGraphQL(graphQLSchema).build();
    }
}


