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

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationConfig {
    public static void register(RuntimeWiring.Builder builder, ReservationDAO reservationDAO) {
        builder.type("Query", typeWiring -> typeWiring
            .dataFetcher("allReservations", env -> reservationDAO.getAllReservations())
            .dataFetcher("reservationById", env -> reservationDAO.findById(env.getArgument("reservationId")))
        )
        .type("Mutation", typeWiring -> typeWiring
            .dataFetcher("addReservation", env -> {
                Reservation r = new Reservation(
                    null, // reservationId null karena akan di-generate
                    env.getArgument("customerId"),
                    LocalDate.parse(env.getArgument("reservationDate")),
                    LocalTime.parse(env.getArgument("reservationTime")),
                    env.getArgument("reservedTable"),
                    env.getArgument("numberOfPeople"),
                    "admin_web", // createdBy
                    null,       // editedBy
                    null        // deletedBy
                );
                return reservationDAO.insertReservation(r);
            })

            .dataFetcher("updateReservation", env -> {
                Reservation r = new Reservation(
                    env.getArgument("reservationId"),
                    env.getArgument("customerId"),
                    LocalDate.parse(env.getArgument("reservationDate")),
                    LocalTime.parse(env.getArgument("reservationTime")),
                    env.getArgument("reservedTable"),
                    env.getArgument("numberOfPeople"),
                    null, // createdBy tidak diubah saat update
                    "admin_web", // editedBy
                    null
                );
                return reservationDAO.updateReservation(r);
            })

            .dataFetcher("deleteReservation", env -> {
                String id = env.getArgument("reservationId");
                return reservationDAO.deleteReservation(id);
            })
        );
    }
}
