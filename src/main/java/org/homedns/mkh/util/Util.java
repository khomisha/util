/* 
 * Copyright 2020 Mikhail Khodonov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.homedns.mkh.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.regex.Pattern;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.log4j.Logger;
import com.google.gson.Gson;

/**
 * Utility object
 *
 */
public class Util {
	private static final Logger LOG = Logger.getLogger( Util.class );
	public static final Pattern NUM_PATTERN = Pattern.compile( "-?\\d+(\\.\\d+)?" );
	public static final Pattern TIMESTAMP_PATTERN = Pattern.compile( "(^(((\\d\\d)(([02468][048])|([13579][26]))-02-29)|(((\\d\\d)(\\d\\d)))-((((0\\d)|(1[0-2]))-((0\\d)|(1\\d)|(2[0-8])))|((((0[13578])|(1[02]))-31)|(((0[1,3-9])|(1[0-2]))-(29|30)))))\\s(([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d))$)" ); 
	public static final Pattern EMAIL_PATTERN = Pattern.compile( "^([\\w-]+(?:\\.[\\w-]+)*)@((?:[\\w-]+\\.)*\\w[\\w-]{0,66})\\.([a-z]{2,6}(?:\\.[a-z]{2})?)$" );
//	public static final Pattern PHONE_PATTERN = Pattern.compile( "\\\\+?\\\\d{1,4}?[-.\\\\s]?\\\\(?\\\\d{1,3}?\\\\)?[-.\\\\s]?\\\\d{1,4}[-.\\\\s]?\\\\d{1,4}[-.\\\\s]?\\\\d{1,9}" );
//	public static final Pattern PHONE_PATTERN_RU = Pattern.compile( "^((8|\\\\+7)[\\\\- ]?)?(\\\\(?\\\\d{3}\\\\)?[\\\\- ]?)?[\\\\d\\\\- ]{7,10}$" );
	public static final Pattern PHONE_PATTERN_RU = Pattern.compile( "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$" );
	public static final Pattern IP4_PATTERN = Pattern.compile( "(?:\\b|^)((?:(?:(?:\\d)|(?:\\d{2})|(?:1\\d{2})|(?:2[0-4]\\d)|(?:25[0-5]))\\.){3}(?:(?:(?:\\d)|(?:\\d{2})|(?:1\\d{2})|(?:2[0-4]\\d)|(?:25[0-5]))))(?:\\b|$)" );
	public static final Pattern RF_ZIP_CODE_PATTERN = Pattern.compile( "^(\\d{6})(?:)?$" );
	public static final Pattern MONEY_PATTERN = Pattern.compile( "[0-9]*(\\.[0-9][0-9])?$" );
	public static final Pattern JSON_FILENAME_PATTERN = Pattern.compile( "([a-zA-Z0-9\\s_\\\\.\\-\\(\\):])+(.json)$" );
	public static final Pattern PORT_PATTERN = Pattern.compile( "^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$" );

	private static final String HEXES = "0123456789abcdef";
	private static final String CHARS = "0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,!,?,_,@,#,$,%,&,*,(,),-,+,=,1,2,3,4,5,6,7,8,9,0";
	
	/**
	 * Returns current local date/time as a string
	 * 
	 * @return the current date/time in short format
	 */
	public static String now( ) {
		return( 
			DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT ).format( new Date( ) ) 
		);
	}

	/**
	* Returns generated unique id.
	*/
	public static String getGUID( ) {
		return( UUID.randomUUID( ).toString( ) );
	}
	
	/**
	 * Returns timestamp from specified ntp server
	 * 
	 * @param ntpHost the ntp server host
	 * 
	 * @return the ntp server timestamp or if ntp server unreachable local computer timestamp
	 */
	public static Timestamp getNtpTimestamp( String ntpHost ) {
		Timestamp timestamp = null;
		try {
			NTPUDPClient timeClient = new NTPUDPClient( );
			timeClient.setDefaultTimeout( 5000 );
			TimeInfo timeInfo = timeClient.getTime( InetAddress.getByName( ntpHost ) );
			timestamp = new Timestamp( timeInfo.getMessage( ).getTransmitTimeStamp( ).getTime( ) );
		} 
		catch ( Exception e ) {
			LOG.warn( "NTP server " + ntpHost + " unreachable. Using local machine time." );
			timestamp = new Timestamp( ( new Date( ) ).getTime( ) );
		}
		return( timestamp );
	}
	
	/**
	 * Converts specified timestamp to the specified time zone
	 * 
	 * @param timestamp the input timestamp
	 * @param sTimeZone the time zone id, e.g. "Asia/Irkutsk"
	 * 
	 * @return the timestamp
	 */
	public static Timestamp getZoneTimestamp( Timestamp timestamp, String sTimeZone ) {
		ZonedDateTime zdt = ZonedDateTime.ofInstant( timestamp.toInstant( ), ZoneId.of( sTimeZone ) );
		return( Timestamp.valueOf( zdt.format( DateTimeFormatter.ofPattern( "uuuu-MM-dd HH:mm:ss.nnnnnnnnn" ) ) ) );
	}

	/**
	 * Returns UTC+0 timestamp
	 * 
	 * @return the timestamp
	 */
	public static Timestamp getTimestamp( ) {
		OffsetDateTime utc = OffsetDateTime.now( ZoneOffset.UTC );
		return( Timestamp.valueOf( utc.format( DateTimeFormatter.ofPattern( "uuuu-MM-dd HH:mm:ss.nnnnnnnnn" ) ) ) );
	}
	
	/**
	 * Returns true if specified regex pattern match specified string a false otherwise
	 * 
	 * @param pattern the regex pattern
	 * @param s the string to test
	 * 
	 * @return true or false
	 */
	public static boolean isValid( Pattern pattern, String s ) {
		return( pattern.matcher( s ).matches( ) );
	}
	
	/**
	 * Returns true if specified regex match specified string a false otherwise.
	 * Always returns true if sRegex.isEmpty( ) || sRegex == null || s == null. 
	 * 
	 * @param sRegex the regex
	 * @param value the value to test
	 * 
	 * @return true or false
	 */
	public static boolean isValid( String sRegex, Object value ) {
		if( sRegex == null || sRegex.isEmpty( ) || value == null ) {
			return( true );
		}
		return( isValid( Pattern.compile( sRegex ), String.valueOf( value ) ) );
	}
	
	/**
	 * Returns Gson instance
	 * 
	 * @return the Gson instance
	 */
	public static Gson getGson( ) {
		return( GsonAgent.getAgent( ).getGson( ) );
	}
	
	/**
	 * Converts specified string to the hex string
	 * 
	 * @param s the source string
	 * 
	 * @return the hex string
	 */
//	public static String toHex( String s ) {
//		StringBuffer sb = new StringBuffer( );
//		char[] ac = s.toCharArray( );
//		for( char c : ac ) {
//			sb.append( Integer.toHexString( c ) );
//		}
//      	return( sb.toString( ) );
//	}
	
	/**
	 * Converts specified string to the hex string
	 * 
	 * @param s the string to convert
	 * 
	 * @return the hex string
	 */
	public static String toHex( String s ) {
        return( toHex( s.getBytes( StandardCharsets.UTF_8 ) ) );
	}

	/**
	 * Converts specified byte array to the hex string
	 * 
	 * @param ab the byte array
	 * 
	 * @return the hex string
	 */
	public static String toHex( byte[] ab ) {
		char[] chars = Hex.encodeHex( ab );
        return( String.valueOf( chars ) );
	}
	
	/**
	 * Converts byte array to the hexadecimal string.
	 * 
	 * @param ab
	 *            the byte array to convert
	 * 
	 * @return hexadecimal string or null if input array is null.
	 */
	public static String getHex( byte[] ab ) {
		if( ab == null ) {
			return null;
		}
		final StringBuilder hex = new StringBuilder( 2 * ab.length );
		for( final byte b : ab ) {
			hex.append( HEXES.charAt( ( b & 0xF0 ) >> 4 ) ).append( HEXES.charAt( ( b & 0x0F ) ) );
		}
		return hex.toString( );
	}
	
	/**
	 * Returns specified string hash digest using SHA-1 algorithm
	 * 
	 * @param s the source string
	 * 
	 * @return the hash code as hex string
	 * 
	 * @throws Exception
	 */
	public static String hashCode( String s ) throws Exception {
		MessageDigest md = MessageDigest.getInstance( "SHA-1" );
		return( toHex( md.digest( s.getBytes( ) ) ) );
	}
	
	/**
	 * Returns specified file hash 
	 * 
	 * @param path the file path
	 * 
	 * @return the hash code as hex string
	 * 
	 * @throws Exception
	 */
	public static String hashCode( Path path ) throws Exception {
		MessageDigest md = MessageDigest.getInstance( "SHA-1" );
		byte[] data = Files.readAllBytes( path );
		return( toHex( md.digest( data ) ) );
	}
	
	/**
	 * Returns hex string in UUID format(8-4-4-4-12)
	 * 
	 * @param sHex the hex string
	 * 
	 * @return the uuid as string
	 */
	public static String getUUID( String sHex ) {
		StringBuffer sb = new StringBuffer( sHex );
		if( sb.length( ) > 32 ) {
			sb = sb.delete( 32, sb.length( ) );
		}
		while( sb.length( ) < 32 ) {
			sb.append( "0" );
		}
		sb.insert( 8, "-" );
		sb.insert( 13, "-" );
		sb.insert( 18, "-" );
		sb.insert( 23, "-" );
		return( sb.toString( ) );
	}
	
	/**
	 * Converts hex string to the string using decode charset {@see java.nio.charset.StandardCharsets#UTF_8}
	 * 
	 * @param sHex the source hex string
	 * 
	 * @return the result string
	 */
	public static String fromHex( String sHex ) {
        return( fromHex( sHex, StandardCharsets.UTF_8 ) );
	}
	
	/**
	 * Converts hex string to the string
	 * 
	 * @param sHex the source hex string
	 * @param charset the charset to decode bytes
	 * 
	 * @return the result string
	 */
	public static String fromHex( String sHex, Charset charset ) {
        String s = "";
        try {
            byte[] ab = Hex.decodeHex( sHex );
            s = new String( ab, charset );
        } catch( DecoderException e ) {
            throw new IllegalArgumentException( "Invalid Hex format." );
        }
        return( s );
	}

	/**
	 * Converts hex string to the byte array
	 * 
	 * @param sHex the source hex string
	 * 
	 * @return the byte array
	 */
	public static byte[] fromHexString( String sHex ) {
		byte[] ab;
        try {
            ab = Hex.decodeHex( sHex );
        } catch( DecoderException e ) {
            throw new IllegalArgumentException( "Invalid Hex format." );
        }
        return( ab );
	}

	/**
	 * Returns true if this machine OS is windows and false if nix
	 * 
	 * @return true or false
	 */
	public static boolean isWindows( ) {
		return( System.getProperty( "os.name" ).toLowerCase( ).startsWith( "windows" ) );
	}
	
	/**
	 * Converts serializable object to the byte array
	 * 
	 * @param obj the object ot convert
	 * 
	 * @return the byte array
	 * 
	 * @throws IOException
	 */
	public static byte[] toByteArray( Serializable obj ) throws IOException {
		byte [] ab = null;
		try(
			ByteArrayOutputStream baos = new ByteArrayOutputStream( );
			ObjectOutputStream oos = new ObjectOutputStream( baos );
		) {
			oos.writeObject( obj );
			oos.flush( );
			ab = baos.toByteArray( );
		}
		return( ab );
	}

	/**
	 * Builds a string of the specified length by repeating the specified
	 * characters until the result string is long enough.
	 * 
	 * @param s
	 *            a string whose value will be repeated to fill the return
	 *            string
	 * @param iN
	 *            an integer whose value is the length of the string you want
	 *            returned
	 * 
	 * @return a string iN characters long filled with the characters in the
	 *         argument s. If the argument chars has more than iN characters,
	 *         the first iN characters of s are used to fill the return string.
	 *         If the argument s has fewer than iN characters, the characters in
	 *         s are repeated until the return string has iN characters. If any
	 *         argument's value is null, it returns null.
	 */
	public static String fill( String s, int iN ) {
		StringBuffer sb = new StringBuffer( );
		if( s.length( ) > iN ) {
			sb.append( s.substring( 0, iN ) );
		} else {
			int iCount = iN / s.length( );
			for( int i = 1; i <= iCount; i++ ) {
				sb.append( s );
			}
			if( iN - sb.length( ) > 0 ) {
				sb.append( s.substring( 0, iN - sb.length( ) ) );
			}
		}
		return( sb.toString( ) );
	}
	
	/**
	 * Returns stack trace as string
	 * 
	 * @param t the throwable
	 * 
	 * @return the stack trace
	 */
	public static String getStackTrace( Throwable t ) {
		StringBuffer sb = new StringBuffer( );
		for( StackTraceElement ste : t.getStackTrace( ) ) {
			sb.append( ste.toString( ) );
			sb.append( "\n" );
		}
		return( sb.length( ) > 0 ? sb.toString( ) : "empty stack trace" );
	}
	
	/**
	 * Returns hash as integer of the specified string
	 * 
	 * @param s the string
	 * 
	 * @return hash
	 */
	public static int getHash( String s ) {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( s == null ) ? 0 : s.hashCode( ) );
		return result;
	}
	
	/**
	 * Returns random string of the specified length
	 * 
	 * @param iLength the length
	 *  
	 * @return random string
	 */
	public static String getRandomString( int iLength ) {
		String[] as = CHARS.split( "," );
		if( iLength <= 0 ) {
			return( null );
		}
		StringBuffer sb = new StringBuffer( );
		Random random = new Random( );
		for( int iIndex = 0; iIndex < iLength; iIndex++ ) {
			sb.append( as[ random.nextInt( as.length ) ] );
		}
		return( sb.toString( ) );
	}
	
	/**
	 * Returns resource path
	 * 
	 * @param type the class
	 * @param sName the resource name
	 * 
	 * @return the resource path
	 */
	public static String getResourcePath( Class< ? > type, String sName ) {
		return( type.getResource( sName ).getPath( ) );
	}

	/**
	 * Assembles an array elements to string using a delimiter string.
	 * 
	 * @param asSrc
	 *            the array to assemble.
	 * @param sDelim
	 *            the delimiter.
	 * 
	 * @return s string which assembles array elements or empty string if array
	 *         is empty or null
	 */
	public static String assemble( String[] asSrc, String sDelim ) {
		List< String > list = Arrays.asList( asSrc );
		return( assemble( list, sDelim ) );
	}

	/**
	 * Assembles an list elements to string using a delimiter string.
	 * 
	 * @param list
	 *            the list to assemble.
	 * @param sDelim
	 *            the delimiter.
	 * 
	 * @return s the string which assembles list elements or empty string if list
	 *         is empty or null
	 */
	public static String assemble( List< String > list, String sDelim ) {
		if( list == null || list.size( ) == 0 ) {
			return( "" );
		}
		StringBuffer s = new StringBuffer( );
		int iItem;
		for( iItem = 0; iItem < list.size( ) - 1; iItem++ ) {
			s.append( list.get( iItem ) );
			s.append( sDelim );
		}
		s.append( list.get( iItem ) );
		return( s.toString( ) );
	}

	/**
	 * Returns resource bundle for given locale.
	 * 
	 * @param sBundle
	 *            the base name of the resource bundle, a fully qualified class
	 *            name
	 * @param locale
	 *            the locale object
	 * 
	 * @return resource bundle
	 */
	public static ResourceBundle getBundle( String sBundle, Locale locale ) {
		return( 
			ResourceBundle.getBundle( 
				sBundle, 
				locale,
                ResourceBundle.Control.getControl( ResourceBundle.Control.FORMAT_PROPERTIES )
            )
        );
	}

	/**
	 * Converts LSB integer to the MSB integer. Usage:
	 * <pre>
	 * public static void main(String argv[]) {
	 *     before 0x01020304
	 *     after  0x04030201
	 *     int v = 0x01020304;
	 *     System.out.println("before : 0x" + Integer.toString(v,16));
	 *     System.out.println("after  : 0x" + Integer.toString(swabInt(v),16));
	 * }
	 * </pre>
	 * 
	 * @param i
	 *            integer to convert
	 * 
	 * @return integer in MSB first.
	 */
	public final static int swabInt( int i ) {
		return ( i >>> 24 ) | ( i << 24 ) | ( ( i << 8 ) & 0x00FF0000 ) | ( ( i >> 8 ) & 0x0000FF00 );
	}

	/**
	 * Fixes ordinary and double quotes in input string
	 * 
	 * @param s
	 *            the input string
	 * 
	 * @return result string.
	 */
	public static String fixQuotes( String s ) {
		if( s == null || s.equals( "" ) ) {
			return( s );
		}
		s = s.replaceAll( "'", "~'" );
		return( s.replaceAll( "\"", "~\"" ) );
	}

	/**
	 * Converts integer to the byte array (little endian).
	 * 
	 * @param iValue
	 *            the integer to convert
	 * 
	 * @return byte array.
	 */
	public static final byte[] intToByteArray( int iValue ) {
	    return new byte[] {
	        ( byte )( iValue >>> 24 ),
	        ( byte )( iValue >>> 16 ),
	        ( byte )( iValue >>> 8 ),
	        ( byte )iValue
	    };
	}

	/**
	 * Converts byte array (little endian) to integer.
	 * 
	 * @param ab
	 *            the byte array to convert
	 * 
	 * @return integer.
	 */
	public static final int byteArrayToInt( byte[] ab ) {
	    return( ab[ 0 ] << 24 )
            + ( ( ab[ 1 ] & 0xFF ) << 16 )
            + ( ( ab[ 2 ] & 0xFF ) << 8 )
            + ( ab[ 3 ] & 0xFF );
	}
}
