#include "DataModel.h"

DataModel::DataModel() {
  pwmLedsFront = -1;
  cameraState = -1;
  pwmLeftForward = 0;
  pwmLeftBackward = 0;
  pwmRightForward = 0;
  pwmRightBackward = 0;
  frameSize = 0;
  isRunForward = false;
  isRunBackward = false;
  isRotationRight = false;
  isRotationLeft = false;
  disconnect = false;
}

void DataModel::retrieve() {
  if (pwmLeftForward == 1023 && pwmRightBackward == 1023) {
    isRotationRight = true;
    //Serial.println("turn right");
  } else if (pwmLeftBackward == 1023 && pwmRightForward == 1023) {
    isRotationLeft = true;
    //Serial.println("turn left");
  } else if (pwmLeftForward > 0 && pwmRightForward > 0) {
    isRunForward = true;
    //Serial.println("run forward");
  } else if (pwmLeftBackward > 0 && pwmRightBackward > 0) {
    isRunBackward = true;
    //Serial.println("run backward");
  }

/*
    Serial.print("pwmLeftForward:");
    Serial.print(pwmLeftForward);
    Serial.print(" pwmLeftBackward:");
    Serial.print(pwmLeftBackward);
    Serial.print(" pwmRightForward:");
    Serial.print(pwmRightForward);
    Serial.print(" pwmRightBackward:");
    Serial.print(pwmRightBackward);
    Serial.print(" inRotation:");
    Serial.println((isRotationRight || isRotationLeft) ? "true" : "false");
    */

  if (frameSize) {
    Serial.print("Frame size:");
    Serial.println(frameSize);
  }
  //if (cameraState == 0) Serial.println("Camera disabled");
  //else if (cameraState == 1) Serial.println("Camera enabled");
  if (pwmLedsFront >= 0) {
    Serial.print("Leds front pwm:");
    Serial.println(pwmLedsFront);
  }
  if (disconnect) Serial.println("Disconnected by user");
  //Serial.print("data:");
  //Serial.println(characters);
}
