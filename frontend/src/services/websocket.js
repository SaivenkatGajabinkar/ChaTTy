import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

class WebSocketService {
    constructor() {
        this.stompClient = null;
        this.onMessageReceived = null;
        this.onNotificationReceived = null;
        this.userId = null;
    }

    connect(userId, onMessageReceived, onNotificationReceived) {
        if (this.stompClient && this.stompClient.connected && this.userId === userId) {
            // Already connected, just update handlers
            if (onMessageReceived) this.onMessageReceived = onMessageReceived;
            if (onNotificationReceived) this.onNotificationReceived = onNotificationReceived;
            return;
        }

        this.userId = userId;
        this.onMessageReceived = onMessageReceived;
        this.onNotificationReceived = onNotificationReceived;

        const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';
        const socket = new SockJS(`${API_URL}/chat`);
        this.stompClient = Stomp.over(socket);
        this.stompClient.debug = () => {};

        this.stompClient.connect({}, () => {
            console.log("Connected to WebSocket");
            
            this.stompClient.subscribe(`/topic/messages/${userId}`, (message) => {
                if (message.body && this.onMessageReceived) {
                    this.onMessageReceived(JSON.parse(message.body));
                }
            });

            this.stompClient.subscribe(`/topic/notifications/${userId}`, (payload) => {
                if (payload.body && this.onNotificationReceived) {
                    this.onNotificationReceived(payload.body);
                }
            });
        }, (err) => {
            console.error("STOMP error:", err);
            setTimeout(() => this.connect(userId, this.onMessageReceived, this.onNotificationReceived), 5000);
        });
    }

    setMessageHandler(handler) {
        this.onMessageReceived = handler;
    }

    setNotificationHandler(handler) {
        this.onNotificationReceived = handler;
    }

    sendMessage(conversationId, senderId, text) {
        if (this.stompClient && this.stompClient.connected) {
            const message = {
                conversationId,
                senderId,
                text
            };
            this.stompClient.send("/app/send", {}, JSON.stringify(message));
            return true;
        } else {
            console.error("Cannot send message, STOMP client is not connected.");
            return false;
        }
    }

    disconnect() {
        if (this.stompClient !== null) {
            this.stompClient.disconnect();
            console.log("Disconnected from WebSocket");
        }
    }
}

export default new WebSocketService();
