package com.tus.coupon;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoadController {

  // Example: /burn?ms=500  --> ~0.5s CPU work
  @GetMapping("/burn")
  public String burn(@RequestParam(defaultValue = "300") long ms) {
    long end = System.currentTimeMillis() + ms;
    double x = 0;
    while (System.currentTimeMillis() < end) {
      // Tight loop to keep the CPU busy
      x += Math.sqrt(Math.random());
    }
    return "burned ~" + ms + "ms, x=" + x;
  }
}
