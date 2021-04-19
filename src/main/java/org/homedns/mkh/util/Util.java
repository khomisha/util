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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
	public static final Pattern IP4_PATTERN = Pattern.compile( "(?:\\b|^)((?:(?:(?:\\d)|(?:\\d{2})|(?:1\\d{2})|(?:2[0-4]\\d)|(?:25[0-5]))\\.){3}(?:(?:(?:\\d)|(?:\\d{2})|(?:1\\d{2})|(?:2[0-4]\\d)|(?:25[0-5]))))(?:\\b|$)" );
	public static final Pattern RF_ZIP_CODE_PATTERN = Pattern.compile( "^(\\d{6})(?:)?$" );
	public static final Pattern MONEY_PATTERN = Pattern.compile( "[0-9]*(\\.[0-9][0-9])?$" );
	public static final Pattern JSON_FILENAME_PATTERN = Pattern.compile( "([a-zA-Z0-9\\s_\\\\.\\-\\(\\):])+(.json)$" );
	public static final Pattern PORT_PATTERN = Pattern.compile( "^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$" );

	private static final String HEXES = "0123456789abcdef";
	
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
	 * @return the hash code as hex string
	 * 
	 * @throws Exception
	 */
	public static String hashCode( String s ) throws Exception {
		MessageDigest md = MessageDigest.getInstance( "SHA-1" );
		md.update( s.getBytes( ) );
		return( getHex( md.digest( ) ) );
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
	 * Converts hex string to the string
	 * 
	 * @param sHex the source hex string
	 * 
	 * @return the result string
	 */
	public static String fromHex( String sHex ) {
        String s = "";
        try {
            byte[] ab = Hex.decodeHex( sHex );
            s = new String( ab, StandardCharsets.UTF_8 );
        } catch( DecoderException e ) {
            throw new IllegalArgumentException( "Invalid Hex format." );
        }
        return( s );
	}
	
	/**
	 * Returns true if this machine OS is windows and false if linux
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

}
