package com.example.smd_lab8;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Singleton {
    private static Singleton singleton = new Singleton();
    private KeyGenerator KG = null;
    private SecretKey SK = null;

    private Singleton()
    {
    }

    public static Singleton getInstance() {
        return singleton;
    }

    public KeyGenerator getKG () {
        return KG;
    }

    public void key(KeyGenerator key) {
        this.KG = key;
        this.SK = this.KG.generateKey();
    }

    public SecretKey getSK () {
        return SK;
    }
}
