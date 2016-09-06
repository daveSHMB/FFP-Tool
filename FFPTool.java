import javax.swing.SwingUtilities;

/**
 * 
 * Main class and glue code for the FFP tool
 * @author David McLintock
 *
 */
public class FFPTool {

	public static void main(String[] args){
		
		//glue code
		FFPController con = new FFPController();
		ResultsWindow rw = new ResultsWindow();
		TextList tl = new TextList();
		rw.addController(con);
		con.addView(rw);
		con.addModel(tl);
		
		//start gui
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new SetupWindow(con, tl);
			}	
		});
	}
}
