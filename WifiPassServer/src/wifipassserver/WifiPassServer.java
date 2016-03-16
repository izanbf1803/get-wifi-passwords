package wifipassserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class WifiPassServer {

    public static void main(String[] args) throws IOException {
        Servidor serv = new Servidor(80);
        serv.StartServer();
    }
    
}

class Servidor {
        
    private int PUERTO;
    protected ServerSocket ss;
    protected Socket cs;
    protected DataOutputStream salidaCliente;
    protected String mensajeCliente, DATA = "";
    public static final String br = System.getProperty("line.separator");

    public Servidor(int puerto) throws IOException{
        this.PUERTO = puerto;
        StartServer();
    }
    
    public void StartServer() throws IOException {
        ss = new ServerSocket(PUERTO);
        RunServer();
    }

    private void RunServer() throws IOException {
        while (true) {
            Socket cs = new Socket();
            System.out.println("Esperando...");
            cs = ss.accept();
            
            Worker worker = new Worker(cs);
            new Thread(worker).start();
        }
    }
    
}  
