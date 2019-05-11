package com.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;


@EnableWebMvc
@Configuration
public class ClientWebConfig implements WebMvcConfigurer {
    /*
    Very important here is that we can register view controllers that create a direct mapping between the URL
    and the view name using the ViewControllerRegistry.
    This way, there’s no need for any Controller between the two.
    */

    // We are creating a new route
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/index");
    }

    /*
    We registered a ViewResolver bean that will return .jsp views from the /WEB-INF/view directory.
    */
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver bean = new InternalResourceViewResolver();

        // JstlView helps us resolve JSP view requests
        bean.setViewClass(JstlView.class);
        bean.setPrefix("/WEB-INF/view/");
        bean.setSuffix(".jsp");
        return bean;
    }

}
