package lucns.robot6wd.activities.controllers;

import android.util.Log;

public class Stabilizer {

    private final int ARRAY_LENGTH = 16;
    private final int[] myArray = new int[ARRAY_LENGTH];
    private int index;

    public int put(int newValue) {
        for (int i = index - 1; i > 0; i--) myArray[i] = myArray[i - 1];
        if (index < ARRAY_LENGTH) {
            index++;
        }
        myArray[0] = newValue;

        int value = 0;
        for (int i = 0; i < index; i++) value += myArray[i];
        return value / index;
    }
}
