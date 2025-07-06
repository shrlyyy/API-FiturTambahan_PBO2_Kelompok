/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;

/**
 *
 * @author ASUS
 */
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;

public class CustomerConfig {
    public static void register(RuntimeWiring.Builder builder, CustomerDAO customerDAO) {

        builder.type("Query", typeWiring -> typeWiring
            .dataFetcher("allCustomers", env -> customerDAO.getAllCustomers())
            .dataFetcher("customerById", env -> customerDAO.findById(env.getArgument("id")))
        )
        
        .type("Mutation", typeWiring -> typeWiring
            .dataFetcher("addCustomer", env -> {
                Customer c = new Customer(
                    null,
                    env.getArgument("name"),
                    env.getArgument("phoneNumber"),
                    env.getArgument("address")
                );
                AuditInfo audit = new AuditInfo();
                audit.setCreatedBy("admin_web");
                c.setAuditInfo(audit);
                return customerDAO.insertCustomer(c);
            })

            .dataFetcher("updateCustomer", env -> {
                Customer c = new Customer(
                    env.getArgument("id"),
                    env.getArgument("name"),
                    env.getArgument("phoneNumber"),
                    env.getArgument("address")
                );
                AuditInfo audit = new AuditInfo();
                audit.setEditedBy("admin_web");
                c.setAuditInfo(audit);
                return customerDAO.updateCustomer(c);
            })
            
            .dataFetcher("deleteCustomer", env -> customerDAO.deleteCustomer(env.getArgument("id")))
        );
    }
}


