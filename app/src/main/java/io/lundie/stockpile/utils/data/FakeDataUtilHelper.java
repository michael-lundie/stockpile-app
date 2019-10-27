package io.lundie.stockpile.utils.data;

import java.util.Random;

public class FakeDataUtilHelper {

    public static String getRandomString(String[] array, Random random) {
        int index = random.nextInt(array.length);
        return array[index];
    }

    public static int getRandomInt(int[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }
}
