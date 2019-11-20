import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import GridList from '@material-ui/core/GridList';
import GridListTile from '@material-ui/core/GridListTile';
import GridListTileBar from '@material-ui/core/GridListTileBar';
import Paper from '@material-ui/core/Paper';
import Button from '@material-ui/core/Button';
import Modal from '@material-ui/core/Modal';
import Typography from '@material-ui/core/Typography';
import Rating from '@material-ui/lab/Rating';
import VideoComponent from './VideoComponent.js';
import AudioPlayer from './AudioPlayer.js';
import PDFDocument from './PDFDocument.js';
import Box from '@material-ui/core/Box';
import { NavLink } from "react-router-dom";
import { Link } from '@material-ui/core';

function rand() {
  return Math.round(Math.random() * 20) - 10;
}

const useStyles = makeStyles(theme => ({
  root: {
    display: 'flex',
    flexWrap: 'wrap',
    justifyContent: 'space-around',
    overflow: 'hidden',
    backgroundColor: theme.palette.background.paper,
  },
  gridList: {
    flexWrap: 'nowrap',
    // Promote the list into his own layer on Chrome. This cost memory but helps keeping high FPS.
    transform: 'translateZ(0)',
  },
  title: {
    color: 'white' //theme.palette.primary.light,
  },
  titleBar: {
    background:
      'linear-gradient(to top, rgba(0,0,0,0.7) 0%, rgba(0,0,0,0.3) 70%, rgba(0,0,0,0) 100%)',
  },
  paper: {
    position: 'absolute',
    width: 800,
    backgroundColor: theme.palette.background.paper,
    border: '2px solid #000',
    boxShadow: theme.shadows[5],
    padding: theme.spacing(2, 4, 3),
},
}));

function getModalStyle() {
  const top = 50 + rand();
  const left = 50 + rand();

  return {
    top: `${top}%`,
    left: `${left}%`,
    transform: `translate(-${top}%, -${left}%)`,
  };
}

export default function RecommenContent() {
  const classes = useStyles();
  const [tileData, setTileData] = React.useState([]);

  // getModalStyle is not a pure function, we roll the style only on the first render
  const [modalStyle] = React.useState(getModalStyle);
  const [open, setOpen] = React.useState(false);
  const [value, setValue] = React.useState(0);

  const handleOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  function renderContent(content) {
    switch (content.format) {
      case ("pdf"):
        return (
          <React.Fragment>
            <PDFDocument value={content.url} />
          </React.Fragment>
        );
      case ("video"):
        return (
          <React.Fragment>
            <VideoComponent value={content.url} />
          </React.Fragment>
        );
      case ("audio"):
        return (
          <React.Fragment>
            <AudioPlayer value={content.url} />
          </React.Fragment>
        );
      case ("img"):
        return (
          <React.Fragment>
            <Typography>Not implemented {content.url}</Typography>
          </React.Fragment>
        );
      default:
        return (
          <React.Fragment>
            <Typography>Tipo de contenido no soportado: {content.format}</Typography>
          </React.Fragment>
        );
    }
  }

  let data = {
    id_estudiante: '1745265',
    ruido: 'bajo',
    luz: 'bajo',
    conect: 'bajo',
    acelm: 'quieto',
    ubicacion: 'casa',
    canal: 'web',
    timestamp: 'weekday',
    id_contenido: '1,2,3'
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
        mode: 'cors',
        cache: 'default',
      }

      fetch(`http://localhost:9000/content/get/${data.contenido_canal}`)
        .then(res => res.json())
        .then((data) => {
          setTileData([data])
        })
    })
    .catch(console.log)

  return (
    <Paper className={classes.root}>
      <div className={classes.root}>
        <GridList className={classes.gridList} cols={3}>
          {tileData.map(content => (
            <GridListTile key={content.img} >
              <img src={content.img} alt={content.title} />

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
                      Comienza por aquí o puedes hacer click en <b>Mostrar</b> y te mostraremos el resto del curso.
                    </Typography>
                    <br />

                    <div className={classes.root}>

                      <main className={classes.content}>
                        <div className={classes.toolbar} />
                        {content ? (
                          <React.Fragment>

                            <Typography>{content.title}</Typography>

                            {renderContent(content)}

                            <Box component="fieldset" mb={3} borderColor="transparent">
                              <Typography component="legend">Qué tal te parecio esta actividad ? </Typography>
                              <Rating
                                name="simple-controlled"
                                value={value}
                                onChange={(event, newValue) => {
                                  setValue(newValue);
                                  let data = {
                                    user_id: "1",
                                    content_id: content.id,
                                    rating: newValue
                                  }

                                  var httpHeaders = {
                                    'Access-Control-Request-Headers': 'origin, x-requested-with',
                                    'origin': 'localhost:3000',
                                  }

                                  var headers = new Headers(httpHeaders);

                                  var init = {
                                    headers: headers,
                                    body: JSON.stringify(data),
                                    method: 'POST',
                                    mode: 'cors',
                                    cache: 'default',
                                  };

                                  fetch('http://localhost:9000/recommend/rated', init)
                                    .catch(console.log);
                                }}
                              />
                            </Box>
                          </React.Fragment>
                        ) : (
                            <React.Fragment>
                              <Typography style={{ fontFamily: 'Gill Sans', fontSize: 20 }} >Has terminado el curso</Typography>
                            </React.Fragment>
                          )
                        }
                      </main>
                    </div>

                    <br />

                    <NavLink to={`/course/${content.courseId}`}>
                      <Button
                        variant="contained"
                        color="primary"
                        className={classes.button}
                      >
                        Mostrar
                        </Button>
                    </NavLink>

                  </div>
                </Modal>
              </React.Fragment>

              <Link onClick={handleOpen} >
                <GridListTileBar
                  title={content.title}
                  classes={{
                    root: classes.titleBar,
                    title: classes.title
                  }}
                />
              </Link>
            </GridListTile>

          ))}
        </GridList>
      </div>
    </Paper>
  );
}