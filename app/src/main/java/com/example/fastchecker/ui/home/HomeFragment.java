package com.example.fastchecker.ui.home;

import static android.content.Context.MODE_PRIVATE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fastchecker.R;
import com.example.fastchecker.databinding.FragmentHomeBinding;

import java.time.Duration;
import java.time.Instant;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    protected String currentTextSave = "";
    protected String pastTextSave = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Instant start = Instant.now();

        //HomeViewModel homeViewModel =
        //        new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button _ClearButton = (Button) root.findViewById(R.id.ClearButton);
        Button _CopyButton = (Button) root.findViewById(R.id.CopyButton);
        Button _UndoRedoButton = (Button) root.findViewById(R.id.UndoRedoButton);
        EditText _TypeTextEdit = (EditText) root.findViewById(R.id.TypeTextField);

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
        Log.d("DEBUG", "TOTAL>>>Time for HomeFragment class : "+ Duration.between(start, end));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}