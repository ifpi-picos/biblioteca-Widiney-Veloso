package com.example.dominio;

public class Usuario {
    private int idUsuario;
    private String nomeUsuario;
    private String cpf;
    private String email;
    private String senha;

    public Usuario(String nomeUsuario, String cpf, String email, String senha) {
        this.nomeUsuario = nomeUsuario;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
    }

    public Usuario(int id, String nomeUsuario, String cpf, String email, String senha) {
        this.idUsuario = id;
        this.nomeUsuario = nomeUsuario;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
    }

    public Usuario() {
    }

    public int getId() { return idUsuario; }
    public String getNomeUsuario() { return nomeUsuario; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
}