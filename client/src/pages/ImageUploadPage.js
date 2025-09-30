import React, { useState } from 'react';
import ImageUploader from '../components/ImageUploader';

const ImageUploadPage = () => {
  const [selectedImage, setSelectedImage] = useState(null);
  const [result, setResult] = useState(null);

  const handleImageSelect = (imageData) => {
    setSelectedImage(imageData);
    setResult(null); // reset any old result
  };

  const handleClassificationResult = (classificationResult) => {
    setResult(classificationResult);
  };

  return (
    <div className="max-w-3xl mx-auto p-6 space-y-6">
      <h1 className="text-2xl font-bold">Mushroom Classification</h1>

      <ImageUploader
        onImageSelect={handleImageSelect}
        selectedImage={selectedImage}
        onClassificationResult={handleClassificationResult}
      />

      {result && (
        <div className="mt-6 p-4 border rounded-lg bg-gray-50">
          {result.success !== false ? (
            <>
              <h2 className="text-lg font-semibold">Prediction: {result.result.prediction}</h2>
              <p>Confidence: {(result.result.confidence * 100).toFixed(2)}%</p>
            </>
          ) : (
            <p className="text-red-600">{result.message}</p>
          )}
        </div>
      )}
    </div>
  );
};

export default ImageUploadPage;
