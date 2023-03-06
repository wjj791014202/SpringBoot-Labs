package com.base.batch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BatchExecutor {

    static final int MAX_THREAD_SIZE = 200;
    static final int DEFAULT_EXECUTE_TIME_OUT_IN_SECONDS = 15;

    public BatchExecutor() {
    }

    public static <T, K> List<K> batchListExecute(BatchExecutor.BatchListCommand<T, K> command, List<T> data, int size) {
        return batchListExecute(command, data, size, 15);
    }

    public static <T, K> List<K> batchListExecute(BatchExecutor.BatchListCommand<T, K> command, List<T> data, int size, ExecutorService executor) {
        return batchListExecute(command, data, size, 15, executor);
    }

    public static <T, K> List<K> batchListExecute(BatchExecutor.BatchListCommand<T, K> command, List<T> data, int size, int executeTimeoutInSeconds) {
        return batchListExecute(command, data, size, executeTimeoutInSeconds, (ExecutorService) null);
    }

    public static <T, K> List<K> batchListExecute(BatchExecutor.BatchListCommand<T, K> command, List<T> data, int size, int executeTimeoutInSeconds, ExecutorService executor) {
        List<List<T>> splitDataList = splitBatch(data, size);
        List tasks = (List) splitDataList.stream().map((splitData) -> {
            return new Callable<List<K>>() {
                public List<K> call() throws Exception {
                    return command.execute(splitData);
                }
            };
        }).collect(Collectors.toList());

        try {
            List<K> resultList = new ArrayList();
            int threadSize = splitDataList.size() > 200 ? 200 : splitDataList.size();
            executeTimeoutInSeconds = executeTimeoutInSeconds <= 0 ? 15 : executeTimeoutInSeconds;
            List<K> list=execute(tasks, executor == null ? Executors.newFixedThreadPool(threadSize) : executor, (long) executeTimeoutInSeconds, executor == null);
            resultList.addAll(list);
            return resultList;
        } catch (Exception var9) {
            return new ArrayList();
        }
    }

    public static <T> void batchExecute(BatchExecutor.BatchVoidCommand<T> command, List<T> data, int size) {
        batchExecute(command, data, size, 15L);
    }

    public static <T> void batchExecute(BatchExecutor.BatchVoidCommand<T> command, List<T> data, int size, ExecutorService executor) {
        batchExecute(command, data, size, 15L, executor);
    }

    public static <T> boolean batchExecute(BatchExecutor.BatchVoidCommand<T> command, List<T> data, int size, long executeTimeoutInSeconds) {
        return batchExecute(command, data, size, executeTimeoutInSeconds, (ExecutorService) null);
    }

    public static <T> boolean batchExecute(BatchExecutor.BatchVoidCommand<T> command, List<T> data, int size, long executeTimeoutInSeconds, ExecutorService executor) {
        List<List<T>> splitDataList = splitBatch(data, size);
        List tasks = (List) splitDataList.stream().map((splitData) -> {
            return new Callable<Integer>() {
                public Integer call() throws Exception {
                    command.execute(splitData);
                    return 1;
                }
            };
        }).collect(Collectors.toList());

        try {
            executeTimeoutInSeconds = executeTimeoutInSeconds <= 0L ? 15L : executeTimeoutInSeconds;
            int threadSize = splitDataList.size() > 200 ? 200 : splitDataList.size();
            execute(tasks, executor == null ? Executors.newFixedThreadPool(threadSize) : executor, executeTimeoutInSeconds, executor == null);
            return true;
        } catch (Exception var9) {
            return false;
        }
    }

    public static <T> List<T> execute(Collection<? extends Callable<T>> tasks, ExecutorService executor, long executeTimeout, boolean shutdownExecutor) throws Exception {
        List var5;
        try {
            var5 = (List) executor.invokeAll(tasks).stream().map((future) -> {
                try {
                    return future.get(executeTimeout, TimeUnit.SECONDS);
                } catch (Exception var4) {
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } finally {
            if (shutdownExecutor) {
                executor.shutdown();
            }

        }

        return var5;
    }

    public static <T> List<List<T>> splitBatch(List<T> data, int size) {
        List<List<T>> resultList = new ArrayList();
        int part = data.size() / size;

        for (int i = 0; i < part; ++i) {
            resultList.add(data.subList(i * size, (i + 1) * size));
        }

        if (data.size() % size > 0) {
            resultList.add(data.subList(part * size, data.size()));
        }

        return resultList;
    }

    @FunctionalInterface
    public interface BatchVoidCommand<T> {

        void execute(List<T> data);
    }

    @FunctionalInterface
    public interface BatchListCommand<T, K> {

        List<K> execute(List<T> data);
    }
}

