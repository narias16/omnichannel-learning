import pandas as pd
import numpy as np


class Data:
    def read_data(self):
        data = pd.read_csv('../SensorData/reactivoContexto.csv')
        return data