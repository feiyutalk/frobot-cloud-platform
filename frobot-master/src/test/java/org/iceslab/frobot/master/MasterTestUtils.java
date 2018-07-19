package org.iceslab.frobot.master;

import org.iceslab.frobot.master.manager.channel.WorkerManager;

public class MasterTestUtils {
    private static MasterNodeConfig config;
    private static MasterApplication application = MasterApplication.getInstance();
    private static WorkerManager workerManager;

    public static MasterApplication getApplication(){
        workerManager = new WorkerManager(application);
        workerManager.start();

        config = new MasterNodeConfig.Builder()
                .identity("frobot-master-1")
                .build();

        application.setMasterNodeConfig(config);
        application.setWorkerManager(workerManager);
        return application;
    }
}