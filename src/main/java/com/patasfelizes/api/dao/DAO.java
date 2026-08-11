package com.patasfelizes.api.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import singleton.DatabaseConnection;

import java.util.List;

import com.patasfelizes.api.model.Cliente;

public class DAO {

    private static Connection conn = DatabaseConnection.getInstance().getConnection();

    private static ResultSet select(String target, String param, int id){
        String sql = "SELECT * FROM " + target + " WHERE " + param + " = ?";
        ResultSet rs = null;

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
        } catch (Exception e) { 
            System.err.println("erro ao executar query.");
            System.err.println("sql string: SELECT * FROM " + target + " WHERE " + param + " = " + id);

            e.printStackTrace(); 
        }

        return rs;
    }

    public List<Cliente> listarCliente() {
        return null;
    }

    public List<Cliente> listarCliente(String nome) {
        return null;
    }
    
    public List<Cliente> listarCliente(Integer id) {
        return null;
    }

    public Cliente criarCliente (Cliente cliente) {
        return null;
    }

}
