package trabalhofinal;

import java.sql.Date;

public class Logs {
    private int id;
    private String description;
//    private Date date;
    private String date;
    private int id_user;
    private User user;
    private int count;
    
    public void setLogs(String description) {
        this.description = description;
    }
    public void setLogs(String description, int id_user) {
        this.description = description;
        this.id_user = id_user;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
//    public Date getDate() {
//        return date;
//    }
//    public void setDate(Date date) {
//        this.date = date;
//    }
    
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    
    public int getId_user() {
        return id_user;
    }
    public void setId_user(int id_user) {
        this.id_user = id_user;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public int getCount() {
        return this.count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public String formatarData(){
        String datetime = this.getDate();
        String[] separados = datetime.split(" ");
        String data = separados[0];
        String[] dataseparada = data.split("-");
        String dia = dataseparada[2];
        String mes = dataseparada[1];
        String ano = dataseparada[0];
        String hora = separados[1];
        return dia+"/"+mes+"/"+ano+" "+hora;
    }
}