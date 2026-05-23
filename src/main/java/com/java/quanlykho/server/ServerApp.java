package com.java.quanlykho.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP Server - Lắng nghe kết nối client trên port 8888.
 * Mỗi client được xử lý bởi một ClientHandler thread riêng.
 *
 * Chạy: java com.java.quanlykho.server.ServerApp
 */
public class ServerApp {

    private static final int PORT = 8888;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║   SMART DEPOT - TCP Server v1.0              ║");
        System.out.println("║   Hệ Thống Quản Lý Kho Smartphone            ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();

        DatabaseManager db;
        try {
            db = new DatabaseManager();
        } catch (Exception e) {
            System.err.println("[Server] KHÔNG THỂ KẾT NỐI MySQL!");
            System.err.println("[Server] Lỗi: " + e.getMessage());
            System.err.println("[Server] Đảm bảo MySQL đang chạy và database warehouse_db đã được tạo.");
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[Server] TCP Server đang lắng nghe trên port " + PORT + "...");
            System.out.println("[Server] Chờ client kết nối...");
            System.out.println();

            // Add shutdown hook to close DB
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("[Server] Đang tắt server...");
                db.close();
            }));

            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread clientThread = new Thread(new ClientHandler(clientSocket, db));
                clientThread.setDaemon(true);
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("[Server] Lỗi server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
