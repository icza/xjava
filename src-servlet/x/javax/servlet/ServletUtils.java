package x.javax.servlet;

import javax.servlet.Filter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet API related utilities.
 * 
 * @author Andras Belicza
 * 
 * @see HttpServlet
 * @see HttpServletRequest
 * @see HttpServletResponse
 * @see Filter
 */
public class ServletUtils {
    
    /**
     * Returns the value of the specified {@link Cookie}.
     * 
     * @param req request to look for the cookie in
     * @param name name of the cookie
     * @return the value of the specified {@link Cookie} or <code>null</code> if no cookie found with the
     *         specified name
     * 
     * @see #addCookie(HttpServletResponse, String, String, int)
     * @see #removeCookie(HttpServletResponse, String)
     * @see Cookie
     */
    public static String getCookieValue(final HttpServletRequest req, final String name) {
        final Cookie[] cs = req.getCookies();
        
        if (cs != null)
            for (final Cookie c : cs)
                if (name.equals(c.getName()))
                    return c.getValue();
        
        return null;
    }
    
    /**
     * Adds a new {@link Cookie} to the specified response.
     * 
     * @param resp response to add the cookie to
     * @param name name of the cookie
     * @param value value of the cookie
     * @param maxAge age of the cookie in seconds
     * 
     * @see #getCookieValue(HttpServletRequest, String)
     * @see #removeCookie(HttpServletResponse, String)
     * @see Cookie
     */
    public static void addCookie(final HttpServletResponse resp, final String name, final String value,
            final int maxAge) {
        final Cookie c = new Cookie(name, value);
        
        c.setHttpOnly(true); // To prevent access from JavaScript and plugins
        c.setSecure(true); // To send over only HTTPS
        c.setPath("/");
        c.setMaxAge(maxAge);
        
        resp.addCookie(c);
    }
    
    /**
     * Removes the specified {@link Cookie} from the specified response.
     * 
     * <p>
     * A cookie is removed by settings its value to <code>null</code> and its max age to <code>0</code>.
     * </p>
     * 
     * @param resp response to remove the cookie from
     * @param name name of the cookie to be removed
     * 
     * @see #addCookie(HttpServletResponse, String, String, int)
     * @see #getCookieValue(HttpServletRequest, String)
     * @see Cookie
     */
    public static void removeCookie(final HttpServletResponse resp, final String name) {
        addCookie(resp, name, null, 0);
    }
    
    /**
     * Returns the original IP address of the client the specified {@link HttpServletRequest} is originating
     * from.
     * 
     * <p>
     * The problem with {@link HttpServletRequest#getRemoteAddr()} is that it returns the IP address of the
     * last proxy server if the request goes through proxy servers. This method attempts best-effort to find
     * out where the request comes from originally. If the origin of the request is unknown,
     * {@link HttpServletRequest#getRemoteAddr()} is returned as a last resort.
     * </p>
     * 
     * @param req request whose client's original IP address to be returned
     * 
     * @return the original IP address of the client the specified request is originating from
     * 
     * @see HttpServletRequest#getRemoteAddr()
     */
    public static String getClientOrigIpAddr(final HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
            ip = req.getHeader("Proxy-Client-IP");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
            ip = req.getHeader("WL-Proxy-Client-IP");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
            ip = req.getHeader("HTTP_CLIENT_IP");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
            ip = req.getHeader("HTTP_X_FORWARDED_FOR");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
            ip = req.getRemoteAddr();
        
        return ip;
    }
    
    /**
     * Disables any caching for the specified {@link HttpServletResponse}.
     * 
     * <p>
     * See <a href="https://www.mnot.net/cache_docs/">Caching Tutorial</a>.
     * </p>
     * 
     * @param resp response to disable caching for
     * 
     * @see #setCaching(HttpServletResponse, int)
     */
    public static void disableCaching(final HttpServletResponse resp) {
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // For HTTP 1.1
        resp.setHeader("Pragma", "no-cache"); // For HTTP 1.0
        resp.setDateHeader("Expires", 0); // For proxies
    }
    
    /**
     * Sets caching for the specified {@link HttpServletResponse} for the specified max age.
     * 
     * <p>
     * See <a href="https://www.mnot.net/cache_docs/">Caching Tutorial</a>.
     * </p>
     * 
     * @param resp response to set caching for
     * @param maxAgeSec max response age in seconds.
     * 
     * @see #disableCaching(HttpServletResponse)
     */
    public static void setCaching(final HttpServletResponse resp, final int maxAgeSec) {
        resp.setHeader("Cache-Control", "max-age=" + maxAgeSec + ", must-revalidate"); // For HTTP 1.1
        resp.setDateHeader("Expires", 0); // For proxies
    }
    
}
