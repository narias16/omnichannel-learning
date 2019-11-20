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
  
const recommendedContent = [
  {
    "courseId": "1234",
    "title": "Curso de programación básica",
    "format": "audio",
    "size": 456,
    "url": "https://kealearning.s3.amazonaws.com/courses/1234/Curso+de+Programacio%CC%81n+Ba%CC%81sica+-+Clase+1.mp3",
    "duration": 22,
    "interactivity": "activo-combinado-interactivo",
    "resourceType": "map",
    "interactivityLevel": 1
  },
  {
    "courseId": "1234",
    "title": "Curso de programación básica",
    "format": "video",
    "size": 456,
    "url": "https://kealearning.s3.amazonaws.com/courses/1234/Curso+de+Programacio%CC%81n+Ba%CC%81sica+-+Clase+1.mp4",
    "duration": 22,
    "interactivity": "activo-combinado-interactivo",
    "resourceType": "map",
    "interactivityLevel": 1
  },
  {
    "courseId": "1234",
    "title": "Conceptos básicos",
    "format": "pdf",
    "size": 456,
    "url": "https://kealearning.s3.amazonaws.com/courses/1234/Dialnet-APL-4794572.pdf",
    "duration": 10,
    "interactivity": "activo-combinado-interactivo",
    "resourceType": "map",
    "interactivityLevel": 1
  }
]

export default function SimpleModal({value, handler}) {
  const classes = useStyles();
  // getModalStyle is not a pure function, we roll the style only on the first render
  const [modalStyle] = React.useState(getModalStyle);
  const [open, setOpen] = React.useState(true);

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
            <Table value={recommendedContent} handleContent={handler} handleClose={handleClose}/>
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