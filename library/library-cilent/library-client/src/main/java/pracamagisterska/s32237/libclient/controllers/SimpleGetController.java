package pracamagisterska.s32237.libclient.controllers;

@RestController
public class SimpleGetController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, World!";
    }

}
