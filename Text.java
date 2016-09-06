
/**
 * Class representing a text, storing its title and the full text of the work
 * @author David McLintock
 *
 */
public class Text {

	private String text;
	private String title;

	/**
	 * Constructor for a Text object
	 * @param title the title of the text
	 * @param text the full content of the text
	 */
	public Text(String title, String text){

		this.title = title;
		this.text = text.toLowerCase();
	}

	
	/**
	 * Returns the title of the text
	 * @return the title of the text
	 */
	public String getTitle(){
		return title;
	}

	
	/**
	 * Sets the title of the text
	 * @param title the new title of the text
	 */
	public void setTitle(String title){
		this.title = title;
	}

	
	/**
	 * Gets the content of the text
	 * @return the content of the text
	 */
	public String getText(){
		return this.text;
	}

}
