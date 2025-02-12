package com.example.dominio;

public class Livro {
        private final String titulo;
        private final String autor;
        private final String genero;
        private boolean isDisponivel;
        private final int id;
    
        public Livro(String titulo, String autor, String genero, boolean isDisponivel, int id){
            this.titulo = titulo;
            this.autor = autor;
            this.genero = genero;
            this.isDisponivel = isDisponivel;
            this.id = id;
        }
    
        public void setDisponivel(boolean disponivel){
            this.isDisponivel = disponivel;
        }
    
        public String getTitulo(){
            return titulo;
        }
    
        public String getAutor(){
            return autor;
        }
    
        public String getGenero(){
            return genero;
        }
    
        public boolean isDisponivel(){
            return isDisponivel;
        }

        public int getId(){
            return id;
        }
    
    }
