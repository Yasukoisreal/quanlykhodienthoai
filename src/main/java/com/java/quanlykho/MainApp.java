package com.java.quanlykho;

import com.java.quanlykho.controller.LoginPane;
import com.java.quanlykho.controller.MainController;
import com.java.quanlykho.model.Employee;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point cho ứng dụng JavaFX - Hệ Thống Quản Lý Kho Smartphone.
 * Hiển thị Login → sau khi xác thực thành công → chuyển sang MainView.
 */
public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Smart Depot — Hệ Thống Quản Lý Kho Smartphone ERP");
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);

        showLogin();
    }

    private void showLogin() {
        LoginPane loginPane = new LoginPane(this::onLoginSuccess);
        Scene scene = new Scene(loginPane, 1320, 820);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void onLoginSuccess(Employee employee) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();

            // Truyền thông tin user đã login vào MainController
            MainController controller = loader.getController();
            controller.setLoggedInUser(employee);

            Scene scene = new Scene(root, 1320, 820);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            primaryStage.setScene(scene);
            System.out.println("[App] Chuyển sang MainView cho: " + employee.getFullName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
