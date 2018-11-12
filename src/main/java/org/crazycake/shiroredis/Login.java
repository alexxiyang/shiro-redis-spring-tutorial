package org.crazycake.shiroredis;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Login Servlet. Adapted from shiro tutorial.
 * https://shiro.apache.org/tutorial.html#final-tutorial-class
 * @author Alex Yang
 */
public class Login extends HttpServlet {
	
	private static final transient Logger log = LoggerFactory.getLogger(Login.class);
	
	/**
	 * Use shiro-redis to handle session and cached
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
		Subject currentUser = SecurityUtils.getSubject();

        Session session = currentUser.getSession();
        checkSession(session);
        
        String username = request.getParameter("username");
        if (!currentUser.isAuthenticated()) {
            UsernamePasswordToken token = new UsernamePasswordToken(username, request.getParameter("password"));
            try {
                currentUser.login(token);
            } catch (UnknownAccountException uae) {
                log.info("There is no user with username of " + token.getPrincipal());
                throw uae;
            } catch (IncorrectCredentialsException ice) {
                log.info("Password for account " + token.getPrincipal() + " was incorrect!");
                throw ice;
            }
        }

        checkAuthorization(currentUser);

        showWelcomePage(response, session, username);
    }

    private void checkSession(Session session) {
        session.setAttribute("someKey", "aValue");
        String value = (String) session.getAttribute("someKey");
        if (value.equals("aValue")) {
            log.info("Retrieved the correct value! [" + value + "]");
        }
    }

    private void checkAuthorization(Subject currentUser) {
        //say who they are:
        //print their identifying principal (in this case, a username):
        log.info("User [" + currentUser.getPrincipal() + "] logged in successfully.");
        if ( currentUser.hasRole( "schwartz" ) ) {
            log.info("May the Schwartz be with you!" );
        } else {
            log.info( "Hello, mere mortal." );
        }
    }

    /**
     *
     * @param response
     * @param session
     * @param username
     * @throws IOException
     */
    private void showWelcomePage(HttpServletResponse response, Session session, String username) throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>Hello " + username + "</h1>");
        response.getWriter().println("<p>session id: " + session.getId());
        response.getWriter().println("<p><a href=\"/logout\">LogOut</a>");
    }
}
