package dit.hua.gr.greenride.config;

//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.client.RestTemplate;
//
///**
// * REST API client configuration for external services used by GreenRide.
// */
//@Configuration
//public class RestApiClientConfig {
//
//    // TODO: change this to the actual NOC base URL your professor has given you.
//    public static final String BASE_URL = "https://noc-demo.example.com";
//
//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }
//}
//

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestApiClientConfig {

    // @future Get me from application properties!
    public static final String BASE_URL = "http://localhost:8081";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}