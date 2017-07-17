package com.example.demo.controller;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;

/**
 * Created by rvann on 7/17/17.
 */
@RestController
@RequestMapping("demo")
@RefreshScope
public class DemoController {
    @Value("${foo}")
    private String foo;
    @Value("${demo.message.text}")
    private String messageText;
    @Value("${config.alt.url}")
    private String configAltUrl;

    private RestTemplate restTemplate;

    @Autowired
    public DemoController(RestTemplate configRestTemplate){
        this.restTemplate = configRestTemplate;
    }

    @RequestMapping(path = "message", method = RequestMethod.GET)
    public String getMessage() {
        return MessageFormat.format(messageText, foo);
    }

    @RequestMapping(path = "{fileName}.json", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public String getJson(@PathVariable final String fileName, final HttpServletRequest request){
        String fileToFind = FilenameUtils.getName(request.getRequestURL().toString());
        String url = MessageFormat.format(configAltUrl, fileToFind);

        return restTemplate.getForObject(url, String.class);
    }

    @RequestMapping(path = {"{fileName}.yaml", "{fileName}.yml"}, method = RequestMethod.GET)
    public String getYaml(@PathVariable final String fileName, final HttpServletRequest request) throws Exception {
        String fileToFind = FilenameUtils.getName(request.getRequestURL().toString());
        String url = MessageFormat.format(configAltUrl, fileToFind);

        return restTemplate.getForObject(url, String.class);
    }
}
