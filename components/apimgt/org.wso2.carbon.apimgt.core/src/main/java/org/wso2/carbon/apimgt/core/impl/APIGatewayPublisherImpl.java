/*
* Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* WSO2 Inc. licenses this file to you under the Apache License,
* Version 2.0 (the "License"); you may not use this file except
* in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*
*/
package org.wso2.carbon.apimgt.core.impl;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.andes.client.AMQConnectionFactory;
import org.wso2.andes.url.URLSyntaxException;
import org.wso2.carbon.apimgt.core.APIMConfigurations;
import org.wso2.carbon.apimgt.core.api.APIGatewayPublisher;
import org.wso2.carbon.apimgt.core.exception.ExceptionCodes;
import org.wso2.carbon.apimgt.core.exception.GatewayException;
import org.wso2.carbon.apimgt.core.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.core.models.API;
import org.wso2.carbon.apimgt.core.template.dto.GatewayConfigDTO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

/**
 * API gateway related functions
 */
public class APIGatewayPublisherImpl implements APIGatewayPublisher {
    private static final Logger log = LoggerFactory.getLogger(APIGatewayPublisherImpl.class);
    private APIMConfigurations config;
    private String gatewayFileExtension = ".bal";
    private String endpointConfigName = "endpoint";

    public APIGatewayPublisherImpl() {
        config = ServiceReferenceHolder.getInstance().getAPIMConfiguration();
    }

    /**
     * Publishing API configuration artifacts to the gateway
     *
     * @param api API model
     * @return is publishing success
     */
    @Override
    public boolean publishToGateway(API api) throws GatewayException {
        try {
            String gatewayConfig = api.getGatewayConfig();
            String gwHome = System.getProperty("gwHome");
            //TODO: remove temp fix to ignore gateway home
            if (gwHome == null) {
                gwHome = System.getProperty("carbon.home");
            }
            String defaultConfig = null;
            if (api.isDefaultVersion()) {
                //change the context name without version
                String newContext = "@http:BasePath(\"/" + api.getContext() + "\")";
                defaultConfig = gatewayConfig.replaceAll("@http:BasePath\\(\"(.)*\"\\)", newContext);
                //set the service name with api name only
                String newServiceName = "service " + api.getName() + " {";
                defaultConfig = defaultConfig
                        .replaceAll("service( )*" + api.getName() + "_[0-9]*( )*\\{", newServiceName);
            }
            if (gwHome == null) {
                // create the message to send
                GatewayConfigDTO dto = new GatewayConfigDTO();
                dto.setType("api");
                dto.setApiName(api.getName());
                dto.setContext(api.getContext());
                dto.setVersion(api.getVersion());
                dto.setCreator(api.getCreatedBy());
                dto.setConfig(gatewayConfig);
                publishMessage(dto); //publishing versioned API
                if (api.isDefaultVersion()) {
                    //publishing default API
                    publishMessage(dto);
                }
            } else {
                saveApi(api, gwHome, gatewayConfig, false);
                if (api.isDefaultVersion()) {
                    saveApi(api, gwHome, defaultConfig, true);
                }
            }
            return true;
        } catch (JMSException e) {
            log.error("Error deploying API configuration for API " + api.getName(), e);
            throw new GatewayException("Error deploying API configuration for API " + api.getName(),
                    ExceptionCodes.GATEWAY_EXCEPTION);
        } catch (URLSyntaxException e) {
            log.error("Error deploying API configuration for API " + api.getName(), e);
            throw new GatewayException("Error generating API configuration for API " + api.getName(),
                    ExceptionCodes.GATEWAY_EXCEPTION);
        }
    }

    /**
     * Publishing the API config to gateway
     *
     * @param dto GatewayConfigDTO to be published
     * @throws JMSException       if JMS issue is occurred
     * @throws URLSyntaxException If connection String is invalid
     */
    private void publishMessage(GatewayConfigDTO dto) throws JMSException, URLSyntaxException {
        // create connection factory
        TopicConnectionFactory connFactory = new AMQConnectionFactory(
                getTCPConnectionURL(config.getUsername(), config.getPassword()));
        TopicConnection topicConnection = connFactory.createTopicConnection();
        topicConnection.start();
        TopicSession topicSession = topicConnection.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
        // Send message
        Topic topic = topicSession.createTopic(config.getTopicName());

        TextMessage textMessage = topicSession.createTextMessage(new Gson().toJson(dto));
        TopicPublisher topicPublisher = topicSession.createPublisher(topic);
        topicPublisher.publish(textMessage);

        topicPublisher.close();
        topicSession.close();
        topicConnection.stop();
        topicConnection.close();
    }

    /**
     * Get connection config
     *
     * @param username username
     * @param password password
     * @return connection string
     */
    private String getTCPConnectionURL(String username, String password) {
        // amqp://{username}:{password}@carbon/carbon?brokerlist='tcp://{hostname}:{port}'
        return new StringBuffer().append("amqp://").append(username).append(":").append(password).append("@")
                .append(config.getCarbonClientId()).append("/")
                .append(config.getCarbonVirtualHostName()).append("?brokerlist='tcp://")
                .append(config.getTopicServerHost()).append(":")
                .append(config.getTopicServerPort()).append("'").toString();

    }

    /**
     * Save API into FS
     *
     * @param api     API object
     * @param gwHome  path of the gateway
     * @param content API config
     */
    private void saveApi(API api, String gwHome, String content, boolean isDefaultApi) {
        String deploymentDirPath = gwHome + File.separator + config.getGatewayPackageNamePath();
        File deploymentDir = new File(deploymentDirPath);
        if (!deploymentDir.exists()) {
            log.info("Creating deployment dir in: " + deploymentDirPath);
            boolean created = deploymentDir.mkdirs();
            if (!created) {
                log.error("Error creating directory: " + deploymentDirPath);
            }
        }

        String path;
        if (isDefaultApi) {
            path = deploymentDirPath + File.separator + api.getName() + gatewayFileExtension;
        } else {
            path = deploymentDirPath + File.separator + api.getName() + '_' + api.getVersion() + gatewayFileExtension;
        }
        Writer writer = null;
        PrintWriter printWriter = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(path), "UTF-8");
            printWriter = new PrintWriter(writer);
            printWriter.println(content);
        } catch (IOException e) {
            log.error("Error saving API configuration in " + path, e);
        } finally {
            try {
                if (printWriter != null) {
                    printWriter.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                log.error("Error closing connections", e);
            }
        }
    }

    /**
     * Publishing Endpoint configuration artifacts to the gateway
     *
     * @param config endpoint config
     * @return is publishing success
     */
    @Override
    public boolean publishEndpointConfigToGateway(String config) throws GatewayException {
        try {
            String gwHome = System.getProperty("gwHome");

            //TODO: remove temp fix to ignore gateway home
            if (gwHome == null) {
                gwHome = System.getProperty("carbon.home");
            }

            if (gwHome == null) {
                GatewayConfigDTO dto = new GatewayConfigDTO();
                dto.setType("endpoint");
                dto.setApiName(endpointConfigName);
                dto.setConfig(config);
                publishMessage(dto); //TODO publish endpoint configs correctly
            } else {
                saveEndpointConfig(gwHome, config);
            }
            return true;
        } catch (JMSException e) {
            log.error("Error deploying configuration for " + endpointConfigName, e);
            throw new GatewayException("Template " + "resources" + File.separator + "template.xml not Found",
                    ExceptionCodes.GATEWAY_EXCEPTION);
        } catch (URLSyntaxException e) {
            log.error("Error deploying configuration for " + endpointConfigName, e);
            throw new GatewayException("Error deploying configuration for " + endpointConfigName,
                    ExceptionCodes.GATEWAY_EXCEPTION);
        }
    }

    /**
     * Save config into FS
     *
     * @param gwHome  path of the gateway
     * @param content endpoint config
     */
    private void saveEndpointConfig(String gwHome, String content) {
        String deploymentDirPath = gwHome + File.separator + config.getGatewayPackageNamePath();
        File deploymentDir = new File(deploymentDirPath);
        if (!deploymentDir.exists()) {
            log.info("Creating deployment dir in: " + deploymentDirPath);
            boolean created = deploymentDir.mkdirs();
            if (!created) {
                log.error("Error creating directory: " + deploymentDirPath);
            }
        }

        String path = deploymentDirPath + File.separator + endpointConfigName + gatewayFileExtension;
        Writer writer = null;
        PrintWriter printWriter = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(path), "UTF-8");
            printWriter = new PrintWriter(writer);
            printWriter.println(content);
        } catch (IOException e) {
            log.error("Error saving endpoint configuration in " + path, e);
        } finally {
            try {
                if (printWriter != null) {
                    printWriter.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                log.error("Error closing connections", e);
            }
        }
    }
}
