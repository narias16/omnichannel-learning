import csv
import random
import pandas as pd 
import os

tipoC = ["pdf","video",'audio','presentacion']
genero =['m','f']
nivelContexto = ['alto','medio', 'bajo']
move = ['movimiento','quieto']
location = ['casa', 'universidad', 'otro']
medio = ['correo','web']
estiloAprendizaje = ['activo-reflexivo','sensorial-intuitivo','visual-verbal', 'secuencial-global' ]
estudiante = {}
# falta ruido[bajo,medio,alto]                              
dfReactivo = pd.DataFrame(columns=['id_contenido','id_estudiante','edad','genero','estilo','formato','tamaño','duracion','tipo_interactivo','nivel_interactivo','ruido', 'luz','ubicacion','conectividad','acelerometro','canal','rating'])
i = 0
path = os.getcwd()+'/ex'
for file in os.listdir(path):
    print(file)
    dataFile = open(path+'/'+file,'r')
    line = dataFile.readline()
    idC = line[0]
    rand = random.randint(0, 3)
    tam = random.randint(2, 1024)
    duracion =  random.randint(0, 180)
    formato = tipoC[rand]
    if formato in ('PDF', 'audio'):
        tipoI = "activo"
        nivelI = 0
    else:
        rand = random.randint(0, 1)
        tipoI = ["interactivo",'combinado']
        tipoI = tipoI[rand]
        rand = random.randint(1, 5)
        nivelI = rand
    for line in dataFile:
        line = line.split(',')
        idS = line[0]
        rating = line[1]
        ruido = nivelContexto[random.randint(0,2)]
        luz = nivelContexto[random.randint(0,2)]
        acelerometro = move[random.randint(0,1)]
        canal = medio[random.randint(0,1)]
        conectividad = nivelContexto[random.randint(0,2)]
        ubicacion = location[random.randint(0,2)]
        if idS not in estudiante:
            edad = random.randint(17,25)
            sexo = genero[random.randint(0,1)]
            estilo = estiloAprendizaje[random.randint(0,3)]
            estudiante[idS] = [edad,sexo,estilo]
        else:
            edad = estudiante.get(idS)[0]
            sexo = estudiante.get(idS)[1]
            estilo = estudiante.get(idS)[2]
        csvData = [idC,idS,edad, sexo, estilo, formato, tam, duracion, tipoI, nivelI, ruido, luz, ubicacion, conectividad, acelerometro,canal, rating]
        dfReactivo.loc[i] = csvData
        i = i+1
    dataFile.close()

dfProactivo =  pd.DataFrame(columns=['id_estudiante', 'ruido', 'luz','ubicacion','conectividad','acelerometro','canal','rating'])
dfProactivo['id_estudiante'] = dfReactivo['id_estudiante']
dfProactivo['ruido'] = dfReactivo['ruido']
dfProactivo['luz'] = dfReactivo['luz']
dfProactivo['ubicacion'] = dfReactivo['ubicacion']
dfProactivo['conectividad'] = dfReactivo['conectividad']
dfProactivo['acelerometro'] = dfReactivo['acelerometro']
dfProactivo['canal'] = dfReactivo['canal']
dfProactivo['rating'] = dfReactivo['rating']

dfReactivo.to_csv('reactivo.csv',index=False)
dfProactivo.to_csv('proactivo.csv',index=False)



