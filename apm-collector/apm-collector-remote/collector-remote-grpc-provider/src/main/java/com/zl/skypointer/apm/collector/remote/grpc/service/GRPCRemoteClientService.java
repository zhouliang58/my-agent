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


package com.zl.skypointer.apm.collector.remote.grpc.service;

import com.zl.skypointer.apm.collector.client.ClientException;
import com.zl.skypointer.apm.collector.client.grpc.GRPCClient;
import com.zl.skypointer.apm.collector.remote.service.RemoteClient;
import com.zl.skypointer.apm.collector.remote.service.RemoteClientService;
import com.zl.skypointer.apm.collector.remote.service.RemoteDataIDGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author peng-yongsheng
 */
public class GRPCRemoteClientService implements RemoteClientService {

    private final Logger logger = LoggerFactory.getLogger(GRPCRemoteClientService.class);

    private final RemoteDataIDGetter remoteDataIDGetter;

    GRPCRemoteClientService(RemoteDataIDGetter remoteDataIDGetter) {
        this.remoteDataIDGetter = remoteDataIDGetter;
    }

    @Override public RemoteClient create(String host, int port, int channelSize, int bufferSize) {
        GRPCClient client = new GRPCClient(host, port);
        try {
            client.initialize();
        } catch (ClientException e) {
            logger.error(e.getMessage(), e);
        }
        return new GRPCRemoteClient(client, remoteDataIDGetter, channelSize, bufferSize);
    }
}
