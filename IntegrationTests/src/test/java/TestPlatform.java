import com.example.TransactionService.TransactionServiceApplication;
import com.example.UserService.UserServiceApplication;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class TestPlatform implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {

//        TestedApps.main(new String[0]);
        LocalElasticsearch.start();
        SpringApplication.run(TestedApps.class);
//        SpringApplicationBuilder userService = new SpringApplicationBuilder(UserServiceApplication.class).properties("server.port=9091");
//        SpringApplicationBuilder transactionService = new SpringApplicationBuilder(UserServiceApplication.class).properties("server.port=9092");
//        userService.run();
//        transactionService.run();
    }
}