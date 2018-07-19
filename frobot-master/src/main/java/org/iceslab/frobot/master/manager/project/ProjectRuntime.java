package org.iceslab.frobot.master.manager.project;



import org.iceslab.frobot.cluster.TaskInfo;

import java.util.Map;

/**
 * Created by Neuclil on 17-4-2.
 */

public class ProjectRuntime {
    private int workerNum;
    private int memory;
    private int cpu;
    private int hardDisk;
    private Map<TaskInfo, String> taskInfos;

    public static class Builder {
        /* initialized to default values */
        private int workerNum = 1;
        private int memory = 1;
        private int cpu = 1;
        private int hardDisk = 10;
        private Map<TaskInfo, String> taskInfos;

        public Builder() {
        }

        public Builder workerNum(int val) {
            this.workerNum = val;
            return this;
        }

        public Builder memory(int val) {
            this.memory = val;
            return this;
        }

        public Builder cpu(int val) {
            this.cpu = val;
            return this;
        }

        public Builder hardDisk(int val) {
            this.hardDisk = val;
            return this;
        }
        
        public Builder taskInfos(Map<TaskInfo, String> val){
        	this.taskInfos = val;
        	return this;
        }

        public ProjectRuntime build() {
            return new ProjectRuntime(this);
        }
        
    }

    private ProjectRuntime(Builder builder) {
        this.workerNum = builder.workerNum;
        this.memory = builder.memory;
        this.cpu = builder.cpu;
        this.hardDisk = builder.hardDisk;
        this.taskInfos = builder.taskInfos;
    }

    public int getWorkerNum() {
        return workerNum;
    }

    public int getMemory() {
        return memory;
    }

    public int getCpu() {
        return cpu;
    }

    public int getHardDisk() {
        return hardDisk;
    }

	public Map<TaskInfo, String> getTaskInfos() {
		return taskInfos;
	}

    public void setWorkerNum(int workerNum) {
        this.workerNum = workerNum;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public void setHardDisk(int hardDisk) {
        this.hardDisk = hardDisk;
    }

    @Override
    public String toString() {
        return "ProjectRuntime{" +
                "workerNum=" + workerNum +
                ", memory=" + memory +
                ", cpu=" + cpu +
                ", hardDisk=" + hardDisk +
                ", taskInfos=" + taskInfos +
                '}';
    }
}
