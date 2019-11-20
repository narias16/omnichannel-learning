import React from 'react';
import Button from '@material-ui/core/Button';
import { makeStyles } from '@material-ui/core/styles';
import Modal from '@material-ui/core/Modal';
import Typography from '@material-ui/core/Typography';
import Rating from '@material-ui/lab/Rating';
import VideoComponent from './VideoComponent.js';
import AudioPlayer from './AudioPlayer.js';
import PDFDocument from './PDFDocument.js';
import Box from '@material-ui/core/Box';
import {NavLink} from "react-router-dom";

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

export default function ContentModal({ content }) {
    const classes = useStyles();
    // getModalStyle is not a pure function, we roll the style only on the first render
    const [modalStyle] = React.useState(getModalStyle);
    const [open, setOpen] = React.useState(true);
    const [value, setValue] = React.useState(0);

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
    );
}