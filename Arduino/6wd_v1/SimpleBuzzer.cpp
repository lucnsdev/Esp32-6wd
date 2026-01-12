#include "SimpleBuzzer.h"

SimpleBuzzer::SimpleBuzzer() {
	frequency = DEFAULT_FREQUENCY;
	delay.setTime(DEFAULT_TIMEOUT);
}

void SimpleBuzzer::setPin(int pin) {
	this->pin = pin;
	pinMode(pin, OUTPUT);
}

void SimpleBuzzer::setFrequency(int frequency) {
	this->frequency = frequency;
}

void SimpleBuzzer::setTimeout(long timeout) {
	delay.setTime(timeout);
}

void SimpleBuzzer::start() {
	if (playing) {
		delay.reset();
		return;
	}
	playing = true;
	tone(pin, frequency);
}

void SimpleBuzzer::stop() {
	playing = false;
	noTone(pin);
}

void SimpleBuzzer::compute() {
	if (delay.gate()) stop();
}
