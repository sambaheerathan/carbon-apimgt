package org.wso2.carbon.apimgt.rest.api.store;

import javax.ws.rs.core.Response;
import org.wso2.msf4j.Request;

@javax.annotation.Generated(value = "org.wso2.maven.plugins.JavaMSF4JServerCodegen", date = "2017-04-07T10:04:16.863+05:30")
public abstract class TagsApiService {
    public abstract Response tagsGet(Integer limit
 ,Integer offset
 ,String accept
 ,String ifNoneMatch
 , Request request) throws NotFoundException;
}
