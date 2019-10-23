import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardActionArea from '@material-ui/core/CardActionArea';
import CardActions from '@material-ui/core/CardActions';
import CardContent from '@material-ui/core/CardContent';
import CardMedia from '@material-ui/core/CardMedia';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';

const useStyles = makeStyles({
  card: {
    maxWidth: 345,
  },
});

export default function CourseCard({value}) {
  const classes = useStyles();

  return (
    <Card className={classes.card}>
      <CardActionArea>
        <CardMedia
          component="img"
          alt={value.title}
          height="140"
          image={value.img}
          title={value.title}
        />
        <CardContent>
          <Typography gutterBottom variant="h5" component="h2">
            {value.title}
          </Typography>
          <Typography variant="body2" color="textSecondary" component="p">
            {value.desc}
          </Typography>
        </CardContent>
      </CardActionArea>
      <CardActions>
        <Button size="small" color="primary">
          Ir al curso
        </Button>
        <Button size="small" color="primary">
          Acerca de 
        </Button>
      </CardActions>
    </Card>
  );
}