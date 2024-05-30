package com.github.command1264.webProgramming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebProgrammingApplication {

//	private static ConfigurableApplicationContext context;
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(WebProgrammingApplication.class);
//		app.setDefaultProperties(Collections.singletonMap("server.port", "60922"));
		app.run(args);
	}

//	public static void restart() {
//		ApplicationArguments args = context.getBean(ApplicationArguments.class);
//
//		Thread thread = new Thread(() -> {
//			context.close();
//			context = SpringApplication.run(WebProgrammingApplication.class, args.getSourceArgs());
//		});
//
//		thread.setDaemon(false);
//		thread.start();
//	}
}
