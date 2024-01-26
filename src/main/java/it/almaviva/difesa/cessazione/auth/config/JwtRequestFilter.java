package it.almaviva.difesa.cessazione.auth.config;

import io.jsonwebtoken.ExpiredJwtException;
import it.almaviva.difesa.cessazione.auth.constant.Constant;
import it.almaviva.difesa.cessazione.auth.enums.StatusEnum;
import it.almaviva.difesa.cessazione.auth.exception.InvalidTokenException;
import it.almaviva.difesa.cessazione.auth.service.UserTokenService;
import it.almaviva.difesa.cessazione.auth.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static it.almaviva.difesa.cessazione.auth.config.InterceptorConfiguration.EXCLUDED_ABSOLUTE_PATHS;
import static it.almaviva.difesa.cessazione.auth.config.InterceptorConfiguration.EXCLUDED_SPECIFIC_PATHS;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter implements Filter {

    final UserTokenService userTokenService;
    final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public JwtRequestFilter(UserTokenService userTokenService, JwtTokenUtil jwtTokenUtil) {
        this.userTokenService = userTokenService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI().replace(request.getContextPath(), "");

        log.debug("requestURI => " + request.getRequestURI());
        log.debug("servletPath => " + request.getServletPath());

        if (EXCLUDED_SPECIFIC_PATHS.contains(path) || EXCLUDED_ABSOLUTE_PATHS.stream().anyMatch(p -> path.startsWith(p.replace("/**", "")))) {
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getMethod().equalsIgnoreCase(RequestMethod.OPTIONS.name())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String requestTokenHeader = request.getHeader(Constant.AUTH_HEADER);


        if (requestTokenHeader == null) {
            log.error("Token Header in the request is not found");
            throw new InvalidTokenException(StatusEnum.NOT_SUPPLIED_TOKEN.getNameMessage(), HttpStatus.UNAUTHORIZED);
        }

        if (!requestTokenHeader.startsWith("Bearer ")) {
            log.error("Token Header in the request is not start with \"Bearer \"");
            throw new InvalidTokenException(StatusEnum.JWT_TOKEN_BEARER.getNameMessage(), HttpStatus.UNAUTHORIZED);
        }

        String jwtToken = requestTokenHeader.substring(7);
        String fiscalCode;

        try {
            fiscalCode = jwtTokenUtil.getUsername(jwtToken);
        } catch (ExpiredJwtException e) {
            log.error(e.getMessage());
            throw new InvalidTokenException(StatusEnum.JWT_TOKEN_EXPIRED_STATUS.getNameMessage(), HttpStatus.UNAUTHORIZED, List.of(e.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InvalidTokenException(e.getMessage(), HttpStatus.UNAUTHORIZED, List.of(e.getMessage()));
        }

        if (fiscalCode != null && (SecurityContextHolder.getContext().getAuthentication() == null || SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            var userDetails = this.userTokenService.loadUserByUsername(fiscalCode);

            if (Boolean.TRUE.equals(jwtTokenUtil.validateToken(jwtToken, userDetails))) {
                var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                throw new InvalidTokenException(StatusEnum.JWT_OR_USERNAME_INVALID.getNameMessage(), HttpStatus.UNAUTHORIZED, List.of("Token is not valid"));

            }
        }

        filterChain.doFilter(request, response);
    }

}
