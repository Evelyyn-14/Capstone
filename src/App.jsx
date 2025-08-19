import React, { useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';
import { LoginForm } from './LoginForm.jsx';
import { RegisterForm } from './RegisterForm.jsx';
import AppPage from './AppPage.jsx';

export function App(params) {

  const [username, setUsername] = useState("");

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginForm username={username} setUsername={setUsername} />} />
        <Route path="/register" element={<RegisterForm />} />
        <Route path="/app" element={<AppPage username={username} setUsername={setUsername} />} />
        <Route path="/" element={<Navigate to="/login" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
