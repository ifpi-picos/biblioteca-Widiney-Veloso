package com.example.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.example.dao.EmprestimoDAO;
import com.example.dao.LivroDAO;
import com.example.dao.UsuarioDAO;
import com.example.dominio.Emprestimo;
import com.example.dominio.Livro;
import com.example.dominio.Usuario;

public class App {
    @SuppressWarnings({"CallToPrintStackTrace", "ConvertToTryWithResources"})
    public static void main(String[] args) {
        try {
            Connection conexao = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/biblioteca_java", "postgres", "Wid");

            if (conexao != null) {
                System.out.println("Banco de dados conectado com sucesso!");

                Scanner scanner = new Scanner(System.in);
                LivroDAO livroDAO = new LivroDAO();
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                EmprestimoDAO emprestimoDAO = new EmprestimoDAO();
                Usuario usuarioLogado = null;

                while (usuarioLogado == null) {
                    System.out.println("\n=== BEM-VINDO À BIBLIOTECA ===");
                    System.out.println("1. Fazer Login");
                    System.out.println("2. Cadastrar-se");
                    System.out.print("Escolha uma opção: ");
                    int opcao = scanner.nextInt();
                    scanner.nextLine();

                    if (opcao == 1) {
                        System.out.print("Digite seu e-mail: ");
                        String email = scanner.nextLine();
                        System.out.print("Digite sua senha: ");
                        String senha = scanner.nextLine();

                        usuarioLogado = usuarioDAO.fazerLogin(email, senha);
                        if (usuarioLogado == null) {
                            System.out.println("E-mail ou senha errada");
                            System.exit(0);
                        }
                    } else if (opcao == 2) {
                        System.out.print("Digite seu nome completo: ");
                        String nome = scanner.nextLine();
                        System.out.print("Digite seu CPF: ");
                        String cpf = scanner.nextLine();
                        System.out.print("Digite seu e-mail: ");
                        String email = scanner.nextLine();
                        System.out.print("Crie uma senha: ");
                        String senha = scanner.nextLine();

                        Usuario novoUsuario = new Usuario(nome, cpf, email, senha);
                        if (usuarioDAO.adicionarUsuario(novoUsuario)) {
                            usuarioLogado = novoUsuario;
                        }
                    }
                }

                int opcao;
                do {
                    System.out.println("\n=== MENU ===");
                    System.out.println("1. Listar livros");
                    System.out.println("2. Adicionar livro");
                    System.out.println("3. Realizar empréstimo");
                    System.out.println("4. Listar livros indisponíveis");
                    System.out.println("5. Listar livros disponíveis");
                    System.out.println("6. Devolver livro");
                    System.out.println("7. Sair");
                    System.out.print("Escolha uma opção: ");
                    opcao = scanner.nextInt();
                    scanner.nextLine();

                    switch (opcao) {
                        case 1 -> {
                            List<Livro> livros = livroDAO.listarLivros();
                            if (livros.isEmpty()) {
                                System.out.println("Nenhum livro cadastrado.");
                            } else {
                                System.out.println("=== Livros cadastrados ===");
                                for (Livro livro : livros) {
                                    String status = livro.isDisponivel() ? "Disponível" : "Emprestado";
                                    System.out
                                            .println("Título: " + livro.getTitulo() + " | Autor: " + livro.getAutor() +
                                                    " | Gênero: " + livro.getGenero() + " | Status: " + status);
                                }
                            }
                        }
                        case 2 -> {
                            System.out.print("Digite o título do livro: ");
                            String titulo = scanner.nextLine();
                            System.out.print("Digite o autor: ");
                            String autor = scanner.nextLine();
                            System.out.print("Digite o gênero do livro: ");
                            String genero = scanner.nextLine();
                            Random random = new Random();
                            int num = random.nextInt(1000);
                            Livro livro = new Livro(titulo, autor, genero, true, num);
                            livroDAO.adicionarLivro(livro);
                            System.out.println("Livro cadastrado com sucesso!");
                        }
                        case 3 -> {
                            System.out.print("Digite o ID do usuário: ");
                            int idUsuario = scanner.nextInt();
                            scanner.nextLine(); // Limpar o buffer
                            System.out.print("Digite o título do livro: ");
                            String tituloLivro = scanner.nextLine();

                            // Buscar o usuário pelo ID
                            Usuario user = usuarioDAO.buscarUsuarioPorId(idUsuario);
                            if (user == null) {
                                System.out.println("Usuário não encontrado.");
                                break;
                            }

                            // Buscar o livro pelo título
                            Livro book = livroDAO.buscarLivroPorTitulo(tituloLivro);
                            if (book == null) {
                                System.out.println("Livro não encontrado.");
                                break;
                            }
                            if (!book.isDisponivel()) {
                                System.out.println("Este livro já está emprestado.");
                                break;
                            }

                            // Criar e registrar o empréstimo
                            try {
                                Emprestimo emprestimo = new Emprestimo(user.getId(), user, book);
                                emprestimoDAO.adicionarEmprestimo(emprestimo);

                                // Atualizar a disponibilidade do livro
                                livroDAO.atualizarDisponibilidade(book.getId(), false);

                                System.out.println("Empréstimo realizado com sucesso!");
                            } catch (Exception e) {
                                System.err.println("Erro ao realizar empréstimo: " + e.getMessage());
                            }
                        }

                        case 4 -> {
                            List<Livro> indisponiveis = livroDAO.listarLivrosIndisponiveis();
                            if (indisponiveis.isEmpty()) {
                                System.out.println("Nenhum livro indisponível no momento.");
                            } else {
                                System.out.println("=== Livros indisponíveis ===");
                                for (Livro l : indisponiveis) {
                                    System.out.println("Título: " + l.getTitulo() + " | Autor: " + l.getAutor());
                                }
                            }
                        }
                        case 5 -> {
                            List<Livro> disponiveis = livroDAO.listarLivrosDisponiveis();
                            if (disponiveis.isEmpty()) {
                                System.out.println("Nenhum livro disponível no momento.");
                            } else {
                                System.out.println("=== Livros disponíveis ===");
                                for (Livro l : disponiveis) {
                                    System.out.println("Título: " + l.getTitulo() + " | Autor: " + l.getAutor());
                                }
                            }
                        }
                        case 6 -> {
                            System.out.print("Digite o título do livro a ser devolvido: ");
                            String tituloLivro = scanner.nextLine();
                            emprestimoDAO.devolverLivro(tituloLivro);
                        }
                        
                        case 7 -> System.out.println("Saindo do sistema...");
                        default -> System.out.println("Opção inválida!");
                    }
                } while (opcao != 7);

                scanner.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}