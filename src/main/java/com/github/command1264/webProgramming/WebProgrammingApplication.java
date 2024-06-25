package com.github.command1264.webProgramming;

import com.github.command1264.webProgramming.dao.SqlDao;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class WebProgrammingApplication {

//	private static ConfigurableApplicationContext context;
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(WebProgrammingApplication.class);
//		app.setDefaultProperties(Collections.singletonMap("server.port", "60922"));
		ApplicationContext context = app.run(args);
		try {
			SqlDao sqlDao = (SqlDao) context.getBean("sqlDao");
			sqlDao.initTable();
		} catch (Exception e ){
			e.printStackTrace();
		}
	}

//	@Bean
//	public WebMvcConfigurer corsConfigurer() {
//		return new WebMvcConfigurer() {
//			@Override
//			public void addCorsMappings(@NotNull CorsRegistry registry) {
////				final String ip = "https://command1264.xserver.tw";
//				final String ip = "*";
//				registry.addMapping("/ping").allowedOrigins(ip);
//				registry.addMapping("/test").allowedOrigins(ip);
//				registry.addMapping("/api/v1/loginAccount").allowedOrigins(ip);
//				registry.addMapping("/api/v1/createAccount").allowedOrigins(ip);
//				registry.addMapping("/api/v1/getUser").allowedOrigins(ip);
//				registry.addMapping("/api/v1/getAccount").allowedOrigins(ip);
//				registry.addMapping("/api/v1/getUserChatRoom").allowedOrigins(ip);
//				registry.addMapping("/api/v1/createUserChatRoom").allowedOrigins(ip);
//				registry.addMapping("/api/v1/getUserChatRoomChat").allowedOrigins(ip);
//				registry.addMapping("/api/v1/userSendMessage").allowedOrigins(ip);
//				registry.addMapping("/api/v1/getUserReceiveMessage").allowedOrigins(ip);
//			}
//		};
//	}


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
