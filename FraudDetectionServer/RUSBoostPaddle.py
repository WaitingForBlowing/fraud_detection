from sklearn.base import BaseEstimator, ClassifierMixin
from sklearn.tree import DecisionTreeClassifier
from sklearn.utils import check_random_state
from joblib import Parallel, delayed
import numpy as np

class RUSBoostClassifier(BaseEstimator, ClassifierMixin):
    def __init__(self, base_estimator=None, n_estimators=50, learning_rate=1.0, ratio=(1, 1), random_state=None):
        self.base_estimator = base_estimator
        self.n_estimators = n_estimators
        self.learning_rate = learning_rate
        self.ratio = ratio
        self.random_state = random_state

    def fit(self, X, y):
        n_samples = X.shape[0]
        self.estimators_ = []
        self.estimator_weights_ = np.zeros(self.n_estimators, dtype=np.float64)
        self.estimator_errors_ = np.ones(self.n_estimators, dtype=np.float64)

        # Define a helper function for fitting a single estimator
        def fit_estimator(i):
            sample_weight = np.ones(n_samples, dtype=np.float64) / n_samples
            sample_weight[y == 1] *= self.ratio[0]
            sample_weight[y == 0] *= self.ratio[1]
            if self.random_state is not None:
                random_state = check_random_state(self.random_state + i)
                self.base_estimator.set_params(random_state=random_state)
            estimator = self.base_estimator.fit(X, y, sample_weight=sample_weight)
            y_predict = estimator.predict(X)
            estimator_error = np.sum(sample_weight * (y != y_predict)) / np.sum(sample_weight)
            estimator_weight = self.learning_rate * np.log((1.0 - estimator_error) / estimator_error)
            sample_weight *= np.exp(estimator_weight * ((y == y_predict) - 0.5))
            return estimator, estimator_weight, estimator_error

        # Fit estimators in parallel
        results = Parallel(n_jobs=-1, verbose=0)(
            delayed(fit_estimator)(i) for i in range(self.n_estimators)
        )

        # Extract results from parallel processing
        for i, (estimator, estimator_weight, estimator_error) in enumerate(results):
            self.estimators_.append(estimator)
            self.estimator_weights_[i] = estimator_weight
            self.estimator_errors_[i] = estimator_error

        return self

    def predict(self, X):
        proba = self.predict_proba(X)
        return np.argmax(proba, axis=1)

    def predict_proba(self, X):
        proba = np.zeros((X.shape[0], 2), dtype=np.float64)
        for estimator, estimator_weight in zip(self.estimators_, self.estimator_weights_):
            proba += estimator_weight * estimator.predict_proba(X)
        proba /= np.sum(self.estimator_weights_)
        return proba
