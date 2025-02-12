package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.dominio.Usuario;

public class UsuarioDAO {

    public boolean adicionarUsuario(Usuario usuario) {
        if (buscarUsuarioPorCPF(usuario.getCpf()) != null) {
            System.out.println("Usuário já cadastrado com esse CPF!");
            return false;
        }

        String sql = "INSERT INTO usuarios (nome, cpf, email, senha) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNomeUsuario());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getSenha());
            stmt.executeUpdate();
            System.out.println("Cadastro feito com sucesso!");
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar usuário: " + e.getMessage());
            return false;
        }
    }

    public Usuario buscarUsuarioPorCPF(String cpf) {
        String sql = "SELECT * FROM usuarios WHERE cpf = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Usuario(rs.getInt("id"), rs.getString("nome"), rs.getString("cpf"),
                        rs.getString("email"), rs.getString("senha"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por CPF: " + e.getMessage());
        }
        return null;
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Usuario(rs.getInt("id"), rs.getString("nome"), rs.getString("cpf"),
                        rs.getString("email"), rs.getString("senha"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por e-mail: " + e.getMessage());
        }
        return null;
    }

    public Usuario fazerLogin(String email, String senha) {
        Usuario usuario = buscarUsuarioPorEmail(email);
        if (usuario != null && usuario.getSenha().equals(senha)) {
            return usuario;
        }
        return null;
    }

    public Usuario buscarUsuarioPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Usuario(rs.getInt("id"), rs.getString("nome"), rs.getString("cpf"),
                        rs.getString("email"), rs.getString("senha"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por ID: " + e.getMessage());
        }
        return null; // Retorna null se o usuário não for encontrado
    }
    
}