package uwi.dcit.agriexpensesvr;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class NamespaceFilter implements javax.servlet.Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        System.out.println("mookeeneh");
        // Make sure set() is only called if the current namespace is not already set.
        if (NamespaceManager.get() == null) {
            String namespace = UserServiceFactory
                    .getUserService()
                    .getCurrentUser()
                    .getUserId();
            NamespaceManager.set(namespace);// 623 6261 denise dickson
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }
}