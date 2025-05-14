package org.demo.embedded;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class EmbeddedTomcatDemo {
    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        // 设置工作目录
        tomcat.setBaseDir("target");
        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());
        var servletName = "hello";
        Tomcat.addServlet(ctx, servletName, new HelloServlet()).setAsyncSupported(true);
        ctx.addServletMappingDecoded("/", servletName);
        // 强制初始化 Connector；有时候，仅设置 tomcat.setPort(8080) 并不会自动创建 connector，调用 getConnector() 可以确保它被初始化。
        tomcat.getConnector();
        tomcat.start();
        tomcat.getServer().await();
    }
}
