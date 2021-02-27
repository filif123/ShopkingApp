package sk.shopking.shopkingapp.tools;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CacheFile {
    private static final String FILE_NAME = "data";

    private String ipAddress;

    private Context context;

    public CacheFile(Context context){
        this.context = context;
    }

    public String getIpAddress(){
        return this.ipAddress;
    }

    public void setIpAddress(String ip){
        this.ipAddress = ip;
    }

    public void readFile() throws FileNotFoundException {
        FileInputStream fis = context.openFileInput(FILE_NAME);
        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            String contents = stringBuilder.toString();
            String[] data = contents.split("\n");
            this.ipAddress = data[0];
        }
    }

    public void saveFile(){
        String fileContents = this.ipAddress;
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
