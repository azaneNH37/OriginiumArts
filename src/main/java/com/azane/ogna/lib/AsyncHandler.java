package com.azane.ogna.lib;

import lombok.Getter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AsyncHandler
{
    @Getter
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    public static void delayExecute(long delayMillis,Runnable task)
    {
        CompletableFuture.delayedExecutor(delayMillis, TimeUnit.MILLISECONDS,executor).execute(task);
    }
}
