package org.wso2.carbon.apimgt.rest.api.core.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * UriTemplateDTO
 */
@javax.annotation.Generated(value = "org.wso2.maven.plugins.JavaMSF4JServerCodegen", date = "2017-03-31T16:40:30.481+05:30")
public class UriTemplateDTO   {
  @JsonProperty("uriTemplate")
  private String uriTemplate = null;

  @JsonProperty("httpVerb")
  private String httpVerb = null;

  @JsonProperty("authType")
  private String authType = null;

  @JsonProperty("policy")
  private String policy = null;

  public UriTemplateDTO uriTemplate(String uriTemplate) {
    this.uriTemplate = uriTemplate;
    return this;
  }

   /**
   * uri template of api. 
   * @return uriTemplate
  **/
  @ApiModelProperty(value = "uri template of api. ")
  public String getUriTemplate() {
    return uriTemplate;
  }

  public void setUriTemplate(String uriTemplate) {
    this.uriTemplate = uriTemplate;
  }

  public UriTemplateDTO httpVerb(String httpVerb) {
    this.httpVerb = httpVerb;
    return this;
  }

   /**
   * http verb of the uri template. 
   * @return httpVerb
  **/
  @ApiModelProperty(value = "http verb of the uri template. ")
  public String getHttpVerb() {
    return httpVerb;
  }

  public void setHttpVerb(String httpVerb) {
    this.httpVerb = httpVerb;
  }

  public UriTemplateDTO authType(String authType) {
    this.authType = authType;
    return this;
  }

   /**
   * auth type of uri tamplate. 
   * @return authType
  **/
  @ApiModelProperty(value = "auth type of uri tamplate. ")
  public String getAuthType() {
    return authType;
  }

  public void setAuthType(String authType) {
    this.authType = authType;
  }

  public UriTemplateDTO policy(String policy) {
    this.policy = policy;
    return this;
  }

   /**
   * policy of uri template. 
   * @return policy
  **/
  @ApiModelProperty(value = "policy of uri template. ")
  public String getPolicy() {
    return policy;
  }

  public void setPolicy(String policy) {
    this.policy = policy;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UriTemplateDTO uriTemplate = (UriTemplateDTO) o;
    return Objects.equals(this.uriTemplate, uriTemplate.uriTemplate) &&
        Objects.equals(this.httpVerb, uriTemplate.httpVerb) &&
        Objects.equals(this.authType, uriTemplate.authType) &&
        Objects.equals(this.policy, uriTemplate.policy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uriTemplate, httpVerb, authType, policy);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UriTemplateDTO {\n");
    
    sb.append("    uriTemplate: ").append(toIndentedString(uriTemplate)).append("\n");
    sb.append("    httpVerb: ").append(toIndentedString(httpVerb)).append("\n");
    sb.append("    authType: ").append(toIndentedString(authType)).append("\n");
    sb.append("    policy: ").append(toIndentedString(policy)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

