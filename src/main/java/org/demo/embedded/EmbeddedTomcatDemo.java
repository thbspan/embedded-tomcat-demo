package org.demo.embedded;

import jakarta.servlet.Filter;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import java.io.File;

public class EmbeddedTomcatDemo {
    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        // 设置工作目录
        tomcat.setBaseDir("target");
        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());
        var filterName = "loggingFilter";
        ctx.addFilterDef(createFilterDef(filterName, new LoggingFilter()));
        ctx.addFilterMap(createFilterMap(filterName, "/*"));

        var servletName = "hello";
        // 添加 servlet
        Tomcat.addServlet(ctx, servletName, new HelloServlet()).setAsyncSupported(true);
        ctx.addServletMappingDecoded("/hello", servletName);
        // 添加 错误页面
        addErrorPage(ctx);
        // 强制初始化 Connector；有时候，仅设置 tomcat.setPort(8080) 并不会自动创建 connector，调用 getConnector() 可以确保它被初始化。
        tomcat.getConnector();
        tomcat.start();
        tomcat.getServer().await();
    }

    /// FilterDef 辅助方法
    private static FilterDef createFilterDef(String name, Filter filter) {
        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName(name);
        filterDef.setFilter(filter);
        filterDef.setFilterClass(filter.getClass().getName());
        return filterDef;
    }

    /// FilterMap 辅助方法
    private static FilterMap createFilterMap(String name, String pattern) {
        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(name);
        filterMap.addURLPattern(pattern);
        return filterMap;
    }

    private static void addErrorPage(Context ctx) {
        // ✅ 设置 404 错误页面
        ErrorPage error404 = new ErrorPage();
        error404.setErrorCode(404);
        error404.setLocation("/notfound");

        // ✅ 设置 500 错误页面
        ErrorPage error500 = new ErrorPage();
        error500.setErrorCode(500);
        error500.setLocation("/servererror");

        // ✅ 设置某个异常类型的处理
        ErrorPage npePage = new ErrorPage();
        npePage.setExceptionType(NullPointerException.class.getName());
        npePage.setLocation("/npe");

        // 注册错误页面
        ctx.addErrorPage(error404);
        ctx.addErrorPage(error500);
        ctx.addErrorPage(npePage);

        // 注册对应的处理 Servlet
        Tomcat.addServlet(ctx, "notFoundServlet", new SimplePageServlet("页面不存在（404）"));
        ctx.addServletMappingDecoded("/notfound", "notFoundServlet");

        Tomcat.addServlet(ctx, "errorServlet", new SimplePageServlet("服务器内部错误（500）"));
        ctx.addServletMappingDecoded("/servererror", "errorServlet");

        Tomcat.addServlet(ctx, "npeServlet", new SimplePageServlet("空指针异常处理页面"));
        ctx.addServletMappingDecoded("/npe", "npeServlet");
    }
}
