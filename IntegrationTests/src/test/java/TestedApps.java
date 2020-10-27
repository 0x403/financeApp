import com.example.TransactionService.TransactionServiceApplication;
import com.example.UserService.UserServiceApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Collections;

@SpringBootApplication(scanBasePackages = {"com.example.TransactionService", "com.example.UserService"})
@EntityScan(basePackages={"com.example.UserService.entity"})
@EnableJpaRepositories(basePackages={"com.example.UserService.repository","com.example.TransactionService.repository"})
public class TestedApps {
    public static void main( String[] args )
    {
//        SpringApplication userServiceApp = new SpringApplication(UserServiceApplication.class);
//        userServiceApp.setDefaultProperties(Collections.singletonMap("server.port", "9091"));
//        userServiceApp.run(args);
//
//        SpringApplication transactionsServiceApp = new SpringApplication(TransactionServiceApplication.class);
//        transactionsServiceApp.setDefaultProperties(Collections.singletonMap("server.port", "9092"));
//        transactionsServiceApp.run(args);

        SpringApplication.run(TestedApps.class, args);
    }
}