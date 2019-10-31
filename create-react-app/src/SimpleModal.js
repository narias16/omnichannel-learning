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
        "id": "20845a0d-dd33-46b7-8915-87cfbdebbbdd",
        "title": "Trabajo final",
        "courseId": "1234",
        "format": "pdf",
        "size": 456,
        "url": "https://kealearning.s3.amazonaws.com/courses/1234/Red+de+Estaciones+Hidrologicas.pdf",
        "duration": 10,
        "interactivity": "activo-combinado-interactivo",
        "resourceType": "map",
        "interactivityLevel": 1
    },
    {
        "id": "9f28b485-0664-4249-a969-9e69d94cf547",
        "title": "Trabajo final",
        "courseId": "1234",
        "format": "pdf",
        "size": 456,
        "url": "https://kealearning.s3.amazonaws.com/courses/1234/Red+de+Estaciones+Hidrologicas.pdf",
        "duration": 10,
        "interactivity": "activo-combinado-interactivo",
        "resourceType": "map",
        "interactivityLevel": 1
    },
    {
        "id": "a2497f74-b9fa-488e-b2de-06d30961f163",
        "title": "Trabajo final",
        "courseId": "1234",
        "format": "video",
        "size": 456,
        "url": "https://kealearning.s3.amazonaws.com/courses/1234/What+is+reactive+programming.mp4",
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
                Puedes seleccionar una de estas opciones o seguir el curso a tu manera 
            </Typography>
            <br />
            <Table value={recommendedContent} handleContent={handler} handleClose={handleClose}/>
            <br/>
            <Button 
                variant="contained" 
                color="primary" 
                className={classes.button} 
                onClick={handleClose}>
                Seguir el curso
            </Button>
        </div>
      </Modal>
    </React.Fragment>
  );
}