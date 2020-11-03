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

import java.io.Serializable;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

/**
 * Gson agent
 *
 */
public class GsonAgent {
	private Gson gson;
	private SimpleDateFormat dformat;
	
	/**
	 * 
	 */
	private GsonAgent( ) {
		dformat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		gson = new GsonBuilder( )
			.registerTypeAdapter( Timestamp.class, new TimestampSerializer( ) )
			.registerTypeAdapter( Timestamp.class, new TimestampDeserializer( ) )
			.registerTypeAdapterFactory( new SerializableTypeAdapterFactory( ) )
			.disableHtmlEscaping( )
			.setPrettyPrinting( )
			.create( );		
	}
	
	/**
	 * Returns gson agent instance
	 * 
	 * @return the gson agent instance
	 */
	public static GsonAgent getAgent( ) {
		return( Singleton.INSTANCE );
	}

	/**
	 * Returns gson
	 * 
	 * @return the gson
	 */
	public Gson getGson( ) {
		return( gson );
	}
	
	/**
	 * Custom timestamp serializer
	 *
	 */
	private class TimestampSerializer implements JsonSerializer< Timestamp > {
		/**
		 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
		 */
		@Override
		public JsonElement serialize( 
			Timestamp timestamp, Type type, JsonSerializationContext context 
		) {
			return( new JsonPrimitive( dformat.format( timestamp ) ) );
		}
	}
	
	/**
	 * Custom timestamp deserializer
	 *
	 */
	private class TimestampDeserializer implements JsonDeserializer< Timestamp > {
		
		public Timestamp deserialize( JsonElement json, Type type, JsonDeserializationContext context ) throws JsonParseException {
			Timestamp ts = null;
			try {
				ts = new Timestamp( dformat.parse( json.getAsJsonPrimitive( ).getAsString( ) ).getTime( ) );
			}
			catch( ParseException e ) {
				throw new JsonParseException( e );
			}
		    return( ts );
	    }
	}
	
	/**
	 * Type adapter factory for Serializable data type
	 *
	 */
	private class SerializableTypeAdapterFactory implements TypeAdapterFactory {

		/**
		 * @see com.google.gson.TypeAdapterFactory#create(com.google.gson.Gson, com.google.gson.reflect.TypeToken)
		 */
		@SuppressWarnings( "unchecked" )
		@Override
		public < T > TypeAdapter< T > create( Gson gson, TypeToken< T > type ) {
			if( Serializable.class.equals( type.getRawType( ) ) ) {
				return( ( TypeAdapter< T > )gson.getAdapter( Object.class ) );
			}
			return( null );
		}		
	}

	/**
	 * Inner class to provide instance of the class
	 */
	private static class Singleton {
		private static final GsonAgent INSTANCE = new GsonAgent( );		
	}
}
