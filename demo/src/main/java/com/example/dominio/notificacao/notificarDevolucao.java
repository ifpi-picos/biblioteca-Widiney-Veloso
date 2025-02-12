package com.example.dominio.notificacao;

public class notificarDevolucao implements Notificar {

    @Override
    public void enviarEmail() {
        System.out.println("Notificação de Devolução enviada ao Email!");
    }
    
}
