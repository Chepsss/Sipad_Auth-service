package it.almaviva.difesa.cessazione.auth.service.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.almaviva.difesa.cessazione.auth.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class BaseRestClient {

    private static final RestTemplate restTemplate = new RestTemplate();

    private static <T> ResponseEntity<T> callRestService(String url,
                                                         Object payload,
                                                         HttpMethod method,
                                                         HttpHeaders headers,
                                                         Class<T> responseTypeClazz,
                                                         ParameterizedTypeReference<T> responseTypeParam) {
        ResponseEntity<T> out;
        if (responseTypeParam != null)
            out = restTemplate.exchange(url, method, new HttpEntity<>(payload, headers), responseTypeParam);
        else
            out = restTemplate.exchange(url, method, new HttpEntity<>(payload, headers), responseTypeClazz);
        return out;
    }

    public static <T> T callPostService(String url, Object payload, HttpHeaders headers, Class<T> responseTypeClazz, ParameterizedTypeReference<T> responseTypeParam) {
        try {
            ResponseEntity<T> t = callRestService(url, payload, HttpMethod.POST, headers, responseTypeClazz, responseTypeParam);
            return t.getBody();
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            log.error(">>>>>>>>>>>> ERROR in callPostService" +
                    "\ninput: " + getObjectToString(payload) +
                    "\nurl: " + url +
                    "\n Error: ", e);
            throw new ServiceException(e.getLocalizedMessage(), e.getStatusCode());
        }
    }

    public static <T> T callGetService(String url, HttpHeaders headers, Class<T> responseTypeClazz, ParameterizedTypeReference<T> responseTypeParam) {
        try {
            ResponseEntity<T> t = callRestService(url, null, HttpMethod.GET, headers, responseTypeClazz, responseTypeParam);
            return t.getBody();
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            log.error(">>>>>>>>>>>> ERROR in callGetService" +
                    "\nurl: " + url +
                    "\n Error: ", e);
            throw new ServiceException(e.getLocalizedMessage(), e.getStatusCode());
        }
    }

    private static String getObjectToString(Object payload) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            return mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error(">>>>>>> ERROR in getObjectToString ", e);
            return "";
        }
    }

}
