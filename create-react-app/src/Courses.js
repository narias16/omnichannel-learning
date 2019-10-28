import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import CourseCard from './CourseCard';

const useStyles = makeStyles(theme => ({
    root: {
        flexGrow: 1,
    },
    paper: {
        padding: theme.spacing(1),
        textAlign: 'center',
        color: theme.palette.text.secondary,
    },
}));

const s3path = "https://kealearning.s3.amazonaws.com";
const course1 = {
    id: "1234",
    title: "Fundamentos de Programación",
    desc: "Entenderás la lógica del código, cómo piensan los programadores y cómo programar juegos, proyectos y hasta robots y electrónica.",
    img: s3path + "/img/fundamentos.jpg",
};
const course2 = {
    id: "5467",
    title: "Lógica",
    desc: "Aprenderás habilidades para leer, comprender y construir razonamientos de tipo argumentativo, en lo referente a la representación, modelación y solución de problemas que involucren razonamiento de tipo deductivo.",
    img: s3path + "/img/logica.png",
};
const course3 = {
    id: "15678",
    title: "Lenguajes de Programación",
    desc: "El entendimiento de distintos paradigmas de programación te permitiran asimilar los conceptos mas generales de la computación y aplicarlos en la construcción de cualquier solución de software.",
    img: s3path + "/img/lenguajes.jpg",
};
const course4 = {
    id: "0987",
    title: "Bases de Datos",
    desc: "Los datos que se generan diariamente en las organizaciones son producto de las transacciones del día a día",
    img: s3path + "/img/bases+de+datos.jpg",
};
const course5 = {
    id: "87654",
    title: "Ingeniería de Software",
    desc: "Conocerás de primera mano los lineamientos, estándares y técnicas para proponer soluciones de software de complejidad mediana.",
    img: s3path + "/img/ingenieria.png",
};
const course6 = {
    id: "2864",
    title: "Matemáticas Discretas",
    desc: "Este curso proporciona conceptos y técnicas necesarios para el desarrollo de cursos mas avanzados como Estructuras de Datos y Algoritmos y Lenguajes Formales.",
    img: s3path + "/img/discretas.jpg",
};
const course7 = {
    id: "14564",
    title: "Estructuras de Datos y Algoritmos",
    desc: "Si quieres aprender a desarrollar sistemas intensivos en software es necesario que conozcas sobre el análisis de algoritmos y estructuras de datos.",
    img: s3path + "/img/datos.jpg",
};
const course8 = {
    id: "1234334",
    title: "Algebra Lineal para Inteligencia Artificial",
    desc: "Curso básico de Algebra Lineal enfocado en el aprendizaje de los conceptos mas utilizados en Inteligencia Artificial.",
    img:  s3path + "/img/algebra.png",
};
const course9 = {
    id: "16789",
    title: "Organización de Computadores",
    desc: "Todos los estudiantes de computación, deberían adquirir un nivel de entendimiento y apreciación de los componentes funcionales de un sistema de computadoras.",
    img: s3path + "/img/organizacion.jpg",
};

export default function Courses() {
    const classes = useStyles();

    function FormRow({ value }) {
        const course1 = value[0];
        const course2 = value[1];
        const course3 = value[2];
        return (
            <React.Fragment>
                <Grid item xs={4} zeroMinWidth>
                    <CourseCard value={course1} />
                </Grid>
                <Grid item xs={4} zeroMinWidth>
                    <CourseCard value={course2} />
                </Grid>
                <Grid item xs={4} zeroMinWidth>
                    <CourseCard value={course3} />
                </Grid>
            </React.Fragment>
        );
    }

    return (
        <div className={classes.root}>
            <Grid container spacing={1}>
                <Grid container item xs={12} spacing={3}>
                    <FormRow value={[course1, course2, course3]}/>
                </Grid>
                <Grid container item xs={12} spacing={3}>
                    <FormRow value={[course4, course5, course6]}/>
                </Grid>
                <Grid container item xs={12} spacing={3}>
                    <FormRow value={[course7, course8, course9]}/>
                </Grid>
            </Grid>
        </div>
    );
}