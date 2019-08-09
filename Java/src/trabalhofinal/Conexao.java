package trabalhofinal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conexao {
    
    public static Connection getConexao(){
        try {
            //executa o código para a conexão com o banco de dados, passando por parâmetro as variáveis necessárias
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/casainteligente", "root", "");
        } catch (ClassNotFoundException | SQLException ex) {
            // se der erro na conexão com o banco de dados, exibe mensagem de erro
            throw new RuntimeException("Erro de Conexão com o Banco de Dados: ", ex);
        }
    }
    
    public static void fecharConexao(Connection conexao){
        //encerra a conexão com o banco de dados
        try {
            if(conexao != null){
                conexao.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void fecharConexao(Connection conexao, PreparedStatement preparacao){
        fecharConexao(conexao);
        //encerra a conexão com o banco de dados
        try {
            if(conexao != null){
                conexao.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void fecharConexao(Connection conexao, PreparedStatement preparacao, ResultSet resultado){
        fecharConexao(conexao);
        //encerra a conexão com o banco de dados
        try {
            if(resultado != null){
                resultado.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
