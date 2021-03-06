package com.example.android.bluetoothchat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.content.Context;

public class StartScreenFragment extends Fragment {

    private Button textButton;
    private Button speechButton;
    private Context context;

    public StartScreenFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        setupStart();
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
        return inflater.inflate(R.layout.start_screen, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        speechButton = (Button) view.findViewById(R.id.button_speech);
        textButton = (Button) view.findViewById(R.id.button_text);
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupStart() {

        // Initialize the send button with a listener that for click events
        speechButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SpeechToTextFragment fragment = new SpeechToTextFragment(context);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.sample_content_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        textButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TypeToTextFragment fragment = new TypeToTextFragment(context);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.sample_content_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }
}