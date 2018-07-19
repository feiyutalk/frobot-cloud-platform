package org.iceslab.frobot.registry;



/**
 * Created by Neuclil on 17-4-12.
 */
public class RegistryApplication {
    private RegistryNodeConfig nodeConfig;

    private MasterManager masterManager;

    private static volatile RegistryApplication application;

    private RegistryApplication(){}

    public static RegistryApplication getInstance(){
        if(application == null){
            synchronized (RegistryApplication.class){
                if(application == null){
                    application = new RegistryApplication();
                }
            }
        }
        return application;
    }

    /************************* 	Getter & Setter	*************************/
    public MasterManager getMasterManager() {
        return masterManager;
    }

    public void setMasterManager(MasterManager masterManager) {
        this.masterManager = masterManager;
    }

    public RegistryNodeConfig getNodeConfig() {
        return nodeConfig;
    }

    public void setNodeConfig(RegistryNodeConfig nodeConfig) {
        this.nodeConfig = nodeConfig;
    }
}
