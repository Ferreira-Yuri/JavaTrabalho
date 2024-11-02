package com.controleagendamento;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() {
        String URL = "jdbc:sqlserver://localhost:1433;databaseName=DBControleAgendamento;encrypt=true;trustServerCertificate=true";
        String USER = "Yuri";
        String PASSWORD = "123456";

        try {
            // Estabelecendo a conexão
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexão estabelecida com sucesso!");
            return conn;
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados.");
            e.printStackTrace();
            return null;
        }
    }
}
