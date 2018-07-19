package org.iceslab.frobot.master.manager.project;

import org.apache.log4j.Logger;
import org.iceslab.frobot.cluster.Project;
import org.iceslab.frobot.commons.utils.db.SQLiteOperation;
import org.iceslab.frobot.master.MasterApplication;
import org.iceslab.frobot.master.ProjectInfo;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Project Scheduler used to scan schedule project in the data base and pick up
 * the project need to be process.
 * @auther Neuclil
 */
public class ProjectScheduler {
	private static final Logger LOGGER = Logger.getLogger(ProjectScheduler.class);
	private ScheduledThreadPoolExecutor timer;
	private MasterApplication application;
	// TODO: change to configurable
	// schedule period in milliseconds
	private long period = 60000;
	private long initPeriod = 30000;

	public ProjectScheduler(MasterApplication application) {
		this.application = application;
		timer = new ScheduledThreadPoolExecutor(1);
	}

	public void start() {
		timer.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				Project waitingProject = application.getProjectManager().getWaitingProject();
				if (waitingProject != null) {
					SQLiteOperation.updateProjectStatus(application.getRootPath(),
							waitingProject.getProjectId(), ProjectInfo.RUNNING);
					LOGGER.info("project scheduler start to process project.");
					new Thread(new Runnable() {
						@Override
						public void run() {
							application.getProjectManager().processProject(waitingProject);
						}
					}).start();
				} else {
					LOGGER.info("there have no waiting project in data base.");
				}
			}
		}, initPeriod, period, TimeUnit.MILLISECONDS);
	}
}
