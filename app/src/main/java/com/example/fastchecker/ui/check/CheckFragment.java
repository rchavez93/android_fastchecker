package com.example.fastchecker.ui.check;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fastchecker.R;
import com.example.fastchecker.databinding.FragmentCheckBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringJoiner;

public class CheckFragment extends Fragment {


    private FragmentCheckBinding binding;

    protected ChipGroup _ChangeChipButtons;

    public EditText _ChangeText;

    public TextView _CurrentWordText;

    protected ArrayList<String> cleanedWordsToCheck;
    protected ArrayList<String> incorrectWordList;
    //protected String[][][] combineWords;
    protected ArrayList<String> suggestions = new ArrayList();
    protected ArrayList<Boolean> areCorrect = new ArrayList();

    HashMap hashedFileFromSave = new HashMap<>();
    HashMap hashedWordsToCheck = new HashMap<>();

    protected boolean whiteSpace;

    protected String cleanedInput = "";
    protected String newWord = "";
    protected String currentWord = "";

    StringJoiner stringJoiner;

    // can combine interfaces to be 'getData' that is needed
    public interface hashInterface {

        HashMap getHash();  // change to if data.size == 0/null then do this
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Instant start = Instant.now();

        CheckViewModel dashboardViewModel =
                new ViewModelProvider(this).get(CheckViewModel.class);
        binding = FragmentCheckBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        cleanedWordsToCheck = new ArrayList<>();
        incorrectWordList = new ArrayList<>();

        _ChangeText = (EditText) root.findViewById(R.id.ChangeText);
        _ChangeChipButtons = (ChipGroup) root.findViewById(R.id.ChangeChipButtons);

        _CurrentWordText = (TextView) root.findViewById(R.id.CurrentWordText);

        readHomeEnteredText();  // cleans user words -> cleanedWordsToCheck
        loadHashWords();
        showSuggestionChips();

        Instant end = Instant.now();
        Log.d("DEBUG", "TOTAL>>>Time for CheckFragment class : " + Duration.between(start, end));
        return root;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        readHomeEnteredText();  // cleans user words -> cleanedWordsToCheck
        _ChangeText.setText(cleanedInput);  // move homefrag text to checkfrag
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected void readHomeEnteredText() { // gets null pointer at 2nd line if going from 3 to 2

        EditText homeWords = getActivity().findViewById(R.id.TypeTextField); // Words typed by user in Home Fragment
        String userInput = String.valueOf(homeWords.getText());  // raw data of words typed by user in home fragment to clean
        StringBuilder stringBuilder = new StringBuilder();  // to help clean the data instead of looping chars
        whiteSpace = false;
        cleanedWordsToCheck.clear();

        for (int x = 0; userInput.length() > x; x++) {
            try {

                if (userInput.charAt(x) == ' ' || userInput.charAt(x) == '\n') {  // if space or enter key, we will also check if we didn't double enter/space

                    if (!whiteSpace) {  // if its actually a word separated, add to our cleaned arraylist and reset builder

                        cleanedWordsToCheck.add(String.valueOf(stringBuilder));
                        stringBuilder.setLength(0);
                        whiteSpace = true;
                    }
                } else {

                    stringBuilder.append(userInput.charAt(x));
                    whiteSpace = false;
                }
            } catch (StringIndexOutOfBoundsException exception) {

                Log.d("WARNING", "StringIndexOutOfBoundsException of WordSet");
            }
        }

        if (stringBuilder.length() > 0) {   // Add last word to set if needed

            cleanedWordsToCheck.add(String.valueOf(stringBuilder));
            stringBuilder.setLength(0);
            whiteSpace = true;
        }

        for (int x = 0; x < cleanedWordsToCheck.size(); x++) {  // going through every cleaned word, putting them in a hash

            hashedWordsToCheck.put(x, cleanedWordsToCheck.get(x));
        }

        /* Lastly, we update a string to contain the cleaned info from home to manipulate */
        cleanedInput = cleanedWordsToCheck.toString()
                .replace(",", "")  //remove the commas
                .replace("[", "")  //remove the right bracket
                .replace("]", "")  //remove the left bracket
                .trim();

        _ChangeText.setText(cleanedInput);  // move homefrag text to checkfrag
        Log.d("DEBUG", String.valueOf(_ChangeText.getText()));
    }

    protected void loadHashWords() {

        /* Instead of reloading file to another hash, we will just use it directly from main frag */
        hashInterface hashDictionary = (hashInterface) getActivity();

        if (hashedFileFromSave.size() < 1) {
            hashedFileFromSave.putAll(hashDictionary.getHash());
        }

        for (int z = 0; z < hashedWordsToCheck.size(); z++) {

            String toCheck = (String) hashedWordsToCheck.get(z);

            if (!hashedFileFromSave.containsValue(toCheck)) {

                incorrectWordList.add(toCheck);
                areCorrect.add(false);
            } else {
                areCorrect.add(true);
            }
        }

        int i = incorrectWordList.size();
        //combineWords = new String[i][i][i];
        giveFirstSuggestion(incorrectWordList);
    }

    private void giveFirstSuggestion(ArrayList arrayList) {

        ArrayList wrongWordsList = arrayList;

        if (wrongWordsList.size() > 0) {

            currentWord = (String) wrongWordsList.get(0);
            //findSuggestions((String) wrongWordsList.get(0));
            findSuggestions(currentWord);
            _CurrentWordText.setText(currentWord);
            //Log.d("DEBUG", "currentwordtext should be -> "+_CurrentWordText.getText());
        } else {
            //_CurrentWordText.setText("");
            Log.d("DEBUG", "End of incorrect word list!");
        }
    }

    protected ArrayList findSuggestions(String badWord) {
        Instant start = Instant.now();

        ArrayList suggestions = new ArrayList();
        ArrayList word = new ArrayList();

        suggestAdd(badWord);
        suggestRemove(badWord);
        suggestSwap(badWord);
        //suggestCombine(combineWords);

        word.add(suggestions);

        Instant end = Instant.now();
        Log.d("INFO", "findSuggestions(" + badWord + ") time: " + Duration.between(start, end));

        return word;
    }

    private void suggestAdd(String word) {

        if (word.length() < 2) {    // Too small of a word, just return

            return;
        }

        for (int y = 97; y <= 122; y++) {   // For each char in alphabet

            String oldWord = word;
            char newWordCharArray[] = new char[word.length() + 1];    // because we are ADDING, length will definitely be +1

            for (int x = 0; x < newWordCharArray.length; x++) {

                newWord = customAddChar(oldWord, y, x);

                if (hashedFileFromSave.containsValue(newWord)) {

                    Log.d("DEBUG", "suggestAdd added : " + newWord);
                    suggestions.add(newWord);
                }
            }
        }
    }

    private void suggestRemove(String word) {

        if (word.length() < 2) {    // Too small of a word, just return

            return;
        }
        String oldWord = word;  // making a copy of imported word to make suggestions of
        int oldWordSize = word.length();

        for (int x = 0; x < oldWordSize; x++) {

            StringBuilder stringBuilder = new StringBuilder(oldWord); // try .clear() later for performance
            stringBuilder.deleteCharAt(x);
            newWord = stringBuilder.toString();

            if (hashedFileFromSave.containsValue(newWord)) {

                Log.d("DEBUG", "suggestRemove added : " + newWord);
                suggestions.add(newWord);
            }
        }
    }

    private void suggestSwap(String word) {

        if (word.length() < 2) {    // Too small of a word, just return

            return;
        }

        String oldWord = word;  // making a copy of imported word to make suggestions of
        int oldWordSize = word.length();

        for (int x = 0; x < oldWordSize; x++) {

            StringBuilder stringBuilder = new StringBuilder(oldWord);
            char firstChar;
            char secondChar;

            if (x == oldWordSize) {
                firstChar = stringBuilder.charAt(x);
                secondChar = stringBuilder.charAt(x + 1);
                stringBuilder.setCharAt(x, secondChar);
                stringBuilder.setCharAt(x + 1, firstChar);
            } else {
                firstChar = stringBuilder.charAt(x);
                secondChar = stringBuilder.charAt(0);
                stringBuilder.setCharAt(x, secondChar);
                stringBuilder.setCharAt(0, firstChar);
            }

            newWord = stringBuilder.toString();

            if (hashedFileFromSave.containsValue(newWord)) {

                Log.d("DEBUG", "suggestSwap added : " + newWord);
                suggestions.add(newWord);
            }
        }
    }

//    private void suggestCombine(String[][][] word) {
//
//    }

    private String customAddChar(String temp, int y, int x) {   // will remove after optimizing

        String string = temp;
        char letter = (char) y;
        Integer pos = x;

        return string.substring(0, pos) + letter + string.substring(pos);
    }

    protected void showSuggestionChips() {

        if (suggestions.size() > 0) {

            _ChangeChipButtons.removeAllViews();

            for (int x = 0; x < suggestions.size(); x++) {

                Chip _tempChip = new Chip(getContext());

                _tempChip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String clickedText = (String) _tempChip.getText();
                        updateFragText(clickedText);
                        nextSuggestion();
                        stringJoiner = new StringJoiner(" ");  // update text-box below

                        for (int z = 0; z < cleanedWordsToCheck.size(); z++) {

                            stringJoiner.add(cleanedWordsToCheck.get(z));
                        }

                        //EditText _ChangeText = getActivity().findViewById(R.id.ChangeText);
                        _ChangeText.setText(stringJoiner.toString());
                    }
                });

                _tempChip.setText(String.valueOf(suggestions.get(x)));
                _ChangeChipButtons.addView(_tempChip);
            }
        } else {

            _ChangeChipButtons.removeAllViews();
            Chip _tempChip = new Chip(getContext());

            if (incorrectWordList.size() > 0) {

                nextSuggestion();
            } else {

                _CurrentWordText.setText("");
                _tempChip.setText("Done!");
                _tempChip.setEnabled(false);
            }

            _ChangeChipButtons.addView(_tempChip);
        }
    }

    private void updateFragText(String clickedText) {

        for (int x = 0; cleanedWordsToCheck.size() > x; x++) {
            try {

                if (areCorrect.get(x) == false) {
                    cleanedWordsToCheck.set(x, clickedText);
                    areCorrect.set(x, true);
                    return; // temp
                }
            } catch (StringIndexOutOfBoundsException exception) {

                Log.d("WARNING", "StringIndexOutOfBoundsException of WordSet");
            }
        }
    }

    private void nextSuggestion() {

        if (incorrectWordList.size() > 0) {

            incorrectWordList.remove(0);
            suggestions.clear();
            giveFirstSuggestion(incorrectWordList);
            showSuggestionChips();
        } else {
            // do nothing, either change or delete if-else
        }
    }


}