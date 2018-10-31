package controllers;

import classes.Time;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import okhttp3.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.time.LocalTime;

@SpringBootApplication
public class Application {
    public static GpioPinDigitalInput doorSensor;
    public static GpioPinDigitalOutput buzzer;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        run();
    }

    public static void run(){
        setup();
        addListener();

        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
                System.out.println(ex.getStackTrace());
            }
        }
    }

    public static void setup(){
        GpioController gpio = GpioFactory.getInstance();
        doorSensor = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_UP);
        buzzer = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04);
        buzzer.high();
    }

    public static void addListener() {
        doorSensor.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                boolean doorOpen = event.getState() == PinState.HIGH;

                if (Time.isTimeBetweenTimes(LocalTime.parse(Time.getCurrentTime())) && doorOpen) {
                    buzzer.low();
                } else {
                    buzzer.high();
                }

                String timeStamp = Time.getTimeStamp();

                String json = "{\"doorOpen\":" + doorOpen + ",\"timeStamp\":\"" + timeStamp + "\"}";

                OkHttpClient client = new OkHttpClient();

                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, json);
                Request request = new Request.Builder()
                        .url("http://localhost:8080/statueses")
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("Postman-Token", "860b2525-f278-4a0c-abec-8c1c4ae64e80")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}