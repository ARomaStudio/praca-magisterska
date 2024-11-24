package pm.s32237.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pm.s32237.SdkRequestDto;
import pm.s32237.SdkResponseDto;

@RestController
public class TestController {

    @PostMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
    public SdkResponseDto post(@RequestBody SdkRequestDto requestDto) {
        SdkResponseDto sdkResponseDto = new SdkResponseDto();
        sdkResponseDto.setResponseDtoParametr("parametr");
        return sdkResponseDto;
    }

}
