package com.lieluobo.web

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ImportResource

@SpringBootApplication
@ImportResource( "classpath*:spring/*.xml" )
class MyprojWebApplication

fun main(args: Array<String>) {
    SpringApplication.run(MyprojWebApplication::class.java, *args)
}
