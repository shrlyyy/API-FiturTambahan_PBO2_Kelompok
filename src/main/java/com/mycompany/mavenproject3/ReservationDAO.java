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

    public void insertReservation(Reservation r) throws SQLException {
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
    }

    public void updateReservation(Reservation r) throws SQLException {
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
    }

    public void deleteReservation(String reservationId, String deletedBy) throws SQLException {
        String sql = "UPDATE reservation SET deletedBy=? WHERE reservationId=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, deletedBy);
            stmt.setString(2, reservationId);
            stmt.executeUpdate();
        }
    }

    public List<Reservation> getAllReservations() throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE deletedBy IS NULL";
        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String resId = rs.getString("reservationId");
                String custId = rs.getString("customerId");
                LocalDate date = rs.getDate("reservationDate").toLocalDate();
                LocalTime time = rs.getTime("reservationTime").toLocalTime();
                String table = rs.getString("reservedTable");
                int people = rs.getInt("numberOfPeople");

                Reservation r = new Reservation(
                    resId, custId, date, time, table, people,
                    rs.getString("createdBy"),
                    rs.getString("editedBy"),
                    rs.getString("deletedBy")
                );
                list.add(r);
            }
        }
        return list;
    }

    public void close() throws SQLException {
        if (conn != null) conn.close();
    }
}

