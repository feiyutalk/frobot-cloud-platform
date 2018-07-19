package org.iceslab.frobot.remoting.command.body.request;

import org.iceslab.frobot.cluster.RemoteWorkerInfo;
import org.iceslab.frobot.remoting.command.body.AbstractRemotingCommandBody;

public class HeartBeatRequestBody extends AbstractRemotingCommandBody {
    private static final long serialVersionUID = 8442764611832226224L;
    private int cpu;
    /* Worker 硬盘 情况 */
    private int harddisk;
    /* Worker 内存 情况 */
    private int memory;
    /* Worker 是否空闲 */
    private boolean running;
    /* Worker 是否可用 */
    private boolean available;

    public HeartBeatRequestBody() {
    }

    public HeartBeatRequestBody(RemoteWorkerInfo remoteWorkerInfo) {
        setWorkerInfo(remoteWorkerInfo);
    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getCpu() {
        return cpu;
    }

    public int getHarddisk() {
        return harddisk;
    }

    public int getMemory() {
        return memory;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setWorkerInfo(RemoteWorkerInfo info){
        setCpu(info.getCpu()).setMemory(info.getMemory())
                .setHarddisk(info.getHarddisk())
                .setAvailable(info.isAvailable())
                .setRunning(info.isRunning());
    }

    public HeartBeatRequestBody setCpu(int cpu) {
        this.cpu = cpu;
        return this;
    }

    public HeartBeatRequestBody setHarddisk(int harddisk) {
        this.harddisk = harddisk;
        return this;
    }

    public HeartBeatRequestBody setMemory(int memory) {
        this.memory = memory;
        return this;
    }

    public HeartBeatRequestBody setRunning(boolean running) {
        this.running = running;
        return this;
    }

    public HeartBeatRequestBody setAvailable(boolean available) {
        this.available = available;
        return this;
    }

    @Override
    public void checkFields() throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public String toString() {
        return "HeartBeatRequestBody{" +
                "cpu=" + cpu +
                ", harddisk=" + harddisk +
                ", memory=" + memory +
                ", running=" + running +
                ", available=" + available +
                "} " + super.toString();
    }
}