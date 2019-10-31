import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import AppBar from '@material-ui/core/AppBar';
import Button from '@material-ui/core/Button';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import {NavLink} from "react-router-dom";

const useStyles = makeStyles(theme => ({
  root: {
    flexGrow: 1,
  },
  appBar: {
    zIndex: theme.zIndex.drawer + 1,
  },
  title: {
    flexGrow: 1,
  },
}));



export default function ButtonAppBar() {
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <AppBar style={{color:'white'}} position="fixed" className={classes.appBar}>
        <Toolbar>
          <Typography variant="h6" style={{ fontFamily: 'Didot-Bold', fontSize: 25 }}className={classes.title}>
            Kea-learning
          </Typography>

          <NavLink style={{color:"white"}} to={"/"}>
            Home
          
          </NavLink>
        </Toolbar>
      </AppBar>
    </div>
  );
}