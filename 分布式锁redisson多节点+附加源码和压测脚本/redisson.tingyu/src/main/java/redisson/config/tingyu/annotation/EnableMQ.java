package redisson.config.tingyu.annotation;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import redisson.config.tingyu.configuration.MQConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ java.lang.annotation.ElementType.TYPE })
@Documented
@Import(MQConfiguration.class)
@Configuration
public @interface EnableMQ {

}
