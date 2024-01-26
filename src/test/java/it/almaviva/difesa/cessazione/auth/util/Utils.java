package it.almaviva.difesa.cessazione.auth.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import it.almaviva.difesa.cessazione.auth.model.CustomUserDetail;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for testing REST controllers.
 */
public class Utils {


    private static final String secret = "2be78f5834a02f41cafb28abe017eb247fa999a3286cb8786277a5ea6fd9fe0910a684bff2e5111e08a7c9841fbde2e61d070dbbe21965beb3e4124597db9380";
    private static final Long expiration = 3600L;

    public static final String UUID_TO_TEST = "fe8a03d7-6495-4231-9843-8ee2f5282620";
    public static final String RANKDESC_TO_TEST = "Capitano";
    public static final String FISCAL_CODE = "LAINRN03E58H501K";

    private static final ObjectMapper mapper = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    /**
     * Convert an object to JSON String.
     *
     * @param object the object to convert.
     * @return the JSON String.
     * @throws  IOException
     */
    public static String convertObjectToJsonString(Object object) throws IOException {
        return mapper.writeValueAsString(object);
    }

    /**
     * Convert an object to JSON byte array.
     *
     * @param object the object to convert.
     * @return the JSON byte array.
     * @throws IOException
     */
    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        return mapper.writeValueAsBytes(object);
    }

    /**
     * Get Token from Custom User Detail.
     *
     * @param customUserDetail the CustomUserDetail.
     * @return the String token.
     */
    public static String getToken(CustomUserDetail customUserDetail) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("uuid", customUserDetail.getUuid());
        claims.put("fiscalCode", customUserDetail.getUsername());
        claims.put("firstName", customUserDetail.getFirstName());
        claims.put("lastName", customUserDetail.getLastName());
        claims.put("rank", customUserDetail.getRankDescription());
        claims.put("auth", customUserDetail.getAuthorities());
        claims.put("employeeId", customUserDetail.getEmployeeId());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(customUserDetail.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date((new Date()).getTime() + expiration * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    private static SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
