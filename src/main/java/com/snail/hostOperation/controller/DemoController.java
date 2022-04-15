package com.snail.hostOperation.controller;

import org.springframework.web.bind.annotation.*;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/demo")
public class DemoController {

    private String acb = "";

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }

    @GetMapping("/pushImage")
    public String pushImage(@RequestParam(value = "from", defaultValue = "harbor.dcos.xixian.unicom.local/common/nginx-standalone-v1.5:1.20.1") String from,
            @RequestParam(value = "to", defaultValue = "harbor.dcos.xixian.unicom.local/common/nginx-standalone-v1.5:1.20.1") String to) {
        String shStr = "docker pull "+from;
        StringBuilder sb = new StringBuilder();
        StringBuilder sbt = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh","-c",shStr},null,null);
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            process.waitFor();
            while ((line = input.readLine()) != null){
                sb.append(line);
            }
            if(sbt.toString().contains("complete")||sb.toString().contains("Image is up")){
                String tabStr = "docker tag "+from+" "+to;
                Runtime.getRuntime().exec(new String[]{"/bin/sh","-c",tabStr},null,null);
                Thread.sleep(5000);
                String puStr = "docker push "+to;
                Process processt = Runtime.getRuntime().exec(new String[]{"/bin/sh","-c",puStr},null,null);
                InputStreamReader irt = new InputStreamReader(processt.getInputStream());
                LineNumberReader inputt = new LineNumberReader(irt);
                String linet;
                process.waitFor();
                while ((linet = inputt.readLine()) != null){
                    sbt.append(linet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sbt.toString();
    }


    @GetMapping("/redis")
    public List<String> hello() {
        String shStr = "redis-cli info memory";
        List<String> strList = new ArrayList<String>();
       try {
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh","-c",shStr},null,null);
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            process.waitFor();
            while ((line = input.readLine()) != null){
                strList.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        strList = strList.stream().filter(t->t.contains("used_memory_peak_perc")).collect(Collectors.toList());
        return strList;
    }
}