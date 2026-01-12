// Developed by @lucns
// Esp32 Arduino Board version 2.0.6

#include <freertos/FreeRTOS.h>
#include <freertos/task.h>
#include "MyWiFiUdp.h"
#include <WiFi.h>
#include "esp_camera.h"
#include "Motors.h"
#include "DataProvider.h"
#include <Delay.h>
#include "SimpleBuzzer.h"

#define LEDS_FRONT 2
#define BATTERY 33
#define LASER_OUT 0
//#define LASER_IN 32
#define BUZZER 32
#define CAMERA_MODEL_WROVER_KIT
#include "camera_pins.h"

const char *ssid = "Esp32_Cam_6wd";
const char *password = "12345678";
const int port = 1234;

MyWiFiUdp udp;
IPAddress ip(192, 168, 1, 1);
IPAddress netmask(255, 255, 255, 0);
IPAddress androidIp;
uint16_t androidPort;
Motors motors;
DataProvider dataProvider;
Delay delayBattery;
SimpleBuzzer buzzer;
bool cameraEnabled = true;

void sendImage(uint8_t* buf, uint32_t len) {
  //long startTime = millis();
  String s = "image " + String(len);
  uint8_t b[s.length()];
  for (int i = 0; i < s.length(); i++) b[i] = (uint8_t) s.charAt(i);
  udp.beginPacket(androidIp, androidPort);
  udp.write(b, s.length());
  udp.endPacket();

  int rest = len % MAXIMUM_UDP_PAYLOAD;
  int parcels = (len - rest) / MAXIMUM_UDP_PAYLOAD;

  for (int a = 0; a < parcels; a++) {
    udp.beginPacket(androidIp, androidPort);
    for (int b = 0; b < MAXIMUM_UDP_PAYLOAD; b++) {
      udp.write(buf[(a * MAXIMUM_UDP_PAYLOAD) + b]);
    }
    udp.endPacket();
  }

  if (rest) {
    udp.beginPacket(androidIp, androidPort);
    for (int a = 0; a < rest; a++) {
      udp.write(buf[(parcels * MAXIMUM_UDP_PAYLOAD) + a]);
    }
    udp.endPacket();
    rest = 1;
  }
  /*
    long endTime = millis();
    Serial.print("packets:");
    Serial.print(parcels + rest);
    Serial.print(" time:");
    Serial.println(endTime - startTime);
    //return parcels + rest;
  */
}

void sendBatteryStatus() {
  String s = "status " + String(analogRead(BATTERY));
  uint8_t b[s.length()];
  for (int i = 0; i < s.length(); i++) b[i] = (uint8_t) s.charAt(i);
  udp.beginPacket(androidIp, androidPort);
  udp.write(b, s.length());
  udp.endPacket();
}

void run2(void *arg) {
  bool bound = false;
  camera_fb_t* fb = NULL;
  while (1) {
    //if (androidPort == 0 || !cameraEnabled) {
    if (androidPort == 0) {
      bound = false;
      vTaskDelay(100);
      delayBattery.cancel();
      continue;
    }

    if (!bound || delayBattery.gate()) {
      bound = true;
      delayBattery.reset();
      sendBatteryStatus();
    }

    fb = esp_camera_fb_get(); // camera frame capture
    if (!fb) {
      Serial.println("Camera capture failed!");
      esp_camera_fb_return(fb);
      continue;
    }

    sendImage(fb->buf, fb->len);
    esp_camera_fb_return(fb);
  }
}

void run(void *arg) {
  while (1) {
    while (WiFi.softAPgetStationNum() == 0) { // wait for client connection
      androidPort = 0;
      analogWrite(LEDS_FRONT, 0);
      vTaskDelay(500);
      analogWrite(LEDS_FRONT, 1023);
      Serial.println("Wait for Wifi client...");
      vTaskDelay(500);
    }

    // receiver data from android
    int packetSize = udp.parsePacket();
    if (packetSize) {
      char packetBuffer[packetSize + 1];
      int size = udp.read(packetBuffer, packetSize + 1);
      packetBuffer[packetSize] = '\0';
      if (size) {
        androidIp = udp.remoteIP();
        androidPort = udp.remotePort();
        DataModel data = dataProvider.retrieve(packetBuffer);
        if (data.invalidCharacters) {
          Serial.print("invalid chars on:");
          Serial.println(packetBuffer);
        }
        motors.putData(data);
        if (data.pwmLedsFront >= 0) analogWrite(LEDS_FRONT, data.pwmLedsFront);
        if (data.cameraState == 1) cameraEnabled = true;
        else if (data.cameraState == 0) cameraEnabled = false;
        if (data.buzzerState) buzzer.start();
        else buzzer.stop();
      }
    }
    motors.compute();
    buzzer.compute();

    if (androidPort == 0) {
      motors.disable();
      analogWrite(LEDS_FRONT, 0);
      vTaskDelay(250);
      analogWrite(LEDS_FRONT, 1023);
      Serial.println("Wait for UDP client...");
      vTaskDelay(250);
    }
    //Serial.println(digitalRead(LASER_IN));
  }
}

void setup() {
  Serial.begin(115200);
  while (millis() < 1000);

  pinMode(LEDS_FRONT, OUTPUT);
  pinMode(BATTERY, INPUT);
  //pinMode(LASER_IN, INPUT);
  pinMode(LASER_OUT, OUTPUT);

  buzzer.setPin(BUZZER);
  motors.begin();
  delayBattery.setTime(5000UL);

  // OV2640 camera module
  camera_config_t config;
  config.ledc_channel = LEDC_CHANNEL_0;
  config.ledc_timer = LEDC_TIMER_0;
  config.pin_d0 = Y2_GPIO_NUM;
  config.pin_d1 = Y3_GPIO_NUM;
  config.pin_d2 = Y4_GPIO_NUM;
  config.pin_d3 = Y5_GPIO_NUM;
  config.pin_d4 = Y6_GPIO_NUM;
  config.pin_d5 = Y7_GPIO_NUM;
  config.pin_d6 = Y8_GPIO_NUM;
  config.pin_d7 = Y9_GPIO_NUM;
  config.pin_xclk = XCLK_GPIO_NUM;
  config.pin_pclk = PCLK_GPIO_NUM;
  config.pin_vsync = VSYNC_GPIO_NUM;
  config.pin_href = HREF_GPIO_NUM;
  config.pin_sscb_sda = SIOD_GPIO_NUM;
  config.pin_sscb_scl = SIOC_GPIO_NUM;
  config.pin_pwdn = PWDN_GPIO_NUM;
  config.pin_reset = RESET_GPIO_NUM;
  config.xclk_freq_hz = 20000000;
  config.pixel_format = PIXFORMAT_JPEG;
  config.grab_mode = CAMERA_GRAB_LATEST; // CAMERA_GRAB_WHEN_EMPTY
  config.fb_location = CAMERA_FB_IN_PSRAM; // CAMERA_FB_IN_DRAM
  config.jpeg_quality = 10;
  config.fb_count = 4; // 1 is slower fps
  config.frame_size = FRAMESIZE_SVGA; // 800x600 - 25fps
  // camera init
  esp_err_t err = esp_camera_init(&config);
  if (err != ESP_OK) {
    Serial.printf("Camera init failed with error 0x%x", err);
    return;
  }

  WiFi.softAPConfig(ip, ip, netmask);
  WiFi.softAP(ssid, password);

  udp.begin(port);
  Serial.println("Initializing tasks...");

  //xTaskCreatePinnedToCore(run, "ControllerTask", 2048, NULL, 4, NULL, APP_CPU_NUM);
  //xTaskCreatePinnedToCore(run2, "TaskOnProtocol", 2048, NULL, 8, NULL, PRO_CPU_NUM);
  xTaskCreate(&run, "ControllerTask", 4096, NULL, tskIDLE_PRIORITY, NULL);
  xTaskCreate(&run2, "CameraImageSenderTask", 4096, NULL, tskIDLE_PRIORITY, NULL);
  Serial.println("Ready");
}

void loop() {
  // not working
}
