package com.example.demo.controller;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by rvann on 7/17/17.
 */
@RestController
@RequestMapping("demo")
public class DemoController {
    @Value("${foo}")
    private String foo;
    private RestTemplate restTemplate;

    @Autowired
    public DemoController(RestTemplate configRestTemplate){
        this.restTemplate = configRestTemplate;
    }

    @RequestMapping(path = "message", method = RequestMethod.GET)
    public String getMessage() {
        return "the value contained in property {foo} is [" + this.foo + "]";
    }

    @RequestMapping(path = "{application}/{fileName}.json", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public String getJson(@PathVariable final String application, @PathVariable final String fileName){
        String url = "http://localhost:8888/alternate/" + application + "/" + fileName + ".json";

        return restTemplate.getForObject(url, String.class);
    }

    @RequestMapping(path = {"{application}/{fileName}.yaml", "{application}/{fileName}.yml"}, method = RequestMethod.GET)
    public String getYaml(@PathVariable final String application, @PathVariable final String fileName, final HttpServletRequest request) throws Exception {
        String fileToFind = FilenameUtils.getName(request.getRequestURL().toString());
        String url = "http://localhost:8888/alternate/" + application + "/" + fileToFind;

        return restTemplate.getForObject(url, String.class);
    }
}
