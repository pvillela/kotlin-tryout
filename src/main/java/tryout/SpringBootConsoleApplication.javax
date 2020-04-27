package tryout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootConsoleApplication implements CommandLineRunner {

    private DemoConfigurationProperties cfg;

    public SpringBootConsoleApplication(@Autowired DemoConfigurationProperties cfg) {
        this.cfg = cfg;
    }

    public static void main(String[] args) throws Exception {

        SpringApplication.run(SpringBootConsoleApplication.class, args);

    }

    //access command line arguments
    @Override
    public void run(String... args) throws Exception {

        System.out.println(cfg.getFoo());

    }
}
