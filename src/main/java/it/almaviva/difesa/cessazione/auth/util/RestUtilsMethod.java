package it.almaviva.difesa.cessazione.auth.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import it.almaviva.difesa.cessazione.auth.enums.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static it.almaviva.difesa.cessazione.auth.constant.Constant.*;

@Component
@Slf4j
public class RestUtilsMethod {

    private RestUtilsMethod() {
    }

    public static String getRoleCodeFromAuthsOrElseThrow(List<String> auths) {
        return auths.stream()
                .filter(s -> s.startsWith(ROLE_PREFIX))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, StatusEnum.ROLE_NOT_FINDABLE_ID.getNameMessage()));
    }

    public static String getRoleCodeFromAuthsOrElseNull(List<String> auths) {
        return auths.stream()
                .filter(s -> s.startsWith(ROLE_PREFIX))
                .findFirst()
                .orElse(null);
    }

    public static Long getUserEmployeeIdFromClaims(HttpServletRequest request) {
        var authorId = Long.valueOf(Objects.requireNonNull(RestUtilsMethod.getClaimValueByHeaderAndClaimKey(request, AUTH_HEADER, "employeeId")));

        if (ObjectUtils.isEmpty(authorId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, StatusEnum.DISSOCATED_TOKEN_ID_USER.getNameMessage());
        }

        return authorId;
    }

    public static String getClaimValueByHeaderAndClaimKey(HttpServletRequest request, String header, String key) {
        String gedoccTokenRequest = request.getHeader(header);

        if (!ObjectUtils.isEmpty(gedoccTokenRequest)) {
            return getClaimsAsText(gedoccTokenRequest, key);
        }

        return null;
    }

    public static String getClaimValueFromTokenByKey(String token, String key) {
        return getClaimsAsText(token, key);
    }

    private static String getClaimsAsText(String token, String key) {
        String[] chunks = token.split("\\.");
        var decoder = Base64.getDecoder();
        var payload = new String(decoder.decode(chunks[1]));
        var objectMapper = new ObjectMapper();
        try {
            var jsonNode = objectMapper.readTree(payload);
            return jsonNode.get(key).asText();
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static List<String> getClaimsAsTextList(String token, String key) {
        String[] chunks = token.split("\\.");
        var decoder = Base64.getDecoder();
        var payload = new String(decoder.decode(chunks[1]));
        var objectMapper = new ObjectMapper();
        try {
            var arrayNode = objectMapper.readTree(payload).get(key);
            ObjectReader reader = objectMapper.readerFor(new TypeReference<List<String>>() {
            });
            return reader.readValue(arrayNode);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public static void streamResponseFile(HttpServletResponse response, byte[] data, String fileName, String contentType) throws IOException {
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "inline; filename=" + fileName + ";");
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }

    public static String getRoleCodeOrElseThrow(HttpServletRequest request) {
        var auths = getClaimsAsTextList(request.getHeader(AUTH_HEADER), AUTH);
        return getRoleCodeFromAuthsOrElseThrow(auths);
    }

    public static String getRoleCodeOrElseNull(HttpServletRequest request) {
        var auths = getClaimsAsTextList(request.getHeader(AUTH_HEADER), AUTH);
        return getRoleCodeFromAuthsOrElseNull(auths);
    }
}
