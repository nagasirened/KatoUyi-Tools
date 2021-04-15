package com.katouyi.tools.system.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@SpringBootConfiguration
public class SearchWebMvcConfigurer extends WebMvcConfigurationSupport {
    @Autowired
    ApplicationContext applicationContext;

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // converters.add(new CustomFastJsonHttpMessageConverter());
    }

    @Bean
    public FilterRegistrationBean registerUserDefinedFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new UserDefinedFilter());
        registration.addUrlPatterns("/*");
        registration.setName("user-defined-filter");
        registration.setOrder(1);
        return registration;
    }

    /**
     * 拦截器住注册
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/static", "/webjars", "/swagger", "/v2", "/doc.html");
    }

    @Bean
    public BasedInterceptor accessInterceptor() {
        return new BasedInterceptor();
    }



    /*
    @Value("${dove.servers}")
    private String doveClientServers;

    @Bean
    public DoveConfigEngine doveConfigEngine() {
        return new DoveConfigEngine(doveClientServers);
    }*/

    @Bean
    public ServletRegistrationBean servlet() {
        UserDefinedDispatcherServlet servlet = new UserDefinedDispatcherServlet();
        servlet.setApplicationContext(applicationContext);

        ServletRegistrationBean bean = new ServletRegistrationBean(servlet, "/*", "/**", "/rest/*");
        bean.setLoadOnStartup(1);
        bean.setName("rest");
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.of(50, DataUnit.MEGABYTES)); //KB,MB
        factory.setMaxRequestSize(DataSize.of(50, DataUnit.MEGABYTES));
        bean.setMultipartConfig(factory.createMultipartConfig());
        return bean;
    }

    /**
     * 静态资源配置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/").addResourceLocations("classpath:/META-INF/resources/");
    }
}
