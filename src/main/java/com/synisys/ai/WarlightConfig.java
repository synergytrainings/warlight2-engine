package com.synisys.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.ServletContext;

/**
 * Created by hayk.movsisyan on 2/23/17.
 */
public class WarlightConfig {

    @Autowired
    ServletContext servletContext;



    @Configuration
    @EnableWebMvc
    public class WebConfig extends WebMvcConfigurerAdapter {

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {

            String uploadsDir = "/uploads/";
            String realPathtoUploads =  servletContext.getRealPath(uploadsDir);

            registry.addResourceHandler("/resources/**")
                    .addResourceLocations("file:" + realPathtoUploads);
        }
    }
}
