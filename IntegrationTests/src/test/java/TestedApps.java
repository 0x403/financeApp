import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.example.TransactionService", "com.example.UserService"})
//@EnableJpaRepositories(basePackages={"com.example.UserService.repository.*","com.example.TransactionService.repository.*"})
//@EntityScan(basePackages={"com.example.UserService.entity.*"})
public class TestedApps {
    public static void main( String[] args )
    {
        SpringApplication.run(TestedApps.class, args);
    }
}