package com.me4502.hardnestgui;

import com.me4502.hardnestgui.app.HardNestedApplication;

import java.io.IOException;

public class HardNestedGui {

    public static void main(String[] args) {
        try {
            HardNestedApplication.getInstance().load();
        } catch (IOException e) {
            // If an exception makes it here, runtime.
            throw new RuntimeException(e);
        }
    }
}
