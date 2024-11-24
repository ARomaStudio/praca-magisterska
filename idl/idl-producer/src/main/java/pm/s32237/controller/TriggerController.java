package pm.s32237.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pm.producer.s32237.ProtobufExample;
import pm.s32237.service.SdkClientService;

@RestController
public class TriggerController {

    private final SdkClientService service;
    private final ProtobufHttpMessageConverter protobufHttpMessageConverter;

    public TriggerController(SdkClientService service, ProtobufHttpMessageConverter protobufHttpMessageConverter) {
        this.service = service;
        this.protobufHttpMessageConverter = protobufHttpMessageConverter;
    }

    @GetMapping(value = "/api", produces = "application/json")
    public String trigger() {
        protobufHttpMessageConverter.getSupportedMediaTypes();
        return service.sendRequest().toString();
    }

}
