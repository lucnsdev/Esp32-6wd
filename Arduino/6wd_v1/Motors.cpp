#include "Motors.h"

Motors::Motors() {}

void Motors::begin() {
  pinMode(MOTOR_LEFT_FORWARD, OUTPUT);
  pinMode(MOTOR_LEFT_BACKWARD, OUTPUT);
  pinMode(MOTOR_RIGHT_FORWARD, OUTPUT);
  pinMode(MOTOR_RIGHT_BACKWARD, OUTPUT);
  disable();

  delayMotorsLeft.setTime(250);
  delayMotorsRight.setTime(250);
}

void Motors::applyMotorsCommands() {
  //Serial.print("Motors ");
  //Serial.print("left:");
  if (dataModel.pwmLeftForward > 0) {
    //Serial.print(dataModel.pwmLeftForward);
    delayMotorsLeft.reset();
    //Serial.println("Motors left on forward");
    analogWrite(MOTOR_LEFT_BACKWARD, 0);
    analogWrite(MOTOR_LEFT_FORWARD, dataModel.pwmLeftForward);
  } else if (dataModel.pwmLeftBackward > 0) {
    //Serial.print(dataModel.pwmLeftBackward);
    //Serial.println("Motors left on backward");
    delayMotorsLeft.reset();
    analogWrite(MOTOR_LEFT_FORWARD, 0);
    analogWrite(MOTOR_LEFT_BACKWARD, dataModel.pwmLeftBackward);
  } else {
    //Serial.println("Motors left off");
    delayMotorsLeft.cancel();
    analogWrite(MOTOR_LEFT_BACKWARD, 0);
    analogWrite(MOTOR_LEFT_FORWARD, 0);
  }

  //Serial.print(",  right:");
  if (dataModel.pwmRightForward > 0) {
    //Serial.println(dataModel.pwmRightForward);
    //Serial.println("Motors right on forward");
    delayMotorsRight.reset();
    analogWrite(MOTOR_RIGHT_BACKWARD, 0);
    analogWrite(MOTOR_RIGHT_FORWARD, dataModel.pwmRightForward);
  } else if (dataModel.pwmRightBackward > 0) {
    //Serial.println(dataModel.pwmRightBackward);
    //Serial.println("Motors right on backward");
    delayMotorsRight.reset();
    analogWrite(MOTOR_RIGHT_FORWARD, 0);
    analogWrite(MOTOR_RIGHT_BACKWARD, dataModel.pwmRightBackward);
  } else {
    //Serial.println("Motors right off");
    delayMotorsRight.cancel();
    analogWrite(MOTOR_RIGHT_BACKWARD, 0);
    analogWrite(MOTOR_RIGHT_FORWARD, 0);
  }
}

void Motors::putData(DataModel data) {
  if (data.disconnect) {
    disable();
    return;
  }
  dataModel = data;
  applyMotorsCommands();
}

void Motors::compute() {
  if (delayMotorsLeft.gate()) {
    //Serial.println("Motors left disable timeout");
    analogWrite(MOTOR_LEFT_BACKWARD, 0);
    analogWrite(MOTOR_LEFT_FORWARD, 0);
  }
  if (delayMotorsRight.gate()) {
    //Serial.println("Motors right disable timeout");
    analogWrite(MOTOR_RIGHT_BACKWARD, 0);
    analogWrite(MOTOR_RIGHT_FORWARD, 0);
  }
}

void Motors::disable() {
  //Serial.println("Motors disable");
  analogWrite(MOTOR_RIGHT_BACKWARD, 0);
  analogWrite(MOTOR_RIGHT_FORWARD, 0);
  analogWrite(MOTOR_LEFT_BACKWARD, 0);
  analogWrite(MOTOR_LEFT_FORWARD, 0);
  delayMotorsLeft.cancel();
  delayMotorsRight.cancel();
}
