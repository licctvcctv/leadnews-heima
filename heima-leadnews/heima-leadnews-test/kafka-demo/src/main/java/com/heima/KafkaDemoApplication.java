package com.heima;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class KafkaDemoApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(KafkaDemoApplication.class,args);
    }
}
