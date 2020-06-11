import java.net.Socket;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;

public class Servidor {
	private ServerSocket server;
	private int port;
	private final int last_point = 65535;
	private Socket client;
	private final String repositoryAddress = "localFiles";
	private String allFiles;
	private ObjectOutputStream saida;
	
	public Servidor(int port) {
		if (port > 1024) this.port = port;
		else Erros.portCannotBeLessThan1024();
		
		this.allFiles = "";
		connectPort(port);
	}
	
	public void connectClient() {
		try {
			this.client = server.accept();
			startStream();
			sendToClient();
		} catch (IOException e) {
			e.printStackTrace();
			Erros.clientNotAccepted();
		}
	}
	
	public void showClient() {
		System.out.println("Cliente conectado: " + client.getInetAddress().getHostAddress());
	}
	
	public void changePort() {
		if (this.port <= this.last_point)
			this.port++; // tenta para a pr�xima porta.
		connectPort(port);
	}
	
	public void sendToClient() {
		shareFiles();
		try {
			saida.flush();
			saida.writeObject(allFiles);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void startStream() {
		try {
			saida = new ObjectOutputStream(client.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			Erros.unableToStream();
		}
	}
	
	public void shareFiles() {
		File arquivo = startRepository();
		shareFiles(arquivo);
	}
	
	private void shareFiles(File arquivo) {
		for (File arq : arquivo.listFiles()) {
			if (arq.isDirectory())
				shareFiles(arq);
			else
				allFiles += arq + "\n";
		}
	}

	private void connectPort(int port) {
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			Erros.portNotAvailable();
		}
	}
	
	public void close() {
		try {
			client.close();
			server.close();
			saida.close();
		} catch (IOException e) {
			e.printStackTrace();
			Erros.cannotClose();
		}
	}
	
	private File startRepository() {
		File arquivo = new File(repositoryAddress);
		if (!arquivo.exists())
			arquivo.mkdir();
		return arquivo;
	}
	
	// TODO: Colocar o acesso do cliente no servidor no modo multithread
	// TODO:	com os clientes dentro de um array
	// * OBS: visualizar a implementação no diretório chat-privado.
	public static void main(String[] args) {	
		final int port = 5858;	
		System.out.println("Iniciando..");
		Servidor server = new Servidor(port);
		System.out.println("Servidor ouvindo a porta " + port);
		server.connectClient();
		while (true) {
			switch(Menu.mainMenu()) {
			case 1: server.showClient(); break;
			case 2:
				server.close();
				System.out.println("Saindo do servidor...");
				System.exit(0);
			default:
				System.out.println("Insira um valor valido.");
			}
		}
	}
}

class Menu {
	public static int mainMenu() {
		System.out.println("Insira a opcaoo correspondente:\n"
				+ "\t1. Mostrar cliente.\n"
				+ "\t2. Fechar servidor\n");
		System.out.print("Qual sua opcaoo: ");
		Scanner scan = new Scanner(System.in);
		String value = scan.nextLine();
		if (value.matches("[0-9]+")) {
			scan.close();
			return Integer.valueOf(value);
		} else {
			System.out.println("Digite apenas n�meros.");
			scan.close();
			return mainMenu();
		}
	}
}

class Erros {
	public static void portNotAvailable() {
		System.out.println("A porta n�o est� dispon�vel. Tente mudar de porta "
				+ "com o m�todo changePort() em Servidor.");
	}
	
	public static void portCannotBeLessThan1024() {
		System.out.println("A porta n�o pode ser menor que 1024.\n"
				+ "S� devemos utilizar de 1024 em diante, pois as portas"
				+ "com n�meros abaixo deste s�o reservados para o uso do sistema\n"
				+ "Tente aumentar o valor ou utilizar o m�todo changePort() em Servidor.");
	}
	
	public static void clientNotAccepted() {
		System.out.println("Por algum motivo o cliente n�o foi aceito. Tente novamente.");
	}
	
	public static void cannotClose() {
		System.out.println("N�o foi poss�vel fechar a conex�o.");
	}
	
	public static void unableToStream() {
		System.out.println("N�o foi poss�vel criar a Stream");
	}
}

