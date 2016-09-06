import java.util.ArrayList;
import java.util.Observable;

/**
 * Model class, stores a list of text objects
 * 
 * @author David McLintock
 *
 */
public class TextList extends Observable {

	private ArrayList<Text> textList;

	/**
	 * Default constructor
	 */
	public TextList(){

		textList = new ArrayList<Text>();
	}


	/**
	 * Adds a text to the text list
	 * @param title the title of the text
	 * @param text the content of the text
	 */
	public void addText(String title, String text){
		String[] titleFile = title.split("[.]");
		
		//if text is not a duplicate, add text
		if(!isDuplicate(titleFile[0])){
			Text newText = new Text(titleFile[0], text);
			textList.add(newText);
			setChanged();
			notifyObservers();
		}

	}

	
	/**
	 * Remove a text from the text list
	 * @param index the location of the text to be removed
	 */
	public void removeText(int index){
		textList.remove(index);
		setChanged();
		notifyObservers();
	}

	
	/**
	 * Change the title of a text
	 * @param index the position of the text
	 * @param title the new title given to the text
	 */
	public void setTitle(int index, String title){
		textList.get(index).setTitle(title);
		setChanged();
		notifyObservers();
	}

	
	/**
	 * Tests if a text is a duplicate
	 * @param title the title of the text being tested
	 * @return true if duplicate
	 */
	public boolean isDuplicate(String title){
		for(Text t: textList){
			if(t.getTitle().equals(title)){
				return true;
			}
		}
		return false;
	}

	
	/**
	 * Returns the text at a given index
	 * @param index the location of the text
	 * @return the text required
	 */
	public Text getText(int index){
		return textList.get(index);
	}

	
	/**
	 * Returns the list of text objects
	 * @return the list of texts
	 */
	public ArrayList<Text> getTextList(){
		return textList;
	}

	
	/**
	 * Returns the size of the text list
	 * @return the size of the text list
	 */
	public int getTextListLength(){
		return textList.size();
	}

	
	/**
	 * Returns a array of strings containing details of each text in the list
	 * @return an array containing titles of texts
	 */
	public String[] getTextDetails(){

		String[] textInfo = new String[textList.size()];
		//loop through texts collecting titles
		for(int i = 0; i < textList.size(); i++){
			String textTitle = textList.get(i).getTitle();
			textInfo[i] = textTitle;
		}

		return textInfo;
	}	
}
