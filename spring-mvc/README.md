<h1>Sample Spring MVC application</h1>
You can clone the project and import it to your IDE and run "mvn clean package". Afterwards just deploy the artifact to the server.

Spring MVC java configuration steps

1. Create a basic maven project New > Project . Depending on the version you assign, that will be applied to your artifact when you package the project. Keep the version simple (example 1.0) for convenience 
org.alo.springmvc
spring-mvc

2. Adding necessary dependencies to pom.xml

Spring contexts are also called Spring IoC containers, which are responsible for instantiating, configuring, and assembling beans by reading configuration metadata from XML, Java annotations, and/or Java code in the configuration files.
1)
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.1.5.RELEASE</version>
</dependency>

2) The spring-web dependency contains common web specific utilities for both Servlet and Portlet environments, while spring-webmvc enables the MVC support for Servlet environments.
Since spring-webmvc has spring-web as a dependency, explicitly defining spring-web is not required when using spring-webmvc.
The Spring Web model-view-controller (MVC) framework is designed around a DispatcherServlet that dispatches requests to handlers, with configurable handler mappings, view resolution, locale, time zone and theme resolution as well as support for uploading files. The default handler is based on the @Controller and @RequestMapping annotations, offering a wide range of flexible handling methods. With the introduction of Spring 3.0, the @Controller mechanism also allows you to create RESTful Web sites and applications, through the @PathVariable annotation and other features.
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>5.1.5.RELEASE</version>
</dependency>

3)
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.1.5.RELEASE</version>
</dependency>

4) The javax.servlet package contains a number of classes and interfaces that describe and define the contracts between a servlet class and the runtime environment provided for an instance of such a class by a conforming servlet container.
It will be used in the Initializer to deal with servlet logic
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>3.1.0</version>
</dependency>

Add <packaging>war</packaging> to deploy it to a server


To have the latest build tool and to avoid warnings about obsolete versions define maven-compiler-plugin. Depending on the build level you wish to set then JAVA_HOME needs to be set on the same version in the terminal you are in. In case the java version is not the same as defined in the pom.xml then you will receive a error regarding the flag not found especially when you are trying to use java 11
By default, the compiler plugin compiles source code compatible with Java 5, and the generated classes also work with Java 5 regardless of the JDK in use. We can modify these settings in the configuration element:
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.0</version>
            <configuration>
    		<release>11</release>
	</configuration>
        </plugin>
    </plugins>
</build>


Once the dependencies are set, make sure the maven dependencies are imported. Sometimes auto import can behave weirdly and manually doing it is always a way to guarantee that you have all the sources downloaded. You can just reimport project from the maven tool tab. 


3. We create a class to handle the request mappings of the client who wishes to go to /test page for example. We make it so that it is directed to the /WEB-INF/view/ directory and it reads .jsp files.

We will need to enable Spring MVC support through a Java configuration class, all we have to do is add the @EnableWebMvc annotation:
This will set up the basic support we need for an MVC project, such as registering controllers and mappings, and exception handling.
If we want to customize this configuration, we need to implement the WebMvcConfigurer interface:

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
    Important here is that we can register view controllers that create a direct mapping between the URL
    and the view name using the ViewControllerRegistry.
    */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/index");
    }

    
    //We register a ViewResolver bean that will return .jsp views from the /WEB-INF/view directory.
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setViewClass(JstlView.class);
        bean.setPrefix("/WEB-INF/view/");
        bean.setSuffix(".jsp");
        return bean;
    }

}

You can add several viewResolvers to handle different requests and map other directories, for example if you would like to handle Thymeleaf files

@Bean
public ServletContextTemplateResolver templateResolver() {
    final ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
    templateResolver.setPrefix("/WEB-INF/templates/");
    templateResolver.setSuffix(".html”);
    return templateResolver;
}



4. Next step we create a controller to handle mapped requests. @GetMapping annotation helps to map a certain path that you enter in the URL and return your desired function. Currently we are returning a test.jsp file which we will create. As we configured the ClientWebConfig to handle views for the .jsp files, the controller will search in the /WEB-INF/view/ path for the tsp that the controller should return. As we return “test”, then the servlet will search for /WEB-INF/view/test.jsp file in the webapp folder. 
Depending if you created a sample maven web application the folder can be already there, but if it is not there, just create a webapp folder in /src/main/webapp. Under there you should add WEB-INF folder and folders where you would like to store your static files. 

Here is the SampleController class
package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SampleController {

    @GetMapping(“/test”)
    public String saySomething() {
        return “test”;
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "hello";
    }

    @GetMapping(value = "/")
    public String homepage() {
        return "index";
    }
}


Here is the test.jsp file which we create under /webapp/WEB-INF/view folder.
<html>
<head>Testing JRebel</head>
<body>
<h1>
    Testing Spring MVC in action!
</h1>
</body>
</html>



5. Now we need to create a Spring Application initialiser which will implement the logic to configure ServletContext (handles the logic of understanding requests, send it to the correct place and send the response back to the calling party) programmatically in comparison to the traditional way of web.xml

package com.Initializer;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class MainWebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(final ServletContext sc) throws ServletException {

        AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();

	// Your applications root path.
        root.scan(("com"));
        sc.addListener(new ContextLoaderListener(root));

        ServletRegistration.Dynamic appServlet =
                sc.addServlet("mvc", new DispatcherServlet(new GenericWebApplicationContext()));
        appServlet.setLoadOnStartup(1);
        appServlet.addMapping("/");
    }
}



6. All the necessary logic for basic Spring MVC application is now here. We can launch the application on a server of your own preference (using Wildly 16 for this example) . We run “mvn clean package” to create a new artefact, which for us will be war that we defined in the pom.xml
Assign the war artifact to the Wildly server via the Intellij Wildly run configuration.
Run the Wildfly configuration from the IDE. 
You can then access the test.jsp at localhost:8080/springmvc-1.0/test


This is my understanding of the guide from Baeldung and what I had to do different to set it up myself. (https://www.baeldung.com/spring-mvc-tutorial) . Great guides and check him out as he goes a lot deeper into it.
