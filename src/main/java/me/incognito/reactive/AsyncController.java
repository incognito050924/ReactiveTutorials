package me.incognito.reactive;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import lombok.extern.slf4j.Slf4j;

/**
 * Create by incognito on 2021/6/2
 */
@EnableAsync
@RestController
@RequestMapping("/async")
@Slf4j
public class AsyncController {
    private final AtomicReference<DeferredResult<String>> DR = new AtomicReference<>();

    @GetMapping("/get")
    public String get() {
        log.info("Start get()");
        final String val = expensiveJob();
        log.info("Finish get()");
        return val;
    }

    @GetMapping("/get/dr")
    public DeferredResult<String> getListenableFuture() {
        log.info("Start getDR()");
        final DeferredResult<String> dr = new DeferredResult<>();
        DR.set(dr);
        log.info("Finish getDR()");
        return dr;
    }

    @GetMapping("/set/dr/{val}")
    public Map<String, Object> setDrResult(@PathVariable final String val) {
        DR.get().setResult(val);
        return Collections.singletonMap("result", "success");
    }

    private static String expensiveJob() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Done";
    }
}
