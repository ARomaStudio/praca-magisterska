import React, { useState, useEffect } from "react";
import "bootstrap/dist/css/bootstrap.min.css";

const UserForm = ({ onSave, userToEdit }) => {
    const [user, setUser] = useState({ code: "", comment: "" });
    const [error, setError] = useState(null); // To store errors, if any
    const [success, setSuccess] = useState(null); // To store success messages

    useEffect(() => {
        if (userToEdit) {
            setUser(userToEdit);
        } else {
            setUser({ code: "", comment: "" });
        }
    }, [userToEdit]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setUser((prev) => ({ ...prev, [name]: value }));
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
            const url = userToEdit
                ? `http://localhost:8082/adapt/${userToEdit.id}` // Update specific lambda by ID
                : "http://localhost:8082/adapt"; // Create new lambda

            const method = userToEdit ? "PUT" : "POST"; // Use PUT for updates and POST for new entries

            const response = await fetch(url, {
                method,
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ code: user.code, comment: user.comment }), // Include both code and comment
            });

            if (!response.ok) {
                const errorData = await response.json();
                console.log("Error response code:", response);
                throw new Error(errorData.message || "Failed to save code.");
            }

            const responseData = await response.json();
            setSuccess(userToEdit ? "Code updated successfully!" : "Code added successfully!"); // Display appropriate success message
            onSave(responseData); // Pass the server response to the parent component
            setUser({ code: "", comment: "" }); // Reset form after success
        } catch (err) {
            setError(err.message); // Handle and display errors
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
                <button type="submit" className="btn btn-primary">
                    {userToEdit ? "Update" : "Add"} Code
                </button>
            </form>
        </div>
    );
};

export default UserForm;