/***********************************************************************************************************************
 *
 *  *
 *  *   Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  *
 *  *   WSO2 Inc. licenses this file to you under the Apache License,
 *  *   Version 2.0 (the "License"); you may not use this file except
 *  *   in compliance with the License.
 *  *   You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing,
 *  *  software distributed under the License is distributed on an
 *  *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  *  KIND, either express or implied.  See the License for the
 *  *  specific language governing permissions and limitations
 *  *  under the License.
 *  *
 *
 */

package org.wso2.carbon.apimgt.rest.api.core.utils;


import org.wso2.carbon.apimgt.core.models.Label;
import org.wso2.carbon.apimgt.core.models.SubscriptionValidationData;
import org.wso2.carbon.apimgt.rest.api.core.dto.LabelDTO;
import org.wso2.carbon.apimgt.rest.api.core.dto.SubscriptionDTO;
import org.wso2.carbon.apimgt.rest.api.core.dto.SubscriptionListDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MappingUtil {

    /**
     * This method converts List of SubscriptionValidationData into SubscriptionListDTO object.
     *
     * @param subscriptionValidationData List of Subscription Validation Data
     * @return subscriptionListDTO
     */
    public static SubscriptionListDTO convertToSubscriptionListDto(
            List<SubscriptionValidationData> subscriptionValidationData) {
        SubscriptionListDTO subscriptionListDTO = new SubscriptionListDTO();
        for (SubscriptionValidationData subscriptionData : subscriptionValidationData) {
            SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
            subscriptionDTO.setApiName(subscriptionData.getApiName());
            subscriptionDTO.setApiContext(subscriptionData.getApiContext());
            subscriptionDTO.setApiVersion(subscriptionData.getApiVersion());
            subscriptionDTO.setApiProvider(subscriptionData.getApiProvider());
            subscriptionDTO.setConsumerKey(subscriptionData.getConsumerKey());
            subscriptionDTO.setSubscriptionPolicy(subscriptionData.getSubscriptionPolicy());
            subscriptionDTO.setApplicationName(subscriptionData.getApplicationName());
            subscriptionDTO.setApplicationOwner(subscriptionData.getApplicationOwner());
            subscriptionDTO.setKeyEnvType(subscriptionData.getKeyEnvType());
            subscriptionListDTO.addListItem(subscriptionDTO);
        }
        return subscriptionListDTO;
    }

    /**
     * Converts labelDTOs into labels
     *
     * @param labelDTOs List of LabelDTOs
     * @return List of Labels
     */
    public static List<Label> convertToLabels(List<LabelDTO> labelDTOs) {
        List<Label> labels = new ArrayList<>();
        for (LabelDTO labelDTO : labelDTOs) {
            String labelId = UUID.randomUUID().toString();
            Label label = new Label.Builder().id(labelId).name(labelDTO.getName()).accessUrls(labelDTO.getAccessUrls())
                    .build();
            labels.add(label);
        }
        return labels;
    }
}
