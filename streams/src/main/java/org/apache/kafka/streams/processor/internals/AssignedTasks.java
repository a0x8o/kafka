/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.processor.internals;

<<<<<<< HEAD
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.streams.errors.LockException;
import org.apache.kafka.streams.errors.StreamsException;
import org.apache.kafka.streams.errors.TaskMigratedException;
=======
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.streams.errors.LockException;
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
import org.apache.kafka.streams.processor.TaskId;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

<<<<<<< HEAD
class AssignedTasks implements RestoringTasks {
=======
class AssignedTasks {
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
    private final Logger log;
    private final String taskTypeName;
    private final TaskAction maybeCommitAction;
    private final TaskAction commitAction;
    private Map<TaskId, Task> created = new HashMap<>();
    private Map<TaskId, Task> suspended = new HashMap<>();
    private Map<TaskId, Task> restoring = new HashMap<>();
    private Set<TopicPartition> restoredPartitions = new HashSet<>();
    private Set<TaskId> previousActiveTasks = new HashSet<>();
    // IQ may access this map.
    private Map<TaskId, Task> running = new ConcurrentHashMap<>();
    private Map<TopicPartition, Task> runningByPartition = new HashMap<>();
<<<<<<< HEAD
    private Map<TopicPartition, Task> restoringByPartition = new HashMap<>();
=======
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
    private int committed = 0;


    AssignedTasks(final LogContext logContext,
                  final String taskTypeName) {
        this.taskTypeName = taskTypeName;

        this.log = logContext.logger(getClass());

        maybeCommitAction = new TaskAction() {
            @Override
            public String name() {
                return "maybeCommit";
            }

            @Override
            public void apply(final Task task) {
                if (task.commitNeeded()) {
                    committed++;
                    task.commit();
                    if (log.isDebugEnabled()) {
                        log.debug("Committed active task {} per user request in", task.id());
                    }
                }
            }
        };

        commitAction = new TaskAction() {
            @Override
            public String name() {
                return "commit";
            }

            @Override
            public void apply(final Task task) {
                task.commit();
            }
        };
    }

    void addNewTask(final Task task) {
        created.put(task.id(), task);
    }

    Set<TopicPartition> uninitializedPartitions() {
        if (created.isEmpty()) {
            return Collections.emptySet();
        }
        final Set<TopicPartition> partitions = new HashSet<>();
        for (final Map.Entry<TaskId, Task> entry : created.entrySet()) {
            if (entry.getValue().hasStateStores()) {
                partitions.addAll(entry.getValue().partitions());
            }
        }
        return partitions;
    }

<<<<<<< HEAD
    /**
     * @return partitions that are ready to be resumed
     * @throws IllegalStateException If store gets registered after initialized is already finished
     * @throws StreamsException if the store's change log does not contain the partition
     */
    Set<TopicPartition> initializeNewTasks() {
        final Set<TopicPartition> readyPartitions = new HashSet<>();
=======
    void initializeNewTasks() {
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
        if (!created.isEmpty()) {
            log.debug("Initializing {}s {}", taskTypeName, created.keySet());
        }
        for (final Iterator<Map.Entry<TaskId, Task>> it = created.entrySet().iterator(); it.hasNext(); ) {
            final Map.Entry<TaskId, Task> entry = it.next();
            try {
                if (!entry.getValue().initialize()) {
<<<<<<< HEAD
                    log.debug("Transitioning {} {} to restoring", taskTypeName, entry.getKey());
                    addToRestoring(entry.getValue());
                } else {
                    transitionToRunning(entry.getValue(), readyPartitions);
=======
                    log.debug("transitioning {} {} to restoring", taskTypeName, entry.getKey());
                    restoring.put(entry.getKey(), entry.getValue());
                } else {
                    transitionToRunning(entry.getValue());
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
                }
                it.remove();
            } catch (final LockException e) {
                // made this trace as it will spam the logs in the poll loop.
                log.trace("Could not create {} {} due to {}; will retry", taskTypeName, entry.getKey(), e.getMessage());
            }
        }
<<<<<<< HEAD
        return readyPartitions;
=======
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
    }

    Set<TopicPartition> updateRestored(final Collection<TopicPartition> restored) {
        if (restored.isEmpty()) {
            return Collections.emptySet();
        }
<<<<<<< HEAD
        log.trace("{} changelog partitions that have completed restoring so far: {}", taskTypeName, restored);
=======
        log.trace("{} partitions restored for {}", taskTypeName, restored);
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
        final Set<TopicPartition> resume = new HashSet<>();
        restoredPartitions.addAll(restored);
        for (final Iterator<Map.Entry<TaskId, Task>> it = restoring.entrySet().iterator(); it.hasNext(); ) {
            final Map.Entry<TaskId, Task> entry = it.next();
            final Task task = entry.getValue();
            if (restoredPartitions.containsAll(task.changelogPartitions())) {
<<<<<<< HEAD
                transitionToRunning(task, resume);
=======
                transitionToRunning(task);
                resume.addAll(task.partitions());
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
                it.remove();
            } else {
                if (log.isTraceEnabled()) {
                    final HashSet<TopicPartition> outstandingPartitions = new HashSet<>(task.changelogPartitions());
                    outstandingPartitions.removeAll(restoredPartitions);
<<<<<<< HEAD
                    log.trace("{} {} cannot resume processing yet since some of its changelog partitions have not completed restoring: {}",
                              taskTypeName,
                              task.id(),
                              outstandingPartitions);
=======
                    log.trace("partition restoration not complete for {} {} partitions: {}",
                              taskTypeName,
                              task.id(),
                              task.changelogPartitions());
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
                }
            }
        }
        if (allTasksRunning()) {
            restoredPartitions.clear();
        }
        return resume;
    }

    boolean allTasksRunning() {
        return created.isEmpty()
                && suspended.isEmpty()
                && restoring.isEmpty();
    }

    Collection<Task> running() {
        return running.values();
    }

    RuntimeException suspend() {
        final AtomicReference<RuntimeException> firstException = new AtomicReference<>(null);
        log.trace("Suspending running {} {}", taskTypeName, runningTaskIds());
        firstException.compareAndSet(null, suspendTasks(running.values()));
        log.trace("Close restoring {} {}", taskTypeName, restoring.keySet());
        firstException.compareAndSet(null, closeNonRunningTasks(restoring.values()));
        log.trace("Close created {} {}", taskTypeName, created.keySet());
        firstException.compareAndSet(null, closeNonRunningTasks(created.values()));
        previousActiveTasks.clear();
        previousActiveTasks.addAll(running.keySet());
        running.clear();
        restoring.clear();
        created.clear();
        runningByPartition.clear();
<<<<<<< HEAD
        restoringByPartition.clear();
=======
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
        return firstException.get();
    }

    private RuntimeException closeNonRunningTasks(final Collection<Task> tasks) {
        RuntimeException exception = null;
        for (final Task task : tasks) {
            try {
                task.close(false, false);
            } catch (final RuntimeException e) {
                log.error("Failed to close {}, {}", taskTypeName, task.id(), e);
                if (exception == null) {
                    exception = e;
                }
            }
        }
        return exception;
    }

    private RuntimeException suspendTasks(final Collection<Task> tasks) {
<<<<<<< HEAD
        final AtomicReference<RuntimeException> firstException = new AtomicReference<>(null);
=======
        RuntimeException exception = null;
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
        for (Iterator<Task> it = tasks.iterator(); it.hasNext(); ) {
            final Task task = it.next();
            try {
                task.suspend();
                suspended.put(task.id(), task);
<<<<<<< HEAD
            } catch (final TaskMigratedException closeAsZombieAndSwallow) {
                // as we suspend a task, we are either shutting down or rebalancing, thus, we swallow and move on
                firstException.compareAndSet(null, closeZombieTask(task));
                it.remove();
            } catch (final RuntimeException e) {
                log.error("Suspending {} {} failed due to the following error:", taskTypeName, task.id(), e);
                firstException.compareAndSet(null, e);
                try {
                    task.close(false, false);
                } catch (final RuntimeException f) {
                    log.error("After suspending failed, closing the same {} {} failed again due to the following error:", taskTypeName, task.id(), f);
                }
            }
        }
        return firstException.get();
    }

    private RuntimeException closeZombieTask(final Task task) {
        log.warn("{} {} got migrated to another thread already. Closing it as zombie.", taskTypeName, task.id());
        try {
            task.close(false, true);
        } catch (final RuntimeException e) {
            log.warn("Failed to close zombie {} {} due to {}; ignore and proceed.", taskTypeName, task.id(), e.getMessage());
            return e;
        }
        return null;
=======
            } catch (final CommitFailedException e) {
                suspended.put(task.id(), task);
                // commit failed during suspension. Just log it.
                log.warn("Failed to commit {} {} state when suspending due to CommitFailedException", taskTypeName, task.id());
            } catch (final ProducerFencedException e) {
                closeZombieTask(task);
                it.remove();
            } catch (final RuntimeException e) {
                log.error("Suspending {} {} failed due to the following error:", taskTypeName, task.id(), e);
                try {
                    task.close(false, false);
                } catch (final Exception f) {
                    log.error("After suspending failed, closing the same {} {} failed again due to the following error:", taskTypeName, task.id(), f);
                }
                if (exception == null) {
                    exception = e;
                }
            }
        }
        return exception;
    }

    private void closeZombieTask(final Task task) {
        log.warn("Producer of task {} fenced; closing zombie task", task.id());
        try {
            task.close(false, true);
        } catch (final Exception e) {
            log.warn("{} Failed to close zombie due to {}, ignore and proceed", taskTypeName, e);
        }
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
    }

    boolean hasRunningTasks() {
        return !running.isEmpty();
    }

<<<<<<< HEAD
    /**
     * @throws TaskMigratedException if the task producer got fenced (EOS only)
     */
=======
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
    boolean maybeResumeSuspendedTask(final TaskId taskId, final Set<TopicPartition> partitions) {
        if (suspended.containsKey(taskId)) {
            final Task task = suspended.get(taskId);
            log.trace("found suspended {} {}", taskTypeName, taskId);
            if (task.partitions().equals(partitions)) {
                suspended.remove(taskId);
<<<<<<< HEAD
                try {
                    task.resume();
                } catch (final TaskMigratedException e) {
                    final RuntimeException fatalException = closeZombieTask(task);
                    if (fatalException != null) {
                        throw fatalException;
                    }
                    suspended.remove(taskId);
                    throw e;
                }
                transitionToRunning(task, new HashSet<TopicPartition>());
                log.trace("resuming suspended {} {}", taskTypeName, task.id());
                return true;
            } else {
                log.warn("couldn't resume task {} assigned partitions {}, task partitions {}", taskId, partitions, task.partitions());
=======
                task.resume();
                transitionToRunning(task);
                log.trace("resuming suspended {} {}", taskTypeName, task.id());
                return true;
            } else {
                log.trace("couldn't resume task {} assigned partitions {}, task partitions {}", taskId, partitions, task.partitions());
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
            }
        }
        return false;
    }

<<<<<<< HEAD
    private void addToRestoring(final Task task) {
        restoring.put(task.id(), task);
        for (TopicPartition topicPartition : task.partitions()) {
            restoringByPartition.put(topicPartition, task);
        }
        for (TopicPartition topicPartition : task.changelogPartitions()) {
            restoringByPartition.put(topicPartition, task);
        }
    }

    private void transitionToRunning(final Task task, final Set<TopicPartition> readyPartitions) {
=======
    private void transitionToRunning(final Task task) {
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
        log.debug("transitioning {} {} to running", taskTypeName, task.id());
        running.put(task.id(), task);
        for (TopicPartition topicPartition : task.partitions()) {
            runningByPartition.put(topicPartition, task);
<<<<<<< HEAD
            if (task.hasStateStores()) {
                readyPartitions.add(topicPartition);
            }
=======
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
        }
        for (TopicPartition topicPartition : task.changelogPartitions()) {
            runningByPartition.put(topicPartition, task);
        }
    }

<<<<<<< HEAD
    @Override
    public Task restoringTaskFor(final TopicPartition partition) {
        return restoringByPartition.get(partition);
    }

=======
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
    Task runningTaskFor(final TopicPartition partition) {
        return runningByPartition.get(partition);
    }

    Set<TaskId> runningTaskIds() {
        return running.keySet();
    }

    Map<TaskId, Task> runningTaskMap() {
        return Collections.unmodifiableMap(running);
    }

    public String toString(final String indent) {
        final StringBuilder builder = new StringBuilder();
        describe(builder, running.values(), indent, "Running:");
        describe(builder, suspended.values(), indent, "Suspended:");
        describe(builder, restoring.values(), indent, "Restoring:");
        describe(builder, created.values(), indent, "New:");
        return builder.toString();
    }

    private void describe(final StringBuilder builder,
                          final Collection<Task> tasks,
                          final String indent,
                          final String name) {
        builder.append(indent).append(name);
        for (final Task t : tasks) {
            builder.append(indent).append(t.toString(indent + "\t\t"));
        }
        builder.append("\n");
    }

    private List<Task> allTasks() {
        final List<Task> tasks = new ArrayList<>();
        tasks.addAll(running.values());
        tasks.addAll(suspended.values());
        tasks.addAll(restoring.values());
        tasks.addAll(created.values());
        return tasks;
    }

    Collection<Task> restoringTasks() {
        return Collections.unmodifiableCollection(restoring.values());
    }

    Set<TaskId> allAssignedTaskIds() {
        final Set<TaskId> taskIds = new HashSet<>();
        taskIds.addAll(running.keySet());
        taskIds.addAll(suspended.keySet());
        taskIds.addAll(restoring.keySet());
        taskIds.addAll(created.keySet());
        return taskIds;
    }

    void clear() {
        runningByPartition.clear();
<<<<<<< HEAD
        restoringByPartition.clear();
=======
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
        running.clear();
        created.clear();
        suspended.clear();
        restoredPartitions.clear();
    }

    Set<TaskId> previousTaskIds() {
        return previousActiveTasks;
    }

<<<<<<< HEAD
    /**
     * @throws TaskMigratedException if committing offsets failed (non-EOS)
     *                               or if the task producer got fenced (EOS)
     */
=======
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
    int commit() {
        applyToRunningTasks(commitAction);
        return running.size();
    }

<<<<<<< HEAD
    /**
     * @throws TaskMigratedException if committing offsets failed (non-EOS)
     *                               or if the task producer got fenced (EOS)
     */
=======
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
    int maybeCommit() {
        committed = 0;
        applyToRunningTasks(maybeCommitAction);
        return committed;
    }

<<<<<<< HEAD
    /**
     * @throws TaskMigratedException if the task producer got fenced (EOS only)
     */
    int process() {
        int processed = 0;
        final Iterator<Map.Entry<TaskId, Task>> it = running.entrySet().iterator();
        while (it.hasNext()) {
            final Task task = it.next().getValue();
=======
    int process() {
        int processed = 0;
        for (final Task task : running.values()) {
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
            try {
                if (task.process()) {
                    processed++;
                }
<<<<<<< HEAD
            } catch (final TaskMigratedException e) {
                final RuntimeException fatalException = closeZombieTask(task);
                if (fatalException != null) {
                    throw fatalException;
                }
                it.remove();
                throw e;
            } catch (final RuntimeException e) {
=======
            } catch (RuntimeException e) {
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
                log.error("Failed to process {} {} due to the following error:", taskTypeName, task.id(), e);
                throw e;
            }
        }
        return processed;
    }

<<<<<<< HEAD
    /**
     * @throws TaskMigratedException if the task producer got fenced (EOS only)
     */
    int punctuate() {
        int punctuated = 0;
        final Iterator<Map.Entry<TaskId, Task>> it = running.entrySet().iterator();
        while (it.hasNext()) {
            final Task task = it.next().getValue();
=======
    int punctuate() {
        int punctuated = 0;
        for (Task task : running.values()) {
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
            try {
                if (task.maybePunctuateStreamTime()) {
                    punctuated++;
                }
                if (task.maybePunctuateSystemTime()) {
                    punctuated++;
                }
<<<<<<< HEAD
            } catch (final TaskMigratedException e) {
                final RuntimeException fatalException = closeZombieTask(task);
                if (fatalException != null) {
                    throw fatalException;
                }
                it.remove();
                throw e;
            } catch (final KafkaException e) {
=======
            } catch (KafkaException e) {
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
                log.error("Failed to punctuate {} {} due to the following error:", taskTypeName, task.id(), e);
                throw e;
            }
        }
        return punctuated;
    }

    private void applyToRunningTasks(final TaskAction action) {
        RuntimeException firstException = null;

        for (Iterator<Task> it = running().iterator(); it.hasNext(); ) {
            final Task task = it.next();
            try {
                action.apply(task);
<<<<<<< HEAD
            } catch (final TaskMigratedException e) {
                final RuntimeException fatalException = closeZombieTask(task);
                if (fatalException != null) {
                    throw fatalException;
                }
                it.remove();
                if (firstException == null) {
                    firstException = e;
                }
=======
            } catch (final CommitFailedException e) {
                // commit failed. This is already logged inside the task as WARN and we can just log it again here.
                log.warn("Failed to commit {} {} during {} state due to CommitFailedException; this task may be no longer owned by the thread", taskTypeName, task.id(), action.name());
            } catch (final ProducerFencedException e) {
                closeZombieTask(task);
                it.remove();
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
            } catch (final RuntimeException t) {
                log.error("Failed to {} {} {} due to the following error:",
                          action.name(),
                          taskTypeName,
                          task.id(),
                          t);
                if (firstException == null) {
                    firstException = t;
                }
            }
        }

        if (firstException != null) {
            throw firstException;
        }
    }

    void closeNonAssignedSuspendedTasks(final Map<TaskId, Set<TopicPartition>> newAssignment) {
        final Iterator<Task> standByTaskIterator = suspended.values().iterator();
        while (standByTaskIterator.hasNext()) {
            final Task suspendedTask = standByTaskIterator.next();
            if (!newAssignment.containsKey(suspendedTask.id()) || !suspendedTask.partitions().equals(newAssignment.get(suspendedTask.id()))) {
                log.debug("Closing suspended and not re-assigned {} {}", taskTypeName, suspendedTask.id());
                try {
                    suspendedTask.closeSuspended(true, false, null);
                } catch (final Exception e) {
                    log.error("Failed to remove suspended {} {} due to the following error:", taskTypeName, suspendedTask.id(), e);
                } finally {
                    standByTaskIterator.remove();
                }
            }
        }
    }

    void close(final boolean clean) {
<<<<<<< HEAD
        final AtomicReference<RuntimeException> firstException = new AtomicReference<>(null);
        for (final Task task : allTasks()) {
            try {
                task.close(clean, false);
            } catch (final TaskMigratedException e) {
                firstException.compareAndSet(null, closeZombieTask(task));
            } catch (final RuntimeException t) {
=======
        close(allTasks(), clean);
        clear();
    }

    private void close(final Collection<Task> tasks, final boolean clean) {
        for (final Task task : tasks) {
            try {
                task.close(clean, false);
            } catch (final Throwable t) {
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
                log.error("Failed while closing {} {} due to the following error:",
                          task.getClass().getSimpleName(),
                          task.id(),
                          t);
<<<<<<< HEAD
                if (clean) {
                    if (!closeUnclean(task)) {
                        firstException.compareAndSet(null, t);
                    }
                } else {
                    firstException.compareAndSet(null, t);
                }
            }
        }

        clear();

        final RuntimeException fatalException = firstException.get();
        if (fatalException != null) {
            throw fatalException;
        }
    }

    private boolean closeUnclean(final Task task) {
        log.info("Try to close {} {} unclean.", task.getClass().getSimpleName(), task.id());
        try {
            task.close(false, false);
        } catch (final RuntimeException fatalException) {
            log.error("Failed while closing {} {} due to the following error:",
                task.getClass().getSimpleName(),
                task.id(),
                fatalException);
            return false;
        }

        return true;
=======
            }
        }
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
    }
}
