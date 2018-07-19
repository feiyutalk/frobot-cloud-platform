package org.iceslab.frobot.loadbalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Neuclil on 17-4-18.
 */
public class RandomLoadBalance extends AbstractLoadBalance{

    @Override
    protected <S> S doSelect(List<S> shards, String seed) {
        return shards.get(ThreadLocalRandom.current().nextInt(shards.size()));
    }
}
