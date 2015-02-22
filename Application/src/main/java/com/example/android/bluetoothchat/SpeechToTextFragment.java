package com.example.android.bluetoothchat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import com.att.android.speech.ATTSpeechError;
import com.att.android.speech.ATTSpeechError.ErrorType;
import com.att.android.speech.ATTSpeechErrorListener;
import com.att.android.speech.ATTSpeechResult;
import com.att.android.speech.ATTSpeechResultListener;
import com.att.android.speech.ATTSpeechService;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

/**
 * Created by Zurka on 2/22/2015.
 */
public class SpeechToTextFragment extends Fragment {

    private static final String TAG = "BluetoothChatFragment";

    // Layout Views
    private ListView mConversationView;
    private Button mListenButton;
    private Context context;
    private String oauthToken = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    public SpeechToTextFragment(Context context)
    {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        setupChat();
        validateOAuth();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.speech_to_text_screen, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mConversationView = (ListView) view.findViewById(R.id.in_speech);
        mListenButton = (Button) view.findViewById(R.id.button_listen);
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);

        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the send button with a listener that for click events
        mListenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startSpeechService();
                Toast.makeText(getActivity(), "this is my Toast message!!! =)",
                        Toast.LENGTH_LONG).show();
            }
        });

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        Log.d(TAG, "sendMessage()");
        // Check that there's actually something to send
        if (message.length() > 0) {
            //TODO send message here
            Intent clearIntent = new Intent(this.context, TowlieService.class);
            clearIntent.putExtra("action", "clear");
            this.context.startService(clearIntent);

            Intent pushIntent = new Intent(this.context, TowlieService.class);
            pushIntent.putExtra("action", "push");
            pushIntent.putExtra("message", message);
            this.context.startService(pushIntent);

            //Clear the current text and add the new text to the screen
            mConversationArrayAdapter.clear();
            mConversationArrayAdapter.add(message);
            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
        }
    }

    /**
     * Called by the Speak button in the sample activity.
     * Starts the SpeechKit service that listens to the microphone and returns
     * the recognized text.
     **/
    private void
    startSpeechService()
    {
        Log.d(TAG, "startSpeechService()");
// The ATTSpeechKit uses a singleton object to interface with the
// speech server.
        ATTSpeechService speechService = ATTSpeechService.getSpeechService(this.getActivity());
// Register for the success and error callbacks.
        speechService.setSpeechResultListener(new ResultListener());
        speechService.setSpeechErrorListener(new ErrorListener());
// Next, we'll put in some basic parameters.
// First is the Request URL. This is the URL of the speech recognition
// service that you were given during onboarding.
        try {
            speechService.setRecognitionURL(new URI(SpeechConfig.recognitionUrl()));
        }
        catch (URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        }
// Specify the speech context for this app.
        speechService.setSpeechContext("QuestionAndAnswer");
// Set the OAuth token that was fetched in the background.
        speechService.setBearerAuthToken(oauthToken);
// Add extra arguments for speech recognition.
// The parameter is the name of the current screen within this app.
        speechService.setXArgs(
                Collections.singletonMap("ClientScreen", "main"));
// Finally we have all the information needed to start the speech service.
        speechService.startListening();
        Log.v("SimpleTTS", "Starting speech interaction");
    }
    /**
     * This callback object will get all the speech success notifications.
     **/
    private class ResultListener implements ATTSpeechResultListener
    {
        public void
        onResult(ATTSpeechResult result)
        {
            Log.d(TAG, "resultListenerOnResult()");
// The hypothetical recognition matches are returned as a list of strings.
            List<String> textList = result.getTextStrings();
            String resultText = null;
            if (textList != null && textList.size() > 0) {
// There may be multiple results, but this example will only use
// the first one, which is the most likely.
                resultText = textList.get(0);
            }
            if (resultText != null && resultText.length() > 0) {
// This is where your app will process the recognized text.
                Log.v("SimpleTTS", "Recognized "+textList.size()+" hypotheses.");
                handleRecognition(resultText);
            }
            else {
// The speech service did not recognize what was spoken.
                Log.v("SimpleTTS", "Recognized no hypotheses.");
                alert("Didn't recognize speech", "Please try again.");
            }
        }
    }
    /** Make use of the recognition text in this app. **/
    private void handleRecognition(String resultText) {
        Log.d(TAG, "handleRecognition()");
        mConversationArrayAdapter.clear();
        mConversationArrayAdapter.add(resultText);
        // Reset out string buffer to zero and clear the edit text field
        mOutStringBuffer.setLength(0);
    }
    /**
     * This callback object will get all the speech error notifications.
     **/
    private class ErrorListener implements ATTSpeechErrorListener
    {
        public void onError(ATTSpeechError error) {
            Log.d(TAG, "errorListenerOnError()");
            ErrorType resultCode = error.getType();
            if (resultCode == ErrorType.USER_CANCELED) {
// The user canceled the speech interaction.
// This can happen through several mechanisms:
// pressing a cancel button in the speech UI;
// pressing the back button; starting another activity;
// or locking the screen.
// In all these situations, the user was instrumental
// in canceling, so there is no need to put up a UI alerting
// the user to the fact.
                Log.v("SimpleTTS", "User canceled.");
            }
            else {
// Any other value for the result code means an error has occurred.
// The argument includes a message to help the programmer
// diagnose the issue.
                String errorMessage = error.getMessage();
                Log.v("SimpleTTS", "Recognition error #"+resultCode+": "+errorMessage,
                        error.getException());
                alert("Speech Error", "Please try again later.");
            }
        }
    }
    /**
     * Start an asynchronous OAuth credential check.
     * Disables the Speak button until the check is complete.
     **/
    private void
    validateOAuth()
    {
        Log.d(TAG, "validateOAuth()");
        SpeechAuth auth =
                SpeechAuth.forService(SpeechConfig.oauthUrl(), SpeechConfig.oauthScope(),
                        SpeechConfig.oauthKey(), SpeechConfig.oauthSecret());
        auth.fetchTo(new OAuthResponseListener());
        //mListenButton.setText(R.string.listen);
        //mListenButton.setEnabled(false);
    }
    /**
     * Handle the result of an asynchronous OAuth check.
     **/
    private class OAuthResponseListener implements SpeechAuth.Client
    {
        public void
        handleResponse(String token, Exception error)
        {
            Log.d(TAG, "OAuthResponce handleResponce()");
            if (token != null) {
                Log.d(TAG, "tokenSuccess()");
                oauthToken = token;
            }
            else {
                Log.v("SimpleTTS", "OAuth error: "+error);
// There was either a network error or authentication error.
// Show alert for the latter.
                alert("Speech Unavailable",
                        "This app was rejected by the speech service. Contact the developer for an update.");
            }
        }
    }
    private void
    alert(String header, String message)
    {
        Log.d(TAG, "alert()");
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setMessage(message)
                .setTitle(header)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
