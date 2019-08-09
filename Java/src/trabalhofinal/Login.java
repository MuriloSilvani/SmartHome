package trabalhofinal;

import java.awt.Color;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import util.ArduinoSerial;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;
import java.util.List;

public class Login extends javax.swing.JFrame {

    // instancia UserLogado para setar um usuario dentro do sistema
    User userLogado = new User();
    // instancia Usuario para uso sem ter necessidade de intanciar em cada função
    User user = new User();
    // instancia UserDAO para uso sem ter necessidade de intanciar em cada função
    UserDAO userDAO = new UserDAO();
    // instancia Logs para uso sem ter necessidade de intanciar em cada função
    Logs logs = new Logs();
    // instancia LogsDAO para uso sem ter necessidade de intanciar em cada função
    LogsDAO logsDAO = new LogsDAO();
    // variavel representa status do alarme, 1 = on, 0 = off;
    private int statusAlarme = 1;
    
    
    // instancia Classe arduino para conexão e envio de dados pela porta serial
    ArduinoSerial arduino = new ArduinoSerial("COM4");
    String array[] = new String[4];
    
    public Login() {
        initComponents();
        // inicializa centralizado
        this.setLocationRelativeTo(null);
        // inicia paineis
        painelCasa.setVisible(false);
        painelCadastro.setVisible(false);
        painelDashboard.setVisible(false);
        painelLogin.setVisible(true);
        // inicia arduino
        arduino.initialize();
        
        
        
        Thread thread;
        thread = new Thread(){
            @Override
            public void run() {
                arduino.initialize();
                try {
                    sleep(4000);
                    int contador = 10;
                    int m=0;
                    while(true){
                        
                        //Criação do objeto bot com as informações de acesso
                        com.pengrad.telegrambot.TelegramBot bot = TelegramBotAdapter.build("866666672:AAF1gsnh3KF4vruEj7kx-DyaE7rO2Wj4zfs");
                        //objeto responsável por receber as mensagens
                        GetUpdatesResponse updatesResponse;
                        //objeto responsável por gerenciar o envio de respostas
                        SendResponse sendResponse;
                        //objeto responsável por gerenciar o envio de ações do chat
                        BaseResponse baseResponse;
                        String mensagem;
                        
                        array = arduino.read().split(";");
                        float temperaturaC = Float.parseFloat(array[0]);
                        float luminosidadeExterna = Float.parseFloat(array[1]);
                        float proximidade = Float.parseFloat(array[2]);
                        float umidade = Float.parseFloat(array[3]);
                        if(contador < 10){
                            contador++;
                        }else{
                            contador = 0;
                            logs.setLogs("Temperatura: "+temperaturaC);
                            logsDAO.newLog(logs);
                            logs.setLogs("Luminosidade: "+luminosidadeExterna);
                            logsDAO.newLog(logs);
                            logs.setLogs("Umidade: "+umidade);
                            logsDAO.newLog(logs);
                            labelTemperatura.setText(Float.toString(temperaturaC));
                            labelUmidade.setText(Float.toString(umidade));
                            labelLuminosidade.setText(Float.toString(luminosidadeExterna/10));
                        }
                        if((luminosidadeExterna > 280)&&(luminosidadeExterna < 320)){
                            if(luminosidadeExterna < 300){
                                logs.setLogs("Luz varanda acesa!");
                                logsDAO.newLog(logs);
                                varanda2.setBackground(new Color(255,255,255));
                            }
                        }else{
                            logs.setLogs("Luz varanda apagada!");
                            logsDAO.newLog(logs);
                            varanda2.setBackground(new Color(153,153,153));
                        }

                        //executa comando no Telegram para obter as mensagens pendentes a partir de um off-set (limite inicial)
                        updatesResponse =  bot.execute(new GetUpdates().limit(100).offset(m));
                        //lista de mensagens
                        List<Update> updates = updatesResponse.updates();
                        
//                        if(statusAlarme == 1){
//                            if(proximidade > 0 && proximidade < 7){
//                                Update update1 = new Update();
//                                System.out.println("alarme");
//                                sendResponse = bot.execute(new SendMessage(update1.message().chat().id(),"Alarme disparado!"));
//                            }
//                        }
                        
                        //análise de cada ação da mensagem
                        for (Update update : updates) {
                            mensagem = update.message().text();
                            
                            if(statusAlarme == 1){
                                if(proximidade > 0 && proximidade < 7){
//                                    Update update1 = new Update();
                                    System.out.println("alarme");
                                    sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Alarme disparado!"));
                                }
                            }

                            if(!"".equals(mensagem)){
                                switch(mensagem){
                                    case "/command1":
    //                                command1 - Ligar todas as Lâmpadas
                                        logs.setLogs("Todas as luzes acesas!",userLogado.getId());
                                        logsDAO.newLog(logs);
                                        quarto3.setBackground(new Color(255,255,255));
                                        banheiro.setBackground(new Color(255,255,255));
                                        sala.setBackground(new Color(255,255,255));
                                        corredor.setBackground(new Color(255,255,255));
                                        cozinha.setBackground(new Color(255,255,255));
                                        quarto1.setBackground(new Color(255,255,255));
                                        quarto2.setBackground(new Color(255,255,255));
                                        arduino.send("1");
                                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Todas as luzes acesas!"));
                                    break;
                                    case "/command2":
    //                                command2 - Apagar todas as Lâmpadas
                                        logs.setLogs("Todas as luzes apagadas!",userLogado.getId());
                                        logsDAO.newLog(logs);
                                        quarto3.setBackground(new Color(0,0,0));
                                        banheiro.setBackground(new Color(0,0,0));
                                        sala.setBackground(new Color(0,0,0));
                                        corredor.setBackground(new Color(0,0,0));
                                        cozinha.setBackground(new Color(0,0,0));
                                        quarto1.setBackground(new Color(0,0,0));
                                        quarto2.setBackground(new Color(0,0,0));
                                        arduino.send("0");
                                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Todas as luzes apagadas!"));
                                    break;
                                    case "/command3":
    //                                command3 - Luz Cozinha
                                        if(cozinha.getBackground().equals(new Color(255, 255, 255))){
                                            logs.setLogs("Luz cozinha apagada!",userLogado.getId());
                                            logsDAO.newLog(logs);
                                            cozinha.setBackground(new Color(0,0,0));
                                            arduino.send("3");
                                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Luz cozinha apagada!"));
                                        }else{
                                            logs.setLogs("Luz cozinha acesa!",userLogado.getId());
                                            logsDAO.newLog(logs);
                                            cozinha.setBackground(new Color(255,255,255));
                                            arduino.send("3");
                                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Luz cozinha acesa!"));
                                        }
                                    break;
                                    case "/command4":
    //                                command4 - Luz Sala
                                        if(sala.getBackground().equals(new Color(255, 255, 255))){
                                            logs.setLogs("Luz sala apagada!",userLogado.getId());
                                            logsDAO.newLog(logs);
                                            sala.setBackground(new Color(0,0,0));
                                            corredor.setBackground(new Color(0,0,0));
                                            arduino.send("2");
                                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Luz sala apagada!"));
                                        }else{
                                            logs.setLogs("Luz sala acesa!",userLogado.getId());
                                            logsDAO.newLog(logs);
                                            sala.setBackground(new Color(255,255,255));
                                            corredor.setBackground(new Color(255,255,255));
                                            arduino.send("2");
                                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Luz sala acesa!"));
                                        }
                                    break;
                                    case "/command5":
    //                                command5 - Luz Banheiro
                                        if(banheiro.getBackground().equals(new Color(255, 255, 255))){
                                            logs.setLogs("Luz banheiro apagada!",userLogado.getId());
                                            logsDAO.newLog(logs);
                                            banheiro.setBackground(new Color(0,0,0));
                                            arduino.send("7");
                                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Luz banheiro apagada!"));
                                        }else{
                                            logs.setLogs("Luz banheiro acesa!",userLogado.getId());
                                            logsDAO.newLog(logs);
                                            banheiro.setBackground(new Color(255,255,255));
                                            arduino.send("7");
                                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Luz banheiro acesa!"));
                                        }
                                    break;
                                    case "/command6":
    //                                command6 - Luz Quarto 1
                                        if(quarto1.getBackground().equals(new Color(255, 255, 255))){
                                            logs.setLogs("Luz quarto1 apagada!",userLogado.getId());
                                            logsDAO.newLog(logs);
                                            quarto1.setBackground(new Color(0,0,0));
                                            arduino.send("4");
                                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Luz quarto1 apagada!"));
                                        }else{
                                            logs.setLogs("Luz quarto1 acesa!",userLogado.getId());
                                            logsDAO.newLog(logs);
                                            quarto1.setBackground(new Color(255,255,255));
                                            arduino.send("4");
                                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Luz quarto1 acesa!"));
                                        }
                                    break;
                                    case "/command7":
    //                                command7 - Luz Quarto 2
                                        if(quarto2.getBackground().equals(new Color(255, 255, 255))){
                                            logs.setLogs("Luz quarto2 apagada!",userLogado.getId());
                                            logsDAO.newLog(logs);
                                            quarto2.setBackground(new Color(0,0,0));
                                            arduino.send("5");
                                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Luz quarto2 apagada!"));
                                        }else{
                                            logs.setLogs("Luz quarto2 acesa!",userLogado.getId());
                                            logsDAO.newLog(logs);
                                            quarto2.setBackground(new Color(255,255,255));
                                            arduino.send("5");
                                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Luz quarto2 acesa!"));
                                        }
                                    break;
                                    case "/command8":
    //                                command8 - Luz Quarto 3
                                        if(quarto3.getBackground().equals(new Color(255, 255, 255))){
                                            logs.setLogs("Luz quarto3 apagada!",userLogado.getId());
                                            logsDAO.newLog(logs);
                                            quarto3.setBackground(new Color(0,0,0));
                                            arduino.send("6");
                                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Luz quarto3 apagada!"));
                                        }else{
                                            logs.setLogs("Luz quarto3 acesa!",userLogado.getId());
                                            logsDAO.newLog(logs);
                                            quarto3.setBackground(new Color(255,255,255));
                                            arduino.send("6");
                                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Luz quarto3 acesa!"));
                                        }
                                    break;
                                    case "/command9":
    //                                command9 - Liga/Desliga Alarme
                                        if(statusAlarme == 0){
                                            statusAlarme = 1;
                                            arduino.send("8");
                                            logs.setLogs("Alarme ligado!",userLogado.getId());
                                            logsDAO.newLog(logs);
                                            labelAlarme.setText("Ligado");
                                            labelAlarme.setForeground(new Color(0,255,0));
                                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Alarme ligado!"));
                                        }else{
                                            statusAlarme = 0;
                                            arduino.send("8");
                                            logs.setLogs("Alarme desligado!",userLogado.getId());
                                            logsDAO.newLog(logs);
                                            labelAlarme.setText("Desligado");
                                            labelAlarme.setForeground(new Color(255,0,0));
                                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"Alarme desligado!"));
                                        }
                                    break;
                                    case "/command10":
    //                                command10 - Ver Status Alarme
                                        if(statusAlarme == 0){
                                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"O Alarme esta desligado!"));
                                        }else{
                                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(),"O Alarme esta ligado!"));
                                        }
                                    break;
                                    case "/command11":
    //                                command11 - Ver Luminosidade
                                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(),Float.toString(luminosidadeExterna/10)+"%"));
                                    break;
                                    case "/command12":
    //                                command12 - Ver Temperatura
                                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(),Float.toString(temperaturaC)+"°C"));
                                    break;
                                    case "/command13":
    //                                command13 - Ver Umidade
                                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(),Float.toString(umidade/10)+"%"));
                                    break;
                                }
                            }
                            m = 1+update.updateId();
                        }
                        mensagem = "";
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        thread.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        painelLogin = new javax.swing.JPanel();
        painelIcone = new javax.swing.JPanel();
        labelIcone = new javax.swing.JLabel();
        labelSmart = new javax.swing.JLabel();
        labelHome = new javax.swing.JLabel();
        painelLoginInterno = new javax.swing.JPanel();
        btnFechar = new javax.swing.JPanel();
        labelFechar = new javax.swing.JLabel();
        labelLogin = new javax.swing.JLabel();
        labelUsuario = new javax.swing.JLabel();
        inputName = new javax.swing.JTextField();
        labelSenha = new javax.swing.JLabel();
        inputPassword = new javax.swing.JPasswordField();
        labelAlert = new javax.swing.JLabel();
        btnEntrar = new javax.swing.JPanel();
        labelEntrar = new javax.swing.JLabel();
        btnCadastrar = new javax.swing.JPanel();
        labelCadastrar = new javax.swing.JLabel();
        painelCasa = new javax.swing.JPanel();
        panelMenutopo1 = new javax.swing.JPanel();
        labelOla1 = new javax.swing.JLabel();
        labelUsuarioLogado1 = new javax.swing.JLabel();
        labelDashboard3 = new javax.swing.JLabel();
        labelSair = new javax.swing.JLabel();
        menuLateral = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        labelTemperatura = new javax.swing.JLabel();
        varanda2 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        planta = new javax.swing.JPanel();
        corredor = new javax.swing.JPanel();
        quarto2 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        quarto1 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        banheiro = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        cozinha = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        sala = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        quarto3 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        labelLuminosidade = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        labelAlarme = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        labelUmidade = new javax.swing.JLabel();
        painelCadastro = new javax.swing.JPanel();
        panelMenutopo3 = new javax.swing.JPanel();
        labelOla3 = new javax.swing.JLabel();
        labelUsuarioLogado2 = new javax.swing.JLabel();
        labelDashboard6 = new javax.swing.JLabel();
        labelCadastro = new javax.swing.JLabel();
        labelUsuarioCadastro = new javax.swing.JLabel();
        inputNameCadastro = new javax.swing.JTextField();
        labelSenhaCadastro = new javax.swing.JLabel();
        inputPasswordCadastro = new javax.swing.JPasswordField();
        labelAlertCadastro = new javax.swing.JLabel();
        btnCadastrar1 = new javax.swing.JPanel();
        labelCadastrarBtn = new javax.swing.JLabel();
        btnEditar = new javax.swing.JPanel();
        labelEditarBtn = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        labelCadastro1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        painelDashboard = new javax.swing.JPanel();
        labelLogs = new javax.swing.JLabel();
        labelLogs1 = new javax.swing.JLabel();
        graficoTemp1 = new javax.swing.JPanel();
        geraGraficoTemp2 = new javax.swing.JLabel();
        graficoTemp = new javax.swing.JPanel();
        geraGraficoTemp = new javax.swing.JLabel();
        panelMenutopo2 = new javax.swing.JPanel();
        labelOla2 = new javax.swing.JLabel();
        labelUsuarioLogado3 = new javax.swing.JLabel();
        labelCasa = new javax.swing.JLabel();
        labelSair1 = new javax.swing.JLabel();
        menuLateral1 = new javax.swing.JPanel();
        labelRelatorioLogin = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        labelRelatorioTemp = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        labelTituloSmart = new javax.swing.JLabel();
        labelTituloHome = new javax.swing.JLabel();
        labelRelatorioTemp1 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        labelRelatorioLuz = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabelaLogs = new javax.swing.JTable();
        panelGrafico = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        painelIcone.setBackground(new java.awt.Color(50, 60, 128));
        painelIcone.setAlignmentX(0.0F);
        painelIcone.setAlignmentY(0.0F);

        labelIcone.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelIcone.setIcon(new javax.swing.ImageIcon(getClass().getResource("/trabalhofinal/smart-home.png"))); // NOI18N

        labelSmart.setBackground(new java.awt.Color(50, 60, 128));
        labelSmart.setFont(new java.awt.Font("Verdana", 0, 23)); // NOI18N
        labelSmart.setForeground(new java.awt.Color(255, 255, 255));
        labelSmart.setText("SMART");

        labelHome.setBackground(new java.awt.Color(50, 60, 128));
        labelHome.setFont(new java.awt.Font("Verdana", 1, 24)); // NOI18N
        labelHome.setForeground(new java.awt.Color(255, 255, 255));
        labelHome.setText("HOME");

        javax.swing.GroupLayout painelIconeLayout = new javax.swing.GroupLayout(painelIcone);
        painelIcone.setLayout(painelIconeLayout);
        painelIconeLayout.setHorizontalGroup(
            painelIconeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelIconeLayout.createSequentialGroup()
                .addContainerGap(117, Short.MAX_VALUE)
                .addGroup(painelIconeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labelIcone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(painelIconeLayout.createSequentialGroup()
                        .addComponent(labelSmart)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelHome)))
                .addContainerGap(135, Short.MAX_VALUE))
        );
        painelIconeLayout.setVerticalGroup(
            painelIconeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelIconeLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelIcone)
                .addGap(18, 18, 18)
                .addGroup(painelIconeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelSmart)
                    .addComponent(labelHome))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnFechar.setBackground(new java.awt.Color(50, 60, 128));
        btnFechar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnFecharMouseClicked(evt);
            }
        });

        labelFechar.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        labelFechar.setForeground(new java.awt.Color(255, 255, 255));
        labelFechar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelFechar.setText("X");
        labelFechar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelFechar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout btnFecharLayout = new javax.swing.GroupLayout(btnFechar);
        btnFechar.setLayout(btnFecharLayout);
        btnFecharLayout.setHorizontalGroup(
            btnFecharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelFechar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
        );
        btnFecharLayout.setVerticalGroup(
            btnFecharLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelFechar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
        );

        labelLogin.setBackground(new java.awt.Color(88, 93, 128));
        labelLogin.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        labelLogin.setForeground(new java.awt.Color(102, 102, 102));
        labelLogin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelLogin.setText("LOGIN");

        labelUsuario.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        labelUsuario.setForeground(new java.awt.Color(153, 153, 153));
        labelUsuario.setText("Usuário:");

        inputName.setForeground(new java.awt.Color(102, 102, 102));
        inputName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(50, 60, 128)));

        labelSenha.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        labelSenha.setForeground(new java.awt.Color(153, 153, 153));
        labelSenha.setText("Senha:");

        inputPassword.setForeground(new java.awt.Color(102, 102, 102));
        inputPassword.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(50, 60, 128)));

        labelAlert.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        labelAlert.setForeground(new java.awt.Color(153, 153, 153));

        btnEntrar.setBackground(new java.awt.Color(50, 60, 128));
        btnEntrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        labelEntrar.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        labelEntrar.setForeground(new java.awt.Color(255, 255, 255));
        labelEntrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelEntrar.setText("ENTRAR");
        labelEntrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelEntrarMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout btnEntrarLayout = new javax.swing.GroupLayout(btnEntrar);
        btnEntrar.setLayout(btnEntrarLayout);
        btnEntrarLayout.setHorizontalGroup(
            btnEntrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelEntrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
        );
        btnEntrarLayout.setVerticalGroup(
            btnEntrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelEntrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
        );

        btnCadastrar.setBackground(new java.awt.Color(176, 186, 255));
        btnCadastrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        labelCadastrar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        labelCadastrar.setForeground(new java.awt.Color(255, 255, 255));
        labelCadastrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelCadastrar.setText("  CADASTRAR");
        labelCadastrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        labelCadastrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelCadastrarMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout btnCadastrarLayout = new javax.swing.GroupLayout(btnCadastrar);
        btnCadastrar.setLayout(btnCadastrarLayout);
        btnCadastrarLayout.setHorizontalGroup(
            btnCadastrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelCadastrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        btnCadastrarLayout.setVerticalGroup(
            btnCadastrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelCadastrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout painelLoginInternoLayout = new javax.swing.GroupLayout(painelLoginInterno);
        painelLoginInterno.setLayout(painelLoginInternoLayout);
        painelLoginInternoLayout.setHorizontalGroup(
            painelLoginInternoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, painelLoginInternoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnFechar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(painelLoginInternoLayout.createSequentialGroup()
                .addContainerGap(238, Short.MAX_VALUE)
                .addGroup(painelLoginInternoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelAlert, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(painelLoginInternoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(labelSenha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(inputName)
                        .addComponent(labelUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(painelLoginInternoLayout.createSequentialGroup()
                            .addComponent(btnEntrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnCadastrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(labelLogin, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(painelLoginInternoLayout.createSequentialGroup()
                            .addComponent(inputPassword)
                            .addGap(3, 3, 3))))
                .addContainerGap(238, Short.MAX_VALUE))
        );
        painelLoginInternoLayout.setVerticalGroup(
            painelLoginInternoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelLoginInternoLayout.createSequentialGroup()
                .addComponent(btnFechar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 210, Short.MAX_VALUE)
                .addComponent(labelLogin)
                .addGap(18, 18, 18)
                .addComponent(labelUsuario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputName, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addComponent(labelSenha)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelAlert, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(painelLoginInternoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEntrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCadastrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(244, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout painelLoginLayout = new javax.swing.GroupLayout(painelLogin);
        painelLogin.setLayout(painelLoginLayout);
        painelLoginLayout.setHorizontalGroup(
            painelLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelLoginLayout.createSequentialGroup()
                .addComponent(painelIcone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(painelLoginInterno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        painelLoginLayout.setVerticalGroup(
            painelLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(painelIcone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(painelLoginInterno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        panelMenutopo1.setBackground(new java.awt.Color(176, 186, 255));
        panelMenutopo1.setAlignmentX(0.0F);
        panelMenutopo1.setAlignmentY(0.0F);

        labelOla1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        labelOla1.setForeground(new java.awt.Color(255, 255, 255));
        labelOla1.setText("Olá, ");

        labelUsuarioLogado1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        labelUsuarioLogado1.setForeground(new java.awt.Color(255, 255, 255));
        labelUsuarioLogado1.setText("usuário");

        labelDashboard3.setBackground(new java.awt.Color(50, 60, 128));
        labelDashboard3.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        labelDashboard3.setForeground(new java.awt.Color(255, 255, 255));
        labelDashboard3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelDashboard3.setText("DASHBOARD");
        labelDashboard3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelDashboard3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelDashboard3MouseClicked(evt);
            }
        });

        labelSair.setBackground(new java.awt.Color(50, 60, 128));
        labelSair.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        labelSair.setForeground(new java.awt.Color(255, 255, 255));
        labelSair.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelSair.setText("SAIR");
        labelSair.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelSair.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelSairMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panelMenutopo1Layout = new javax.swing.GroupLayout(panelMenutopo1);
        panelMenutopo1.setLayout(panelMenutopo1Layout);
        panelMenutopo1Layout.setHorizontalGroup(
            panelMenutopo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMenutopo1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(labelOla1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelUsuarioLogado1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 629, Short.MAX_VALUE)
                .addComponent(labelDashboard3, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelSair, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelMenutopo1Layout.setVerticalGroup(
            panelMenutopo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelUsuarioLogado1, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
            .addComponent(labelOla1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(labelDashboard3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(labelSair, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        menuLateral.setBackground(new java.awt.Color(50, 60, 128));
        menuLateral.setAlignmentX(0.0F);
        menuLateral.setAlignmentY(0.0F);
        menuLateral.setLayout(null);

        jLabel3.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("  LIGAR TODAS LÂMPADAS");
        jLabel3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        menuLateral.add(jLabel3);
        jLabel3.setBounds(20, 250, 180, 40);

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/trabalhofinal/smart-home.png"))); // NOI18N
        jLabel4.setText("jLabel4");
        menuLateral.add(jLabel4);
        jLabel4.setBounds(50, 50, 130, 130);

        jPanel2.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        menuLateral.add(jPanel2);
        jPanel2.setBounds(20, 290, 180, 1);

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        menuLateral.add(jPanel1);
        jPanel1.setBounds(20, 250, 180, 1);

        jLabel5.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("  APAGAR TODAS LÂMPADAS");
        jLabel5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });
        menuLateral.add(jLabel5);
        jLabel5.setBounds(20, 290, 180, 40);

        jPanel3.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        menuLateral.add(jPanel3);
        jPanel3.setBounds(20, 330, 180, 1);

        jLabel6.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("  LUZ COZINHA");
        jLabel6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });
        menuLateral.add(jLabel6);
        jLabel6.setBounds(20, 330, 180, 40);

        jPanel4.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        menuLateral.add(jPanel4);
        jPanel4.setBounds(20, 370, 180, 1);

        jLabel7.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("  LUZ SALA");
        jLabel7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });
        menuLateral.add(jLabel7);
        jLabel7.setBounds(20, 370, 180, 40);

        jPanel5.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        menuLateral.add(jPanel5);
        jPanel5.setBounds(20, 410, 180, 1);

        jLabel9.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("  LUZ BANHEIRO");
        jLabel9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel9MouseClicked(evt);
            }
        });
        menuLateral.add(jLabel9);
        jLabel9.setBounds(20, 410, 180, 40);

        jPanel7.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        menuLateral.add(jPanel7);
        jPanel7.setBounds(20, 450, 180, 1);

        jLabel8.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("  LUZ QUARTO 1");
        jLabel8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel8MouseClicked(evt);
            }
        });
        menuLateral.add(jLabel8);
        jLabel8.setBounds(20, 450, 180, 40);

        jPanel6.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        menuLateral.add(jPanel6);
        jPanel6.setBounds(20, 490, 180, 1);

        jLabel10.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("  LUZ QUARTO 2");
        jLabel10.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });
        menuLateral.add(jLabel10);
        jLabel10.setBounds(20, 490, 180, 40);

        jPanel9.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        menuLateral.add(jPanel9);
        jPanel9.setBounds(20, 530, 180, 1);

        jPanel11.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        menuLateral.add(jPanel11);
        jPanel11.setBounds(20, 490, 180, 1);

        jLabel11.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("  LUZ QUARTO 2");
        jLabel11.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        menuLateral.add(jLabel11);
        jLabel11.setBounds(20, 490, 180, 40);

        jPanel13.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        menuLateral.add(jPanel13);
        jPanel13.setBounds(20, 570, 180, 1);

        jLabel12.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("  LUZ QUARTO 3");
        jLabel12.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel12MouseClicked(evt);
            }
        });
        menuLateral.add(jLabel12);
        jLabel12.setBounds(20, 530, 180, 40);

        jLabel14.setFont(new java.awt.Font("Verdana", 0, 16)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("SMART");
        menuLateral.add(jLabel14);
        jLabel14.setBounds(60, 200, 60, 21);

        jLabel15.setFont(new java.awt.Font("Verdana", 1, 16)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("HOME");
        menuLateral.add(jLabel15);
        jLabel15.setBounds(120, 200, 60, 21);

        jLabel13.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("  LUZ QUARTO 3");
        jLabel13.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel13MouseClicked(evt);
            }
        });
        menuLateral.add(jLabel13);
        jLabel13.setBounds(20, 530, 180, 40);

        jLabel38.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(255, 255, 255));
        jLabel38.setText("ALARME");
        jLabel38.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel38.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel38MouseClicked(evt);
            }
        });
        menuLateral.add(jLabel38);
        jLabel38.setBounds(30, 570, 170, 40);

        jPanel14.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        menuLateral.add(jPanel14);
        jPanel14.setBounds(20, 610, 180, 1);

        jLabel22.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(102, 102, 102));
        jLabel22.setText("TEMPERATURA ATUAL:");

        jLabel23.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(102, 102, 102));
        jLabel23.setText("°C");

        labelTemperatura.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        labelTemperatura.setForeground(new java.awt.Color(102, 102, 102));
        labelTemperatura.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        varanda2.setBackground(new java.awt.Color(153, 153, 153));
        varanda2.setAlignmentX(0.0F);
        varanda2.setAlignmentY(0.0F);
        varanda2.setLayout(null);

        jLabel26.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setText("VARANDA");
        varanda2.add(jLabel26);
        jLabel26.setBounds(320, 6, 72, 130);

        planta.setBackground(new java.awt.Color(255, 255, 255));
        planta.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(88, 93, 128), 5));
        planta.setAlignmentX(0.0F);
        planta.setAlignmentY(0.0F);
        planta.setLayout(null);

        corredor.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout corredorLayout = new javax.swing.GroupLayout(corredor);
        corredor.setLayout(corredorLayout);
        corredorLayout.setHorizontalGroup(
            corredorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 670, Short.MAX_VALUE)
        );
        corredorLayout.setVerticalGroup(
            corredorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 70, Short.MAX_VALUE)
        );

        planta.add(corredor);
        corredor.setBounds(10, 200, 670, 70);

        quarto2.setBackground(new java.awt.Color(0, 0, 0));
        quarto2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(88, 93, 128), 5));
        quarto2.setForeground(new java.awt.Color(88, 93, 128));

        jLabel18.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(102, 102, 102));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("QUARTO 2");

        javax.swing.GroupLayout quarto2Layout = new javax.swing.GroupLayout(quarto2);
        quarto2.setLayout(quarto2Layout);
        quarto2Layout.setHorizontalGroup(
            quarto2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
        );
        quarto2Layout.setVerticalGroup(
            quarto2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
        );

        planta.add(quarto2);
        quarto2.setBounds(450, 0, 230, 200);

        quarto1.setBackground(new java.awt.Color(0, 0, 0));
        quarto1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(88, 93, 128), 5));
        quarto1.setForeground(new java.awt.Color(88, 93, 128));

        jLabel16.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(102, 102, 102));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("QUARTO 1");

        javax.swing.GroupLayout quarto1Layout = new javax.swing.GroupLayout(quarto1);
        quarto1.setLayout(quarto1Layout);
        quarto1Layout.setHorizontalGroup(
            quarto1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(quarto1Layout.createSequentialGroup()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 19, Short.MAX_VALUE))
        );
        quarto1Layout.setVerticalGroup(
            quarto1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
        );

        planta.add(quarto1);
        quarto1.setBounds(200, 0, 270, 200);

        banheiro.setBackground(new java.awt.Color(0, 0, 0));
        banheiro.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(88, 93, 128), 5));
        banheiro.setForeground(new java.awt.Color(88, 93, 128));

        jLabel20.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(102, 102, 102));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("BANHEIRO");

        javax.swing.GroupLayout banheiroLayout = new javax.swing.GroupLayout(banheiro);
        banheiro.setLayout(banheiroLayout);
        banheiroLayout.setHorizontalGroup(
            banheiroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
        );
        banheiroLayout.setVerticalGroup(
            banheiroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
        );

        planta.add(banheiro);
        banheiro.setBounds(350, 270, 140, 220);

        cozinha.setBackground(new java.awt.Color(0, 0, 0));
        cozinha.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(88, 93, 128), 5));
        cozinha.setForeground(new java.awt.Color(88, 93, 128));

        jLabel1.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("COZINHA");

        javax.swing.GroupLayout cozinhaLayout = new javax.swing.GroupLayout(cozinha);
        cozinha.setLayout(cozinhaLayout);
        cozinhaLayout.setHorizontalGroup(
            cozinhaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cozinhaLayout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                .addContainerGap())
        );
        cozinhaLayout.setVerticalGroup(
            cozinhaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
        );

        planta.add(cozinha);
        cozinha.setBounds(0, 0, 210, 200);

        sala.setBackground(new java.awt.Color(0, 0, 0));
        sala.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(88, 93, 128), 5));
        sala.setForeground(new java.awt.Color(88, 93, 128));

        jLabel21.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(102, 102, 102));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("SALA");

        javax.swing.GroupLayout salaLayout = new javax.swing.GroupLayout(sala);
        sala.setLayout(salaLayout);
        salaLayout.setHorizontalGroup(
            salaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(salaLayout.createSequentialGroup()
                .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        salaLayout.setVerticalGroup(
            salaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
        );

        planta.add(sala);
        sala.setBounds(0, 200, 360, 290);

        quarto3.setBackground(new java.awt.Color(0, 0, 0));
        quarto3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(88, 93, 128), 5));
        quarto3.setForeground(new java.awt.Color(88, 93, 128));

        jLabel19.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(102, 102, 102));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("QUARTO 3");

        javax.swing.GroupLayout quarto3Layout = new javax.swing.GroupLayout(quarto3);
        quarto3.setLayout(quarto3Layout);
        quarto3Layout.setHorizontalGroup(
            quarto3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
        );
        quarto3Layout.setVerticalGroup(
            quarto3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
        );

        planta.add(quarto3);
        quarto3.setBounds(480, 270, 200, 220);

        jLabel27.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(102, 102, 102));
        jLabel27.setText("LUMINOSIDADE:");

        labelLuminosidade.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        labelLuminosidade.setForeground(new java.awt.Color(102, 102, 102));
        labelLuminosidade.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelLuminosidade.setText(" ");

        jLabel29.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(102, 102, 102));
        jLabel29.setText("%");

        jLabel24.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(102, 102, 102));
        jLabel24.setText("ALARME:");

        labelAlarme.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        labelAlarme.setForeground(new java.awt.Color(0, 255, 0));
        labelAlarme.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelAlarme.setText("Ligado");

        jLabel28.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(102, 102, 102));
        jLabel28.setText("UMIDADE:");

        jLabel30.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(102, 102, 102));
        jLabel30.setText("%");

        labelUmidade.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        labelUmidade.setForeground(new java.awt.Color(102, 102, 102));
        labelUmidade.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelUmidade.setText(" ");

        javax.swing.GroupLayout painelCasaLayout = new javax.swing.GroupLayout(painelCasa);
        painelCasa.setLayout(painelCasaLayout);
        painelCasaLayout.setHorizontalGroup(
            painelCasaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelCasaLayout.createSequentialGroup()
                .addComponent(menuLateral, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(painelCasaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelMenutopo1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(painelCasaLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(painelCasaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(varanda2, javax.swing.GroupLayout.PREFERRED_SIZE, 683, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(planta, javax.swing.GroupLayout.PREFERRED_SIZE, 683, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(painelCasaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(painelCasaLayout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelAlarme, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(painelCasaLayout.createSequentialGroup()
                                .addGroup(painelCasaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(painelCasaLayout.createSequentialGroup()
                                        .addComponent(jLabel27)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(labelLuminosidade, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(painelCasaLayout.createSequentialGroup()
                                        .addComponent(jLabel28)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(labelUmidade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(painelCasaLayout.createSequentialGroup()
                                        .addComponent(jLabel22)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(labelTemperatura, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(painelCasaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel29)
                                    .addComponent(jLabel23)
                                    .addComponent(jLabel30))))
                        .addContainerGap())))
        );
        painelCasaLayout.setVerticalGroup(
            painelCasaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelCasaLayout.createSequentialGroup()
                .addComponent(panelMenutopo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(painelCasaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(painelCasaLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(planta, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(painelCasaLayout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(painelCasaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24)
                            .addComponent(labelAlarme))
                        .addGap(24, 24, 24)
                        .addGroup(painelCasaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel23)
                            .addComponent(labelTemperatura, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel22))
                        .addGap(25, 25, 25)
                        .addGroup(painelCasaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel27)
                            .addComponent(jLabel29)
                            .addComponent(labelLuminosidade))
                        .addGap(28, 28, 28)
                        .addGroup(painelCasaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel28)
                            .addComponent(jLabel30)
                            .addComponent(labelUmidade))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(varanda2, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(menuLateral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        panelMenutopo3.setBackground(new java.awt.Color(176, 186, 255));
        panelMenutopo3.setAlignmentX(0.0F);
        panelMenutopo3.setAlignmentY(0.0F);

        labelOla3.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        labelOla3.setForeground(new java.awt.Color(255, 255, 255));
        labelOla3.setText("Olá, ");

        labelUsuarioLogado2.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        labelUsuarioLogado2.setForeground(new java.awt.Color(255, 255, 255));
        labelUsuarioLogado2.setText("Visitante");

        labelDashboard6.setBackground(new java.awt.Color(50, 60, 128));
        labelDashboard6.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        labelDashboard6.setForeground(new java.awt.Color(255, 255, 255));
        labelDashboard6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelDashboard6.setText("LOGIN");
        labelDashboard6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelDashboard6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelDashboard6MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panelMenutopo3Layout = new javax.swing.GroupLayout(panelMenutopo3);
        panelMenutopo3.setLayout(panelMenutopo3Layout);
        panelMenutopo3Layout.setHorizontalGroup(
            panelMenutopo3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMenutopo3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(labelOla3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelUsuarioLogado2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelDashboard6, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelMenutopo3Layout.setVerticalGroup(
            panelMenutopo3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMenutopo3Layout.createSequentialGroup()
                .addComponent(labelUsuarioLogado2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(labelOla3, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
            .addComponent(labelDashboard6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        labelCadastro.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        labelCadastro.setForeground(new java.awt.Color(102, 102, 102));
        labelCadastro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelCadastro.setText("CADASTRO");

        labelUsuarioCadastro.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        labelUsuarioCadastro.setForeground(new java.awt.Color(153, 153, 153));
        labelUsuarioCadastro.setText("Usuário:");

        inputNameCadastro.setForeground(new java.awt.Color(102, 102, 102));
        inputNameCadastro.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(50, 60, 128)));

        labelSenhaCadastro.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        labelSenhaCadastro.setForeground(new java.awt.Color(153, 153, 153));
        labelSenhaCadastro.setText("Senha:");

        inputPasswordCadastro.setForeground(new java.awt.Color(102, 102, 102));
        inputPasswordCadastro.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(50, 60, 128)));

        labelAlertCadastro.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        labelAlertCadastro.setForeground(new java.awt.Color(153, 153, 153));

        btnCadastrar1.setBackground(new java.awt.Color(50, 60, 128));
        btnCadastrar1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        labelCadastrarBtn.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        labelCadastrarBtn.setForeground(new java.awt.Color(255, 255, 255));
        labelCadastrarBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelCadastrarBtn.setText("CADASTRAR");
        labelCadastrarBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelCadastrarBtnMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout btnCadastrar1Layout = new javax.swing.GroupLayout(btnCadastrar1);
        btnCadastrar1.setLayout(btnCadastrar1Layout);
        btnCadastrar1Layout.setHorizontalGroup(
            btnCadastrar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelCadastrarBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        btnCadastrar1Layout.setVerticalGroup(
            btnCadastrar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelCadastrarBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
        );

        btnEditar.setBackground(new java.awt.Color(99, 121, 255));
        btnEditar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        labelEditarBtn.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        labelEditarBtn.setForeground(new java.awt.Color(255, 255, 255));
        labelEditarBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelEditarBtn.setText("EDITAR");
        labelEditarBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        labelEditarBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelEditarBtnMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout btnEditarLayout = new javax.swing.GroupLayout(btnEditar);
        btnEditar.setLayout(btnEditarLayout);
        btnEditarLayout.setHorizontalGroup(
            btnEditarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnEditarLayout.createSequentialGroup()
                .addComponent(labelEditarBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        btnEditarLayout.setVerticalGroup(
            btnEditarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelEditarBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel15.setBackground(new java.awt.Color(176, 186, 255));

        jLabel17.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("DELETAR");
        jLabel17.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel17MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
        );

        jPanel16.setBackground(new java.awt.Color(50, 60, 128));
        jPanel16.setPreferredSize(new java.awt.Dimension(10, 0));

        labelCadastro1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        labelCadastro1.setForeground(new java.awt.Color(255, 255, 255));
        labelCadastro1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelCadastro1.setText("USUÁRIOS CADASTRADOS");

        jTable1.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "ID", "Usuário", "Senha"
            }
        ));
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap(84, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62))
            .addComponent(labelCadastro1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addComponent(labelCadastro1)
                .addGap(74, 74, 74)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(208, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout painelCadastroLayout = new javax.swing.GroupLayout(painelCadastro);
        painelCadastro.setLayout(painelCadastroLayout);
        painelCadastroLayout.setHorizontalGroup(
            painelCadastroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelMenutopo3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(painelCadastroLayout.createSequentialGroup()
                .addGroup(painelCadastroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(painelCadastroLayout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addGroup(painelCadastroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(painelCadastroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(labelSenhaCadastro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(inputNameCadastro)
                                .addComponent(labelUsuarioCadastro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(painelCadastroLayout.createSequentialGroup()
                                    .addGroup(painelCadastroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(inputPasswordCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(painelCadastroLayout.createSequentialGroup()
                                            .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addGap(3, 3, 3)))
                            .addGroup(painelCadastroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(btnCadastrar1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(labelAlertCadastro, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE))))
                    .addComponent(labelCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 208, Short.MAX_VALUE)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        painelCadastroLayout.setVerticalGroup(
            painelCadastroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelCadastroLayout.createSequentialGroup()
                .addComponent(panelMenutopo3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(painelCadastroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(painelCadastroLayout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(labelCadastro)
                        .addGap(72, 72, 72)
                        .addComponent(labelUsuarioCadastro)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputNameCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17)
                        .addComponent(labelSenhaCadastro)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputPasswordCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelAlertCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCadastrar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(painelCadastroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnEditar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 689, Short.MAX_VALUE)))
        );

        labelLogs.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        labelLogs.setForeground(new java.awt.Color(102, 102, 102));
        labelLogs.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelLogs.setText("LOGS");

        labelLogs1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        labelLogs1.setForeground(new java.awt.Color(102, 102, 102));
        labelLogs1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelLogs1.setText("GRÁFICOS");

        graficoTemp1.setBackground(new java.awt.Color(50, 60, 128));
        graficoTemp1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        geraGraficoTemp2.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        geraGraficoTemp2.setForeground(new java.awt.Color(255, 255, 255));
        geraGraficoTemp2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        geraGraficoTemp2.setText("LUMINOSIDADE");
        geraGraficoTemp2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                geraGraficoTemp2MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout graficoTemp1Layout = new javax.swing.GroupLayout(graficoTemp1);
        graficoTemp1.setLayout(graficoTemp1Layout);
        graficoTemp1Layout.setHorizontalGroup(
            graficoTemp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, graficoTemp1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(geraGraficoTemp2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        graficoTemp1Layout.setVerticalGroup(
            graficoTemp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(geraGraficoTemp2, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        graficoTemp.setBackground(new java.awt.Color(50, 60, 128));
        graficoTemp.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        geraGraficoTemp.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        geraGraficoTemp.setForeground(new java.awt.Color(255, 255, 255));
        geraGraficoTemp.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        geraGraficoTemp.setText("TEMPERATURA");
        geraGraficoTemp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                geraGraficoTempMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout graficoTempLayout = new javax.swing.GroupLayout(graficoTemp);
        graficoTemp.setLayout(graficoTempLayout);
        graficoTempLayout.setHorizontalGroup(
            graficoTempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, graficoTempLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(geraGraficoTemp, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        graficoTempLayout.setVerticalGroup(
            graficoTempLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(geraGraficoTemp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        panelMenutopo2.setBackground(new java.awt.Color(176, 186, 255));
        panelMenutopo2.setAlignmentX(0.0F);
        panelMenutopo2.setAlignmentY(0.0F);

        labelOla2.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        labelOla2.setForeground(new java.awt.Color(255, 255, 255));
        labelOla2.setText("Olá, ");

        labelUsuarioLogado3.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        labelUsuarioLogado3.setForeground(new java.awt.Color(255, 255, 255));
        labelUsuarioLogado3.setText("usuário");

        labelCasa.setBackground(new java.awt.Color(50, 60, 128));
        labelCasa.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        labelCasa.setForeground(new java.awt.Color(255, 255, 255));
        labelCasa.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelCasa.setText("CASA");
        labelCasa.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelCasa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelCasaMouseClicked(evt);
            }
        });

        labelSair1.setBackground(new java.awt.Color(50, 60, 128));
        labelSair1.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        labelSair1.setForeground(new java.awt.Color(255, 255, 255));
        labelSair1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelSair1.setText("SAIR");
        labelSair1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelSair1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelSair1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panelMenutopo2Layout = new javax.swing.GroupLayout(panelMenutopo2);
        panelMenutopo2.setLayout(panelMenutopo2Layout);
        panelMenutopo2Layout.setHorizontalGroup(
            panelMenutopo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMenutopo2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelOla2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelUsuarioLogado3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelCasa, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelSair1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelMenutopo2Layout.setVerticalGroup(
            panelMenutopo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMenutopo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(labelOla2, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                .addComponent(labelUsuarioLogado3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(labelCasa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(labelSair1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        menuLateral1.setBackground(new java.awt.Color(50, 60, 128));
        menuLateral1.setAlignmentX(0.0F);
        menuLateral1.setAlignmentY(0.0F);
        menuLateral1.setLayout(null);

        labelRelatorioLogin.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        labelRelatorioLogin.setForeground(new java.awt.Color(255, 255, 255));
        labelRelatorioLogin.setText("  RELATÓRIO LOGIN");
        labelRelatorioLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelRelatorioLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelRelatorioLoginMouseClicked(evt);
            }
        });
        menuLateral1.add(labelRelatorioLogin);
        labelRelatorioLogin.setBounds(20, 250, 180, 40);

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/trabalhofinal/smart-home.png"))); // NOI18N
        jLabel25.setText("jLabel4");
        menuLateral1.add(jLabel25);
        jLabel25.setBounds(50, 50, 130, 130);

        jPanel17.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        menuLateral1.add(jPanel17);
        jPanel17.setBounds(20, 290, 180, 1);

        jPanel18.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        menuLateral1.add(jPanel18);
        jPanel18.setBounds(20, 250, 180, 1);

        jPanel19.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        menuLateral1.add(jPanel19);
        jPanel19.setBounds(20, 330, 180, 1);

        labelRelatorioTemp.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        labelRelatorioTemp.setForeground(new java.awt.Color(255, 255, 255));
        labelRelatorioTemp.setText("  RELATÓRIO TEMPERATURA");
        labelRelatorioTemp.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelRelatorioTemp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelRelatorioTempMouseClicked(evt);
            }
        });
        menuLateral1.add(labelRelatorioTemp);
        labelRelatorioTemp.setBounds(20, 330, 180, 40);

        jPanel20.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        menuLateral1.add(jPanel20);
        jPanel20.setBounds(20, 370, 180, 1);

        labelTituloSmart.setFont(new java.awt.Font("Verdana", 0, 16)); // NOI18N
        labelTituloSmart.setForeground(new java.awt.Color(255, 255, 255));
        labelTituloSmart.setText("SMART");
        menuLateral1.add(labelTituloSmart);
        labelTituloSmart.setBounds(60, 200, 60, 21);

        labelTituloHome.setFont(new java.awt.Font("Verdana", 1, 16)); // NOI18N
        labelTituloHome.setForeground(new java.awt.Color(255, 255, 255));
        labelTituloHome.setText("HOME");
        menuLateral1.add(labelTituloHome);
        labelTituloHome.setBounds(120, 200, 60, 21);

        labelRelatorioTemp1.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        labelRelatorioTemp1.setForeground(new java.awt.Color(255, 255, 255));
        labelRelatorioTemp1.setText("  RELATÓRIO ALARME");
        labelRelatorioTemp1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelRelatorioTemp1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelRelatorioTemp1MouseClicked(evt);
            }
        });
        menuLateral1.add(labelRelatorioTemp1);
        labelRelatorioTemp1.setBounds(20, 370, 180, 40);

        jPanel21.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 180, Short.MAX_VALUE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        menuLateral1.add(jPanel21);
        jPanel21.setBounds(20, 410, 180, 1);

        labelRelatorioLuz.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
        labelRelatorioLuz.setForeground(new java.awt.Color(255, 255, 255));
        labelRelatorioLuz.setText("  RELATÓRIO DISPOSITIVOS");
        labelRelatorioLuz.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelRelatorioLuz.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelRelatorioLuzMouseClicked(evt);
            }
        });
        menuLateral1.add(labelRelatorioLuz);
        labelRelatorioLuz.setBounds(20, 290, 180, 40);

        tabelaLogs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Descrição", "Usuário", "Data/Hora"
            }
        ));
        jScrollPane2.setViewportView(tabelaLogs);

        panelGrafico.setBackground(new java.awt.Color(153, 153, 153));
        panelGrafico.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout painelDashboardLayout = new javax.swing.GroupLayout(painelDashboard);
        painelDashboard.setLayout(painelDashboardLayout);
        painelDashboardLayout.setHorizontalGroup(
            painelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelDashboardLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(menuLateral1, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(painelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelMenutopo2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(painelDashboardLayout.createSequentialGroup()
                        .addGroup(painelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelLogs, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(painelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(painelDashboardLayout.createSequentialGroup()
                                .addComponent(graficoTemp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(graficoTemp1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 302, Short.MAX_VALUE))
                            .addComponent(labelLogs1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelGrafico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        painelDashboardLayout.setVerticalGroup(
            painelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menuLateral1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(painelDashboardLayout.createSequentialGroup()
                .addComponent(panelMenutopo2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addGroup(painelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelLogs)
                    .addComponent(labelLogs1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(painelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(graficoTemp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(graficoTemp1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(painelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                    .addComponent(panelGrafico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(painelLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(painelCasa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(painelCadastro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(painelDashboard, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(painelLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(painelCasa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(painelCadastro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(painelDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFecharMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnFecharMouseClicked
        //fechar a tela quando clicar no botão de fechar customizado
        dispose();
    }//GEN-LAST:event_btnFecharMouseClicked

    private void labelCadastrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelCadastrarMouseClicked
        // navegação da tela de login para a tela de cadastro
        inputName.setText("");
        inputPassword.setText("");
        labelAlert.setText("");
        
        ListarTabelaUser();
        painelLogin.setVisible(false);
        painelCadastro.setVisible(true);
    }//GEN-LAST:event_labelCadastrarMouseClicked

    private void labelEntrarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelEntrarMouseClicked
        //função para efetuar o login
        int checkLogin = 0;
        //compara os usuários cadastrados às informações fornecidas no painel do login
        for(User user1: userDAO.listUsers()){
            if(user1.getName().equals(inputName.getText())){
                if(user1.getPassword().equals(inputName.getText())){
                    checkLogin = 1;
                    userLogado.setUser(user1.getId(),user1.getName(),user1.getPassword());
                    break;
                }
            }
        }
        if(checkLogin == 1){
            labelUsuarioLogado3.setText(userLogado.getName());
            labelUsuarioLogado1.setText(userLogado.getName());
            
            painelCasa.setVisible(true);
            painelLogin.setVisible(false);
            labelAlert.setText("");
            
            logs.setLogs("Entrou na casa!",userLogado.getId());
            logsDAO.newLog(logs);
            
            labelAlert.setText("");
        }else{
            labelAlert.setText("Esqueceu as Chaves?");
            
            logs.setLogs("Tentativa de assalto!");
            logsDAO.newLog(logs);
            arduino.send("9");
            statusAlarme = 1;
            labelAlarme.setText("Ligado");
            labelAlarme.setForeground(new Color(0,255,0));
        }
        inputName.setText("");
        inputPassword.setText("");
    }//GEN-LAST:event_labelEntrarMouseClicked

    private void labelSairMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelSairMouseClicked
        // resetar as variáveis de usuário quando clicar no botão de sair e redirecionar para a tela de login
        painelLogin.setVisible(true);
        painelCasa.setVisible(false);
        
        logs.setLogs("Saiu da casa!",userLogado.getId());
        logsDAO.newLog(logs);
        
        userLogado.setUser(0,"","");
    }//GEN-LAST:event_labelSairMouseClicked

    private void labelDashboard3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelDashboard3MouseClicked
        // navegação da página de dashboard para a planta da casa
        painelDashboard.setVisible(true);
        painelCasa.setVisible(false);
        
    }//GEN-LAST:event_labelDashboard3MouseClicked

    private void labelDashboard6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelDashboard6MouseClicked
        // navegação da página de cadastro para a página de login
        painelCadastro.setVisible(false);
        painelLogin.setVisible(true);

    }//GEN-LAST:event_labelDashboard6MouseClicked

    public void ListarTabelaUser(){
        //função para listar os usuários cadastrados na tabela
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        modelo.setNumRows(0);
        
        for(User user2: userDAO.listUsers()){
            modelo.addRow(new Object[]{
                user2.getId(),
                user2.getName(),
                user2.getPassword()
            });
                    
        }
    }
    
    private void labelCadastrarBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelCadastrarBtnMouseClicked
        //salvar o ususário com os dados dos inputs se forem diferentes de vazio
        if(inputNameCadastro.getText().equals("") || inputPasswordCadastro.getText().equals("")){
            labelAlertCadastro.setText("Complete o cadastro");
        }else{
            user.setUser(inputNameCadastro.getText(),inputPasswordCadastro.getText());
            userDAO.newUser(user);
            logs.setLogs("Usuário Criado!",user.getId());
            logsDAO.newLog(logs);
            inputNameCadastro.setText("");
            inputPasswordCadastro.setText("");
            labelAlertCadastro.setText("");
            ListarTabelaUser();
        }
    }//GEN-LAST:event_labelCadastrarBtnMouseClicked

    private void labelEditarBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelEditarBtnMouseClicked
        //editar o usuário selecionado na tabela de listagem
        if(jTable1.getSelectedRow() != -1){
            user.setUser((int)jTable1.getValueAt(jTable1.getSelectedRow(), 0),inputNameCadastro.getText(),inputPasswordCadastro.getText());
            userDAO.editUser(user);
            ListarTabelaUser();
        }
    }//GEN-LAST:event_labelEditarBtnMouseClicked

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        //carregar as informações do usuário selecionado na tabela nos inputs para a edição
        if(jTable1.getSelectedRow() != -1){
            inputNameCadastro.setText(jTable1.getValueAt(jTable1.getSelectedRow(),1).toString());
            inputPasswordCadastro.setText(jTable1.getValueAt(jTable1.getSelectedRow(),2).toString());
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jLabel17MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MouseClicked
        //deletar o usuário selecionado na tabela
        user.setUser((int)jTable1.getValueAt(jTable1.getSelectedRow(), 0),inputNameCadastro.getText(),inputPasswordCadastro.getText());
        userDAO.delUser(user);

        inputNameCadastro.setText("");
        inputPasswordCadastro.setText("");
        ListarTabelaUser();
    }//GEN-LAST:event_jLabel17MouseClicked

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        //alterar a cor do background dos cômodos da casa e enviar o sinal ao arduino para ligar todas as luzes
        logs.setLogs("Todas as luzes acesas!",userLogado.getId());
        logsDAO.newLog(logs);
        
        quarto3.setBackground(new Color(255,255,255));
        banheiro.setBackground(new Color(255,255,255));
        sala.setBackground(new Color(255,255,255));
        corredor.setBackground(new Color(255,255,255));
        cozinha.setBackground(new Color(255,255,255));
        quarto1.setBackground(new Color(255,255,255));
        quarto2.setBackground(new Color(255,255,255));
        arduino.send("1");
    }//GEN-LAST:event_jLabel3MouseClicked

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        //alterar a cor do background dos cômodos da casa e enviar o sinal ao arduino para apagar todas as luzes
        logs.setLogs("Todas as luzes apagadas!",userLogado.getId());
        logsDAO.newLog(logs);
        
        quarto3.setBackground(new Color(0,0,0));
        banheiro.setBackground(new Color(0,0,0));
        sala.setBackground(new Color(0,0,0));
        corredor.setBackground(new Color(0,0,0));
        cozinha.setBackground(new Color(0,0,0));
        quarto1.setBackground(new Color(0,0,0));
        quarto2.setBackground(new Color(0,0,0));
        arduino.send("0");
    }//GEN-LAST:event_jLabel5MouseClicked

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        // verificar se a luz da cozinha está acesa ou apagada e inverter a cor do background. Enviar o sinal ao arduino para inverter o status do led
        if(cozinha.getBackground().equals(new Color(255, 255, 255))){
            logs.setLogs("Luz cozinha apagada!",userLogado.getId());
            logsDAO.newLog(logs);

            cozinha.setBackground(new Color(0,0,0));
            arduino.send("3");
        }else{
            logs.setLogs("Luz cozinha acesa!",userLogado.getId());
            logsDAO.newLog(logs);

            cozinha.setBackground(new Color(255,255,255));
            arduino.send("3");
        }
    }//GEN-LAST:event_jLabel6MouseClicked

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        // verificar se a luz da sala está acesa ou apagada e inverter a cor do background. Enviar o sinal ao arduino para inverter o status do led
        if(sala.getBackground().equals(new Color(255, 255, 255))){
            logs.setLogs("Luz sala apagada!",userLogado.getId());
            logsDAO.newLog(logs);

            sala.setBackground(new Color(0,0,0));
            corredor.setBackground(new Color(0,0,0));
            arduino.send("2");
        }else{
            logs.setLogs("Luz sala acesa!",userLogado.getId());
            logsDAO.newLog(logs);

            sala.setBackground(new Color(255,255,255));
            corredor.setBackground(new Color(255,255,255));
            arduino.send("2");
        }
    }//GEN-LAST:event_jLabel7MouseClicked

    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseClicked
        // verificar se a luz do banheiro está acesa ou apagada e inverter a cor do background. Enviar o sinal ao arduino para inverter o status do led
        if(banheiro.getBackground().equals(new Color(255, 255, 255))){
            logs.setLogs("Luz banheiro apagada!",userLogado.getId());
            logsDAO.newLog(logs);

            banheiro.setBackground(new Color(0,0,0));
            arduino.send("7");
        }else{
            logs.setLogs("Luz banheiro acesa!",userLogado.getId());
            logsDAO.newLog(logs);

            banheiro.setBackground(new Color(255,255,255));
            arduino.send("7");
        }
    }//GEN-LAST:event_jLabel9MouseClicked

    private void jLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MouseClicked
        // verificar se a luz do quarto 1 está acesa ou apagada e inverter a cor do background. Enviar o sinal ao arduino para inverter o status do led
        if(quarto1.getBackground().equals(new Color(255, 255, 255))){
            logs.setLogs("Luz quarto1 apagada!",userLogado.getId());
            logsDAO.newLog(logs);

            quarto1.setBackground(new Color(0,0,0));
            arduino.send("4");
        }else{
            logs.setLogs("Luz quarto1 acesa!",userLogado.getId());
            logsDAO.newLog(logs);

            quarto1.setBackground(new Color(255,255,255));
            arduino.send("4");
        }
    }//GEN-LAST:event_jLabel8MouseClicked

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
        // verificar se a luz do quarto 2 está acesa ou apagada e inverter a cor do background. Enviar o sinal ao arduino para inverter o status do led
        if(quarto2.getBackground().equals(new Color(255, 255, 255))){
            logs.setLogs("Luz quarto2 apagada!",userLogado.getId());
            logsDAO.newLog(logs);

            quarto2.setBackground(new Color(0,0,0));
            arduino.send("5");
        }else{
            logs.setLogs("Luz quarto2 acesa!",userLogado.getId());
            logsDAO.newLog(logs);

            quarto2.setBackground(new Color(255,255,255));
            arduino.send("5");
        }
    }//GEN-LAST:event_jLabel10MouseClicked

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
        // verificar se a luz do quarto 3 está acesa ou apagada e inverter a cor do background. Enviar o sinal ao arduino para inverter o status do led
        if(quarto3.getBackground().equals(new Color(255, 255, 255))){
            logs.setLogs("Luz quarto3 apagada!",userLogado.getId());
            logsDAO.newLog(logs);

            quarto3.setBackground(new Color(0,0,0));
            arduino.send("6");
        }else{
            logs.setLogs("Luz quarto3 acesa!",userLogado.getId());
            logsDAO.newLog(logs);

            quarto3.setBackground(new Color(255,255,255));
            arduino.send("6");
        }
    }//GEN-LAST:event_jLabel12MouseClicked

    private void jLabel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel13MouseClicked

    private void jLabel38MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel38MouseClicked
        // verificar se o alarme está ligado ou não para alterar a label. Enviar o sinal ao arduino para acionar o sensor de proximidade
        if(statusAlarme == 0){
            statusAlarme = 1;
            arduino.send("8");
            logs.setLogs("Alarme ligado!",userLogado.getId());
            logsDAO.newLog(logs);
            labelAlarme.setText("Ligado");
            labelAlarme.setForeground(new Color(0,255,0));
        }else{
            statusAlarme = 0;
            arduino.send("8");
            logs.setLogs("Alarme desligado!",userLogado.getId());
            logsDAO.newLog(logs);
            labelAlarme.setText("Desligado");
            labelAlarme.setForeground(new Color(255,0,0));
        }
    }//GEN-LAST:event_jLabel38MouseClicked

    private void labelCasaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelCasaMouseClicked
        // navegação da tela de dashboard para a planta da casa
        painelDashboard.setVisible(false);
        painelCasa.setVisible(true);
    }//GEN-LAST:event_labelCasaMouseClicked

    private void labelSair1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelSair1MouseClicked
        // função de logout. Redireciona da tela de dashboard para a tela de login
        painelDashboard.setVisible(false);
        painelLogin.setVisible(true);
        
        logs.setLogs("Saiu da casa!",userLogado.getId());
        logsDAO.newLog(logs);
        
        userLogado.setUser(0,"","");
    }//GEN-LAST:event_labelSair1MouseClicked

    private void labelRelatorioLoginMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRelatorioLoginMouseClicked
        // carrega a tabela da dashboard com os logs relacionados ao login 
        DefaultTableModel modelo = (DefaultTableModel) tabelaLogs.getModel();
        modelo.setNumRows(0);
        LogsDAO logsdao = new LogsDAO();

        for(Logs logs: logsdao.listLogsLogin()){

            modelo.addRow(new Object[]{
                logs.getDescription(),
                logs.getUser(),
                logs.formatarData()
            });

        }
    }//GEN-LAST:event_labelRelatorioLoginMouseClicked

    private void labelRelatorioTempMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRelatorioTempMouseClicked
        // carrega a tabela da dashboard com os logs relacionados a temperatura 
        DefaultTableModel modelo = (DefaultTableModel) tabelaLogs.getModel();
        modelo.setNumRows(0);
        LogsDAO logsdao = new LogsDAO();

        for(Logs logs: logsdao.listLogsTemperatura()){

            modelo.addRow(new Object[]{
                logs.getDescription(),
                logs.getUser(),
                logs.formatarData()
            });

        }
    }//GEN-LAST:event_labelRelatorioTempMouseClicked

    private void labelRelatorioTemp1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRelatorioTemp1MouseClicked
        // carrega a tabela da dashboard com os logs relacionados ao alarme 
        DefaultTableModel modelo = (DefaultTableModel) tabelaLogs.getModel();
        modelo.setNumRows(0);
        LogsDAO logsdao = new LogsDAO();

        for(Logs logs: logsdao.listLogsAlarme()){
            modelo.addRow(new Object[]{
                logs.getDescription(),
                logs.getUser(),
                logs.formatarData()
            });

        }
    }//GEN-LAST:event_labelRelatorioTemp1MouseClicked

    private void labelRelatorioLuzMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelRelatorioLuzMouseClicked
        // carrega a tabela da dashboard com os logs relacionados a luminosidade
        DefaultTableModel modelo = (DefaultTableModel) tabelaLogs.getModel();
        modelo.setNumRows(0);
        LogsDAO logsdao = new LogsDAO();
        
        for(Logs logs: logsdao.listLogsLuz()){           
            
            modelo.addRow(new Object[]{
                logs.getDescription(),
                logs.getUser(),
                logs.formatarData()
            });
                    
        }
    }//GEN-LAST:event_labelRelatorioLuzMouseClicked

    private void geraGraficoTempMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_geraGraficoTempMouseClicked
        //criação do gráfico de barras de temperatura
        //monta o gráfico com base nas 5 últimas leituras de temperaturas
        DefaultCategoryDataset barra = new DefaultCategoryDataset();
        LogsDAO logdao = new LogsDAO();

        for(Logs log : logdao.listGraficoTemperatura()) {
            barra.setValue(Integer.parseInt(log.getDescription()), log.getDate(), "");
        }

        JFreeChart grafico = ChartFactory.createBarChart("Temperatura", "Data", "Valor", barra, PlotOrientation.VERTICAL, true, true, false);
        ChartPanel painel = new ChartPanel(grafico);
        panelGrafico.add(painel); 
        setVisible(true);
    }//GEN-LAST:event_geraGraficoTempMouseClicked

    private void geraGraficoTemp2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_geraGraficoTemp2MouseClicked
        //criação do gráfico de pontos de luminosidade
        // monta o gráfico listando os 10 últimos registros de luminozidade do banco de dados
        
        LogsDAO logdao = new LogsDAO();
        
        XYSeriesCollection dataset1 = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("Luzes");  
        int cont = 0;
        for(Logs log : logdao.listGraficoLuminozidade()) {
            cont++;
            series1.add(cont, (Float.parseFloat(log.getDescription())/10));
        }
        
       dataset1.addSeries(series1);
        
        XYDataset dataset = dataset1;
    
        JFreeChart grafico = ChartFactory.createScatterPlot("Luminozidade", "Indice", "Hora", dataset);
        
        ChartPanel painel = new ChartPanel(grafico);
        panelGrafico.add(painel); 
        setVisible(true);
    }//GEN-LAST:event_geraGraficoTemp2MouseClicked

    public static void main(String args[]) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel banheiro;
    private javax.swing.JPanel btnCadastrar;
    private javax.swing.JPanel btnCadastrar1;
    private javax.swing.JPanel btnEditar;
    private javax.swing.JPanel btnEntrar;
    private javax.swing.JPanel btnFechar;
    private javax.swing.JPanel corredor;
    private javax.swing.JPanel cozinha;
    private javax.swing.JLabel geraGraficoTemp;
    private javax.swing.JLabel geraGraficoTemp2;
    private javax.swing.JPanel graficoTemp;
    private javax.swing.JPanel graficoTemp1;
    private javax.swing.JTextField inputName;
    private javax.swing.JTextField inputNameCadastro;
    private javax.swing.JPasswordField inputPassword;
    private javax.swing.JPasswordField inputPasswordCadastro;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel labelAlarme;
    private javax.swing.JLabel labelAlert;
    private javax.swing.JLabel labelAlertCadastro;
    private javax.swing.JLabel labelCadastrar;
    private javax.swing.JLabel labelCadastrarBtn;
    private javax.swing.JLabel labelCadastro;
    private javax.swing.JLabel labelCadastro1;
    private javax.swing.JLabel labelCasa;
    private javax.swing.JLabel labelDashboard3;
    private javax.swing.JLabel labelDashboard6;
    private javax.swing.JLabel labelEditarBtn;
    private javax.swing.JLabel labelEntrar;
    private javax.swing.JLabel labelFechar;
    private javax.swing.JLabel labelHome;
    private javax.swing.JLabel labelIcone;
    private javax.swing.JLabel labelLogin;
    private javax.swing.JLabel labelLogs;
    private javax.swing.JLabel labelLogs1;
    private javax.swing.JLabel labelLuminosidade;
    private javax.swing.JLabel labelOla1;
    private javax.swing.JLabel labelOla2;
    private javax.swing.JLabel labelOla3;
    private javax.swing.JLabel labelRelatorioLogin;
    private javax.swing.JLabel labelRelatorioLuz;
    private javax.swing.JLabel labelRelatorioTemp;
    private javax.swing.JLabel labelRelatorioTemp1;
    private javax.swing.JLabel labelSair;
    private javax.swing.JLabel labelSair1;
    private javax.swing.JLabel labelSenha;
    private javax.swing.JLabel labelSenhaCadastro;
    private javax.swing.JLabel labelSmart;
    private javax.swing.JLabel labelTemperatura;
    private javax.swing.JLabel labelTituloHome;
    private javax.swing.JLabel labelTituloSmart;
    private javax.swing.JLabel labelUmidade;
    private javax.swing.JLabel labelUsuario;
    private javax.swing.JLabel labelUsuarioCadastro;
    private javax.swing.JLabel labelUsuarioLogado1;
    private javax.swing.JLabel labelUsuarioLogado2;
    private javax.swing.JLabel labelUsuarioLogado3;
    private javax.swing.JPanel menuLateral;
    private javax.swing.JPanel menuLateral1;
    private javax.swing.JPanel painelCadastro;
    private javax.swing.JPanel painelCasa;
    private javax.swing.JPanel painelDashboard;
    private javax.swing.JPanel painelIcone;
    private javax.swing.JPanel painelLogin;
    private javax.swing.JPanel painelLoginInterno;
    private javax.swing.JPanel panelGrafico;
    private javax.swing.JPanel panelMenutopo1;
    private javax.swing.JPanel panelMenutopo2;
    private javax.swing.JPanel panelMenutopo3;
    private javax.swing.JPanel planta;
    private javax.swing.JPanel quarto1;
    private javax.swing.JPanel quarto2;
    private javax.swing.JPanel quarto3;
    private javax.swing.JPanel sala;
    private javax.swing.JTable tabelaLogs;
    private javax.swing.JPanel varanda2;
    // End of variables declaration//GEN-END:variables
}
