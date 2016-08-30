import java.util.ArrayList;
import java.util.Observable;

public class TextList extends Observable {

	private ArrayList<Text> textList;
	
	public TextList(){
		
		textList = new ArrayList<Text>();
	}
	

	public void addText(String title, String text){
		
		String[] titleFile = title.split("[.]");
		
		Text newText = new Text(titleFile[0], text);
		textList.add(newText);
		setChanged();
		notifyObservers();
	}
	
	public void removeText(int index){
		textList.remove(index);
		setChanged();
		notifyObservers();
		}
	
	public void setAuthor(int index, String author){
		textList.get(index).setAuthor(author);
		setChanged();
		notifyObservers();
	}
	
	public void setTitle(int index, String title){
		textList.get(index).setAuthor(title);
		setChanged();
		notifyObservers();
	}
	
	public Text getText(int index){
		return textList.get(index);
	}
	
	public ArrayList<Text> getTextList(){
		return textList;
	}
	
	public int getTextListLength(){
		return textList.size();
	}
	
	public String[] getTextDetails(){
		
		String[] textInfo = new String[textList.size()];
		
		for(int i = 0; i < textList.size(); i++){
			
			String formatted = "";
			
			formatted += textList.get(i).getTitle();
			
			if(textList.get(i).getAuthor() != null){
				formatted += " - " + textList.get(i).getAuthor();
			}
			
			textInfo[i] = formatted;
		}
		
		return textInfo;
	}	
}
