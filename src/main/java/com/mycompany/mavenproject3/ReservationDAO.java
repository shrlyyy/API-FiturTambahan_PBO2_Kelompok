/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;

/**
 *
 * @author ASUS
 */
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    private Connection conn;

    public ReservationDAO() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/fiturtambahan-pbo2",
            "root", 
            ""
        );
    }

    // Generate next ID
    private String generateNextId() throws SQLException {
        String lastId = null;
        String sql = "SELECT reservationId FROM reservation ORDER BY reservationId DESC LIMIT 1";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                lastId = rs.getString("reservationId");
            }
        }

        if (lastId != null && lastId.startsWith("R")) {
            int num = Integer.parseInt(lastId.substring(1));
            return String.format("R%03d", num + 1);
        } else {
            return "R001";
        }
    }

    // Insert Reservation
    public Reservation insertReservation(Reservation r) throws SQLException {
        if (r.getReservationId() == null || r.getReservationId().isEmpty()) {
            r.setReservationId(generateNextId());
        }
        String sql = "INSERT INTO reservation (reservationId, customerId, reservationDate, reservationTime, reservedTable, numberOfPeople, createdBy) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, r.getReservationId());
            stmt.setString(2, r.getCustomerId());
            stmt.setDate(3, Date.valueOf(r.getReservationDate()));
            stmt.setTime(4, Time.valueOf(r.getReservationTime()));
            stmt.setString(5, r.getTable());
            stmt.setInt(6, r.getNumberOfPeople());
            stmt.setString(7, r.getCreatedBy());
            stmt.executeUpdate();
        }
        return r;
    }

    // Update Reservation
    public Reservation updateReservation(Reservation r) throws SQLException {
        String sql = "UPDATE reservation SET reservationDate=?, reservationTime=?, reservedTable=?, numberOfPeople=?, editedBy=? WHERE reservationId=? AND deletedBy IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(r.getReservationDate()));
            stmt.setTime(2, Time.valueOf(r.getReservationTime()));
            stmt.setString(3, r.getTable());
            stmt.setInt(4, r.getNumberOfPeople());
            stmt.setString(5, r.getEditedBy());
            stmt.setString(6, r.getReservationId());
            stmt.executeUpdate();
        }
        return r;
    }

    public boolean deleteReservation(String reservationId) throws SQLException {
        String sql = "DELETE FROM reservation WHERE reservationId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reservationId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    // Get All Reservations
    public List<Reservation> getAllReservations() throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE deletedBy IS NULL";  // filter hanya data aktif
        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Reservation r = new Reservation(
                    rs.getString("reservationId"),
                    rs.getString("customerId"),
                    rs.getDate("reservationDate").toLocalDate(),
                    rs.getTime("reservationTime").toLocalTime(),
                    rs.getString("reservedTable"),
                    rs.getInt("numberOfPeople"),
                    rs.getString("createdBy"),
                    rs.getString("editedBy"),
                    rs.getString("deletedBy")
                );
                list.add(r);
            }
        }
        return list;
    }


    // Find reservation by ID
    public Reservation findById(String reservationId) throws SQLException {
        String sql = "SELECT * FROM reservation WHERE reservationId = ? AND deletedBy IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reservationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Reservation(
                        rs.getString("reservationId"),
                        rs.getString("customerId"),
                        rs.getDate("reservationDate").toLocalDate(),
                        rs.getTime("reservationTime").toLocalTime(),
                        rs.getString("reservedTable"),
                        rs.getInt("numberOfPeople"),
                        rs.getString("createdBy"),
                        rs.getString("editedBy"),
                        rs.getString("deletedBy")
                    );
                }
            }
        }
        return null;
    }

    // Close connection
    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}


