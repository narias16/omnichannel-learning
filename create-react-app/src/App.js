import React from 'react';
import Container from '@material-ui/core/Container';
import Typography from '@material-ui/core/Typography';
import Box from '@material-ui/core/Box';
import Link from '@material-ui/core/Link';
import AppBar from './AppBar'
import Courses from './Courses';
import Course from './Course'
import { Route, NavLink, HashRouter } from "react-router-dom";

function Copyright() {
  return (
    <Typography variant="body2" color="textSecondary" align="center">
      {'Copyright © '}
      <Link color="inherit" href="https://material-ui.com/">
        kealearning
      </Link>{' '}
      {new Date().getFullYear()}
      {'.'}
    </Typography>
  );
}

export default function App() {
  return (
    <HashRouter>
      <AppBar/>
      <Container maxWidth="md">
        <Box my={4}>
          <Courses/>
          <Copyright/>
        </Box>
      </Container>

      <div className="content">
        <Route exact path="/"/>
        <Route path="/course/" component={Course} />
      </div>
    </HashRouter>
  );
}
