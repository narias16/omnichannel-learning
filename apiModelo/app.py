from flask import Flask, jsonify, request, abort

app = Flask(__name__)


@app.route('/recommend', methods=['GET','POST'])
def get_recommendation():
     if not request.json:
        abort(400)
     userid = request.json['id_estudiante']
     ruido = request.json['ruido']
     conect = request.json['conect']
     acelm = request.json['acelm']
     ubicacion = request.json['ubicacion']
     luz = request.json['luz']
     contents = request.json['id_contenido']
     contenido_canal = {'contenido' : 1,'canal': 'correo'} #recommend(userid,ruido,conect,acelm,ubicacion,luz,contents)
     print(userid)
     return jsonify({'contenido_canal': contenido_canal})


if __name__ == '__main__':
    app.run(debug=True)

