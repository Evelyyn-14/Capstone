import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getJWTToken } from './restdb';

export function LoginForm({ setUsername }) {
  const [formData, setFormData] = useState({ username: '', password: '' });
  const [status, setStatus] = useState({
    status: 'init',
    message: "Enter credentials and click 'login'",
    token: '',
  });

  const navigate = useNavigate();

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const onRegisterClick = () => {
    navigate('/register');
  };

  const onLoginClick = async ({ username, password }) => {
    if (!username || !password) {
      setStatus({
        status: 'error',
        message: 'Username and password cannot be empty',
        token: '',
      });
      return;
    }

    try {
      console.debug('Attempting login with', { username, password });
      const response = await getJWTToken(username, password);
      console.debug('getJWTToken response', response);

      setStatus({ status: response.status, message: response.message, token: response.token });

      if (response.status === 'success') {
        setUsername(username);
        navigate('/app');
      }
    } catch (err) {
      console.error('Login exception', err);
      setStatus({ status: 'error', message: err.message, token: '' });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    await onLoginClick(formData);
  };

  return (
    <form className="boxed" onSubmit={handleSubmit}>
      <h3>Login</h3>

      <label>
        Username:<br />
        <input type="text" name="username" value={formData.username} onChange={handleInputChange} />
      </label>
      <br />
      <label>
        Password:<br />
        <input type="password" name="password" value={formData.password} onChange={handleInputChange} />
      </label>
      <br /><br />
      <button type="submit">Login</button>
      <button type="button" onClick={onRegisterClick}>Register</button>
      <br />
      <p style={{ color: status.status === 'error' ? 'red' : 'black' }}>
        {status.message}
      </p>
    </form>
  );
}
