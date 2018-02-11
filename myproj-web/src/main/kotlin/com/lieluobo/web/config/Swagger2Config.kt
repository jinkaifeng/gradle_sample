package com.lieluobo.web.config

import com.google.common.base.Predicates
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


@Configuration
@EnableSwagger2
class Swagger2Config : WebMvcConfigurerAdapter() {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry?) {
        registry!!.addResourceHandler("**/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/")
    }

    @Bean
    fun swaggerSpringMvcPlugin(): Docket {
        val apiInfo = apiInfo()
        return Docket(DocumentationType.SWAGGER_2).select()
                .apis(Predicates.and(RequestHandlerSelectors.basePackage("com.lieluobo"))).build().apiInfo(apiInfo)
                .useDefaultResponseMessages(false)
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder().title("基础平台 RESTful APIs").description("test12345678").termsOfServiceUrl(null)
                .contact(Contact("jinkafieng", "jinkaifeng.online", "jinkaifeng_@hotmail.com")).version("1.0.0")
                .build()
    }

}