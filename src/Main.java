import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

	/**
	 * Entry Method
	 * @param args
	 */
	public static void main(String[] args) {
		Path file = Paths.get(args[0]);
		try {
			InputStream in = Files.newInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			int noRotors = 3;
			
			Enigma e = new Enigma(noRotors);

			// Alphabet
			String line = reader.readLine();
			e.setAlphabet(line);

			// Plugboard Config
			line = reader.readLine();
			String[] parts;
			if (line.equals("") == false) { // Empty line
				parts = line.split("\\s");
				if (parts[0].compareTo("  ") != 0)
					for (int i = 0; i < parts.length; i++)
						e.setPlugboard(parts[i].charAt(1), parts[i].charAt(2));
			}
			// Reflector + 3 Rotor Types
			line = reader.readLine();
			parts = line.split("\\s+");
			e.setReflector(parts[0].charAt(0));

			e.createRotor(mapIntToType(Integer.parseInt(parts[3]))); // Rotor3
																		// Type
			e.createRotor(mapIntToType(Integer.parseInt(parts[2]))); // Rotor2
																		// Type
			e.createRotor(mapIntToType(Integer.parseInt(parts[1]))); // Rotor1
																		// Type

			// Initial position of rings
			line = reader.readLine();
			e.setRingsPosition(line);

			// Initial position of rotors
			line = reader.readLine();
			e.setRotorsPosition(line);

			// Crypt or decrypt
			String operation = reader.readLine();

			// Message
			String mess = "";
			line = reader.readLine();
			while (line != null) {
				mess += line;
				line = reader.readLine();
			}

			// Use the machine
			if (operation.charAt(0) == 'C') {
				e.crypt(mess);
			} else {
				e.crypt(mess);
			}

		} catch (IOException e) {
			System.err.println(e);
			e.printStackTrace();
		}

	}

	/**
	 * Method that transforms an int into a RotorType
	 * @param x int that needs to be converted to RotorTypes
	 * @return RotorType of the int
	 */
	public static RotorType mapIntToType(int x) {
		return RotorType.values()[x - 1];
	}

}

enum RotorType {
	I, II, III, IV, V, VI, VII, VIII;
}
