/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package com.zl.skypointer.apm.collector.remote.grpc.handler;

import io.grpc.stub.StreamObserver;
import com.zl.skypointer.apm.collector.core.data.Data;
import com.zl.skypointer.apm.collector.core.graph.GraphManager;
import com.zl.skypointer.apm.collector.core.graph.Next;
import com.zl.skypointer.apm.collector.core.util.Const;
import com.zl.skypointer.apm.collector.remote.grpc.service.GRPCRemoteDeserializeService;
import com.zl.skypointer.apm.collector.remote.grpc.proto.Empty;
import com.zl.skypointer.apm.collector.remote.grpc.proto.RemoteCommonServiceGrpc;
import com.zl.skypointer.apm.collector.remote.grpc.proto.RemoteData;
import com.zl.skypointer.apm.collector.remote.grpc.proto.RemoteMessage;
import com.zl.skypointer.apm.collector.remote.service.RemoteDataInstanceCreatorGetter;
import com.zl.skypointer.apm.collector.remote.service.RemoteDataInstanceCreatorNotFoundException;
import com.zl.skypointer.apm.collector.server.grpc.GRPCHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author peng-yongsheng
 */
public class RemoteCommonServiceHandler extends RemoteCommonServiceGrpc.RemoteCommonServiceImplBase implements GRPCHandler {

    private final Logger logger = LoggerFactory.getLogger(RemoteCommonServiceHandler.class);

    private final RemoteDataInstanceCreatorGetter instanceCreatorGetter;
    private final GRPCRemoteDeserializeService service;

    public RemoteCommonServiceHandler(RemoteDataInstanceCreatorGetter instanceCreatorGetter) {
        this.instanceCreatorGetter = instanceCreatorGetter;
        this.service = new GRPCRemoteDeserializeService();
    }

    @SuppressWarnings("unchecked")
    @Override public StreamObserver<RemoteMessage> call(StreamObserver<Empty> responseObserver) {
        return new StreamObserver<RemoteMessage>() {
            @Override public void onNext(RemoteMessage message) {
                int graphId = message.getGraphId();
                int nodeId = message.getNodeId();
                int remoteDataId = message.getRemoteDataId();
                RemoteData remoteData = message.getRemoteData();

                try {
                    Data output = instanceCreatorGetter.getInstanceCreator(remoteDataId).createInstance(Const.EMPTY_STRING);
                    service.deserialize(remoteData, output);
                    Next next = GraphManager.INSTANCE.findGraph(graphId).toFinder().findNext(nodeId);
                    next.execute(output);
                } catch (RemoteDataInstanceCreatorNotFoundException e) {
                    logger.error(e.getMessage(), e);
                }
            }

            @Override public void onError(Throwable throwable) {
                logger.error(throwable.getMessage(), throwable);
            }

            @Override public void onCompleted() {
                responseObserver.onNext(Empty.newBuilder().build());
                responseObserver.onCompleted();
            }
        };
    }
}
