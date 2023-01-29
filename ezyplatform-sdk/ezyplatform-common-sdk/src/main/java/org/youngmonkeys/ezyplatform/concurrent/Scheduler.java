/*
 * Copyright 2022 youngmonkeys.org
 * 
 * Licensed under the ezyplatform, Version 1.0.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     https://youngmonkeys.org/licenses/ezyplatform-1.0.0.txt
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.youngmonkeys.ezyplatform.concurrent;

import com.tvd12.ezyfox.concurrent.EzyExecutors;
import com.tvd12.ezyfox.util.EzyLoggable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class Scheduler extends EzyLoggable {

    private final boolean stoppable;
    private final Set<Task> runningTasks;
    private final Map<Runnable, Task> tasks;
    private final ExecutorService executorService;
    private final ScheduledExecutorService inspector;
    private final Map<Object, ScheduledExecutorService> executorServiceByName;

    private static final int DEFAULT_PERIOD_IN_MILLIS = 5;

    public Scheduler(int maxExecutionThread) {
        this(maxExecutionThread, false);
    }

    public Scheduler(
        int maxExecutionThread,
        boolean stoppable
    ) {
        this(
            maxExecutionThread,
            DEFAULT_PERIOD_IN_MILLIS,
            stoppable
        );
    }

    public Scheduler(
        int maxExecutionThread,
        int periodInMillis,
        boolean stoppable
    ) {
        this.stoppable = stoppable;
        this.tasks = new ConcurrentHashMap<>();
        this.runningTasks = ConcurrentHashMap.newKeySet();
        this.executorServiceByName = new ConcurrentHashMap<>();
        this.inspector = EzyExecutors.newSingleThreadScheduledExecutor(
            DefaultThreadFactory.create("scheduler")
        );
        this.executorService = Executors.newFixedThreadPool(
            maxExecutionThread,
            DefaultThreadFactory.create("scheduler-executor")
        );
        List<Task> taskBuffer = new ArrayList<>();
        this.inspector.scheduleAtFixedRate(
            () -> run(taskBuffer),
            periodInMillis,
            periodInMillis,
            TimeUnit.MILLISECONDS
        );
    }

    private void run(List<Task> taskBuffer) {
        taskBuffer.addAll(tasks.values());
        for (Task task : taskBuffer) {
            if (!runningTasks.contains(task)) {
                doRunTask(task);
            }
        }
        taskBuffer.clear();
    }

    private void doRunTask(Task task) {
        try {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= task.nexRunTime.get()) {
                task.calculateNextRunTime();
                runningTasks.add(task);
                executorService.execute(() -> {
                    runTask(task);
                    if (!task.runForever) {
                        tasks.remove(task.command);
                    }
                });
            }
        } catch (Throwable e) {
            logger.warn("run task: {} error", task, e);
        }
    }

    public void scheduleOneTime(
        Runnable command,
        long delayTime,
        TimeUnit unit
    ) {
        this.scheduleAtFixRate(
            command,
            false,
            delayTime,
            0,
            unit
        );
    }

    public void scheduleAtFixRate(
        Runnable command,
        long initialDelay,
        long period,
        TimeUnit unit
    ) {
        this.scheduleAtFixRate(
            command,
            true,
            initialDelay,
            period,
            unit
        );
    }

    private void scheduleAtFixRate(
        Runnable command,
        boolean runForever,
        long initialDelay,
        long period,
        TimeUnit unit
    ) {
        if (initialDelay < 0) {
            throw new IllegalArgumentException("delay time must be >= 0");
        }
        Task task = new Task(
            command,
            runForever,
            initialDelay,
            period,
            unit
        );
        tasks.put(task.command, task);
    }

    public void cancelSchedule(Runnable command) {
        tasks.remove(command);
    }

    public void stop() {
        if (stoppable) {
            this.inspector.shutdown();
            this.executorService.shutdown();
        } else {
            throw new IllegalStateException("can not stop unstoppable scheduler");
        }
    }

    private void runTask(Task task) {
        try {
            task.command.run();
        } catch (Throwable e) {
            logger.warn("scheduler run task error", e);
        } finally {
            runningTasks.remove(task);
        }
    }

    public ScheduledExecutorService getOrCreateSingleThreadScheduledExecutor(
        String name
    ) {
        return executorServiceByName.computeIfAbsent(
            name,
            k -> EzyExecutors.newSingleThreadScheduledExecutor(
                DefaultThreadFactory.create(name)
            )
        );
    }

    public void removeAndShutdownScheduledExecutorService(
        String name
    ) {
        ScheduledExecutorService service = executorServiceByName.remove(
            name
        );
        if (service != null) {
            service.shutdown();
        }
    }

    private static class Task {
        final Runnable command;
        final boolean runForever;
        final long periodMillis;
        final AtomicLong nexRunTime = new AtomicLong();

        Task(
            Runnable command,
            boolean runForever,
            long initialDelay,
            long period,
            TimeUnit unit
        ) {
            this.command = command;
            this.runForever = runForever;
            this.periodMillis = unit.toMillis(period);
            this.nexRunTime.set(System.currentTimeMillis() + unit.toMillis(initialDelay));
        }

        void calculateNextRunTime() {
            this.nexRunTime.addAndGet(periodMillis);
        }
    }
}
