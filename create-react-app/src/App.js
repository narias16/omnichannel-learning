import React from 'react';
import Container from '@material-ui/core/Container';
import Typography from '@material-ui/core/Typography';
import Box from '@material-ui/core/Box';
import Link from '@material-ui/core/Link';
import AppBar from './AppBar'
import Courses from './Courses';
import Course from './Course'
import { Route, HashRouter } from "react-router-dom";

function Copyright() {
  return (
    <Typography variant="body2" color="textSecondary" align="center">
      {'Copyright © '}
      <Link color="inherit" href="https://github.com/narias16/omnichannel-learning">
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
      <AppBar />
      <Container maxWidth="md" >
        <Box my={4}>
          <div className="content">
            <Route exact path="/" component={Courses} />
            <Route path="/course/:id" component={Course} />
          </div>
          <Copyright />
        </Box>
      </Container>
    </HashRouter>
  );
}
