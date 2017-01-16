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

package org.wso2.carbon.apimgt.rest.api.publisher.utils;


import org.wso2.carbon.apimgt.core.models.API;
import org.wso2.carbon.apimgt.core.models.Application;
import org.wso2.carbon.apimgt.core.models.BusinessInformation;
import org.wso2.carbon.apimgt.core.models.CorsConfiguration;
import org.wso2.carbon.apimgt.core.models.DocumentInfo;
import org.wso2.carbon.apimgt.core.models.Subscription;
import org.wso2.carbon.apimgt.rest.api.publisher.dto.APIDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.dto.APIInfoDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.dto.APIListDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.dto.API_businessInformationDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.dto.API_corsConfigurationDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.dto.ApplicationDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.dto.DocumentDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.dto.DocumentListDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.dto.SubscriptionDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.dto.SubscriptionListDTO;

import java.util.ArrayList;
import java.util.List;

public class MappingUtil {

    /**
     * This method converts the API Object from models into APIDTO object.
     *
     * @param api
     * @return
     */
    public static APIDTO toAPIDto(API api) {
        APIDTO apidto = new APIDTO();
        apidto.setId(api.getId());
        apidto.setName(api.getName());
        apidto.version(api.getVersion());
        apidto.setContext(api.getContext());
        apidto.setDescription(api.getDescription());
        apidto.setApiDefinition(api.getApiDefinition());
        apidto.setIsDefaultVersion(api.isDefaultVersion());
        apidto.setVisibility(APIDTO.VisibilityEnum.valueOf(api.getVisibility().toString()));
        apidto.setResponseCaching(Boolean.toString(api.isResponseCachingEnabled()));
        apidto.setCacheTimeout(api.getCacheTimeout());
        apidto.setVisibleRoles(api.getVisibleRoles());
        apidto.setLifeCycleStatus(api.getLifeCycleStatus());
        apidto.setTags(api.getTags());
        apidto.setTransport(api.getTransport());
        api.getPolicies().forEach(apidto::addPoliciesItem);
        BusinessInformation businessInformation = api.getBusinessInformation();
        API_businessInformationDTO apiBusinessInformationDTO = new API_businessInformationDTO();
        apiBusinessInformationDTO.setBusinessOwner(businessInformation.getBusinessOwner());
        apiBusinessInformationDTO.setBusinessOwnerEmail(businessInformation.getBusinessOwnerEmail());
        apiBusinessInformationDTO.setTechnicalOwner(businessInformation.getTechnicalOwner());
        apiBusinessInformationDTO.setTechnicalOwnerEmail(businessInformation.getTechnicalOwnerEmail());
        apidto.setBusinessInformation(apiBusinessInformationDTO);
        CorsConfiguration corsConfiguration = api.getCorsConfiguration();
        API_corsConfigurationDTO apiCorsConfigurationDTO = new API_corsConfigurationDTO();
        apiCorsConfigurationDTO.setAccessControlAllowCredentials(corsConfiguration.isAllowCredentials());
        apiCorsConfigurationDTO.setAccessControlAllowHeaders(corsConfiguration.getAllowHeaders());
        apiCorsConfigurationDTO.setAccessControlAllowMethods(corsConfiguration.getAllowMethods());
        apiCorsConfigurationDTO.setAccessControlAllowOrigins(corsConfiguration.getAllowOrigins());
        apiCorsConfigurationDTO.setCorsConfigurationEnabled(corsConfiguration.isEnabled());
        apidto.setCorsConfiguration(apiCorsConfigurationDTO);

        return apidto;
    }

    /**
     * This method converts the API model object from the DTO object.
     *
     * @param apidto
     * @return
     */
    public static API.APIBuilder toAPI(APIDTO apidto) {
        BusinessInformation businessInformation = new BusinessInformation();
        API_businessInformationDTO apiBusinessInformationDTO = apidto.getBusinessInformation();
        businessInformation.setBusinessOwner(apiBusinessInformationDTO.getBusinessOwner());
        businessInformation.setBusinessOwnerEmail(apiBusinessInformationDTO.getBusinessOwnerEmail());
        businessInformation.setTechnicalOwner(apiBusinessInformationDTO.getTechnicalOwner());
        businessInformation.setTechnicalOwnerEmail(apiBusinessInformationDTO.getTechnicalOwnerEmail());

        API_corsConfigurationDTO apiCorsConfigurationDTO = apidto.getCorsConfiguration();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(apiCorsConfigurationDTO.getAccessControlAllowCredentials());
        corsConfiguration.setAllowHeaders(apiCorsConfigurationDTO.getAccessControlAllowHeaders());
        corsConfiguration.setAllowMethods(apiCorsConfigurationDTO.getAccessControlAllowMethods());
        corsConfiguration.setAllowOrigins(apiCorsConfigurationDTO.getAccessControlAllowOrigins());
        corsConfiguration.setEnabled(apiCorsConfigurationDTO.getCorsConfigurationEnabled());

        API.APIBuilder apiBuilder = new API.APIBuilder(apidto.getProvider(), apidto.getName(), apidto.getVersion()).
                id(apidto.getId()).
                context(apidto.getContext()).
                description(apidto.getDescription()).
                apiDefinition(new StringBuilder(apidto.getApiDefinition())).
                lifeCycleStatus(apidto.getLifeCycleStatus()).
                visibleRoles(apidto.getVisibleRoles()).
                visibility(API.Visibility.valueOf(apidto.getVisibility().toString())).
                policies(apidto.getPolicies()).
                tags(apidto.getTags()).
                transport(apidto.getTransport()).
                cacheTimeout(apidto.getCacheTimeout()).
                isResponseCachingEnabled(Boolean.valueOf(apidto.getResponseCaching())).
                policies(apidto.getPolicies()).
                businessInformation(businessInformation).
                corsConfiguration(corsConfiguration);
        return apiBuilder;
    }

    /**
     * Converts {@link API} List to an {@link APIInfoDTO} List.
     *
     * @param apiSummaryList
     * @return
     */
    private static List<APIInfoDTO> toAPIInfo(List<API> apiSummaryList) {
        List<APIInfoDTO> apiInfoList = new ArrayList<APIInfoDTO>();
        for (API apiSummary : apiSummaryList) {
            APIInfoDTO apiInfo = new APIInfoDTO();
            apiInfo.setId(apiSummary.getId());
            apiInfo.setContext(apiSummary.getContext());
            apiInfo.setDescription(apiSummary.getDescription());
            apiInfo.setName(apiSummary.getName());
            apiInfo.setProvider(apiSummary.getProvider());
            apiInfo.setLifeCycleStatus(apiSummary.getLifeCycleStatus());
            apiInfo.setVersion(apiSummary.getVersion());
            apiInfoList.add(apiInfo);
        }
        return apiInfoList;
    }

    /**
     * Converts {@link List<API>} to {@link APIListDTO} DTO.
     *
     * @param apisResult
     * @return
     */
    public static APIListDTO toAPIListDTO(List<API> apisResult) {
        APIListDTO apiListDTO = new APIListDTO();
        apiListDTO.setCount(apisResult.size());
        // apiListDTO.setNext(next);
        // apiListDTO.setPrevious(previous);
        apiListDTO.setList(toAPIInfo(apisResult));
        return apiListDTO;
    }

    /**
     * this  method convert Model object into Dto
     * @param documentInfo
     * @return
     */
    public static DocumentDTO toDocumentDTO(DocumentInfo documentInfo) {
        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setName(documentInfo.getName());
        documentDTO.setDocumentId(documentInfo.getId());
        documentDTO.setOtherTypeName(documentInfo.getOtherType());
        documentDTO.setSourceType(DocumentDTO.SourceTypeEnum.fromValue(documentInfo.getSourceType().getType()));
        documentDTO.setSourceUrl(documentInfo.getSourceURL());
        documentDTO.setSummary(documentInfo.getSummary());
        documentDTO.setVisibility(DocumentDTO.VisibilityEnum.fromValue(documentInfo.getVisibility().toString()));
        documentDTO.setType(DocumentDTO.TypeEnum.fromValue(documentInfo.getType().toString()));
        return documentDTO;
    }

    /**
     * This mrthod convert the Dto object into Model
     * @param documentDTO
     * @return
     */
    public  static DocumentInfo toDocumentInfo(DocumentDTO documentDTO){
        return new DocumentInfo.Builder().
                id(documentDTO.getDocumentId()).
                summary(documentDTO.getSummary()).
                name(documentDTO.getName()).
                otherType(documentDTO.getOtherTypeName()).
                sourceType(DocumentInfo.SourceType.valueOf(documentDTO.getSourceType().toString())).
                sourceURL(documentDTO.getSourceUrl()).
                type(DocumentInfo.DocType.valueOf(documentDTO.getType().toString())).
                visibility(DocumentInfo.Visibility.valueOf(documentDTO.getVisibility().toString())).build();
    }

    /**
     * This method converts documentInfoResults to documentListDTO
     * @param documentInfoResults
     * @return
     */
    public static DocumentListDTO toDocumentListDTO(List<DocumentInfo> documentInfoResults){
        DocumentListDTO documentListDTO = new DocumentListDTO();
        for (DocumentInfo documentInfo : documentInfoResults){
            documentListDTO.addListItem(toDocumentDTO(documentInfo));
        }
        return documentListDTO;
    }



    /**
     * This method convert {@link org.wso2.carbon.apimgt.core.models.Application} to {@link ApplicationDTO}
     * return
     */
    public static ApplicationDTO toApplicationDto(Application application){
        ApplicationDTO applicationDTO = new ApplicationDTO();
        applicationDTO.setApplicationId(application.getId());
        applicationDTO.setDescription(application.getDescription());
        applicationDTO.setGroupId(application.getGroupId());
        applicationDTO.setName(application.getName());
        applicationDTO.setSubscriber(application.getCreatedUser());
        applicationDTO.setThrottlingTier(application.getTier());
        return applicationDTO;
    }

    /**
     * Converts List<{@link Subscription}> into {@link SubscriptionListDTO}</>
     * @param subscriptionList list of {@link Subscription}
     * @param limit no of items to return
     * @param offset
     * @return
     */
    public static SubscriptionListDTO fromSubscriptionListToDTO(List<Subscription> subscriptionList, Integer limit,
                                                                Integer offset) {
        SubscriptionListDTO subscriptionListDTO = new SubscriptionListDTO();
        for (Subscription subscription : subscriptionList) {
            subscriptionListDTO.addListItem(fromSubscription(subscription));
        }
        return subscriptionListDTO;
    }

    /**
     * Converts {@link Subscription} to {@link SubscriptionDTO}
     * @param subscription
     * @return
     */
    public static SubscriptionDTO fromSubscription(Subscription subscription) {
        SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
        subscriptionDTO.setApplicationId(subscription.getId());
        subscriptionDTO.setLifeCycleStatus(
                SubscriptionDTO.LifeCycleStatusEnum.fromValue(subscription.getStatus().toString()));
        subscriptionDTO.setApplicationId(subscription.getApplication().getId());
        subscriptionDTO.setApiIdentifier(subscription.getApi().getId());
        subscriptionDTO.setPolicy(subscription.getSubscriptionTier());
        return subscriptionDTO;
    }
}
