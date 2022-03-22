package com.example.qrhunt;

import java.util.Map;

/**
 * This interface uses Map to control the data from FireDatabase.
 * The data is calling back as response.
 */
public interface PlayerCallback {
    void callBack(Player player);

}
