package com.synisys.ai;

import com.theaigames.game.warlight2.Warlight2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WarlightApplication {

	private static String [] args;
	WarlightApplication() throws Exception {
		Warlight2.main(args);
	}

	public static void main(String[] args) {
		WarlightApplication.args = args;
		SpringApplication.run(WarlightApplication.class, args);
	}
}
