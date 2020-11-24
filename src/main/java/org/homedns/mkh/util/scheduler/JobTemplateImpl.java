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

import org.quartz.Job;
import org.quartz.JobDataMap;

/**
 * Implements @see org.homedns.mkh.ledgerpipe.JobTemplate
 * 
 * @author Mikhail Khodonov
 *
 */
public class JobTemplateImpl implements JobTemplate {
	private String sName;
	private String sCronExp;
	private Class< ? extends Job > jobClazz;
	private JobDataMap jobData;

	/**
	 * @param sName the name
	 * @param sCronExp the cron expression
	 * @param jobClazz the job class
	 */
	public JobTemplateImpl( String sName, String sCronExp, Class< ? extends Job > jobClazz ) {
		this.sName = sName;
		this.sCronExp = sCronExp;
		this.jobClazz = jobClazz;
	}

	/**
	 * @param sName the name
	 * @param sCronExp the cron expression
	 * @param jobClazz the job class
	 * @param jobData the input job data store 
	 */
	public JobTemplateImpl( String sName, String sCronExp, Class< ? extends Job > jobClazz, JobDataMap jobData ) {
		this( sName, sCronExp, jobClazz );
		this.jobData = jobData;
	}

	/**
	 * @see org.homedns.mkh.util.scheduler.JobTemplate#getName()
	 */
	@Override
	public String getName( ) {
		return( sName );
	}

	/**
	 * @see org.homedns.mkh.util.scheduler.JobTemplate#getCronExp()
	 */
	@Override
	public String getCronExp( ) {
		return( sCronExp );
	}

	/**
	 * @see org.homedns.mkh.util.scheduler.JobTemplate#getJobClazz()
	 */
	@Override
	public Class< ? extends Job > getJobClazz( ) {
		return( jobClazz );
	}

	/**
	 * @see org.homedns.mkh.util.scheduler.JobTemplate#getJobData()
	 */
	@Override
	public JobDataMap getJobData( ) {
		return( jobData );
	}
	
	/**
	 * @see org.homedns.mkh.util.scheduler.JobTemplate#add2JobData(java.lang.String, java.lang.Object)
	 */
	public void add2JobData( String sKey, Object value ) {
		if( jobData == null ) {
			jobData = new JobDataMap( );
		}
		jobData.put( sKey, value );
	}
}
