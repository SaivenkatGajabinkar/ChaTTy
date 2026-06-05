package com.chaty.service;

import com.chaty.dto.MessageDto;
import com.chaty.model.Conversation;
import com.chaty.model.Message;
import com.chaty.model.User;
import com.chaty.repository.ConversationRepository;
import com.chaty.repository.MessageRepository;
import com.chaty.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TranslationService translationService;

    /**
     * Processes a new message: fetches conversation/sender/receiver,
     * performs translation if needed, and persists the message to MySQL.
     */
    public Message processAndSaveMessage(MessageDto messageDto) {
        // Fetch entities from database
        Conversation conversation = conversationRepository.findById(messageDto.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        User sender = userRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        // Determine the recipient (the other member of the chat)
        String receiverId = conversation.getMembers().stream()
                .filter(id -> !id.equals(messageDto.getSenderId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Receiver not found in conversation"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // Multilingual Translation Logic
        String sourceLang = sender.getPreferredLanguage() != null ? sender.getPreferredLanguage() : "en";
        String targetLang = receiver.getPreferredLanguage() != null ? receiver.getPreferredLanguage() : "en";

        String translatedText = messageDto.getText(); // default to original
        
        // Only translate if languages are different
        if (!sourceLang.equalsIgnoreCase(targetLang)) {
             translatedText = translationService.translateText(messageDto.getText(), sourceLang, targetLang);
        }

        // Prepare and save the message entity
        Message message = new Message();
        message.setConversationId(messageDto.getConversationId());
        message.setSenderId(messageDto.getSenderId());
        message.setOriginalText(messageDto.getText());
        message.setTranslatedText(translatedText);
        message.setSourceLanguage(sourceLang);
        message.setTargetLanguage(targetLang);
        message.setTimestamp(new Date());

        return messageRepository.save(message);
    }

    public List<Message> getConversationMessages(String conversationId) {
        return messageRepository.findByConversationIdOrderByTimestampAsc(conversationId);
    }
}
