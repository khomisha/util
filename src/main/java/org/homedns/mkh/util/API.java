package org.homedns.mkh.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Surrounding application interface
 * 
 * @author Mikhail Khodonov
 *
 */
public interface API {
		
	/**
	 * Processes specified method
	 * 
	 * @param sMethod the method signature in json format
	 * 
	 * @return the result in json format or null
	 * 
	 * @throws Exception
	 */
	public String processMethod( String sMethod ) throws Exception;
	
	/**
	 * Returns true if method with specified name exist in specified type and false otherwise 
	 * 
	 * @param type the class type
	 * @param sMethodName the method name
	 * 
	 * @return true or false
	 */
	public static boolean isExist( Class< ? > type, String sMethodName ) {
		List< Method > methods = Arrays.asList( type.getDeclaredMethods( ) );
		Optional< Method > method = methods.stream( ).filter( m -> sMethodName.equals( m.getName( ) ) ).findFirst( );
		return( method.isPresent( ) );
	}
}
