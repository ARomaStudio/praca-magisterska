import React, { useEffect, useState } from "react";

const UserList = ({ onEdit }) => {
    const [codes, setCodes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [threadColors, setThreadColors] = useState({});

    useEffect(() => {
        const fetchCodes = async () => {
            try {
                const response = await fetch("http://localhost:8082/lambdas");
                if (!response.ok) {
                    throw new Error(`Error: ${response.statusText}`);
                }
                const data = await response.json();
                setCodes(data);
                
                // Generate colors for each thread
                const colors = {};
                data.forEach(code => {
                    if (!colors[code.threadId]) {
                        colors[code.threadId] = `hsl(${Math.random() * 360}, 70%, 90%)`;
                    }
                });
                setThreadColors(colors);
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

            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("application/json")) {
                const jsonData = await response.json();
                throw new Error(jsonData.message || "Error downloading file");
            } else if (contentType && contentType.includes("text/plain")) {
                const text = await response.text();
                if (text === "Done!") {
                    // Success case, continue with download
                    const blob = await response.blob();
                    const url = window.URL.createObjectURL(blob);
                    const link = document.createElement("a");
                    link.href = url;
                    link.download = `${lambdaId}.proto`;
                    document.body.appendChild(link);
                    link.click();
                    document.body.removeChild(link);
                    window.URL.revokeObjectURL(url);
                    return;
                }
                throw new Error(text);
            }

            // Handle binary response
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement("a");
            link.href = url;
            link.download = `${lambdaId}.proto`;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
        } catch (err) {
            alert(`Failed to download proto file: ${err.message}`);
        }
    };

    const handleDelete = async (id) => {
        try {
            const response = await fetch(`http://localhost:8082/lambda/${id}`, {
                method: "DELETE",
            });

            if (!response.ok) {
                throw new Error(`Error deleting lambda: ${response.statusText}`);
            }

            setCodes((prevCodes) => prevCodes.filter((code) => code.id !== id));
        } catch (err) {
            alert(`Failed to delete lambda: ${err.message}`);
        }
    };

    if (loading) return (
        <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '200px' }}>
            <div className="spinner-border text-primary" role="status">
                <span className="visually-hidden">Loading...</span>
            </div>
        </div>
    );
    
    if (error) return (
        <div className="alert alert-danger m-4" role="alert">
            <i className="bi bi-exclamation-triangle me-2"></i>
            Error: {error}
        </div>
    );

    // Group codes by thread_id
    const groupedCodes = codes.reduce((acc, code) => {
        if (!acc[code.threadId]) {
            acc[code.threadId] = [];
        }
        acc[code.threadId].push(code);
        return acc;
    }, {});

    return (
        <div className="container mt-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2 className="mb-0">Code List</h2>
                <span className="badge bg-primary">{codes.length} items</span>
            </div>
            
            {codes.length === 0 ? (
                <div className="text-center py-5">
                    <i className="bi bi-code-square display-1 text-muted mb-3"></i>
                    <p className="text-muted">No code available. Add code to get started!</p>
                </div>
            ) : (
                <div className="row">
                    {Object.entries(groupedCodes).map(([threadId, threadCodes]) => (
                        <div key={threadId} className="col-12 mb-4">
                            <div className="card shadow-sm" style={{ borderLeft: `5px solid ${threadColors[threadId]}` }}>
                                <div className="card-header bg-light">
                                    <div className="d-flex justify-content-between align-items-center">
                                        <span className="badge bg-secondary">Thread ID: {threadId}</span>
                                    </div>
                                </div>
                                <div className="card-body">
                                    {threadCodes.map((code) => (
                                        <div key={code.id} className="mb-4">
                                            <div className="d-flex justify-content-between align-items-center mb-2">
                                                <h6 className="text-muted mb-0">Code:</h6>
                                                <span className="badge bg-info">Version {code.versionNumber}</span>
                                            </div>
                                            <div className="bg-light p-3 rounded" style={{ minHeight: '100px', maxHeight: '200px', overflowY: 'auto' }}>
                                                <pre className="mb-0" style={{ whiteSpace: 'pre-wrap', wordBreak: 'break-word' }}>
                                                    {code.code}
                                                </pre>
                                            </div>
                                            <div className="mt-2">
                                                <h6 className="text-muted mb-2">Comment:</h6>
                                                <p className="card-text text-muted mb-0">{code.comment}</p>
                                            </div>
                                            <div className="mt-3">
                                                <div className="btn-group">
                                                    <button
                                                        className="btn btn-primary px-3"
                                                        onClick={() => onEdit(code)}
                                                        title="Edit"
                                                        style={{ height: '28px' }}
                                                    >
                                                        <i className="bi bi-pencil"></i>
                                                    </button>
                                                    <button
                                                        className="btn btn-danger px-3"
                                                        onClick={() => handleDelete(code.id)}
                                                        title="Delete"
                                                        style={{ height: '28px' }}
                                                    >
                                                        <i className="bi bi-trash"></i>
                                                    </button>
                                                    <button
                                                        className="btn btn-success px-3"
                                                        onClick={() => downloadProto(code.id)}
                                                        title="Download Proto"
                                                        style={{ height: '28px' }}
                                                    >
                                                        <i className="bi bi-download"></i>
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default UserList;
