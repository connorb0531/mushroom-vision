import React from 'react';
import ImageUploadPage from './pages/ImageUploadPage';
import Navbar from './components/Navbar';
import './App.css';


export default function App() {
    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-blue-100">
            <Navbar />
            <ImageUploadPage />
        </div>
    );
}
