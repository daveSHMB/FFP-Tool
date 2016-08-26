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

	public String removePunctuation(){
		System.out.println(text.replaceAll("[^a-zA-Z ]", ""));
		return text.replaceAll("[^a-zA-Z ]", "");
	}


	public Double meanWordLength(){

		String[] words = text.split(" ");	
		double count = 0.0;

		for(int i=0; i < words.length; i++){
			count += words[i].length();
		}	
		return count / words.length;	
	}


	public Double meanSentenceLength(){

		String[] sentences = text.split("\\. ");
		double count = 0.0;

		for(int i=0; i<sentences.length; i++){
			String[] words = sentences[i].split(" ");
			count += words.length;
		}

		return count / sentences.length;
	}

	public double deviationOfSentenceLength(){
		return 0.0;
	}

	//TODO this could probably be a LOT less complex
	public int countWord(String word){

		//with this regex, extra spaces are added to the list where the punctuation is at the start of a word or isolation
		String[] wordCountText = text.split("\\s+|(?=\\p{Punct})|(?<=\\p{Punct})");
		return Collections.frequency(new ArrayList<String>(Arrays.asList(wordCountText)), word);	
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

	public int[] countNgrams(String[] ngramList){

		ArrayList<Integer> frequencies = new ArrayList<Integer>();

		for(String s:ngramList){
			//int count = Collections.frequency(ngrams, s);
			//frequencies.add(count);
		}

		//convert back to an integer array (doesn't seem like there's an easy way to do this??)
		int[] freq = new int[frequencies.size()];

		for(int i=0; i<freq.length; i++){
			freq[i] = frequencies.get(i);
		}


		return freq;
	}
	
	

}
