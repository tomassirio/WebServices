import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception{
        //Queue initialization
        BlockingQueue<Runnable> threadPoolQueue = new BlockingArrayQueue<>(20, 2, 80);
        // Create Threadpool for the server to use
        // default idle time as defined in the QueuedThreadPool
        QueuedThreadPool threadPool = new QueuedThreadPool(20, 2, (int) TimeUnit.MINUTES.toMillis(1), threadPoolQueue);
        threadPool.setName("jetty-test-server");
        threadPool.setDaemon(true);

        Server server = new Server(threadPool);
        ServerConnector serverConnector = new ServerConnector(server);
        serverConnector.setPort(8080);
        // add connector to server
        server.setConnectors(new Connector[]{serverConnector});

        final ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath("/");

        ResourceConfig resourceConfig = new ResourceConfig();
        final ServletHolder servletHolder = new ServletHolder(new ServletContainer(resourceConfig));

        servletContextHandler.addServlet(servletHolder, "/*");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{servletContextHandler});
        server.setHandler(handlers);

        server.start();
        server.join();

    }
}
