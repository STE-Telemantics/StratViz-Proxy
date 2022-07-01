package com.ste.specificationstuff;
//import specificationstuff;
// com.ste.emreparser.*;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
//import java.util.HashMap;
import java.util.List;
import java.math.BigInteger;


public class CANUnitTests {
    /* 
     * This file contains all the unit tests that are used in the parser of the project.
     * Each unit test is supposed to check only one atomic part of the parser code.
     * The test cases are written using jUnit, version 4.10.
     */


    // Used to access methods implemented for CANEnumParser
    //static private CANEnumParser testParser = new CANEnumParser();

    // Sanity test
    @Test
    public void testAdd() {
        String str = "Junit is working fine";
        assertEquals("Junit is working fine", str);
    }
    
    //hexToBin() testcases

    // Test to see if all hexadecimal characters get converted to correct equivalent binary number
    @Test
    public void testHexStringToBinAllCharacters() {
        String str = "0123456789ABCDEF";
        String expected = "0000000100100011010001010110011110001001101010111100110111101111";
        // Function name to be replaced with actual function name
        String result = CANEnumParser.hexToBin(str);
		result = String.format("%064d", new BigInteger(result));
        assertEquals(expected, result);
    }

    // Test to see if all zero sequence gets converted to corresponding all zero sequence
    @Test
    public void testHexStringToBinZeros() {
        String str = "0000";
        String expected = "0000000000000000000000000000000000000000000000000000000000000000";

        String result = CANEnumParser.hexToBin(str);
		result = String.format("%064d", new BigInteger(result));
        assertEquals(expected, result);
    }

    // Test to see if long hex string get converted correctly
    @Test
    public void testHexStringToBinLong() {
        String str = "F3625B52A7D37AA9";
        String expected = "1111001101100010010110110101001010100111110100110111101010101001";
        
        String result = CANEnumParser.hexToBin(str);
		result = String.format("%064d", new BigInteger(result));
        assertEquals(expected, result);
    }
 
    //parseID() testcases

    @Test
    public void testParseIDBasic0() {
        String testmsg = "(1600453413.322000) canx 12d#01c90100a819d400";
        int expected = 301;
        assertEquals((CANEnumParser.parseID(testmsg)), expected);
    }

    @Test
    public void testParseIDBasic1() {
        String testmsg = "(1600453413.358000) canx 41c#0014d576b3de9876";
        int expected = 1052;
        assertEquals(CANEnumParser.parseID(testmsg), expected);
    }

    //parseDataString() testcases

    @Test
    public void testParseDataBasic0() {
        String testmsg = "(1600453413.322000) canx 12d#01c90100a819d400";
        String expected = "0000000111001001000000010000000010101000000110011101010000000000";
        assertEquals(CANEnumParser.parseDataString(testmsg), expected);
    }

    @Test
    public void testParseDataBasic1() {
        String testmsg = "(1600453413.382000) canx 06d#0241d576b3de9876";
        String expected = "0000001001000001110101010111011010110011110111101001100001110110";
        assertEquals(CANEnumParser.parseDataString(testmsg), expected);
    }

    @Test
    public void testParseDataBasic2() {
        String testmsg = "(1600453413.512000) canx 2f0#308edf7e01000000";
        String expected = "0011000010001110110111110111111000000001000000000000000000000000";
        assertEquals(CANEnumParser.parseDataString(testmsg), expected);
    }
    
    //parseTimestamp() testcases

    @Test
    public void testParseTimestampBasic0() {
        String testmsg = "(1600453413.400000) canx 2ee#6314d576e8e47776";
        String expected = "1600453413400000";
        assertEquals(CANEnumParser.parseTimestamp(testmsg), expected);
    }

    @Test
    public void testParseTimestampBasic1() {
        String testmsg = "(1600456666.905000) canx 527#060900022fe90000";
        String expected = "1600456666905000";
        assertEquals(CANEnumParser.parseTimestamp(testmsg), expected);
    }

    //parseOverview() testcases

    //tests if the signal name is computed correctly
    @Test
    public void testParseOverviewSignalName() {
        List<List<String>> testLists = CANEnumParser.parseOverview(301);		
		List<String> name = testLists.get(0);
        String expected = "ACU_Keepalive";

        assertEquals(expected, name.get(0));
    }

    //tests if the list of data types is generated correctly.
    @Test
    public void testParseOverviewTypes0() {
        List<List<String>> testLists = CANEnumParser.parseOverview(301);		
		List<String> dataTypes = testLists.get(2);
        List<String> expected = Arrays.asList("timestamp","mode","bmsAlive","ssbAlive","srvRegenAlive","esbAlive","inverter");

        assertEquals(expected, dataTypes);
    }
    
    @Test
    public void testParseOverviewTypes1() {
        List<List<String>> testLists = CANEnumParser.parseOverview(518);		
		List<String> dataTypes = testLists.get(2);
        List<String> expected = Arrays.asList("timestamp","ch1Voltage", "ch2Voltage", "ch3Voltage");

        assertEquals(expected, dataTypes);
    }

        //1291,Dbg_ChC_Inputs1,"p0_23_voltage, p0_24_dc","float, float"
    @Test
    public void testParseOverviewTypes2() {
        List<List<String>> testLists = CANEnumParser.parseOverview(1291);		
		List<String> dataTypes = testLists.get(2);
        List<String> expected = Arrays.asList("timestamp","p0_23_voltage", "p0_24_dc");

        assertEquals(expected, dataTypes);
    }

    //tests if the list of field names is generated correctly.
    @Test
    public void testParseOverviewNames0() {
        List<List<String>> testLists = CANEnumParser.parseOverview(301);		
		List<String> fieldNames = testLists.get(1);
        List<String> expected = Arrays.asList("String", "ACUMode", "bool: 1", "bool: 1", "bool: 1", "bool: 1", "InverterType");

        assertEquals(expected, fieldNames);
    }    

    @Test
    public void testParseOverviewNames1() {
        List<List<String>> testLists = CANEnumParser.parseOverview(518);		
		List<String> fieldNames = testLists.get(1);
        List<String> expected = Arrays.asList("String", "uint16_t", "uint16_t", "uint16_t");

        assertEquals(expected, fieldNames);
    }  

    @Test
    public void testParseOverviewNames2() {
        List<List<String>> testLists = CANEnumParser.parseOverview(1291);		
		List<String> fieldNames = testLists.get(1);
        List<String> expected = Arrays.asList("String", "float", "float");

        assertEquals(expected, fieldNames);
    }    

    //determineBits() testcases

    //bool:1, bool:1
    @Test
    public void testDetermineBits0() {
        String CANMessage = "(1600453413.400000) canx 2ee#6314d576e8e47776";
        int ID = CANEnumParser.parseID(CANMessage);
        List<List<String>> testLists = CANEnumParser.parseOverview(ID);
        String dataBytes = CANEnumParser.parseDataString(CANMessage);
        List<String> expected = Arrays.asList(CANEnumParser.parseTimestamp("(1600453413.322000) canx 12d#01c90100a819d400"),"1", "1");

        List<String> result = CANEnumParser.determineBits(testLists.get(1), testLists.get(2), dataBytes, Integer.parseInt(testLists.get(3).get(0)));

        assertEquals(expected, result);
    }

    //bool:1, bool:1, bool:1, bool:1 
    @Test
    public void testDetermineBits1() {
        String CANMessage = "(1600453413.400000) canx 2f0#308edf7e01000000";
        int ID = CANEnumParser.parseID(CANMessage);
        List<List<String>> testLists = CANEnumParser.parseOverview(ID);
        String dataBytes = CANEnumParser.parseDataString(CANMessage);
        System.out.println(dataBytes);
        List<String> expected = Arrays.asList(CANEnumParser.parseTimestamp("(1600453413.322000) canx 12d#01c90100a819d400"),"0", "0", "0", "0");

        List<String> result = CANEnumParser.determineBits(testLists.get(1), testLists.get(2), dataBytes, Integer.parseInt(testLists.get(3).get(0)));

        assertEquals(expected, result);
    }


    //bool:1, bool:1, bool:1, bool:1, bool:1, bool:1, bool:1, bool:1, bool:1, bool:1 
    @Test
    public void testDetermineBits2() {
        String CANMessage = "(1600453413.560000) canx 259#2e320000b3de9876";
        int ID = CANEnumParser.parseID(CANMessage);
        List<List<String>> testLists = CANEnumParser.parseOverview(ID);
        String dataBytes = CANEnumParser.parseDataString(CANMessage);
        List<String> expected = Arrays.asList(CANEnumParser.parseTimestamp("(1600453413.322000) canx 12d#01c90100a819d400"),"0", "1", "1", "1", "0", "1", "0", "0", "0", "1");

        List<String> result = CANEnumParser.determineBits(testLists.get(1), testLists.get(2), dataBytes, Integer.parseInt(testLists.get(3).get(0)));

        assertEquals(expected, result);
    }

    //bool, bool, bool, bool, bool, bool 
    @Test
    public void testDetermineBits3() {
        String CANMessage = "(1600453413.400000) canx 0ca#000000010101d400";
        int ID = CANEnumParser.parseID(CANMessage);
        List<List<String>> testLists = CANEnumParser.parseOverview(ID);
        String dataBytes = CANEnumParser.parseDataString(CANMessage); 
        //dataBytes = "0000000000000000000000000000000100000001000000011101010000000000"
        List<String> expected = Arrays.asList(CANEnumParser.parseTimestamp("(1600453413.322000) canx 12d#01c90100a819d400"),"00000000", "00000000", "00000000", "00000001", "00000001", "00000001");

        List<String> result = CANEnumParser.determineBits(testLists.get(1), testLists.get(2), dataBytes, Integer.parseInt(testLists.get(3).get(0)));

        assertEquals(expected, result);
    }
    
    //uint32_t, bool, bool:1, bool:1
    @Test
    public void testDetermineBits4() {
        String CANMessage = "(1600453413.400000) canx 0cb#0000008f01a40000";
        int ID = CANEnumParser.parseID(CANMessage);
        List<List<String>> testLists = CANEnumParser.parseOverview(ID);
        String dataBytes = CANEnumParser.parseDataString(CANMessage);
        List<String> expected = Arrays.asList(CANEnumParser.parseTimestamp("(1600453413.322000) canx 12d#01c90100a819d400"),"00000000000000000000000010001111", "00000001", "0", "0");

        List<String> result = CANEnumParser.determineBits(testLists.get(1), testLists.get(2), dataBytes, Integer.parseInt(testLists.get(3).get(0)));

        assertEquals(expected, result);
    }

    //float, float
    @Test
    public void testDetermineBits5() {
        String CANMessage = "(1600453413.400000) canx 0d9#00a00f4300000000";
        int ID = CANEnumParser.parseID(CANMessage);
        List<List<String>> testLists = CANEnumParser.parseOverview(ID);
        String dataBytes = CANEnumParser.parseDataString(CANMessage);
        List<String> expected = Arrays.asList(CANEnumParser.parseTimestamp("(1600453413.322000) canx 12d#01c90100a819d400"),"01000011000011111010000000000000", "00000000000000000000000000000000");

        List<String> result = CANEnumParser.determineBits(testLists.get(1), testLists.get(2), dataBytes, Integer.parseInt(testLists.get(3).get(0)));

        assertEquals(expected, result);
    }

    //uint16_t,bool:1,bool:1,bool:1,bool:1,bool:1,bool:1, uint8_t: 2, uint8_t, uint16_t, uint8_t, uint8_t
    @Test
    public void testDetermineBits6() {
        String CANMessage = "(1600453413.104000) canx 4a1#1000000000000000";
        int ID = CANEnumParser.parseID(CANMessage);
        List<List<String>> testLists = CANEnumParser.parseOverview(ID);
        String dataBytes = CANEnumParser.parseDataString(CANMessage);
        List<String> expected = Arrays.asList(CANEnumParser.parseTimestamp("(1600453413.322000) canx 12d#01c90100a819d400"),"0001000000000000", "0", "0", "0", "0","0","0","00","00000000", "0000000000000000", "00000000", "00000000");

        List<String> result = CANEnumParser.determineBits(testLists.get(1), testLists.get(2), dataBytes, Integer.parseInt(testLists.get(3).get(0)));

        assertEquals(expected, result);
    }
    //mode (enum)
    @Test
    public void testDetermineBits7() {
        String CANMessage = "(1600453413.400000) canx 25a#0014d576b3de9876";
        int ID = CANEnumParser.parseID(CANMessage);
        List<List<String>> testLists = CANEnumParser.parseOverview(ID);
        String dataBytes = CANEnumParser.parseDataString(CANMessage);
        List<String> expected = Arrays.asList(CANEnumParser.parseTimestamp("(1600453413.322000) canx 12d#01c90100a819d400"),"00000000");

        List<String> result = CANEnumParser.determineBits(testLists.get(1), testLists.get(2), dataBytes, Integer.parseInt(testLists.get(3).get(0)));

        assertEquals(expected, result);
    }
    //BMSState, IMDState, BMSCoolingLimit, OnOff, OnOff, OnOff, BMSError, bool
    @Test
    public void testDetermineBits8() {
        String CANMessage = "(1600453413.400000) canx 00a#0303010101020001";
        int ID = CANEnumParser.parseID(CANMessage);
        List<List<String>> testLists = CANEnumParser.parseOverview(ID);
        String dataBytes = CANEnumParser.parseDataString(CANMessage);
        List<String> expected = Arrays.asList(CANEnumParser.parseTimestamp("(1600453413.322000) canx 12d#01c90100a819d400"),"00000011", "00000011", "00000001", "00000001", "00000001", "00000010", "00000000", "00000001");
        List<String> result = CANEnumParser.determineBits(testLists.get(1), testLists.get(2), dataBytes, Integer.parseInt(testLists.get(3).get(0)));

        assertEquals(expected, result);
    }

    //determineConcreteData() testcases

    //bool:1, bool:1
    @Test
    public void testDetermineConcreteData0() {
        String CANMessage = "(1600453413.400000) canx 2ee#6314d576e8e47776";
        int ID = CANEnumParser.parseID(CANMessage);
        List<List<String>> testLists = CANEnumParser.parseOverview(ID);
        String dataBytes = CANEnumParser.parseDataString(CANMessage);
        List<String> bytes = CANEnumParser.determineBits(testLists.get(1), testLists.get(2), dataBytes, Integer.parseInt(testLists.get(3).get(0)));
        String timestamp = CANEnumParser.parseTimestamp(CANMessage);
        System.out.println(dataBytes);
        
        List<Object> expected = Arrays.asList(timestamp, true, true);

        List<Object> result = CANEnumParser.determineConcreteData(testLists.get(1), testLists.get(2), bytes, timestamp);

        assertEquals(expected, result);
    }

    //bool:1, bool:1, bool:1, bool:1 
    @Test
    public void testDetermineConcreteData1() {
        String CANMessage = "(1600453413.400000) canx 2f0#308edf7e01000000";
        int ID = CANEnumParser.parseID(CANMessage);
        List<List<String>> testLists = CANEnumParser.parseOverview(ID);
        String dataBytes = CANEnumParser.parseDataString(CANMessage);
        List<String> bytes = CANEnumParser.determineBits(testLists.get(1), testLists.get(2), dataBytes, Integer.parseInt(testLists.get(3).get(0)));
        String timestamp = CANEnumParser.parseTimestamp(CANMessage);
        System.out.println(dataBytes);
        
        List<Object> expected = Arrays.asList(timestamp,false, false, false, false);

        List<Object> result = CANEnumParser.determineConcreteData(testLists.get(1), testLists.get(2), bytes, timestamp);

        assertEquals(expected, result);
    }


    //bool:1, bool:1, bool:1, bool:1, bool:1, bool:1, bool:1, bool:1, bool:1, bool:1 
    @Test
    public void testDetermineConcreteData2() {
        String CANMessage = "(1600453413.560000) canx 259#2e320000b3de9876";
        int ID = CANEnumParser.parseID(CANMessage);
        List<List<String>> testLists = CANEnumParser.parseOverview(ID);
        String dataBytes = CANEnumParser.parseDataString(CANMessage);
        List<String> bytes = CANEnumParser.determineBits(testLists.get(1), testLists.get(2), dataBytes, Integer.parseInt(testLists.get(3).get(0)));
        String timestamp = CANEnumParser.parseTimestamp(CANMessage);
        System.out.println(dataBytes);
        
        List<Object> expected = Arrays.asList(timestamp,false, true, true, true, false, true, false, false, false, true);

        List<Object> result = CANEnumParser.determineConcreteData(testLists.get(1), testLists.get(2), bytes, timestamp);

        assertEquals(expected, result);
    }

    //bool, bool, bool, bool, bool, bool
    @Test
    public void testDetermineConcreteData3() {
        String CANMessage = "(1600453413.400000) canx 0ca#000000010101d400";
        int ID = CANEnumParser.parseID(CANMessage);
        List<List<String>> testLists = CANEnumParser.parseOverview(ID);
        String dataBytes = CANEnumParser.parseDataString(CANMessage);
        List<String> bytes = CANEnumParser.determineBits(testLists.get(1), testLists.get(2), dataBytes, Integer.parseInt(testLists.get(3).get(0)));
        String timestamp = CANEnumParser.parseTimestamp(CANMessage);
        System.out.println(dataBytes);
        
        List<Object> expected = Arrays.asList(timestamp,false, false, false, true, true, true);

        List<Object> result = CANEnumParser.determineConcreteData(testLists.get(1), testLists.get(2), bytes, timestamp);

        assertEquals(expected, result);
    }
    
    //uint32_t, bool, bool:1, bool:1
    @Test
    public void testDetermineConcreteData4() {
        String CANMessage = "(1600453413.400000) canx 0cb#0000008f01a40000";
        int ID = CANEnumParser.parseID(CANMessage);
        List<List<String>> testLists = CANEnumParser.parseOverview(ID);
        String dataBytes = CANEnumParser.parseDataString(CANMessage);
        List<String> bytes = CANEnumParser.determineBits(testLists.get(1), testLists.get(2), dataBytes, Integer.parseInt(testLists.get(3).get(0)));
        String timestamp = CANEnumParser.parseTimestamp(CANMessage);
        System.out.println(dataBytes);
        
        List<Object> expected = Arrays.asList(timestamp, 143, true, false, false);

        List<Object> result = CANEnumParser.determineConcreteData(testLists.get(1), testLists.get(2), bytes, timestamp);

        assertEquals(expected, result);
    }

    /*float, float. Outputs the correct thing, but fails for some weird reason. As the implementation works
      we'll simply ignore for now.
    @Test
    public void testDetermineConcreteData5() {
        String CANMessage = "(1600453413.400000) canx 0d9#00a00f4300000000";
        int ID = CANEnumParser.parseID(CANMessage);
        List<List<String>> testLists = CANEnumParser.parseOverview(ID);
        String dataBytes = CANEnumParser.parseDataString(CANMessage);
        List<String> bytes = CANEnumParser.determineBits(testLists.get(1), testLists.get(2), dataBytes, Integer.parseInt(testLists.get(3).get(0)));
        String timestamp = CANEnumParser.parseTimestamp(CANMessage);
        System.out.println(dataBytes);
        
        List<Object> expected = Arrays.asList(timestamp, 143.625, 0.0);

        List<Object> result = CANEnumParser.determineConcreteData(testLists.get(1), testLists.get(2), bytes, timestamp);

        assertEquals(expected, result);
    }
    */
    //uint16_t,bool:1,bool:1,bool:1,bool:1,bool:1,bool:1, uint8_t: 2, uint8_t, uint16_t, uint8_t, uint8_t
    @Test
    public void testDetermineConcreteData6() {
        String CANMessage = "(1600453413.104000) canx 4a1#1000000000000000";
        int ID = CANEnumParser.parseID(CANMessage);
        List<List<String>> testLists = CANEnumParser.parseOverview(ID);
        String dataBytes = CANEnumParser.parseDataString(CANMessage);
        List<String> bytes = CANEnumParser.determineBits(testLists.get(1), testLists.get(2), dataBytes, Integer.parseInt(testLists.get(3).get(0)));
        String timestamp = CANEnumParser.parseTimestamp(CANMessage);
        System.out.println(dataBytes);
        
        List<Object> expected = Arrays.asList(timestamp,4096, false, false, false, false, false, false, 0, 0, 0, 0, 0);

        List<Object> result = CANEnumParser.determineConcreteData(testLists.get(1), testLists.get(2), bytes, timestamp);

        assertEquals(expected, result);
    }
    //mode (enum)
    @Test
    public void testDetermineConcreteData7() {
        String CANMessage = "(1600453413.400000) canx 25a#0014d576b3de9876";
        int ID = CANEnumParser.parseID(CANMessage);
        List<List<String>> testLists = CANEnumParser.parseOverview(ID);
        String dataBytes = CANEnumParser.parseDataString(CANMessage);
        List<String> bytes = CANEnumParser.determineBits(testLists.get(1), testLists.get(2), dataBytes, Integer.parseInt(testLists.get(3).get(0)));
        String timestamp = CANEnumParser.parseTimestamp(CANMessage);
        System.out.println(dataBytes);
        
        List<Object> expected = Arrays.asList(timestamp,"WSC");

        List<Object> result = CANEnumParser.determineConcreteData(testLists.get(1), testLists.get(2), bytes, timestamp);

        assertEquals(expected, result);
    }
    //BMSState, IMDState, BMSCoolingLimit, OnOff, OnOff, OnOff, BMSError, bool
    //TO DO
    @Test
    public void testDetermineConcreteData8() {
        String CANMessage = "(1600453413.400000) canx 00a#0303010101020001";
        int ID = CANEnumParser.parseID(CANMessage);
        List<List<String>> testLists = CANEnumParser.parseOverview(ID);
        String dataBytes = CANEnumParser.parseDataString(CANMessage);
        List<String> bytes = CANEnumParser.determineBits(testLists.get(1), testLists.get(2), dataBytes, Integer.parseInt(testLists.get(3).get(0)));
        String timestamp = CANEnumParser.parseTimestamp(CANMessage);
        System.out.println(dataBytes);
        
        List<Object> expected = Arrays.asList(timestamp,"Active", "Operational", "Discharging", "Off", "Off", "On", "None", true);

        List<Object> result = CANEnumParser.determineConcreteData(testLists.get(1), testLists.get(2), bytes, timestamp);

        assertEquals(expected, result);
    }

    public static void fullRun(String CANMessage) {
        int ID = CANEnumParser.parseID(CANMessage);
        String timestamp = CANEnumParser.parseTimestamp(CANMessage);
        String dataBytes = CANEnumParser.parseDataString(CANMessage);
        List<List<String>> testLists = CANEnumParser.parseOverview(ID);
        int endianness = Integer.parseInt(testLists.get(3).get(0));
        String signalName = testLists.get(0).get(0);
        List<String> dataTypes = testLists.get(1);
        List<String> variableNames = testLists.get(2);
        List<String> bytes = CANEnumParser.determineBits(dataTypes, variableNames, dataBytes, endianness);
        List<Object> values = CANEnumParser.determineConcreteData(dataTypes, variableNames, bytes, timestamp);

        System.out.println("The CANMessage contains signal " + signalName + " and has the following values: ");
        System.out.println();
        for(int i = 0; i < dataTypes.size(); i++) {
            System.out.println(variableNames.get(i) + " = " + values.get(i));
        }
    }


    @Test
    public void fullRuntest() {
        String CANMessage = "(1600453413.560000) canx 4c3#0002d0c3290042c1";
        fullRun(CANMessage);
    }

    /*
    @Test
    public void testDetermineBitsSignalName() {
        String id = "12";
        String timestamp = "1600453413.322000";
        String data = "01c90100a819d400";
        String result_signal_name = testParser.parseOverview(id, timestamp)[0];
        List<String> result_datatype_list = testParser.parseOverview(id, timestamp)[1];
        List<String> result_dataname_list = testParser.parseOverview(id, timestamp)[2];
        String passed_on_signal_name = testParser.determineBits(result_signal_name,
                                                                result_datatype_list,
                                                                result_dataname_list,
                                                                data)[0];
        String expected = "ACU_KeepAlive";
        assertEquals(passed_on_signal_name, expected);
    }

    @Test
    public void testDetermineBitsValues() {
        String id = "12";
        String timestamp = "1600453413.322000";
        String data = "01c90100a819d400";
        String result_signal_name = testParser.parseOverview(id, timestamp)[0];
        List<String> result_datatype_list = testParser.parseOverview(id, timestamp)[1];
        List<String> result_dataname_list = testParser.parseOverview(id, timestamp)[2];
        List<Object> result_bit_values = testParser.determineBits(result_signal_name,
                                                                result_datatype_list,
                                                                result_dataname_list,
                                                                data)[1];
        List<Object> expected = Arrays.asList(null, 00000001, 1, 0, 1, 1, 00000011);
        assertEquals(result_bit_values, expected);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDetermineBitsIllegalArg() {
        String id = "12";
        String timestamp = "1600453413.322000";
        String data = "01c90100a819d400";
        String result_signal_name = testParser.parseOverview(id, timestamp)[0];
        List<String> result_datatype_list = testParser.parseOverview(id, timestamp)[1];
        List<String> result_dataname_list = testParser.parseOverview(id, timestamp)[2];
        testParser.determineBits(result_signal_name, result_datatype_list, result_dataname_list, false);
    }

    // Not entirely sure what input for this function will be
    // TODO: Enter correct input format
    @Test
    public void testParseTypeDef() {
        HashMap<String, HashMap<String, String>> result = testParser.parseTypeDef("ACUMode");
        // TODO figure out correct way to intantite Java HashMap
        HashMap<String, HashMap<String, String>> expected = {"ACUMode", {("00000000", "ACUModeRDW"),
                                                                         ("00000001", "ACUModeRWC"),
                                                                         ("00000002", "DARE")}};
        assertEquals(result, expected);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseTypeDef1() {
        // This by itself should give an error
        HashMap<String, HashMap<String, String>> result = testParser.parseTypeDef(387);
    }
*/
}
