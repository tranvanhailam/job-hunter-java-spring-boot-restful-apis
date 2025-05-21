package vn.kyler.job_hunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.kyler.job_hunter.service.IdInvalidException;

@RestController
public class HelloController {

    @GetMapping("/")
    public String getHelloWorld() throws IdInvalidException {
        // if (true)
        // throw new IdInvalidException("check exception");
        return "Hello World !!!";
    }
}
