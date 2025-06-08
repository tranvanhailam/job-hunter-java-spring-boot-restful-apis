package vn.kyler.job_hunter.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.kyler.job_hunter.service.exception.IdInvalidException;

@RestController
public class HelloController {

    @GetMapping("/")
    // @CrossOrigin
    public String getHelloWorld() throws IdInvalidException {
        return "Hello World !";
    }
}
