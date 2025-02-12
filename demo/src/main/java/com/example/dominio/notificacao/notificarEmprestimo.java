package com.example.dominio.notificacao;

public class notificarEmprestimo implements Notificar {
    
    @Override
    public void enviarEmail() {
        System.out.println("Notificação de Emprestimo enviada ao Email.");
    }
    
}
