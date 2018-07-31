package pl.upaid.graphitedemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UpaidController {

    @GetMapping("/hello")
    String hello() {
        return "hello";
    }
}
