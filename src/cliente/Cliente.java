import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente {
	public static int setPort(Scanner scan) {
		System.out.print("Insira a porta desejada: ");
		String value = scan.nextLine();
		if (value.matches("[0-9]+"))
			return Integer.valueOf(value);
		System.out.println("Insira apenas numeros.");
		return setPort(scan);
	}
	
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		int porta = Cliente.setPort(scan);
		
		System.out.println("Starting...");
		Socket cliente;
		try {
			cliente = new Socket("localhost", porta);
			ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());
			System.out.println("Connected.");
					
			System.out.println("Comandos aceitáveis:"
			+ "\tls - revela os arquivos no local\n"
			+ "\texit - sair\n");

			while (true) {
				System.out.print("> ");
				switch (scan.nextLine()) {
				case "ls":
					String arquivos = (String) entrada.readObject();
					System.out.println(arquivos);
					break;
				case "exit":
					entrada.close();
					cliente.close();
					System.out.println("Saindo da conexao...");
					System.exit(0);
				default:
					System.out.println("Digite um comando valido.");
				}
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
