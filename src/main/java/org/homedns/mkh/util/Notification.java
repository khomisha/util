/* 
 * Copyright 2018-2020 Mikhail Khodonov.
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/**
 * Notification object
 *
 */
public abstract class Notification {
    private final Map< Class< ? extends Event >, Map< Class< ? extends Publisher >, List< Subscriber > > > subscribers;
    
    public Notification( ) {
    	subscribers = new ConcurrentHashMap< >( );
	}

    /**
     * Notifies that specified event has happened in specified publisher
     * 
     * @param event the event
     * @param publisher the event source
     * 
     * @throws Exception 
     */
    public void publish( Event event, Publisher publisher ) throws Exception {
        List< Subscriber > list = subscribers.get( event.getClass( ) ).get( publisher.getClass( ) );
		if( list != null ) {
			for( Subscriber s : list ) {
				s.update( event, publisher );
			}
 		}
    }

    /**
     * Subscribes specified subscriber to the specified event and event source
     * 
     * @param eventType the event type
     * @param publisherType the event source type
     * @param subscriber the subscriber to subscribe
     */
    public void subscribe( 
    	Class< ? extends Event > eventType, 
    	Class< ? extends Publisher > publisherType, 
    	Subscriber subscriber 
    ) {
        if( !subscribers.containsKey( eventType ) ) {
            subscribers.put( eventType, new ConcurrentHashMap< >( ) );
        }
        Map< Class< ? extends Publisher >, List< Subscriber > > subscribers4Publisher = subscribers.get( eventType );
        if( !subscribers4Publisher.containsKey( publisherType ) ) {
        	subscribers4Publisher.put( publisherType, new CopyOnWriteArrayList< >( ) );
        }
        subscribers4Publisher.get( publisherType ).add( subscriber );
    }

    /**
     * Unsubscribes specified subscriber from specified event and event source
     * 
     * @param eventType the event type
     * @param publisherType the event source type
     * @param subscriber the subscriber to unsubscribe
     */
    public void unsubscribe( 
    	Class< ? extends Event > eventType, 
    	Class< ? extends Publisher > publisherType, 
    	Subscriber subscriber 
    ) {
        List< Subscriber > list = subscribers.get( eventType ).get( publisherType );
        if( list != null ) {
            list.remove( subscriber );
        }
    }
    
    /**
     * Clears all subscribers
     */
    public void clear( ) {
    	subscribers.clear( );
    }
}
