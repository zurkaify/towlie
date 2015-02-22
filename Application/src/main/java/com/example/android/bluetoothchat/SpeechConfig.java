package com.example.android.bluetoothchat;

    /** Configuration parameters for this application's account on Speech API. **/
public class SpeechConfig {
    private SpeechConfig() {} // can't instantiate
    /** The URL of AT&T Speech to Text service. **/
    static String recognitionUrl() {
        return "https://api.att.com/speech/v3/speechToText";
    }
    /** The URL of AT&T Speech API OAuth service. **/
    static String oauthUrl() {
        return "https://api.att.com/oauth/v4/token";
    }
    /** The OAuth scope of AT&T Speech API. **/
    static String oauthScope() {
        return "SPEECH";
    }
    /** Unobfuscates the OAuth client_id credential for the application. **/
    static String oauthKey() {
        return "q2mlfn2e5cwu8zrv2v57oqvt4makleb8";
    }
    /** Unobfuscates the OAuth client_secret credential for the application. **/
    static String oauthSecret() {
        return "8m86rovudgxwnzklgfavc9ar41qxf71h";
    }
}
