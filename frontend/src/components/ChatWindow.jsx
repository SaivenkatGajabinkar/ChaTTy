import React, { useState, useEffect, useRef } from 'react';
import { messageService } from '../services/api';
import websocketService from '../services/websocket';
import './ChatWindow.css';

const ChatWindow = ({ conversation, currentUser, otherUserName }) => {
    const [messages, setMessages] = useState([]);
    const [inputText, setInputText] = useState('');
    const messagesEndRef = useRef(null);

    useEffect(() => {
        if (!conversation) return;

        // 1. Fetch history
        loadMessages();

        // 2. Set Message Handler
        websocketService.setMessageHandler((newMessage) => {
            // Only add if it belongs to current conversation
            if (newMessage.conversationId === conversation.id) {
                setMessages(prev => [...prev, newMessage]);
                
                // Play notification if the message is from someone else
                if (newMessage.senderId !== currentUser.userId) {
                    playNotification(newMessage);
                }
            }
        });

        // Request browser notification permission on load
        if (Notification.permission !== "denied") {
            Notification.requestPermission();
        }

        return () => {
            // Clear message handler when unmounting
            websocketService.setMessageHandler(null);
        };
    }, [conversation]);

    const playNotification = (newMessage) => {
        // Play WhatsApp-style ping sound
        const audio = new Audio('https://assets.mixkit.co/active_storage/sfx/2869/2869-preview.mp3');
        audio.play().catch(() => { /* Audio blocked */ });

        // Show browser notification
        if (Notification.permission === "granted") {
            new Notification("New message from " + (otherUserName || "Chat"), {
                body: newMessage.translatedText,
                icon: "https://cdn-icons-png.flaticon.com/512/124/124034.png"
            });
        }
    };

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    const loadMessages = async () => {
        try {
            const res = await messageService.getMessages(conversation.id);
            setMessages(res.data);
        } catch (e) { console.error("Failed to load messages", e); }
    };

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    const handleSend = (e) => {
        e.preventDefault();
        if (!inputText.trim()) return;

        const sent = websocketService.sendMessage(conversation.id, currentUser.userId, inputText);
        if (sent) {
            setInputText('');
        } else {
            console.warn("Message not sent yet, waiting for connection.");
        }
    };

    return (
        <div className="chat-window">
            <div className="chat-header">
                <div className="avatar small">{otherUserName?.substring(0,2) || '??'}</div>
                <h3>{otherUserName || 'Unknown'}</h3>
            </div>
            
            <div className="chat-messages">
                {messages.map((msg, index) => {
                    const isMine = msg.senderId === currentUser.userId;
                    const hasTranslation = msg.translatedText && msg.translatedText !== msg.originalText;
                    
                    return (
                        <div key={index} className={`message-wrapper ${isMine ? 'mine' : 'theirs'}`}>
                            <div className="message-bubble">
                                <div className="msg-content">
                                    {hasTranslation ? (
                                        <>
                                            <p className="msg-text original">{msg.originalText}</p>
                                            <p className="msg-text translated">{msg.translatedText}</p>
                                        </>
                                    ) : (
                                        <p className="msg-text translated">{msg.originalText}</p>
                                    )}
                                </div>
                                <span className="msg-time">
                                    {new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                </span>
                            </div>
                        </div>
                    );
                })}
                <div ref={messagesEndRef} />
            </div>

            <form className="chat-input-area" onSubmit={handleSend}>
                <input 
                    type="text" 
                    placeholder="Type a message..." 
                    value={inputText}
                    onChange={(e) => setInputText(e.target.value)}
                />
                <button type="submit">
                    Send
                </button>
            </form>
        </div>
    );
};

export default ChatWindow;
