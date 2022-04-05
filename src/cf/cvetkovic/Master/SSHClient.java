package cf.cvetkovic.Master;

import java.io.ByteArrayOutputStream;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;

public class SSHClient {

	public static String command(String username, String password, 
			  String host, int port, String command) throws Exception {
			    
			    Session session = null;
			    ChannelExec channel = null;
			    
			    try {
			        session = new JSch().getSession(username, host, port);
			        session.setPassword(password);
			        session.setConfig("StrictHostKeyChecking", "no");
			        session.connect();
			        
			        channel = (ChannelExec) session.openChannel("exec");
			        channel.setCommand(command + "; exit\n");
			        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
			        channel.setOutputStream(responseStream);
			        channel.connect();
			        
			        while (channel.isConnected()) {
			            Thread.sleep(100);
			        }
			        
			        
			        
			        String responseString = new String(responseStream.toByteArray());
			        System.out.println(responseString);
			        return responseString;
			    } finally {
			        if (session != null) {
			            session.disconnect();
			        }
			        if (channel != null) {
			            channel.disconnect();
			        }
			    }
			}
	
	public static void commandApache(String username, String password, 
			  String host, int port, long defaultTimeoutSeconds, String command) throws IOException {
			    
			    SshClient client = SshClient.setUpDefaultClient();
			    client.start();
			    
			    try (ClientSession session = client.connect(username, host, port)
			      .verify(defaultTimeoutSeconds, TimeUnit.SECONDS).getSession()) {
			        session.addPasswordIdentity(password);
			        session.auth().verify(defaultTimeoutSeconds, TimeUnit.SECONDS);
			        
			        try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream(); 
			          ClientChannel channel = session.createChannel(org.apache.sshd.common.channel.Channel.CHANNEL_SHELL)) {
			            channel.setOut(responseStream);
			            try {
			                channel.open().verify(defaultTimeoutSeconds, TimeUnit.SECONDS);
			                try (OutputStream pipedIn = channel.getInvertedIn()) {
			                    pipedIn.write(command.getBytes());
			                    pipedIn.flush();
			                }
			            
			                channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), 
			                TimeUnit.SECONDS.toMillis(defaultTimeoutSeconds));
			                String responseString = new String(responseStream.toByteArray());
			                System.out.println(responseString);
			            } finally {
			                channel.close(false);
			            }
			        }
			    } finally {
			        client.stop();
			    }
			}
	
	
 }
