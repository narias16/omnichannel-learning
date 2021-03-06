import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Box from '@material-ui/core/Box';
import Drawer from '@material-ui/core/Drawer';
import PDFDocument from './PDFDocument.js'
import Typography from '@material-ui/core/Typography';
import { useParams } from "react-router-dom";
import Stepper from "./Stepper.js"
import Rating from '@material-ui/lab/Rating';
import VideoComponent from './VideoComponent.js';
import AudioPlayer from './AudioPlayer.js';
import SimpleModal from './SimpleModal.js';

const drawerWidth = 240;

const useStyles = makeStyles(theme => ({
  root: {
    display: 'flex',
  },
  appBar: {
    zIndex: theme.zIndex.drawer + 1,
  },
  drawer: {
    width: drawerWidth,
    flexShrink: 0,
  },
  drawerPaper: {
    width: drawerWidth,
  },
  content: {
    flexGrow: 1,
    padding: theme.spacing(3),
  },
  toolbar: theme.mixins.toolbar,
}));

function renderContent(content) {
  switch(content.format) {
    case("pdf"):
      return(
        <React.Fragment>
          <PDFDocument value={content.url} />
        </React.Fragment>
      );
    case ("video"):
      return(
        <React.Fragment>
          <VideoComponent value={content.url} />
        </React.Fragment>
      );
    case ("audio"):
      return(
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
      return(
          <React.Fragment>
            <Typography>Tipo de contenido no soportado: {content.format}</Typography>
          </React.Fragment>
      );
  }
}

const inicio = {
  "id": "435145454113415", 
  "title": "Introducción al curso",
  "courseId": "1234",
  "format": "pdf",
  "size": 456,
  "url": "https://kealearning-copy.s3.amazonaws.com/courses/1234/ST0242_20192.pdf",
  "duration": 5,
  "interactivity": "activo-combinado-interactivo",
  "resourceType": "map",
  "interactivityLevel": 1
}

export default function ClippedDrawer() {
  const classes = useStyles();
  let { id } = useParams();
  const [content, setContent] = React.useState(inicio);
  const [value, setValue] = React.useState(0);

  return (
    <div className={classes.root}>
      <Drawer
        className={classes.drawer}
        variant="permanent"
        classes={{
          paper: classes.drawerPaper,
        }}>

        <div className={classes.toolbar} />
        <Stepper value={id} handler={setContent} />
      </Drawer>

      <main className={classes.content}>
        <div className={classes.toolbar} />
        {content ? (
          <React.Fragment>

            <SimpleModal value={id} handler={setContent}/>
            
            <Typography>{content.title}</Typography>

            {renderContent(content)}
            
            <Box component="fieldset" mb={3} borderColor="transparent">
              <Typography component="legend">Qué tal te parecio esta actividad ? </Typography>
              <Rating
                name="simple-controlled"
                value={value}
                onChange={(event, newValue) => {
                  setValue(newValue);
                  // TODO Make API request to save rating
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
              <Typography style={{fontFamily:'Gill Sans' ,fontSize:20}} >Has terminado el curso</Typography>
            </React.Fragment>
          )
        }
      </main>
    </div>
  );
}