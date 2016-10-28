import java.util.ArrayList;

public class Enigma {

	private String alphabet;
	private String plugboard;
	private String reflector;
	private String relativealphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private int noRotors;
	private ArrayList<Rotor> rotors;

	public Enigma(int noR) {
		noRotors = noR;
		rotors = new ArrayList<Rotor>(noRotors);
	}

	/**
	 * Sets the alphabet of the machine
	 * 
	 * @param s
	 *            the alphabet
	 */
	public void setAlphabet(String s) {
		alphabet = s;
		plugboard = alphabet;
	}

	/**
	 * Sets the plugboard configuration of the machine
	 * 
	 * @param c1
	 *            the letter that is mapped to the c2
	 * @param c2
	 *            the letter that is mapped to the c1
	 */
	public void setPlugboard(char c1, char c2) {
		char[] p = plugboard.toCharArray();
		p[plugboard.indexOf(c1)] = c2;
		p[plugboard.indexOf(c2)] = c1;
		plugboard = String.valueOf(p);
	}

	/**
	 * Sets Reflector with the given type
	 * 
	 * @param c
	 *            type of reflector 'C' , 'B'
	 */
	public void setReflector(char c) {
		if (c == 'B')
			reflector = "YRUHQSLDPXNGOKMIEBFZCWVJAT";
		else
			reflector = "FVPJIAOYEDRZXWGCTKUQSBNMHL";
	}

	/**
	 * Creates a new rotor with a given Type
	 * 
	 * @param type
	 *            type of the rotor
	 */
	public void createRotor(RotorType type) {
		rotors.add(new Rotor(type));
	}

	/**
	 * Set Rotors Position
	 * 
	 * @param s
	 *            String that contains the configuration of Rotors
	 */
	public void setRotorsPosition(String s) {
		int off;
		for (int i = 0; i < rotors.size(); i++) {
			off = getOffset(s.charAt(rotors.size() - i - 1));
			rotors.get(i).setOffset(off);
		}
	}

	/**
	 * Sets Ring Position (Ringstellung)
	 * 
	 * @param s
	 *            String that contains the configuration of Rings
	 */
	public void setRingsPosition(String s) {
		int off;
		for (int i = 0; i < rotors.size(); i++) {
			off = getOffset(s.charAt(rotors.size() - i - 1));
			rotors.get(i).setRingOffset(off);
		}
	}

	/**
	 * Crypts the message
	 * 
	 * @param s
	 *            The message that is crypted/decrypted
	 */
	public void crypt(String s) {
		char letter;
		String output = "";
		int ok = 1;
		for (int i = 0; i < s.length(); i++) {
			// Verify if the input is in alphabet
			if (isInAlphabet(s.charAt(i)) == false)
				continue;
			// Rotating rotors
			if (rotors.get(1).isInNotchPosition1() == true && i != 0 && ok == 0) {// DStep
				rotors.get(1).rotate();
				rotors.get(2).rotate();
			}
			ok = 1;
			rotors.get(0).rotate();
			if (rotors.get(0).isInNotchPosition() == true) {
				rotors.get(1).rotate();
				ok = 0;
			}
			// Plugboard
			letter = plugboard.charAt(alphabet.indexOf(s.charAt(i)));
			// Rotors forward
			for (int j = 0; j < noRotors; j++) {
				letter = rotors.get(j).moveThrough(letter);
				if (letter == '\0')
					break;
			}
			if (letter == '\0')
				continue;
			// Reflector
			int ok1 = 0;
			for (int j = 0; j < relativealphabet.length(); j++)
				if (relativealphabet.charAt(j) == letter) {
					if (isInAlphabet(reflector.charAt(j)) == true)
						letter = reflector.charAt(j);
					else
						ok1 = 1;
					break;
				}
			if (ok1 == 1)
				continue;
			// Rotors Backward
			for (int j = noRotors - 1; j >= 0; j--) {
				letter = rotors.get(j).moveThroughBackwards(letter);
				if (letter == '\0')
					break;
			}
			if (letter == '\0')
				continue;
			// Plugboard
			letter = plugboard.charAt(alphabet.indexOf(letter));
			// Output
			output += letter;
		}
		System.out.println(output);
	}

	/**
	 * Class that contains the arhitecture of the rotors inside Enigma
	 * 
	 * @author Paul
	 *
	 */
	private class Rotor {

		private String map, notch;
		private int offset, ringoffset;

		/**
		 * Rotates the rotor once
		 */
		public void rotate() {
			offset = (offset + 1) % alphabet.length();
		}

		/**
		 * Constructor that builds the rotor from a type
		 * 
		 * @param type
		 *            Creates the rotor with a given type
		 */
		public Rotor(RotorType type) {
			switch (type) {
			case I:
				map = "EKMFLGDQVZNTOWYHXUSPAIBRCJ";
				notch = "R";
				break;
			case II:
				map = "AJDKSIRUXBLHWTMCQGZNPYFVOE";
				notch = "F";
				break;
			case III:
				map = "BDFHJLCPRTXVZNYEIWGAKMUSQO";
				notch = "W";
				break;
			case IV:
				map = "ESOVPZJAYQUIRHXLNFTGKDCMWB";
				notch = "K";
				break;
			case V:
				map = "VZBRGITYUPSDNHLXAWMJQOFECK";
				notch = "A";
				break;
			case VI:
				map = "JPGVOUMFYQBENHZRDKASXLICTW";
				notch = "AN";
				break;
			case VII:
				map = "NZJHGRCXMYSWBOUFAIVLPEKQDT";
				notch = "AN";
				break;
			case VIII:
				map = "FKQHTLXOCBJSPDZRAMEWNIUYGV";
				notch = "AN";
				break;
			}
		}

		/**
		 * The forward process of a letter through a rotor
		 * 
		 * @param c
		 *            the letter that is input to the rotor
		 * @return letter after going through a rotor, '\0' if the letter must
		 *         be dropped
		 */
		public char moveThrough(char c) {
			int x, i;
			x = (relativealphabet.indexOf(c) + offset - ringoffset + relativealphabet.length())
					% relativealphabet.length();
			for (i = 0; i < alphabet.length(); i++)
				if (relativealphabet.charAt(x) == alphabet.charAt(i)) {
					x = i;
					break;
				}
			if (i == alphabet.length())
				return '\0';
			for (i = 0; i < relativealphabet.length(); i++)
				if (alphabet.charAt(x) == relativealphabet.charAt(i)) {
					x = i;
					break;
				}
			c = map.charAt(x);
			if (isInAlphabet(c) == false)
				return '\0';
			x = (relativealphabet.indexOf(c) + ringoffset - offset + relativealphabet.length())
					% relativealphabet.length();
			for (i = 0; i < alphabet.length(); i++)
				if (relativealphabet.charAt(x) == alphabet.charAt(i)) {
					x = i;
					break;
				}
			if (i == alphabet.length())
				return '\0';
			return alphabet.charAt(x);
		}

		/**
		 * The backwards process of a letter through a rotor
		 * 
		 * @param c
		 *            the letter that is input to the rotor
		 * @return letter after going through a rotor, '\0' if the letter must
		 *         be dropped
		 */
		public char moveThroughBackwards(char c) {
			int x, i;
			x = (relativealphabet.indexOf(c) + offset - ringoffset + relativealphabet.length())
					% relativealphabet.length();
			int ok = 0;
			for (i = 0; i < alphabet.length(); i++)
				if (relativealphabet.charAt(x) == alphabet.charAt(i)) {
					x = i;
					break;
				}
			if (i == alphabet.length())
				return '\0';
			for (i = 0; i < map.length(); i++)
				if (alphabet.charAt(x) == map.charAt(i)) {
					for (int j = 0; j < alphabet.length(); j++)
						if (relativealphabet.charAt(i) == alphabet.charAt(j)) {
							c = alphabet.charAt(j);
							ok = 1;
							break;
						}
					if (ok == 0)
						return '\0';
				}
			x = (relativealphabet.indexOf(c) + ringoffset - offset + relativealphabet.length())
					% relativealphabet.length();
			for (i = 0; i < alphabet.length(); i++)
				if (relativealphabet.charAt(x) == alphabet.charAt(i)) {
					x = i;
					break;
				}
			if (i == alphabet.length())
				return '\0';
			return alphabet.charAt(x);
		}

		/**
		 * Method that finds out if a rotor is in notch position
		 * 
		 * @return
		 */
		public boolean isInNotchPosition() {
			for (int i = 0; i < notch.length(); i++)
				if (notch.charAt(i) == alphabet.charAt((offset) % alphabet.length()))
					return true;
			return false;
		}

		/**
		 * Method that finds out if a rotor is after 1 rotation in notch
		 * position
		 * 
		 * @return true - if it will be in notch after 1 rotation<br>
		 *         false - if it won't be in notch after 1 rotation
		 */
		public boolean isInNotchPosition1() {
			for (int i = 0; i < notch.length(); i++)
				if (notch.charAt(i) == alphabet.charAt((offset + 1) % alphabet.length()))
					return true;
			return false;
		}

		/**
		 * Sets the Rotor Offset
		 * 
		 * @param o
		 *            the rotor offset
		 */
		public void setOffset(int o) {
			offset = o;
		}

		/**
		 * Sets the Ring Offset
		 * 
		 * @param o
		 *            the ring offset
		 */
		public void setRingOffset(int o) {
			ringoffset = o;
		}

	}

	/**
	 * Method that verifies if a letter is in the used alphabet
	 * 
	 * @param c
	 *            the letter that needs to be verified
	 * @return true - if the letter is in the alphabet <br>
	 *         false - if the letter isn't in the alphabet
	 */
	public boolean isInAlphabet(char c) {
		for (int i = 0; i < alphabet.length(); i++)
			if (c == alphabet.charAt(i))
				return true;
		return false;
	}

	/**
	 * Method that gets the offset/index of a letter in the alphabet used
	 * 
	 * @param c
	 *            the letter that needs to get it's index
	 * @return the offset/index of the letter
	 */
	public int getOffset(char c) {
		return alphabet.indexOf(c);
	}

}
