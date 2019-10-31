import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';
import StepContent from '@material-ui/core/StepContent';
import Button from '@material-ui/core/Button';
import Paper from '@material-ui/core/Paper';
import Typography from '@material-ui/core/Typography';

const useStyles = makeStyles(theme => ({
  root: {
    width: '90%',
  },
  button: {
    marginTop: theme.spacing(1),
    marginRight: theme.spacing(1),
    color: "white"
  },
  actionsContainer: {
    marginBottom: theme.spacing(2),
  },
  resetContainer: {
    padding: theme.spacing(3),
  },
}));

export default function VerticalLinearStepper({ value, handler }) {
  const classes = useStyles();
  const [activeStep, setActiveStep] = React.useState(0);
  const [steps, setSteps] = React.useState([]);
  
  var httpHeaders = {
    'Access-Control-Request-Headers': 'origin, x-requested-with',
    'origin': 'localhost:3000',
  }

  var headers = new Headers(httpHeaders);

  var init = {
    method: 'GET',
    headers: headers,
    mode: 'cors',
    cache: 'default',
  };

  fetch(`http://localhost:9000/content/${value}`, init)
    .then(res => res.json())
    .then((data) => { 
      setSteps(data)
    })
    .catch(console.log)

  const handleNext = () => {
    handler(steps[activeStep + 1]);
    setActiveStep(prevActiveStep => prevActiveStep + 1);
  };

  const handleBack = () => {
    handler(steps[activeStep - 1]);
    setActiveStep(prevActiveStep => prevActiveStep - 1);
  };

  const handleReset = () => {
    handler(steps[0]);
    setActiveStep(0);
  };

  const setStep = (i) => {
    handler(steps[i]);
    setActiveStep(i);
  }

  return (
    <div className={classes.root}>
      <Stepper activeStep={activeStep} orientation="vertical">
        {steps.map((content, index) => (
          <Step key={content.title}>
            <StepLabel>{content.title}</StepLabel>
            <StepContent>
              <Typography className="subtitle2">{content.duration} minutos restantes </Typography>
              <div className={classes.actionsContainer}>
                <div>
                  <Button
                    color="secondary"
                    disabled={activeStep === 0}
                    onClick={handleBack}
                    className={classes.button}
                  >
                    Back
                  </Button>
                  <Button
                    variant="contained"
                    color="secondary"
                    onClick={handleNext}
                    className={classes.button}
                  >
                    {activeStep === steps.length - 1 ? 'Finish' : 'Next'}
                  </Button>
                </div>
              </div>
            </StepContent>
          </Step>
        ))}
      </Stepper>
      {activeStep === steps.length && (
        <Paper square elevation={0} className={classes.resetContainer}>
          <Typography>All steps completed - you&apos;re finished</Typography>
          <Button onClick={handleReset} className={classes.button}>
            Reset
          </Button>
        </Paper>
      )}
    </div>
  );
}