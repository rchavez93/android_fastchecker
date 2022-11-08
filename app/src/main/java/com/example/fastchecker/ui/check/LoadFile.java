package com.example.fastchecker.ui.check;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fastchecker.MainActivity;
import com.example.fastchecker.R;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

public class LoadFile extends CheckFragment {

    private HashMap hashFile = new HashMap();

    private void LoadFile() {

        //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        //File file = new File("C:\\Users\\rober\\Documents\\betterwords.txt");

        hashFile.clear();

        try {

            InputStream myInputStream = getContext().getResources().openRawResource(R.raw.betterwords);
            Scanner sc = new Scanner(myInputStream);

            int x = 0;

            while (sc.hasNextLine()) {

                hashFile.put(x, sc.nextLine());
                x++;
            }
            Log.d("DEBUG", "loadFile of words totaling "+x+"!");

        } catch (NullPointerException e) {
            Log.d("EXCEPTION", "NullPointer occurred! Index out of bounds!");
        }
    }


}
