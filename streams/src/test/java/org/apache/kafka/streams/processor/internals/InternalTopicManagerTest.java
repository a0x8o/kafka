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

import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.MockAdminClient;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.Node;
<<<<<<< HEAD
import org.apache.kafka.common.metrics.MetricsReporter;
import org.apache.kafka.common.protocol.Errors;
import org.apache.kafka.common.requests.MetadataResponse;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Time;
=======
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.utils.Utils;
>>>>>>> cf2e714f3f44ee03c678823e8def8fa8d7dc218f
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.StreamsException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
<<<<<<< HEAD
=======
import java.util.List;
>>>>>>> cf2e714f3f44ee03c678823e8def8fa8d7dc218f
import java.util.Map;

<<<<<<< HEAD
import static org.apache.kafka.streams.processor.internals.InternalTopicManager.WINDOW_CHANGE_LOG_ADDITIONAL_RETENTION_DEFAULT;
import static org.junit.Assert.assertEquals;
=======
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
>>>>>>> cf2e714f3f44ee03c678823e8def8fa8d7dc218f

public class InternalTopicManagerTest {

    private final Node broker1 = new Node(0, "dummyHost-1", 1234);
    private final Node broker2 = new Node(1, "dummyHost-2", 1234);
    private final List<Node> cluster = new ArrayList<Node>(2) {
        {
            add(broker1);
            add(broker2);
        }
    };
    private final String topic = "test_topic";
<<<<<<< HEAD
    private final String userEndPoint = "localhost:2171";
    private MockStreamKafkaClient streamsKafkaClient;
    private final Time time = new MockTime();
=======
    private final String topic2 = "test_topic_2";
    private final String topic3 = "test_topic_3";
    private final List<Node> singleReplica = Collections.singletonList(broker1);

    private MockAdminClient mockAdminClient;
    private InternalTopicManager internalTopicManager;

    private final Map<String, Object> config = new HashMap<String, Object>() {
        {
            put(StreamsConfig.APPLICATION_ID_CONFIG, "app-id");
            put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, broker1.host() + ":" + broker1.port());
            put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
            put(StreamsConfig.adminClientPrefix(StreamsConfig.RETRIES_CONFIG), 1);
            put(StreamsConfig.producerPrefix(ProducerConfig.BATCH_SIZE_CONFIG), 16384);
        }
    };
>>>>>>> cf2e714f3f44ee03c678823e8def8fa8d7dc218f

    @Before
    public void init() {
        mockAdminClient = new MockAdminClient(cluster);
        internalTopicManager = new InternalTopicManager(
            mockAdminClient,
            new StreamsConfig(config));
    }

    @After
    public void shutdown() {
        mockAdminClient.close();
    }

    @Test
    public void shouldReturnCorrectPartitionCounts() {
<<<<<<< HEAD
        final InternalTopicManager internalTopicManager = new InternalTopicManager(
            streamsKafkaClient,
            1,
            WINDOW_CHANGE_LOG_ADDITIONAL_RETENTION_DEFAULT,
            time);
=======
        mockAdminClient.addTopic(
            false,
            topic,
            Collections.singletonList(new TopicPartitionInfo(0, broker1, singleReplica, Collections.<Node>emptyList())),
            null);
>>>>>>> cf2e714f3f44ee03c678823e8def8fa8d7dc218f
        assertEquals(Collections.singletonMap(topic, 1), internalTopicManager.getNumPartitions(Collections.singleton(topic)));
    }

    @Test
<<<<<<< HEAD
    public void shouldCreateRequiredTopics() {
        streamsKafkaClient.returnNoMetadata = true;

        final InternalTopicManager internalTopicManager = new InternalTopicManager(
            streamsKafkaClient,
            1,
            WINDOW_CHANGE_LOG_ADDITIONAL_RETENTION_DEFAULT,
            time);

        final InternalTopicConfig topicConfig = new InternalTopicConfig(topic, Collections.singleton(InternalTopicConfig.CleanupPolicy.compact), null);
        internalTopicManager.makeReady(Collections.singletonMap(topicConfig, 1));

        assertEquals(Collections.singletonMap(topic, topicConfig), streamsKafkaClient.createdTopics);
        assertEquals(Collections.singletonMap(topic, 1), streamsKafkaClient.numberOfPartitionsPerTopic);
        assertEquals(Collections.singletonMap(topic, 1), streamsKafkaClient.replicationFactorPerTopic);
=======
    public void shouldCreateRequiredTopics() throws Exception {
        final InternalTopicConfig topicConfig = new RepartitionTopicConfig(topic, Collections.<String, String>emptyMap());
        topicConfig.setNumberOfPartitions(1);
        final InternalTopicConfig topicConfig2 = new UnwindowedChangelogTopicConfig(topic2, Collections.<String, String>emptyMap());
        topicConfig2.setNumberOfPartitions(1);
        final InternalTopicConfig topicConfig3 = new WindowedChangelogTopicConfig(topic3, Collections.<String, String>emptyMap());
        topicConfig3.setNumberOfPartitions(1);

        internalTopicManager.makeReady(Collections.singletonMap(topic, topicConfig));
        internalTopicManager.makeReady(Collections.singletonMap(topic2, topicConfig2));
        internalTopicManager.makeReady(Collections.singletonMap(topic3, topicConfig3));

        assertEquals(Utils.mkSet(topic, topic2, topic3), mockAdminClient.listTopics().names().get());
        assertEquals(new TopicDescription(topic, false, new ArrayList<TopicPartitionInfo>() {
            {
                add(new TopicPartitionInfo(0, broker1, singleReplica, Collections.<Node>emptyList()));
            }
        }), mockAdminClient.describeTopics(Collections.singleton(topic)).values().get(topic).get());
        assertEquals(new TopicDescription(topic2, false, new ArrayList<TopicPartitionInfo>() {
            {
                add(new TopicPartitionInfo(0, broker1, singleReplica, Collections.<Node>emptyList()));
            }
        }), mockAdminClient.describeTopics(Collections.singleton(topic2)).values().get(topic2).get());
        assertEquals(new TopicDescription(topic3, false, new ArrayList<TopicPartitionInfo>() {
            {
                add(new TopicPartitionInfo(0, broker1, singleReplica, Collections.<Node>emptyList()));
            }
        }), mockAdminClient.describeTopics(Collections.singleton(topic3)).values().get(topic3).get());

        ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, topic);
        ConfigResource resource2 = new ConfigResource(ConfigResource.Type.TOPIC, topic2);
        ConfigResource resource3 = new ConfigResource(ConfigResource.Type.TOPIC, topic3);

        assertEquals(new ConfigEntry(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE), mockAdminClient.describeConfigs(Collections.singleton(resource)).values().get(resource).get().get(TopicConfig.CLEANUP_POLICY_CONFIG));
        assertEquals(new ConfigEntry(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT), mockAdminClient.describeConfigs(Collections.singleton(resource2)).values().get(resource2).get().get(TopicConfig.CLEANUP_POLICY_CONFIG));
        assertEquals(new ConfigEntry(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT + "," + TopicConfig.CLEANUP_POLICY_DELETE), mockAdminClient.describeConfigs(Collections.singleton(resource3)).values().get(resource3).get().get(TopicConfig.CLEANUP_POLICY_CONFIG));

>>>>>>> cf2e714f3f44ee03c678823e8def8fa8d7dc218f
    }

    @Test
    public void shouldNotCreateTopicIfExistsWithDifferentPartitions() {
<<<<<<< HEAD
        final InternalTopicManager internalTopicManager = new InternalTopicManager(
            streamsKafkaClient,
            1,
            WINDOW_CHANGE_LOG_ADDITIONAL_RETENTION_DEFAULT,
            time);
        try {
            internalTopicManager.makeReady(Collections.singletonMap(new InternalTopicConfig(topic, Collections.singleton(InternalTopicConfig.CleanupPolicy.compact), null), 2));
            Assert.fail("Should have thrown StreamsException");
=======
        mockAdminClient.addTopic(
            false,
            topic,
            new ArrayList<TopicPartitionInfo>() {
                {
                    add(new TopicPartitionInfo(0, broker1, singleReplica, Collections.<Node>emptyList()));
                    add(new TopicPartitionInfo(1, broker1, singleReplica, Collections.<Node>emptyList()));
                }
            },
            null);

        try {
            final InternalTopicConfig internalTopicConfig = new RepartitionTopicConfig(topic, Collections.<String, String>emptyMap());
            internalTopicConfig.setNumberOfPartitions(1);
            internalTopicManager.makeReady(Collections.singletonMap(topic, internalTopicConfig));
            fail("Should have thrown StreamsException");
>>>>>>> cf2e714f3f44ee03c678823e8def8fa8d7dc218f
        } catch (StreamsException expected) { /* pass */ }
    }

    @Test
    public void shouldNotThrowExceptionIfExistsWithDifferentReplication() {
<<<<<<< HEAD

        // create topic the first time with replication 2
        final InternalTopicManager internalTopicManager = new InternalTopicManager(
            streamsKafkaClient,
            2,
            WINDOW_CHANGE_LOG_ADDITIONAL_RETENTION_DEFAULT,
            time);
        internalTopicManager.makeReady(Collections.singletonMap(
            new InternalTopicConfig(topic,
                                    Collections.singleton(InternalTopicConfig.CleanupPolicy.compact),
                                    null),
            1));

        // attempt to create it again with replication 1
        final InternalTopicManager internalTopicManager2 = new InternalTopicManager(
            streamsKafkaClient,
            1,
            WINDOW_CHANGE_LOG_ADDITIONAL_RETENTION_DEFAULT,
            time);

        internalTopicManager2.makeReady(Collections.singletonMap(
            new InternalTopicConfig(topic,
                                    Collections.singleton(InternalTopicConfig.CleanupPolicy.compact),
                                   null),
            1));
=======
        mockAdminClient.addTopic(
            false,
            topic,
            Collections.singletonList(new TopicPartitionInfo(0, broker1, cluster, Collections.<Node>emptyList())),
            null);

        // attempt to create it again with replication 1
        final InternalTopicManager internalTopicManager2 = new InternalTopicManager(
            mockAdminClient,
            new StreamsConfig(config));

        final InternalTopicConfig internalTopicConfig = new RepartitionTopicConfig(topic, Collections.<String, String>emptyMap());
        internalTopicConfig.setNumberOfPartitions(1);
        internalTopicManager2.makeReady(Collections.singletonMap(topic, internalTopicConfig));
>>>>>>> cf2e714f3f44ee03c678823e8def8fa8d7dc218f
    }

    @Test
    public void shouldNotThrowExceptionForEmptyTopicMap() {
<<<<<<< HEAD
        final InternalTopicManager internalTopicManager = new InternalTopicManager(
            streamsKafkaClient,
            1,
            WINDOW_CHANGE_LOG_ADDITIONAL_RETENTION_DEFAULT,
            time);

        internalTopicManager.makeReady(Collections.<InternalTopicConfig, Integer>emptyMap());
    }

    private Properties configProps() {
        return new Properties() {
            {
                setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "Internal-Topic-ManagerTest");
                setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, userEndPoint);
                setProperty(StreamsConfig.BUFFERED_RECORDS_PER_PARTITION_CONFIG, "3");
                setProperty(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, MockTimestampExtractor.class.getName());
            }
        };
    }

    private class MockStreamKafkaClient extends StreamsKafkaClient {

        boolean returnNoMetadata = false;

        Map<String, InternalTopicConfig> createdTopics = new HashMap<>();
        Map<String, Integer> numberOfPartitionsPerTopic = new HashMap<>();
        Map<String, Integer> replicationFactorPerTopic = new HashMap<>();

        MockStreamKafkaClient(final StreamsConfig streamsConfig) {
            super(StreamsKafkaClient.Config.fromStreamsConfig(streamsConfig),
                  new MockClient(new MockTime()),
                  Collections.<MetricsReporter>emptyList(),
                  new LogContext());
        }

        @Override
        public void createTopics(final Map<InternalTopicConfig, Integer> topicsMap,
                                 final int replicationFactor,
                                 final long windowChangeLogAdditionalRetention,
                                 final MetadataResponse metadata) {
            for (final Map.Entry<InternalTopicConfig, Integer> topic : topicsMap.entrySet()) {
                final InternalTopicConfig config = topic.getKey();
                final String topicName = config.name();
                createdTopics.put(topicName, config);
                numberOfPartitionsPerTopic.put(topicName, topic.getValue());
                replicationFactorPerTopic.put(topicName, replicationFactor);
            }
        }

        @Override
        public MetadataResponse fetchMetadata() {
            final Node node = new Node(1, "host1", 1001);
            final MetadataResponse.PartitionMetadata partitionMetadata = new MetadataResponse.PartitionMetadata(Errors.NONE, 1, node, new ArrayList<Node>(), new ArrayList<Node>(), new ArrayList<Node>());
            final MetadataResponse.TopicMetadata topicMetadata = new MetadataResponse.TopicMetadata(Errors.NONE, topic, true, Collections.singletonList(partitionMetadata));
            final MetadataResponse metadataResponse;
            if (returnNoMetadata) {
                metadataResponse = new MetadataResponse(
                    Collections.<Node>singletonList(node),
                    null,
                    MetadataResponse.NO_CONTROLLER_ID,
                    Collections.<MetadataResponse.TopicMetadata>emptyList());
            } else {
                metadataResponse = new MetadataResponse(
                    Collections.<Node>singletonList(node),
                    null,
                    MetadataResponse.NO_CONTROLLER_ID,
                    Collections.singletonList(topicMetadata));
            }

            return metadataResponse;
=======
        internalTopicManager.makeReady(Collections.<String, InternalTopicConfig>emptyMap());
    }

    @Test
    public void shouldExhaustRetriesOnTimeoutExceptionForMakeReady() {
        mockAdminClient.timeoutNextRequest(4);

        final InternalTopicConfig internalTopicConfig = new RepartitionTopicConfig(topic, Collections.<String, String>emptyMap());
        internalTopicConfig.setNumberOfPartitions(1);
        try {
            internalTopicManager.makeReady(Collections.singletonMap(topic, internalTopicConfig));
            fail("Should have thrown StreamsException.");
        } catch (final StreamsException expected) {
            assertNull(expected.getCause());
            assertEquals("Could not create topics. This can happen if the Kafka cluster is temporary not available. You can increase admin client config `retries` to be resilient against this error.", expected.getMessage());
>>>>>>> cf2e714f3f44ee03c678823e8def8fa8d7dc218f
        }
    }

}
