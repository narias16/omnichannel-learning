import React from 'react';
import Button from '@material-ui/core/Button';
import { makeStyles } from '@material-ui/core/styles';
import Modal from '@material-ui/core/Modal';
import Table from './Table.js'
import Typography from '@material-ui/core/Typography';

function rand() {
  return Math.round(Math.random() * 20) - 10;
}

function getModalStyle() {
  const top = 50 + rand();
  const left = 50 + rand();

  return {
    top: `${top}%`,
    left: `${left}%`,
    transform: `translate(-${top}%, -${left}%)`,
  };
}

const useStyles = makeStyles(theme => ({
  paper: {
    position: 'absolute',
    width: 800,
    backgroundColor: theme.palette.background.paper,
    border: '2px solid #000',
    boxShadow: theme.shadows[5],
    padding: theme.spacing(2, 4, 3),
  },
}));

export default function SimpleModal({value, handler}) {
  const classes = useStyles();
  // getModalStyle is not a pure function, we roll the style only on the first render
  const [modalStyle] = React.useState(getModalStyle);
  const [open, setOpen] = React.useState(true);
  const [tileData, setTileData] = React.useState([]);
  const [ids, setIds] = React.useState([]);

  let data = {
    id_estudiante: '1745265',
    ruido: 'bajo',
    luz: 'bajo',
    conect: 'bajo',
    acelm: 'quieto',
    ubicacion: 'casa',
    canal: 'web',
    timestamp: 'weekday',
    id_contenido: 'undefined'
  }
  
  var httpHeaders = {
    'Access-Control-Request-Headers': 'origin, x-requested-with',
    'Content-Type': 'application/json',
    'origin': 'localhost:3000',
  }
  
  var headers = new Headers(httpHeaders);
  
  var init = {
    method: 'POST',
    headers: headers,
    mode: 'cors',
    body: JSON.stringify(data),
    cache: 'default'
  };
  
  fetch(`http://localhost:5000/api/recommend`, init)
  .then(res => res.json())
  .then((data) => {
      var req = {
        method: 'GET',
        headers: headers,
        mode: 'no-cors',
        cache: 'default',
      }
      
      data.contenido_canal.map ( id => {
        fetch(`http://localhost:9000/content/get/${id.contenido_canal}`, req)
        .then(res => res.json())
        .then((data) => {
          console.log("PaSOOOO")
          setTileData(tileData.push(data))
        })
        .catch(console.log)
      })
      
      console.log(tileData)
    })
    .catch(console.log)

  const handleOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  return (
    <React.Fragment>
      <Modal
        aria-labelledby="simple-modal-title"
        aria-describedby="simple-modal-description"
        open={open}
        onClose={handleClose}
      >
        <div style={modalStyle} className={classes.paper}>
            <h2 id="simple-modal-title">Te recomendamos </h2>
            <Typography id="simple-modal-description">
                Escoge el tipo de contenido que prefieras o haz click en <b>Mostrar</b> y te mostraremos el contenido mas acorde
            </Typography>
            <br />
            <Table value={tileData} handleContent={handler} handleClose={handleClose}/>
            <br/>
            <Button 
                variant="contained" 
                color="primary" 
                className={classes.button} 
                onClick={handleClose}>
                Mostrar
            </Button>
        </div>
      </Modal>
    </React.Fragment>
  );
}