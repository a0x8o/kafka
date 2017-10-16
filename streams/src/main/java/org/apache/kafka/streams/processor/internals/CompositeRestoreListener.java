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


import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.streams.KeyValue;
<<<<<<< HEAD
import org.apache.kafka.streams.errors.StreamsException;
=======
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
import org.apache.kafka.streams.processor.AbstractNotifyingBatchingRestoreCallback;
import org.apache.kafka.streams.processor.BatchingStateRestoreCallback;
import org.apache.kafka.streams.processor.StateRestoreCallback;
import org.apache.kafka.streams.processor.StateRestoreListener;

import java.util.Collection;

public class CompositeRestoreListener implements BatchingStateRestoreCallback, StateRestoreListener {

    public static final NoOpStateRestoreListener NO_OP_STATE_RESTORE_LISTENER = new NoOpStateRestoreListener();
    private final BatchingStateRestoreCallback internalBatchingRestoreCallback;
    private final StateRestoreListener storeRestoreListener;
<<<<<<< HEAD
    private StateRestoreListener userRestoreListener = NO_OP_STATE_RESTORE_LISTENER;
=======
    private StateRestoreListener globalRestoreListener = NO_OP_STATE_RESTORE_LISTENER;
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d

    CompositeRestoreListener(final StateRestoreCallback stateRestoreCallback) {

        if (stateRestoreCallback instanceof StateRestoreListener) {
            storeRestoreListener = (StateRestoreListener) stateRestoreCallback;
        } else {
            storeRestoreListener = NO_OP_STATE_RESTORE_LISTENER;
        }

        internalBatchingRestoreCallback = getBatchingRestoreCallback(stateRestoreCallback);
    }

<<<<<<< HEAD
    /**
     * @throws StreamsException if user provided {@link StateRestoreListener} raises an exception in
     * {@link StateRestoreListener#onRestoreStart(TopicPartition, String, long, long)}
     */
=======
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
    @Override
    public void onRestoreStart(final TopicPartition topicPartition,
                               final String storeName,
                               final long startingOffset,
                               final long endingOffset) {
<<<<<<< HEAD
        userRestoreListener.onRestoreStart(topicPartition, storeName, startingOffset, endingOffset);
        storeRestoreListener.onRestoreStart(topicPartition, storeName, startingOffset, endingOffset);
    }

    /**
     * @throws StreamsException if user provided {@link StateRestoreListener} raises an exception in
     * {@link StateRestoreListener#onBatchRestored(TopicPartition, String, long, long)}
     */
=======
        globalRestoreListener.onRestoreStart(topicPartition, storeName, startingOffset, endingOffset);
        storeRestoreListener.onRestoreStart(topicPartition, storeName, startingOffset, endingOffset);
    }

>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
    @Override
    public void onBatchRestored(final TopicPartition topicPartition,
                                final String storeName,
                                final long batchEndOffset,
                                final long numRestored) {
<<<<<<< HEAD
        userRestoreListener.onBatchRestored(topicPartition, storeName, batchEndOffset, numRestored);
        storeRestoreListener.onBatchRestored(topicPartition, storeName, batchEndOffset, numRestored);
    }

    /**
     * @throws StreamsException if user provided {@link StateRestoreListener} raises an exception in
     * {@link StateRestoreListener#onRestoreEnd(TopicPartition, String, long)}
     */
=======
        globalRestoreListener.onBatchRestored(topicPartition, storeName, batchEndOffset, numRestored);
        storeRestoreListener.onBatchRestored(topicPartition, storeName, batchEndOffset, numRestored);
    }

>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
    @Override
    public void onRestoreEnd(final TopicPartition topicPartition,
                             final String storeName,
                             final long totalRestored) {
<<<<<<< HEAD
        userRestoreListener.onRestoreEnd(topicPartition, storeName, totalRestored);
        storeRestoreListener.onRestoreEnd(topicPartition, storeName, totalRestored);
=======
        globalRestoreListener.onRestoreEnd(topicPartition, storeName, totalRestored);
        storeRestoreListener.onRestoreEnd(topicPartition, storeName, totalRestored);

>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
    }

    @Override
    public void restoreAll(final Collection<KeyValue<byte[], byte[]>> records) {
        internalBatchingRestoreCallback.restoreAll(records);
    }

<<<<<<< HEAD
    void setUserRestoreListener(final StateRestoreListener userRestoreListener) {
        if (userRestoreListener != null) {
            this.userRestoreListener = userRestoreListener;
=======
    void setGlobalRestoreListener(final StateRestoreListener globalRestoreListener) {
        if (globalRestoreListener != null) {
            this.globalRestoreListener = globalRestoreListener;
>>>>>>> 74551108ea1e7cb8a09861db4ae63a531bf19e9d
        }
    }

    @Override
    public void restore(final byte[] key,
                        final byte[] value) {
        throw new UnsupportedOperationException("Single restore functionality shouldn't be called directly but "
                                                + "through the delegated StateRestoreCallback instance");
    }

    private BatchingStateRestoreCallback getBatchingRestoreCallback(StateRestoreCallback restoreCallback) {
        if (restoreCallback instanceof  BatchingStateRestoreCallback) {
            return (BatchingStateRestoreCallback) restoreCallback;
        }

        return new WrappedBatchingStateRestoreCallback(restoreCallback);
    }


    private static final class NoOpStateRestoreListener extends AbstractNotifyingBatchingRestoreCallback {

        @Override
        public void restoreAll(final Collection<KeyValue<byte[], byte[]>> records) {

        }
    }
}
