/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.apimgt.gateway.throttling.dto;

import javax.jms.ExceptionListener;
import javax.jms.MessageListener;

/**
 * this is a temporary class to hold JMS throttling topic configs
 */
public class JMSConfigDTO {

    private String username;
    private String password;
    private String topicName;

    private String clientId;
    private String virtualHostName;
    private String defaultHostname;
    private String defaultPort;

    private ExceptionListener defaultExceptionListener;
    private MessageListener messageListenerl;

    public JMSConfigDTO(String username, String password, String topicName) {
        this.username = username;
        this.password = password;
        this.topicName = topicName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getVirtualHostName() {
        return virtualHostName;
    }

    public void setVirtualHostName(String virtualHostName) {
        this.virtualHostName = virtualHostName;
    }

    public String getDefaultHostname() {
        return defaultHostname;
    }

    public void setDefaultHostname(String defaultHostname) {
        this.defaultHostname = defaultHostname;
    }

    public String getDefaultPort() {
        return defaultPort;
    }

    public void setDefaultPort(String defaultPort) {
        this.defaultPort = defaultPort;
    }

    public ExceptionListener getDefaultExceptionListener() {
        return defaultExceptionListener;
    }

    public void setDefaultExceptionListener(ExceptionListener defaultExceptionListener) {
        this.defaultExceptionListener = defaultExceptionListener;
    }

    public MessageListener getMessageListenerl() {
        return messageListenerl;
    }

    public void setMessageListenerl(MessageListener messageListenerl) {
        this.messageListenerl = messageListenerl;
    }
}
