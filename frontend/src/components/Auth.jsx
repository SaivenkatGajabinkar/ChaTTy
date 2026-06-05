import React, { useState } from 'react';
import { authService } from '../services/api';
import './Auth.css';

const Auth = ({ onLogin }) => {
    const [isLogin, setIsLogin] = useState(true);
    const [showPassword, setShowPassword] = useState(false);
    const [rememberMe, setRememberMe] = useState(false);
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        password: '',
        preferredLanguage: 'en'
    });
    const [error, setError] = useState('');

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            let response;
            if (isLogin) {
                response = await authService.login(formData.email, formData.password);
            } else {
                response = await authService.register(formData);
            }
            
            const userData = response.data;
            localStorage.setItem('token', userData.token);
            onLogin(userData);
            
        } catch (err) {
            setError(err.response?.data || "An error occurred");
        }
    };

    return (
        <div className="auth-container">
            <div className="glass-panel auth-card">
                <div className="auth-header">
                    <h2>ChaTT</h2>
                    <p>An Online Chatting Platform</p>
                </div>
                
                {error && <div className="error-alert">{error}</div>}

                <form onSubmit={handleSubmit} className="auth-form">
                    {!isLogin && (
                        <input 
                            type="text" name="name" placeholder="Full Name" 
                            value={formData.name} onChange={handleChange} required 
                        />
                    )}
                    
                    <input 
                        type="email" name="email" placeholder="Email" 
                        value={formData.email} onChange={handleChange} required 
                    />
                    
                    <div className="password-wrapper">
                        <input 
                            type={showPassword ? "text" : "password"} name="password" placeholder="Password" 
                            value={formData.password} onChange={handleChange} required 
                        />
                        <span className="eye-icon" onClick={() => setShowPassword(!showPassword)}>
                            {showPassword ? '👁️' : '🙈'}
                        </span>
                    </div>
                    
                    {isLogin && (
                        <div className="forgot-password">
                            <a href="#">Forgot Password?</a>
                        </div>
                    )}
                    
                    {!isLogin && (
                        <select name="preferredLanguage" value={formData.preferredLanguage} onChange={handleChange}>
                            <option value="en">English (EN)</option>
                            <option value="es">Spanish (ES)</option>
                            <option value="fr">French (FR)</option>
                            <option value="de">German (DE)</option>
                            <option value="zh">Chinese (ZH)</option>
                            <option value="ja">Japanese (JA)</option>
                        </select>
                    )}

                    <div className="remember-me">
                        <input 
                            type="checkbox" 
                            id="remember" 
                            checked={rememberMe} 
                            onChange={(e) => setRememberMe(e.target.checked)} 
                        />
                        <label htmlFor="remember">Remember Me</label>
                    </div>
                    
                    <button type="submit">{isLogin ? 'Sign In' : 'Sign Up'}</button>
                </form>

                <p className="auth-toggle">
                    {isLogin ? "Don't have an account? " : "Already have an account? "}
                    <span onClick={() => setIsLogin(!isLogin)}>
                        {isLogin ? "Sign Up" : "Sign In"}
                    </span>
                </p>
            </div>
        </div>
    );
};

export default Auth;
