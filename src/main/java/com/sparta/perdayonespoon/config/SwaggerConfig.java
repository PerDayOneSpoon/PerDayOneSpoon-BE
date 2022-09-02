package com.sparta.perdayonespoon.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {  // Swagger

    private static final String API_NAME = "하루한줌 프로젝트 API";
    private static final String API_VERSION = "0.0.1";
    private static final String API_DESCRIPTION = "하루한줌 프로젝트 API 명세서";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                // 스웨거에서 헤더에 토큰을 받기위한 설정들 추가
                .securityContexts(Arrays.asList(securityContext(),securityContexts()))
                .securitySchemes(Arrays.asList(accesstoken(),refreshtoken()))
                // 스웨거에서 헤더에 토큰을 받기위한 설정들 추가
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sparta.perdayonespoon"))  // Swagger를 적용할 클래스의 package명
                .paths(PathSelectors.any())  // 해당 package 하위에 있는 모든 url에 적용
                .build()
                .apiInfo(apiInfo());
    }

    public ApiInfo apiInfo() {  // API의 이름, 현재 버전, API에 대한 정보
        return new ApiInfoBuilder()
                .title(API_NAME)
                .version(API_VERSION)
                .description(API_DESCRIPTION)
                .build();
    }

    // 스웨거에서 헤더에 토큰을 받기위한 설정들 추가
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    private SecurityContext securityContexts() {
        return SecurityContext.builder()
                .securityReferences(defaultOauth())
                .build();
    }
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("Authorization", authorizationScopes));
    }

    private List<SecurityReference> defaultOauth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("refreshtoken", authorizationScopes));
    }

    private ApiKey accesstoken() {
        return new ApiKey("Authorization", "Authorization", "header");
    }
    private ApiKey refreshtoken() {
        return new ApiKey("refreshtoken", "refreshtoken", "header");
    }
}
