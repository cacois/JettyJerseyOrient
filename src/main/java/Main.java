import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        LogManager.getLogManager().reset();
        Logger.getLogger("").setLevel(Level.ALL);
        SLF4JBridgeHandler.install();

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        ServletContainer jerseyServletContainer = new ServletContainer(new AppResourceConfig());
        ServletHolder jerseyServletHolder = new ServletHolder(jerseyServletContainer);
        context.addServlet(jerseyServletHolder, "/*");

        Server jettyServer = new Server(8080);
        jettyServer.setHandler(context);

        try {
            String orientdbHome = new File("").getAbsolutePath();
            System.setProperty("ORIENTDB_HOME", orientdbHome);
            OServer server = OServerMain.create();
            server.startup(Main.class.getClassLoader().getResourceAsStream("orientdb.xml"));
            server.activate();
            jettyServer.start();
            jettyServer.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jettyServer.destroy();
        }
    }
}