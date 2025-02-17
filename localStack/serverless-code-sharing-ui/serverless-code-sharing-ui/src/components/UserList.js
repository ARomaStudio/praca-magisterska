import React, { useEffect, useState } from "react";

const UserList = ({ onEdit }) => {
    const [codes, setCodes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchCodes = async () => {
            try {
                const response = await fetch("http://localhost:8082/lambdas");
                if (!response.ok) {
                    throw new Error(`Error: ${response.statusText}`);
                }
                const data = await response.json();
                setCodes(data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchCodes();
    }, []);

    const downloadProto = async (lambdaId) => {
        try {
            const response = await fetch(`http://localhost:8082/download?key=${lambdaId}`, {
                method: "GET",
            });

            if (!response.ok) {
                throw new Error(`Error downloading file: ${response.statusText}`);
            }

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement("a");
            link.href = url;
            link.download = `${lambdaId}.proto`; // Set the filename for download
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        } catch (err) {
            alert(`Failed to download proto file: ${err.message}`);
        }
    };

    const handleDelete = async (id) => {
        try {
            const response = await fetch(`http://localhost:8082/lambdas/${id}`, {
                method: "DELETE",
            });

            if (!response.ok) {
                throw new Error(`Error deleting lambda: ${response.statusText}`);
            }

            // Remove the deleted lambda from the list
            setCodes((prevCodes) => prevCodes.filter((code) => code.id !== id));
        } catch (err) {
            alert(`Failed to delete lambda: ${err.message}`);
        }
    };

    if (loading) return <p className="text-center mt-4">Loading...</p>;
    if (error) return <p className="text-danger text-center mt-4">Error: {error}</p>;

    return (
        <div className="container mt-5">
            <h2 className="text-center mb-4">Code List</h2>
            {codes.length === 0 ? (
                <p className="text-center text-muted">No code available. Add code!</p>
            ) : (
                <ul className="list-group">
                    {codes.map((code) => (
                        <li key={code.id} className="list-group-item d-flex justify-content-between align-items-center">
                            <div>
                                <strong>{code.code}</strong>: {code.comment}
                            </div>
                            <div>
                                <button
                                    className="btn btn-primary btn-sm me-2"
                                    onClick={() => onEdit(code)}
                                >
                                    Edit
                                </button>
                                <button
                                    className="btn btn-danger btn-sm me-2"
                                    onClick={() => handleDelete(code.id)}
                                >
                                    Delete
                                </button>
                                <button
                                    className="btn btn-success btn-sm"
                                    onClick={() => downloadProto(code.id)}
                                >
                                    Download Proto
                                </button>
                            </div>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default UserList;
