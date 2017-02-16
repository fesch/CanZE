/*
    CanZE
    Take a closer look at your ZE car

    Copyright (C) 2015 - The CanZE Team
    http://canze.fisch.lu

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any
    later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/


/*
 * Some convertion utilities.
 */
package lu.fisch.canze.actors;


import lu.fisch.canze.activities.MainActivity;

/**
 *
 * @author robertfisch
 */
public class Utils {
    /**
     * Converts a string in hexadecimals format into a byte array
     * @param hexString     the hexadecimal string
     * @return 
     */
    public static int[] toIntArray(String hexString) throws IllegalArgumentException
    {
        // make shure the string has an even length
        // cut the last symbol if length is odd
        //if(hexString.length()%2==1) hexString=hexString.substring(0, hexString.length()-1);
        return parseHexBinary(hexString);
    }

    public static int[] toIntArray(byte[] byteArray)
    {
        int[] toIntArray = new int[byteArray.length];
        for(int i=0; i<byteArray.length; i++)
            toIntArray[i]=(byteArray[i]<0?256+byteArray[i]:byteArray[i]);
        return toIntArray;
    }
    
    public static String get8Bit(int b)
    {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }
    
    public static int getFirstNibble(int b)
    {
        return (b >> 4);
    }
    
    public static int getLastNibble(int b)
    {
        return (b & 0x0F);
    }
    
    public static void main(String[] args)
    {
        int valueO = (int) 0x1A;
        int value = valueO;
        System.out.println("Value = "+(value)+" / "+get8Bit(value));
        
        value = valueO;
        value = getFirstNibble(value);
        System.out.println("First = "+(value)+" / "+get8Bit(value));
        
        value = valueO;
        value = getLastNibble(value);
        System.out.println("Last  = "+(value)+" / "+get8Bit(value));
    }


    // source: http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java

    public static int[] parseHexBinary(String s) throws IllegalArgumentException
    {
        final int len = s.length();

        // "111" is not a valid hex encoding.
        if( len%2 != 0 )
            throw new IllegalArgumentException("hexBinary needs to be even-length: "+s);

        int[] out = new int[len/2];

        for( int i=0; i<len; i+=2 ) {
            int h = hexToBin(s.charAt(i  ));
            int l = hexToBin(s.charAt(i+1));
            if( h==-1 || l==-1 )
                throw new IllegalArgumentException("contains illegal character for hexBinary: "+s);

            out[i/2] = (int)(h*16+l);
        }

        return out;
    }

    private static int hexToBin( char ch ) {
        if( '0'<=ch && ch<='9' )    return ch-'0';
        if( 'A'<=ch && ch<='F' )    return ch-'A'+10;
        if( 'a'<=ch && ch<='f' )    return ch-'a'+10;
        return -1;
    }

    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    public String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length*2);
        for ( byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    public static double kmOrMiles (double km) {if (MainActivity.milesMode) return km/1.609344; return km;}

}
