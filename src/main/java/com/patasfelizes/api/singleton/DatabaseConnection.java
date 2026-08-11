package com.patasfelizes.api.singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {
    private static DatabaseConnection instance = null;

    private static final String HOST = "ep-muddy-bonus-ac038z13.sa-east-1.aws.neon.tech";
    private static final String USER = "neondb_owner";
    private static final String PORT = "5432";
    private static final String DB_NAME = "neondb";
    private static final String PASSWORD = "npg_ThcNk1DQtdE4";

    private static final String DB_CONN_STRING = String.format(
        "jdbc:postgresql://" +
        "%s:%s/%s?" + 
        // "user=%s&" + 
        // "password=%s&" + 
        "sslmode=require&" + 
        "channelBinding=require", 
        HOST, PORT, DB_NAME
        // , USER, PASSWORD
    );

    private DatabaseConnection(){}

    public static DatabaseConnection getInstance(){
        if(instance==null)
            instance = new DatabaseConnection();
        return instance;
    }

    private Connection conn = null;

    public void connect(){
        System.out.print("\nEXECUTANDO CONEXAO COM O BANCO DE DADOS... ");
        
        try{
            Class.forName("org.postgresql.Driver");

            if(this.conn != null && !this.conn.isClosed()) 
                this.conn.close();

            this.conn = DriverManager.getConnection(DB_CONN_STRING, USER, PASSWORD);

            System.out.println("STATUS DA CONEXAO: " + (conn.isClosed() ? "FALHA" : "CONECTADO") + "\n");

        } catch(ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }

    public Connection getConnection(){
        if(this.conn == null) connect();
        return this.conn;
    }

    public static void main(String[] args){
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();

        try {
            if (conn != null && !conn.isClosed()) {
                System.out.println(">>> Sucesso! A conexao com o Neon PostgreSQL esta ativa.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
