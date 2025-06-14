package vn.kyler.job_hunter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.ApplicationContext;

@SpringBootApplication
//@ComponentScan(basePackages = "vn.kyler.job_hunter")
public class JobHunterApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobHunterApplication.class, args);
//		ApplicationContext abc = SpringApplication.run(JobHunterApplication.class, args);
//		for (String s : abc.getBeanDefinitionNames()) {
//			System.out.println(s + "------------");
//		}
	}

}
