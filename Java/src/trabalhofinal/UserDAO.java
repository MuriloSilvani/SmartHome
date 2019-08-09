package trabalhofinal;

import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class UserDAO {
    
    public void newUser(User user){
        Connection conexao = Conexao.getConexao();
        PreparedStatement preparar = null;
        
        try{
            preparar = conexao.prepareStatement("INSERT INTO user (name, password) VALUES (?, ?)");
            preparar.setString(1, user.getName());
            preparar.setString(2, user.getPassword());
            preparar.executeUpdate();
            JOptionPane.showMessageDialog(null, "Usuário salvo com sucesso!");
        }catch(SQLException | HeadlessException e){
            System.out.println("Erro ao inserir o Usuário: "+ e);
        }finally{
            Conexao.fecharConexao(conexao, preparar);
        }
    }
    
    public void delUser(User user){
        Connection conexao = Conexao.getConexao();
        PreparedStatement preparar = null;
        
        try{
            preparar = conexao.prepareStatement("DELETE FROM user WHERE user.id = ?");
            preparar.setInt(1, user.getId());
            preparar.executeUpdate();
            preparar = conexao.prepareStatement("DELETE FROM logs WHERE logs.id_user = ?");
            preparar.setInt(1, user.getId());
            preparar.executeUpdate();
            JOptionPane.showMessageDialog(null, "Usuário deletado com sucesso!");
        }catch(SQLException | HeadlessException e){
            System.out.println("Erro ao deletar o usuário: "+ e);
        }finally{
            Conexao.fecharConexao(conexao, preparar);
        }
    }
        
    public void editUser(User user){
        Connection conexao = Conexao.getConexao();
        PreparedStatement preparar = null;
        
        try{
            preparar = conexao.prepareStatement("UPDATE user set name = ?, password = ? WHERE id = ?");
            preparar.setString(1, user.getName());
            preparar.setString(2, user.getPassword());
            preparar.setInt(3, user.getId());
            preparar.executeUpdate();
            JOptionPane.showMessageDialog(null, "Usuário editado com sucesso!");
        }catch(SQLException | HeadlessException e){
            System.out.println("Erro ao editar o usuário: "+ e);
        }finally{
            Conexao.fecharConexao(conexao, preparar);
        }
    }
    
    public List<User> listUsers(){
        Connection conexao = Conexao.getConexao();
        PreparedStatement preparar = null;
        ResultSet resultado;
        List<User> users = new ArrayList<>();
        
        try{
            preparar = conexao.prepareStatement("SELECT * FROM user");
            resultado = preparar.executeQuery();
            while(resultado.next()){
                User user = new User();
                user.setId(resultado.getInt("id"));
                user.setName(resultado.getString("name"));
                user.setPassword(resultado.getString("password"));
                users.add(user);
            }
        }catch(Exception e){
            System.out.println("Erro ao ler usuários normal: "+ e);
        }finally{
            Conexao.fecharConexao(conexao, preparar);
        }
        return users;
    }
    
    public User listUsersById(int id){
        Connection conexao = Conexao.getConexao();
        PreparedStatement preparar = null;
        ResultSet resultado;
        User user = new User();
        
        try{
            preparar = conexao.prepareStatement("SELECT * FROM user WHERE id = ?");
            preparar.setInt(1, id);
            resultado = preparar.executeQuery();
            while(resultado.next()){
                user.setId(resultado.getInt("id"));
                user.setName(resultado.getString("name"));
                user.setPassword(resultado.getString("password"));
            }
        }catch(Exception e){
            System.out.println("Erro ao ler usuários pelo id: "+ e);
        }finally{
            Conexao.fecharConexao(conexao, preparar);
        }
        return user;
    }
    
}
