import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;


public class Lexer {
	private BufferedReader buffer;
	private char lastChar = ' ';
	private final static char EOF = (char) -1;
	
	public Lexer(File file) throws FileNotFoundException {
		// setup buffer with file
		InputStream is = new FileInputStream(file);
		Reader reader = new InputStreamReader(is);
		buffer = new BufferedReader(reader);
	}
	
	public Token getToken() throws IOException {
		Token token;
		
		// skip any whitespace
		while (Character.isWhitespace(lastChar))
			lastChar = (char) buffer.read();
		
		// identifier: [a-zA-Z][a-zA-Z0-9]*
		if (Character.isLetter(lastChar)) {
			String identStr = "";
			do {
				identStr += lastChar;
				lastChar = (char) buffer.read();
			} while (Character.isLetterOrDigit(lastChar));
			
			// check if identifier matches keyword
			if (identStr.equalsIgnoreCase("def"))
				return new Token(TokenClass.DEF);
			if (identStr.equalsIgnoreCase("extern"))
				return new Token(TokenClass.EXTERN);
			if (identStr.equalsIgnoreCase("if"))
				return new Token(TokenClass.IF);
			if (identStr.equalsIgnoreCase("then"))
				return new Token(TokenClass.THEN);
			if (identStr.equalsIgnoreCase("else"))
				return new Token(TokenClass.ELSE);
			if (identStr.equalsIgnoreCase("for"))
				return new Token(TokenClass.FOR);
			if (identStr.equalsIgnoreCase("in"))
				return new Token(TokenClass.IN);
			
			token = new Token(TokenClass.IDENTFIFIER);
			token.setIdentStr(identStr);
			return token;
		}
		
		// number: [0-9.]+
		if (Character.isDigit(lastChar) || lastChar == '.') {
			String numString = "";
			boolean hasDecimal = false;
			do {
				if (lastChar == '.') hasDecimal = true;
				numString += lastChar;
				lastChar = (char) buffer.read();
			} while (Character.isDigit(lastChar) || (lastChar == '.' && !hasDecimal));
			
			token = new Token(TokenClass.NUMBER);
			token.setNumVal(Double.parseDouble(numString));
			return token;
		}
		
		// comment (ignore as token)
		if (lastChar == '#') {
			// comment until end of line
			do
				lastChar = (char) buffer.read();
			while (lastChar != EOF && lastChar != '\n' && lastChar != '\r');
			
			// grab next token
			if (lastChar != EOF)
				return getToken();
		}
		
		// check for end of file
		if (lastChar == EOF)
			return new Token(TokenClass.EOF);
		
		// unknown character
		token = new Token(TokenClass.UNKNOWN);
		token.setUnknownChar(lastChar);
		lastChar = (char) buffer.read();
		return token;
	}
	
	public static void main(String[] args) throws IOException {
		Lexer lexer = new Lexer(new File("numvals.txt"));
		Token token;
		do {
			token = lexer.getToken();
			System.out.println(token);
		} while (token.getType() != TokenClass.EOF);
	}
}
