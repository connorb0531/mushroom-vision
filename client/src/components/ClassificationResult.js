import React from 'react';
import './ClassificationResult.css';

const ClassificationResult = ({ result, isLoading }) => {
  if (isLoading) {
    return (
      <div className="classification-result loading">
        <div className="loading-spinner"></div>
        <p>Analyzing mushroom image...</p>
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
    <div className="classification-result">
      <div className={`result-header ${isEdible ? 'edible' : 'poisonous'}`}>
        <div className="result-icon">
          {isEdible ? '✅' : '⚠️'}
        </div>
        <div className="result-text">
          <h2 className="prediction">
            {isEdible ? 'Edible' : 'Poisonous'}
          </h2>
          <p className="confidence">
            {confidencePercentage}% confidence
          </p>
        </div>
      </div>

      <div className="probability-bars">
        <div className="probability-item">
          <div className="probability-label">
            <span>Edible</span>
            <span>{ediblePercentage}%</span>
          </div>
          <div className="probability-bar">
            <div 
              className="probability-fill edible"
              style={{ width: `${ediblePercentage}%` }}
            ></div>
          </div>
        </div>

        <div className="probability-item">
          <div className="probability-label">
            <span>Poisonous</span>
            <span>{poisonousPercentage}%</span>
          </div>
          <div className="probability-bar">
            <div 
              className="probability-fill poisonous"
              style={{ width: `${poisonousPercentage}%` }}
            ></div>
          </div>
        </div>
      </div>

      <div className="result-warning">
        {isEdible ? (
          <div className="warning edible-warning">
            <p><strong>⚠️ Important:</strong> This classification is for educational purposes only. 
            Always consult with a mycologist or expert before consuming any wild mushrooms. 
            When in doubt, don't eat it!</p>
          </div>
        ) : (
          <div className="warning poisonous-warning">
            <p><strong>🚨 Warning:</strong> This mushroom appears to be poisonous. 
            Do not consume and keep away from children and pets. 
            If accidental ingestion occurs, contact poison control immediately.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ClassificationResult;
