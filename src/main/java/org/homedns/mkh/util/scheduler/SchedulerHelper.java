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
import java.util.List;
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
 * Scheduler helper object
 *
 */
public class SchedulerHelper {
	private static final Logger LOG = Logger.getLogger( SchedulerHelper.class );

	public static Scheduler scheduler;
	static {
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler( );
			scheduler.getListenerManager( ).addTriggerListener( new TriggerListener( ) );		
		}
		catch( SchedulerException e ) {
			LOG.error( e.getMessage( ), e );
		}		
	}
	
	/**
	 * Schedules jobs, which are created from specified job templates list
	 * 
	 * @param jobTmps the job templates list
	 * 
	 * @return the trigger key list
	 * 
	 * @throws SchedulerException
	 */
	public static List< TriggerKey > scheduleJobs( List< JobTemplate > jobTmps ) throws SchedulerException {
		List< TriggerKey > triggerKeys = new ArrayList< >( );
		for( JobTemplate tmp : jobTmps ) {
			triggerKeys.add( scheduleJob( tmp ) );
		}
		return( triggerKeys );
	}
	
	/**
	 * Schedules job which is created from specified job template
	 * 
	 * @param jobTmps the job template
	 * 
	 * @return the trigger key
	 * 
	 * @throws SchedulerException
	 */
	public static TriggerKey scheduleJob( JobTemplate jobTmp ) throws SchedulerException {
		Trigger trigger = createTrigger( jobTmp ); 
		scheduler.scheduleJob( createJob( jobTmp ), trigger );
		LOG.info( jobTmp.getName( ) + " is scheduled successfully. Schedule: " + jobTmp.getCronExp( ) );
		return( trigger.getKey( ) );
	}
	
	/**
	 * Unschedules job
	 * 
	 * @param triggerKey the trigger key
	 * 
	 * @throws SchedulerException
	 */
	public static void unscheduleJob( TriggerKey triggerKey ) throws SchedulerException {
		scheduler.unscheduleJob( triggerKey );
	}
	
	/**
	 * Adds jobs to the scheduler for later use, they are created using specified job templates list
	 * 
	 * @param jobTmps the job templates list
	 * 
	 * @return the job key list
	 * 
	 * @throws SchedulerException
	 */
	public static List< JobKey > addJobs( List< JobTemplate > jobTmps ) throws SchedulerException {
		List< JobKey > jobKeys = new ArrayList< >( );
		for( JobTemplate tmp : jobTmps ) {
			jobKeys.add( addJob( tmp ) );
		}	
		return( jobKeys );
	}
	
	/**
	 * Adds job to the scheduler for later use, it is created using specified job template
	 * 
	 * @param jobTmp the job template
	 * 
	 * @return the job key
	 * 
	 * @throws SchedulerException
	 */
	public static JobKey addJob( JobTemplate jobTmp ) throws SchedulerException {
		JobDetail jd = createJob( jobTmp );
		scheduler.addJob( jd, false );
		return( jd.getKey( ) );
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
	private static JobDetail createJob( JobTemplate tmp ) throws SchedulerException {
		JobKey key = generateJobKey( );
		JobBuilder jbuilder = JobBuilder
			.newJob( tmp.getJobClazz( ) )
			.withIdentity( key )
			.usingJobData( "name", tmp.getName( ) );
		jbuilder = ( tmp.getJobData( ) == null ) ? jbuilder : jbuilder.usingJobData( tmp.getJobData( ) );
		return( jbuilder.build( ) );
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
	private static CronTrigger createTrigger( JobTemplate tmp ) throws SchedulerException {
		tmp.isValidCronExp( tmp.getCronExp( ) );
		CronScheduleBuilder csb = CronScheduleBuilder.cronSchedule( tmp.getCronExp( ) );
		CronTrigger trigger = TriggerBuilder
			.newTrigger( )
			.withIdentity( generateTriggerKey( ) )
			.withSchedule( csb )
			.build( );
		return( trigger );
	}

	/**
	 * Generates job key
	 * 
	 * @return the job key
	 */
	private static JobKey generateJobKey( ) {
		String sUID = Util.getGUID( );
		return( JobKey.jobKey( sUID, "group_" + sUID ) );
	}
	
	/**
	 * Generates trigger key
	 * 
	 * @return the trigger key
	 */
	private static TriggerKey generateTriggerKey( ) {
		String sUID = Util.getGUID( );
		return( TriggerKey.triggerKey( sUID, "group_" + sUID ) ); 
	}
	
	private static class TriggerListener extends TriggerListenerSupport {

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
//			LOG.debug( context.getJobDetail( ).getJobDataMap( ).get( "name" ) + " is fired" );
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
					LOG.info( context.getJobDetail( ).getJobDataMap( ).get( "name" ) + ": is unscheduled" );
					scheduler.unscheduleJob( trigger.getKey( ) );
				}
			} 
			catch( Exception e ) {
				LOG.error( e.getMessage( ), e );
			}
		}
	}
}