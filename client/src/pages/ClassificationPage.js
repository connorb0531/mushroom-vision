import React, { useState } from 'react';
import ImageUploader from '../components/ImageUploader';
import ClassificationResult from '../components/ClassificationResult';

const ClassificationPage = () => {
  const [selectedImage, setSelectedImage] = useState(null);
  const [classificationResult, setClassificationResult] = useState(null);
  const [error, setError] = useState(null);

  const handleImageSelect = (imageData) => {
    setSelectedImage(imageData);
    setClassificationResult(null);
    setError(null);
  };

  const handleClassificationResult = (result) => {
    if (result.success !== false) {
      setClassificationResult(result.result);
      setError(null);
    } else {
      setError(result.message || 'Classification failed');
    }
  };

  return (
    <main className="flex flex-1 items-center justify-center">
      <div className="w-full max-w-4xl px-4 py-10">
        <div className="text-center mb-8">
          <h1 className="mb-4 text-4xl font-bold text-white">
            Mushroom Classification
          </h1>
          <p className="text-lg text-slate-300">
            Upload an image to classify if a mushroom is edible or poisonous
          </p>
        </div>

        <div className="bg-white/10 backdrop-blur-sm rounded-2xl p-8 shadow-xl">
          <div className="mb-6">
            <h2 className="mb-4 text-2xl font-semibold text-white">
              Upload Mushroom Image
            </h2>
            <ImageUploader
              onImageSelect={handleImageSelect}
              selectedImage={selectedImage}
              onClassificationResult={handleClassificationResult}
            />
          </div>

          {error && (
            <div className="mb-6 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-red-700">
              <p>{error}</p>
            </div>
          )}

          <ClassificationResult
            result={classificationResult}
            isLoading={false}
          />
        </div>

        <div className="mt-8 text-center">
          <div className="rounded-xl border border-yellow-200 bg-yellow-50 px-6 py-4 text-yellow-800">
            <p>
              <strong>Disclaimer:</strong> This tool is for educational purposes only. 
              Always consult with a mycologist or expert before consuming any wild mushrooms. 
              When in doubt, don't eat it!
            </p>
          </div>
        </div>
      </div>
    </main>
  );
};

export default ClassificationPage;
