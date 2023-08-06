package store.management.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import store.management.enums.Permission;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class PermissionAspect {

    @AfterReturning("@annotation(requireAllPermissions)")
    public void checkAllPermissions(JoinPoint joinPoint, RequireAllPermissions requireAllPermissions) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> list = authentication.getAuthorities();
        Set<String> authoritySet = list.stream().map(a -> a.getAuthority()).collect(Collectors.toSet());
        for (Permission permission : requireAllPermissions.value()) {
            if (!authoritySet.contains(permission.toString())) {
                throw new AccessDeniedException("You do not have permissions to perform this action");
            }
        }
    }

    @AfterReturning("@annotation(requireAnyPermission)")
    public void checkAnyPermission(JoinPoint joinPoint, RequireAnyPermission requireAnyPermission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> list = authentication.getAuthorities();
        Set<String> authoritySet = list.stream().map(a -> a.getAuthority()).collect(Collectors.toSet());
        for (Permission permission : requireAnyPermission.value()) {
            if (authoritySet.contains(permission.toString())) {
                return;
            }
        }
        throw new AccessDeniedException("You do not have permissions to perform this action");
    }
}
