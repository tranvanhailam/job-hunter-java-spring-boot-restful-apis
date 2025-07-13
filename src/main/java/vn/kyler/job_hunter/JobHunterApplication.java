package vn.kyler.job_hunter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class JobHunterApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobHunterApplication.class, args);
//		ApplicationContext abc = SpringApplication.run(JobHunterApplication.class, args);
//		for (String s : abc.getBeanDefinitionNames()) {
//			System.out.println(s + "------------");
//		}
	}

}
