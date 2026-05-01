package com.hello.hello.controller;

import com.hello.hello.service.ConstructorInjectionNotificationService;
import com.hello.hello.service.FieldInjectionNotificationService;
import com.hello.hello.service.SetterInjectionNotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notify")
public class DependencyInjectionDemoController {

    private final ConstructorInjectionNotificationService constructorInjectionService;
    private final FieldInjectionNotificationService fieldInjectionService;
    private final SetterInjectionNotificationService setterInjectionService;

    public DependencyInjectionDemoController(
            ConstructorInjectionNotificationService constructorInjectionService,
            FieldInjectionNotificationService fieldInjectionService,
            SetterInjectionNotificationService setterInjectionService) {
        this.constructorInjectionService = constructorInjectionService;
        this.fieldInjectionService = fieldInjectionService;
        this.setterInjectionService = setterInjectionService;
    }

    @GetMapping("/field")
    public String fieldInjectionDemo() {
        return "Field Injection -> " + fieldInjectionService.sendEmail("Test");
    }

    @GetMapping("/constructor")
    public String constructorInjectionDemo() {
        return "Constructor Injection -> " + constructorInjectionService.sendEmail("Test");
    }

    @GetMapping("/setter")
    public String setterInjectionDemo() {
        return "Setter Injection -> " + setterInjectionService.sendEmail("Test");
    }
}
