import java.util.HashMap;


public class Text {

	private String text;
	private String title;

	public Text(String title, String text){

		this.title = title;
		this.text = text.toLowerCase();
	}

	public String getTitle(){
		return title;
	}

	public void setTitle(String title){
		this.title = title;
		System.out.println(this.title);
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
