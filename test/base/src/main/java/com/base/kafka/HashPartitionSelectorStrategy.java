package com.base.kafka;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.cloud.stream.binder.PartitionSelectorStrategy;

public class HashPartitionSelectorStrategy implements PartitionSelectorStrategy {
    public HashPartitionSelectorStrategy() {
    }

    public int selectPartition(Object key, int partitionCount) {
        if (key == null) {
            return ThreadLocalRandom.current().nextInt(2147483647);
        } else {
            int hashCode = key instanceof byte[] ? Arrays.hashCode((byte[])((byte[])key)) : key.hashCode();
            if (hashCode == -2147483648) {
                hashCode = 0;
            }

            return hashCode;
        }
    }
}
