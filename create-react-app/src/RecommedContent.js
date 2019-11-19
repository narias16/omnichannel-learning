import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import GridList from '@material-ui/core/GridList';
import GridListTile from '@material-ui/core/GridListTile';
import GridListTileBar from '@material-ui/core/GridListTileBar';
import TileData from './TileData.js';
import Paper from '@material-ui/core/Paper';
import {NavLink} from "react-router-dom";

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
}));

export default function RecommenContent() {
  const classes = useStyles();
  const [contents, setContents] = React.useState([]);
  const [tileData, setTileData] = React.useState([]);
  
  let data = {
    id_estudiante: '1745265',
	  ruido : 'bajo',
	  luz: 'bajo',
	  conect :'bajo',
	  acelm : 'quieto',
	  ubicacion : 'casa',
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
    mode: 'no-cors',
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
      
      fetch(`http://localhost:9000/content/1234`, req)
      .then(res => res.json())
      .then((data) => {
        console.log("DATA" + data)
        setTileData(data)
      })
      .catch(console.log)
  })
  .catch(console.log)

  return (
    <Paper className={classes.root}>
      <div className={classes.root}>
        <GridList className={classes.gridList} cols={3}>
          {tileData.map(tile => (
            <GridListTile key={tile.img} >
              <img src={tile.img} alt={tile.title}/>
              <NavLink to={`/course/${tile.id}`}>
                <GridListTileBar
                  title={tile.title}
                  classes={{
                    root: classes.titleBar,
                    title: classes.title
                  }}
                />
              </NavLink>
            </GridListTile>
            
          ))}
        </GridList>
      </div>
    </Paper>
  );
}