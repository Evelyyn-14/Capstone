import { useState } from 'react';
import { registerUser } from './restdb';
import { useNavigate } from 'react-router-dom';

export function RegisterForm(props) {

    const navigate = useNavigate();
    let [formData, setFormData] = useState({ username: "", password: "", email: "" });

    const handleInputChange = (event) => {
        const { name, value } = event.target;
        setFormData({ ...formData, [name]: value });
    };

    const onRegisterClick = async (formData) => {     
        const response = await registerUser(formData.username, formData.password, formData.email);
        navigate("/login");
    };

    return (
        <form className='boxed'>
            <h3>Register as a new user.</h3>
            <p>Please enter a username and password to continue.</p>

            Username:<br />
            <input type="text" name="username"
                value={formData.username}
                onChange={handleInputChange} />

            <br />

            Password:<br />
            <input type="password" name="password"
                value={formData.password}
                onChange={handleInputChange} />

            <br />
            Email:<br />
            <input type="email" name="email"
                value={formData.email}
                onChange={handleInputChange} />

            <br />
            <br />
            <button type="button" onClick={() => onRegisterClick(formData)}>Register</button>

        </form>
    )
}