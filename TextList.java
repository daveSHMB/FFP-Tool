import java.util.ArrayList;

public class TextList {

	private ArrayList<Text> textList;
	
	public TextList(){
		
		textList = new ArrayList<Text>();
	}
	
	//get rid of this?? User can edit author details later
	public void addText(String text, String author, String title){
		
		Text newText = new Text(text, author, title);
		textList.add(newText);				
	}
	
	public void addText(String title, String text){
		
		Text newText = new Text(title, text);
		textList.add(newText);
	}
	
	public void removeText(int index){
		textList.remove(index);
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
