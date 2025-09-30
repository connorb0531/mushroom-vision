import React from 'react';

const ClassificationResult = ({ result, isLoading }) => {
  if (isLoading) {
    return (
      <div className="flex flex-col items-center justify-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mb-4"></div>
        <p className="text-gray-700 text-lg">Analyzing mushroom image...</p>
      </div>
    );
  }

  if (!result) {
    return null;
  }

  const { prediction, confidence, probabilities } = result;
  const isEdible = prediction === 'edible';
  const confidencePercentage = Math.round(confidence * 100);
  const ediblePercentage = Math.round(probabilities.edible * 100);
  const poisonousPercentage = Math.round(probabilities.poisonous * 100);

  return (
    <div className="mt-8">
      <div className={`rounded-2xl p-6 shadow-lg ${
        isEdible 
          ? 'bg-green-50 border-2 border-green-200' 
          : 'bg-red-50 border-2 border-red-200'
      }`}>
        <div className="flex items-center gap-4 mb-6">
          <div className={`text-4xl ${isEdible ? 'text-green-600' : 'text-red-600'}`}>
            {isEdible ? (
              <svg className="w-10 h-10" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
              </svg>
            ) : (
              <svg className="w-10 h-10" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
              </svg>
            )}
          </div>
          <div>
            <h2 className={`text-3xl font-bold ${isEdible ? 'text-green-600' : 'text-red-600'}`}>
              {isEdible ? 'Edible' : 'Poisonous'}
            </h2>
            <p className="text-gray-700 text-lg">
              {confidencePercentage}% confidence
            </p>
          </div>
        </div>

        <div className="space-y-4">
          <div className="space-y-2">
            <div className="flex justify-between text-sm text-gray-700">
              <span>Edible</span>
              <span>{ediblePercentage}%</span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-3">
              <div 
                className="bg-green-500 h-3 rounded-full transition-all duration-500"
                style={{ width: `${ediblePercentage}%` }}
              ></div>
            </div>
          </div>

          <div className="space-y-2">
            <div className="flex justify-between text-sm text-gray-700">
              <span>Poisonous</span>
              <span>{poisonousPercentage}%</span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-3">
              <div 
                className="bg-red-500 h-3 rounded-full transition-all duration-500"
                style={{ width: `${poisonousPercentage}%` }}
              ></div>
            </div>
          </div>
        </div>

        <div className="mt-6">
          {isEdible ? (
            <div className="rounded-xl border border-yellow-200 bg-yellow-50 px-4 py-3 text-yellow-800">
              <p><strong>Important:</strong> This classification is for educational purposes only. 
              Always consult with a mycologist or expert before consuming any wild mushrooms. 
              When in doubt, don't eat it!</p>
            </div>
          ) : (
            <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-red-800">
              <p><strong>Warning:</strong> This mushroom appears to be poisonous. 
              Do not consume and keep away from children and pets. 
              If accidental ingestion occurs, contact poison control immediately.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ClassificationResult;
