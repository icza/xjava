/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java.os;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

/**
 * Operating system enumeration and OS specific tasks.
 * 
 * @author Andras Belicza
 */
public enum OpSys {
    
    /** Windows. */
    WINDOWS,
    
    /** MAC OS-X. */
    OS_X,
    
    /** Unix (including Linux). */
    UNIX,
    
    /** Solaris (Sun OS). */
    SOLARIS,
    
    /** Other. */
    OTHER;
    
    /** Current detected {@link OpSys}. */
    public static OpSys CURRENT_OP_SYS;
    static {
        final String osName = System.getProperty("os.name").toLowerCase();
        
        if (osName.indexOf("win") >= 0)
            CURRENT_OP_SYS = WINDOWS;
        else if (osName.indexOf("mac") >= 0)
            CURRENT_OP_SYS = OS_X;
        else if (osName.indexOf("nix") >= 0 || osName.indexOf("nux") >= 0)
            CURRENT_OP_SYS = UNIX;
        else if (osName.indexOf("sunos") >= 0)
            CURRENT_OP_SYS = SOLARIS;
        else
            CURRENT_OP_SYS = OTHER;
    }
    
    /** Documents path of the user. */
    private static Path userDocumentsPath; // Lazily initialized
    
    /**
     * Returns the path pointing to the users' documents folder.
     * 
     * @return the path pointing to the users' documents folder
     */
    public static Path getUserDocumentsPath() {
        if (userDocumentsPath == null) {
            // Lazy init to avoid loaded swing related classes!
            userDocumentsPath = new JFileChooser().getFileSystemView().getDefaultDirectory().toPath();
        }
        
        return userDocumentsPath;
    }
    
    /**
     * Opens the web page specified by the URL in the system's default browser.
     * 
     * @param url {@link URL} to be opened
     * 
     * @return <code>null</code> if URL is opened successfully; the encountered {@link Exception} otherwise
     */
    public static Exception showURLInBrowser(final URL url) {
        try {
            if (Desktop.isDesktopSupported())
                try {
                    Desktop.getDesktop().browse(url.toURI());
                    return null;
                } catch (final Exception e) {
                    // If default method fails, we try our own method, so ignore this.
                }
            
            // Desktop failed, try our own method
            String[] cmdArray = null;
            if (CURRENT_OP_SYS == WINDOWS) {
                cmdArray = new String[] { "rundll32", "url.dll,FileProtocolHandler", url.toString() };
            } else {
                // Linux
                final String[] browsers = { "firefox", "google-chrome", "opera", "konqueror", "epiphany",
                        "mozilla", "netscape" };
                for (final String browser : browsers)
                    if (Runtime.getRuntime().exec(new String[] { "which", browser }).waitFor() == 0) {
                        cmdArray = new String[] { browser, url.toString() };
                        break;
                    }
            }
            
            if (cmdArray != null)
                Runtime.getRuntime().exec(cmdArray);
            
            return null;
        } catch (final Exception e) {
            LOGGER.log(Level.WARNING, "Failed to open URL: " + url, e);
            return e;
        }
    }
    
    /**
     * Opens the specified file or folder in the default file browser application of the user's OS.
     * <p>
     * If a file is specified, in case of {@link #WINDOWS} it will also be selected.
     * </p>
     * 
     * @param path file or folder to be opened
     * 
     * @return <code>null</code> if file browser showing path is opened successfully; the encountered
     *         {@link Exception} otherwise
     */
    public static Exception showPathInFileBrowser(final Path path) {
        try {
            final boolean isFolder = Files.isDirectory(path);
            final boolean isFile = !isFolder;
            
            if (isFile && CURRENT_OP_SYS == WINDOWS) {
                // On Windows we have a way to not just open but also select the replay file; source:
                // http://stackoverflow.com/questions/7357969/how-to-use-java-code-to-open-windows-file-explorer-and-highlight-the-specified-f
                // http://support.microsoft.com/kb/152457
                // It should be "explorer.exe /select,c:\dir\filename.ext", but if (double) spaces are in the
                // file name, it doesn't work, so I use it this way (which also works):
                // "explorer.exe" "/select," "c:\dir\filename.ext"
                new ProcessBuilder("explorer.exe", "/select,", path.toAbsolutePath().toString()).start();
            } else {
                // If not folder it surely has a parent
                Desktop.getDesktop().open(isFolder ? path.toFile() : path.getParent().toFile());
            }
            
            return null;
        } catch (final IOException ie) {
            LOGGER.log(Level.WARNING, "Failed to open file browser!", ie);
            return ie;
        }
    }
    
    /**
     * Copies the specified text to the system clipboard.
     * 
     * @param text text to be copied to the system clipboard
     * 
     * @return <code>null</code> if text is copied successfully; the encountered {@link Exception} otherwise
     */
    public static Exception copyToClipboard(final String text) {
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
            return null;
        } catch (final IllegalStateException ise) {
            // Just to make sure: on some platforms if the clipboard is being accessed by another application,
            // this might happen
            return ise;
        }
    }
    
    
    /** {@link Logger} used for logging. */
    private static final Logger LOGGER = Logger.getLogger(OpSys.class.getName());
    
}
