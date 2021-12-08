package cn.flood.core.security.service;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;


import java.util.Collection;

/**
 * 权限判断工具类
 *
 * @author mmdai
 */
@Service(value = "mt")
public class PermissionService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public boolean hasPerm(String... permissions) {
		if (ArrayUtils.isEmpty(permissions)) {
			return false;
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return false;
		}
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		return authorities.stream()
				.map(GrantedAuthority::getAuthority)
				.filter(StringUtils::hasText)
				.anyMatch(x -> PatternMatchUtils.simpleMatch(permissions, x));
	}
}
