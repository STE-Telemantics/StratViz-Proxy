import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class CANTransformTest {
    // Used to access methods implemented for CANEnumParser
    CANParser testParser = new CANParser();

    // Sanity test
    @Test
    public void testAdd() {
        String str = "Junit is working fine";
        assertEquals("Junit is working fine", str);
    }
    
    // Test to see if all hexadecimal characters get converted to correct equivalent binary number
    @Test
    public void testHexStringToBinAllCharacters() {
        int str = 0x0123456789ABCDEF;
        String expected = "0000000100100011010001010110011110001001101010111100110111101111";
        // Function name to be replaced with actual function name
        assertEquals(expected, testParser.convertHexadecimal(str));
    }

    // Test to see if all zero sequence gets converted to corresponding all zero sequence
    @Test
    public void testHexStringToBinZeros() {
        String str = "0000";
        String expected = 0x0000000000000000;
        // Function name to be replaced with actual function name
        assertEquals(expected, testParser.convertHexadecimal(str));
    }

    // Test to see if empty string is handled correctly
    @Test
    public void testHexStringToBinEmpty() {
        String str = "";
        String expected = "";
        // Function name to be replaced with actual function name
        assertEquals(expected, testParser.convertHexadecimal(str));
    }

    // Test to see if short hex string gets converted correctly
    @Test
    public void testHexStringToBinShort() {
        String str = "625AC";
        String expected = "01100010010110101100";
        // Function name to be replaced with actual function name
        assertEquals(expected, testParser.convertHexadecimal(str));
    }

    // Test to see if long hex string get converted correctly
    @Test
    public void testHexStringToBinLong() {
        String str = "F3625B52A7D37AA9";
        String expected = "1111001101100010010110110101001010100111110100110111101010101001";
        // Function name to be replaced with actual function name
        assertEquals(expected, testParser.convertHexadecimal(str));        
    }

    // Test to see if an illegal argument provides the expected error message
    @Test(expected = IllegalArgumentException.class)
    public void testHexStringToBinIllegalArg() {
        // Function name to be replaced with actual function name
        testParser.convertHexadecimal(true); // should only accept strings, so this boolean should throw an error
    }

    @Test
    public void testParseIDBasic() {
        String testmsg = "(1600453413.322000) canx 12d#01c90100a819d400";
        int expected = 12;
        assertEquals(testParser.parseID("(1600453413.322000) canx 12d#01c90100a819d400"), expected);
    }

    @Test
    public void testParseDataBasic() {
        String testmsg = "(1600453413.322000) canx 12d#01c90100a819d400";
        byte expected = "01c90100a819d400";
        assertEquals(testParser.parseData("(1600453413.322000) canx 12d#01c90100a819d400"), expected);
    }
    
    @Test
    public void testParseTimestampBasic() {
        String testmsg = "(1600453413.322000) canx 12d#01c90100a819d400";
        byte expected = "1600453413.322000";
        assertEquals(testParser.parseTimestamp("(1600453413.322000) canx 12d#01c90100a819d400"), expected);
    }
}
