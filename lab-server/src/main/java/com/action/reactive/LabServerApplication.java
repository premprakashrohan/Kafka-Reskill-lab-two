package com.action.reactive;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.ConfigurableApplicationContext;
@EnableEurekaServer
@SpringBootApplication
public class LabServerApplication {
	private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		args = new String[1];
		args[0]="--server.port=8086";
		context = SpringApplication.run(LabServerApplication.class, args);
	}

	public static void restart() {
		ApplicationArguments args = context.getBean(ApplicationArguments.class);
		Thread thread = new Thread(()->{
			context.close();
			context = SpringApplication.run(LabServerApplication.class, args.getSourceArgs());
		});
		thread.setDaemon(false);
		thread.start();
	}
}
