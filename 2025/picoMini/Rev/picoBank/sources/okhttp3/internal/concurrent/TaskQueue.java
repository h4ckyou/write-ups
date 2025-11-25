package okhttp3.internal.concurrent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal.Util;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010!\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u00002\u00020\u0001:\u00013B\u0017\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0006\u0010!\u001a\u00020\"J\r\u0010#\u001a\u00020\u000eH\u0000¢\u0006\u0002\b$J8\u0010%\u001a\u00020\"2\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010&\u001a\u00020'2\b\b\u0002\u0010(\u001a\u00020\u000e2\u000e\b\u0004\u0010)\u001a\b\u0012\u0004\u0012\u00020\"0*H\bø\u0001\u0000J\u0006\u0010+\u001a\u00020,J.\u0010-\u001a\u00020\"2\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010&\u001a\u00020'2\u000e\b\u0004\u0010)\u001a\b\u0012\u0004\u0012\u00020'0*H\bø\u0001\u0000J\u0018\u0010-\u001a\u00020\"2\u0006\u0010.\u001a\u00020\b2\b\b\u0002\u0010&\u001a\u00020'J%\u0010/\u001a\u00020\u000e2\u0006\u0010.\u001a\u00020\b2\u0006\u0010&\u001a\u00020'2\u0006\u00100\u001a\u00020\u000eH\u0000¢\u0006\u0002\b1J\u0006\u0010\u001c\u001a\u00020\"J\b\u00102\u001a\u00020\u0005H\u0016R\u001c\u0010\u0007\u001a\u0004\u0018\u00010\bX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001a\u0010\r\u001a\u00020\u000eX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001a\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\b0\u0014X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0014\u0010\u0004\u001a\u00020\u0005X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0017\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\b0\u001a8F¢\u0006\u0006\u001a\u0004\b\u001b\u0010\u0016R\u001a\u0010\u001c\u001a\u00020\u000eX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u0010\"\u0004\b\u001e\u0010\u0012R\u0014\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 \u0002\u0007\n\u0005\b20\u0001¨\u00064"}, d2 = {"Lokhttp3/internal/concurrent/TaskQueue;", "", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "name", "", "(Lokhttp3/internal/concurrent/TaskRunner;Ljava/lang/String;)V", "activeTask", "Lokhttp3/internal/concurrent/Task;", "getActiveTask$okhttp", "()Lokhttp3/internal/concurrent/Task;", "setActiveTask$okhttp", "(Lokhttp3/internal/concurrent/Task;)V", "cancelActiveTask", "", "getCancelActiveTask$okhttp", "()Z", "setCancelActiveTask$okhttp", "(Z)V", "futureTasks", "", "getFutureTasks$okhttp", "()Ljava/util/List;", "getName$okhttp", "()Ljava/lang/String;", "scheduledTasks", "", "getScheduledTasks", "shutdown", "getShutdown$okhttp", "setShutdown$okhttp", "getTaskRunner$okhttp", "()Lokhttp3/internal/concurrent/TaskRunner;", "cancelAll", "", "cancelAllAndDecide", "cancelAllAndDecide$okhttp", "execute", "delayNanos", "", "cancelable", "block", "Lkotlin/Function0;", "idleLatch", "Ljava/util/concurrent/CountDownLatch;", "schedule", "task", "scheduleAndDecide", "recurrence", "scheduleAndDecide$okhttp", "toString", "AwaitIdleTask", "okhttp"}, k = 1, mv = {1, 4, 0})
/* compiled from: TaskQueue.kt */
public final class TaskQueue {
    private Task activeTask;
    private boolean cancelActiveTask;
    private final List<Task> futureTasks = new ArrayList();
    private final String name;
    private boolean shutdown;
    private final TaskRunner taskRunner;

    public TaskQueue(TaskRunner taskRunner2, String name2) {
        Intrinsics.checkNotNullParameter(taskRunner2, "taskRunner");
        Intrinsics.checkNotNullParameter(name2, "name");
        this.taskRunner = taskRunner2;
        this.name = name2;
    }

    public final TaskRunner getTaskRunner$okhttp() {
        return this.taskRunner;
    }

    public final String getName$okhttp() {
        return this.name;
    }

    public final boolean getShutdown$okhttp() {
        return this.shutdown;
    }

    public final void setShutdown$okhttp(boolean z) {
        this.shutdown = z;
    }

    public final Task getActiveTask$okhttp() {
        return this.activeTask;
    }

    public final void setActiveTask$okhttp(Task task) {
        this.activeTask = task;
    }

    public final List<Task> getFutureTasks$okhttp() {
        return this.futureTasks;
    }

    public final boolean getCancelActiveTask$okhttp() {
        return this.cancelActiveTask;
    }

    public final void setCancelActiveTask$okhttp(boolean z) {
        this.cancelActiveTask = z;
    }

    public final List<Task> getScheduledTasks() {
        List<Task> list;
        synchronized (this.taskRunner) {
            list = CollectionsKt.toList(this.futureTasks);
        }
        return list;
    }

    public static /* synthetic */ void schedule$default(TaskQueue taskQueue, Task task, long j, int i, Object obj) {
        if ((i & 2) != 0) {
            j = 0;
        }
        taskQueue.schedule(task, j);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002e, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void schedule(okhttp3.internal.concurrent.Task r8, long r9) {
        /*
            r7 = this;
            java.lang.String r0 = "task"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r8, r0)
            okhttp3.internal.concurrent.TaskRunner r0 = r7.taskRunner
            monitor-enter(r0)
            r1 = 0
            boolean r2 = r7.shutdown     // Catch:{ all -> 0x0060 }
            if (r2 == 0) goto L_0x0050
            boolean r2 = r8.getCancelable()     // Catch:{ all -> 0x0060 }
            if (r2 == 0) goto L_0x002f
            r2 = r7
            r3 = r8
            r4 = 0
            okhttp3.internal.concurrent.TaskRunner$Companion r5 = okhttp3.internal.concurrent.TaskRunner.Companion     // Catch:{ all -> 0x0060 }
            java.util.logging.Logger r5 = r5.getLogger()     // Catch:{ all -> 0x0060 }
            java.util.logging.Level r6 = java.util.logging.Level.FINE     // Catch:{ all -> 0x0060 }
            boolean r5 = r5.isLoggable(r6)     // Catch:{ all -> 0x0060 }
            if (r5 == 0) goto L_0x002c
            r5 = 0
            java.lang.String r6 = "schedule canceled (queue is shutdown)"
            okhttp3.internal.concurrent.TaskLoggerKt.log(r3, r2, r6)     // Catch:{ all -> 0x0060 }
        L_0x002c:
            monitor-exit(r0)
            return
        L_0x002f:
            r2 = r7
            r3 = r8
            r4 = 0
            okhttp3.internal.concurrent.TaskRunner$Companion r5 = okhttp3.internal.concurrent.TaskRunner.Companion     // Catch:{ all -> 0x0060 }
            java.util.logging.Logger r5 = r5.getLogger()     // Catch:{ all -> 0x0060 }
            java.util.logging.Level r6 = java.util.logging.Level.FINE     // Catch:{ all -> 0x0060 }
            boolean r5 = r5.isLoggable(r6)     // Catch:{ all -> 0x0060 }
            if (r5 == 0) goto L_0x0047
            r5 = 0
            java.lang.String r6 = "schedule failed (queue is shutdown)"
            okhttp3.internal.concurrent.TaskLoggerKt.log(r3, r2, r6)     // Catch:{ all -> 0x0060 }
        L_0x0047:
            java.util.concurrent.RejectedExecutionException r2 = new java.util.concurrent.RejectedExecutionException     // Catch:{ all -> 0x0060 }
            r2.<init>()     // Catch:{ all -> 0x0060 }
            java.lang.Throwable r2 = (java.lang.Throwable) r2     // Catch:{ all -> 0x0060 }
            throw r2     // Catch:{ all -> 0x0060 }
        L_0x0050:
            r2 = 0
            boolean r2 = r7.scheduleAndDecide$okhttp(r8, r9, r2)     // Catch:{ all -> 0x0060 }
            if (r2 == 0) goto L_0x005c
            okhttp3.internal.concurrent.TaskRunner r2 = r7.taskRunner     // Catch:{ all -> 0x0060 }
            r2.kickCoordinator$okhttp(r7)     // Catch:{ all -> 0x0060 }
        L_0x005c:
            kotlin.Unit r1 = kotlin.Unit.INSTANCE     // Catch:{ all -> 0x0060 }
            monitor-exit(r0)
            return
        L_0x0060:
            r1 = move-exception
            monitor-exit(r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.concurrent.TaskQueue.schedule(okhttp3.internal.concurrent.Task, long):void");
    }

    public static /* synthetic */ void schedule$default(TaskQueue taskQueue, String name2, long delayNanos, Function0 block, int i, Object obj) {
        if ((i & 2) != 0) {
            delayNanos = 0;
        }
        Intrinsics.checkNotNullParameter(name2, "name");
        Intrinsics.checkNotNullParameter(block, "block");
        taskQueue.schedule(new TaskQueue$schedule$2(block, name2, name2), delayNanos);
    }

    public final void schedule(String name2, long delayNanos, Function0<Long> block) {
        Intrinsics.checkNotNullParameter(name2, "name");
        Intrinsics.checkNotNullParameter(block, "block");
        schedule(new TaskQueue$schedule$2(block, name2, name2), delayNanos);
    }

    public static /* synthetic */ void execute$default(TaskQueue taskQueue, String name2, long delayNanos, boolean cancelable, Function0 block, int i, Object obj) {
        if ((i & 2) != 0) {
            delayNanos = 0;
        }
        if ((i & 4) != 0) {
            cancelable = true;
        }
        Intrinsics.checkNotNullParameter(name2, "name");
        Intrinsics.checkNotNullParameter(block, "block");
        taskQueue.schedule(new TaskQueue$execute$1(block, name2, cancelable, name2, cancelable), delayNanos);
    }

    public final void execute(String name2, long delayNanos, boolean cancelable, Function0<Unit> block) {
        Intrinsics.checkNotNullParameter(name2, "name");
        Intrinsics.checkNotNullParameter(block, "block");
        schedule(new TaskQueue$execute$1(block, name2, cancelable, name2, cancelable), delayNanos);
    }

    public final CountDownLatch idleLatch() {
        synchronized (this.taskRunner) {
            if (this.activeTask != null || !this.futureTasks.isEmpty()) {
                Task existingTask = this.activeTask;
                if (existingTask instanceof AwaitIdleTask) {
                    CountDownLatch latch = ((AwaitIdleTask) existingTask).getLatch();
                    return latch;
                }
                for (Task futureTask : this.futureTasks) {
                    if (futureTask instanceof AwaitIdleTask) {
                        CountDownLatch latch2 = ((AwaitIdleTask) futureTask).getLatch();
                        return latch2;
                    }
                }
                AwaitIdleTask newTask = new AwaitIdleTask();
                if (scheduleAndDecide$okhttp(newTask, 0, false)) {
                    this.taskRunner.kickCoordinator$okhttp(this);
                }
                CountDownLatch latch3 = newTask.getLatch();
                return latch3;
            }
            CountDownLatch countDownLatch = new CountDownLatch(0);
            return countDownLatch;
        }
    }

    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0007\u001a\u00020\bH\u0016R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\t"}, d2 = {"Lokhttp3/internal/concurrent/TaskQueue$AwaitIdleTask;", "Lokhttp3/internal/concurrent/Task;", "()V", "latch", "Ljava/util/concurrent/CountDownLatch;", "getLatch", "()Ljava/util/concurrent/CountDownLatch;", "runOnce", "", "okhttp"}, k = 1, mv = {1, 4, 0})
    /* compiled from: TaskQueue.kt */
    private static final class AwaitIdleTask extends Task {
        private final CountDownLatch latch = new CountDownLatch(1);

        public AwaitIdleTask() {
            super(Util.okHttpName + " awaitIdle", false);
        }

        public final CountDownLatch getLatch() {
            return this.latch;
        }

        public long runOnce() {
            this.latch.countDown();
            return -1;
        }
    }

    public final boolean scheduleAndDecide$okhttp(Task task, long delayNanos, boolean recurrence) {
        String str;
        Task task2 = task;
        Intrinsics.checkNotNullParameter(task2, "task");
        task2.initQueue$okhttp(this);
        long now = this.taskRunner.getBackend().nanoTime();
        long executeNanoTime = now + delayNanos;
        int existingIndex = this.futureTasks.indexOf(task2);
        if (existingIndex != -1) {
            if (task.getNextExecuteNanoTime$okhttp() <= executeNanoTime) {
                if (TaskRunner.Companion.getLogger().isLoggable(Level.FINE)) {
                    TaskLoggerKt.log(task2, this, "already scheduled");
                }
                return false;
            }
            this.futureTasks.remove(existingIndex);
        }
        task2.setNextExecuteNanoTime$okhttp(executeNanoTime);
        if (TaskRunner.Companion.getLogger().isLoggable(Level.FINE)) {
            if (recurrence) {
                str = "run again after " + TaskLoggerKt.formatDuration(executeNanoTime - now);
            } else {
                str = "scheduled after " + TaskLoggerKt.formatDuration(executeNanoTime - now);
            }
            TaskLoggerKt.log(task2, this, str);
        }
        int index$iv = 0;
        Iterator<Task> it = this.futureTasks.iterator();
        while (true) {
            if (!it.hasNext()) {
                index$iv = -1;
                break;
            }
            if ((it.next().getNextExecuteNanoTime$okhttp() - now > delayNanos ? 1 : null) != null) {
                break;
            }
            index$iv++;
        }
        int insertAt = index$iv;
        if (insertAt == -1) {
            insertAt = this.futureTasks.size();
        }
        this.futureTasks.add(insertAt, task2);
        if (insertAt == 0) {
            return true;
        }
        return false;
    }

    public final void cancelAll() {
        if (!Util.assertionsEnabled || !Thread.holdsLock(this)) {
            synchronized (this.taskRunner) {
                if (cancelAllAndDecide$okhttp()) {
                    this.taskRunner.kickCoordinator$okhttp(this);
                }
                Unit unit = Unit.INSTANCE;
            }
            return;
        }
        StringBuilder append = new StringBuilder().append("Thread ");
        Thread currentThread = Thread.currentThread();
        Intrinsics.checkNotNullExpressionValue(currentThread, "Thread.currentThread()");
        throw new AssertionError(append.append(currentThread.getName()).append(" MUST NOT hold lock on ").append(this).toString());
    }

    public final void shutdown() {
        if (!Util.assertionsEnabled || !Thread.holdsLock(this)) {
            synchronized (this.taskRunner) {
                this.shutdown = true;
                if (cancelAllAndDecide$okhttp()) {
                    this.taskRunner.kickCoordinator$okhttp(this);
                }
                Unit unit = Unit.INSTANCE;
            }
            return;
        }
        StringBuilder append = new StringBuilder().append("Thread ");
        Thread currentThread = Thread.currentThread();
        Intrinsics.checkNotNullExpressionValue(currentThread, "Thread.currentThread()");
        throw new AssertionError(append.append(currentThread.getName()).append(" MUST NOT hold lock on ").append(this).toString());
    }

    public final boolean cancelAllAndDecide$okhttp() {
        if (this.activeTask != null) {
            Task task = this.activeTask;
            Intrinsics.checkNotNull(task);
            if (task.getCancelable()) {
                this.cancelActiveTask = true;
            }
        }
        boolean tasksCanceled = false;
        int size = this.futureTasks.size() - 1;
        while (size >= 0) {
            int i = size;
            if (this.futureTasks.get(i).getCancelable()) {
                Task task$iv = this.futureTasks.get(i);
                if (TaskRunner.Companion.getLogger().isLoggable(Level.FINE)) {
                    TaskLoggerKt.log(task$iv, this, "canceled");
                }
                tasksCanceled = true;
                this.futureTasks.remove(i);
            }
            size = i - 1;
        }
        return tasksCanceled;
    }

    public String toString() {
        return this.name;
    }
}
