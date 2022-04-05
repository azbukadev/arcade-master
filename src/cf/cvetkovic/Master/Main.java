package cf.cvetkovic.Master;

import com.fazecast.jSerialComm.SerialPort;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;

public class Main {

	public static void main(String[] args) {
		
		//Docker-Verbindung erstellen
		//DefaultDockerClientConfig.Builder config = DefaultDockerClientConfig.createDefaultConfigBuilder();
		//DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();
		
		SerialPort sp = SerialPort.getCommPort("/dev/cu.SLAB_USBtoUART"); // device name TODO: must be changed
	    sp.setComPortParameters(115200, 8, 1, 0); // default connection settings for Arduino
	    sp.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0); // block until bytes can be written
	    
	    if (sp.openPort()) {
	      System.out.println("Port is open :)");
	    } else {
	      System.out.println("Failed to open port :(");
	      return;
	    }		
	    
	    for (Integer i = 0; i < 5; ++i) {			
	      
	    	try {
	    		sp.getOutputStream().write(i.byteValue());
	  	      	sp.getOutputStream().flush();
	  	      	System.out.println("Sent number: " + i);
	  	      	Thread.sleep(1000);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	      
	    }		
	    
	    if (sp.closePort()) {
	      System.out.println("Port is closed :)");
	    } else {
	      System.out.println("Failed to close port :(");
	      return;
	    }
		
		
	}
	
	
}
