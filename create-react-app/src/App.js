import React from 'react';
import Container from '@material-ui/core/Container';
import Typography from '@material-ui/core/Typography';
import Box from '@material-ui/core/Box';
import Link from '@material-ui/core/Link';
import AppBar from './AppBar'
import MyView from './MyView';
import { Route, NavLink, HashRouter } from "react-router-dom";

function Copyright() {
  return (
    <Typography variant="body2" color="textSecondary" align="center">
      {'Copyright © '}
      <Link color="inherit" href="https://material-ui.com/">
        Your Website
      </Link>{' '}
      {new Date().getFullYear()}
      {'.'}
    </Typography>
  );
}

export default function App() {
  return (
    <HashRouter>
      <AppBar />
      <Container maxWidth="md">
        <Box my={4}>
          <MyView />
          <Copyright />
        </Box>
      </Container>
    </HashRouter>
  );
}
