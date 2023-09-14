package com.snort.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.snort.enums.Constants.*;

@Configuration
@Slf4j
@EnableWebMvc
@EnableSwagger2
public class SwaggerConfigue {
    //http://localhost:8181/swagger-ui/index.html#/
    ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title(CUSTOMER_ORDERS_APPLICATION.getValue())
                .description(REQUIREMENT_ORDERS_APPLICATION.getValue())
                .version(VERSION.getValue())
                .license(LICENCE.getValue())
                .contact(new Contact(MAUSOOF_AZAM.getValue(), WEBSITE.getValue(), EMAIL.getValue())).build();
    }
    //Docket Bean
    @Bean
    public Docket getDocket() {
        log.info("Api Build Successfully..Docket Bean is Created");
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(getApiInfo()).forCodeGeneration(true)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any()).build();
    }

}