import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getJWTToken } from './restdb';

export function LoginForm(props) {
  let [formData, setFormData] = useState({ username: "", password: "" });
  let [status, setStatus] = useState({ status: "init", message: "Enter credentials and click 'login'", token: "" });
  const navigate = useNavigate();

  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setFormData({ ...formData, [name]: value });
  };

  const onRegisterClick = (formData) => {
    navigate("/register");
  };

  let onLoginClick = async function (credentials) {
    if (credentials === undefined ||
      credentials === null ||
      credentials.username === ""
      || credentials.password === "") {
      alert("Username and password cannot be empty");
      return;
    }
    const response = await getJWTToken(credentials.username, credentials.password);
    setStatus({ status: response.status, message: response.message, token: response.token });
    if (response.status === "error") {
    } else if (response.status === "success") {
      props.setUsername(credentials.username);
      navigate("/app");
    }


  }

  return (
    <form className='boxed'>
      <h3>Login</h3>
      {/* <p>Please enter your username and password to continue.</p>             */}

      Username:<br />
      <input type="text" name="username"
        value={formData.username}
        onChange={handleInputChange} />

      <br />

      Password:<br />
      <input type="password" name="password"
        value={formData.password}
        onChange={handleInputChange} />

      <br /><br />
      <button type="button" onClick={() => onLoginClick(formData)}>Login</button>
      <button type="button" onClick={() => onRegisterClick(formData)}>Register</button>
      <br />
      <p>{status.message} </p>

    </form>
  )
}