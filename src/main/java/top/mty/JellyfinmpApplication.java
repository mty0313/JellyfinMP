package top.mty;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableFeignClients
@EnableTransactionManagement
@MapperScan("top.mty.mapper")
public class JellyfinmpApplication {

	public static void main(String[] args) {
		SpringApplication.run(JellyfinmpApplication.class, args);
	}

}
