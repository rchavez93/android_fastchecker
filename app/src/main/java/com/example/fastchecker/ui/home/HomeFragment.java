package com.example.fastchecker.ui.home;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.fastchecker.R;
import com.example.fastchecker.databinding.FragmentHomeBinding;
import com.example.fastchecker.ui.check.CheckFragment;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    protected String currentTextSave = "";
    protected String pastTextSave = "";

    protected String wordsToHome = "";
    protected String toChange = "";

    public EditText _TypeTextEdit;
/*

    public interface textCheckedInterface {

            String passCurrentText();
        }*/

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Instant start = Instant.now();

        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        Button _ClearButton = (Button) root.findViewById(R.id.ClearButton);
        Button _CopyButton = (Button) root.findViewById(R.id.CopyButton);
        Button _UndoRedoButton = (Button) root.findViewById(R.id.UndoRedoButton);

        _TypeTextEdit = (EditText) root.findViewById(R.id.TypeTextField);

        readCheckedEnteredText();

        Log.d("DEBUG", "toChange after method is currently -> "+toChange);

        _TypeTextEdit.setText(String.valueOf(toChange));  // move homefrag text to checkfrag
        Log.d("DEBUG", "_typetextedit at reset is currently -> "+String.valueOf(_TypeTextEdit.getText()));


        _TypeTextEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus) {

                    //Log.d("DEBUG","1>>>"+currentTextSave);
                }
            }
        });
        _TypeTextEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentTextSave = String.valueOf(_TypeTextEdit.getText());
            }
        });

        _ClearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentTextSave = String.valueOf(_TypeTextEdit.getText());
                pastTextSave = currentTextSave;
                _TypeTextEdit.setText("");
            }
        });

        _CopyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentTextSave = String.valueOf(_TypeTextEdit.getText());
                ClipboardManager clipboard = getSystemService(getContext(), ClipboardManager.class);
                ClipData clip = ClipData.newPlainText("copy", currentTextSave);
                clipboard.setPrimaryClip(clip);
            }
        });

        _UndoRedoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String temp = currentTextSave;

                currentTextSave = String.valueOf(_TypeTextEdit.getText());
                _TypeTextEdit.setText(pastTextSave);
                pastTextSave = temp;
            }
        });

        Instant end = Instant.now();
        //Log.d("DEBUG", "TOTAL>>>Time for HomeFragment class : "+ Duration.between(start, end));
        Log.d("DEBUG", "_typetextedit at root is currently -> "+String.valueOf(_TypeTextEdit.getText()));
        return root;
    }


//    private String readCheckEnteredText() {
//
//            EditText CheckedWords = getActivity().findViewById(R.id.ChangeText); // Words typed by user in Check Fragment
//            String str = String.valueOf(CheckedWords.getText());  // raw data of words
//
//            return str;
//    }


        protected void readCheckedEnteredText() { // can be optimize better for other data being passed

        try {

            EditText checkWords = getActivity().findViewById(R.id.ChangeText); // Words typed by user in Home Fragment
            toChange = String.valueOf(checkWords.getText());  // raw data of words typed by user in home fragment to clean
            Log.d("WARNING", "toChange should be -> "+toChange);
            //checkWords.setText(toChange, TextView.BufferType.EDITABLE);

        } catch (NullPointerException e) {
                Log.d("WARNING", "Text has nothing, ignoring for now...");
            }

//
//            EditText ChangedWords = getActivity().findViewById(R.id.ChangeText); // Words typed by user in Home Fragment
//
//            String string = String.valueOf(ChangedWords.getText());  // raw data of words typed by user in home fragment to clean
//            toCheck.add(string);  // raw data of words typed by user in home fragment to clean

        }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        readCheckedEnteredText();
        _TypeTextEdit.setText(String.valueOf(toChange));  // move homefrag text to checkfrag
        Log.d("DEBUG", "_typetextedit at restore is currently -> "+String.valueOf(_TypeTextEdit.getText()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}