package tools;

import java.util.HashMap;
import java.util.Map;

/**
 * Tool class that help parsing formula
 * @author Julien OUVRARD
 *
 */
public class Parser {

	private Map<String,Float> atom_numbers;
	
	/**
	 * Constructor of the Parser class
	 */
	public Parser(){
		atom_numbers=new HashMap<String,Float>();
	}

	/**
	 * Explode the formula and fill a map of the atom numbers
	 * @param formule The formula to explode
	 */
	public void explodeFormula(String formula){
		int i=0;
		formula=unquote(formula);
		formula+=" ";
		float coeff_sup=0;
		do{
			float coeff=0;
			char c=formula.charAt(i);
			String ch="", s="", coefficient="1";
			
			// First letter has to be uppercase
			
			if(Character.isUpperCase(c)){
				ch=String.valueOf(c);
				i++;
				s=ch;
			}
			
			// If exists, second letter has to be lowercase
			
			c = formula.charAt(i);
			
			if(Character.isLowerCase(c)){
				ch = String.valueOf(c);
				s=s+ch; // The symbol of the element is obtained
				i++;
			}
			
			c = formula.charAt(i);

			if (Character.isDigit(c)){
				coefficient = String.valueOf(c);
				i++;
			}

			// Could be again a number

			c = formula.charAt(i);

			if (Character.isDigit(c)){
				coefficient = coefficient + String.valueOf(c);
				i++;
			}

			// Then could be a dot if it is a real number

			c = formula.charAt(i);

			if(c =='.'){
				coefficient = coefficient + ".";
				i++;
			}

			// Then could be a digit (first decimal)

			c = formula.charAt(i);

			if (Character.isDigit(c)){
				coefficient = coefficient + String.valueOf(c);
				i++;
			}

			// Then could be again a digit (second decimal)

			c = formula.charAt(i);

			if (Character.isDigit(c)){
				coefficient = coefficient + String.valueOf(c);
				i++;
			}

			c = formula.charAt(i);

			// The next character could be a comma, a dot, a space or parenthesis

			if(c ==','||c=='.'||c==' '||c=='('||c==')') i++;
			
			coeff = Float.valueOf(coefficient).floatValue();

			if (coeff==0) coeff = 1;
			if(atom_numbers.containsKey(s)){
				coeff_sup+=atom_numbers.get(s);
				coeff_sup+=coeff;
				atom_numbers.put(s, coeff_sup);
				coeff_sup=0;
			}
			else{
				coeff_sup=0;
				atom_numbers.put(s, coeff);
			}
			
			
		}while(i<formula.length()-1);

	}

	public Map<String, Float> getAtom_numbers() {
		return atom_numbers;
	}

	public void setAtom_numbers(Map<String, Float> atom_number) {
		this.atom_numbers = atom_number;
	}
	
	public float getAtom_numberByName(String atom){
		float ret=0;
		if(atom_numbers.containsKey(atom)){
			ret=atom_numbers.get(atom);
		}
		return ret;		
	}
	
	public String unquote(String s){
		if(s!=null && 
				((s.startsWith("\"") && s.endsWith("\""))||
						(s.startsWith("'") && s.endsWith("'")))){
			s=s.substring(1, s.length()-1);
		}
		return s;
	}

	public float getEnergy(String energie){
		float ret=0;
		energie=energie.substring(1,energie.indexOf("\"", 2));
		ret = Float.valueOf(energie).floatValue();
		return ret;
	}
}
