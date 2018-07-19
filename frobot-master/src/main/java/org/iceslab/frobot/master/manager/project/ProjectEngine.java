package org.iceslab.frobot.master.manager.project;

import java.util.List;
import java.util.Map;

import org.iceslab.frobot.cluster.Project;
import org.iceslab.frobot.commons.exception.XMLFileNotMatchException;
import org.iceslab.frobot.master.MasterApplication;
import org.iceslab.frobot.master.ProjectInfo;
import org.iceslab.frobot.master.DefaultTaskEngineInfo;
import org.iceslab.frobot.master.manager.channel.WorkerInfo;
import org.iceslab.frobot.master.workflow.WorkFlow;

/**
 * Project Engine Interface. Every project engine should implements it.
 * @auther Neuclil
 */
public interface ProjectEngine {

    ProjectRuntime init(Project project, Map<String,DefaultTaskEngineInfo> taskEngines) throws XMLFileNotMatchException;

    void start(WorkFlow workFlow, Map<String,List<WorkerInfo>> workers, MasterApplication application)
            throws ClassNotFoundException;

    void stop();

    void pause();

    void resume();
}