package cf.cvetkovic.Master;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.time.Instant;
import java.util.Scanner;
import java.util.UUID;


import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;


public class Main {
	
	
	
	public static Connection connection;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		/* Der Master erteilt über SSH Kommandos an den Server. 
		 * Zunächst wird ein alter ReCog-Container gestoppt.
		 * Darauf wird der Docker Daemon gesäubert
		 * Als letztes wird ein neuer ReCog-Container gestartet
		 * Der letzte Befehl gibt nur den Status in die Konsole aus
		 */
		
		try {
			//SSHClient.command("root", "2022ModernTechnology", "46.101.214.73", 22, "docker kill ReCog");
			//SSHClient.command("root", "2022ModernTechnology", "46.101.214.73", 22, "docker rm ReCog");
			
			
			//SSHClient.command("root", "2022ModernTechnology", "46.101.214.73", 22, "docker run -t -d -v /home/FaceDB:/home/FaceDB --name ReCog animcogn/face_recognition");
			SSHClient.command("root", "2022ModernTechnology", "46.101.214.73", 22, "docker ps");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		org.apache.log4j.BasicConfigurator.configure();
		DevInterface.createGUI();
		
		//MariaDB Verbindung wird hergestellt
		
		
		
		//Test für das kopieren der Dateien und starten der Gesichtserkennung
		//runRecognition();
		
	}
	
	
	public static String runRecognition(boolean performRegister) {
		System.out.println("Start Recognition");
		MariaDB_Client.connect();
		try(InputStream in = new URL("http://192.168.0.32/jpg").openStream()){
		    Files.copy(in, Paths.get("/Users/jordancvetkovic/Projekte/TestRepo/Target.jpg"), StandardCopyOption.REPLACE_EXISTING);
		} catch (MalformedURLException e1) {e1.printStackTrace();} catch (IOException e1) {	e1.printStackTrace();}
		
		try(InputStream in = new URL("http://192.168.0.32/jpg").openStream()){
		    Files.copy(in, Paths.get("/Users/jordancvetkovic/Projekte/TestRepo/Target.jpg"), StandardCopyOption.REPLACE_EXISTING);
		} catch (MalformedURLException e1) {e1.printStackTrace();} catch (IOException e1) {	e1.printStackTrace();}
		
		
		//Diese Funktion führt einen Git Push auf ein privates Github Repository aus.
		//Danach wird dem Server der Befehl gegeben, einen Git Clone durchzuführen.
		
		try {
			
			//Repository wird geladen
			Repository repo = new FileRepositoryBuilder()
				    .setGitDir(new File("/Users/jordancvetkovic/Projekte/TestRepo/.git"))
				    .build();
			Git git = new Git(repo);
			
			//URL und Personal Access Token werden eingesetzt
			String remoteUrl = "https://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@github.com/azbukadev/ImageCheck.git";
			CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider("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", "");
			
			
			
			//Neue Datei wird hinzugefügt und ein Commit wird erstellt
			git.add().addFilepattern("Target.jpg").call();
			git.commit().setMessage(Instant.now().toString()).setAuthor("arcard", "arcade@arcade.zz").call();
			
			
			//URL zu GitHub wird als "origin" definiert
			StoredConfig config = git.getRepository().getConfig();
			config.setString("remote", "origin", "url", remoteUrl);
			config.save();
			
			
			//Datei wird zu GitHub gepusht
			git.push().add("main").setRemote("origin").setCredentialsProvider(credentialsProvider).call();
			
			
			
			
			//Dem Server wird das Kommando erteilt, einen Git Pull durchzuführen
			SSHClient.command("root", "2022ModernTechnology", "46.101.214.73", 22, "git pull https://github.com/azbukadev/ImageCheck.git");
			SSHClient.command("root", "2022ModernTechnology", "46.101.214.73", 22, "cp /root/Target.jpg /home/FaceDB/Target.jpg");
			
			//Gesichtserkennung wird angefordert
			String result = SSHClient.command("root", "2022ModernTechnology", "46.101.214.73", 22, "docker exec ReCog face_recognition /home/FaceDB/DB /home/FaceDB/Target.jpg");
			String[] o = result.split(",");
			
			
			if(o[1].contains("unknown_person")) {
				System.out.println(o[1]);
				System.out.println("Person unknown. ");
				
				if(performRegister == true) {
					System.out.println("Performing register with UUID");
					UUID uuid = UUID.randomUUID();
					System.out.println(uuid.toString()+".jpg");
					SSHClient.command("root", "2022ModernTechnology", "46.101.214.73", 22, "cp /home/FaceDB/Target.jpg /home/FaceDB/DB/" + uuid.toString() + ".jpg");
					
					try (Scanner scanner = new Scanner(System.in)) {
						System.out.println("Enter name:");
						String name = scanner.nextLine();
						MariaDB_Client.insert("INSERT INTO player_names(UUID,Name) VALUES('" + uuid.toString() + "','" + name + "');");
					}
					
				}
				MariaDB_Client.close();
				return "Unknown";
			
			} else if (o[1].contains("no_persons_found")) {
				System.out.println("Image lacking faces. No results.");
				MariaDB_Client.close();
				return "Negative";
			} else {
				System.out.println("Lookup successful. Returned UUID:");
				System.out.println(o[1]);
				String raw = o[1].replace("\n", "").replace("\r", "");
				String query = "SELECT Name FROM player_names WHERE UUID='" + raw +"';";
				System.out.println(query);
				String dbresult = MariaDB_Client.select(query);
				System.out.println("Identified as: " + dbresult);
				MariaDB_Client.close();
				return dbresult;
			}
			
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MariaDB_Client.close();
			return "Negative";
		}
		
		
		
	}
	
	public static void printResults(Process process) throws IOException {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	    String line = "";
	    while ((line = reader.readLine()) != null) {
	        System.out.println(line);
	    }
	}
	
}
