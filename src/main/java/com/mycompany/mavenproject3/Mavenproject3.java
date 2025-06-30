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

import com.mycompany.mavenproject3.Mavenproject3.BannerPanel;

import java.awt.*;
import java.util.ArrayList;

public class Mavenproject3 extends JFrame implements Runnable {
    private String text;
    private int x;
    private int width;
    private BannerPanel bannerPanel;
    private JButton addProductButton;
    private JButton sellingButton;
    private JButton customerButton;
    private JButton reservationButton;
    private JButton logoutButton;
    private Cashier currentUser;
    private ProductForm form;

    private ArrayList<Customer> sharedCustomers = new ArrayList<>();


    public Mavenproject3() {
        initUI();
        showLoginDialog();
    }

    private void initUI() {
        setTitle("WK. STI Chill");
        setSize(850, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        text = "Silakan login untuk melihat menu yang tersedia";
        x = getWidth();
        
        // Panel teks berjalan
        bannerPanel = new BannerPanel();
        add(bannerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        addProductButton = new JButton("Kelola Produk");
        customerButton = new JButton("Kelola Customer");
        sellingButton = new JButton("Penjualan");
        reservationButton = new JButton("Reservasi");
        logoutButton = new JButton("Logout");

        bottomPanel.add(addProductButton);
        bottomPanel.add(customerButton);
        bottomPanel.add(sellingButton);
        bottomPanel.add(reservationButton);
        bottomPanel.add(logoutButton);

        addProductButton.setVisible(false);
        customerButton.setVisible(false);
        sellingButton.setVisible(false);
        reservationButton.setVisible(false);
        logoutButton.setVisible(false);

        add(bottomPanel, BorderLayout.SOUTH);

        addProductButton.addActionListener(e -> {
            if (form != null) {
                form.setVisible(true);
                updateBannerText(form.getProductBannerText());
            }
        });

        customerButton.addActionListener(e -> {
        CustomerForm customerForm = new CustomerForm(sharedCustomers, currentUser.getUsername());
        customerForm.setVisible(true);
        });

        sellingButton.addActionListener(e -> {
        SellingForm sellingForm = new SellingForm(form, sharedCustomers, ReservationForm.getReservations());
        sellingForm.setVisible(true);
        });

        reservationButton.addActionListener(e -> {
            ReservationForm reservationForm = new ReservationForm(sharedCustomers, currentUser.getUsername());
            reservationForm.setVisible(true);
        });

        logoutButton.addActionListener(e -> logout());

        setVisible(true);

        Thread thread = new Thread(this);
        thread.start();
    }

    private void showLoginDialog() {
        LoginForm login = new LoginForm(this);
        login.setVisible(true);
    }

    public void onLoginSuccess(Cashier cashier) {
        this.currentUser = cashier; 

        form = new ProductForm(currentUser);
        form.setProductChangeListener(() ->
            SwingUtilities.invokeLater(() ->
                updateBannerText(form.getProductBannerText())
            )
        );

        updateBannerText(form.getProductBannerText());

        addProductButton.setVisible(true);
        customerButton.setVisible(true);
        sellingButton.setVisible(true);
        reservationButton.setVisible(true);
        logoutButton.setVisible(true);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin logout?", 
            "Konfirmasi Logout", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            this.currentUser = null;

        // Tampilkan login form lagi
        showLoginDialog();
        }
    }

    class BannerPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString(text, x, getHeight() / 2);
        }
    }

    @Override
    public void run() {
        width = getWidth();
        x = width;
        while (true) {
            x -= 5;
            if (x + bannerPanel.getFontMetrics(new Font("Arial", Font.BOLD, 18)).stringWidth(text) < 0) {
                x = width;
            }
            bannerPanel.repaint();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void updateBannerText(String newText) {
        this.text = newText;
        x = -bannerPanel.getFontMetrics(new Font("Arial", Font.BOLD, 18)).stringWidth(newText);
    }

    public static void main(String[] args) {
        new Mavenproject3();
    }
}
