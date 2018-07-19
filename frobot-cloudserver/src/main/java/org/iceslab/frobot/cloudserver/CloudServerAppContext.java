package org.iceslab.frobot.cloudserver;


import org.iceslab.frobot.commons.io.FileSender;

/**
 * Created by Neuclil on 17-4-10.
 */
public class CloudServerAppContext {
    private CloudServerNodeConfig cloudServerNodeConfig;
    private FileSender fileSender;

    /************************* 	Singleton	*************************/
    private static volatile CloudServerAppContext application;
    private CloudServerAppContext(){}

    public static CloudServerAppContext getInstance(){
        if(application == null){
            synchronized (CloudServerAppContext.class){
                if(application == null){
                    application = new CloudServerAppContext();
                }
            }
        }
        return application;
    }

    /************************* 	Getter & Setter	*************************/

    public CloudServerNodeConfig getCloudServerNodeConfig() {
        return cloudServerNodeConfig;
    }

    public void setCloudServerNodeConfig(CloudServerNodeConfig cloudServerNodeConfig) {
        this.cloudServerNodeConfig = cloudServerNodeConfig;
    }

    public FileSender getFileSender() {
        return fileSender;
    }

    public void setFileSender(FileSender fileSender) {
        this.fileSender = fileSender;
    }

}
