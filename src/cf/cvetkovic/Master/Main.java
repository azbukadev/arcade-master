package cf.cvetkovic.Master;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.time.Instant;
import java.util.Properties;

import org.apache.sshd.common.channel.Channel;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;


public class Main {
	
	
	

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
			
			
			//SSHClient.command("root", "2022ModernTechnology", "46.101.214.73", 22, "docker run -t -d --name ReCog animcogn/face_recognition");
			//SSHClient.command("root", "2022ModernTechnology", "46.101.214.73", 22, "docker ps");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		//TEST SECTION-----------------------------------------------------
		
		try {
			SSHClient.command("root", "2022ModernTechnology", "46.101.214.73", 22,  "sudo sh run.sh");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		//TEST-SECTION--------------------------------------------------
		
		
		
		//Test für das kopieren der Dateien und starten der Gesichtserkennung
		//runRecognition();
		
	}
	
	
	public static void runRecognition() {
		
		//Diese Funktion führt einen Git Push auf ein privates Github Repository aus.
		//Danach wird dem Server der Befehl gegeben, einen Git Clone durchzuführen.
		
		try {
			Repository repo = new FileRepositoryBuilder()
				    .setGitDir(new File("/Users/jordancvetkovic/Projekte/TestRepo/.git"))
				    .build();
			Git git = new Git(repo);
			
			String remoteUrl = "https://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@github.com/azbukadev/ImageCheck.git";
			CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider("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", "");
			
			git.add().addFilepattern(".").call();
			git.commit().setAmend(true).setMessage(Instant.now().toString()).call();
			git.push().setCredentialsProvider(credentialsProvider).call();
			
			SSHClient.command("root", "2022ModernTechnology", "46.101.214.73", 22, "git pull https://github.com/azbukadev/ImageCheck.git");
			System.out.println(SSHClient.command("root", "2022ModernTechnology", "46.101.214.73", 22, "docker exec ReCog face_recognition"));
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
