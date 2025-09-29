import React, { useState } from 'react';
import ImageUploader from './components/ImageUploader';
import ClassificationResult from './components/ClassificationResult';
import { classificationAPI } from './services/api';
import './App.css';

function App() {
  const [selectedImage, setSelectedImage] = useState(null);
  const [imageName, setImageName] = useState('');
  const [classificationResult, setClassificationResult] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleImageSelect = (imageData, fileName) => {
    setSelectedImage(imageData);
    setImageName(fileName);
    setClassificationResult(null);
    setError(null);
  };

  const handleClassify = async () => {
    if (!selectedImage) {
      setError('Please select an image first');
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      const result = await classificationAPI.classifyImage(selectedImage, imageName);
      
      if (result.success) {
        setClassificationResult(result.result);
      } else {
        setError(result.message || 'Classification failed');
      }
    } catch (err) {
      setError('Error connecting to the server. Please make sure the backend is running.');
      console.error('Classification error:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleReset = () => {
    setSelectedImage(null);
    setImageName('');
    setClassificationResult(null);
    setError(null);
  };

  return (
    <div className="App">
      <header className="app-header">
        <div className="header-content">
          <h1 className="app-title">
            🍄 Mushroom Vision
          </h1>
          <p className="app-subtitle">
            AI-Powered Mushroom Classification System
          </p>
        </div>
      </header>

      <main className="app-main">
        <div className="container">
          <div className="upload-section">
            <h2>Upload Mushroom Image</h2>
            <ImageUploader
              onImageSelect={handleImageSelect}
              selectedImage={selectedImage}
              isLoading={isLoading}
            />
            
            {selectedImage && (
              <div className="action-buttons">
                <button
                  className="classify-btn"
                  onClick={handleClassify}
                  disabled={isLoading}
                >
                  {isLoading ? 'Classifying...' : 'Classify Mushroom'}
                </button>
                <button
                  className="reset-btn"
                  onClick={handleReset}
                  disabled={isLoading}
                >
                  Reset
                </button>
              </div>
            )}
          </div>

          {error && (
            <div className="error-message">
              <p>❌ {error}</p>
            </div>
          )}

          <ClassificationResult
            result={classificationResult}
            isLoading={isLoading}
          />
        </div>
      </main>

      <footer className="app-footer">
        <div className="container">
          <p>
            <strong>⚠️ Disclaimer:</strong> This tool is for educational purposes only. 
            Always consult with a mycologist or expert before consuming any wild mushrooms. 
            When in doubt, don't eat it!
          </p>
        </div>
      </footer>
    </div>
  );
}

export default App;
