package org.crazycake.shiroredis;

import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This realm is only for tutorial
 * @author Alex Yang
 *
 */
public class ExampleRealm extends AuthorizingRealm {

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo();
		// set authorization for tutorial
		List<String> roles = new ArrayList<String>();
		roles.add("schwartz");
		authInfo.addRoles(roles);
		return authInfo;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken)token;
		UserInfo userInfo = new UserInfo();
		userInfo.setId(ThreadLocalRandom.current().nextInt(1, 100));
		userInfo.setUsername(usernamePasswordToken.getUsername());
		userInfo.setAge(23);
		// Expect password is "123456"
		return new SimpleAuthenticationInfo(userInfo, "123456", getName());
	}
	
	@PostConstruct
	public void initCredentialsMatcher() {
		setCredentialsMatcher(new SimpleCredentialsMatcher());
	}

}
