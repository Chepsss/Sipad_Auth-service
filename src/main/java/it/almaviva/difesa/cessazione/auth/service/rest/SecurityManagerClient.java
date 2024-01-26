package it.almaviva.difesa.cessazione.auth.service.rest;

import it.almaviva.difesa.cessazione.auth.service.rest.request.PreAuthInfoRequest;
import it.almaviva.difesa.cessazione.auth.service.rest.response.PostAuthResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

@Service
@Slf4j
public class SecurityManagerClient extends BaseRestClient {

    private static final String GET_AUTHORIZATION = "%s/authorize";

    @Value("${application.security-manager-api.baseurl}")
    private String securityManagerUrl;

    public PostAuthResponse getAuthorization(String fiscalCode) {
        UriTemplate uriTemplate = new UriTemplate(String.format(GET_AUTHORIZATION, securityManagerUrl));
        PreAuthInfoRequest req = new PreAuthInfoRequest();
        req.setFiscalCode(fiscalCode);
        return callPostService(uriTemplate.toString(), req, null, PostAuthResponse.class, null);
    }

}
