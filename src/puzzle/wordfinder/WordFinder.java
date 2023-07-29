package puzzle.wordfinder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Click an image and go to https://www.imagetotext.info/
 * upload the image and figure out the letters. Copy the letters to crossWords.txt.
 * 
 * or you write the letters in crossWords.txt
 * 
 * pickup the words related to the context
 * 
 * very rudimentary. good enough to solve a puzzle.
 * 
 * @author Rakesh Mohanty
 *
 */
public class WordFinder {
	
	private int minLen = 5;
	private String crossWordFilePath = ".\\input.txt";
	private static final String WORD_DICT = ".\\words_alpha.txt";
	
	private List<String> dictionary = null;
	private List<String> allWordList = new ArrayList<>();
	
	public void setCrossWordFilePath(String crossWordFilePath) {
		this.crossWordFilePath = crossWordFilePath;
	}
	
	public void setMinLen(int minLen) {
		this.minLen = minLen;
	}
	
	private void init(String...args) throws IOException {
		if(args.length >= 1) {
			setCrossWordFilePath(args[0]);
			setMinLen(Integer.parseInt(args[1]));
		}
		this.dictionary = Files.readAllLines(Path.of(WORD_DICT));
		initMatrixWords();

	}
	
	private void initMatrixWords() throws IOException {
		try {
			List<String> allLines = Files.readAllLines(Path.of(crossWordFilePath));
			if(!allLines.isEmpty()) {
				int size = allLines.size();
				
				String [] rowLetters = new String[size];
				String [] colLetters =  new String[size];
				String [] diagonalLetters =  new String[size*2 - 1];
				String [] rDiagonalLetters = new String[size*2 - 1];
				
				Arrays.fill(rowLetters, "");
				Arrays.fill(colLetters, "");
				Arrays.fill(diagonalLetters, "");
				Arrays.fill(rDiagonalLetters, "");
				
				int row = 0;
				for(String word : allLines) {
					for(int col = 0; col < word.length() ; col++) {
						String currentLetter = String.valueOf(word.charAt(col));
						rowLetters[row] = rowLetters[row] + currentLetter;
						colLetters[col] = colLetters[col] + currentLetter;
						diagonalLetters[row+col] =  currentLetter + diagonalLetters[row+col];
						if(row <= col) {
							rDiagonalLetters[col-row] = rDiagonalLetters[col-row] + currentLetter;
						} else {
							rDiagonalLetters[2*size-(row-col)-1] = rDiagonalLetters[2*size-(row-col)-1] + currentLetter;
						}
					}
					row++;
				}
				
				allWordList.addAll(Arrays.asList(rowLetters));
				allWordList.addAll(Arrays.asList(colLetters));
				allWordList.addAll(Arrays.asList(diagonalLetters));
				allWordList.addAll(Arrays.asList(rDiagonalLetters));
				
			} else {
				throw new IllegalArgumentException("File has empty data.");
			}
		} catch (IOException e) {
			throw e;
		}
	}
	
	private void solve() {
		for(int count =0 ; count < allWordList.size(); count++) {
			String rowWord = allWordList.get(count);
			dictionary.parallelStream()
				.filter(word -> (word.length() >= minLen && rowWord.contains(word)))
				.forEach(System.out::println);
		}
	}
	
	public static void main(String[] args) throws IOException {
		WordFinder crossWord = new WordFinder();
		crossWord.init(args);
		crossWord.solve();
	}

}
