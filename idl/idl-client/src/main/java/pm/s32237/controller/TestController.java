package pm.s32237.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pm.s32237.ProtobufExample;

@RestController
public class TestController {

    @PostMapping(value = "/api", consumes = "application/x-protobuf", produces = "application/x-protobuf")
    public ProtobufExample.ResponseDto post(@RequestBody ProtobufExample.RequestDto requestDto) {
        ProtobufExample.ResponseDto responseDto = ProtobufExample.ResponseDto.newBuilder()
                .setId(requestDto.getId())
                .build();
        return responseDto;
    }

}