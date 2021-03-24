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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.homedns.mkh.util.Util;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.listeners.TriggerListenerSupport;

/**
 * Job scheduler object
 *
 */
public class JobScheduler {
	private static final Logger LOG = Logger.getLogger( JobScheduler.class );

	private static Map< JobKey, String > jobsMap = new HashMap< JobKey, String >( );
	public static Scheduler scheduler;
	
	public JobScheduler( ) throws SchedulerException {
		scheduler = StdSchedulerFactory.getDefaultScheduler( );		
		scheduler.getListenerManager( ).addTriggerListener( new TriggerListener( ) );
	}
	
	/**
	 * @param jobs the job templates list
	 * 
	 * @throws SchedulerException 
	 */
	public JobScheduler( List< JobTemplate > jobTmps ) throws SchedulerException {
		this( );
		scheduleJobs( jobTmps );
	}

	/**
	 * Schedules jobs, which created from specified job templates list
	 * 
	 * @param jobTmps the job templates list
	 * 
	 * @throws SchedulerException
	 */
	public void scheduleJobs( List< JobTemplate > jobTmps ) throws SchedulerException {
		for( JobTemplate tmp : jobTmps ) {
			scheduler.scheduleJob( createJob( tmp ), createTrigger( tmp ) );
			LOG.info( tmp.getName( ) + " is scheduled successfully. Schedule: " + tmp.getCronExp( ) );
		}
	}
	
	/**
	 * Adds jobs for later use, which created specified job templates list
	 * 
	 * @param jobTmps the job templates list
	 * 
	 * @return the added jobs keys
	 * 
	 * @throws SchedulerException
	 */
	public List< JobKey > addJobs( List< JobTemplate > jobTmps ) throws SchedulerException {
		List< JobKey > jobKeys = new ArrayList< >( );
		for( JobTemplate tmp : jobTmps ) {
			JobDetail jd = createJob( tmp );
			scheduler.addJob( jd, false );
			jobKeys.add( jd.getKey( ) );
		}	
		return( jobKeys );
	}
	
	/**
	 * Creates job using specified job template
	 * 
	 * @param tmp the job template
	 * 
	 * @return the job
	 * 
	 * @throws SchedulerException
	 */
	private JobDetail createJob( JobTemplate tmp ) throws SchedulerException {
		JobKey key = generateJobKey( );
		JobBuilder jbuilder = JobBuilder
			.newJob( tmp.getJobClazz( ) )
			.withIdentity( key )
			.usingJobData( "name", tmp.getName( ) );
		jbuilder = ( tmp.getJobData( ) == null ) ? jbuilder : jbuilder.usingJobData( tmp.getJobData( ) );
		JobDetail job = jbuilder.build( );
		jobsMap.put( key, tmp.getName( ) );
		return( job );
	}
	
	/**
	 * Creates trigger using specified job template
	 * 
	 * @param tmp the job template
	 * 
	 * @return the trigger
	 * 
	 * @throws SchedulerException
	 */
	private CronTrigger createTrigger( JobTemplate tmp ) throws SchedulerException {
		tmp.isValidCronExp( tmp.getCronExp( ) );
		CronScheduleBuilder csb = CronScheduleBuilder.cronSchedule( tmp.getCronExp( ) );
		CronTrigger trigger = (
			TriggerBuilder.newTrigger( )
				.withIdentity( generateTriggerKey( ) )
				.withSchedule( csb )
				.build( )
		);
		return( trigger );
	}
	
	/**
	 * Returns job's name by it's key
	 * 
	 * @param jobKey
	 *            the job key
	 * 
	 * @return the job name
	 */
	public static String getJobName( JobKey jobKey ) {
		return( jobsMap.get( jobKey ) );
	}
	
	/**
	 * Returns jobs pool empty flag
	 * 
	 * @return true if jobs pool is empty and false otherwise
	 */
	public static boolean isJobPoolEmpty( ) {
		return( jobsMap.isEmpty( ) );
	}
		
	/**
	 * Generates job key
	 * 
	 * @return the job key
	 */
	private JobKey generateJobKey( ) {
		String sUID = Util.getGUID( );
		return( JobKey.jobKey( sUID, "group_" + sUID ) );
	}
	
	/**
	 * Generates trigger key
	 * 
	 * @return the trigger key
	 */
	private TriggerKey generateTriggerKey( ) {
		String sUID = Util.getGUID( );
		return( TriggerKey.triggerKey( sUID, "group_" + sUID ) ); 
	}
	
	private class TriggerListener extends TriggerListenerSupport {

		/**
		 * @see org.quartz.TriggerListener#getName()
		 */
		@Override
		public String getName( ) {
			return( "xxx" );
		}

		/**
		 * @see org.quartz.listeners.TriggerListenerSupport#triggerFired(org.quartz.Trigger, org.quartz.JobExecutionContext)
		 */
		@Override
		public void triggerFired( Trigger trigger, JobExecutionContext context ) {
//			LOG.debug( getJobName( trigger.getJobKey( ) ) + " is fired" );
		}

		/**
		 * @see org.quartz.listeners.TriggerListenerSupport#triggerComplete(org.quartz.Trigger, org.quartz.JobExecutionContext, org.quartz.Trigger.CompletedExecutionInstruction)
		 */
	    @Override
		public void triggerComplete( 
			Trigger trigger,
			JobExecutionContext context,
			CompletedExecutionInstruction triggerInstructionCode 
		) {
			try {
				if( triggerInstructionCode == CompletedExecutionInstruction.SET_TRIGGER_COMPLETE ) {
					JobKey jobKey = trigger.getJobKey( );
					LOG.info( getJobName( jobKey ) + ": is unscheduled" );
					jobsMap.remove( jobKey );
					scheduler.unscheduleJob( trigger.getKey( ) );
					if( isJobPoolEmpty( ) ) {
						LOG.info( "Jobs pool is empty." );
					}
				}
			} 
			catch( Exception e ) {
				LOG.error( e.getMessage( ), e );
			}
		}
	}
}