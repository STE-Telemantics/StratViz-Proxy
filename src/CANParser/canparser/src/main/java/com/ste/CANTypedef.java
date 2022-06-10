package com.ste;

import java.util.HashMap;
import java.util.List;

import com.ste.CANParser;
import com.ste.ParsedTypedef;

// @author Emre Aydogan
public class CANTypedef {
	/**
	 * Create a Hashmap for each Enum which contains the bitsequence for that specific state of an enum. 
	 * 
	 * This bitsequence/state pair is a key/value pair in the inner HashMap. The key of the outer HashMap is the name of the enum 
	 * class. Together with the inner Hashmap we then get the following form:
	 * 
	 * 	HashMap<String EnumName, HashMap<String Bitsequence, String StateName>>
	 * 
	 * A concrete example of a single entry is as follows: 
	 * 	<"AcuMode", <"00000010", "DARE">>
	 * 
	 * @return A HashMap containing the Enum classname as the key, and another Hashmap
	 */
	public HashMap<String, HashMap<String, String>> parseTypedef() {

		HashMap<String, HashMap<String, String>> parsedEnums = new HashMap<>();

		// First we make use of the CANParser class to read in the default typedefs.csv file and extract all the fields per typedef
		CANParser cp = new CANParser(); 
		
		// Store all the parsed typedefs in a list of ParsedTypedef objects
		List<ParsedTypedef> lpdf = cp.parseTypedefsDefault();

		// Now iterate through the list of parsed typedefs and build the Hashmap by getting the name of the enum and its codestates.
		// Assign the corresponding bitsequence to it and store it in the Hashmap
		for (ParsedTypedef pdf : lpdf) {
			// Build the Hashmap<String, String> first (e.g. the value of a parsed enum)
			HashMap<String, String> bitsequenceStateMap = new HashMap<>();

			// Get all the codestates this Enum class can take
			String[] codeStates = pdf.getCodeStates();
			
			// We're going to check whether a byte sequence is already defined in the typedefs file. If so, use that value. 
			// Else we assign our own values
			boolean isPredefined = false; 
			
			for (String s : codeStates) {
				// We have a hit, this state is predefined
				if (s.contains("=")) {

					isPredefined = true;

					// Extract the predefined state by splitting on the "=" sign
					String[] predefinedState = s.split("=");

					// Remove any trailing spaces and save the predefined name and its value
					String name = predefinedState[0].trim();
					String value = predefinedState[1].trim();

					// We now have all the predefined states in an array, check whether this is a hex or integer
					if (value.contains("0x")) {
						// This is a hex, parse it to an integer
						int i = Integer.decode(value);
						
						// Add the String bitsequence, String StateName pair to the inner HashMap
						bitsequenceStateMap.put(String.format("%08d", Integer.parseInt(Integer.toString(i, 2))), name);
					} else {
						// This is an int, parse it to a Byte
						int i = Integer.parseInt(value);

						bitsequenceStateMap.put(String.format("%08d", Integer.parseInt(Integer.toString(i, 2))), name);
					}
				}
			}

			// TODO: Make it so that we don't have to hardcode these states
			// There are 3 enums that have only a single (the first) state predefined as "0". Assign bitsequences to the rest of the states
			if (isPredefined) {
				String name = pdf.getName();
				if (name.equals("KeyStatus") || name.equals("LVCState") || name.equals("VehicleState")) {
					for (int i = 1; i < pdf.getCodeStates().length; i++) {
						bitsequenceStateMap.put(String.format("%08d", Integer.parseInt(Integer.toString(i, 2))), pdf.getCodeStates()[i]);
					}
				}
			}
				
			// No predefined states were found for this Enum, continue with the basic assignment of bitsequences, starting with 0 
			if (!isPredefined) {
				for (int i = 0; i < pdf.getCodeStates().length; i++) {
					bitsequenceStateMap.put(String.format("%08d", Integer.parseInt(Integer.toString(i, 2))), pdf.getCodeStates()[i]);
				}
			}

			// Now that we have mapped all the states to a bitsequence, set this as the key in the inner hashmap and use the statename as the value
			parsedEnums.put(pdf.getName(), bitsequenceStateMap);
		}		

		return parsedEnums;
	}
}
