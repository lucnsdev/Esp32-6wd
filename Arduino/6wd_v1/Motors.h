#include <Arduino.h>
#include <Delay.h>
#include "DataModel.h";

#define MOTOR_LEFT_FORWARD 13
#define MOTOR_LEFT_BACKWARD 15
#define MOTOR_RIGHT_FORWARD 12
#define MOTOR_RIGHT_BACKWARD 14

class Motors {
  private:
    Delay delayMotorsLeft, delayMotorsRight;
    DataModel dataModel;
    void applyMotorsCommands();
  public:
    Motors();
    void begin();
    void putData(DataModel dataModel);
    void compute();
    void disable();
};
