#ifndef MOTORS_H
#define MOTORS_H

#include <Arduino.h>

class DataModel {
  public:
    DataModel();
    char* characters;
    void retrieve();
    int pwmLeftForward, pwmLeftBackward, pwmRightForward, pwmRightBackward;
    int pwmLedsFront;
    int cameraState, frameSize;
    bool buzzerState;
    bool disconnect, invalidCharacters, isRunForward, isRunBackward, isRotationLeft, isRotationRight;
};
#endif
