package br.com.acheiacai.uteis;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static String gerarHash(String senhaBruta) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(senhaBruta, salt);
    }

    public static boolean verificarSenha(String senhaBruta, String senhaHash) throws IllegalArgumentException{

        try {
            return BCrypt.checkpw(senhaBruta, senhaHash);
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao verificar a senha: formato de hash inválido.");
            return false;
        }
    }
}
