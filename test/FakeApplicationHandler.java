/*
 * Copyright (C) 2009-2013 Typesafe Inc. <http://www.typesafe.com>
 */

import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.Helpers;
import play.test.TestServer;
import play.test.WithApplication;

/**
 * Provides a server to JUnit tests. Make your OTNTest class extend this class and
 * an HTTP server will be started before each OTNTest is invoked. You can setup the
 * fake application and port to use by overriding the provideFakeApplication and
 * providePort methods. Within a OTNTest, the running application and the TCP port
 * are available through the app and port fields, respectively.
 */
public class FakeApplicationHandler extends WithApplication {

    public static int port;
    public static TestServer testServer = null;
    public static Application application = null;

    public static int providePort() {
        return play.api.test.Helpers.testServerPort();
    }

    protected Application provideApplication() {
        //ConfigFactory.invalidateCaches();
        return new GuiceApplicationBuilder()
                /*.configure("config.resource", "test/"+CommonConstants.CONFIGPATH + CommonConstants.SLASH + CommonConstants.APP_TEST_CONF)
                .configure("logger.resource", "test/"+CommonConstants.CONFIGPATH + CommonConstants.SLASH + CommonConstants.APP_LOG_TEST_FILE)
                .bindings(new ComponentModule())
                //.configure("play.http.router", CommonConstants.CONFIGPATH + CommonConstants.SLASH + CommonConstants.ROUTES)
                //.configure("application.router", CommonConstants.CONFIGPATH + CommonConstants.SLASH + CommonConstants.ROUTES)
                //.load(Guiceable.modules(new ComponentModule()))*/
                .build();
    }

    public void startServer() {
        if (testServer == null) {
            app = provideApplication();
            port = providePort();
            testServer = Helpers.testServer(port, app);
            testServer.start();
        }
    }

    public void stopServer() {
        if (testServer != null) {
            System.out.println("Stopped");
            Helpers.stop(testServer);
            testServer.stop();
            testServer = null;
            app = null;
        }
    }

}