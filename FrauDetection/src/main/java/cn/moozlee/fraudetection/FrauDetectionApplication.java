package cn.moozlee.fraudetection;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.moozlee.fraudetection.mapper")
public class FrauDetectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrauDetectionApplication.class, args);
    }

}
