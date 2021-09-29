import java.net.Socket;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.net.ServerSocket;
import java.io.*;

public class ServidorTCP
{
	private static void broadcast(String message, Collection<Socket> online, Socket conAtual){
		online.stream().filter(skt->!skt.equals(conAtual)).forEach((skt) -> {
			try {
				//CRIA UM PACOTE DE SA�DA PARA ENVIAR MENSAGENS, ASSOCIANDO-O � CONEX�O (p)
				ObjectOutputStream sSerOut = new ObjectOutputStream(skt.getOutputStream());
				sSerOut.writeObject(message); //ESCREVE NO PACOTE
				System.out.println(" -S- Enviando broadcast...");
				sSerOut.flush(); //ENVIA O PACOTE
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		});
	}
	private static void send(String message, Socket conSocket){
		try {
			//CRIA UM PACOTE DE SA�DA PARA ENVIAR MENSAGENS, ASSOCIANDO-O � CONEX�O (p)
			ObjectOutputStream sSerOut = new ObjectOutputStream(conSocket.getOutputStream());
			sSerOut.writeObject(message); //ESCREVE NO PACOTE
			System.out.println(" -S- Enviando mensagem..");
			sSerOut.flush(); //ENVIA O PACOTE
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	private static Object receive(Socket conSocket) throws Exception{
		return new ObjectInputStream(conSocket.getInputStream()).readObject();
	}

	//METODO PRINCIPAL DA CLASSE
	public static void main (String args[])
	{
		while(true)
		{
		try
		{
			int PortaServidor = 7000;
			
			//INICIALIZA UM SERVI�O DE ESCUTA POR CONEX�ES NA PORTA ESPECIFICADA
			System.out.println(" -S- Aguardando conexao (P:"+PortaServidor+")...");
			ServerSocket socktServ = new ServerSocket(PortaServidor);
			Boolean fakeBoolean = true;

			Set<Socket> online = new HashSet<Socket>();
			while (fakeBoolean) {
				//ESPERA (BLOQUEADO) POR CONEX�ES			
				Socket newConSer = socktServ.accept(); //RECEBE CONEX�O E CRIA UM NOVO CANAL (p) NO SENTIDO CONTR�RIO (SERVIDOR -> CLIENTE)
				System.out.println(" -S- Conectado ao cliente ->" + newConSer.toString());
				online.add(newConSer);

				final Socket conSer = newConSer;
				Thread thread = new Thread( () -> {
					try {

						ObjectOutputStream sServOut = new ObjectOutputStream(conSer.getOutputStream());
						sServOut.writeObject("Insira seu nome de exibição:");
						sServOut.flush();

						String name = (String) receive(conSer);

						broadcast(name + " entrou na conversa", online, conSer);

						Object msgIn;
						do {
							msgIn = receive(conSer);
							System.out.println(" -S- Recebido: " + msgIn.toString());
							
							broadcast( 
								name + (!msgIn.equals("sair") ? (": " + (String)msgIn) : " está saindo... ;-;") , 
								online, conSer
							);

						} while (!msgIn.equals("sair"));
						
						send("%%d#i#s#c#o#n#n#e#c#t%%", conSer);
						
						//FINALIZA A CONEX�O
						online.remove(conSer);
						conSer.close();
						System.out.println(" -S- Conexao finalizada...");
							
					} catch(EOFException e){
						System.out.println("Conexão com cliente perdida abruptamente: " + e.toString());
					} catch (Exception e) {
						System.out.println(e.toString());
					}
					
				});

				thread.start();
			}

			socktServ.close();
		}
		catch(Exception e) //SE OCORRER ALGUMA EXCESS�O, ENT�O DEVE SER TRATADA (AMIGAVELMENTE)
		{
			System.out.println(" -S- O seguinte problema ocorreu : \n" + e.toString());
		}
	}
	}
}