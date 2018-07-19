package org.iceslab.frobot.master.manager.channel;

import org.iceslab.frobot.master.manager.project.ProjectRuntime;
import org.iceslab.frobot.master.MasterApplication;
import org.iceslab.frobot.remoting.Channel;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The class store the worker information, including identity, channel, cpu, harddisk,
 * memory, and so on. Every connected worker's information will be store here.
 *
 * @auther Neuclil
 */
public class WorkerManager {
    private static final String DEFAULT_WORKER_IDENTITY_PREFIX = "worker-";
    private HashMap<String, WorkerInfo> connectedWorkers;
    private MasterApplication application;
    private static AtomicInteger counter;

    public WorkerManager(MasterApplication application) {
        this.application = application;
    }

    public void start() {
        counter = new AtomicInteger();
        connectedWorkers = new HashMap<String, WorkerInfo>();
    }

    /**
     * Add the worker information to the worker pool. There are two situation that
     * the worker information will be add.
     * <p>
     * 1. the worker first connect to master node, then the master node will
     * allocate identity to the worker node.
     * 2. the worker send heart beat information to the master node, then the
     * worker manager will update the worker information.
     * </p>
     *
     * @param workerInfo the worker information.
     * @return identity about the last added worker.
     */
    public synchronized String add(WorkerInfo workerInfo) {
        String id = workerInfo.getIdentity();
        if (id.equals("noid")) {
            counter.getAndIncrement();
            id = application.getMasterNodeConfig().getIdentity() + "_"
                    + DEFAULT_WORKER_IDENTITY_PREFIX + counter.get();
            workerInfo.setIdentity(id);
        }
        checkIdentity(id);
        connectedWorkers.put(id, workerInfo);
        return id;
    }

    //TODO: check the id's validity
    private void checkIdentity(String id) throws IllegalArgumentException {
    }

    /**
     * remove the worker information from pool by channel.
     *
     * @param channel channel related to worker to be remove
     */
    public synchronized void remove(Channel channel) {
        for (WorkerInfo workerInfo : connectedWorkers.values()) {
            if (workerInfo.getChannel().equals(channel)) {
                connectedWorkers.remove(workerInfo.getIdentity());
                return;
            }
        }
    }

    /**
     * get the worker information by worker's identity.
     *
     * @param id the worker identity
     * @return the worker information stored in worker pool
     */
    public synchronized WorkerInfo get(String id) {
        return connectedWorkers.get(id);
    }

    /**

     * get the worker's number.
     *
     * @return the worker's number in worker pool
     */
    public synchronized int size() {
        return connectedWorkers.size();
    }

    /**
     * @return true if the worker pool is empty, false not empty.
     */
    public synchronized boolean isEmpty() {
        return connectedWorkers.isEmpty();
    }

    /**
     * Get resources for special project runtime. the method will select workers
     * that meet the project runtime requirement and has not be selected.
     *
     * @param projectRuntime project runtime resources.
     * @return {@link List<String>} contains allocated worker's identity.
     */
    public synchronized List<String> requireWorkerResources(ProjectRuntime projectRuntime) {
        List<WorkerInfo> idleWorkers = getIdleWorkers();
        int neededWorkerNum = projectRuntime.getWorkerNum();
        if (idleWorkers.size() < neededWorkerNum)
            return null;
        int count = 0;
        for (WorkerInfo workerInfo : idleWorkers) {
            if (meetRequirements(workerInfo, projectRuntime)) {
                count++;
                if(count >= neededWorkerNum)
                    break;
            }
        }
        if (count < neededWorkerNum)
            return null;
        List<String> allocatedIds = new ArrayList<String>();
        for (WorkerInfo workerInfo : idleWorkers) {
            if (meetRequirements(workerInfo, projectRuntime)) {
                workerInfo.setAllocated(true);
                allocatedIds.add(workerInfo.getIdentity());
            }
        }
        return allocatedIds;
    }

    private boolean meetRequirements(WorkerInfo workerInfo,
                                     ProjectRuntime projectRuntime) {
        return workerInfo.getCpu() > projectRuntime.getCpu()
                && workerInfo.getHarddisk() > projectRuntime.getHardDisk()
                && workerInfo.getMemory() > projectRuntime.getMemory();
    }

    private List<WorkerInfo> getIdleWorkers() {
        List<WorkerInfo> idleWorkers = new ArrayList<WorkerInfo>();
        for (Map.Entry<String, WorkerInfo> entry : connectedWorkers.entrySet()) {
            WorkerInfo workerInfo = entry.getValue();
            if (isIdleWorker(workerInfo)) {
                idleWorkers.add(workerInfo);
            }
        }
        return idleWorkers;
    }

    private boolean isIdleWorker(WorkerInfo workerInfo) {
        return workerInfo.isAvailable() && !workerInfo.isRunning()
                && !workerInfo.isAllocated();
    }

    /**
     * get worker information list according to allocated identity list.
     * @param workerIds
     * @return unmodifiable worker list
     */
    public synchronized List<WorkerInfo> getWorkers(List<String> workerIds) {
        List<WorkerInfo> workers = new ArrayList<WorkerInfo>();
        for (int i = 0; i < workerIds.size(); i++) {
            workers.add(connectedWorkers.get(workerIds.get(i)));
        }
        return Collections.unmodifiableList(workers);
    }

    public String showInfo() {
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<String, WorkerInfo>> entries = connectedWorkers.entrySet();
        Iterator<Map.Entry<String, WorkerInfo>> iterator = entries.iterator();
        sb.append("[ ");
        while (iterator.hasNext()) {
            sb.append("{");
            Map.Entry<String, WorkerInfo> entry = iterator.next();
            sb.append(entry.getKey() + ":" + entry.getValue() + ",");
            sb.append("}");
        }
        sb.append(" ]");
        return sb.toString();
    }
}
