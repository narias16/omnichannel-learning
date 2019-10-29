import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Drawer from '@material-ui/core/Drawer';
import Typography from '@material-ui/core/Typography';
import {useParams} from "react-router-dom";
import Stepper from "./Stepper.js"

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

export default function ClippedDrawer() {
  const classes = useStyles();
  let { id } = useParams();
  const [content, setContent] = React.useState({});

  return (
    <div className={classes.root}>
      <Drawer
        className={classes.drawer}
        variant="permanent"
        classes={{
          paper: classes.drawerPaper,
        }}
      >
        <div className={classes.toolbar} />
        <Stepper value={id} handler={setContent}/>
      </Drawer>


      <main className={classes.content}>
        <div className={classes.toolbar} />
        {content ? (
            <Typography>{content.url}</Typography>
            
          ) : (
            <Typography>Content undefinded</Typography>
          )
        }
      </main>
    </div>
  );
}
