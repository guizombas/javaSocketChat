import java.net.Socket;
import java.io.*;

public class ClienteTCP
{
	
	//METODO PRINCIPAL DA CLASSE
	public static void main (String args[])
	{
		var in = new BufferedReader(new InputStreamReader(System.in));
		try
		{
			//ENDERE�O DO SERVIDOR
			String IPServidor = "127.0.0.1";
			int PortaServidor = 7000;
			
			//ESTABELECE CONEX�O COM SERVIDOR
			System.out.println(" -C- Conectando ao servidor ->" + IPServidor + ":" +PortaServidor);
			Socket socktCli = new Socket (IPServidor,PortaServidor);
			System.out.println(" -C- Detalhes conexao :" + socktCli.toString()); //DETALHAMENTO (EXTRA)
			
			Thread receive = new Thread( () -> {
				String strMsg = "";
				while(socktCli.isConnected() && !strMsg.equals("%%d#i#s#c#o#n#n#e#c#t%%")){
					try {
						//CRIA UM PACOTE DE ENTRADA PARA RECEBER MENSAGENS, ASSOCIADO � CONEX�O (c)
						ObjectInputStream sCliIn = new ObjectInputStream (socktCli.getInputStream());
						strMsg = sCliIn.readObject().toString(); //ESPERA (BLOQUEADO) POR UM PACOTE
	
						//PROCESSA O PACOTE RECEBIDO
						//SE FOR STRING DE FIM DE CONEXÃO, MOSTRA A MENSAGEM CORRESPONDENTE
						System.out.println(
							!strMsg.equals("%%d#i#s#c#o#n#n#e#c#t%%") ?
							strMsg :
							"Desconectado. Digite enter para encerrar..."
						);
					} catch (Exception e) {
						//TODO: handle exception
					}
				}
				try {
					socktCli.close();
				} catch (Exception e) {
					//TODO: handle exception
				}
			} );
			Thread send = new Thread( () -> {
				String message = "";
				try {
					while(socktCli.isConnected()){
						message = in.readLine();
						//CRIA UM PACOTE DE SA�DA PARA ENVIAR MENSAGENS, ASSOCIANDO-O � CONEX�O (c)
						ObjectOutputStream sCliOut = new ObjectOutputStream(socktCli.getOutputStream());
						sCliOut.writeObject(message);//ESCREVE NO PACOTE
						sCliOut.flush(); //ENVIA O PACOTE
					}
				} catch (Exception e) {
					//TODO: handle exception
				}
				
				
			});
			
			ObjectInputStream sCliIn = new ObjectInputStream (socktCli.getInputStream());
			System.out.println(" -C- Recebendo mensagem...");
			String strMsg = sCliIn.readObject().toString(); //ESPERA (BLOQUEADO) POR UM PACOTE
			//PROCESSA O PACOTE RECEBIDO
			System.out.println(strMsg);

			receive.start();
			send.start();

		}
		catch(Exception e) //SE OCORRER ALGUMA EXCESS�O, ENT�O DEVE SER TRATADA (AMIGAVELMENTE)
		{
			System.out.println(" -C- O seguinte problema ocorreu : \n" + e.toString());
		}
	}
}		