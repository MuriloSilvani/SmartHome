package trabalhofinal;

import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class LogsDAO {
    public void newLog(Logs logs){
        Connection conexao = Conexao.getConexao();
        PreparedStatement preparar = null;
        
        try{
            preparar = conexao.prepareStatement("INSERT INTO logs (description, id_user) VALUES ( ?, ?)");
            preparar.setString(1, logs.getDescription());
            preparar.setInt(2, logs.getId_user());
            preparar.executeUpdate();
        }catch(SQLException | HeadlessException e){
            System.out.println("Erro ao inserir Log na DB: "+ e);
        }finally{
            Conexao.fecharConexao(conexao, preparar);
        }
    }
    
    
    public List<Logs> listLogsLogin(){
        Connection conexao = Conexao.getConexao();
        PreparedStatement preparar = null;
        ResultSet resultado;
        UserDAO usuariodao = new UserDAO();
        List<Logs> logs = new ArrayList<>();
        
        try{
            preparar = conexao.prepareStatement("SELECT * FROM logs WHERE description = 'Entrou na casa!' OR description = 'Tentativa de assalto!' OR description = 'Saiu da casa!' order by date desc limit 50");
            resultado = preparar.executeQuery();
            while(resultado.next()){
                User usuario = (User)usuariodao.listUsersById(resultado.getInt("id_user"));
                Logs log = new Logs();
                log.setId(resultado.getInt("id"));
                log.setDescription(resultado.getString("description"));
                log.setDate(resultado.getString("date"));
                log.setUser(usuario);
                log.setId_user(resultado.getInt("id_user"));
                logs.add(log);
            }
        }catch(Exception e){
            System.out.println("Erro ao ler logs login: "+ e);
        }finally{
            Conexao.fecharConexao(conexao, preparar);
        }
        return logs;
    }
    
    public List<Logs> listLogsLuz(){
        Connection conexao = Conexao.getConexao();
        PreparedStatement preparar = null;
        ResultSet resultado;
        UserDAO usuariodao = new UserDAO();
        List<Logs> logs = new ArrayList<>();
        
        try{
            preparar = conexao.prepareStatement("SELECT * FROM logs WHERE description = 'Todas as luzes apagadas!' OR description = 'Todas as luzes acesas!' OR description = 'Luz sala apagada!' OR description = 'Luz sala acesa!' OR description = 'Luz cozinha apagada!' OR description = 'Luz cozinha acesa!' OR description = 'Luz banheiro apagada!' OR description = 'Luz banheiro acesa!' OR description = 'Luz quarto1 acesa!' OR description = 'Luz quarto1 apagada!' OR description = 'Luz quarto2 acesa!' OR description = 'Luz quarto2 apagada!' OR description = 'Luz quarto3 acesa!' OR description = 'Luz quarto3 apagada!' order by date desc limit 50");
            resultado = preparar.executeQuery();
            while(resultado.next()){
                User usuario = (User)usuariodao.listUsersById(resultado.getInt("id_user"));
                Logs log = new Logs();
                log.setId(resultado.getInt("id"));
                log.setDescription(resultado.getString("description"));
                log.setDate(resultado.getString("date"));
                log.setUser(usuario);
                log.setId_user(resultado.getInt("id_user"));
                logs.add(log);
            }
        }catch(Exception e){
            System.out.println("Erro ao ler logs luzes: "+ e);
        }finally{
            Conexao.fecharConexao(conexao, preparar);
        }
        return logs;
    }
    
    public List<Logs> listLogsTemperatura(){
        Connection conexao = Conexao.getConexao();
        PreparedStatement preparar = null;
        ResultSet resultado;
        UserDAO usuariodao = new UserDAO();
        List<Logs> logs = new ArrayList<>();
        
        try{
            preparar = conexao.prepareStatement("SELECT * FROM logs WHERE description like '%Temperatura%' order by date desc limit 50");
            resultado = preparar.executeQuery();
            while(resultado.next()){
                User usuario = (User)usuariodao.listUsersById(resultado.getInt("id_user"));
                Logs log = new Logs();
                log.setId(resultado.getInt("id"));
                log.setDescription(resultado.getString("description"));
                log.setDate(resultado.getString("date"));
                log.setUser(usuario);
                log.setId_user(resultado.getInt("id_user"));
                logs.add(log);
            }
        }catch(Exception e){
            System.out.println("Erro ao ler logs temperatura: "+ e);
        }finally{
            Conexao.fecharConexao(conexao, preparar);
        }
        return logs;
    }
    
    
    public List<Logs> listLogsAlarme(){
        Connection conexao = Conexao.getConexao();
        PreparedStatement preparar = null;
        ResultSet resultado;
        UserDAO usuariodao = new UserDAO();
        List<Logs> logs = new ArrayList<>();
        
        try{
            preparar = conexao.prepareStatement("SELECT * FROM logs WHERE description = 'Alarme tocou!' OR description = 'Alarme ligado!' OR description = 'Alarme desligado!' order by date desc limit 50");
            resultado = preparar.executeQuery();
            while(resultado.next()){
                User usuario = (User)usuariodao.listUsersById(resultado.getInt("id_user"));
                Logs log = new Logs();
                log.setId(resultado.getInt("id"));
                log.setDescription(resultado.getString("description"));
                log.setDate(resultado.getString("date"));
                log.setUser(usuario);
                log.setId_user(resultado.getInt("id_user"));
                logs.add(log);
            }
        }catch(Exception e){
            System.out.println("Erro ao ler logs temperatura: "+ e);
        }finally{
            Conexao.fecharConexao(conexao, preparar);
        }
        return logs;
    }
    
    
    public List<Logs> listGraficoLuminozidade(){
        Connection conexao = Conexao.getConexao();
        PreparedStatement preparar = null;
        ResultSet resultado;
        List<Logs> logs = new ArrayList<>();
        
        try{
            preparar = conexao.prepareStatement("SELECT description, DATE_FORMAT(date,'%H') as hora FROM logs where description like '%Luminosidade%' order by date desc limit 10");
            resultado = preparar.executeQuery();
            while(resultado.next()){
                Logs log = new Logs();
                log.setDate(resultado.getString("hora"));
                String[] valor = resultado.getString("description").split(" ");
                log.setDescription(valor[1]);
                logs.add(log);
            }
        }catch(Exception e){
            System.out.println("Erro ao ler logs grafico luminosidade: "+ e);
        }finally{
            Conexao.fecharConexao(conexao, preparar);
        }
        return logs;
    }
    
    public List<Logs> listGraficoTemperatura(){
        Connection conexao = Conexao.getConexao();
        PreparedStatement preparar = null;
        ResultSet resultado;
        List<Logs> logs = new ArrayList<>();
        
        try{
            preparar = conexao.prepareStatement("SELECT id, RIGHT(description, 2) as temperatura, logs.date as data, id_user FROM logs WHERE description like '%Temperatura%' order by date DESC limit 5");
            resultado = preparar.executeQuery();
            while(resultado.next()){
                Logs log = new Logs();
                log.setId(resultado.getInt("id"));
                log.setDate(resultado.getString("data"));
                log.setDescription(resultado.getString("temperatura"));
                logs.add(log);
            }
        }catch(Exception e){
            System.out.println("Erro ao ler logs grafico temperatura: "+ e);
        }finally{
            Conexao.fecharConexao(conexao, preparar);
        }
        return logs;
    }
    
}
