package org.iceslab.frobot.loadbalance;

import java.util.List;

/**
 * Created by Neuclil on 17-4-18.
 */
public interface LoadBalance {
    public <S> S select(List<S> shards, String seed);
}
