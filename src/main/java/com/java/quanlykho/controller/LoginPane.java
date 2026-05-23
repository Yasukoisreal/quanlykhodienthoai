package com.java.quanlykho.controller;

import com.java.quanlykho.model.Employee;
import com.java.quanlykho.network.NetworkService;
import com.java.quanlykho.network.Request;
import com.java.quanlykho.network.Response;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.Consumer;

/**
 * Màn hình đăng nhập - xác thực username + password qua TCP server.
 */
public class LoginPane extends StackPane {

    private final Consumer<Employee> onLoginSuccess;
    private final TextField tfUsername;
    private final PasswordField tfPassword;
    private final Label lblError;
    private final Label lblStatus;

    public LoginPane(Consumer<Employee> onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
        setStyle("-fx-background-color: linear-gradient(to bottom right, #0f172a, #1e293b);");

        // Center card
        VBox card = new VBox(20);
        card.setMaxWidth(420);
        card.setMaxHeight(520);
        card.setPadding(new Insets(40));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 30, 0, 0, 10);");

        // Logo
        Label logo = new Label("📱");
        logo.setStyle("-fx-font-size: 40px;");

        Label appName = new Label("SMART DEPOT");
        appName.setFont(Font.font("Segoe UI", FontWeight.BLACK, 24));
        appName.setStyle("-fx-text-fill: #0f172a;");

        Label subtitle = new Label("Hệ Thống Quản Lý Kho Smartphone ERP");
        subtitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

        // Separator
        Separator sep = new Separator();
        sep.setStyle("-fx-padding: 5 0;");

        // Form
        VBox form = new VBox(12);
        form.setAlignment(Pos.CENTER_LEFT);

        Label lblUser = new Label("Tên tài khoản");
        lblUser.setStyle("-fx-font-weight: 700; -fx-text-fill: #334155; -fx-font-size: 12px;");
        tfUsername = new TextField();
        tfUsername.setPromptText("Nhập username...");
        tfUsername.setStyle("-fx-padding: 10; -fx-font-size: 13px; -fx-background-radius: 8; " +
            "-fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-border-width: 1;");

        Label lblPass = new Label("Mật khẩu");
        lblPass.setStyle("-fx-font-weight: 700; -fx-text-fill: #334155; -fx-font-size: 12px;");
        tfPassword = new PasswordField();
        tfPassword.setPromptText("Nhập mật khẩu...");
        tfPassword.setStyle("-fx-padding: 10; -fx-font-size: 13px; -fx-background-radius: 8; " +
            "-fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-border-width: 1;");
        tfPassword.setOnAction(e -> handleLogin());

        form.getChildren().addAll(lblUser, tfUsername, lblPass, tfPassword);

        // Error label
        lblError = new Label();
        lblError.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px; -fx-font-weight: 600;");
        lblError.setWrapText(true);
        lblError.setVisible(false);

        // Login button
        Button btnLogin = new Button("🔐 Đăng Nhập Hệ Thống");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white; -fx-font-size: 13px; " +
            "-fx-font-weight: 700; -fx-padding: 12; -fx-background-radius: 8; -fx-cursor: hand;");
        btnLogin.setOnAction(e -> handleLogin());

        // Status
        lblStatus = new Label("🖥 TCP Server: Đang kiểm tra...");
        lblStatus.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");

        // Check connection
        checkServerConnection();

        card.getChildren().addAll(logo, appName, subtitle, sep, form, lblError, btnLogin, lblStatus);
        getChildren().add(card);
    }

    private void checkServerConnection() {
        NetworkService net = NetworkService.getInstance();
        boolean connected = net.connect();
        if (connected) {
            lblStatus.setText("🟢 TCP Server: CONNECTED (port 8888)");
            lblStatus.setStyle("-fx-text-fill: #166534; -fx-font-size: 11px; -fx-font-weight: 600;");
        } else {
            lblStatus.setText("🔴 TCP Server: DISCONNECTED — Hãy chạy ServerApp trước!");
            lblStatus.setStyle("-fx-text-fill: #991b1b; -fx-font-size: 11px; -fx-font-weight: 600;");
        }
    }

    private void handleLogin() {
        String username = tfUsername.getText().trim();
        String password = tfPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ tên tài khoản và mật khẩu!");
            return;
        }

        NetworkService net = NetworkService.getInstance();
        if (!net.isConnected()) {
            // Try reconnecting
            if (!net.connect()) {
                showError("Không thể kết nối TCP Server! Hãy chạy ServerApp trước.");
                return;
            }
        }

        // Send LOGIN request
        Response res = net.sendRequest(new Request("LOGIN", "employees", new String[]{username, password}));

        if (res.isSuccess() && res.getData() != null) {
            Employee employee = (Employee) res.getData();
            lblError.setVisible(false);
            System.out.println("[Login] Đăng nhập thành công: " + employee.getFullName() + " (" + employee.getRole() + ")");
            onLoginSuccess.accept(employee);
        } else {
            showError(res.getMessage() != null ? res.getMessage() : "Sai tên tài khoản hoặc mật khẩu!");
        }
    }

    private void showError(String msg) {
        lblError.setText("⚠ " + msg);
        lblError.setVisible(true);
    }
}
