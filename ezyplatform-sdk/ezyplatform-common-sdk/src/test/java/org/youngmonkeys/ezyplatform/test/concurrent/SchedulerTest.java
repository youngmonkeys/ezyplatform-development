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

package org.youngmonkeys.ezyplatform.test.concurrent;

import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyfox.util.EzyThreads;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.FieldUtil;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.concurrent.Scheduler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class SchedulerTest {

    @Test
    public void scheduleOneTest() {
        // given
        Runnable command = mock(Runnable.class);
        Scheduler scheduler = new Scheduler(1);

        // when
        scheduler.scheduleOneTime(command, 3, TimeUnit.MILLISECONDS);
        EzyThreads.sleep(100);

        // then
        Map<Runnable, Object> tasks = FieldUtil.getFieldValue(
            scheduler,
            "tasks"
        );

        Asserts.assertEmpty(tasks);
        Set<Object> runningTasks = FieldUtil.getFieldValue(
            scheduler,
            "runningTasks"
        );
        Asserts.assertEmpty(runningTasks);
        verify(command, times(1)).run();
    }

    @Test
    public void scheduleOneWithExceptionTest() {
        // given
        Runnable command = mock(Runnable.class);
        RuntimeException exception = new RuntimeException("just test");
        doThrow(exception).when(command).run();
        Scheduler scheduler = new Scheduler(1);

        // when
        scheduler.scheduleOneTime(command, 3, TimeUnit.MILLISECONDS);
        EzyThreads.sleep(100);

        // then
        Map<Runnable, Object> tasks = FieldUtil.getFieldValue(
            scheduler,
            "tasks"
        );

        Asserts.assertEmpty(tasks);
        Set<Object> runningTasks = FieldUtil.getFieldValue(
            scheduler,
            "runningTasks"
        );
        Asserts.assertEmpty(runningTasks);
        verify(command, times(1)).run();
    }

    @Test
    public void scheduleAtFixRateTest() {
        // given
        Runnable command1 = mock(Runnable.class);
        Runnable command2 = mock(Runnable.class);
        Scheduler scheduler = new Scheduler(2, true);

        // when
        scheduler.scheduleAtFixRate(command1, 0, 3, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixRate(command2, 0, 3, TimeUnit.MILLISECONDS);
        EzyThreads.sleep(100);

        // then
        Map<Runnable, Object> tasks = FieldUtil.getFieldValue(
            scheduler,
            "tasks"
        );

        Asserts.assertEquals(
            tasks.keySet(),
            Sets.newHashSet(command1, command2),
            false
        );
        Set<Object> runningTasks = FieldUtil.getFieldValue(
            scheduler,
            "runningTasks"
        );
        Asserts.assertNotNull(runningTasks);
        verify(command1, atLeast(1)).run();
        verify(command2, atLeast(1)).run();
        scheduler.stop();
    }

    @Test
    public void scheduleAtFixRateFailedDueToInitialDelay() {
        // given
        Runnable command1 = mock(Runnable.class);
        Scheduler scheduler = new Scheduler(1, true);

        // when
        Throwable e = Asserts.assertThrows(() ->
            scheduler.scheduleAtFixRate(
                command1,
                -1,
                3,
                TimeUnit.MILLISECONDS
            )
        );

        // then
        Asserts.assertEqualsType(e, IllegalArgumentException.class);
        Map<Runnable, Object> tasks = FieldUtil.getFieldValue(
            scheduler,
            "tasks"
        );
        Asserts.assertEmpty(tasks);

        Set<Object> runningTasks = FieldUtil.getFieldValue(
            scheduler,
            "runningTasks"
        );
        Asserts.assertNotNull(runningTasks);
        verify(command1, atLeast(0)).run();
        scheduler.stop();
    }

    @Test
    public void cancelScheduleTest() {
        // given
        Runnable command1 = mock(Runnable.class);
        Runnable command2 = mock(Runnable.class);
        Scheduler scheduler = new Scheduler(2, true);

        // when
        scheduler.scheduleAtFixRate(command1, 1000, 3, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixRate(command2, 1000, 3, TimeUnit.MILLISECONDS);
        EzyThreads.sleep(100);
        scheduler.cancelSchedule(command1);
        scheduler.cancelSchedule(command2);

        // then
        Map<Runnable, Object> tasks = FieldUtil.getFieldValue(
            scheduler,
            "tasks"
        );

        Asserts.assertEmpty(tasks);
        Set<Object> runningTasks = FieldUtil.getFieldValue(
            scheduler,
            "runningTasks"
        );
        Asserts.assertEmpty(runningTasks);
        verify(command1, atLeast(0)).run();
        verify(command2, atLeast(0)).run();
        scheduler.stop();
    }

    @Test
    public void stopFailedDueToUnstoppable() {
        // given
        Scheduler scheduler = new Scheduler(1);

        // when
        Throwable e = Asserts.assertThrows(scheduler::stop);

        // then
        Asserts.assertEqualsType(e, IllegalStateException.class);
        ScheduledExecutorService inspector = FieldUtil.getFieldValue(
            scheduler,
            "inspector"
        );
        inspector.shutdownNow();
        ExecutorService executorService = FieldUtil.getFieldValue(
            scheduler,
            "executorService"
        );
        executorService.shutdownNow();
    }

    @Test
    public void doRunTaskTest() {
        // given
        Runnable command1 = mock(Runnable.class);
        Runnable command2 = mock(Runnable.class);
        Scheduler scheduler = new Scheduler(2, true);

        ExecutorService executorService = mock(ExecutorService.class);
        RuntimeException exception = new RuntimeException("test");
        doThrow(exception).when(executorService).execute(any());
        FieldUtil.setFieldValue(scheduler, "executorService", executorService);

        // when
        scheduler.scheduleAtFixRate(command1, 0, 3, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixRate(command2, 0, 3, TimeUnit.MILLISECONDS);
        EzyThreads.sleep(10);

        // then
        Map<Runnable, Object> tasks = FieldUtil.getFieldValue(
            scheduler,
            "tasks"
        );

        Asserts.assertEquals(
            tasks.keySet(),
            Sets.newHashSet(command1, command2),
            false
        );
        Set<Object> runningTasks = FieldUtil.getFieldValue(
            scheduler,
            "runningTasks"
        );
        Asserts.assertNotNull(runningTasks);
        verify(command1, atLeast(0)).run();
        verify(command2, atLeast(0)).run();
        verify(executorService, atLeast(1)).execute(any());
        scheduler.stop();
    }
}
