package com.example.lycia.test.persistance;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by nissrine on 19/12/16.
 */

public class ReadWriteData {

    private static BufferedReader bufferedReader;
    private static InputStream inputStream;
    private static OutputStreamWriter outputStreamWriter;

    public static void writeToFile(String data) {
        try {
            outputStreamWriter.write(data);
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static void addToFile(String data, Context context, String fileName) {
        try {
            outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_APPEND));
            outputStreamWriter.append(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static void openInputStream(String fileName, Context context) {
        try {
            inputStream = context.openFileInput(fileName);
        } catch (FileNotFoundException e) {
            if(fileName == "level") {
                openOutputStream(fileName, context);
                writeToFile("1");
                closeOutputStream();
            }
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }

    public static boolean openOutputStream(String fileName, Context context) {
        try {
            outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return true;
    }

    public static void readFromFile() {
        if ( inputStream != null ) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
        }
    }

    public static void closeinputStream() {
        try {
            inputStream.close();
        } catch (IOException e) {
        }
    }

    public static void closeOutputStream() {
        try {
            outputStreamWriter.close();
        } catch (IOException e) {
        }
    }

    public static BufferedReader getBufferedReader() {
        return bufferedReader;
    }
}
