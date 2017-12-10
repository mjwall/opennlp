/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opennlp.demo;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoApplication extends javax.ws.rs.core.Application {

  private static final Logger LOG = LoggerFactory.getLogger(DemoApplication.class);

  final public static String VERSION = "1";
  final public static String REST_CONTEXT = "/rest/v" + VERSION + "/";
  final public static String DEFAULT_PORT = "4567";
  final public static String DEFAULT_IP = "127.0.0.1";
  final private Server server;
  final private String serverUrl;

  public DemoApplication() throws IOException {
    String ip = System.getProperty("serverIp", DEFAULT_IP);
    int port = Integer.parseInt(System.getProperty("serverPort", DEFAULT_PORT));

    InetSocketAddress address = new InetSocketAddress(ip, port);
    server = new Server(address);
    serverUrl = "http://" + ip + ":" + port + "/";

    ContextHandler apiHandler = buildApiHandler();
    apiHandler.setContextPath(REST_CONTEXT);
    ContextHandler staticHandler = buildStaticHandler();
    staticHandler.setContextPath("/");

    final HandlerList handlers = new HandlerList();
    handlers.addHandler(apiHandler);
    handlers.addHandler(staticHandler);

    server.setHandler(handlers);
    server.setStopAtShutdown(true);
    server.setStopTimeout(10000l);
    server.dump(System.err);
  }

  public static void main(String[] args) throws Exception {
    DemoApplication app = new DemoApplication();
    LOG.info("Starting server at " + app.serverUrl);
    app.start();
  }

  public String getServerUrl() {
    return serverUrl;
  }

  protected void start() {
    try {
      server.start();
      server.join();
    } catch (Throwable t) {
      t.printStackTrace(System.err);
    }
  }

  private ContextHandler buildStaticHandler() {
    // Add pathspec for static assets
    ServletHolder staticHolder = new ServletHolder("static-holder", DefaultServlet.class);
    // If no filesytem path was passed in, load assets from the classpath
    String staticDir = System.getProperty("staticDir");
    if (null == staticDir) {
      LOG.debug("Loading static resources from jar");
      staticHolder.setInitParameter("resourceBase",
          this.getClass().getClassLoader().getResource("public").toExternalForm());
    } else {
      // set -DstaticDir=${project_loc}/src/main/resources/public
      // in VM args of Run Configuration or in args of mvn exec plugin
      LOG.debug("Loading static resources from staticPath: {}", staticDir);
      staticHolder.setInitParameter("resourceBase", staticDir);
    }
    staticHolder.setInitParameter("dirAllowed", "true");
    staticHolder.setInitParameter("pathInfoOnly", "true");
    // homeHolder.setInitOrder(0);

    ServletContextHandler homeContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    homeContextHandler.addServlet(staticHolder, "/*");

    return homeContextHandler;
  }

  private ContextHandler buildApiHandler() {
    // Add pathspec for REST endpoints
    final ResourceConfig application = new ResourceConfig();
    // setup rest endpoint
    application.packages("opennlp.demo.resource").register(JacksonFeature.class);

    ServletHolder apiHolder = new ServletHolder(
        new org.glassfish.jersey.servlet.ServletContainer(application));
    // apiHolder.setInitOrder(0);
    // apiHolder.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "resource");

    ServletContextHandler apiHolderContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
    apiHolderContext.addServlet(apiHolder, "/*");

    return apiHolderContext;
  }
}
