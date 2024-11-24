package pm.s32237.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pm.s32237.SdkResponseDto;
import pm.s32237.service.SdkClientService;

@RestController
public class TriggerController {

    private final SdkClientService service;

    public TriggerController(SdkClientService service) {
        this.service = service;
    }

    @GetMapping("/api")
    public SdkResponseDto trigger() {
        return service.sendRequest();
    }

}
