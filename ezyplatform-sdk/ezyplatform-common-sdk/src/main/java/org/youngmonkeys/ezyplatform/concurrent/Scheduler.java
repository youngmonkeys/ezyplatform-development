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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class Scheduler extends EzyLoggable {

    private final List<Task> tasks;
    private final Set<Task> runningTasks;
    private final ExecutorService executorService;
    private final ScheduledExecutorService inspector;

    public Scheduler(int maxExecutionThread) {
        this.tasks = Collections.synchronizedList(
            new ArrayList<>()
        );
        this.runningTasks = ConcurrentHashMap.newKeySet();
        this.inspector = EzyExecutors.newSingleThreadScheduledExecutor(
            DefaultThreadFactory.create("scheduler")
        );
        this.executorService = Executors.newFixedThreadPool(
            maxExecutionThread,
            DefaultThreadFactory.create("scheduler-executor")
        );
        this.inspector.scheduleAtFixedRate(
            this::run,
            5,
            5,
            TimeUnit.MILLISECONDS
        );
    }

    private void run() {
        for (Task task : tasks) {
            if (!runningTasks.contains(task)) {
                long currentTime = System.currentTimeMillis();
                if (currentTime >= task.nexRunTime.get()) {
                    task.calculateNextRunTime();
                    runningTasks.add(task);
                    executorService.execute(() -> runTask(task));
                }
            }
        }
    }

    public void scheduleAtFixRate(
        Runnable command,
        long initialDelay,
        long period,
        TimeUnit unit
    ) {
        Task task = new Task(command, initialDelay, period, unit);
        tasks.add(task);
        if (initialDelay <= 0) {
            runningTasks.add(task);
            executorService.execute(() -> runTask(task));
        }
    }

    public void stop() {
        this.inspector.shutdown();
        this.executorService.shutdown();
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

    private static class Task {
        final Runnable command;
        final long periodMillis;
        final AtomicLong nexRunTime = new AtomicLong();

        Task(
            Runnable command,
            long initialDelay,
            long period,
            TimeUnit unit
        ) {
            this.command = command;
            this.periodMillis = unit.toMillis(period);
            this.nexRunTime.set(System.currentTimeMillis() + unit.toMillis(initialDelay));
        }

        void calculateNextRunTime() {
            this.nexRunTime.addAndGet(periodMillis);
        }
    }
}
