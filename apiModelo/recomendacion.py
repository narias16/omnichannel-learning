import data
from surprise import BaselineOnly
from sklearn.cluster import KMeans
import numpy as np
import pandas as pd
import pickle
import boto3
import os
import warnings
warnings.simplefilter(action='ignore', category=FutureWarning)

read = data.Data()
datos = read.read_data()

s3 = boto3.client('s3',
                aws_access_key_id=os.getenv('AWS_KEY'),
                aws_secret_access_key=os.environ.get('AWS_ACCESS_KEY'))


class Recomendacion:
    
    def train_proactivo(self):
        params = {'method': 'sgd','n_epochs': 12}
        algo = BaselineOnly(bsl_options = params)
        trainset = data.build_full_trainset() 
        model = algo.fit(trainset)
        filename = 'modelo_proactivo.sav'
        pickle.dump(modelo, open(filename, 'wb'))
        try:
            s3.upload_file(filename, 'kealearning-copy', 'models/{}'.format(filename))
            print("Upload Successful")
            return True
        except FileNotFoundError:
            print("The file was not found")
            return False
        except NoCredentialsError:
            print("Credentials not available")
            return False

    def train_reactivo(self):
        kmeans = KMeans(n_clusters=3,init='k-means++', max_iter=300, n_init=10, random_state=0)
        trainset = datos.build_full_trainset()
        kmeans.fit(trainset) 
        model = kmeans.fit(trainset)
        filename = 'modelo_reactivo.sav'
        pickle.dump(modelo, open(filename, 'wb'))
        try:
            s3.upload_file(filename, 'kealearning-copy', 'models/{}'.format(filename)) #'folder/{}'.format(filename)
            print("Upload Successful")
            return True
        except FileNotFoundError:
            print("The file was not found")
            return False
        except NoCredentialsError:
            print("Credentials not available")
            return False
    
    def predict_proactivo(self,userId):
         # get a prediction for specific users and items.
         modelo_proactivo =  s3.get_object(Bucket='kealearning-copy',Key='models/modelo_proactivo.sav')
         bodyString = modelo_proactivo['Body'].read()
         baseline = pickle.loads(bodyString)

         predictions = []
         for i in datos['id_contenido'].unique():
             predictions.append(baseline.predict(userId, i, verbose=True))
    
         dfPred = pd.DataFrame(predictions, columns=['uid', 'iid', 'rui', 'est', 'details'])      
         dfPred.sort_values(by=['est'], ascending=False)

         print(dfPred)


    
    def predict_reactivo(self,userId,ruido,conect,acelm,ubicacion,luz,canal,contenido,time):
        print(contenido)
        modelo_reactivo =  s3.get_object(Bucket ='kealearning-copy', Key='models/modelo_reactivo.sav')
        bodyString = modelo_reactivo['Body'].read()
        kmeans = pickle.loads(bodyString)
        # calcular formato y canal favorito del usuario
        labels = kmeans.labels_
        datos_usuarios = datos.copy()
        datos_usuarios = datos_usuarios[datos_usuarios['id_estudiante'] == int(userId)]
        edad = datos_usuarios['edad']
        estilo = datos_usuarios['estilo']

        correo = datos_usuarios.loc[datos_usuarios['canal']=='correo']
        web = datos_usuarios.loc[datos_usuarios['canal']=='web']

        if len(correo) > len(web):
            canal_favorito =('0')
        else:
            canal_favorito =('1')  
        pdf = datos_usuarios.loc[datos_usuarios['formato']=='pdf']
        audio = datos_usuarios.loc[datos_usuarios['formato']=='audio']
        presentacion =datos_usuarios.loc[datos_usuarios['formato']=='presentacion']
        video = datos_usuarios.loc[datos_usuarios['formato']=='video']
        if len(pdf) >= len(audio) and len(pdf) >= len(presentacion) and len(pdf) >= len(video):
            formato_favarito ='0'
        elif len(audio) >= len(pdf) and len(audio) >= len(presentacion) and len(audio) >= len(video):
            formato_favarito = '1'
        elif len(presentacion) >= len(pdf) and len(presentacion) >= len(audio) and len(presentacion) >= len(video):
            formato_favarito='2'
        elif len(video) >= len(pdf) and len(video) >= len(audio) and len(video) >= len(presentacion):
            formato_favarito='3'
                

        if(estilo.values[0] == 'secuencial-global'):
            estilo = 0
        elif (estilo.values[0] == 'visual-verbal'):
            estilo = 1
        elif(estilo.values[0] == 'activo-reflexivo'):
            estilo = 2
        else:
            estilo = 3

        # predecir cluster del usurio
        data = np.array([['','edad','estilo','canal_fav','formato_fav'],
                ['row1',edad.values[0],estilo,canal_favorito,formato_favarito]])

        i = pd.DataFrame(data=data[1:,1:],
                        index=data[1:,0],
                        columns=data[0,1:])
        cluster = kmeans.predict(i)

        #mirar cuantos usuarios del mismo grupo(label) hay con el mismo contexto
        copy =  pd.DataFrame()
        copy['usuario']=datos['id_estudiante'].values
        copy['id_contenido']=datos['id_contenido'].values
        copy['rating']=datos['rating'].values
        copy['canal']=datos['canal'].values
        copy['conect']=datos['conectividad'].values
        copy['luz']=datos['luz'].values
        copy['ubicacion']=datos['ubicacion'].values
        copy['acel']=datos['acelerometro'].values
        copy['time']=datos['timestamp'].values
        copy['ruido']=datos['ruido'].values
        copy['label'] = labels

        group_referrer_index = copy['label'] == cluster[0]
        group_referrals = copy[group_referrer_index]

        diversidadGrupo =  pd.DataFrame()

        ux = []
        diversidadGrupo['cantidad']=group_referrals.groupby('conect').size()
        #user_context = pd.DataFrame(columns=['usuario','id_cotenido','canal','rating'])
        for index, row in group_referrals.iterrows():
            if row["conect"] == 'alto':
                if row['luz'] == 'medio':
                    if row['ubicacion'] == 'casa':
                        if row['time'] == 'weekday':
                            if row['ruido'] == 'bajo':
                                if row['acel'] == 'quieto':
                                    ux.append([row[0:4].usuario,row[0:4].id_contenido,row[0:4].canal,row[0:4].rating])
        user_context = pd.DataFrame(ux,columns=['usuario','id_contenido','canal','rating'])
        best_content_context= user_context.sort_values(by='rating',ascending=False)

        if contenido == 'undefined' and canal == 'web':
            content_contexto = best_content_context.loc[best_content_context['canal'] =='web']
            return str(content_contexto.iloc[0:3]['id_contenido'].values)
        elif canal == 'web':
            # df.loc[df['column_name'] == some_value]
            canal_contexto = best_content_context.loc[best_content_context['canal'] =='web']
            contenidos = contenido.split(',')
            contenido = [ int(x) for x in contenidos ]
            content_canal_contexto = best_content_context.loc[best_content_context['id_contenido'].isin(contenido)]
            return str(content_canal_contexto.iloc[0]['id_contenido'])
        else:
            canal = str(best_content_context.iloc[0]['canal'])
            contenido = str(best_content_context.iloc[0]['id_contenido'])
            canal_contenido = [contenido,canal]
            return str(canal_contenido)
 

