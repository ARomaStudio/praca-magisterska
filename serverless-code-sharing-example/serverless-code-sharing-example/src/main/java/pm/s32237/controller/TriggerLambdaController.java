package pm.s32237.controller;

import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pm.s32237.service.SdkClientService;

@RestController
public class TriggerLambdaController {

    private final SdkClientService service;

    public TriggerLambdaController(SdkClientService service) {
        this.service = service;
    }

    @GetMapping("/api")
    public String trigger() throws InvalidProtocolBufferException {
        return service.invokeLambda("", "");
    }

}
