/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package specificationstuff;


import java.util.ArrayList;
import java.util.Arrays;

//CONSULT DRIVE FOR EASIER OVERVIEW OF FUNCTIONS - https://drive.google.com/drive/u/0/folders/1vgE6a2_4SK2RJL6YsllVkD5Wmiu9INAC 


/*Note: Bytes should always be read from left to right. Enums are always represented using a single byte, whereas booleans are represented using a single bit. Integers can be 
* represented with upto 32 bits. Furthermore, the bytes that represent enums are typically equal to 00, 01, 02, 03 etc in hexadecimal. If a variable has three states it can only 
* be equal to 00, 01, and 02. Also, enums are allowed to have non-standard byte sequences. In this case STE defines a what the byte sequence of a specific state looks like in the
* CAN_typedef_2019_17-format.csv file.
*
* Examples: 
* signal A has 1 boolean that is set to true. The 8 bytes look as follows: 00000001 00000000 00000000 00000000 00000000 00000000 00000000 00000000
* signal B has 3 booleans in the following order (from top to bottom in CAN-X) true, false, true. 8 bytes: 00000101 00000000 00000000 00000000 00000000 00000000 00000000 00000000
* signal C has 2 booleans and 1 enum in the following order true true enumState2. 8 bytes: 00000011 00000010 00000000 00000000 00000000 00000000 00000000 00000000
* signal D has 2 booleans and 2 enum in the following order true enumState0 enumState1 true. 8 bytes: 00000001 00000000 00000001 00000001 00000000 00000000 00000000 00000000
* signal E has 3 booleans and a predefined enum state as 0x03 in the following order false false true enumState: 00000100 00000011 00000000 00000000 00000000 00000000 00000000 00000000
/*

/**
 *
 * @author 20172420
 */
import java.util.HashMap;
import java.util.List;
import java.util.HashMap;
import CANParser.canparser.src.main.java.com.ste.*;

public class CANEnumParser {
	//The idea of this class is that we receive a CAN message such as the string 'testmsg' down below, and we convert it to a hashmap that contains the name of the variable
	//together with its value. The example below should eventually output a hashmap that contains {(mode, ACUmode), (bmsAlive, T/F), (ssbAlive, T/F), (srvRegenAlive, T/F),
	// (inverter, InverterType)}. This hashmap can then be easily interpreted on the front-end.

	//Note that the current version of the file contains hardcoded examples to translate. Eventually we need to be able to translate the CAN message using only the CAN message itself.

    //The actual message we'll eventually need to parse
    static String testmsg = "(1600453413.322000) canx 12d#01c90100a819d400";
        
    //Data part of 'testmsg' (01c90100a819d400) converted to binary: 0001 11001001 00000001 00000000 10101000 00011001 11010100 00000000
        
     //These are strings that we will eventually need to deduce using the .csv files and the id of string 'testmsg'. The data types correspond to the following variables: 
	//mode, bmsAlive, ssbAlive, srvRegenAlive, esbAlive, inverter
     String msgdatatypes = "ACUMode, bool: 1, bool: 1, bool: 1, bool: 1, InverterType";
        
    //string msgdatatypes has ACUMode, booleans, and an InverterType. We will eventually need to deduce what these enums are 
    //by using the file 'CAN_typedef_2019_17-format.csv' file.
     static String ACUdef = "enum ACUMode:uint8_t{ACUModeRDW,ACUModeWSC,DARE}";
     static String InvertDef = "enum InverterType:uint8_t{InverterTypeUnknown,Tritium,NLE}";
        
        
	//The hashmap that includes the transformed data.
	static HashMap<String, CANTypedef> mapping = new HashMap<>();

	//This method creates a hashmap of hashmaps of the form <enumName, hashmap<bytesequence, state>>, where enumName is the name of the enumeration, and the nested hashmap contains all possible
	//states that said enum with enumNume can have together with their unique bytesequence.
	static void parseTypedef() {
		String[] result = ACUdef.split(",");

		CANTypedef type = new CANTypedef();

		type.name = result[0];
		type.description = result[2];

		type.options = new HashMap<Byte, String>();

		String optionstring = result[2];

		String options = optionstring.split("{")[1].split("}")[0];
		String optionsCSV = options.replace(" ", ""); // Replace spaces with no space if any exist

		String[] optionswhatever = optionsCSV.split(","); // [RDW=0x0] [WSC0x4] [DARE=0x5]

		for (byte i = 0; i < optionswhatever.length; i++) {
			if (optionswhatever[i].contains("=")) { // If the byte value is defined, use that one
				// Split on =
				String[] splits = optionswhatever[i].split("="); // [RWD] [0x0]
				byte bytee = Byte.parseByte(splits[1].split("x")[1]); // Read 0x0 as a byte
				type.options.put(bytee, splits[0]);
			} else {
				type.options.put(i, optionswhatever[i]);
			}
		}

		mapping.put(type.name, type);
	}

	//Takes a CAN message "(1600453413.322000) canx 12d#01c90100a819d400" (which is in hexadecimal notation and contains 8 bytes) to a list of bytes 
	//should be returned as a list that contains the following bytes: "00000001 11001001 00000001 00000000 10101000 00011001 11010100 00000000"
	public List<String> parseDataList(String CANMessage) {
		List<String> result;

		String[] split0 = CANMessage.split("#"); //split0[1] = "01c90100a819d400"

		result = new ArrayList<String>((split0[1].length() + 2 - 1) / 2);

    	for (int i = 0; i < split0[1].length(); i += 2) {
        result.add(split0[1].substring(i, Math.min(split0[1].length(), i + 2)));
   	 	}

    	return result;
	}

	//Converts a CAN message "(1600453413.322000) canx 12d#01c90100a819d400" (which is in hexadecimal notation and contains 8 bytes) to a String:
	// "00000001 11001001 00000001 00000000 10101000 00011001 11010100 00000000". I'm not sure yet whether a list or string is more convenient, so we use both.
	public String parseDataString(String CANMessage) {
		String result;

		String[] split0 = CANMessage.split("#"); //split0[1] = "01c90100a819d400"

		int data = Integer.parseInt(split0[1], 16);
		result = String.valueOf(data);

    	return result;
	}


	//takes a CAN message "(1600453413.322000) canx 12d#01c90100a819d400" and returns the string id. The id is represented by the characters after canx and before the #. In this
	//example the id is denoted by 12d. Note: this is in hexadecimal. The id in this case is equal to 301.
	public int parseID(String CANMessage) {
		String[] split0 = CANMessage.split("x");
		String[] split1 = split0[0].split(" ");  //split0[0] = " 12d#01c90100a819d400"
		String[] split2 = split1[1].split("#");  //split1[1] = "12d#01c90100a819d400"
		String idInHex = split2[0]; //take the 'left side' after splitting "12d#01c90100a819d400" on "#"
		int ID = Integer.parseInt(idInHex,16); 

		return ID;
	}

	//Takes a CAN message "(1600453413.322000) canx 12d#01c90100a819d400" and returns the timestamp associated with it.
	public long parseTimestamp(String CANMessage) {
		String[] split0 = CANMessage.split(")"); 
		String[] split1 = split0[0].split("(");//split0[0] = "(1600453413.322000", split1[1] = "1600453413.322000" 


		long timestamp = Long.parseLong(split1[1]);
		return timestamp;
	}

	// We need to deduce what data types (enum, bool, bool: 1, int etc.) reside within a signal. This information is found in the typedefs.csv file by searching on ID.
	//Calculates one of the main parts of function parseOverview()
	public String deduceDataTypes(int ID) {
		return "temp";
	}

	

	/*
	 Using the ID of the message in combination with the CAN overview, we can figure out what data types are present in the current CAN message.
	 E.g. in this case something along the lines of:
	 (int timestamp, ACUMode mode, bool bmsAlive, bool ssbAlive, bool srvRegenAlive, bool esbAlive, InverterType inverter)
	 
	 Note that ideally we'd like to have a list that contains multiple different types in a specific order. 
	 It therefore probably shouldn't return a List of Strings, but see it as a placeholder.
	 */
	public List<List<String>> parseOverview(int id, int timestamp, String CANOverview) {
		List<String> temp = List.of("temp");
		List<List<String>> temp2 = List.of(temp);
		return temp2;
	}

	/*
	We can determine which bits belong to which data as this has a structured order. 
	We will create a hashmap that links these as follows:
	{(ACUMode mode, 00000001), (bool bmsAlive, 1), (bool ssbAlive, 0), 
	(bool srvRegenAlive, 1), (bool esbAlive, 1), (InverterType inverter, 00000110)}

	Note that we will probably save the timestamp and pass it along as a separate variable,
	only to be added to the final result at the end. It would otherwise clutter the hashmap.

	As for the parameters:
	l1: (int, ACUMode, bool, bool, bool, bool, InverterType)
	l2: (timestamp, mode, bmsAlive, ssbAlive, srvRegenAlive, esbAlive, inverter).
	endianness: if endianness is >= 1 the byte order is different. Each signal has a specific integer denoted to endianness, and this should thus be checked in messages.csv.

	Lastly, all available data types are:
	bool, bool:1, uint8_t, uint16_t, uint32_t, uint64_t, int8_t, int16_t, int32_t, int64_t, float, 
	and OTHER (where OTHER is always an enum).
	*/
	public List<String> determineBits(List<String> l1, List<String> l2, String dataBytes, int endianness) {
		
		List<String> result = new ArrayList<String>();

		for (int i = 0; i < l1.size() + 1; i++) {
			String dataType = l1.get(i);
			dataType.replaceAll(" ", ""); //make "bool: 1" and "bool:1" equivalent
			dataType.replaceAll("u", ""); //make "uints" equivalent to "ints"

			switch (dataType) {

				case "int": //int is only used for timestamp, thus we do nothing.
				result.add("null");
				break;

				case "bool": //8 bits
					result.add(dataBytes.substring(0,8)); //add bits to list3 (see overview on drive for a description of list3)
					dataBytes = dataBytes.substring(8); //remove the byte from the overall String of bytes.
				break;
				
				case "bool:1": //1 bit
				//We need to check whether there are multiple "bool:1"s in a row. If there are we need to look within the same byte to find the correct corresponding bit.
					int count = 0;
					for (int j = i+1; j < l2.size(); j++) {
						if (l1.get(j).equals("bool:1")) {
							count++;
						} else {
							break;
						}
					}

					if (count == 0) { //No consecutive "bool:1"s, thus only the least significant bit of the left-most byte is relevant.
						result.add(dataBytes.substring(7,8));
						dataBytes = dataBytes.substring(8);
					} else { //multiple consecutive "bool:1"s
						int beginIndex = 7;
						int endIndex = 8;
						for (int k = 0; k < count; k++) {
							result.add(dataBytes.substring(beginIndex, endIndex));
							beginIndex--;
							endIndex--;
							i++; //we are moving ahead in l1, thus i also needs to increase.

							if(beginIndex < 0) { //we've processed a full byte, and thus should move onto the next byte.
								dataBytes = dataBytes.substring(8);
								beginIndex = 7;
								endIndex = 8;
							}
							i--; //we've done i++ one too many times for consecutive booleans, as the for loop itself will also increment i at the end. We will thus decrement
									//it once.
						}

					}

				break;
				
				case "int8_t": 
					result.add(dataBytes.substring(0,8));
					dataBytes = dataBytes.substring(8);
					break;
				
				case "int16_t": 
					if(endianness > 0) {
						result.add(dataBytes.substring(0,16));
						dataBytes = dataBytes.substring(16);
					} else {
						String temp = dataBytes.substring(8, 16);
						temp.concat(dataBytes.substring(0,8));
						result.add(temp);

						dataBytes = dataBytes.substring(16);
					}
					break;
				
				
				case "int32_t": 
					if(endianness > 0) {
						result.add(dataBytes.substring(0,32));
						dataBytes = dataBytes.substring(32);
					} else {
						String temp = dataBytes.substring(24,32);
						temp.concat(dataBytes.substring(16,24));
						temp.concat(dataBytes.substring(8,16));
						temp.concat(dataBytes.substring(0,8));
						result.add(temp);
					}
					break;
				
				
				case "int64_t": 
					if(endianness > 0) {
						result.add(dataBytes);//is the only variable in the data, so string dataBytes does not need to be trimmed
					} else {
						String temp = dataBytes.substring(56,64);
						temp.concat(dataBytes.substring(48,56));
						temp.concat(dataBytes.substring(40,48));
						temp.concat(dataBytes.substring(32,40));
						temp.concat(dataBytes.substring(24,32));
						temp.concat(dataBytes.substring(16,24));
						temp.concat(dataBytes.substring(8,16));
						temp.concat(dataBytes.substring(0,8));
						result.add(temp);
					}
					break;
					

				
				case "float": 
					if (endianness > 0) {
					result.add(dataBytes.substring(0,32));
					dataBytes = dataBytes.substring(32);
					} else {
						String temp = dataBytes.substring(24,32);
						temp.concat(dataBytes.substring(16,24));
						temp.concat(dataBytes.substring(8,16));
						temp.concat(dataBytes.substring(0,8));
						result.add(temp);
					}
					break;

				default: 
					result.add(dataBytes.substring(0,8));
					dataBytes = dataBytes.substring(8);
					break;
				}
		}
		return result;
	}

	/* Finally, we use the hashmap created by determineBits() to determine what state the bytesequence
	on the left partains. The integers and booleans all have set bit/byte sequences (see top of this java file), 
	so those can be translated directly. The result should look as follows: 

	{
   "timestamp": "(1600453413.322000)"
   "name": "ACU_KeepAlive",
   "fields": [
	"ACU_Mode mode": "ACUModeRDW",
  	"bool bmsAlive": true,
	"bool ssbAlive": true,
	"bool srvRegenAlive": true,
	"bool esbAlive": true,
	"InverterType inverter": "Tritium"]
	}

	This is a specific format that allows for easier sending, receiving and parsing.
	
	As for what the lists are, please refer to the overview on google drive.
	*/
	public String determineConcreteData(List<String> l1, List<String> l2, List<String> l3) {

		List<String> result = new ArrayList<>();

		for(int i = 0; i < l1.size() + 1; i++) {
			dataType = l1.get(i);
			bytes = l3.get(i);

			switch (dataType.replaceAll(" ", "")) {

				case "int"://timestamp
				int timestamp = 0; //TO DO

				case "bool": //8 bits
					if (bytes.equals("00000000")) {
						result.add("false");
					} else {
						result.add("true");
					}
					break;
				
				case "bool:1": //1 bit
					if(bytes.equals("0")) {
						result.add("false");
					} else {
						result.add("true");
					}
					break;
				
				case "uint8_t": 
					result.add(Integer.toString(Integer.parseInt(bytes, 2)));
					break;
				
				case "uint16_t": 
					result.add(Integer.toString(Integer.parseInt(bytes, 2)));
					break;
						
				case "uint32_t": 
					result.add(Integer.toString(Integer.parseInt(bytes, 2)));
					break;	
				
				case "uint64_t": 
					result.add(Integer.toString(Integer.parseInt(bytes, 2)));
					break;
					
				case "int8_t": 
					String decimalValue; //final value to be calculated. Needs to be done in two steps: 1) determine if the value is negative or positive 2) determine value

					if(bytes.substring(0,1).equals("1")) { //if the first bit is a '1' the value is negative.
						decimalValue = "-";
					}
					
					decimal.concat(Integer.toString(Integer.parseInt(bytes.substring(1), 2)));
					result.add(decimal);
					break;
				
				case "int16_t": 
					String decimalValue; 

					if(bytes.substring(0,1).equals("1")) { 
						decimalValue = "-";
					}
					
					decimal.concat(Integer.toString(Integer.parseInt(bytes.substring(1), 2))); 
					result.add(decimal);
					break;
						
				case "int32_t": 
					String decimalValue; 

					if(bytes.substring(0,1).equals("1")) { 
						decimalValue = "-";
					}
					
					decimal.concat(Integer.toString(Integer.parseInt(bytes.substring(1), 2))); 
					result.add(decimal);
					break;	
				
				case "int64_t": 
					String decimalValue;

					if(bytes.substring(0,1).equals("1")) { 
						decimalValue = "-";
					}
					
					decimal.concat(Integer.toString(Integer.parseInt(bytes.substring(1), 2)));
					result.add(decimal);
					break;
				
				case "float": 
					int intBits = Integer.parseInt(bytes, 2);
					String floatValue = Float.intBitsToFloat(intBits).toString();
					result.add(floatValue);
					break;

				default: //None of the other cases, thus it must be an enum.
					CanTypeDef enums = mapping.get(dataType);
					
					break;
				}
		}
		String temp = "temp";
		return temp;
	}

}
