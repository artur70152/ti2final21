package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginClient {

    public static void main(String[] args) {
        String email = "a1";
        String senha = "a";

        login(email, senha);
    }

    public static void login(String email, String senha) {
        // Configurações do banco (PostgreSQL ou MySQL no Docker)
    	String jdbcURL = "jdbc:postgresql://localhost:5436/ti2";
    	String dbUser = "postgres";
    	String dbPassword = "docker";


        Connection conexao = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Carregar o driver
            Class.forName("org.postgresql.Driver");  // Ou: "com.mysql.cj.jdbc.Driver"

            // Estabelecer a conexão
            conexao = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);

            String sql = "SELECT name, senha FROM usuario WHERE email = ?";
            stmt = conexao.prepareStatement(sql);
            stmt.setString(1, email);

            rs = stmt.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("name");
                String senhaBanco = rs.getString("senha");

                if (senhaBanco.equals(senha)) {
                    System.out.println("Bem-vindo, " + nome + "!");
                } else {
                    System.out.println("E-mail ou senha incorretos.");
                }
            } else {
                System.out.println("Usuário não encontrado.");
            }

        } catch (Exception e) {
            System.out.println("Erro ao conectar ou consultar o banco de dados.");
            e.printStackTrace();
        } finally {
            // Fechar recursos
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conexao != null) conexao.close(); } catch (Exception e) {}
        }
    }
}
