import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';
import Button from '@material-ui/core/Button';


const useStyles = makeStyles(theme => ({
  button: {
    margin: theme.spacing(1),
  },
  input: {
    display: 'none',
  },
  root: {
    width: '100%',
    overflowX: 'auto',
  },
  table: {
    minWidth: 650,
  },
}));

export default function SimpleTable({value, handleContent, handleClose}) {
  const classes = useStyles();

  return (
    <Paper className={classes.root}>
      <Table className={classes.table} aria-label="simple table">
        <TableHead>
          <TableRow>
            <TableCell>Contenido</TableCell>
            <TableCell align="right">Duracion</TableCell>
            <TableCell align="right">Formato</TableCell>
            <TableCell align="right">Enlace</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {value.map(content => (
            <TableRow key={content.title}>
              <TableCell component="th" scope="row">
                {content.title}
              </TableCell>
              <TableCell align="right">{content.duration}</TableCell>
              <TableCell align="right">{content.format}</TableCell>
              <TableCell align="right">
              <Button 
                variant="contained" 
                color="primary" 
                className={classes.button} 
                onClick={function(event) {
                    handleContent(content);
                    handleClose();
                    event.preventDefault();
                    }}>
                Ver
                </Button>
              </TableCell>
              
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </Paper>
  );
}