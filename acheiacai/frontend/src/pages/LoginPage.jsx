
import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';

import styles from './LoginPage.module.css'

function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        const success = await login(username, password);
        if (success) {
            navigate('/'); // Redireciona para a página principal após o login
        } else {
            setError('Utilizador ou senha inválidos.');
        }
    };

    return (
        <div className={styles.loginPageContainer}>
            <form className={styles.loginForm} onSubmit={handleSubmit}>
                <h2>Login</h2>
                
                <div className={styles.formGroup}>
                    <label htmlFor="username">Utilizador</label>
                    <input
                        id="username"
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div className={styles.formGroup}>
                    <label htmlFor="password">Senha</label>
                    <input
                        id="password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>

                {error && <p className={styles.errorMessage}>{error}</p>}
                
                <button type="submit" className={styles.loginButton}>Entrar</button>
            </form>
        </div>
    );
}

export default LoginPage;