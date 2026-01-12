#ifndef SIMPLE_BUZZER_H
#define SIMPLE_BUZZER_H

#include <Arduino.h>
#include <Delay.h>

#define DEFAULT_FREQUENCY 500
#define DEFAULT_TIMEOUT 250UL

class SimpleBuzzer {
  private:
    Delay delay;
		int frequency;
		int pin;
		bool playing;
  public:
    SimpleBuzzer();
		void setFrequency(int frequency);
		void setTimeout(long timeout);
		void setPin(int pin);
		void start();
		void stop();
		void compute();
};
#endif
