package pm.s32237.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pm.s32237.SdkRequestDto;
import pm.s32237.SdkResponseDto;

@Service
public class SdkClientService {

    public static final String SDK_PRODUCER_URL = "http://localhost:8080/api";
    private final RestTemplate restTemplate;

    public SdkClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SdkResponseDto sendRequest() {
        ResponseEntity<SdkResponseDto> sdkResponseDtoResponseEntity = restTemplate.postForEntity(SDK_PRODUCER_URL, new SdkRequestDto(), SdkResponseDto.class);
        return sdkResponseDtoResponseEntity.getBody();
    }

}
