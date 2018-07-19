package org.iceslab.frobot.master.manager.channel;

import org.iceslab.frobot.cluster.NodeType;
import org.iceslab.frobot.remoting.Channel;

/**
 * WorkerInfo 封装了连接上的Worker的信息
 */
public class WorkerInfo {
    /* Woker和Master通信的管道 */
    private Channel channel;
    /* Woker的节点类型 */
    private NodeType nodeType;
    /* WorkerId */
    private String identity;
    /* Worker CPU情况 */
    private int cpu;
    /* Worker 硬盘 情况 */
    private int harddisk;
    /* Worker 内存 情况 */
    private int memory;
    /* Worker 是否空闲 */
    private boolean running;
    /* Task 数目 未使用 */
    private int tasks;
    /* Worker 是否可用 */
    private boolean available;
    /* Worker是否被分配 */
    private boolean allocated;

    public static class Builder {
        private final Channel channel;
        private String identity;
        private NodeType nodeType = NodeType.WORKER;
        private int cpu = 0;
        private int harddisk = 0;
        private int memory = 0;
        private boolean running = false;
        private int tasks = 0;
        private boolean available = false;
        public Builder(Channel channel){
            this.channel = channel;
        }
        public Builder identity(String identity){
            this.identity = identity;
            return this;
        }
        public Builder nodeType(NodeType nodeType){
            this.nodeType = nodeType;
            return this;
        }
        public Builder cpu(int cpu){
            this.cpu = cpu;
            return this;
        }
        public Builder harddisk(int harddisk){
            this.harddisk = harddisk;
            return this;
        }
        public Builder memory(int memory){
            this.memory = memory;
            return this;
        }
        public Builder running(boolean running){
            this.running = running;
            return this;
        }
        public Builder tasks(int tasks){
            this.tasks = tasks;
            return this;
        }
        public Builder available(boolean available){
            this.available = available;
            return this;
        }
        public WorkerInfo build(){
            return new WorkerInfo(this);
        }
    }
    /**
     * 构造函数 私有化 只能通过Builder构造
     */
    private WorkerInfo(Builder builder) {
        this.channel = builder.channel;
        this.identity = builder.identity;
        this.nodeType = builder.nodeType;
        this.cpu = builder.cpu;
        this.harddisk = builder.harddisk;
        this.memory = builder.memory;
        this.running = builder.running;
        this.tasks = builder.tasks;
        this.available = builder.available;
    }

    /************************* 	Getter & Setter	*************************/

    public synchronized Channel getChannel() {
        return channel;
    }

    public synchronized String getIdentity() {
        return identity;
    }

    public synchronized void setIdentity(String identity){
        this.identity = identity;
    }

    public synchronized boolean isOpen() {
        return channel.isOpen();
    }

    public synchronized boolean isClosed() {
        return channel.isClosed();
    }

    public synchronized NodeType getNodeType() {
        return nodeType;
    }

    public synchronized void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public synchronized int getCpu() {
        return cpu;
    }

    public synchronized void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public synchronized int getHarddisk() {
        return harddisk;
    }

    public synchronized void setHarddisk(int harddisk) {
        this.harddisk = harddisk;
    }

    public synchronized int getMemory() {
        return memory;
    }

    public synchronized void setMemory(int memory) {
        this.memory = memory;
    }

    public synchronized int getTasks() {
        return tasks;
    }

    public synchronized void setTasks(int tasks) {
        this.tasks = tasks;
    }

    public synchronized boolean isAvailable() {
        return available;
    }

    public synchronized void setAvailable(boolean available) {
        this.available = available;
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    public synchronized boolean isAllocated(){
        return allocated;
    }

    public synchronized void setAllocated(boolean allocated){
        this.allocated = allocated;
    }

    @Override
    public int hashCode() {
        return identity.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return ((WorkerInfo) obj).identity.equals(this.identity);
    }

    @Override
    public String toString() {
        return "WorkerInfo{" +
                "channel=" + channel +
                ", nodeType=" + nodeType +
                ", identity='" + identity + '\'' +
                ", cpu=" + cpu +
                ", harddisk=" + harddisk +
                ", memory=" + memory +
                ", running=" + running +
                ", tasks=" + tasks +
                ", available=" + available +
                ", allocated=" + allocated +
                '}';
    }
}