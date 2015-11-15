/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java.net.httppost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import x.java.net.URLBuilder;

/**
 * Utility class to perform an HTTP POST. This class allows interaction with standard HTTP servers using the
 * POST method.
 * 
 * <p>
 * More Info and Knowledge: <a href=
 * "http://stackoverflow.com/questions/2793150/how-to-use-java-net-urlconnection-to-fire-and-handle-http-requests"
 * >How to use java.net.URLConnection to fire and handle HTTP requests?</a>
 * </p>
 * 
 * <p>
 * Parameters are sent as if they would be part of an HTML form. The content-type (<code>"Content-Type"</code>
 * request property) of the request will be set to
 * <code>"application/x-www-form-urlencoded;charset=UTF-8"</code>.
 * </p>
 * 
 * <p>
 * Its usefulness includes -but is not limited to- uploading/downloading files without having to use
 * Multi-part requests. The following examples will demonstrate these.
 * </p>
 * 
 * <p>
 * <b>Example #1: Upload a file encoded with Base64:</b><br>
 * 
 * <pre>
 * <blockquote style='border:1px solid black'>
 * String url = &quot;http://some.site.com/upload&quot;;
 * Path file = Paths.get(&quot;c:/downloads/mine.txt&quot;);
 * Map&lt;String, String&gt; paramsMap = new HashMap&lt;&gt;();
 * paramsMap.put(&quot;fileName&quot;, file.getFileName().toString());
 * try {
 *     paramsMap.put(&quot;fileBase64&quot;, StringUtils.toBase64String(Files.readAllBytes(file)));
 * } catch (IOException ie) {
 * }
 * // Add other parameters you need...
 * paramsMap.put(&quot;someOtherThing&quot;, &quot;some other value&quot;);
 * 
 * try (HttpPost httpPost = new HttpPost(url, paramsMap)) {
 *     if (httpPost.connect()) {
 *         if (httpPost.doPost())
 *             System.out.println(&quot;File sent successfully, server response: &quot; + httpPost.getResponse());
 *         else
 *             System.out.println(&quot;Failed to send file!&quot;);
 *     } else
 *         System.out.println(&quot;Failed to connect!&quot;);
 * } catch (Exception e) {
 *     System.out.println(&quot;Unexpected file upload error!&quot; + e);
 * }
 * </blockquote>
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * <b>Example #2: Download a file from a server:</b><br>
 * 
 * <pre>
 * <blockquote style='border:1px solid black'>
 * String url = &quot;http://some.site.com/download&quot;;
 * Map&lt;String, String&gt; paramsMap = new HashMap&lt;&gt;();
 * String fileName = &quot;somefile.txt&quot;;
 * paramsMap.put(&quot;fileName&quot;, fileName);
 * // Add other parameters you need...
 * paramsMap.put(&quot;userId&quot;, &quot;someUserId&quot;);
 * 
 * try (HttpPost httpPost = new HttpPost(url, paramsMap)) {
 *     if (httpPost.connect()) {
 *         if (httpPost.doPost()) {
 *             // Note: we could simply acquire a suitable file provider by:
 *             // new SimpleFileProvider(Paths.get(&quot;c:/downloads&quot;, fileName), null);
 *             // but this is to demonstrate the use and possibilities of FileProvider
 *             boolean result = httpPost.saveAttachmentToFile(new FileProvider() {
 *                 public Path getFile(HttpURLConnection httpUrlConnection) {
 *                     Path file = Paths.get(&quot;c:/downloads&quot;, fileName);
 *                     System.out.println(&quot;Saving file to: &quot; + file);
 *                     return file;
 *                 }
 *                 
 *                 public Long getLastModified(HttpURLConnection httpUrlConnection) {
 *                     // We assume here that the server sends the file last modified value as a header field named &quot;X-file-date&quot;:
 *                     String fileDateString = httpUrlConnection.getHeaderField(&quot;X-file-date&quot;);
 *                     return fileDateString == null ? null : Long.valueOf(fileDateString);
 *                 }
 *             } );
 *             if (result)
 *                 System.out.println(&quot;Attachment saved successfully to file: &quot; + file);
 *         } else
 *             System.out.println(&quot;Failed to send request!&quot;);
 *     } else
 *         System.out.println(&quot;Failed to connect!&quot;);
 * } catch (Exception e) {
 *     System.out.println(&quot;Unexpected file download error!&quot; + e);
 * }
 * </blockquote>
 * </pre>
 * 
 * @author Andras Belicza
 * 
 * @see URLBuilder
 */
public class HttpPost implements AutoCloseable {
    
    /** {@link Logger} used for logging. */
    private static final Logger LOGGER = Logger.getLogger(HttpPost.class.getName());
    
    
    /** Default charset to be used. */
    public static final String DEFAULT_CHARSET = "UTF-8";
    
    
    /** Internal state of the connection/communication. */
    private State state = State.NOT_CONNECTED;
    
    /** Tells if internal state checking is enabled. */
    private boolean internalStateCheckingEnabled = true;
    
    /** Map of parameters to be sent. */
    private final Map<String, String> paramsMap;
    
    /** {@link URL} to post to. */
    private final URL url;
    
    /** Charset to use to send the request. */
    private String requestCharset = DEFAULT_CHARSET;
    
    /** Optional additional request properties. */
    private Map<String, String> requestPropertyMap;
    
    /** HttpUrlConnection to perform the POST. */
    private HttpURLConnection httpUrlConnection;
    
    
    /**
     * Creates a new {@link HttpPost}.
     * 
     * @param url {@link URL} to post to
     * @param paramsMap map of parameters to be sent
     */
    public HttpPost(final URL url, final Map<String, String> paramsMap) {
        this.url = url;
        this.paramsMap = paramsMap;
    }
    
    /**
     * Sets whether internal state checking should be performed.
     * 
     * <p>
     * You may want to disable internal state checking if you want to tweak the {@link HttpURLConnection}.
     * </p>
     * 
     * @param enabled the internal state checking value to be set
     */
    public void setInternalStateCheckingEnabled(final boolean enabled) {
        internalStateCheckingEnabled = enabled;
    }
    
    /**
     * Tells if internal state checking is enabled. Internal state checking is enabled by default.
     * 
     * @return true if internal state checking is enabled; false otherwise
     */
    public boolean isInternalStateCheckingEnabled() {
        return internalStateCheckingEnabled;
    }
    
    /**
     * Returns the internal state of the connection/communication.
     * 
     * @return the internal state of the connection/communication
     */
    public State getState() {
        return state;
    }
    
    /**
     * Sets the charset of the request.
     * 
     * <p>
     * This will set the request property <code>"Accept-Charset"</code> to <code>charset</code>.
     * </p>
     * 
     * <p>
     * It must be called before {@link #connect()}. If charset is not set, the {@link #DEFAULT_CHARSET} will
     * be used.
     * </p>
     * 
     * @param requestCharset charset of the request to be set
     * 
     * @throws IllegalStateException if internal state checking is enabled and the internal state is not
     *             {@link State#NOT_CONNECTED}
     */
    public void setRequestCharset(final String requestCharset) {
        if (internalStateCheckingEnabled && state != State.NOT_CONNECTED)
            throw new IllegalStateException("setRequestCharset() can only be called in NOT_CONNECTED state!");
        
        this.requestCharset = requestCharset;
    }
    
    /**
     * Returns the charset of the request. The default charset of the request is {@link #DEFAULT_CHARSET}.
     * 
     * @return the charset of the request
     */
    public String getRequestCharset() {
        return requestCharset;
    }
    
    /**
     * Sets a request property.
     * 
     * <p>
     * The properties will be passed to the underlying {@link HttpURLConnection} before it's
     * <code>connect()</code> method is called.
     * </p>
     * 
     * <p>
     * It must be called before {@link #connect()}.
     * </p>
     * 
     * @param key the property key
     * @param value the property value
     * 
     * @throws IllegalStateException if internal state checking is enabled and the internal state is not
     *             {@link State#NOT_CONNECTED}
     */
    public void setRequestProperty(final String key, final String value) {
        if (internalStateCheckingEnabled && state != State.NOT_CONNECTED)
            throw new IllegalStateException("setRequestProperty() can only be called in NOT_CONNECTED state!");
        
        if (requestPropertyMap == null)
            requestPropertyMap = new HashMap<>();
        
        requestPropertyMap.put(key, value);
    }
    
    /**
     * Returns the underlying {@link HttpURLConnection}.
     * 
     * <p>
     * This method may return <code>null</code> if called before {@link #connect()} or if called when
     * {@link #connect()} returned false.
     * </p>
     * 
     * @return the underlying {@link HttpURLConnection}
     */
    public HttpURLConnection getConnection() {
        return httpUrlConnection;
    }
    
    /**
     * Connects to the provided URL.
     * 
     * <p>
     * Only one connect method ({@link #connect()} or {@link #connect(Runnable)}) can be called and only once.
     * </p>
     * 
     * @return true if connection was successful; false otherwise
     * 
     * @throws IllegalStateException if internal state checking is enabled and the internal state is not
     *             {@link State#NOT_CONNECTED}
     * 
     * @see #connect(Runnable)
     */
    public boolean connect() {
        return connect(null);
    }
    
    /**
     * Connects to the provided URL.
     * 
     * <p>
     * Only one connect method ({@link #connect()} or {@link #connect(Runnable)}) can be called and only once.
     * </p>
     * 
     * @param beforeConnectionConnectTask task to be executed right before calling
     *            {@link HttpURLConnection#connect()}
     * @return true if connection was successful; false otherwise
     * 
     * @throws IllegalStateException if internal state checking is enabled and the internal state is not
     *             {@link State#NOT_CONNECTED}
     * 
     * @see #connect()
     */
    public boolean connect(final Runnable beforeConnectionConnectTask) {
        if (internalStateCheckingEnabled && state != State.NOT_CONNECTED)
            throw new IllegalStateException("connect() can only be called in NOT_CONNECTED state!");
        
        try {
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            
            httpUrlConnection.setDoOutput(true);
            
            if (requestPropertyMap != null)
                for (final Entry<String, String> entry : requestPropertyMap.entrySet())
                    httpUrlConnection.setRequestProperty(entry.getKey(), entry.getValue());
            
            httpUrlConnection.setRequestProperty("Accept-Charset", requestCharset);
            httpUrlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset="
                    + requestCharset);
            
            if (beforeConnectionConnectTask != null)
                beforeConnectionConnectTask.run();
            
            httpUrlConnection.connect();
        } catch (final IOException ie) {
            state = State.CONNECT_FAILED;
            LOGGER.log(Level.SEVERE, "Failed to connect to: " + url, ie);
            return false;
        }
        
        state = State.CONNECTED;
        return true;
    }
    
    /**
     * Posts the parameters to the server.
     * 
     * <p>
     * The parameters will be encoded using the charset set by {@link #setRequestCharset(String)} (defaults to
     * <code>{@value #DEFAULT_CHARSET}</code> ).
     * </p>
     * 
     * <p>
     * Can only be called if {@link #connect()} returned <code>true</code>.
     * </p>
     * 
     * @return true if the operation was successful; false otherwise
     * 
     * @throws IllegalStateException if internal state checking is enabled and the internal state is not
     *             {@link State#CONNECTED}
     */
    public boolean doPost() {
        if (internalStateCheckingEnabled && state != State.CONNECTED)
            throw new IllegalStateException("doPost() can only be called in CONNECTED state!");
        
        try (final OutputStream output = httpUrlConnection.getOutputStream()) {
            final StringBuilder paramsBuilder = new StringBuilder();
            for (final Entry<String, String> entry : paramsMap.entrySet()) {
                if (paramsBuilder.length() > 0)
                    paramsBuilder.append('&');
                paramsBuilder.append(entry.getKey()).append('=')
                        .append(URLEncoder.encode(entry.getValue(), DEFAULT_CHARSET));
            }
            
            output.write(paramsBuilder.toString().getBytes(requestCharset));
            output.flush();
            
            state = State.REQUEST_SENT;
        } catch (final IOException ie) {
            state = State.SENDING_REQUEST_FAILED;
            LOGGER.log(Level.SEVERE, "Failed to post parameters to: " + url, ie);
            return false;
        }
        
        return true;
    }
    
    /**
     * Tells if the the HTTP response code is OK (HTTP 200).
     * 
     * <p>
     * Can only be called after {@link #doPost()} and before {@link #close()}.
     * </p>
     * 
     * @return true if the the HTTP response code is OK; false otherwise
     * 
     * @throws IllegalStateException if internal state checking is enabled and {@link #doPost()} has not been
     *             called or {@link #close()} has been called
     * 
     * @see #getServerResponseCode()
     * @see #getServerResponseMessage()
     */
    public boolean isServerResponseOk() {
        if (internalStateCheckingEnabled
                && (state.compareTo(State.REQUEST_SENT) < 0 || state == State.CLOSED))
            throw new IllegalStateException(
                    "isServerResponseOk() can only be called after doPost() and before close()!");
        
        try {
            return httpUrlConnection.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (final IOException ie) {
            LOGGER.log(Level.SEVERE, "Failed to get server response code from: " + url, ie);
            return false;
        }
    }
    
    /**
     * Returns the HTTP response code of the server.
     * 
     * <p>
     * Can only be called after {@link #doPost()} and before {@link #close()}.
     * </p>
     * 
     * @return the HTTP response code of the server; <code>-1</code> if getting the response code fails
     * 
     * @throws IllegalStateException if internal state checking is enabled and {@link #doPost()} has not been
     *             called or {@link #close()} has been called
     * 
     * @see #isServerResponseOk()
     * @see #getServerResponseMessage()
     */
    public int getServerResponseCode() {
        if (internalStateCheckingEnabled
                && (state.compareTo(State.REQUEST_SENT) < 0 || state == State.CLOSED))
            throw new IllegalStateException(
                    "getServerResponseCode() can only be called after doPost() and before close()!");
        
        try {
            return httpUrlConnection.getResponseCode();
        } catch (final IOException ie) {
            LOGGER.log(Level.SEVERE, "Failed to get server response code from: " + url, ie);
            return -1;
        }
    }
    
    /**
     * Returns the HTTP response message of the server.
     * 
     * <p>
     * Can only be called after {@link #doPost()} and before {@link #close()}.
     * </p>
     * 
     * @return the HTTP response message of the server; <code>null</code> if getting the response fails
     * 
     * @throws IllegalStateException if internal state checking is enabled and {@link #doPost()} has not been
     *             called or {@link #close()} has been called
     * 
     * @see #isServerResponseOk()
     * @see #getServerResponseCode()
     */
    public String getServerResponseMessage() {
        if (internalStateCheckingEnabled
                && (state.compareTo(State.REQUEST_SENT) < 0 || state == State.CLOSED))
            throw new IllegalStateException(
                    "getServerResponseMessage() can only be called after doPost() and before close()!");
        
        try {
            return httpUrlConnection.getResponseMessage();
        } catch (final IOException ie) {
            LOGGER.log(Level.SEVERE, "Failed to get server response message from: " + url, ie);
            
            return null;
        }
    }
    
    /**
     * Gets the response from the server.
     * 
     * <p>
     * Can only be called after {@link #doPost()}.
     * </p>
     * 
     * <p>
     * If the server returned an error, this will return the error page provided by the server.
     * </p>
     * 
     * @return the server response, or <code>null</code> if error occurred
     * 
     * @throws IllegalStateException if internal state checking is enabled and the internal state is not
     *             {@link State#REQUEST_SENT}
     * 
     * @see #getResponseLines()
     * @see #saveAttachmentToFile(FileProvider, byte[][])
     */
    public String getResponse() {
        if (internalStateCheckingEnabled && state != State.REQUEST_SENT)
            throw new IllegalStateException("getResponse() can only be called in REQUEST_SENT state!");
        
        return (String) readResponse(false);
    }
    
    /**
     * Gets the response from the server as a list of lines.
     * 
     * <p>
     * Can only be called after {@link #doPost()}.
     * </p>
     * 
     * <p>
     * If the server returned an error, this will return the error page provided by the server.
     * </p>
     * 
     * @return the server response as a list of lines, or <code>null</code> if error occurred
     * 
     * @throws IllegalStateException if internal state checking is enabled and the internal state is not
     *             {@link State#REQUEST_SENT}
     * 
     * @see #getResponse()
     * @see #saveAttachmentToFile(FileProvider, byte[][])
     */
    @SuppressWarnings("unchecked")
    public List<String> getResponseLines() {
        if (internalStateCheckingEnabled && state != State.REQUEST_SENT)
            throw new IllegalStateException("getResponseLines() can only be called in REQUEST_SENT state!");
        
        return (List<String>) readResponse(true);
    }
    
    /**
     * Reads the response from the server.
     * 
     * @param asLineList tells response should be returned as a list of lines or as 1 string
     * @return the server response, or <code>null</code> if error occurred
     */
    private Object readResponse(final boolean asLineList) {
        final boolean ok = isServerResponseOk();
        
        String responseCharset = DEFAULT_CHARSET;
        final String contentType = httpUrlConnection.getHeaderField("Content-Type");
        if (contentType != null) {
            for (final String token : contentType.replace(" ", "").split(";")) {
                if (token.startsWith("charset=")) {
                    responseCharset = token.split("=", 2)[1];
                    break;
                }
            }
        }
        
        try (final BufferedReader reader = new BufferedReader(
                new InputStreamReader(ok ? httpUrlConnection.getInputStream()
                        : httpUrlConnection.getErrorStream(), responseCharset))) {
            
            if (asLineList) {
                String line;
                final List<String> lineList = new ArrayList<>();
                while ((line = reader.readLine()) != null)
                    lineList.add(line);
                
                state = State.RESPONSE_PROCESSED;
                return lineList;
            } else {
                final StringBuilder responseBuilder = new StringBuilder();
                final char[] buffer = new char[64];
                int charsRead;
                while ((charsRead = reader.read(buffer)) > 0)
                    responseBuilder.append(buffer, 0, charsRead);
                
                state = State.RESPONSE_PROCESSED;
                return responseBuilder.toString();
            }
        } catch (final IOException ie) {
            LOGGER.log(Level.SEVERE, "Failed to read response from server: " + url, ie);
        }
        
        state = State.PROCESSING_RESPONSE_FAILED;
        return null;
    }
    
    /**
     * Saves the attachment of the response, the content is treated as <code>application/octet-stream</code>.
     * 
     * <p>
     * Can only be called if {@link #doPost()} returned <code>true</code>.
     * </p>
     * 
     * <p>
     * A {@link FileProvider} is used to get a file to save the attachment to.
     * </p>
     * 
     * @param fileProvider file provider to specify a file to save to
     * @param buffer_ optional buffer to use for IO read/write operations
     * @return true if the attachment was saved successfully; false otherwise
     * 
     * @throws IllegalStateException if internal state checking is enabled and the internal state is not
     *             {@link State#REQUEST_SENT}
     * 
     * @see FileProvider
     * @see SimpleFileProvider
     * @see #getResponse()
     * @see #getResponseLines()
     */
    public boolean saveAttachmentToFile(final FileProvider fileProvider, final byte[]... buffer_) {
        if (internalStateCheckingEnabled && state != State.REQUEST_SENT)
            throw new IllegalStateException(
                    "saveAttachmentToFile() can only be called in REQUEST_SENT state!");
        
        Path file = null;
        try (final InputStream input = httpUrlConnection.getInputStream()) {
            final int status = httpUrlConnection.getResponseCode();
            
            if (status == HttpURLConnection.HTTP_OK) {
                file = fileProvider.getFile(httpUrlConnection);
                
                try (final OutputStream out = Files.newOutputStream(file)) {
                    final byte[] buffer = buffer_.length == 0 ? new byte[16 * 1024] : buffer_[0];
                    
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) > 0)
                        out.write(buffer, 0, bytesRead);
                }
                
                final Long lastModified = fileProvider.getLastModified(httpUrlConnection);
                if (lastModified != null)
                    Files.setLastModifiedTime(file, FileTime.fromMillis(lastModified));
                
                state = State.RESPONSE_PROCESSED;
                return true;
            }
        } catch (final IOException ie) {
            LOGGER.log(Level.SEVERE,
                    "Failed to save attachment" + (file == null ? "!" : " to file: " + file), ie);
        }
        
        state = State.PROCESSING_RESPONSE_FAILED;
        return false;
    }
    
    /**
     * Closes this {@link HttpPost}, releases all allocated resources.
     */
    public void close() {
        if (httpUrlConnection != null)
            httpUrlConnection.disconnect();
        
        state = State.CLOSED;
    }
    
}
