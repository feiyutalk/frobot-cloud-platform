package org.iceslab.frobot.registry;

import org.apache.log4j.Logger;
import org.iceslab.frobot.cluster.NodeConfig;
import org.iceslab.frobot.loadbalance.LoadBalance;
import org.iceslab.frobot.loadbalance.RandomLoadBalance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Neuclil on 17-4-18.
 */
public class MasterManager {
    private static final Logger LOGGER = Logger.getLogger(MasterManager.class);

    private static final String MASTER_IDENTITY_PREFIX = "master-";

    private ConcurrentHashMap<String, NodeConfig> registeredMasters;

    private AtomicInteger counter;

    public MasterManager(){}

    public void start(){
        counter = new AtomicInteger(1);
        registeredMasters = new ConcurrentHashMap<>();
    }

    public String register(String addr, NodeConfig nodeConfig){
        String id = MasterManager.MASTER_IDENTITY_PREFIX + counter.getAndIncrement();
        nodeConfig.setIdentity(id);
        registeredMasters.put(addr, nodeConfig);
        return id;
    }

    public String findMaster(LoadBalance loadBalance, String seed){
        List<String> shards = new ArrayList<String>(registeredMasters.keySet());
        return loadBalance.select(shards,seed);
    }

    public String findMaster(){
        LoadBalance randomLoadBalance = new RandomLoadBalance();
        return findMaster(randomLoadBalance,null);
    }

    public void removeMaster(String failMasterAddr) {
        registeredMasters.remove(failMasterAddr);
    }

    public boolean isEmpty(){
        return registeredMasters.isEmpty();
    }

    /************************* 	Getter & Setter	*************************/
    public static Logger getLOGGER() {
        return LOGGER;
    }

    public ConcurrentHashMap<String, NodeConfig> getRegisteredMasters() {
        return registeredMasters;
    }

    public void setRegisteredMasters(ConcurrentHashMap<String, NodeConfig> registeredMasters) {
        this.registeredMasters = registeredMasters;
    }

    public AtomicInteger getCounter() {
        return counter;
    }

    public void setCounter(AtomicInteger counter) {
        this.counter = counter;
    }
}
