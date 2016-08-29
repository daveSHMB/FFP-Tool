

import javax.swing.SwingUtilities;

public class FFPTool {

	public static void main(String[] args){
		
		
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new GUI();
			}	
		});
	}
}
