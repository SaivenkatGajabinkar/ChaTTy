import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const api = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Add a request interceptor to include JWT token
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export const authService = {
    login: (email, password) => api.post('/auth/login', { email, password }),
    register: (data) => api.post('/auth/register', data),
    googleLogin: (token) => api.post('/auth/google', { token }),
};

export const friendService = {
    getFriends: (userId) => api.get(`/friend/list/${userId}`),
    getPending: (userId) => api.get(`/friend/pending/${userId}`),
    sendRequest: (senderId, receiverId) => api.post(`/friend/request?senderId=${senderId}&receiverId=${receiverId}`),
    acceptRequest: (requestId) => api.put(`/friend/accept?requestId=${requestId}`)
};

export const conversationService = {
    getConversations: (userId) => api.get(`/conversation/${userId}`),
    createConversation: (user1Id, user2Id) => api.post(`/conversation/create?user1Id=${user1Id}&user2Id=${user2Id}`)
};

export const messageService = {
    getMessages: (conversationId) => api.get(`/message/${conversationId}`)
};

export const userService = {
    getUser: (userId) => api.get(`/user/${userId}`)
};

export default api;
