import com.example.UserService.dto.UserDto;
import com.example.UserService.entity.UserEntity;
import com.example.UserService.repository.UserRepository;
import com.example.UserService.service.UserServiceImpl;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, TestPlatform.class})
public class IntegrationTests {

//    @InjectMocks
//    UserServiceImpl userService;

//    @Mock
//    UserRepository userRepository;

//    @Mock
//    BCryptPasswordEncoder bCryptPasswordEncoder;
//
//    String firstName = "John";
//    String encryptedPassword = "fjhfjdsafjaks";

//    @Test
//    void createUserTest() {
//
//        UserEntity userEntity = new UserEntity();
//        userEntity.setUsername(firstName);
//        userEntity.setEncryptedPassword(encryptedPassword);
//
//        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
//        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
//
//        ModelMapper modelMapper = new ModelMapper();
//        UserDto userDto = modelMapper.map(userEntity, UserDto.class);
//        UserDto storedUserDetails = userService.createUser(userDto);
//
//        assertNotNull(storedUserDetails);
//        assertEquals(firstName, storedUserDetails.getUsername());
//        assertEquals(encryptedPassword, storedUserDetails.getEncryptedPassword());
//
//    }

    @Test
    void createUserEndpointTest() throws IOException {

        CloseableHttpClient client = HttpClients.createDefault();

        HttpPost createUserRequest = new HttpPost( "http://localhost:9090/users");
        createUserRequest.setHeader("Content-Type", "application/json");
        createUserRequest.setEntity(new StringEntity("{\"username\":\"johnny\",\"password\":\"123\"}"));

        CloseableHttpResponse response = client.execute(createUserRequest);

        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        assertEquals("{\"username\":\"johnny\"}", EntityUtils.toString(response.getEntity()));

        client.close();
    }

    @Test
    void loginUserEndpointTest() throws IOException {

        CloseableHttpClient client = HttpClients.createDefault();

        HttpPost request = new HttpPost( "http://localhost:9090/login");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity("{\"username\":\"johnny\",\"password\":\"123\"}"));

        CloseableHttpResponse reponse = client.execute(request);

        assertEquals(HttpStatus.SC_OK, reponse.getStatusLine().getStatusCode());

        client.close();
    }

//    @Test
//    void getTransactionTestEndpoint() throws IOException {
//
//        HttpGet request = new HttpGet( "http://localhost:9090/transactions/nickname");
//        request.setHeader("Content-Type", "application/json");
//
//        HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
//
//        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
//    }

}
