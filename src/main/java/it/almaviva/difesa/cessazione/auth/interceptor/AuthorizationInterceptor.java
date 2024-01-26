package it.almaviva.difesa.cessazione.auth.interceptor;

import it.almaviva.difesa.cessazione.auth.model.mapper.UserTokenDTO;
import it.almaviva.difesa.cessazione.auth.service.UserTokenService;
import it.almaviva.difesa.cessazione.auth.util.RestUtilsMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static it.almaviva.difesa.cessazione.auth.constant.Constant.AUTH_HEADER;

public class AuthorizationInterceptor implements HandlerInterceptor {

    @Autowired
    private UserTokenService userTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return true;
// TODO verificare il perchè e il funzionamento di questo interceptor
//        String authHeader = request.getHeader(AUTH_HEADER);
//        if (authHeader == null) {
//            return false;
//        }
//        var gedoccTokenRequest = authHeader.substring(7);
//
//        String uuid = RestUtilsMethod.getClaimValueByHeaderAndClaimKey(request, AUTH_HEADER, "uuid");
//
//        if (uuid == null) {
//            return false;
//        }
//
//        UserTokenDTO tokenSaved = userTokenService.findTokenByUuid(uuid);
//
//        return tokenSaved.getToken() != null && tokenSaved.getToken().equals(gedoccTokenRequest);
    }
}
