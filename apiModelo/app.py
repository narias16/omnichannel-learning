#! /usr/bin/env python
from flask import Flask, jsonify, request, abort
import recomendacion as recom
app = Flask(__name__)


@app.route('/api/recommend', methods=['GET','POST'])
def get_recommendation():
   if not request.json:
        abort(400)
   userid = request.json['id_estudiante']
   ruido = request.json['ruido']
   conect = request.json['conect']
   acelm = request.json['acelm']
   ubicacion = request.json['ubicacion']
   luz = request.json['luz']
   canal = request.json['canal']
   time = request.json['timestamp']
   contenido = request.json['id_contenido']
   recomend = recom.Recomendacion()
   print(contenido)
   if contenido =='undefined':
      print('no hay contenido')
      #  mirar que contenidos se le recomienda (ratings)
      #contenido_rec =  recomend.predict_proactivo(userid)
      # segun contexto mirar cual es es el mejor canal para el contenido previamente contenido
      recomendacion_final = recomend.predict_reactivo(userid,ruido,conect,acelm,ubicacion,luz,canal,'undefined',time)
   else:
      recomendacion_final = recomend.predict_reactivo(userid,ruido,conect,acelm,ubicacion,luz,canal,contenido,time)
   return jsonify({'contenido_canal': recomendacion_final})
   
@app.route('/api/train', methods=['GET'])
def train_modelo_reactivo():
   recomendacion= recomendacion.Recomendacion()
   recomendacion.train_proactivo()
   recomendacion.train_reactivo()
   return jsonify('Entrenado :)')


if __name__ == '__main__':
    app.run(debug=True)

