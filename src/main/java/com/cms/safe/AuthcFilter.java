package com.cms.safe;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.cms.service.UserService;

/**
 * 自定义登录认证filter, 登陆时增加一些操作
 * @author XieYu
 *
 */
public class AuthcFilter extends FormAuthenticationFilter{
	private final static Logger LOGGER = LoggerFactory.getLogger(AuthcFilter.class);
	
    /**
     * 登陆时增加统计成功或者失败的次数
     */
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
    	LOGGER.info("Begin Login");
        AuthenticationToken token = createToken(request, response);
        if (token == null) {
            String msg = "createToken method implementation returned null. A valid non-null AuthenticationToken " +
                    "must be created in order to execute a login attempt.";
            throw new IllegalStateException(msg);
        }
        try {
        	Subject subject = getSubject(request, response);
            subject.login(token);
            LOGGER.info("login success.[{}]",subject.getPrincipal());
            //登陆成功
            userService.updateSuccess((String)subject.getPrincipal());
            return onLoginSuccess(token, subject, request, response);
        } catch (AuthenticationException e) {
        	LOGGER.info("login fail");
        	//登陆失败
        	userService.updateFail((String) token.getPrincipal());
            return onLoginFailure(token, e, request, response);
        }
    }
    
    public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		boolean isAllowed = isAccessAllowed(request, response, mappedValue);
		//登录跳转
		/*if (isAllowed && isLoginRequest(request, response)) {
			try {
				issueSuccessRedirect(request, response);
			} catch (Exception e) {
				LOGGER.error("", e);
			}
			return false;
		}*/
        return isAllowed || onAccessDenied(request, response, mappedValue);
    }
/*    
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return super.isAccessAllowed(request, response, mappedValue) ||
                (!isLoginRequest(request, response) && isPermissive(mappedValue));
    }
    
    protected boolean isLoginRequest(ServletRequest request, ServletResponse response) {
        return pathsMatch(getLoginUrl(), request);
    }*/
    
    @Autowired
    private UserService userService;
}
