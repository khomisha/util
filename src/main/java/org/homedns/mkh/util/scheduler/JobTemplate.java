/*
 * Copyright 2014-2020 Mikhail Khodonov
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id$
 */

package org.homedns.mkh.util.scheduler;

import java.util.regex.Pattern;

import org.homedns.mkh.util.Util;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;

/**
 * Scheduled job template
 *
 */
public interface JobTemplate {
	public static final Pattern CRON_EXP_PATTERN = Pattern.compile( 
		"^\\s*($|#|\\w+\\s*=|(\\?|\\*|(?:[0-5]?\\d)(?:(?:-|\\/|\\,)(?:[0-5]?\\d))?(?:,(?:[0-5]?\\d)(?:(?:-|\\/|\\,)(?:[0-5]?\\d))?)*)\\s+(\\?|\\*|(?:[0-5]?\\d)(?:(?:-|\\/|\\,)(?:[0-5]?\\d))?(?:,(?:[0-5]?\\d)(?:(?:-|\\/|\\,)(?:[0-5]?\\d))?)*)\\s+(\\?|\\*|(?:[01]?\\d|2[0-3])(?:(?:-|\\/|\\,)(?:[01]?\\d|2[0-3]))?(?:,(?:[01]?\\d|2[0-3])(?:(?:-|\\/|\\,)(?:[01]?\\d|2[0-3]))?)*)\\s+(\\?|\\*|(?:0?[1-9]|[12]\\d|3[01])(?:(?:-|\\/|\\,)(?:0?[1-9]|[12]\\d|3[01]))?(?:,(?:0?[1-9]|[12]\\d|3[01])(?:(?:-|\\/|\\,)(?:0?[1-9]|[12]\\d|3[01]))?)*)\\s+(\\?|\\*|(?:[1-9]|1[012])(?:(?:-|\\/|\\,)(?:[1-9]|1[012]))?(?:L|W)?(?:,(?:[1-9]|1[012])(?:(?:-|\\/|\\,)(?:[1-9]|1[012]))?(?:L|W)?)*|\\?|\\*|(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(?:(?:-)(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?(?:,(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(?:(?:-)(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?)*)\\s+(\\?|\\*|(?:[0-6])(?:(?:-|\\/|\\,|#)(?:[0-6]))?(?:L)?(?:,(?:[0-6])(?:(?:-|\\/|\\,|#)(?:[0-6]))?(?:L)?)*|\\?|\\*|(?:MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-)(?:MON|TUE|WED|THU|FRI|SAT|SUN))?(?:,(?:MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-)(?:MON|TUE|WED|THU|FRI|SAT|SUN))?)*)(|\\s)+(\\?|\\*|(?:|\\d{4})(?:(?:-|\\/|\\,)(?:|\\d{4}))?(?:,(?:|\\d{4})(?:(?:-|\\/|\\,)(?:|\\d{4}))?)*))$" 
	); 

	/**
	 * Returns the job name
	 * 
	 * @return the job name
	 */
	public String getName( );

	/**
	 * Sets the job name

	 * @param sName
	 *            the job name to set
	 */
	public default void setName( String sName ) {
	}

	/**
	 * Returns the cron expression string to base the schedule on
	 * 
	 * @return the cron expression string
	 */
	public String getCronExp( );

	/**
	 * Sets the cron expression string to base the schedule on
	 * 
	 * @param sCronExp
	 *            the cron expression string to set
	 */
	public default void setCronExp( String sCronExp ) {
	}

	/**
	 * Returns the executor class
	 * 
	 * @return the executor class
	 */
	public Class< ? extends Job > getJobClazz( );

	/**
	 * Sets the executor class
	 * 
	 * @param clazz the executor class to set
	 */
	public default void setJobClazz( Class< ? extends Job > clazz ) {
	}

	/**
	 * Returns job data map which holds data available during job execution
	 * 
	 * @return the job data map
	 */
	public default JobDataMap getJobData( ) {
		return( null );
	}
	
	/**
	 * Sets job data
	 * 
	 * @param data the source data map
	 */
	public default void setJobData( JobDataMap jobData ) {	
	}
	
	/**
	 * Returns true if input string is valid cron expression and false otherwise
	 * 
	 * @param s
	 *            the string to test on valid cron expression
	 * 
	 * @return true if the string is a valid cron expression, false otherwise.
	 */
	public default boolean isValidCronExp( String sCron ) throws SchedulerException { 
		if( !Util.isValid( CRON_EXP_PATTERN, sCron ) ) {
			SchedulerException e = new SchedulerException( sCron );
			throw e;			
		}
	  return( true );  
	}
	
	/**
	 * Adds specified data to the job data map 
	 * 
	 * @param sKey the key
	 * @param value the value
	 */
	public default void add2JobData( String sKey, Object value ) {
	}
	
	/**
	 * Sets job detail
	 * 
	 * @param jobDetail the 
	 */
	public default void setJobDetail( JobDetail jobDetail ) {
	}
	
	/**
	 * Returns job detail
	 * 
	 * @return the job detail
	 */
	public default JobDetail getJobDetail( ) {
		return( null );
	}
}
