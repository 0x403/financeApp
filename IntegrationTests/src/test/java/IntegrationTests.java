import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(TestPlatform.class)
public class IntegrationTests {

    @Test
    void firstTest() {
        assertTrue(true);
    }

}
