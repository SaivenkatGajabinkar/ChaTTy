import React, { useState, useEffect } from 'react';
import { friendService, conversationService, userService } from '../services/api';
import ChatWindow from './ChatWindow';
import websocketService from '../services/websocket';
import './Dashboard.css';

const Dashboard = ({ user, onLogout }) => {
    const [friends, setFriends] = useState([]);
    const [conversations, setConversations] = useState([]);
    const [activeConversation, setActiveConversation] = useState(null);
    const [friendInput, setFriendInput] = useState('');
    const [activeTab, setActiveTab] = useState('chats'); // 'chats' or 'friends'
    const [showProfile, setShowProfile] = useState(false);
    const [showInbox, setShowInbox] = useState(false);
    const [pendingRequests, setPendingRequests] = useState([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [userNames, setUserNames] = useState({});

    const resolveUserNames = async (ids) => {
        const missingIds = ids.filter(id => id && !userNames[id] && id !== user.userId);
        if (missingIds.length === 0) return;

        const newNames = { ...userNames };
        for (const id of missingIds) {
            try {
                const res = await userService.getUser(id);
                newNames[id] = res.data.name;
            } catch (e) {
                console.error("Could not fetch user", id);
                newNames[id] = "Unknown User";
            }
        }
        setUserNames(newNames);
    };

    useEffect(() => {
        loadFriends();
        loadConversations();

        // Connect WebSocket for friend notifications
        websocketService.connect(user.userId, (msg) => {
            // This is for real-time messages, handled in ChatWindow
            // But if we wanted to show a global notification badge, we could here
        }, (notif) => {

            if (notif === "FRIEND_REQUEST_RECEIVED" || notif === "FRIEND_REQUEST_ACCEPTED") {
                loadFriends();
                loadPendingRequests();
            }
        });

        return () => websocketService.disconnect();
    }, []);

    const loadFriends = async () => {
        try {
            const res = await friendService.getFriends(user.userId);
            setFriends(res.data);
            const friendIds = res.data.map(f => f.senderId === user.userId ? f.receiverId : f.senderId);
            resolveUserNames(friendIds);
        } catch (e) { console.error(e); }
    };

    const loadConversations = async () => {
        try {
            const res = await conversationService.getConversations(user.userId);
            setConversations(res.data);
            const otherIds = res.data.map(c => c.members.find(m => m !== user.userId));
            resolveUserNames(otherIds);
        } catch (e) { console.error(e); }
    };

    const loadPendingRequests = async () => {
        try {
            const res = await friendService.getPending(user.userId);
            setPendingRequests(res.data);
            const senderIds = res.data.map(req => req.senderId);
            resolveUserNames(senderIds);
        } catch (e) { console.error(e); }
    };

    const handleOpenInbox = () => {
        loadPendingRequests();
        setShowInbox(true);
    };

    const handleAcceptRequest = async (requestId) => {
        try {
            await friendService.acceptRequest(requestId);
            alert("Friend request accepted!");
            loadPendingRequests();
            loadFriends();
        } catch (e) { console.error(e); }
    };

    const handleAddFriend = async (e) => {
        e.preventDefault();
        try {
            await friendService.sendRequest(user.userId, friendInput);
            alert("Friend request sent!");
            setFriendInput('');
        } catch (e) {
            alert("Could not send friend request: " + e.response?.data);
        }
    };

    const startChat = async (friendId) => {
        try {
            const res = await conversationService.createConversation(user.userId, friendId);
            setActiveConversation(res.data);
            if (!conversations.find(c => c.id === res.data.id)) {
                setConversations([...conversations, res.data]);
                resolveUserNames([friendId]);
            }
        } catch (e) { console.error(e); }
    };

    const getOtherMemberId = (members) => members.find(m => m !== user.userId);
    const getDisplayName = (id) => userNames[id] || id;

    const filteredChats = conversations.filter(c => getDisplayName(getOtherMemberId(c.members)).toLowerCase().includes(searchQuery.toLowerCase()));
    const filteredFriends = friends.filter(f => {
        const friendId = f.senderId === user.userId ? f.receiverId : f.senderId;
        return getDisplayName(friendId).toLowerCase().includes(searchQuery.toLowerCase());
    });

    return (
        <div className="dashboard-container">
            {showProfile && (
                <div className="modal-overlay" onClick={() => setShowProfile(false)}>
                    <div className="glass-panel modal-content" onClick={e => e.stopPropagation()}>
                        <div className="modal-header">
                            <h2>My Profile</h2>
                            <button className="close-btn" onClick={() => setShowProfile(false)}>×</button>
                        </div>
                        <div className="modal-body">
                            <div className="avatar large" style={{margin: '0 auto 20px auto'}}>{user.name.charAt(0)}</div>
                            
                            <div className="profile-detail">
                                <label>User ID (Share with friends to chat!)</label>
                                <div className="copy-box" onClick={() => {navigator.clipboard.writeText(user.userId); alert("User ID Copied!");}}>
                                    <p>{user.userId}</p>
                                    <span>📋</span>
                                </div>
                            </div>

                            <div className="profile-detail">
                                <label>Display Name</label>
                                <p className="detail-text">{user.name}</p>
                            </div>
                            
                            <div className="profile-detail">
                                <label>Preferred Translation Language</label>
                                <p className="detail-text">{user.preferredLanguage.toUpperCase()}</p>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {showInbox && (
                <div className="modal-overlay" onClick={() => setShowInbox(false)}>
                    <div className="glass-panel modal-content" onClick={e => e.stopPropagation()}>
                        <div className="modal-header">
                            <h2>Inbox</h2>
                            <button className="close-btn" onClick={() => setShowInbox(false)}>×</button>
                        </div>
                        <div className="modal-body">
                            <h3>Pending Friend Requests</h3>
                            {pendingRequests.length === 0 ? (
                                <p className="empty-state" style={{marginTop: '20px'}}>No pending requests.</p>
                            ) : (
                                <div className="list-group" style={{marginTop: '15px'}}>
                                {pendingRequests.map(req => (
                                    <div key={req.id} className="list-item">
                                        <div className="avatar small">{getDisplayName(req.senderId).substring(0,2)}</div>
                                        <div className="item-details">
                                            <h4>{getDisplayName(req.senderId)}</h4>
                                            <p>wants to be friends</p>
                                        </div>
                                        <button 
                                            className="action-btn" 
                                            style={{background: '#198754'}}
                                            onClick={() => handleAcceptRequest(req.id)}
                                        >
                                            Accept
                                        </button>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>
            </div>
        )}

        <aside className="glass-panel sidebar">
            <div className="sidebar-header">
                <div className="user-profile" onClick={() => setShowProfile(true)} style={{cursor: 'pointer'}} title="Click to view profile & User ID">
                    <div className="avatar">{user.name.charAt(0)}</div>
                    <div>
                        <h3>{user.name}</h3>
                        <span className="lang-badge">{user.preferredLanguage.toUpperCase()}</span>
                    </div>
                </div>
                <div style={{display: 'flex', gap: '5px'}}>
                    <button className="logout-btn" onClick={handleOpenInbox} title="Inbox" style={{fontSize: '1.2rem', padding: '4px 8px'}}>📥</button>
                    <button className="logout-btn" onClick={onLogout}>🚪 Logout</button>
                </div>
            </div>

            <div className="sidebar-tabs">
                <button className={`tab ${activeTab === 'chats' ? 'active-tab' : ''}`} onClick={() => setActiveTab('chats')}>Chats</button>
                <button className={`tab ${activeTab === 'friends' ? 'active-tab' : ''}`} onClick={() => setActiveTab('friends')}>Friends</button>
                <button className="new-chat-btn" title="New Chat" onClick={() => setActiveTab('friends')}>+</button>
            </div>

            <div className="search-container">
                <input 
                    type="text" 
                    placeholder="Search..." 
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                />
            </div>

            <div className="sidebar-content">
                {activeTab === 'chats' ? (
                    <div className="list-group">
                        {filteredChats.length === 0 ? <p className="empty-state">No chats found.</p> : null}
                        {filteredChats.map(conv => (
                                <div 
                                    key={conv.id} 
                                    className={`list-item ${activeConversation?.id === conv.id ? 'active' : ''}`}
                                    onClick={() => setActiveConversation(conv)}
                                >
                                    <div className="avatar small">{getDisplayName(getOtherMemberId(conv.members)).substring(0,2)}</div>
                                    <div className="item-details">
                                        <h4>{getDisplayName(getOtherMemberId(conv.members))}</h4>
                                        <p>Chat</p>
                                    </div>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="list-group">
                            <form onSubmit={handleAddFriend} className="add-friend-form">
                                <input 
                                    type="text" 
                                    placeholder="Friend User ID..." 
                                    value={friendInput}
                                    onChange={(e) => setFriendInput(e.target.value)}
                                    required
                                />
                                <button type="submit">+</button>
                            </form>
                            
                            {filteredFriends.length === 0 ? <p className="empty-state">No friends found.</p> : null}
                            {filteredFriends.map(f => {
                                const friendId = f.senderId === user.userId ? f.receiverId : f.senderId;
                                return (
                                    <div key={f.id} className="list-item">
                                        <div className="avatar small">{getDisplayName(friendId).substring(0,2)}</div>
                                        <div className="item-details">
                                            <h4>{getDisplayName(friendId)}</h4>
                                        </div>
                                        <button className="action-btn" onClick={() => startChat(friendId)}>Chat</button>
                                    </div>
                                );
                            })}
                        </div>
                    )}
                </div>
            </aside>

            <main className="main-content">
                {activeConversation ? (
                    <ChatWindow 
                        conversation={activeConversation} 
                        currentUser={user} 
                        otherUserName={getDisplayName(getOtherMemberId(activeConversation.members))}
                    />
                ) : (
                    <div className="glass-panel empty-chat">
                        <div className="empty-chat-icon">💬</div>
                        <h2>No Conversation Selected</h2>
                        <p>Choose an existing chat or add a friend to start talking.</p>
                        <button className="start-chat-cta" onClick={() => setActiveTab('friends')}>Start a Chat</button>
                    </div>
                )}
            </main>
        </div>
    );
};

export default Dashboard;
