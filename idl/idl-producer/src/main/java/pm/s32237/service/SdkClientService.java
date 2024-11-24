package pm.s32237.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pm.producer.s32237.ProtobufExample;

@Service
public class SdkClientService {

    public static final String SDK_PRODUCER_URL = "http://localhost:8080/api";
    private final RestTemplate restTemplate;

    public SdkClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ProtobufExample.ResponseDto sendRequest() {
        ResponseEntity<ProtobufExample.ResponseDto> sdkResponseDtoResponseEntity = restTemplate.postForEntity(SDK_PRODUCER_URL, ProtobufExample.RequestDto.newBuilder().setId(1).build(), ProtobufExample.ResponseDto.class);
        return sdkResponseDtoResponseEntity.getBody();
    }

}
