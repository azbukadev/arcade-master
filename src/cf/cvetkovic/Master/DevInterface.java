package cf.cvetkovic.Master;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class DevInterface {
	
	
	public static void createGUI(){
	       JFrame frame = new JFrame("FaceRec");
	       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	       frame.setSize(300,300);
	       JButton button = new JButton("Press");
	       button.addActionListener(new ActionListener() {
	    	   @Override
	    	   public void actionPerformed(ActionEvent e) {
	    		   String run1 = Main.runRecognition(false);
	    		   if(run1 == "Unknown") {
	    			   String run2 = Main.runRecognition(false);
		    		   if(run2 == "Unknown") {
		    			   Main.runRecognition(true);
		    		   } else if (run2 == "Negative") {
		    			   Main.runRecognition(true);
		    		   }
	    		   } else if (run1 == "Negative") {
	    			   String run2 = Main.runRecognition(false);
		    		   if(run2 == "Unknown") {
		    			   Main.runRecognition(true);
		    		   } else if (run2 == "Negative") {
		    			   Main.runRecognition(true);
		    		   }
	    		   }
	    	   }
	        });
	       frame.getContentPane().add(button); // Adds Button to content pane of frame
	       frame.setVisible(true);
	    }
	

}
