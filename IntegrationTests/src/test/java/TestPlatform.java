import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.SpringApplication;

public class TestPlatform implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {

        SpringApplication.run(TestedApps.class);

    }
}