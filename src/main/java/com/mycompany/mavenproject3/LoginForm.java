/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;

/**
 *
 * @author ASUS
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.lang.ClassNotFoundException;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private HashMap<String, String> cashierAccounts;
    private Mavenproject3 mainApp;

    private Cashier loggedInCashier;

    // Konstruktor dengan referensi main app
    public LoginForm(Mavenproject3 mainApp) {
        this();
        this.mainApp = mainApp;
}

    public Cashier getLoggedInCashier() {
        return loggedInCashier;
    }

    // Konstruktor default (inisialisasi UI)
    public LoginForm() {
        setTitle("Login | WK. STI Chill");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3, 1));

        // Panel Username
        JPanel userPanel = new JPanel(new FlowLayout());
        userPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(15);
        userPanel.add(usernameField);
        add(userPanel);

        // Panel Password
        JPanel passPanel = new JPanel(new FlowLayout());
        passPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(15);
        passPanel.add(passwordField);
        add(passPanel);

        // Panel Tombol Login
        loginButton = new JButton("Login");
        JPanel btnPanel = new JPanel();
        btnPanel.add(loginButton);
        add(btnPanel);

        // Event tombol login
        loginButton.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            UserDAO userDAO = new UserDAO();  // Tidak throws lagi
            Cashier cashier = userDAO.login(username, password);
            userDAO.close();

            if (cashier != null) {
                JOptionPane.showMessageDialog(this, "Login berhasil. Selamat datang, " + cashier.getUsername() + "!");
                if (mainApp != null) {
                    mainApp.onLoginSuccess(cashier);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Login gagal. Cek kembali username dan password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Kesalahan koneksi ke database.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}
