import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.regex.Pattern;


public class Text {

	private String text;
	private String title;
	private String author;

	//private TreeMap<String, Double> ngramTree;


	public Text(String text, String title, String author){

		this.text = text.toLowerCase();
		this.title = title;
		this.author = author;		
	}

	public Text(String title, String text){

		this.title = title;
		this.text = text;
	}

	public String getTitle(){
		return title;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getAuthor(){
		return author;
	}

	public void setAuthor(String author){
		this.author = author;
	}

	public HashMap<String, Integer> getNgrams(int ngramLength){

		String noSpaces = text.replaceAll("\\s+", "");

		HashMap<String, Integer> ngrams = new HashMap<String, Integer>();


		for(int i = 0; i < noSpaces.length(); i++){
			if((i+ngramLength <= noSpaces.length())){
				String ngram = noSpaces.substring(i, i+ngramLength);

				if(ngram.matches("[a-zA-Z]+")){
					ngrams.put(ngram, ngrams.getOrDefault(ngram, 0) + 1);
				}
			}
		}
		return ngrams;
	}

}
