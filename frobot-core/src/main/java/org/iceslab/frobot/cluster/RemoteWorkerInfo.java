package org.iceslab.frobot.cluster;

import java.io.Serializable;

/**
 * worker端的WorkerInfo 用于传输
 */
public class RemoteWorkerInfo implements Serializable {
	private static final long serialVersionUID = -1411930703921280962L;
	/* Worker CPU情况 */
    private int cpu;
    /* Worker 硬盘 情况 */
    private int harddisk;
    /* Worker 内存 情况 */
    private int memory;
    /* Worker 是否空闲 */
    private boolean running;
    /* Worker 是否可用 */
    private boolean available;

    public RemoteWorkerInfo() {
    }

    public RemoteWorkerInfo(int cpu, int harddisk, int memory, boolean running, boolean available) {
        this.cpu = cpu;
        this.harddisk = harddisk;
        this.memory = memory;
        this.running = running;
        this.available = available;
    }

    /************************* 	Getter & Setter	*************************/

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public int getHarddisk() {
        return harddisk;
    }

    public void setHarddisk(int harddisk) {
        this.harddisk = harddisk;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        sb.append("cpu=" + cpu + ", ");
        sb.append("harddisk=" + harddisk + ", ");
        sb.append("memory=" + memory + ", ");
        sb.append("running=" + running + ", ");
        sb.append("available=" + available);
        sb.append("]");
        return sb.toString();
    }
}
