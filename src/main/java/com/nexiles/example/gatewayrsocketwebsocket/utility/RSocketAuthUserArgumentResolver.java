package com.nexiles.example.gatewayrsocketwebsocket.utility;

import com.nexiles.example.gatewayrsocketwebsocket.config.SecurityConstants;
import com.nexiles.example.gatewayrsocketwebsocket.pojo.RSocketUser;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.reactive.HandlerMethodArgumentResolver;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Custom user implementation resolving.
 *
 * Obtained from {@link AuthenticationPrincipalArgumentResolver}
 */
public class RSocketAuthUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return findMethodAnnotation(RSocketAuthUser.class, parameter) != null;
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter, Message<?> message) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap((a) -> {
                    RSocketUser user = resolvePrincipal(a.getPrincipal());
                    return Mono.justOrEmpty(user);
                });
    }

    @SuppressWarnings("unchecked")
    private RSocketUser resolvePrincipal(Object principal) {

        if (principal instanceof User) {
            final User user = (User) principal;
            return new RSocketUser(user.getUsername(), SecurityUtility.Provider.SpringSecurity, new HashSet<>(user.getAuthorities()));
        } else if (principal instanceof Jwt) {
            final Jwt user = (Jwt) principal;

            final Map<String, Object> claims = user.getClaims();

            final String userName = (String) claims.get(SecurityConstants.JWT_NAME_CLAIM);

            final Map<String, Object> realmAccessMap = (Map<String, Object>) claims.get(SecurityConstants.JWT_REALM_ACCESS_CLAIM);
            final List<String> realmAccess = (List<String>) realmAccessMap.get(SecurityConstants.JWT_ROLES_CLAIM);
            final String[] prefixedRoles = realmAccess.stream().map(role -> SecurityConstants.ROLE_PREFIX + role).toArray(String[]::new);
            final List<GrantedAuthority> grantedAuthorities = AuthorityUtils.createAuthorityList(prefixedRoles);

            return new RSocketUser(userName, SecurityUtility.Provider.KeyCloak, new HashSet<>(grantedAuthorities));
        }

        return null;
    }

    @SuppressWarnings("SameParameterValue")
    private <T extends Annotation> T findMethodAnnotation(Class<T> annotationClass, MethodParameter parameter) {
        T annotation = parameter.getParameterAnnotation(annotationClass);
        if (annotation != null) {
            return annotation;
        }
        Annotation[] annotationsToSearch = parameter.getParameterAnnotations();
        for (Annotation toSearch : annotationsToSearch) {
            annotation = AnnotationUtils.findAnnotation(toSearch.annotationType(), annotationClass);
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }

}
