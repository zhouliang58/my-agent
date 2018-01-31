package com.zl.skypointer.apm.agent.core.dictionary;

import com.zl.skypointer.apm.network.proto.Application;
import com.zl.skypointer.apm.network.proto.ApplicationMapping;
import com.zl.skypointer.apm.network.proto.ApplicationRegisterServiceGrpc;
import com.zl.skypointer.apm.network.proto.KeyWithIntegerValue;
import io.netty.util.internal.ConcurrentSet;


import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.zl.skypointer.apm.agent.core.conf.Config.Dictionary.APPLICATION_CODE_BUFFER_SIZE;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 15:18
 * @since JDK1.8
 */
public enum ApplicationDictionary {
    INSTANCE;
    private Map<String, Integer> applicationDictionary = new ConcurrentHashMap<String, Integer>();
    private Set<String> unRegisterApplications = new ConcurrentSet<String>();

    public PossibleFound find(String applicationCode) {
        Integer applicationId = applicationDictionary.get(applicationCode);
        if (applicationId != null) {
            return new Found(applicationId);
        } else {
            if (applicationDictionary.size() + unRegisterApplications.size() < APPLICATION_CODE_BUFFER_SIZE) {
                unRegisterApplications.add(applicationCode);
            }
            return new NotFound();
        }
    }

    public void syncRemoteDictionary(
            ApplicationRegisterServiceGrpc.ApplicationRegisterServiceBlockingStub applicationRegisterServiceBlockingStub) {
        if (unRegisterApplications.size() > 0) {
            ApplicationMapping applicationMapping = applicationRegisterServiceBlockingStub.register(
                    Application.newBuilder().addAllApplicationCode(unRegisterApplications).build());
            if (applicationMapping.getApplicationCount() > 0) {
                for (KeyWithIntegerValue keyWithIntegerValue : applicationMapping.getApplicationList()) {
                    unRegisterApplications.remove(keyWithIntegerValue.getKey());
                    applicationDictionary.put(keyWithIntegerValue.getKey(), keyWithIntegerValue.getValue());
                }
            }
        }
    }
}
