package com.synisys.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WarlightApplication {

    public static String[] args;

	public static void main(String[] args) {
        WarlightApplication.args = args;
        SpringApplication.run(WarlightApplication.class, args);
	}
}
