import React, { Component } from 'react';
import {Document, Page, pdfjs } from 'react-pdf';

pdfjs.GlobalWorkerOptions.workerSrc = `//cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjs.version}/pdf.worker.js`;

export default function PDFDocument({value}) {
    const [numPages, setNumPages] = React.useState(null);
    const [pageNumber, setPageNumber] = React.useState(1);

    const onDocumentLoadSuccess = ({ numPages }) => {
        setNumPages(numPages);
    }
    
    return (
        <React.Fragment>
            {console.log(value)}
            <Document
                file={value}
                onLoadSuccess={onDocumentLoadSuccess}
            >
                {Array.from(
                new Array(numPages),
                (el, index) => (
                    <Page
                    key={`page_${index + 1}`}
                    pageNumber={index + 1}
                    />
                ),
                )}
            </Document>
            <p>Page {pageNumber} of {numPages}</p>
        </React.Fragment>
    );
}