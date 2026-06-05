package com.chaty.service;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class TranslationService {

    @Value("${google.cloud.translation.api-key}")
    private String apiKey;

    private Translate translate;

    @PostConstruct
    public void init() {
        try {
            if (apiKey != null && !apiKey.equals("YOUR_GOOGLE_CLOUD_API_KEY") && !apiKey.isEmpty()) {
                translate = TranslateOptions.newBuilder().setApiKey(apiKey).build().getService();
            }
        } catch (Exception e) {
            // Log initialization failure
        }
    }

    public String translateText(String text, String sourceLang, String targetLang) {
        if (translate != null) {
            try {
                Translation translation = translate.translate(
                        text,
                        Translate.TranslateOption.targetLanguage(targetLang)
                );
                return translation.getTranslatedText();
            } catch (Exception e) {
                System.err.println("Translation failed: " + e.getMessage());
                return "[Error] " + text;
            }
        } else {
            // Fallback to Google Translate Free API (gtx)
            try {
                org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
                String encodedText = java.net.URLEncoder.encode(text, "UTF-8");
                String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + sourceLang + "&tl=" + targetLang + "&dt=t&q=" + encodedText;
                
                java.util.List<Object> response = restTemplate.getForObject(url, java.util.List.class);
                if (response != null && !response.isEmpty()) {
                    java.util.List<Object> translations = (java.util.List<Object>) response.get(0);
                    if (translations != null && !translations.isEmpty()) {
                        java.util.List<Object> translationLine = (java.util.List<Object>) translations.get(0);
                        if (translationLine != null && !translationLine.isEmpty()) {
                            return translationLine.get(0).toString();
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Google Free Translation failed: " + e.getMessage());
            }
            return text; // Return original if translation fails
        }
    }
}
