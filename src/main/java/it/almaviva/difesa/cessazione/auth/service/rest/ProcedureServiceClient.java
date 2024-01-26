package it.almaviva.difesa.cessazione.auth.service.rest;

import it.almaviva.difesa.cessazione.auth.constant.Constant;
import it.almaviva.difesa.cessazione.auth.model.mapper.response.ProcedureDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.List;

@Service
public class ProcedureServiceClient extends BaseRestClient {

    private static final String PREFIX_BEARER = "Bearer %s";
    private static final String GET_PROCEDURES_NOT_CLOSED = "%s/procedure/proceduresNotClosedByIdAssignedTo/{id}";

    @Value("${application.procedure-service.baseurl}")
    String procServiceBaseUrl;

    public List<ProcedureDTO> getProceduresNotClosed(Long idAssignedTo, String token) {
        UriTemplate uriTemplate = new UriTemplate(String.format(GET_PROCEDURES_NOT_CLOSED, procServiceBaseUrl));
        URI uri = uriTemplate.expand(idAssignedTo);
        return callGetService(uri.toString(), setHeaders(token), null, new ParameterizedTypeReference<>() {
        });
    }

    private HttpHeaders setHeaders(String token) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add(Constant.AUTH_HEADER, String.format(PREFIX_BEARER, token));
        return new HttpHeaders(multiValueMap);
    }

}
