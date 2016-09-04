import java.util.ArrayList;
import java.util.Observable;


public class TextList extends Observable {

	private ArrayList<Text> textList;

	public TextList(){

		textList = new ArrayList<Text>();
	}


	public void addText(String title, String text){
		String[] titleFile = title.split("[.]");
		
		
		//add notification that duplicate has not been added
		if(!isDuplicate(titleFile[0])){
			Text newText = new Text(titleFile[0], text);
			textList.add(newText);
			setChanged();
			notifyObservers();
		}
		else{
			//warning message
		}
	}

	public void removeText(int index){
		textList.remove(index);
		setChanged();
		notifyObservers();
	}

	public void setTitle(int index, String title){
		textList.get(index).setTitle(title);
		setChanged();
		notifyObservers();
	}

	public boolean isDuplicate(String title){
		for(Text t: textList){
			if(t.getTitle().equals(title)){
				return true;
			}
		}
		return false;
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

			String textTitle = textList.get(i).getTitle();

			textInfo[i] = textTitle;
		}

		return textInfo;
	}	
}
