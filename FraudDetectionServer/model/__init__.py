from joblib import load
import os

logistic = load(os.path.join(os.path.dirname(__file__), 'logistic.pkl'))
svm = load(os.path.join(os.path.dirname(__file__), 'SVM.plk'))
rus = load(os.path.join(os.path.dirname(__file__), 'RUSBoost.plk'))

model_map = {
    1: logistic,
    2: svm,
    3: rus
}
