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

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.google.gson.ExclusionStrategy;
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
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * Gson agent
 *
 */
public class GsonAgent {
	private Gson gson;
	private SimpleDateFormat dformat;
	
	private GsonAgent( ) {
		dformat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		gson = build( );
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
	 * Returns default gson
	 * 
	 * @return the gson
	 */
	public Gson getGson( ) {
		return( gson );
	}
	
	/**
	 * Builds default gson
	 * 
	 * @return the default gson object
	 */
	private Gson build( ) {
		return( createBuilder( null, false, false ).create( ) );
	}
	
	/**
	 * Builds gson with custom exclusion strategy
	 * 
	 * @param strategy the strategy
	 * @param bSerialization the exclusion serialization strategy flag 
	 * @param bDeserialization the exclusion deserialization strategy flag
	 * 
	 * @return the gson object
	 */
	public Gson build( ExclusionStrategy strategy, boolean bSerialization, boolean bDeserialization ) {
		return( createBuilder( strategy, bSerialization, bDeserialization ).create( ) );
	}
	
	/**
	 * {@link #build(ExclusionStrategy, boolean, boolean)}
	 */
	private GsonBuilder createBuilder( ExclusionStrategy strategy, boolean bSerialization, boolean bDeserialization ) {
		GsonBuilder builder = new GsonBuilder( )
			.registerTypeAdapter( Timestamp.class, new TimestampSerializer( ) )
			.registerTypeAdapter( Timestamp.class, new TimestampDeserializer( ) )
			.registerTypeAdapterFactory( new SerializableTypeAdapterFactory( ) )
			.serializeNulls( )
			.disableHtmlEscaping( );
//			.setPrettyPrinting( );
		if( strategy != null && bSerialization ) {
			builder = builder.addSerializationExclusionStrategy( strategy );
		}
		if( strategy != null && bDeserialization ) {
			builder = builder.addDeserializationExclusionStrategy( strategy );			
		}
		return( builder );	
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
		public JsonElement serialize( Timestamp timestamp, Type type, JsonSerializationContext context ) {
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
				return( ( TypeAdapter< T > ) new SerializableTypeAdapter( gson ) );
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
	
	/**
	 * Type adapter for Serializable
	 *
	 */
	private class SerializableTypeAdapter extends TypeAdapter< Serializable > {
		private Gson gson;
		
		private SerializableTypeAdapter( Gson gson ) {
			this.gson = gson;
		}

		/**
		 * @see com.google.gson.TypeAdapter#write(com.google.gson.stream.JsonWriter, java.lang.Object)
		 */
		@Override
		public void write( JsonWriter out, Serializable value ) throws IOException {
			gson.getAdapter( Object.class ).write( out, value );
		}

		/**
		 * @see com.google.gson.TypeAdapter#read(com.google.gson.stream.JsonReader)
		 */
		@Override
		public Serializable read( JsonReader in ) throws IOException {
		    JsonToken token = in.peek( );
		    switch( token ) {
			    case BEGIN_ARRAY:
			    	ArrayList< Serializable > list = new ArrayList< Serializable >( );
			    	in.beginArray( );
			    	while( in.hasNext( ) ) {
			    		list.add( read( in ) );
			    	}
			    	in.endArray( );
			    	return( list );
			    case BEGIN_OBJECT:
			    	LinkedTreeMap< String, Serializable > map = new LinkedTreeMap< String, Serializable >( );
			    	in.beginObject( );
			    	while( in.hasNext( ) ) {
			    		map.put( in.nextName( ), read( in ) );
			    	}
			    	in.endObject( );
			    	return( map );
			    case STRING:
			    	return( in.nextString( ) );
			    case NUMBER:
	                String sNumber = in.nextString( );
	                if( sNumber.contains( "." ) || sNumber.contains( "e" ) || sNumber.contains( "E" ) ) {
	                    return( Double.parseDouble( sNumber ) );
	                }
	                if( Long.parseLong( sNumber ) <= Integer.MAX_VALUE ) {
	                    return( Integer.parseInt( sNumber ) );
	                }
	                return( Long.parseLong( sNumber ) );
			    case BOOLEAN:
			    	return( in.nextBoolean( ) );
			    case NULL:
			    	in.nextNull( );
			    	return( null );
			    default:
			    	throw new IllegalStateException( );
		    }
		}
	}
}
