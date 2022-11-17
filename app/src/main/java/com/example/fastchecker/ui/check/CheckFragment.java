package com.example.fastchecker.ui.check;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

    protected ArrayList<String> WordSetFile;
    protected ArrayList<String> cleanedWordsToCheck;
    protected ArrayList<String> incorrectWordList;
    protected ArrayList<String> suggestions = new ArrayList();
    protected ArrayList<Boolean> areCorrect = new ArrayList();

    HashMap hashedFileFromSave = new HashMap<>();
    HashMap hashedWordsToCheck = new HashMap<>();

    protected boolean whiteSpace;

    protected String cleanedInput = "";
    protected String newWord = "";
    protected String testWord = "";
    StringJoiner stringJoiner = new StringJoiner(" ");
    //protected String wordsToCheck = "";


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
        WordSetFile = new ArrayList<>();
        incorrectWordList = new ArrayList<>();
        //StringJoiner stringJoiner = new StringJoiner(" ");
        _ChangeText = (EditText) root.findViewById(R.id.ChangeText);;
        _ChangeChipButtons = (ChipGroup) root.findViewById(R.id.ChangeChipButtons);

        readHomeEnteredText();  // cleans user words -> cleanedWordsToCheck

//        for (int k = 0; k < cleanedWordsToCheck.size(); k++) {
//
//            stringJoiner.add(cleanedWordsToCheck.get(k));
//        }

        //stringJoiner.add(cleanedWordsToCheck.toString());
        //_ChangeText.setText(stringJoiner.toString());  // move homefrag text to checkfrag
        _ChangeText.setText(cleanedInput);  // move homefrag text to checkfrag

        //Log.d("DEBUG", "_ChangeText should change to : "+stringJoiner.toString());
        loadHashWords();
        showSuggestionChips();

        Instant end = Instant.now();
        Log.d("DEBUG", "TOTAL>>>Time for CheckFragment class : "+Duration.between(start, end));
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
//
//    protected void sendHomeEnteredText(String str) {
//
//        EditText EnteredWords = getActivity().findViewById(R.id.TypeTextField); // Words typed by user in Home Fragment
//        String string = str;
//        Log.d("WARNING", "Sending text to be -> "+string);
//        EnteredWords.setText(string);
//    }

    protected void readHomeEnteredText() { // gets nullpointer at 2nd line if going from 3 to 2

        EditText homeWords = getActivity().findViewById(R.id.TypeTextField); // Words typed by user in Home Fragment
        //wordsToCheck = String.valueOf(homeWords.getText());  // raw data of words typed by user in home fragment to clean
        String userInput = String.valueOf(homeWords.getText());  // raw data of words typed by user in home fragment to clean
        StringBuilder stringBuilder = new StringBuilder();  // to help clean the data instead of looping chars
        whiteSpace = false;

        for (int x = 0; userInput.length() > x; x++) {
            try {

                if (userInput.charAt(x) == ' ' || userInput.charAt(x) == '\n') {  // if space or enter key, we will also check if we didnt double enter/space

                    if (!whiteSpace) {  // if its actually a word seperated, add to our cleaned arraylist and reset builder

                        cleanedWordsToCheck.add(String.valueOf(stringBuilder));
                        stringBuilder.setLength(0);
                        whiteSpace = true;

                    } else {

                        // Do nothing "More whitespace"

                    }

                } else {

                    stringBuilder.append(userInput.charAt(x));
                    whiteSpace = false;

                }
            } catch (StringIndexOutOfBoundsException exception) {

                Log.d("WARNING", "StringIndexOutOfBoundsException of WordSet");
            }
        }

        //Log.d("DEBUG", "End of WordSet");

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

    }

    protected void loadHashWords() {

        /* Instead of reloading file to another hash, we will just use it directly from main frag
        for (int h = 0; h < myActivity.getHash().size(); h++) {

            hashedFileFromSave.put(h, myActivity.getHash().get(h));
        }
        */
        hashInterface myActivity = (hashInterface) getActivity();

        if (hashedFileFromSave.size() < 1) {
            hashedFileFromSave.putAll(myActivity.getHash());
            //Log.d("DEBUG", "hashedFileFromSave UPDATE is " + hashedFileFromSave.size());
        }
        else
        {

            //Log.d("DEBUG", "hashedFileFromSave REUSE is " + hashedFileFromSave.size());
        }
        //isCorrect = new boolean[hashedWordsToCheck.size()];

        for (int z = 0; z < hashedWordsToCheck.size(); z++) {
            //Log.d("DEBUG", "Z is now at " + z + ", testing word " + hashEdit.get(z).toString());
            String toCheck = (String) hashedWordsToCheck.get(z);
            //Log.d("DEBUG", "toCheck is " + toCheck);

            if (!hashedFileFromSave.containsValue(toCheck)) {

                incorrectWordList.add(toCheck);
                //Log.d("DEBUG", "toCheck: " +toCheck+" is NOT correct...");
                areCorrect.add(false);
            }
            else
            {
                areCorrect.add(true);
                //Log.d("DEBUG", "toCheck: " +toCheck+" is correct...");
            }

            /*Trying faster method instead of all this crap
            for (int i = 0; i < hashedFileFromSave.size(); i++) {

                if (hashedWordsToCheck.get(z).equals(hashedFileFromSave.get(i))) {

                    //Log.d("DEBUG", "The word " + hashEdit.get(z) + " is correct");
                    WordSetSuggest.add((String) hashedWordsToCheck.get(z));
                    isCorrect[z] = true;
                    break;
                }

            }*/
            //Log.d("DEBUG", "FINISHED loadHashWords CYCLE, STARTING testHash!");
        }

        giveFirstSuggestion(incorrectWordList);
    }

    private void giveFirstSuggestion(ArrayList arrayList) {

        ArrayList wrongWordsList = arrayList;

        if (wrongWordsList.size() > 0) {

            findSuggestions((String) wrongWordsList.get(0));
        }
        //for (int i = 0; isCorrect[i] == true; i++) {  // not using because of even a better way
        //
        //    temp = (String) hashedWordsToCheck.get(i);
        //    Log.d("DEBUG", "isCorrect is false at ["+i+"], giving first suggestion for -> "+ hashedWordsToCheck.get(i)+"...");
        //
        //}
        /*Using faster method...
        for (int i = 0; i < isCorrect.length; i++) {    // cycling through boolean array

            if (isCorrect[i] == false) {

                temp = (String) hashedWordsToCheck.get(i);
                Log.d("DEBUG", "badWord set to '" + temp + "', with the hashEdit word : "+ hashedWordsToCheck.get(i)+", finding suggestions...");
                findSuggestions(temp);
                return;
            }
            else
            {

                Log.d("DEBUG", "Skipping since correct -> "+ hashedWordsToCheck.get(i)+".");
            }
            i++;
        }*/
    }

    protected ArrayList findSuggestions(String badWord) {
        Instant start = Instant.now();

        ArrayList suggestions = new ArrayList();
        ArrayList word = new ArrayList(); // will contain suggested words for first badWord

        suggestAdd(badWord);
        suggestRemove(badWord);
        suggestSwap(badWord);
        //suggestSplit(badWord);

        word.add(suggestions);

        Instant end = Instant.now();
        Log.d("INFO", "findSuggestions("+badWord+") time: "+Duration.between(start, end));

        return word;
    }

    private void suggestAdd(String word) {

        //ArrayList list = new ArrayList();

        if (word.length() < 2) {
            //Log.d("DEBUG","Word too small, ignoring "+word);
            return;
        }
        for (int y = 97; y <= 122; y++) {       // For each char in alphabet

            //WordSetSuggest.add((String) hashEdit.get(z));
            //hashEdit.get(z).equals(hashFile.get(i));
            //char letter = (char) y;
            String oldWord = word;
            char newWordCharArray[] = new char [word.length()+1];    // because we are ADDING, length will definitely be +1
            //Log.d("DEBUG","suggestAdd hashFile is "+ hashedFileFromSave.size());

            for (int x = 0; x < newWordCharArray.length; x++) {

                newWord = customAddChar(oldWord, y, x);

                //for (int h = 0; h < hashFile.size(); h++) {   // removed right now because performance boost

                    //if (hashFile.get(h).toString().equals(tryWord)) {
                    if (hashedFileFromSave.containsValue(newWord)) {

                        Log.d("DEBUG", "suggestAdd added : "+newWord);
                        suggestions.add(newWord);
                    }
                //}

            }
        }
        //Log.d("DEBUG", "suggestAdd is at " + suggestions.size());
    }

    private void suggestRemove(String word) {

        if (word.length() < 2) {
            //Log.d("DEBUG","Word too small, ignoring "+word);
            return;
        }
            String oldWord = word;  // making a copy of imported word to make suggestions of
            int oldWordSize = word.length();

            for (int x = 0; x < oldWordSize; x++) {

                StringBuilder stringBuilder = new StringBuilder(oldWord); // try .clear() later for performance
                stringBuilder.deleteCharAt(x);
                newWord = stringBuilder.toString();

                if (hashedFileFromSave.containsValue(newWord)) {

                    Log.d("DEBUG", "suggestRemove added : "+newWord);
                    suggestions.add(newWord);
                }
        }
        //Log.d("DEBUG", "suggestRemove is at " + suggestions.size());
    }

    private String customAddChar(String temp, int y, int x) {   // will remove after optimizing

        String string = temp;
        char letter = (char) y;
        Integer pos = x;

        return string.substring(0, pos) + letter + string.substring(pos);
    }

    private void suggestSwap(String word) {

        String oldWord = word;  // making a copy of imported word to make suggestions of
        int oldWordSize = word.length();

        for (int x = 0; x < oldWordSize; x++) {

            StringBuilder stringBuilder = new StringBuilder(oldWord);
            char firstChar;
            char secondChar;

            if (x == oldWordSize) {
                firstChar = stringBuilder.charAt(x);
                secondChar = stringBuilder.charAt(x+1);
                stringBuilder.setCharAt(x, secondChar);
                stringBuilder.setCharAt(x+1, firstChar);
            }
            else
            {
                firstChar = stringBuilder.charAt(x);
                secondChar = stringBuilder.charAt(0);
                stringBuilder.setCharAt(x, secondChar);
                stringBuilder.setCharAt(0, firstChar);
            }

            newWord = stringBuilder.toString();

            if (hashedFileFromSave.containsValue(newWord)) {

                Log.d("DEBUG", "suggestSwap added : "+newWord);
                suggestions.add(newWord);
            }
        }
        //Log.d("DEBUG", "suggestSwap is at " + suggestions.size());
    }

    private void suggestSplit(String word) {
    }

    protected void showSuggestionChips() {

        if (suggestions.size() > 0) {

            _ChangeChipButtons.removeAllViews();

            for (int x = 0; x < suggestions.size(); x++) {

                Chip _tempChip = new Chip(getContext());

                _tempChip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //isCorrect[0] = true;
                        String clickedText = (String) _tempChip.getText();
                        //Log.d("DEBUG", "onCLick for chip '"+clickedText+"' was recognized...");
                        updateFragText(clickedText);
                        nextSuggestion();

                        StringJoiner stringJoiner = new StringJoiner(" ");  // update textbox below
                        for (int z = 0; z < cleanedWordsToCheck.size(); z++) {

                            stringJoiner.add(cleanedWordsToCheck.get(z));
                        }

                        EditText _ChangeText = getActivity().findViewById(R.id.ChangeText);
                        _ChangeText.setText(stringJoiner.toString());
                        //Log.d("SYSTEM", "_changetext is suppose to be "+_ChangeText.getText());

                        //sendHomeEnteredText();   // gives cleaned version back to home for later
                    }
                });

                _tempChip.setText(String.valueOf(suggestions.get(x)));
                _ChangeChipButtons.addView(_tempChip);
                //Log.d("DEBUG", "...");
            }
        }
        else
        {

            _ChangeChipButtons.removeAllViews();
            Chip _tempChip = new Chip(getContext());

            if (incorrectWordList.size() > 0) {

                nextSuggestion();
                //_tempChip.setText("No suggestions [PRESS TO CONTINUE]");
                //_tempChip.setEnabled(true);
            }
            else
            {

                _tempChip.setText("Done!");
                _tempChip.setEnabled(false);
            }
            _ChangeChipButtons.addView(_tempChip);
        }
    }

    private void updateFragText(String clickedText) {

        //EditText EnteredWords = getActivity().findViewById(R.id.TypeTextField);
        //String wordsToUpdate = String.valueOf(EnteredWords.getText());
        //StringBuilder stringBuilder = new StringBuilder();
        //whiteSpace = false;
        //int o = 0;

        //Log.d("DEBUG", "updateFragText at size "+cleanedWordsToCheck.size()+" now...");
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
/*
    public String passCurrentText(String ct) {

        String str = "";

        try {
            //String temp = String.valueOf(_ChangeText.getText());
            String temp = ct);
            str = temp;

        } catch (Exception e) {
            Log.d("ERROR", String.valueOf(e));

        }
        return str;
    }*/

    private void nextSuggestion() {

        if (incorrectWordList.size() > 0) {

            incorrectWordList.remove(0);
            suggestions.clear();
            giveFirstSuggestion(incorrectWordList);
            showSuggestionChips();
        }
        else
        {
            //Log.d("DEBUG", "END OF SUGGESTIONS");
        }
    }


}