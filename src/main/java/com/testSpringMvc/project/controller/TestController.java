package com.testSpringMvc.project.controller;

import com.spring.annotation.*;
import com.testSpringMvc.project.services.OneService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 12289
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    @Qualifier("wewefwef")
    OneService oneService;
 
    @RequestMapping(value = "/doTest",method = RequestMethod.GET)
    public String test1(HttpServletRequest request, HttpServletResponse response){
      return oneService.test1();
    }
  
  
    @RequestMapping(value = "/doTest2",method = RequestMethod.POST)
    public String test2(HttpServletRequest request, HttpServletResponse response){
      return "springboot doTest2 method success!";
    }
}