package wifipassserver;

import java.io.*;
import static java.lang.Math.max;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import static wifipassserver.Servidor.br;

public class Worker implements Runnable{
    
    protected Socket cs;
    protected DataOutputStream salidaCliente;
    protected String mensajeCliente, DATA = "";
    public static final String br = System.getProperty("line.separator");
    protected String _DEBUG = "";
    
    Worker(Socket cs) throws IOException{
        this.cs = cs;
    }   
    
    @Override
    public void run(){
        try {
            salidaCliente = new DataOutputStream(cs.getOutputStream());
            
            BufferedReader entrada = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            
            String username = "";
            int i = 0;
            while((mensajeCliente = entrada.readLine()) != null)
            {
                i += 1;
                if (i == 1) {
                    username = mensajeCliente.substring(2,mensajeCliente.length());
                    _DEBUG += "______________________________________"+br+"NAME: "+username+br+"----------------------------------"+br;
                }
                if (i > 1) {
                    DATA += mensajeCliente+br;
                    _DEBUG += mensajeCliente+br;
                }
            }
            cs.close();
            
            TextFileWriter f = new TextFileWriter(DATA, username);
            f.writeTextFile();
            
            _DEBUG += "______________________________________"+br+br+br;
        } catch (IOException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(_DEBUG);
    }
}

class TextFileWriter {

    String txt, username;
    
    TextFileWriter(String txt, String username) {
        this.txt = txt;
        this.username = username;
    }
    
    public void writeTextFile() {
        BufferedWriter writer = null;
        try {
            String timeLog = new SimpleDateFormat("yyyy_MM_dd_HH.mm.ss").format(Calendar.getInstance().getTime());
            File logFile = new File(username+"_"+timeLog+"-----"+String.valueOf(Math.random())+".txt");

            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write(txt);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
                Logger.getLogger(TextFileWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
