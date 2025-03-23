import React, { useState, useEffect } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import axios from 'axios';

const UserForm = ({ onSave, userToEdit, onUpdateList }) => {
    const [user, setUser] = useState({
        id: userToEdit?.id || '',
        code: userToEdit?.code || '',
        comment: userToEdit?.comment || '',
        active: userToEdit?.active ?? true
    });
    const [error, setError] = useState(null); // To store errors, if any
    const [success, setSuccess] = useState(null); // To store success messages

    useEffect(() => {
        if (userToEdit) {
            setUser(userToEdit);
        } else {
            setUser({ id: '', code: '', comment: '', active: true });
        }
    }, [userToEdit]);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setUser(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null); // Clear previous errors
        setSuccess(null); // Clear previous success messages

        // Regular expression to validate a basic Java function
        const javaFunctionRegex = /public\s+\w+\s+\w+\([\w\s,]*\)\s*\{[\s\S]*\}/;

        if (!javaFunctionRegex.test(user.code)) {
            setError("The code must be a valid Java function.");
            return;
        }

        try {
            let response;

            if (user.id) {
                // Update existing lambda
                response = await axios.put('http://localhost:8082/lambda', {
                    id: user.id,
                    code: user.code,
                    comment: user.comment,
                    isActive: user.active,
                    threadId: user.threadId // Include threadId if necessary
                });
            } else {
                // Create new lambda using the /adapt endpoint
                response = await axios.post('http://localhost:8082/adapt', {
                    code: user.code,
                    comment: user.comment,
                    isActive: user.active,
                    threadId: user.threadId // Include threadId if necessary
                });
            }

            // Check if the response status indicates success
            if (response.status !== 200) {
                if (response.data && response.data.error) {
                    setError("Failed to save code."); // Reset form after success
                }
            } else {
                setSuccess(user.id ? "Code updated successfully!" : "Lambda function created successfully!");
                onSave(response.data); 
                window.location.reload();
                // setUser({ id: '', code: '', comment: '', active: true });
            }
        } catch (err) {
            // Handle and display errors
            setError(err.response?.data?.message || err.message); // Use specific error message if available
            setSuccess(null); // Clear success message on error
        }
    };

    return (
        <div className="container mt-4">
            <form onSubmit={handleSubmit} className="card p-4 shadow">
                <h2 className="mb-3">{userToEdit ? "Edit Code" : "Add Code"}</h2>
                {error && (
                    <div className="alert alert-danger" role="alert">
                        {error}
                    </div>
                )}
                {success && (
                    <div className="alert alert-success" role="alert">
                        {success}
                    </div>
                )}
                <div className="mb-3">
                    <label htmlFor="code" className="form-label">
                        Code:
                    </label>
                    <textarea
                        type="text"
                        id="code"
                        name="code"
                        className="form-control"
                        value={user.code}
                        onChange={handleChange}
                        required
                    ></textarea>
                </div>
                <div className="mb-3">
                    <label htmlFor="comment" className="form-label">
                        Comment:
                    </label>
                    <textarea
                        id="comment"
                        name="comment"
                        className="form-control"
                        rows="3"
                        value={user.comment}
                        onChange={handleChange}
                        required
                    ></textarea>
                </div>
                <div className="mb-3 form-check">
                    <input
                        type="checkbox"
                        className="form-check-input"
                        id="active"
                        name="active"
                        checked={user.active}
                        onChange={handleChange}
                    />
                    <label className="form-check-label" htmlFor="active">Active</label>
                </div>
                <button type="submit" className="btn btn-primary">
                    {userToEdit ? "Update" : "Add"} Code
                </button>
            </form>
        </div>
    );
};

export default UserForm;