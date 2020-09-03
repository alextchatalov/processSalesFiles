package com.processSalesFiles.salesFile;

import com.processSalesFiles.salesFile.threads.SalesThread;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SalesFileApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalesFileApplication.class, args);
		SalesThread salesThread = new SalesThread();
		salesThread.start();
	}

}
