package com.controleagendamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

public class AutenticacaoDAO {

    private Connection connection;
    
    // Método para gerar o hash da senha
    
    public String gerarHashSenha(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    // Método para verificar a senha com o hash armazenado
    public boolean verificarSenha(String senha, String senhaHash) {
        return BCrypt.checkpw(senha, senhaHash);
    }
    
    // Método para verificar se o nome de usuário já existe
    
    public boolean verificarUsuarioExistente(String nomeUsuario) {
        String query = "SELECT COUNT(*) FROM Usuarios WHERE nome_usuario = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nomeUsuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Retorna true se já existir um usuário com esse nome
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Método para criar um novo usuário com o hash da senha
     public boolean criarUsuario(String nomeUsuario, String senha) {
        // Verifica se o nome de usuário já existe
        if (verificarUsuarioExistente(nomeUsuario)) {
            return false; // Retorna falso se o usuário já existe
        }

        String query = "INSERT INTO Usuarios (nome_usuario, senha) VALUES (?, ?)";
        String senhaHash = gerarHashSenha(senha); // Cria o hash da senha usando BCrypt
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nomeUsuario);
            stmt.setString(2, senhaHash);
            stmt.executeUpdate();
            System.out.println("Usuário criado com sucesso com hash: " + senhaHash);
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao criar usuário.");
            e.printStackTrace();
            return false;
        }
    }


    // Método para validar o usuário durante o login
public boolean validarUsuario(String nomeUsuario, String senha) {
    String query = "SELECT senha FROM Usuarios WHERE nome_usuario = ?";
    try (Connection conn = DatabaseConnection.getConnection()) {
        if (conn == null) {
            System.out.println("Falha na conexão com o banco de dados.");
            return false;
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nomeUsuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String senhaHash = rs.getString("senha");
                System.out.println("Hash armazenado no banco: " + senhaHash);
                return verificarSenha(senha, senhaHash);
            } else {
                System.out.println("Usuário não encontrado.");
            }
        }
    } catch (SQLException e) {
        System.out.println("Erro ao validar usuário.");
        e.printStackTrace();
    }
    return false;
}
}
