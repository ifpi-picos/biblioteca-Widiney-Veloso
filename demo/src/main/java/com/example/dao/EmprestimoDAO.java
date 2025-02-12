package com.example.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.example.dominio.Emprestimo;
import com.example.dominio.Livro;
import com.example.dominio.Usuario;
import com.example.dominio.notificacao.Notificar;
import com.example.dominio.notificacao.notificarEmprestimo;

public class EmprestimoDAO {

    public void adicionarEmprestimo(Emprestimo emprestimo) {
        String sql = "INSERT INTO emprestimos (id_usuario, id_livro, data_emprestimo) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, emprestimo.getUsuario().getId());
            stmt.setInt(2, emprestimo.getLivro().getId());
            stmt.setDate(3, Date.valueOf(emprestimo.getDataEmprestimo()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao registrar empréstimo: " + e.getMessage());
        }
    }

    public Emprestimo buscarEmprestimoPorLivro(int idLivro) {
        String sql = "SELECT * FROM emprestimos WHERE id_livro = ? AND data_devolucao IS NULL";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLivro);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                LivroDAO livroDAO = new LivroDAO();
                Usuario usuario = usuarioDAO.buscarUsuarioPorId(rs.getInt("id_usuario"));
                Livro livro = livroDAO.buscarLivroPorId(idLivro);
                return new Emprestimo(rs.getInt("id"), usuario, livro);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar empréstimo: " + e.getMessage());
        }
        return null;
    }

    public void devolverLivro(String tituloLivro) {
        String sqlBusca = "SELECT id, disponivel FROM livros WHERE titulo = ?";
        String sqlAtualizaEmprestimo = "UPDATE emprestimos SET data_devolucao = ? WHERE id_livro = ? AND data_devolucao IS NULL";
        String sqlAtualizaLivro = "UPDATE livros SET disponivel = TRUE WHERE id = ?";

        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement stmtBusca = conn.prepareStatement(sqlBusca)) {
            stmtBusca.setString(1, tituloLivro);
            ResultSet rs = stmtBusca.executeQuery();

            if (rs.next()) {
                int idLivro = rs.getInt("id");
                boolean disponivel = rs.getBoolean("disponivel");

                if (disponivel) {
                    System.out.println("O livro já está disponível na biblioteca.");
                } else {
                    LocalDate dataDevolucao = LocalDate.now();
                    try (PreparedStatement stmtAtualizaEmprestimo = conn.prepareStatement(sqlAtualizaEmprestimo);
                         PreparedStatement stmtAtualizaLivro = conn.prepareStatement(sqlAtualizaLivro)) {

                        stmtAtualizaEmprestimo.setDate(1, Date.valueOf(dataDevolucao));
                        stmtAtualizaEmprestimo.setInt(2, idLivro);
                        stmtAtualizaEmprestimo.executeUpdate();

                        stmtAtualizaLivro.setInt(1, idLivro);
                        stmtAtualizaLivro.executeUpdate();

                        System.out.println("Livro devolvido com sucesso!");

                        Notificar notificar = new NotificarDevolucao();
                        notificar.enviarEmail();
                    }
                }
            } else {
                System.out.println("Livro não encontrado no banco de dados.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao devolver livro: " + e.getMessage());
        }
    }

    public void realizarEmprestimo(int idUsuario, String tituloLivro) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        LivroDAO livroDAO = new LivroDAO();
    
        Usuario usuario = usuarioDAO.buscarUsuarioPorId(idUsuario);
        Livro livro = livroDAO.buscarLivroPorTitulo(tituloLivro);
    
        if (usuario == null) {
            System.out.println("Usuário não encontrado.");
            return;
        }
    
        if (livro == null || !livro.isDisponivel()) {
            System.out.println("Livro não encontrado ou já emprestado.");
            return;
        }
    
        String sql = "INSERT INTO emprestimos (id_usuario, id_livro, data_emprestimo) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoDB.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, livro.getId());
            stmt.setDate(3, Date.valueOf(LocalDate.now()));
            stmt.executeUpdate();
            livroDAO.atualizarDisponibilidade(livro.getId(), false);
            System.out.println("Empréstimo realizado com sucesso!");

            Notificar notificar = new  notificarEmprestimo();
            notificar.enviarEmail();

        } catch (SQLException e) {
            System.err.println("Erro ao registrar empréstimo: " + e.getMessage());
        }
    }
    
}
