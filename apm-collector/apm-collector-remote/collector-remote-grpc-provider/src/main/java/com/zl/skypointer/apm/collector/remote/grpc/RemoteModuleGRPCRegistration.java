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


package com.zl.skypointer.apm.collector.remote.grpc;


import com.zl.skypointer.apm.collector.cluster.ModuleRegistration;
import com.zl.skypointer.apm.collector.core.util.Const;

/**
 * @author peng-yongsheng
 */
public class RemoteModuleGRPCRegistration extends ModuleRegistration {

    private final String host;
    private final int port;

    public RemoteModuleGRPCRegistration(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override public Value buildValue() {
        return new Value(host, port, Const.EMPTY_STRING);
    }
}
