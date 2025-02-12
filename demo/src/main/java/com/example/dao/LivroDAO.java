package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.example.dominio.Livro;

public class LivroDAO {

    // Adicionar livro ao banco de dados
    public void adicionarLivro(Livro livro) {
        String sql = "INSERT INTO livros (titulo, autor, genero, disponivel) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getAutor());
            stmt.setString(3, livro.getGenero());
            stmt.setBoolean(4, livro.isDisponivel());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar livro: " + e.getMessage());
        }
    }

    // Listar todos os livros do banco de dados
    public List<Livro> listarLivros() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT * FROM livros";
        try (Connection conn = ConexaoDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                livros.add(new Livro(
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("genero"),
                        rs.getBoolean("disponivel"),
                        rs.getInt("id")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar livros: " + e.getMessage());
        }
        return livros;
    }

    // Buscar um livro pelo título
    public Livro buscarLivroPorTitulo(String titulo) {
        String sql = "SELECT * FROM livros WHERE titulo = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, titulo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Livro(
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("genero"),
                        rs.getBoolean("disponivel"),
                        rs.getInt("id")
                        
                );
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar livro: " + e.getMessage());
        }
        return null; // Retorna null se o livro não for encontrado
    }

    // Atualizar a disponibilidade do livro (true = disponível, false = emprestado)
    public void atualizarDisponibilidade(int idLivro, boolean disponivel) {
        String sql = "UPDATE livros SET disponivel = ? WHERE id = ?";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, disponivel); // Define o novo status de disponibilidade
            stmt.setInt(2, idLivro);       // Define o ID do livro
            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Disponibilidade do livro atualizada com sucesso!");
            } else {
                System.out.println("Nenhum livro encontrado com o ID fornecido.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar disponibilidade do livro: " + e.getMessage());
        }
    }

    public List<Livro> listarLivrosIndisponiveis() {
        List<Livro> livrosIndisponiveis = new ArrayList<>();
        String sql = "SELECT * FROM livros WHERE disponivel = FALSE";
        try (Connection conn = ConexaoDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                livrosIndisponiveis.add(new Livro(
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("genero"),
                        rs.getBoolean("disponivel"),
                        rs.getInt("id")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar livros indisponíveis: " + e.getMessage());
        }
        return livrosIndisponiveis;
    }
    
    public List<Livro> listarLivrosDisponiveis() {
        List<Livro> livrosDisponiveis = new ArrayList<>();
        String sql = "SELECT * FROM livros WHERE disponivel = TRUE";
        try (Connection conn = ConexaoDB.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                livrosDisponiveis.add(new Livro(
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("genero"),
                        rs.getBoolean("disponivel"),
                        rs.getInt("id")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar livros disponíveis: " + e.getMessage());
        }
        return livrosDisponiveis;
    }

    public Livro buscarLivroPorId(int id) {
        String sql = "SELECT * FROM livros WHERE id = ?";
        
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) { // Verifica se encontrou o livro
                return new Livro(
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getString("genero"),
                    rs.getBoolean("disponivel"),
                    rs.getInt("id")
                );
            }
    
        } catch (SQLException e) {
            System.err.println("Erro ao buscar livro por ID: " + e.getMessage());
        }
        
        return null; 
    }
    

}
