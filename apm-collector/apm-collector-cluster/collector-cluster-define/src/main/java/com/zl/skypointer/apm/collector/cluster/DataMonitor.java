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


package com.zl.skypointer.apm.collector.cluster;


import com.zl.skypointer.apm.collector.client.Client;
import com.zl.skypointer.apm.collector.client.ClientException;

/**
 * @author peng-yongsheng
 */
public interface DataMonitor {
    String BASE_CATALOG = "/skywalking";

    void setClient(Client client);

    void addListener(ClusterModuleListener listener) throws ClientException;

    void register(String path, ModuleRegistration registration) throws ClientException;

    ClusterModuleListener getListener(String path);

    void createPath(String path) throws ClientException;

    void setData(String path, String value) throws ClientException;
}
